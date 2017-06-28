package org.broadleafcommerce.broadleafxmlmigrator;

import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionType {
    public static final CollectionType LIST = new CollectionType("list", ListFactoryBean.class.getName(), "sourceList");
    public static final CollectionType MAP = new CollectionType("map", MapFactoryBean.class.getName(), "sourceMap");
    public static final CollectionType SET = new CollectionType("set", SetFactoryBean.class.getName(), "sourceSet");
    private String collectionName;
    private String collectionClass;
    private String sourcePropertyName;
}