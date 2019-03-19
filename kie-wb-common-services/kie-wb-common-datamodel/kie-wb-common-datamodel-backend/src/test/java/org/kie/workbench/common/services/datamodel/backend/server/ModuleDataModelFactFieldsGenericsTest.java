/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.Product;
import org.kie.workbench.common.services.datamodel.backend.server.testclasses.ProductOrder;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ModuleDataModelOracleTestUtils.assertContains;

public class ModuleDataModelFactFieldsGenericsTest {

    @Test
    public void testModuleDMOGenericParameters() throws Exception {
        final ModuleDataModelOracleBuilder builder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl oracle = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(builder, ProductOrder.class, false, type -> TypeSource.JAVA_PROJECT);
        cb.build(oracle);

        assertEquals(1, oracle.getModuleModelFields().size());
        assertContains(ProductOrder.class.getName(), oracle.getModuleModelFields().keySet());

        assertEquals(2, oracle.getModuleFieldParametersType().size());

        String genericType = oracle.getModuleFieldParametersType().get(ProductOrder.class.getName() + "#products");
        assertNotNull(genericType);
        assertEquals(Product.class.getName(), genericType);

        genericType = oracle.getModuleFieldParametersType().get(ProductOrder.class.getName() + "#productComparator");
        assertNotNull(genericType);
        assertEquals(Product.class.getName(), genericType);

        assertNotNull(cb.getInternalBuilders().get(Product.class.getName()));
    }
}
