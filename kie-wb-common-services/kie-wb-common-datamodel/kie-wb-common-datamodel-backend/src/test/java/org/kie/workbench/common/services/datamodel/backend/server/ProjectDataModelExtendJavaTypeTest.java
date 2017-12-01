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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.assertContains;

/**
 * Tests for DataModelService
 */
public class ProjectDataModelExtendJavaTypeTest extends AbstractDataModelWeldTest {

    @Test
    public void testProjectExtendJavaTypeWithQualifiedDRLBeanName() throws Exception {
        final ProjectDataModelOracle oracle =
                initializeProjectDataModelOracle("/DataModelBackendExtendJavaTypeTest1");

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getProjectModelFields().size());
        assertContains("t4p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getProjectModelFields().keySet());

        assertTrue(oracle.getProjectEventTypes().get("t4p1.Bean1"));
        assertFalse(oracle.getProjectEventTypes().get("java.lang.String"));
    }

    @Test
    public void testProjectExtendJavaTypeWithImport() throws Exception {
        final ProjectDataModelOracle oracle =
                initializeProjectDataModelOracle("/DataModelBackendExtendJavaTypeTest2");

        assertNotNull(oracle);

        assertEquals(2,
                     oracle.getProjectModelFields().size());
        assertContains("t5p1.Bean1",
                       oracle.getProjectModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getProjectModelFields().keySet());

        assertTrue(oracle.getProjectEventTypes().get("t5p1.Bean1"));
        assertFalse(oracle.getProjectEventTypes().get("java.lang.String"));
    }
}
