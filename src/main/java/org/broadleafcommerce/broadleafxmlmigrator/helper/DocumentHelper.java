package org.broadleafcommerce.broadleafxmlmigrator.helper;

import org.broadleafcommerce.broadleafxmlmigrator.arguments.ExecutionArguments;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class DocumentHelper {

    public static Document buildDocument(File file) throws ParserConfigurationException, SAXException, IOException {
        FileInputStream appctx = new FileInputStream(file);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(appctx);
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
    public static Element createCollectionBean(Document document, ExecutionArguments args, String newBeanId) throws XPathExpressionException {
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
    public static Element createMergeBean(Document document, ExecutionArguments args, String newBeanId) {
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
    
    public static String formatDocumentToString(Document document) throws TransformerFactoryConfigurationError, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
     
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
     
        transformer.transform(new DOMSource(document), streamResult);
        return stringWriter.toString();
    }
}
