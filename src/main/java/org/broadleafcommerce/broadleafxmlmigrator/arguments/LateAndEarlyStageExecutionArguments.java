package org.broadleafcommerce.broadleafxmlmigrator.arguments;

import org.broadleafcommerce.broadleafxmlmigrator.type.CollectionType;
import org.broadleafcommerce.broadleafxmlmigrator.type.MergeType;

import java.util.ArrayList;
import java.util.List;

/**
 * A definition of all of the ExecutionArguments (i.e. rows from default.properties) that can be generically changed
 * directly from their current state to a collection bean merged with the target bean using a *StageMergeBeanPostProcessor bean.
 * 
 * This class was made because it was the easiest way to get the definitions into a list that can be loopable.
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class LateAndEarlyStageExecutionArguments {

    public static final ExecutionArguments MERGED_PERSISTENCE_XML_LOCATIONS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blMergedPersistenceXmlLocations']/property[@name='sourceList']/list/value"},
                                   "/beans/bean[@id='blMergedPersistenceXmlLocations']", "blMergedPersistenceXmlLocations", CollectionType.LIST, MergeType.EARLY);
    
    public static final ExecutionArguments MERGED_DATA_SOURCES =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blMergedDataSources']/property[@name='sourceMap']/map/entry"},
                                   "/beans/bean[@id='blMergedDataSources']", "blMergedDataSources", CollectionType.MAP, MergeType.EARLY);
    
    public static final ExecutionArguments MERGED_CACHE_CONFIG_LOCATIONS = 
            new ExecutionArguments(new String[]{"/beans/bean[@id='blMergedCacheConfigLocations']/property[@name='sourceList']/list/value"},
                                   "/beans/bean[@id='blMergedCacheConfigLocations']", "blMergedCacheConfigLocations", CollectionType.LIST, MergeType.EARLY);
    
    public static final ExecutionArguments CUSTOM_PERSISTENCE_HANDLERS = 
            new ExecutionArguments(new String[]{"/beans/bean[@id='blCustomPersistenceHandlers']/property[@name='sourceList']/list/bean", "/beans/bean[@id='blCustomPersistenceHandlers']/property[@name='sourceList']/list/ref"},
                                   "/beans/bean[@id='blCustomPersistenceHandlers']", "blCustomPersistenceHandlers", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments CONTENT_RULE_PROCESSORS = 
            new ExecutionArguments(new String[]{"/beans/bean[@id='blContentRuleProcessors']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blContentRuleProcessors']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blContentRuleProcessors']", "blContentRuleProcessors", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments MERGED_ENTITY_CONTEXTS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blMergedEntityContexts']/property[@name='sourceList']/list/value"},
                                   "/beans/bean[@id='blMergedEntityContexts']", "blMergedEntityContexts", CollectionType.LIST, MergeType.EARLY);
    
    public static final ExecutionArguments STATIC_MAP_NAMED_OPERATION = 
            new ExecutionArguments(new String[]{"/beans/bean[@id='blStaticMapNamedOperationComponent']/property[@name='namedOperations']/map/entry"},
                                   "/beans/bean[@id='blStaticMapNamedOperationComponent']", "blStaticMapNamedOperations", CollectionType.MAP, MergeType.LATE);
    
    public static final ExecutionArguments FULFILLMENT_PRICING_PROVIDERS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blFulfillmentPricingProviders']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blFulfillmentPricingProviders']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blFulfillmentPricingProviders']", "blFulfillmentPricingProviders", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments PAGE_RULE_PROCESSORS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blPageRuleProcessors']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blPageRuleProcessors']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blPageRuleProcessors']", "blPageRuleProcessors", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments MESSAGE_SOURCE = 
            new ExecutionArguments(new String[]{"/beans/bean[@id='messageSource']/property[@name='basenames']/list/value"}, 
                                   "/beans/bean[@id='messageSource']", "blMessageSourceBaseNames", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments MERGED_CLASS_TRANSFORMERS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blMergedClassTransformers']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blMergedClassTransformers']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blMergedClassTransformers']", "blMergedClassTransformers", CollectionType.LIST, MergeType.EARLY);
    
    public static final ExecutionArguments FIELD_METADATA_PROVIDERS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blFieldMetadataProviders']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blFieldMetadataProviders']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blFieldMetadataProviders']", "blFieldMetadataProviders", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments PERSISTENCE_PROVIDERS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blPersistenceProviders']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blPersistenceProviders']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blPersistenceProviders']", "blPersistenceProviders", CollectionType.LIST, MergeType.EARLY);
    
    public static final ExecutionArguments ADDITIONAL_SECTION_AUTHORIZATIONS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blAdditionalSectionAuthorizations']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blAdditionalSectionAuthorizations']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blAdditionalSectionAuthorizations']", "blAdditionalSectionAuthorizations", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments TAX_PROVIDERS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blTaxProviders']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blTaxProviders']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blTaxProviders']", "blTaxProviders", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments ADDRESS_VERFICATION_PROVIDERS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blAddressVerificationProviders']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blAddressVerificationProviders']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blAddressVerificationProviders']", "blAddressVerificationProviders", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments RULE_BUILDER_FIELD_SERVICES =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blRuleBuilderFieldServices']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blRuleBuilderFieldServices']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blRuleBuilderFieldServices']", "blRuleBuilderFieldServices", CollectionType.LIST, MergeType.LATE);
    
    public static final ExecutionArguments PERSISTENCE_UNIT_POST_PROCESSORS =
            new ExecutionArguments(new String[]{"/beans/bean[@id='blPersistenceUnitPostProcessors']/property[@name='sourceList']/list/ref", "/beans/bean[@id='blPersistenceUnitPostProcessors']/property[@name='sourceList']/list/bean"},
                                   "/beans/bean[@id='blPersistenceUnitPostProcessors']", "blPersistenceUnitPostProcessors", CollectionType.LIST, MergeType.EARLY);
    
    public static final ExecutionArguments CONVERSION_SERVICE = 
            new ExecutionArguments(new String[]{"/beans/bean[@id='conversionService']/property/list/bean", "/beans/bean[@id='conversionService']/property/list/ref"},
                                   "/beans/bean[@id='conversionService']", "blConverters", CollectionType.SET, MergeType.LATE);
    
    protected List<ExecutionArguments> lateAndEarlyMergeArguments = new ArrayList<>();
    
    public LateAndEarlyStageExecutionArguments() {
        lateAndEarlyMergeArguments.add(MERGED_PERSISTENCE_XML_LOCATIONS);
        lateAndEarlyMergeArguments.add(MERGED_DATA_SOURCES);
        lateAndEarlyMergeArguments.add(MERGED_CACHE_CONFIG_LOCATIONS);
        lateAndEarlyMergeArguments.add(CUSTOM_PERSISTENCE_HANDLERS);
        lateAndEarlyMergeArguments.add(CONTENT_RULE_PROCESSORS);
        lateAndEarlyMergeArguments.add(MERGED_ENTITY_CONTEXTS);
        lateAndEarlyMergeArguments.add(STATIC_MAP_NAMED_OPERATION);
        lateAndEarlyMergeArguments.add(FULFILLMENT_PRICING_PROVIDERS);
        lateAndEarlyMergeArguments.add(PAGE_RULE_PROCESSORS);
        lateAndEarlyMergeArguments.add(MESSAGE_SOURCE);
        lateAndEarlyMergeArguments.add(MERGED_CLASS_TRANSFORMERS);
        lateAndEarlyMergeArguments.add(FIELD_METADATA_PROVIDERS);
        lateAndEarlyMergeArguments.add(PERSISTENCE_PROVIDERS);
        lateAndEarlyMergeArguments.add(ADDITIONAL_SECTION_AUTHORIZATIONS);
        lateAndEarlyMergeArguments.add(TAX_PROVIDERS);
        lateAndEarlyMergeArguments.add(ADDRESS_VERFICATION_PROVIDERS);
        lateAndEarlyMergeArguments.add(RULE_BUILDER_FIELD_SERVICES);
        lateAndEarlyMergeArguments.add(PERSISTENCE_UNIT_POST_PROCESSORS);
        lateAndEarlyMergeArguments.add(CONVERSION_SERVICE);
    }
    
    public List<ExecutionArguments> getLateAndEarlyMergeArguments() {
        return lateAndEarlyMergeArguments;
    }
}
