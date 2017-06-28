package org.broadleafcommerce.broadleafxmlmigrator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MergeType {
    public static final MergeType LATE = new MergeType("org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor");
    public static final MergeType EARLY = new MergeType("org.broadleafcommerce.common.extensibility.context.merge.EarlyStageMergeBeanPostProcessor");
    private String mergeClass;
}