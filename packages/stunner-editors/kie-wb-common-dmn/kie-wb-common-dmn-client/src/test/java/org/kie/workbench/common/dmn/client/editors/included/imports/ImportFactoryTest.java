/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImportFactoryTest {

    @Mock
    private IncludedModelsIndex modelsIndex;

    private ImportFactory factory;

    @Before
    public void setup() {
        factory = new ImportFactory(modelsIndex);
    }

    @Test
    public void testMakeDMNImport() {

        final DMNIncludedModelActiveRecord record = new DMNIncludedModelActiveRecord(null);
        final String expectedImportType = DMNImportTypes.DMN.getDefaultNamespace();
        final String nameValue = "name";
        final String path = "/src/main/kie/dmn";
        final Name expectedName = new Name(nameValue);
        final LocationURI expectedLocationURI = new LocationURI(path);
        final String expectedNamespace = "://namespace";
        final int expectedDrgElementsCount = 2;
        final int expectedItemDefinitionsCount = 3;
        final String uuid = "uuid";

        record.setName(nameValue);
        record.setPath(path);
        record.setNamespace(expectedNamespace);
        record.setImportType(DMNImportTypes.DMN.getDefaultNamespace());
        record.setDrgElementsCount(expectedDrgElementsCount);
        record.setDataTypesCount(expectedItemDefinitionsCount);
        record.setUuid(uuid);

        final Import actualImport = factory.makeImport(record);
        assertTrue(actualImport instanceof ImportDMN);

        final ImportDMN dmnImport = (ImportDMN) actualImport;
        assertEquals(expectedImportType, actualImport.getImportType());
        assertEquals(expectedName, actualImport.getName());
        assertEquals(expectedLocationURI, actualImport.getLocationURI());
        assertEquals(expectedNamespace, actualImport.getNamespace());
        assertEquals(expectedImportType, actualImport.getImportType());
        assertEquals(expectedDrgElementsCount, dmnImport.getDrgElementsCount());
        assertEquals(expectedItemDefinitionsCount, dmnImport.getItemDefinitionsCount());
        assertEquals(uuid, dmnImport.getUuid());
    }

    @Test
    public void testMakePMMLImport() {

        final PMMLIncludedModelActiveRecord record = new PMMLIncludedModelActiveRecord(null);
        final String expectedImportType = DMNImportTypes.PMML.getDefaultNamespace();
        final String expectedNameValue = "name";
        final String path = "/src/main/kie/pmml";
        final Name expectedName = new Name(expectedNameValue);
        final LocationURI expectedLocationURI = new LocationURI(path);
        final int expectedModelCount = 2;
        final String uuid = "uuid";

        record.setPath(path);
        record.setName(expectedNameValue);
        record.setNamespace(expectedNameValue);
        record.setImportType(DMNImportTypes.PMML.getDefaultNamespace());
        record.setModelCount(expectedModelCount);
        record.setUuid(uuid);

        final Import actualImport = factory.makeImport(record);
        assertTrue(actualImport instanceof ImportPMML);

        final ImportPMML pmmlImport = (ImportPMML) actualImport;
        assertEquals(expectedImportType, actualImport.getImportType());
        assertEquals(expectedName, actualImport.getName());
        assertEquals(expectedLocationURI, actualImport.getLocationURI());
        assertEquals(expectedNameValue, actualImport.getNamespace());
        assertEquals(expectedImportType, actualImport.getImportType());
        assertEquals(expectedModelCount, pmmlImport.getModelCount());
        assertEquals(uuid, pmmlImport.getUuid());
    }

    @Test
    public void testName() {

        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Import import3 = mock(Import.class);
        final List<Import> imports = asList(import1, import2, import3);
        final DMNIncludedModelActiveRecord record = new DMNIncludedModelActiveRecord(null);

        when(import1.getName()).thenReturn(new Name("foo"));
        when(import2.getName()).thenReturn(new Name("bar"));
        when(import3.getName()).thenReturn(new Name("foo bar"));
        when(modelsIndex.getIndexedImports()).thenReturn(imports);

        record.setName("bla");

        final Name name = factory.name(record);
        final String expected = "bla";
        final String actual = name.getValue();

        assertEquals(expected, actual);
    }

    @Test
    public void testNameWithExistingName() {

        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Import import3 = mock(Import.class);
        final List<Import> imports = asList(import1, import2, import3);
        final DMNIncludedModelActiveRecord record = new DMNIncludedModelActiveRecord(null);

        when(import1.getName()).thenReturn(new Name("foo"));
        when(import2.getName()).thenReturn(new Name("bar"));
        when(import3.getName()).thenReturn(new Name("foo bar"));
        when(modelsIndex.getIndexedImports()).thenReturn(imports);

        record.setName("foo");

        final Name name = factory.name(record);
        final String expected = "foo - 2";
        final String actual = name.getValue();

        assertEquals(expected, actual);
    }
}
