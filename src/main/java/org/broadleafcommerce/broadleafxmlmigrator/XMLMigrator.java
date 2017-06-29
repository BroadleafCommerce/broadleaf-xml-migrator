package org.broadleafcommerce.broadleafxmlmigrator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.broadleafxmlmigrator.arguments.ExecutionArguments;
import org.broadleafcommerce.broadleafxmlmigrator.arguments.LateAndEarlyStageExecutionArguments;
import org.broadleafcommerce.broadleafxmlmigrator.arguments.LoggableExecutionArguments;
import org.broadleafcommerce.broadleafxmlmigrator.arguments.RemovedBeanExecutionArguments;
import org.broadleafcommerce.broadleafxmlmigrator.arguments.WorkflowExecutionArguments;
import org.broadleafcommerce.broadleafxmlmigrator.helper.DocumentHelper;
import org.broadleafcommerce.broadleafxmlmigrator.helper.LoggingHelper;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Main migration object that should be instantiated once and used many times for all application contexts for a project.
 * This class handles migrating xml files that utilized BLC's XML merge processes for merging collections with the same
 * id together as long as the id lived in default.properties. Additionally this migrator will remove any beans that are
 * no longer needed.
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class XMLMigrator {
    
    protected StringBuilder ongoingLog = new StringBuilder();
    
    protected static Log LOG = LogFactory.getLog(XMLMigrator.class);
    
    protected Map<String, String> affectedBeanMap = new LinkedHashMap<>();
    
    protected Map<String, Integer> persistentAffectedBeanMap = new LinkedHashMap<>();
    
    protected boolean isDryRun = false;
    
    protected String qualifier = "";
    
    public XMLMigrator(boolean isDryRun, String qualifier) {
        this.isDryRun = isDryRun;
        this.qualifier = qualifier;
    }

    /**
     * Given a file this method will migrate all beans that were originally being merged (previously located in default.properties in BLC)
     * and either convert those to being their own collection with a Early/LateStageMergeBeanPostProcessor bean, removed, or otherwise modified
     * to work with BLC 5.2+  
     * 
     * @param file
     * @throws Exception
     */
    public void doMigrations(File file) throws Exception {
        if (!isAppContext(file)) {
            return;
        }
        Document document = DocumentHelper.buildDocument(file);
        boolean changed = false;
        for (ExecutionArguments executionArgs : new LateAndEarlyStageExecutionArguments().getLateAndEarlyMergeArguments()) {
            changed = insertAndReplaceWithMerge(document, executionArgs) || changed;
        }
        changed = handleWorkflowBeans(document) || changed;
        changed = handleVariableExpressions(document) || changed;
        changed = handleRemoveUnneededBeans(document) || changed;
        changed = updateXsdSchema(document) || changed;
        if (changed) {
            if (isDryRun) {
                LOG.info(DocumentHelper.formatDocumentToString(document));
            } else {
                try (FileWriter writer = new FileWriter(file, false)) {
                    writer.write(DocumentHelper.formatDocumentToString(document));
                }
            }
            LoggingHelper.printBeanChanges(file.getPath(), ongoingLog, affectedBeanMap);
            LOG.info("\n" + ongoingLog.toString());
        }
        ongoingLog = new StringBuilder();
        affectedBeanMap = new LinkedHashMap<>();
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
     * @return true if document was modified or false if not
     */
    protected boolean insertAndReplaceWithMerge(Document document, ExecutionArguments args) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        if (oldBeanDef == null) {
            return false;
        }
        if (addCollectionAndMergeBeans(oldBeanDef, args, document)) {
            document.getFirstChild().removeChild(oldBeanDef);
            return true;
        }
        return false;
    }
    
    /**
     * Wrapper around the other handleWorkflowBeans method that simply gets the workflow arguments
     * @see handleWorkflwoBeans(Document, ExecutionArguments, String)
     * @param document
     * @param newBeanIdQualifier
     * @throws XPathExpressionException
     */
    protected boolean handleWorkflowBeans(Document document) throws XPathExpressionException {
        boolean changed = false;
        for (ExecutionArguments args : new WorkflowExecutionArguments().getWorkflowArguments()) {
            changed = handlWorkflowBeans(document, args) || changed;
        }
        return changed;
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
    protected boolean handlWorkflowBeans(Document document, ExecutionArguments args) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        if (oldBeanDef == null) {
            return false;
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
            return insertAndReplaceWithMerge(document, args);
        }
        
        // If we actually find activities and create the merge bean then we'll replace the activities property with a reference
        // to the target bean so that their override will have the activities since it's a normal Spring wholesale override
        if (addCollectionAndMergeBeans(oldBeanDef, args, document)) {
            ongoingLog.append("We found a workflow with xpath " + args.getBeanXpath() + " and it had more properties than just the activities.\nWe're assuming that there were custom properties set so we will simply merge the activities into the correct bean named " + args.getTargetBeanId() + " and update the activities reference to that bean.\nIf there is no actual customizations then the bean at xpath " + args.getBeanXpath() + " can instead be removed\n\n");
            String activitiesXpath = new WorkflowExecutionArguments().getActivitiesXpath(args);
            Node activitiesNode = (Node) evaluator.evaluate(activitiesXpath, document, XPathConstants.NODE);
            if (activitiesNode != null) {
                oldBeanDef.removeChild(activitiesNode);
            }
            Element activitiesElement = document.createElement("property");
            activitiesElement.setAttribute("name", "activities");
            activitiesElement.setAttribute("ref", args.getTargetBeanId());
            oldBeanDef.appendChild(activitiesElement);
            return true;
        }
        return false;
    }
    
    /**
     * Creates a new collection bean and MergeBeanPostProcessor based on the given ExecutionArguments and then places
     * those beans above oldBeanDef in the given document 
     * 
     * @param oldBeanDef
     * @param args
     * @param document
     * @return true if any changes to document occurred and false otherwise
     * @throws XPathExpressionException
     */
    protected boolean addCollectionAndMergeBeans(Node oldBeanDef, ExecutionArguments args, Document document) throws XPathExpressionException {
        String newBeanId = resolveBeanIdWithQualifier(args);
        Element newCollectionBean = DocumentHelper.createCollectionBean(document, args, newBeanId);
        if (newCollectionBean == null) {
            return false;
        }
        Element mergeBean = DocumentHelper.createMergeBean(document, args, newBeanId);
        document.getFirstChild().insertBefore(newCollectionBean, oldBeanDef);
        document.getFirstChild().insertBefore(mergeBean, oldBeanDef);
        updateAdditionalBeanIdQualifierMap(args);
        logBeanChange(args.getBeanXpath(), newBeanId);
        return true;
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
    protected boolean handleVariableExpressions(Document document) throws XPathExpressionException {
        ExecutionArguments args = new ExecutionArguments
                (new String[]{"/beans/bean[@id='blVariableExpressions']/property[@name='sourceList']/list/bean"},
                 "/beans/bean[@id='blVariableExpressions']", "blVariableExpressions", null, null);
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node oldBeanDef = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
        if (oldBeanDef == null) {
            return false;
        }
        ongoingLog.append("Variable Expressions now just have to be defined and not merged into blVariableExpression.\nThe beans that were created in blVariableExpressions were moved out and then the blVariableExpressions definition was removed\n\n");
        for (String xpath : args.getCollectionXpaths()) {
            NodeList vals = (NodeList) evaluator.evaluate(xpath, document, XPathConstants.NODESET);
            for (int i = 0; i < vals.getLength(); i++) {
                document.getFirstChild().insertBefore(vals.item(i), oldBeanDef);
            }
        }
        document.getFirstChild().removeChild(oldBeanDef);
        logBeanChange(args.getBeanXpath(), "The original bean was just removed. See logs above");
        return true;
    }
    
    /**
     * Handles removing bean definitions that aren't needed anymore
     * 
     * @param document
     * @throws XPathExpressionException
     */
    protected boolean handleRemoveUnneededBeans(Document document) throws XPathExpressionException {
        boolean changed = false;
        XPath evaluator = XPathFactory.newInstance().newXPath();
        for (LoggableExecutionArguments args : new RemovedBeanExecutionArguments().getLoggableExecutionArguments()) {
            Node node  = (Node) evaluator.evaluate(args.getBeanXpath(), document, XPathConstants.NODE);
            if (node != null) {
                document.getFirstChild().removeChild(node);
                ongoingLog.append(args.getLogMessage() + "\n\n");
                logBeanChange(args.getBeanXpath(), "The original bean was just removed. See logs above");
                changed = true;
            }
        }
        return changed;
    }
    
    protected boolean updateXsdSchema(Document document) throws XPathExpressionException {
        XPath evaluator = XPathFactory.newInstance().newXPath();
        Node node = (Node) evaluator.evaluate("/beans", document, XPathConstants.NODE);
        NamedNodeMap attributeMap = node.getAttributes();
        if (attributeMap == null) {
            return false;
        }
        Node schemaNode = attributeMap.getNamedItem("xsi:schemaLocation");
        if (schemaNode == null) {
            return false;
        }
        String schemaValue = schemaNode.getNodeValue();
        String resultingValue = schemaValue.replaceAll("spring-([a-zA-Z]+)-[0-9].[0-9].xsd", "spring-$1.xsd");
        if (!resultingValue.equals(schemaValue)) {
            schemaNode.setNodeValue(resultingValue);
            return true;
        }
        return false;
    }
    
    /**
     * Logs the old bean xpath that was changed and the newBeanId or other helpful value for logging/debugging
     * 
     * @param oldXpath
     * @param newBeanId
     */
    protected void logBeanChange(String oldXpath, String newBeanId) {
        affectedBeanMap.put(oldXpath, newBeanId);
    }
    
    /**
     * In order to avoid collisions over multiple uses of the XMLMigrator we keep up with a counter for how many
     * times we had to replace a bean with an appropriate MergeBeanPostProcessor. This method is used to easily
     * retrieve what the new bean id should be named given an ExecutionArguments object.
     * 
     * For example if the XMLMigrator were to be used on file A and file B where both A and B had the bean CheckoutWorkflow
     * then without resolving the new bean id through this method two beans would be made with the name blCheckoutWorkflowActivities-{qualifier}
     * which would result in a startup exception
     * 
     * @param args
     * @return
     */
    protected String resolveBeanIdWithQualifier(ExecutionArguments args) {
        Integer counter = persistentAffectedBeanMap.get(args.getTargetBeanId());
        String newBeanId = args.getTargetBeanId() + "-" + qualifier;
        if (counter != null) {
            return newBeanId + "-" + counter;
        }
        return newBeanId;
    }
    
    /**
     * This method increments the next number to be added to the new bean id qualifier when creating a new bean for a client
     * collection. See {@link #resolveBeanIdWithQualifier(ExecutionArguments)} for more details
     * 
     * @param args
     */
    protected void updateAdditionalBeanIdQualifierMap(ExecutionArguments args) {
        Integer counter = persistentAffectedBeanMap.get(args.getTargetBeanId());
        if (counter == null) {
            persistentAffectedBeanMap.put(args.getTargetBeanId(), 1);
        } else {
            persistentAffectedBeanMap.put(args.getTargetBeanId(), counter+1);
        }
    }
    
    /**
     * Simply checks if the given file has a name that conforms to a normal application context naming schema. It does not, however,
     * confirm that it is an XML file in contents.
     * 
     * @param file
     * @return
     */
    protected boolean isAppContext(File file) {
        String fileName = file.getName();
        return StringUtils.startsWithIgnoreCase(fileName, "applicationContext") && StringUtils.endsWithIgnoreCase(fileName, ".xml");
    }
}
