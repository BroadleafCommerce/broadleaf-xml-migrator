package org.broadleafcommerce.broadleafxmlmigrator;

import java.util.ArrayList;
import java.util.List;

public class WorkflowExecutionArguments {

    public static final ExecutionArguments PRICING_WORKFLOW =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blPricingWorkflow']/property[@name='activities']/list/ref", "/beans/bean[@id='blPricingWorkflow']/property[@name='activities']/list/bean"},
                                   "/beans/bean[@id='blPricingWorkflow']", "blPricingWorkflowActivities", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments ADD_ITEM_WORKFLOW =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blAddItemWorkflow']/property[@name='activities']/list/ref", "/beans/bean[@id='blAddItemWorkflow']/property[@name='activities']/list/bean"},
                                   "/beans/bean[@id='blAddItemWorkflow']", "blAddItemWorkflowActivties", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments UPDATE_ITEM_WORKFLOW =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blUpdateItemWorkflow']/property[@name='activities']/list/ref", "/beans/bean[@id='blUpdateItemWorkflow']/property[@name='activities']/list/bean"},
                                   "/beans/bean[@id='blUpdateItemWorkflow']", "blUpdateItemWorkflowActivities", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments REMOVE_ITEM_WORKFLOW =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blRemoveItemWorkflow']/property[@name='activities']/list/ref", "/beans/bean[@id='blRemoveItemWorkflow']/property[@name='activities']/list/bean"},
                                   "/beans/bean[@id='blRemoveItemWorkflow']", "blRemoveItemWorkflowActivities", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments CHECKOUT_WORKFLOW =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blCheckoutWorkflow']/property[@name='activities']/list/ref", "/beans/bean[@id='blCheckoutWorkflow']/property[@name='activities']/list/bean"},
                                   "/beans/bean[@id='blCheckoutWorkflow']", "blCheckoutWorkflowActivities", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments UPDATE_PRODUCT_OPTIONS_WORKFLOW =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blUpdateProductOptionsForItemWorkflow']/property[@name='activities']/list/ref", "/beans/bean[@id='blUpdateProductOptionsForItemWorkflow']/property[@name='activities']/list/bean"},
                                   "/beans/bean[@id='blUpdateProductOptionsForItemWorkflow']", "blUpdateProductOptionsForItemWorkflowActivities", CollectionType.LIST, MergeType.LATE);
    
    protected List<ExecutionArguments> workflowArguments = new ArrayList<>();
    
    protected String activitiesXpath = "/property[@name='activities']";
    
    public WorkflowExecutionArguments() {
        workflowArguments.add(PRICING_WORKFLOW);
        workflowArguments.add(ADD_ITEM_WORKFLOW);
        workflowArguments.add(UPDATE_ITEM_WORKFLOW);
        workflowArguments.add(REMOVE_ITEM_WORKFLOW);
        workflowArguments.add(CHECKOUT_WORKFLOW);
        workflowArguments.add(UPDATE_PRODUCT_OPTIONS_WORKFLOW);
    }
    
    public List<ExecutionArguments> getWorkflowArguments() {
        return workflowArguments;
    }
    
    public String getActivitiesXpath(ExecutionArguments args) {
        return args.getBeanXpath() + activitiesXpath;
    }
}
