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
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsFactoryTest {

    @Mock
    private ImportRecordEngine recordEngine;

    @Mock
    private IncludedModelsIndex includedModelsIndex;

    private IncludedModelsFactory factory;

    @Before
    public void setup() {
        factory = spy(new IncludedModelsFactory(recordEngine, includedModelsIndex));
    }

    @Test
    public void testMakeIncludedDMNModels() {

        final ImportDMN import1 = mock(ImportDMN.class);
        final ImportDMN import2 = mock(ImportDMN.class);
        final Name nameMock1 = mock(Name.class);
        final Name nameMock2 = mock(Name.class);
        final List<Import> imports = asList(import1, import2);
        final String name1 = "name1";
        final String name2 = "name2";
        final String path1 = "path1";
        final String path2 = "path2";
        final String uuid1 = "123";
        final String uuid2 = "456";
        final String[] uuids = {uuid1, uuid2};
        final String uri1 = "/src/main/kie/dmn/1";
        final String uri2 = "/src/main/kie/dmn/2";
        final Integer drgElementsCount1 = 2;
        final Integer drgElementsCount2 = 8;
        final Integer itemDefinitionsCount1 = 4;
        final Integer itemDefinitionsCount2 = 16;

        when(nameMock1.getValue()).thenReturn(name1);
        when(nameMock2.getValue()).thenReturn(name2);
        when(import1.getName()).thenReturn(nameMock1);
        when(import2.getName()).thenReturn(nameMock2);
        when(import1.getNamespace()).thenReturn(path1);
        when(import2.getNamespace()).thenReturn(path2);
        when(import1.getImportType()).thenReturn(DMNImportTypes.DMN.getDefaultNamespace());
        when(import2.getImportType()).thenReturn(DMNImportTypes.DMN.getDefaultNamespace());
        when(import1.getLocationURI()).thenReturn(new LocationURI(uri1));
        when(import2.getLocationURI()).thenReturn(new LocationURI(uri2));
        when(import1.getDrgElementsCount()).thenReturn(drgElementsCount1);
        when(import2.getDrgElementsCount()).thenReturn(drgElementsCount2);
        when(import1.getItemDefinitionsCount()).thenReturn(itemDefinitionsCount1);
        when(import2.getItemDefinitionsCount()).thenReturn(itemDefinitionsCount2);
        doAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                return uuids[count++];
            }
        }).when(factory).uuidWrapper();

        final List<BaseIncludedModelActiveRecord> includedModels = factory.makeIncludedModels(imports);
        final BaseIncludedModelActiveRecord includedModel1 = includedModels.get(0);
        final BaseIncludedModelActiveRecord includedModel2 = includedModels.get(1);

        verify(includedModelsIndex).clear();
        verify(includedModelsIndex).index(includedModel1, import1);
        verify(includedModelsIndex).index(includedModel2, import2);
        verify(factory).setUuid(import1, includedModel1);
        verify(factory).setUuid(import2, includedModel2);
        assertEquals(2, includedModels.size());
        assertEquals(uuid1, includedModel1.getUUID());
        assertEquals(uuid2, includedModel2.getUUID());
        assertEquals(name1, includedModel1.getName());
        assertEquals(name2, includedModel2.getName());
        assertEquals(path1, includedModel1.getNamespace());
        assertEquals(path2, includedModel2.getNamespace());
        assertEquals(uri1, includedModel1.getPath());
        assertEquals(uri2, includedModel2.getPath());
        assertTrue(includedModel1 instanceof DMNIncludedModelActiveRecord);
        assertTrue(includedModel2 instanceof DMNIncludedModelActiveRecord);
        assertEquals(itemDefinitionsCount1, ((DMNIncludedModelActiveRecord) includedModel1).getDataTypesCount());
        assertEquals(itemDefinitionsCount2, ((DMNIncludedModelActiveRecord) includedModel2).getDataTypesCount());
        assertEquals(drgElementsCount1, ((DMNIncludedModelActiveRecord) includedModel1).getDrgElementsCount());
        assertEquals(drgElementsCount2, ((DMNIncludedModelActiveRecord) includedModel2).getDrgElementsCount());
        assertEquals(recordEngine, includedModel1.getRecordEngine());
        assertEquals(recordEngine, includedModel2.getRecordEngine());
    }

    @Test
    public void testMakeIncludedPMMLModels() {

        final ImportPMML import1 = mock(ImportPMML.class);
        final ImportPMML import2 = mock(ImportPMML.class);
        final Name nameMock1 = mock(Name.class);
        final Name nameMock2 = mock(Name.class);
        final List<Import> imports = asList(import1, import2);
        final String name1 = "name1";
        final String name2 = "name2";
        final String path1 = "path1";
        final String path2 = "path2";
        final String uuid1 = "123";
        final String uuid2 = "456";
        final String[] uuids = {uuid1, uuid2};
        final String uri1 = "/src/main/kie/dmn/1";
        final String uri2 = "/src/main/kie/dmn/2";
        final Integer modelCount1 = 2;
        final Integer modelCount2 = 8;

        when(nameMock1.getValue()).thenReturn(name1);
        when(nameMock2.getValue()).thenReturn(name2);
        when(import1.getName()).thenReturn(nameMock1);
        when(import2.getName()).thenReturn(nameMock2);
        when(import1.getNamespace()).thenReturn(path1);
        when(import2.getNamespace()).thenReturn(path2);
        when(import1.getImportType()).thenReturn(DMNImportTypes.PMML.getDefaultNamespace());
        when(import2.getImportType()).thenReturn(DMNImportTypes.PMML.getDefaultNamespace());
        when(import1.getLocationURI()).thenReturn(new LocationURI(uri1));
        when(import2.getLocationURI()).thenReturn(new LocationURI(uri2));
        when(import1.getModelCount()).thenReturn(modelCount1);
        when(import2.getModelCount()).thenReturn(modelCount2);
        doAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                return uuids[count++];
            }
        }).when(factory).uuidWrapper();

        final List<BaseIncludedModelActiveRecord> includedModels = factory.makeIncludedModels(imports);
        final BaseIncludedModelActiveRecord includedModel1 = includedModels.get(0);
        final BaseIncludedModelActiveRecord includedModel2 = includedModels.get(1);

        verify(includedModelsIndex).clear();
        verify(includedModelsIndex).index(includedModel1, import1);
        verify(includedModelsIndex).index(includedModel2, import2);
        verify(factory).setUuid(import1, includedModel1);
        verify(factory).setUuid(import2, includedModel2);
        assertEquals(2, includedModels.size());
        assertEquals(uuid1, includedModel1.getUUID());
        assertEquals(uuid2, includedModel2.getUUID());
        assertEquals(name1, includedModel1.getName());
        assertEquals(name2, includedModel2.getName());
        assertEquals(path1, includedModel1.getNamespace());
        assertEquals(path2, includedModel2.getNamespace());
        assertEquals(uri1, includedModel1.getPath());
        assertEquals(uri2, includedModel2.getPath());
        assertTrue(includedModel1 instanceof PMMLIncludedModelActiveRecord);
        assertTrue(includedModel2 instanceof PMMLIncludedModelActiveRecord);
        assertEquals(modelCount1, ((PMMLIncludedModelActiveRecord) includedModel1).getModelCount());
        assertEquals(modelCount2, ((PMMLIncludedModelActiveRecord) includedModel2).getModelCount());
        assertEquals(recordEngine, includedModel1.getRecordEngine());
        assertEquals(recordEngine, includedModel2.getRecordEngine());
    }

    @Test
    public void testSetUuid() {

        final Import anImport = mock(Import.class);
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String theUuid = "the uuid";

        when(factory.uuidWrapper()).thenReturn(theUuid);

        factory.setUuid(anImport, includedModel);

        verify(includedModel).setUuid(theUuid);
    }

    @Test
    public void testSetUuidWhenUuidIsSetInTheImport() {

        final Import anImport = mock(Import.class);
        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String theUuid = "the uuid";

        when(anImport.getUuid()).thenReturn(theUuid);

        factory.setUuid(anImport, includedModel);

        verify(includedModel).setUuid(theUuid);
        verify(factory, never()).uuidWrapper();
    }
}
