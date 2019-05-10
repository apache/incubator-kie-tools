/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
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
    public void testMakeImport() {

        final IncludedModel record = new IncludedModel(null);
        final String expectedImportType = ImportFactory.IMPORT_TYPE;
        final String nameValue = "name";
        final String path = "/src/main/kie/dmn";
        final Name expectedName = new Name(nameValue);
        final LocationURI expectedLocationURI = new LocationURI(path);
        final String expectedNamespace = "://namespace";
        final int expectedDrgElementsCount = 2;
        final int expectedItemDefinitionsCount = 3;

        record.setName(nameValue);
        record.setPath(path);
        record.setNamespace(expectedNamespace);
        record.setDrgElementsCount(expectedDrgElementsCount);
        record.setDataTypesCount(expectedItemDefinitionsCount);

        final Import actualImport = factory.makeImport(record);

        assertEquals(expectedImportType, actualImport.getImportType());
        assertEquals(expectedName, actualImport.getName());
        assertEquals(expectedLocationURI, actualImport.getLocationURI());
        assertEquals(expectedNamespace, actualImport.getNamespace());
        assertEquals(expectedDrgElementsCount, actualImport.getDrgElementsCount());
        assertEquals(expectedItemDefinitionsCount, actualImport.getItemDefinitionsCount());
    }

    @Test
    public void testName() {

        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Import import3 = mock(Import.class);
        final List<Import> imports = asList(import1, import2, import3);
        final IncludedModel record = new IncludedModel(null);

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
        final IncludedModel record = new IncludedModel(null);

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
