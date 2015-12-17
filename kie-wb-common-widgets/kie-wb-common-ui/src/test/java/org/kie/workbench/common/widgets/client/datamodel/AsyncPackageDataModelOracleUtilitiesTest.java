/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.junit.Test;

import static org.junit.Assert.*;

public class AsyncPackageDataModelOracleUtilitiesTest {

    @Test
    public void testFilterModelFieldsFactsShareNameInDifferentPackagesAnotherOneIsInCurrentPackage() throws Exception {
        HashMap<String, ModelField[]> projectModelFields = new HashMap<String, ModelField[]>();
        projectModelFields.put("org.test.Person", new ModelField[]{getModelField("Person", "org.test.Person")});
        projectModelFields.put("org.test.sub.Person", new ModelField[]{getModelField("Person", "org.test.sub.Person")});
        projectModelFields.put("org.test.sub.Address", new ModelField[]{getModelField("Address", "org.test.sub.Address")});
        projectModelFields.put("org.test.Address", new ModelField[]{getModelField("Address", "org.test.Address")});

        FactNameToFQCNHandleRegistry registry = new FactNameToFQCNHandleRegistry();

        AsyncPackageDataModelOracleUtilities.filterModelFields(
                "org.test.sub",
                new Imports(),
                projectModelFields,
                registry);

        assertEquals("org.test.sub.Person", registry.get("Person"));
        assertEquals("org.test.sub.Address", registry.get("Address"));
    }

    @Test
    public void testFilterModelFieldsFactsShareNameInDifferentPackagesAnotherOneIsImported() throws Exception {
        HashMap<String, ModelField[]> projectModelFields = new HashMap<String, ModelField[]>();
        projectModelFields.put("org.test.Person", new ModelField[]{getModelField("Person", "org.test.Person")});
        projectModelFields.put("org.test.sub.Person", new ModelField[]{getModelField("Person", "org.test.sub.Person")});
        projectModelFields.put("org.test.sub.Address", new ModelField[]{getModelField("Address", "org.test.sub.Address")});
        projectModelFields.put("org.test.Address", new ModelField[]{getModelField("Address", "org.test.Address")});

        FactNameToFQCNHandleRegistry registry = new FactNameToFQCNHandleRegistry();

        Imports imports = new Imports();
        imports.addImport(new Import("org.test.sub.Person"));
        imports.addImport(new Import("org.test.sub.Address"));

        AsyncPackageDataModelOracleUtilities.filterModelFields(
                "org.another",
                imports,
                projectModelFields,
                registry);

        assertEquals("org.test.sub.Person", registry.get("Person"));
        assertEquals("org.test.sub.Address", registry.get("Address"));
    }

    private ModelField getModelField(String type, String className) {
        return new ModelField("field", className, ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.DELEGATED, FieldAccessorsAndMutators.BOTH, type);
    }

    // check imports

}
