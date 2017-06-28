package org.broadleafcommerce.broadleafxmlmigrator;

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
    
    public static void main(String[] args) {
        SpringApplication.run(BroadleafXmlMigratorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> filePathArgs = args.getOptionValues(FILE_PATH_KEY);
        boolean dryRun = resolveDryRunArg(args.getOptionValues(DRY_RUN_KEY));
        String qualifier = resolveQualifierArg(args.getOptionValues(QUALIFIER_KEY));
        XMLMigrator migrator = new XMLMigrator(dryRun, qualifier);
        if (!CollectionUtils.isEmpty(filePathArgs)) {
            for (String path : filePathArgs) {
                File file = new File(path);
                recursivelyDoMigrations(file, migrator);
            }
        } else {
            //throw new IllegalArgumentException("No folder or file path for an application context was provided");
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
            return "client";
        }
        return qualifierArgs.get(0);
    }
    
    protected boolean resolveDryRunArg(List<String> dryRunArgs) {
        if (CollectionUtils.isEmpty(dryRunArgs)) {
            return false;
        }
        String dryRunString = dryRunArgs.get(0);
        try {
            return Boolean.parseBoolean(dryRunString);
        } catch (Exception e) {}
        return false;
    }
}
