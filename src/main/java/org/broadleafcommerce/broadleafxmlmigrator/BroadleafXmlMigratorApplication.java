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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@SpringBootApplication
public class BroadleafXmlMigratorApplication implements ApplicationRunner {
    
    protected static StringBuilder LOG = new StringBuilder();
    
    protected static Map<String, String> affectedBeanMap = new LinkedHashMap<>();
    
    public static void main(String[] args) {
        SpringApplication.run(BroadleafXmlMigratorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: take in qualifier as command line argument
        LOG.append("\n\n\n");
        String qualifier = "client";
        ClassPathResource appctx = new ClassPathResource("applicationContext-test.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(appctx.getInputStream());
        for (ExecutionArguments executionArgs : new LateAndEarlyStageExecutionArguments().getLateAndEarlyMergeArguments()) {
            insertAndReplaceWithMerge(document, executionArgs, qualifier);
        }
        handleWorkflowBeans(document, qualifier);
        handleVariableExpressions(document);
        handleRemoveUnneededBeans(document);
        LOG.append("\n\n\n");
        System.out.println(new XMLDocument(document).toString());
        printBeanChanges();
        System.out.println(LOG.toString());
    }
    
    /**
     * Handles the basic case of migrating a bean definition that utilized functionality in default.properties to the proper
     * Early/LateStageMergeBeanPostProcessor since default.properties has been all but removed. This method creates
     * a new collection bean, a new merge bean and then deletes the original bean definition
     * 
     * @param document
     * @param args
     * @param newBeanIdQualifier
     * @throws XPathExpressionException
     */
    protected void insertAndReplaceWithMerge(Document document, ExecutionArguments args, String newBeanIdQualifier) throws XPathExpressionException {
        String newBeanId = args.getTargetBeanId() + "-" + newBeanIdQualifier;
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        if (oldBeanDef == null) {
            return;
        }
        
        Element newCollectionBean = createCollectionBean(document, args, newBeanId);
        if (newCollectionBean == null) {
            return;
        }
        Element mergeBean = createMergeBean(document, args, newBeanId);
        
        document.getFirstChild().insertBefore(newCollectionBean, oldBeanDef);
        document.getFirstChild().insertBefore(mergeBean, oldBeanDef);
        document.getFirstChild().removeChild(oldBeanDef);
        logBeanChange(args.getBeanXpath(), newBeanId);
    }
    
    /**
     * Wrapper around the other handleWorkflowBeans method that simply gets the workflow arguments
     * @see handleWorkflwoBeans(Document, ExecutionArguments, String)
     * @param document
     * @param newBeanIdQualifier
     * @throws XPathExpressionException
     */
    protected void handleWorkflowBeans(Document document, String newBeanIdQualifier) throws XPathExpressionException {
        for (ExecutionArguments args : new WorkflowExecutionArguments().getWorkflowArguments()) {
            handlWorkflowBeans(document, args, newBeanIdQualifier);
        }
    }
    
    /**
     * Specifically handles migrating workflow beans. This is an exception because sometimes we don't want to delete the workflow bean
     * if other properties are defined (this would mean the client was overriding the workflow) but we still want to merge their custom
     * activities into the appropriate activities bean. If no other properties are defined then the workflow bean is simply removed
     * 
     * @param document
     * @param args
     * @param newBeanIdQualifier
     * @throws XPathExpressionException
     */
    protected void handlWorkflowBeans(Document document, ExecutionArguments args, String newBeanIdQualifier) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        if (oldBeanDef == null) {
            return;
        }
        NodeList nodes = oldBeanDef.getChildNodes();
        int numChildElements = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                numChildElements++;
            }
        }
        if (numChildElements <= 1) {
            // delegate to the normal way to do Early/LateStageMerging since there wasn't additional things being added
            insertAndReplaceWithMerge(document, args, newBeanIdQualifier);
            return;
        }
        LOG.append("We found a workflow with xpath " + args.getBeanXpath() + " and it had more properties than just the activities.\nWe're assuming that there were custom properties set so we will simply merge the activities into the correct bean named " + args.getTargetBeanId() + " and update the activities reference to that bean.\nIf there is no actual customizations then the bean at xpath " + args.getBeanXpath() + " can instead be removed\n\n");
        String newBeanId = args.getTargetBeanId() + "-" + newBeanIdQualifier;
        Element collectionBean = createCollectionBean(document, args, newBeanId);
        if (collectionBean == null) {
            return;
        }
        Element mergeBean = createMergeBean(document, args, newBeanId);
        document.getFirstChild().insertBefore(collectionBean, oldBeanDef);
        document.getFirstChild().insertBefore(mergeBean, oldBeanDef);
        String activitiesXpath = new WorkflowExecutionArguments().getActivitiesXpath(args);
        Node activitiesNode = (Node) evaluator.evaluate(activitiesXpath, document, XPathConstants.NODE);
        if (activitiesNode != null) {
            oldBeanDef.removeChild(activitiesNode);
        }
        Element activitiesElement = document.createElement("property");
        activitiesElement.setAttribute("name", "activities");
        activitiesElement.setAttribute("ref", args.getTargetBeanId());
        oldBeanDef.appendChild(activitiesElement);
        logBeanChange(args.getBeanXpath(), newBeanId);
    }
    
    /**
     * Handles previous declarations of variable expressions.
     * Previously you could define the blVariableExpressions bean and we would merge it with blVariableExpression. Now it's
     * not necessary since that list doesn't exist so this method simply pulls out any variable expressions that were
     * being defined and then deletes the blVariableExpression definition
     * 
     * @param document
     * @throws XPathExpressionException
     */
    protected void handleVariableExpressions(Document document) throws XPathExpressionException {
        ExecutionArguments args = new ExecutionArguments
                (new String[]{"/beans/bean[@id='blVariableExpressions']/property[@name='sourceList']/list/bean"},
                 "/beans/bean[@id='blVariableExpressions']", "blVariableExpressions", null, null);
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        if (oldBeanDef == null) {
            return;
        }
        LOG.append("Variable Expressions now just have to be defined and not merged into blVariableExpression.\nThe beans that were created in blVariableExpressions were moved out and then the blVariableExpressions definition was removed\n\n");
        for (String xpath : args.getCollectionXpaths()) {
            NodeList vals = (NodeList) evaluator.evaluate(xpath, document, XPathConstants.NODESET);
            for (int i = 0; i < vals.getLength(); i++) {
                document.getFirstChild().insertBefore(vals.item(i), oldBeanDef);
            }
        }
        document.getFirstChild().removeChild(oldBeanDef);
        logBeanChange(args.getBeanXpath(), "The original bean was just removed. See logs above");
    }
    
    protected void handleRemoveUnneededBeans(Document document) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        for (LoggableExecutionArguments args : new RemovedBeanExecutionArguments().getLoggableExecutionArguments()) {
            Node node  = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
            if (node != null) {
                document.getFirstChild().removeChild(node);
                LOG.append(args.getLogMessage() + "\n\n");
                logBeanChange(args.getBeanXpath(), "The original bean was just removed. See logs above");
            }
        }
    }
    
    /**
     * Creates a collection bean based off of the given ExecutionArguments
     * 
     * @param document
     * @param args
     * @param newBeanId
     * @return
     * @throws XPathExpressionException
     */
    protected Element createCollectionBean(Document document, ExecutionArguments args, String newBeanId) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Element newCollectionBean = document.createElement("bean");
        newCollectionBean.setAttribute("id", newBeanId);
        newCollectionBean.setAttribute("class", args.getCollectionType().getCollectionClass());
        Element sourcePropertyElement = document.createElement("property");
        sourcePropertyElement.setAttribute("name", args.getCollectionType().getSourcePropertyName());
        Element newCollection = document.createElement(args.getCollectionType().getCollectionName());
        boolean found = false;
        for (String xpath : args.getCollectionXpaths()) {
            NodeList vals = (NodeList) evaluator.evaluate(xpath, document, XPathConstants.NODESET);
            for (int i = 0; i < vals.getLength(); i++) {
                found = true;
                newCollection.appendChild(vals.item(i));
            }
        }
        if (!found) {
            return null;
        }
        sourcePropertyElement.appendChild(newCollection);
        newCollectionBean.appendChild(sourcePropertyElement);
        return newCollectionBean;
    }
    
    /**
     * Creates a Late/EarlyStageMergeBeanPostProcessor bean based off of the given ExecutionArguments
     * 
     * @param document
     * @param args
     * @param newBeanId
     * @return
     */
    protected Element createMergeBean(Document document, ExecutionArguments args, String newBeanId) {
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
        return mergeBean;
    }
    
    protected void logBeanChange(String oldXpath, String newBeanId) {
        affectedBeanMap.put(oldXpath, newBeanId);
    }
    
    protected void printBeanChanges() {
        LOG.append("Beans that were affected:\n");
        int maxKeyLength = -1;
        for (String key : affectedBeanMap.keySet()) {
            if (key.length() > maxKeyLength) {
                maxKeyLength = key.length();
            }
        }
        LOG.append("Old Xpath");
        for (int i = 0 ; i < (maxKeyLength - "Old Xpath".length()); i++) {
            LOG.append(" ");
        }
        LOG.append(" :: New Bean Id\n");
        for (String key : affectedBeanMap.keySet()) {
            LOG.append(key);
            for (int i = 0; i < (maxKeyLength - key.length()); i++) {
                LOG.append(" ");
            }
            LOG.append(" :: " + affectedBeanMap.get(key) + "\n");
        }
        LOG.append("\n");
    }
}
