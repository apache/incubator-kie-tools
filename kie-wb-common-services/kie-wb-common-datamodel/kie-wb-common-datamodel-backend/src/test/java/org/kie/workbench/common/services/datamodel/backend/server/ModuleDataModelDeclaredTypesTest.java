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
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.services.datamodel.backend.server.ModuleDataModelOracleTestUtils.assertContains;

/**
 * Tests for DataModelService
 */
public class ModuleDataModelDeclaredTypesTest extends AbstractDataModelWeldTest {

    @Test
    public void testProjectDeclaredTypes() throws Exception {
        final ModuleDataModelOracle oracle =
                initializeModuleDataModelOracle("/DataModelBackendDeclaredTypesTest1/src/main/java/t1p1");

        assertNotNull(oracle);

        assertEquals(4,
                     oracle.getModuleModelFields().size());
        assertContains("t1p1.Bean1",
                       oracle.getModuleModelFields().keySet());
        assertContains("t1p1.DRLBean",
                       oracle.getModuleModelFields().keySet());
        assertContains("t1p2.Bean2",
                       oracle.getModuleModelFields().keySet());
        assertContains("java.lang.String",
                       oracle.getModuleModelFields().keySet());

        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getModuleTypeSources().get("t1p1.Bean1"));
        assertEquals(TypeSource.DECLARED,
                     oracle.getModuleTypeSources().get("t1p1.DRLBean"));
        assertEquals(TypeSource.JAVA_PROJECT,
                     oracle.getModuleTypeSources().get("t1p2.Bean2"));
        assertEquals(TypeSource.JAVA_DEPENDENCY,
                     oracle.getModuleTypeSources().get("java.lang.String"));
    }
}
