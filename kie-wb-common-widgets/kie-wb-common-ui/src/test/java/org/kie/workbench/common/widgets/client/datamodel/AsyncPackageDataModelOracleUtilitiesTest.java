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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;

import static org.junit.Assert.*;
import static org.kie.workbench.common.widgets.client.datamodel.PackageDataModelOracleTestUtils.*;

public class AsyncPackageDataModelOracleUtilitiesTest {

    @Test
    public void testFilterModelFieldsFactsShareNameInDifferentPackagesAnotherOneIsInCurrentPackage() throws Exception {
        Map<String, ModelField[]> projectModelFields = new TreeMap<String, ModelField[]>();
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
        Map<String, ModelField[]> projectModelFields = new TreeMap<String, ModelField[]>();
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

    @Test
    public void testFilterSuperTypes() {
        Map<String, List<String>> projectSuperTypes = new HashMap<String, List<String>>();
        projectSuperTypes.put("org.test.Person", Arrays.asList(new String[]{"org.test.GrandParent", "org.test.Parent"}));
        projectSuperTypes.put("org.test.sub.Person", Arrays.asList(new String[]{"org.test.sub.GrandParent", "org.test.sub.Parent"}));
        projectSuperTypes.put("org.test.sub.Address", Arrays.asList(new String[]{"org.test.sub.Location"}));
        projectSuperTypes.put("org.test.Address", Arrays.asList(new String[]{"org.test.Location"}));

        Imports imports = new Imports();
        imports.addImport(new Import("org.test.sub.Person"));
        imports.addImport(new Import("org.test.sub.Address"));

        Map<String, List<String>> filterSuperTypes = AsyncPackageDataModelOracleUtilities.filterSuperTypes(
                "org.another",
                imports,
                projectSuperTypes);

        assertEquals(2, filterSuperTypes.size());
        assertContains("Person", filterSuperTypes.keySet());
        assertContains("Address", filterSuperTypes.keySet());

        final List<String> personSuperTypes = filterSuperTypes.get("Person");
        assertEquals(2, personSuperTypes.size());
        assertEquals("org.test.sub.GrandParent", personSuperTypes.get(0));
        assertEquals("org.test.sub.Parent", personSuperTypes.get(1));

        final List<String> addressSuperTypes = filterSuperTypes.get("Address");
        assertEquals(1, addressSuperTypes.size());
        assertEquals("org.test.sub.Location", addressSuperTypes.get(0));
    }

    private ModelField getModelField(String type,
                                     String className) {
        return new ModelField("field", className, ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS, ModelField.FIELD_ORIGIN.DELEGATED, FieldAccessorsAndMutators.BOTH, type);
    }

    // check imports
}
