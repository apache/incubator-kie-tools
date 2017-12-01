/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server;

import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.assertContains;

/**
 * Tests for DataModelService
 */
public class ProjectDataModelPackageWhiteListTest extends AbstractDataModelWeldTest {

    @Test
    public void testPackageNameWhiteList_EmptyWhiteList() throws Exception {
        final ProjectDataModelOracle oracle =
                initializeProjectDataModelOracle("/DataModelPackageWhiteListTest1");

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getProjectModelFields().size());
        assertContains("t7p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("t7p2.Bean2",
                       oracle.getProjectModelFields().keySet());

        assertEquals(1,
                     oracle.getProjectModelFields().get("t7p1.Bean1").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t7p1.Bean1"));

        assertEquals(1,
                     oracle.getProjectModelFields().get("t7p2.Bean2").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t7p2.Bean2"));
    }

    @Test
    public void testPackageNameWhiteList_IncludeOnePackage() throws Exception {
        final ProjectDataModelOracle oracle =
                initializeProjectDataModelOracle("/DataModelPackageWhiteListTest2");

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getProjectModelFields().size());
        assertContains("t8p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("t8p2.Bean2",
                       oracle.getProjectModelFields().keySet());

        assertEquals(1,
                     oracle.getProjectModelFields().get("t8p1.Bean1").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t8p1.Bean1"));

        assertEquals(1,
                     oracle.getProjectModelFields().get("t8p2.Bean2").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t8p2.Bean2"));
    }

    @Test
    public void testPackageNameWhiteList_IncludeAllPackages() throws Exception {
        final ProjectDataModelOracle oracle =
                initializeProjectDataModelOracle("/DataModelPackageWhiteListTest3");

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getProjectModelFields().size());
        assertContains("t9p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("t9p2.Bean2",
                       oracle.getProjectModelFields().keySet());

        assertEquals(1,
                     oracle.getProjectModelFields().get("t9p1.Bean1").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t9p1.Bean1"));

        assertEquals(1,
                     oracle.getProjectModelFields().get("t9p2.Bean2").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t9p2.Bean2"));
    }

    @Test
    public void testPackageNameWhiteList_NoWhiteList() throws Exception {
        final ProjectDataModelOracle oracle =
                initializeProjectDataModelOracle("/DataModelPackageWhiteListTest4");

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getProjectModelFields().size());
        assertContains("t10p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("t10p2.Bean2",
                       oracle.getProjectModelFields().keySet());

        assertEquals(1,
                     oracle.getProjectModelFields().get("t10p1.Bean1").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t10p1.Bean1"));

        assertEquals(1,
                     oracle.getProjectModelFields().get("t10p2.Bean2").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t10p2.Bean2"));
    }

    @Test
    public void testPackageNameWhiteList_Wildcards() throws Exception {
        final ProjectDataModelOracle oracle =
                initializeProjectDataModelOracle("/DataModelPackageWhiteListTest5");

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getProjectModelFields().size());
        assertContains("t11.p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("t11.p2.Bean2",
                       oracle.getProjectModelFields().keySet());

        assertEquals(1,
                     oracle.getProjectModelFields().get("t11.p1.Bean1").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t11.p1.Bean1"));

        assertEquals(1,
                     oracle.getProjectModelFields().get("t11.p2.Bean2").length);
        assertContains("this",
                       oracle.getProjectModelFields().get("t11.p2.Bean2"));
    }
}
