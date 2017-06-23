package org.broadleafcommerce.broadleafxmlmigrator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecutionArguments {
    
    private String[] collectionXpaths;
    private String beanXpath;
    private String targetBeanId;
    private CollectionType collectionType;
    private MergeType mergeType;

}
