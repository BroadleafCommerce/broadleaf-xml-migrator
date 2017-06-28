package org.broadleafcommerce.broadleafxmlmigrator.type;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Enumeration to define the different *StageMergeBeanPostProcessors
 * 
 * Mostly just created to avoid needing to have conditionals everywhere
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
@Data
@AllArgsConstructor
public class MergeType {
    public static final MergeType LATE = new MergeType("org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor");
    public static final MergeType EARLY = new MergeType("org.broadleafcommerce.common.extensibility.context.merge.EarlyStageMergeBeanPostProcessor");
    private String mergeClass;
}