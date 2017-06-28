# broadleaf-xml-migrator
Migrates the XML from a Broadleaf 5.1 project to use the required 5.2 configuration

## Usage
- After cloning the repo locally build it into a JAR using 'mvn package'
- Next run the JAR now located at `target/` by doing `java -jar target/broadleaf-xml-migrator-{version}.jar` with the below arguments
    - `--filepath={filepath}`
        - **Required**
        - This can be a filepath to one xml file or to a directory that has xml files. For ease of use this would be the filepath to your root project directory
    - `--qualifier={qualifier}`
        - Optional
        - This is the custom text that should be added to bean ids in order to keep them unique from a broadleaf bean id
            - For example if in your project you added a custom activity to the CheckoutWorkflow like below
            
            ```
            <bean id="blCheckoutWorkflow" class="org.broadleafcommerce.core.workflow.SequenceProcessor">
                <property name="activities">
                    <list>
                        <!-- This activity should occur at the very end of the checkout workflow, after everything has been executed -->
                        <bean p:order="9999999" class="com.mycompany.workflow.checkout.SendOrderConfirmationEmailActivity" />
                    </list>
                </property>
            </bean>
            ```
            Then the result would be
            ```
            <bean id="blCheckoutWorkflowActivities-client" class="org.springframework.beans.factory.config.ListFactoryBean">
                <property name="sourceList">
                    <list>
                        <bean class="com.mycompany.workflow.checkout.SendOrderConfirmationEmailActivity" p:order="9999999"/>
                    </list>
                </property>
            </bean>
            <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor">
                <property name="sourceRef" value="blCheckoutWorkflowActivities-client"/>
                <property name="targetRef" value="blCheckoutWorkflowActivities"/>
            </bean>
            ```
            where `client` in the id `blCheckoutWorkflowActivities-client` is the qualifier
    - `--dryrun={true|false}`
        - Optional
        - Defaults to false
        - If set to true then the resulting migrated xml file prints to the console. If not set or not true then the file that was being migrated will be altered in place
        
## Example Usage
`mvn package;java -jar target/broadleaf-xml-migrator-0.0.1-SNAPSHOT.jar --filepath=/Users/user/blc/DemoSite --dryrun=true --qualifier=ds`

## Running without packaging into a jar
`mvn spring-boot:run -Drun.arguments="--filepath=/Users/user/blc/DemoSite,--qualifier=ds,--dryrun=true"`
