package org.broadleafcommerce.broadleafxmlmigrator.arguments;

import java.util.ArrayList;
import java.util.List;

/**
 * A definition of all of the ExecutionArguments (i.e. rows from default.properties) that represent beans that can/need to be
 * deleted from the application context. These are LoggableExecutionArguments because the reason from removing a bean is
 * always specific and therefore it should be logged
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class RemovedBeanExecutionArguments {

    public static final LoggableExecutionArguments CONFIGURATION = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blConfiguration']", null, null, null, "The blConfiguration bean definition should be deleted and all additional property locations should be added to spring.factories");
    
    public static final LoggableExecutionArguments PAYMENT_WORKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blPaymentWorkflow']", null, null, null, "The blPaymentWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments AUTHORIZE_WORKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blAuthorizeWorkflow']", null, null, null, "The blAuthorizeWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments DEBIT_WORKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blDebitWorkflow']", null, null, null, "The blDebitWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments AUTHORIZE_AND_DEBIT_WORKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blAuthorizeAndDebitWorkflow']", null, null, null, "The blAuthorizeAndDebitWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments CREDIT_WORKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blCreditWorkflow']", null, null, null, "The blCreditWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments VOID_WORKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blVoidWorkflow']", null, null, null, "The blVoidWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments REVERSE_AUTH_WORKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blReverseAuthWorkflow']", null, null, null, "The blReverseAuthWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments PARTIAL_PAYMENT_WOKFLOW = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blPartialPaymentWorkflow']", null, null, null, "The blPartialPaymentWorkflow has been removed after workflows in BLC had been redone in BLC 3. Please refer to the documentation on migration notes");
    
    public static final LoggableExecutionArguments WEB_TEMPLATE_ENGINE = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blWebTemplateEngine']", null, null, null, "The template engines and all Thymeleaf configuration has been changed to Java configuration and changed to work with autowiring. Refer to the presentation layer documentation to learn how to add a custom template enigne, template resovlers, dialects, variable expressions, and processors");
    
    public static final LoggableExecutionArguments EMAIL_TEMPLATE_ENGINE = new LoggableExecutionArguments
            (null, "/beans/bean[@id='blEmailTemplateEngine']", null, null, null, "The template engines and all Thymeleaf configuration has been changed to Java configuration and changed to work with autowiring. Refer to the presentation layer documentation to learn how to add a custom template enigne, template resovlers, dialects, variable expressions, and processors");
    
    public static final LoggableExecutionArguments APPLICATION_FILTER_CHAIN = new LoggableExecutionArguments
            (null, "/beans/bean[@id='broadleafApplicationFilterChain']", null, null, null, "The broadleafApplicationFilterChain is unneeded and can be removed from your application. Refer to migration documentation if you had customizations");
    
    protected List<LoggableExecutionArguments> loggableExecutionArguments = new ArrayList<>();
    
    public RemovedBeanExecutionArguments() {
        loggableExecutionArguments.add(CONFIGURATION);
        loggableExecutionArguments.add(PAYMENT_WORKFLOW);
        loggableExecutionArguments.add(AUTHORIZE_WORKFLOW);
        loggableExecutionArguments.add(DEBIT_WORKFLOW);
        loggableExecutionArguments.add(AUTHORIZE_AND_DEBIT_WORKFLOW);
        loggableExecutionArguments.add(CREDIT_WORKFLOW);
        loggableExecutionArguments.add(VOID_WORKFLOW);
        loggableExecutionArguments.add(REVERSE_AUTH_WORKFLOW);
        loggableExecutionArguments.add(PARTIAL_PAYMENT_WOKFLOW);
        loggableExecutionArguments.add(WEB_TEMPLATE_ENGINE);
        loggableExecutionArguments.add(EMAIL_TEMPLATE_ENGINE);
        loggableExecutionArguments.add(APPLICATION_FILTER_CHAIN);
    }
    
    public List<LoggableExecutionArguments> getLoggableExecutionArguments() {
        return loggableExecutionArguments;
    }
}
