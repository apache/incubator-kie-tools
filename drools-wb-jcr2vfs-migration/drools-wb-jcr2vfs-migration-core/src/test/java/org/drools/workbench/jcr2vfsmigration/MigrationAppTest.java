package org.drools.workbench.jcr2vfsmigration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.drools.workbench.jcr2vfsmigration.vfs.IOServiceFactory;
import org.junit.Test;
import org.kie.workbench.java.nio.fs.jgit.JGitFileSystemProvider;


public class MigrationAppTest {

    @Test
    public void migrateExample() throws IOException {
        migrate("migrationExample");
    }

    private void verifyResult() {
        //testDTXLS.xls
        //testSrpingContext.springContext
        //testFunction.function
        //testChangeSet.changeset
        //testWorkItem.wid
        //? testRuleTemplate.template: use DRL instead?
        //testFile.doc. "others" format
        
        //TODO:  testServiceConfig.serviceConfig: 
        //serviceConfig has references to UUID and URL:
        //<url>http://localhost:8080/guvnor-5.5.1-SNAPSHOT-jboss-as-7.0/rest/packages/testMigrationPackage/assets/testChangeSet/source</url>
        //<uuid>12a5be42-ea3e-43be-9da4-b3bc6f626f84</uuid>
        
        //NOTE: testWorkingSets.workingset. There is no workingset in 6.0
    }

    private void migrate(String datasetName) throws IOException {
        File testBaseDir = new File("target/test/" + datasetName);
        if (testBaseDir.exists()) {
            FileUtils.deleteDirectory(testBaseDir);
        }
        testBaseDir.mkdirs();
        testBaseDir = testBaseDir.getCanonicalFile();

        File outputVfsRepository = new File(testBaseDir, "outputVfs");
        
        //Hack: Force JGitFileSystemProvider to reload git root dir due to JUnit class loader problem
        System.setProperty("org.kie.nio.git.dir", outputVfsRepository.getCanonicalPath());
        JGitFileSystemProvider.loadConfig();
        //Hack: Force to create a new FileSystem
        IOServiceFactory.DEFAULT_MIGRATION_FILE_SYSTEM = "drools-wb-jcr2vfs-migration-another";
        
        Jcr2VfsMigrationApp.main(
                "-i", getClass().getResource(datasetName + ".jcr").getFile(),
                "-o", outputVfsRepository.getCanonicalPath());
    }
}
