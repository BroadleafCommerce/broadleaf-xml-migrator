package org.broadleafcommerce.broadleafxmlmigrator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class BroadleafXmlMigratorApplication implements ApplicationRunner {
    
    public static final String DRY_RUN_KEY = "dryrun";
    
    public static final String QUALIFIER_KEY = "qualifier";
    
    public static final String FILE_PATH_KEY = "filepath";
    
    public static final String OUTPUT_DIRECTORY = "outputpath";
    
    protected static Log LOG = LogFactory.getLog(BroadleafXmlMigratorApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(BroadleafXmlMigratorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> filePathArgs = args.getOptionValues(FILE_PATH_KEY);
        boolean dryRun = resolveDryRunArg(args.getOptionValues(DRY_RUN_KEY));
        String qualifier = resolveQualifierArg(args.getOptionValues(QUALIFIER_KEY));
        String outputDirectory = resolveOutputDirectory(args.getOptionValues(OUTPUT_DIRECTORY));
        XMLMigrator migrator = new XMLMigrator(dryRun, qualifier, outputDirectory);
        if (!CollectionUtils.isEmpty(filePathArgs)) {
            for (String path : filePathArgs) {
                File file = new File(path);
                recursivelyDoMigrations(file, migrator);
            }
        } else {
            LOG.info("No file path was specified. Please try again with a --" + FILE_PATH_KEY + " argument");
        }
    }
    
    protected void recursivelyDoMigrations(File file, XMLMigrator migrator) throws Exception {
        if (file == null) {
            return;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                recursivelyDoMigrations(files[i], migrator);
            }
        }
        migrator.doMigrations(file);
    }
    
    protected String resolveQualifierArg(List<String> qualifierArgs) {
        if (CollectionUtils.isEmpty(qualifierArgs)) {
            LOG.info("No qualifier argument specified by a --" + QUALIFIER_KEY + " argument. Using the word 'client' for a new bean id qualifier.");
            return "client";
        }
        return qualifierArgs.get(0);
    }
    
    protected boolean resolveDryRunArg(List<String> dryRunArgs) {
        if (CollectionUtils.isEmpty(dryRunArgs)) {
            LOG.info("No dry run argument specified by a --" + DRY_RUN_KEY + " argument. Using the --" + DRY_RUN_KEY + "=true argument will print the resulting xml file to the console instead of modifiying the given xml file in place.");
            return false;
        }
        String dryRunString = dryRunArgs.get(0);
        try {
            return Boolean.parseBoolean(dryRunString);
        } catch (Exception e) {}
        return false;
    }
    
    protected String resolveOutputDirectory(List<String> outputArgs) {
        if (CollectionUtils.isEmpty(outputArgs)) {
            LOG.info("No output directory defined. This is an optional argument but if it desired that the resulting migrated files be saved in a different directory and new file then --" + OUTPUT_DIRECTORY + "={path} must be sent");
            return null;
        }
        return outputArgs.get(0);
    }
}
