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

import java.net.URISyntaxException;

import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.services.datamodel.backend.server.ModuleDataModelOracleTestUtils.assertContains;

/**
 * Tests for DataModelService
 */
public class ModuleDataModelRecursionTest extends AbstractDataModelWeldTest {

    @Test
    public void testModuleDataModelOracle() throws URISyntaxException {
        final ModuleDataModelOracle oracle =
                initializeModuleDataModelOracle("/DataModelBackendRecursionTest1/src/main/java/t6p1");

        assertNotNull(oracle);

        assertEquals(1,
                     oracle.getModuleModelFields().size());
        assertContains("t6p1.Bean1",
                       oracle.getModuleModelFields().keySet());

        assertFalse(oracle.getModuleEventTypes().get("t6p1.Bean1"));

        assertEquals(2,
                     oracle.getModuleModelFields().get("t6p1.Bean1").length);
        assertContains("this",
                       oracle.getModuleModelFields().get("t6p1.Bean1"));
        assertContains("field1",
                       oracle.getModuleModelFields().get("t6p1.Bean1"));
    }
}
