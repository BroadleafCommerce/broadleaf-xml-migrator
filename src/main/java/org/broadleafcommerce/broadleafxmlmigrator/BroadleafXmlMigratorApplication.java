package org.broadleafcommerce.broadleafxmlmigrator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jcabi.xml.XMLDocument;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@SpringBootApplication
public class BroadleafXmlMigratorApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(BroadleafXmlMigratorApplication.class, args);
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: take in qualifier as command line argument
        String qualifier = "client";
        ClassPathResource appctx = new ClassPathResource("applicationContext-test.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(appctx.getInputStream());
        for (ExecutionArguments executionArgs : new LateAndEarlyStageExecutionArguments().getLateAndEarlyMergeArguments()) {
            insertAndReplaceWithMerge(document, executionArgs, qualifier);
        }
        ExecutionArguments variableExpressionArgs = new ExecutionArguments
                (new String[]{"/beans/bean[@id='blVariableExpressions']/property[@name='sourceList']/list/bean"},
                 "/beans/bean[@id='blVariableExpressions']", "blVariableExpressions", null, null);
        moveValuesOutOfParent(document, variableExpressionArgs);
        System.out.println(new XMLDocument(document).toString());
    }
    
    protected void insertAndReplaceWithMerge(Document document, ExecutionArguments args, String newBeanIdQualifier) throws XPathExpressionException {
        String newBeanId = args.getTargetBeanId() + "-" + newBeanIdQualifier;
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        if (oldBeanDef == null) 
            return;
        Element newCollectionBean = document.createElement("bean");
        newCollectionBean.setAttribute("id", newBeanId);
        newCollectionBean.setAttribute("class", args.getCollectionType().getCollectionClass());
        Element sourcePropertyElement = document.createElement("property");
        sourcePropertyElement.setAttribute("name", args.getCollectionType().getSourcePropertyName());
        Element newCollection = document.createElement(args.getCollectionType().getCollectionName());
        Boolean someValuesWereFound = false;
        for (String xpath : args.getCollectionXpaths()) {
            NodeList vals = (NodeList) evaluator.evaluate(xpath, document, XPathConstants.NODESET);
            for (int i = 0; i < vals.getLength(); i++) {
                someValuesWereFound = true;
                newCollection.appendChild(vals.item(i));
            }
        }
        if (!someValuesWereFound)
            return;
        
        sourcePropertyElement.appendChild(newCollection);
        newCollectionBean.appendChild(sourcePropertyElement);
        Element mergeBean = document.createElement("bean");
        mergeBean.setAttribute("class", args.getMergeType().getMergeClass());
        Element source = document.createElement("property");
        source.setAttribute("name", "sourceRef");
        source.setAttribute("value", newBeanId);
        mergeBean.appendChild(source);
        Element target = document.createElement("property");
        target.setAttribute("name", "targetRef");
        target.setAttribute("value", args.getTargetBeanId());
        mergeBean.appendChild(target);
        
        document.getFirstChild().insertBefore(newCollectionBean, oldBeanDef);
        document.getFirstChild().insertBefore(mergeBean, oldBeanDef);
        document.getFirstChild().removeChild(oldBeanDef);
    }
    
    protected void moveValuesOutOfParent(Document document, ExecutionArguments args) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        for (String xpath : args.getCollectionXpaths()) {
            NodeList vals = (NodeList) evaluator.evaluate(xpath, document, XPathConstants.NODESET);
            for (int i = 0; i < vals.getLength(); i++) {
                document.getFirstChild().insertBefore(vals.item(i), oldBeanDef);
            }
        }
        document.getFirstChild().removeChild(oldBeanDef);
    }
    
}
