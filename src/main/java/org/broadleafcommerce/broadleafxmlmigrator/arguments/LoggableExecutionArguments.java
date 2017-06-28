package org.broadleafcommerce.broadleafxmlmigrator;

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
