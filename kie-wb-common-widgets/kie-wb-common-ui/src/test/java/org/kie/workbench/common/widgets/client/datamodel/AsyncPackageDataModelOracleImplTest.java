/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.client.datamodel;

import java.util.HashMap;

import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsyncPackageDataModelOracleImplTest {

    @Test
    public void testName() throws Exception {
        AsyncPackageDataModelOracleImpl oracle = new AsyncPackageDataModelOracleImpl();

        oracle.setPackageName("org.test");

        oracle.projectModelFields = new HashMap<String, ModelField[]>();
        oracle.projectModelFields.put(
                "org.test.Person",
                new ModelField[]{
                        new ModelField("this", "org.test.Person", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.SELF, FieldAccessorsAndMutators.ACCESSOR, "this"),
                        new ModelField("address", "org.test.Address", ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.DECLARED, FieldAccessorsAndMutators.BOTH, "org.test.Address")
                }
        );

        oracle.filter();

        assertEquals("org.test.Person", oracle.getFQCNByFactName("Person"));
        assertEquals("Person", oracle.getFieldClassName("Person", "this"));

        assertEquals("Address", oracle.getFieldClassName("Person", "address"));
    }
}
