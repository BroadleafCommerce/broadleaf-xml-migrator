package org.broadleafcommerce.broadleafxmlmigrator.arguments;

import org.broadleafcommerce.broadleafxmlmigrator.type.CollectionType;
import org.broadleafcommerce.broadleafxmlmigrator.type.MergeType;

public class LoggableExecutionArguments extends ExecutionArguments {

    protected String logMessage;
    
    public LoggableExecutionArguments(String[] collectionXpaths, String beanXpath, String targetBeanId, CollectionType collectionType, MergeType mergeType, String logMessage) {
        super(collectionXpaths, beanXpath, targetBeanId, collectionType, mergeType);
        this.logMessage = logMessage;
    }
    
    public String getLogMessage() {
        return logMessage;
    }

}
