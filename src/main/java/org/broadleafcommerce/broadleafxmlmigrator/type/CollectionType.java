package org.broadleafcommerce.broadleafxmlmigrator.type;

import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An enumeration for the different collections need for the XML migration process
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
@Data
@AllArgsConstructor
public class CollectionType {
    public static final CollectionType LIST = new CollectionType("list", ListFactoryBean.class.getName(), "sourceList");
    public static final CollectionType MAP = new CollectionType("map", MapFactoryBean.class.getName(), "sourceMap");
    public static final CollectionType SET = new CollectionType("set", SetFactoryBean.class.getName(), "sourceSet");
    
    /**
     * The name of the collection. Used to create the necessary element for the *FactoryBean
     */
    private String collectionName;
    
    /**
     * The name of the class that the parent collection bean should be a type of
     */
    private String collectionClass;
    
    /**
     * The source property name. This is needed because the name of the property changes for the *FacotryBean classes
     */
    private String sourcePropertyName;
}