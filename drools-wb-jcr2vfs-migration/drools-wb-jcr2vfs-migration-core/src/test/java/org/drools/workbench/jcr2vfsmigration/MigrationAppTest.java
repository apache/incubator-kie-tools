package org.drools.workbench.jcr2vfsmigration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MigrationAppTest {



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


}
