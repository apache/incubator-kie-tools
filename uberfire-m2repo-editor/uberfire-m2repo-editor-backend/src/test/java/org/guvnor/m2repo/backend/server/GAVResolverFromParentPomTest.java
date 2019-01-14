/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;
import org.appformer.maven.support.PomModel;
import org.guvnor.m2repo.backend.server.helpers.FormData;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.guvnor.m2repo.backend.server.helpers.PomModelResolver;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertEquals;
import static org.guvnor.m2repo.backend.server.M2RepoServiceCreator.deleteDir;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_OK;

public class GAVResolverFromParentPomTest {

    private static final Logger log = LoggerFactory.getLogger(GAVResolverFromParentPomTest.class);

    private static final String PARENT_POM = "parent-gav-pom.xml";

    @AfterClass
    public static void tearDown() {
        log.info("Deleting all Repository instances..");

        File dir = new File("repositories");
        log.info("DELETING test repo: " + dir.getAbsolutePath());
        deleteDir(dir);
        log.info("TEST repo was deleted.");
    }

    @Before
    public void setup() throws Exception {
        M2RepoServiceCreator m2RepoServiceCreator = new M2RepoServiceCreator();
        HttpPostHelper helper = m2RepoServiceCreator.getHelper();
        java.lang.reflect.Method helperMethod = m2RepoServiceCreator.getHelperMethod();

        FormData uploadItem = new FormData();
        FileItem file = new MockFileItem("pom.xml",
                                         this.getClass().getResourceAsStream(PARENT_POM));
        uploadItem.setFile(file);

        assertEquals("Error occurred when uploading pom", UPLOAD_OK, helperMethod.invoke(helper,
                                                                                         uploadItem));
    }

    @Test
    public void testResolveGavFromParentPomOnlyGroupID() throws Exception {
        String pathToPomWithoutVersion = "org/guvnor/m2repo/backend/server/helpers/gav-pom-without-version.xml";

        final PomModel pomModel = resolvePom(pathToPomWithoutVersion);
        assertEquals("The groupID does not match to child pom", "org.guvnor.m2repo.backend.server.helpers", pomModel.getReleaseId().getGroupId());
        assertEquals("The version does not match to parent pom", "1.0", pomModel.getReleaseId().getVersion());
    }

    @Test
    public void testResolveGavFromParentPomOnlyVersion() throws Exception {
        String pathToPomWithoutGroup = "org/guvnor/m2repo/backend/server/helpers/gav-pom-without-group.xml";

        final PomModel pomModel = resolvePom(pathToPomWithoutGroup);
        assertEquals("The groupID does not match to parent pom", "org.guvnor.test", pomModel.getReleaseId().getGroupId());
        assertEquals("The version does not match to child pom", "1.1.1", pomModel.getReleaseId().getVersion());
    }

    private PomModel resolvePom(String path) throws Exception {
        InputStream pomInputStream = this.getClass().getClassLoader().getResourceAsStream(path);
        return PomModelResolver.resolveFromPom(pomInputStream);
    }
}
