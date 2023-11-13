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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.imports.ImportFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.messages.IncludedModelErrorMessageFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.stubs.ManagedInstanceStub;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImportRecordEngineTest {

    @Mock
    private IncludedModelsPageStateProviderImpl stateProvider;

    @Mock
    private IncludedModelsIndex includedModelsIndex;

    @Mock
    private IncludedModelErrorMessageFactory messageFactory;

    @Mock
    private ImportFactory importFactory;

    @Mock
    private EventSourceMock<FlashMessage> flashMessageEvent;

    @Mock
    private DefinitionsHandler definitionsHandler;

    @Mock
    private ItemDefinitionHandler itemDefinitionHandler;

    @Mock
    private DMNIncludedModelHandler dmnIncludedModelHandler;

    @Mock
    private PMMLIncludedModelHandler pmmlIncludedModelHandler;

    private ManagedInstanceStub<DRGElementHandler> drgElementHandlers;

    private ImportRecordEngine recordEngine;

    @Before
    public void setup() {
        drgElementHandlers = new ManagedInstanceStub<>(itemDefinitionHandler, dmnIncludedModelHandler, pmmlIncludedModelHandler);
        recordEngine = spy(new ImportRecordEngine(stateProvider,
                                                  includedModelsIndex,
                                                  messageFactory,
                                                  importFactory,
                                                  flashMessageEvent,
                                                  definitionsHandler,
                                                  drgElementHandlers));
    }

    @Test
    public void testUpdateWhenIncludedModelIsValid() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final ArgumentCaptor<Name> nameCaptor = ArgumentCaptor.forClass(Name.class);
        final Import anImport = mock(Import.class);
        final String name = "name";
        final String oldName = "oldName";

        when(record.getName()).thenReturn(name);
        when(record.isValid()).thenReturn(true);
        when(includedModelsIndex.getImport(record)).thenReturn(anImport);
        when(anImport.getName()).thenReturn(new Name(oldName));

        final List<BaseIncludedModelActiveRecord> actualResult = recordEngine.update(record);
        final List<BaseIncludedModelActiveRecord> expectedResult = singletonList(record);

        verify(anImport).setName(nameCaptor.capture());
        verify(itemDefinitionHandler).update(oldName, name);
        verify(dmnIncludedModelHandler).update(oldName, name);
        verify(pmmlIncludedModelHandler).update(oldName, name);

        final Name actualName = nameCaptor.getValue();
        final Name expectedName = new Name(name);

        assertEquals(expectedName, actualName);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testUpdateWhenIncludedModelIsNotValid() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final Import anImport = mock(Import.class);

        when(record.isValid()).thenReturn(false);
        when(includedModelsIndex.getImport(record)).thenReturn(anImport);

        assertThatThrownBy(() -> recordEngine.update(record))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("An invalid Included Model cannot be updated.");
    }

    @Test
    public void testDestroy() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final List<Import> expectedImports = singletonList(import1);
        final List<Import> actualImports = new ArrayList<>(asList(import1, import2));
        final String name = "name";

        when(record.getName()).thenReturn(name);
        when(includedModelsIndex.getImport(record)).thenReturn(import2);
        when(stateProvider.getImports()).thenReturn(actualImports);

        final List<BaseIncludedModelActiveRecord> actualResult = recordEngine.destroy(record);
        final List<BaseIncludedModelActiveRecord> expectedResult = singletonList(record);

        verify(definitionsHandler).destroy(record);
        verify(itemDefinitionHandler).destroy(name);
        verify(dmnIncludedModelHandler).destroy(name);
        verify(pmmlIncludedModelHandler).destroy(name);
        assertEquals(expectedImports, actualImports);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testIsValidWhenNameIsUnique() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Name name1 = mock(Name.class);
        final Name name2 = mock(Name.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);
        final List<Import> imports = new ArrayList<>(asList(import1, import2));

        when(name1.getValue()).thenReturn("file1");
        when(name2.getValue()).thenReturn("file2");
        when(record.getName()).thenReturn("file-new");
        when(import1.getName()).thenReturn(name1);
        when(import2.getName()).thenReturn(name2);
        when(includedModelsIndex.getImport(record)).thenReturn(import2);
        when(stateProvider.getImports()).thenReturn(imports);
        when(messageFactory.getNameIsNotUniqueFlashMessage(record)).thenReturn(flashMessage);

        final boolean valid = recordEngine.isValid(record);

        assertTrue(valid);
        verifyZeroInteractions(flashMessageEvent);
    }

    @Test
    public void testIsValidWhenNameIsNotUnique() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Name name1 = mock(Name.class);
        final Name name2 = mock(Name.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);
        final List<Import> imports = new ArrayList<>(asList(import1, import2));

        when(name1.getValue()).thenReturn("file1");
        when(name2.getValue()).thenReturn("file2");
        when(record.getName()).thenReturn("file1");
        when(import1.getName()).thenReturn(name1);
        when(import2.getName()).thenReturn(name2);
        when(includedModelsIndex.getImport(record)).thenReturn(import2);
        when(stateProvider.getImports()).thenReturn(imports);
        when(messageFactory.getNameIsNotUniqueFlashMessage(record)).thenReturn(flashMessage);

        final boolean valid = recordEngine.isValid(record);

        assertFalse(valid);
        verify(flashMessageEvent).fire(flashMessage);
    }

    @Test
    public void testIsValidWhenNameIsBlank() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final Import anImport = mock(Import.class);
        final Name name = mock(Name.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);
        final List<Import> imports = new ArrayList<>(singletonList(anImport));

        when(name.getValue()).thenReturn("file");
        when(record.getName()).thenReturn("");
        when(anImport.getName()).thenReturn(name);
        when(includedModelsIndex.getImport(record)).thenReturn(anImport);
        when(stateProvider.getImports()).thenReturn(imports);
        when(messageFactory.getNameIsBlankFlashMessage(record)).thenReturn(flashMessage);

        final boolean valid = recordEngine.isValid(record);

        assertFalse(valid);
        verify(flashMessageEvent).fire(flashMessage);
    }

    @Test
    public void testIsValidWhenNameIsUnchanged() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Name name1 = mock(Name.class);
        final Name name2 = mock(Name.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);
        final List<Import> imports = new ArrayList<>(asList(import1, import2));

        when(name1.getValue()).thenReturn("file1");
        when(name2.getValue()).thenReturn("file2");
        when(record.getName()).thenReturn("file2");
        when(import1.getName()).thenReturn(name1);
        when(import2.getName()).thenReturn(name2);
        when(includedModelsIndex.getImport(record)).thenReturn(import2);
        when(stateProvider.getImports()).thenReturn(imports);
        when(messageFactory.getNameIsNotUniqueFlashMessage(record)).thenReturn(flashMessage);

        final boolean valid = recordEngine.isValid(record);

        assertTrue(valid);
        verifyZeroInteractions(flashMessageEvent);
    }

    @Test
    public void testCreate() {

        final BaseIncludedModelActiveRecord record = mock(BaseIncludedModelActiveRecord.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Import import3 = mock(Import.class);
        final List<Import> expectedImports = asList(import1, import2, import3);
        final List<Import> actualImports = new ArrayList<>(asList(import1, import2));

        when(importFactory.makeImport(record)).thenReturn(import3);
        when(stateProvider.getImports()).thenReturn(actualImports);

        final List<BaseIncludedModelActiveRecord> actualResult = recordEngine.create(record);
        final List<BaseIncludedModelActiveRecord> expectedResult = singletonList(record);

        verify(definitionsHandler).create(record);
        assertEquals(expectedImports, actualImports);
        assertEquals(expectedResult, actualResult);
    }
}
