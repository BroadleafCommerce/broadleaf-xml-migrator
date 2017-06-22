package org.broadleafcommerce.broadleafxmlmigrator;

import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;
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

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lombok.AllArgsConstructor;
import lombok.Data;

@SpringBootApplication
public class BroadleafXmlMigratorApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(BroadleafXmlMigratorApplication.class, args);
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: take in qualifier as command line argument
        String qualifier = "client";
        ClassPathResource appctx = new ClassPathResource("applicationContext.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(appctx.getInputStream());
        ExecutionArguments executionArgs = new ExecutionArguments("/beans/bean[@id='messageSource']/property[@name='basenames']/list/value", 
                                                        "/beans/bean[@id='messageSource']", "messageSourceBaseNames-client", 
                                                        "messageSourceBaseNames", CollectionType.LIST, MergeType.LATE);
        insertAndReplaceWithMerge(document, executionArgs);
        System.out.println(new XMLDocument(document).toString());
    }
    
    protected void insertAndReplaceWithMerge(Document document, ExecutionArguments args) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        NodeList vals = (NodeList) evaluator.evaluate(args.getCollectionXpath(), document, XPathConstants.NODESET);
        Element newCollectionBean = document.createElement("bean");
        newCollectionBean.setAttribute("id", args.getNewBeanId());
        newCollectionBean.setAttribute("class", args.getCollectionType().getCollectionClass());
        Element newCollection = document.createElement(args.getCollectionType().collectionName);
        for (int i = 0; i < vals.getLength(); i++) {
            newCollection.appendChild(vals.item(i));
        }
        newCollectionBean.appendChild(newCollection);
        
        Element mergeBean = document.createElement("bean");
        mergeBean.setAttribute("class", args.getMergeType().getMergeClass());
        Element source = document.createElement("property");
        source.setAttribute("name", "sourceRef");
        source.setAttribute("value", args.getNewBeanId());
        mergeBean.appendChild(source);
        Element target = document.createElement("property");
        target.setAttribute("name", "targetRef");
        target.setAttribute("value", args.getTargetBeanId());
        mergeBean.appendChild(target);
        
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        
        document.getFirstChild().insertBefore(newCollectionBean, oldBeanDef);
        document.getFirstChild().insertBefore(mergeBean, oldBeanDef);
        document.getFirstChild().removeChild(oldBeanDef);
    }
    
    @Data
    @AllArgsConstructor
    public static class ExecutionArguments {
        private String collectionXpath;
        private String beanXpath;
        private String newBeanId;
        private String targetBeanId;
        private CollectionType collectionType;
        private MergeType mergeType;
    }
    
    @Data
    @AllArgsConstructor
    public static class MergeType {
        public static final MergeType LATE = new MergeType("org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor");
        public static final MergeType EARLY = new MergeType("org.broadleafcommerce.common.extensibility.context.merge.EarlyStageMergeBeanPostProcessor");
        private String mergeClass;
    }
    
    @Data
    @AllArgsConstructor
    public static class CollectionType {
        public static final CollectionType LIST = new CollectionType("list", ListFactoryBean.class.getName());
        public static final CollectionType MAP = new CollectionType("map", MapFactoryBean.class.getName());
        public static final CollectionType SET = new CollectionType("set", SetFactoryBean.class.getName());
        private String collectionName;
        private String collectionClass;
    }
    
    protected void loadHandlerProperties() throws IOException {
        Properties defaultProperties = new Properties();
        defaultProperties.load(new ClassPathResource("default.properties").getInputStream());
        Iterator propsIterator = defaultProperties.entrySet().iterator();
        while (propsIterator.hasNext()) {
            
        }
    }
    
}
