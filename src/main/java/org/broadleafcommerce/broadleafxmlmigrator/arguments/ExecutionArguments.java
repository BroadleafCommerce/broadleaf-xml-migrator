package org.broadleafcommerce.broadleafxmlmigrator.arguments;

import org.broadleafcommerce.broadleafxmlmigrator.type.CollectionType;
import org.broadleafcommerce.broadleafxmlmigrator.type.MergeType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Mostly a DTO class to represent a row in the default.properties file
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
@Data
@AllArgsConstructor
public class ExecutionArguments {
    
    /**
     * The xpaths that should be searched for matching elements that need to be in the new collection bean
     */
    private String[] collectionXpaths;
    
    /**
     * The xpath to the parent bean. This is needed so that it can be removed or modified easier than working from the collectionXpaths
     */
    private String beanXpath;
    
    /**
     * The name of the bean that should be the value of the target property on the *StageMergeBeanPostProcessor bean
     */
    private String targetBeanId;
    
    /**
     * The type of collection that this is so that the correct collection bean can be made
     */
    private CollectionType collectionType;
    
    /**
     * The type of merge that we need to do
     */
    private MergeType mergeType;

}
