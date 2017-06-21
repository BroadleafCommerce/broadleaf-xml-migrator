package org.broadleafcommerce.broadleafxmlmigrator;

import org.springframework.beans.factory.config.ListFactoryBean;
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
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
        XPath evaluator = XPathFactory.newInstance().newXPath();
        NodeList vals = (NodeList) evaluator.evaluate("/beans/bean[@id='messageSource']/property[@name='basenames']/list/value", document, XPathConstants.NODESET);
        
        String newBeanId = "messageSourceBaseNames-client";
        Element newListBean = document.createElement("bean");
        newListBean.setAttribute("id", newBeanId);
        newListBean.setAttribute("class", ListFactoryBean.class.getName());
        Element list = document.createElement("list");
        for (int i = 0; i < vals.getLength(); i++) {
            list.appendChild(vals.item(i));
        }
        newListBean.appendChild(list);
        
        Element mergeBean = document.createElement("bean");
        mergeBean.setAttribute("class", "org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor");
        Element source = document.createElement("property");
        source.setAttribute("name", "sourceRef");
        source.setAttribute("value", newBeanId);
        mergeBean.appendChild(source);
        Element target = document.createElement("property");
        target.setAttribute("name", "targetRef");
        target.setAttribute("value", "messageSourceBaseNames");
        mergeBean.appendChild(target);
        
        Node oldBeanDef = (Node) evaluator.evaluate("/beans/bean[@id='messageSource']", document, XPathConstants.NODE);
        
        // the first child should always be <beans>
        document.getFirstChild().insertBefore(newListBean, oldBeanDef);
        document.getFirstChild().insertBefore(mergeBean, oldBeanDef);
        document.getFirstChild().removeChild(oldBeanDef);
        
        System.out.println(new XMLDocument(document).toString());
    }
    
    protected void loadHandlerProperties() throws IOException {
        Properties defaultProperties = new Properties();
        defaultProperties.load(new ClassPathResource("default.properties").getInputStream());
        Iterator propsIterator = defaultProperties.entrySet().iterator();
        while (propsIterator.hasNext()) {
            
        }
    }
    
    @Data
    public static class Handler {
        private String parentOldXpath;
        private List<String> mergePointXpaths;
        private String newMergePointBeanId;
    }
}
