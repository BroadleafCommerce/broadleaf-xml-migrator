# broadleaf-xml-migrator
Migrates the application context XML files from a Broadleaf 5.1 project to use the required 5.2 configuration

## Project Setup
This project can either be cloned locally or the executable jar can be downloaded [here](https://github.com/BroadleafCommerce/broadleaf-xml-migrator/releases/new)

## Usage
### Using Downloaded JAR
Locate the JAR in your local system and run
```
java -jar broadleaf-xml-migrator-{version}.jar --filepath={filepath} --qualifier={qualifier} --dryrun={true|false}
```
### Using JAR created from cloned repo
Create the JAR by navigating to the root directory of where the repo was cloned and run
```
mvn package
```
The JAR will be built inside the `target` directory so navigate there and run the command mentioned for the "Using Downloaded JAR" section
### Running without creating a JAR
Naviagte to the root directory of where the repo was cloned and run
```
mvn spring-boot:run -Drun.arguments="--filepath={filepath},--qualifier={qualifier},--dryrun={true|false}"
```
### Arguments
- `--filepath={filepath}`
    - **Required**
    - The file or directory where the application contexts reside that need migrating. If it's a path to a file then that file will be used. If it's a folder then the application will recursively go through all of the sub directories and run the migration on every file that starts with `applicationContext` and ends with `xml`.
    - **If you are upgrading an entire project set this to the path to the root of your project**
        - This is important because the application deals with collisions and will prevent your application from declaring multiple beans with the same id
        - Alternatively you can run the application multiple times for one project but make sure you change the qualifier argument for every run.
- `--qualifier={qualifier}`
    - Optional
    - Defaults to `client`
    - This is the custom text that should be added to bean ids in order to keep them unique from a broadleaf bean id. For example if in your project you added a custom activity to the CheckoutWorkflow like below

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
    - In the event that multiple declarations exist (for example it's common to add to `blMergedPersistenceXmlLocations` multiple times in one project) a `- {number}` will be added to the end of the bean id in order to prevent that application from having declarations of beans with the same name.
        - For example if multiple applicationContexts were adding activities to the `blCheckoutWorkflow` then the second time it was found the resulting id of the collection bean would be `blCheckoutWorkflowActivities-client-1`.
- `--dryrun={true|false}`
    - Optional
    - Defaults to false
    - If set to true then the resulting migrated xml file prints to the console. If not set or not true then the file that was being migrated will be altered in place
        
## Example Usage
`mvn package;java -jar target/broadleaf-xml-migrator-0.0.1-SNAPSHOT.jar --filepath=/Users/user/blc/DemoSite --dryrun=true --qualifier=ds`

## Running without packaging into a jar
`mvn spring-boot:run -Drun.arguments="--filepath=/Users/user/blc/DemoSite,--qualifier=ds,--dryrun=true"`

## Example Results
The result of running
`mvn spring-boot:run -Drun.arguments="--filepath=src/main/resources/applicationContext-before.xml"` can be found [here](https://github.com/BroadleafCommerce/broadleaf-xml-migrator/blob/master/src/main/resources/applicationContext-after.xml).
**Note** The file was originally changed in place but an original copy was retained for comparison and was named applicationContext-before
