package org.broadleafcommerce.broadleafxmlmigrator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

@RunWith(MockitoJUnitRunner.class)
public class BroadleafXmlMigratorApplicationTests {
    
    protected static Log LOG = LogFactory.getLog(BroadleafXmlMigratorApplicationTests.class);

    @InjectMocks
    private BroadleafXmlMigratorApplication app = new BroadleafXmlMigratorApplication();
    
    @Test
    public void testAppRuns() throws Exception {
        XMLMigrator migrator = new XMLMigrator(false, "client");
        File testFile = getMutableFile("applicationContext.xml");
        app.recursivelyDoMigrations(testFile, migrator);
    }
    
    @Test
    public void testAppProducesSameOutput() throws Exception {
        File beforeFile = getMutableFile("applicationContext.xml");
        XMLMigrator migrator = new XMLMigrator(false, "client");
        app.recursivelyDoMigrations(beforeFile, migrator);
        File beenMigratedFile = getClasspathFile("applicationContext-after.xml");
        Assert.assertTrue(FileUtils.contentEquals(beforeFile, beenMigratedFile));
    }
    
    @Test
    public void testAppDoesNothingOnNonAppContext() throws Exception {
        File beforeFile = getMutableFile("somethingSomethingNotAnXMLFile");
        XMLMigrator migrator = new XMLMigrator(false, "client");
        app.recursivelyDoMigrations(beforeFile, migrator);
        Assert.assertTrue(FileUtils.contentEquals(beforeFile, getClasspathFile("applicationContext-before.xml")));
    }
    
    @Test
    public void testComplicatedDirectoryStructure()  throws Exception {
        File parentDirectory = new File("parent_directory");
        parentDirectory.mkdirs();
        File[] files = getComplicatedDirectoryStructure(parentDirectory);
        XMLMigrator migrator = new XMLMigrator(false, "client");
        app.recursivelyDoMigrations(parentDirectory, migrator);
        for (int i = 0; i < files.length; i++) {
            // Because of the collision protection every file after the first that is processed won't match
            // applicationContext-before.xml but we're just testing that they got read so as long as they changed
            // we'll count that as passing
            Assert.assertFalse(FileUtils.contentEquals(files[i], getClasspathFile("applicationContext-before.xml")));
        }
    }
    
    protected File getMutableFile(String newFileName) throws Exception {
        ClassPathResource beforeResource = new ClassPathResource("applicationContext-before.xml");
        File testFile = new File(newFileName);
        FileUtils.copyFile(beforeResource.getFile(), testFile);
        return testFile;
    }
    
    protected File getClasspathFile(String fileName) throws Exception {
        ClassPathResource afterResource = new ClassPathResource(fileName);
        return afterResource.getFile();
    }
    
    protected File[] getComplicatedDirectoryStructure(File parentDirectory) throws Exception {
        File templateFile = getClasspathFile("applicationContext-before.xml");
        File file1 = new File(parentDirectory, "applicationContext1.xml");
        FileUtils.copyFile(templateFile, file1);
        File childDirectory1 = new File(parentDirectory, "child_directory1");
        childDirectory1.mkdirs();
        File file2 = new File(childDirectory1, "applicationContext2.xml");
        FileUtils.copyFile(templateFile, file2);
        File file3 = new File(childDirectory1, "applicationContext3.xml");
        FileUtils.copyFile(templateFile, file3);
        return new File[]{file1, file2, file3};
    }
    
}
