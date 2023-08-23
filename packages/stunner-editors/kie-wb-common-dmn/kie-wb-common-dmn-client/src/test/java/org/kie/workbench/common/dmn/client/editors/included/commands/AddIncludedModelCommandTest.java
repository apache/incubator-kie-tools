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

package org.kie.workbench.common.dmn.client.editors.included.commands;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.DRG_ELEMENT_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.IMPORT_TYPE_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.ITEM_DEFINITION_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PATH_METADATA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AddIncludedModelCommandTest {

    @Mock
    private Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    @Mock
    private Event<RefreshDecisionComponents> refreshPMMLComponentsEvent;

    @Mock
    private KieAssetsDropdownItem value;

    @Mock
    private Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    @Mock
    private ImportRecordEngine recordEngine;

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private IncludedModelsPagePresenter presenter;

    private final String modelName = "model name";

    private AddIncludedModelCommand command;

    @Before
    public void setup() {

        command = spy(new AddIncludedModelCommand(value,
                                                  presenter,
                                                  refreshDecisionComponentsEvent,
                                                  refreshPMMLComponentsEvent,
                                                  refreshDataTypesListEvent,
                                                  recordEngine,
                                                  client,
                                                  modelName));
    }

    @Test
    public void testExecute() {

        final BaseIncludedModelActiveRecord created = mock(BaseIncludedModelActiveRecord.class);
        doReturn(created).when(command).createIncludedModel(value);
        doNothing().when(command).refreshPresenter();
        doNothing().when(command).refreshDecisionComponents(isA(DMNImportTypes.class));
        doNothing().when(command).refreshDataTypesList(created);

        final CommandResult<CanvasViolation> result = command.execute(mock(AbstractCanvasHandler.class));

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);

        verify(command).createIncludedModel(value);
        verify(command).refreshPresenter();
        verify(command).refreshDecisionComponents(isA(DMNImportTypes.class));
        verify(command).refreshDataTypesList(created);
    }

    @Test
    public void testUndo() {

        final BaseIncludedModelActiveRecord created = mock(BaseIncludedModelActiveRecord.class);
        doReturn(created).when(command).getCreated();
        doNothing().when(command).refreshPresenter();
        doNothing().when(command).refreshDecisionComponents(isA(DMNImportTypes.class));

        final CommandResult<CanvasViolation> result = command.undo(mock(AbstractCanvasHandler.class));

        verify(created).destroy();
        verify(command).refreshPresenter();
        verify(command).refreshDecisionComponents(isA(DMNImportTypes.class));
        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
    }

    @Test
    public void testRefreshDataTypesList() {

        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String name = "name";
        final String namespace = "namespace";
        final Consumer consumer = mock(Consumer.class);

        when(includedModel.getName()).thenReturn(name);
        when(includedModel.getNamespace()).thenReturn(namespace);
        doReturn(consumer).when(command).getItemDefinitionConsumer();

        command.refreshDataTypesList(includedModel);

        verify(client).loadItemDefinitionsByNamespace(name,
                                                      namespace,
                                                      consumer);
    }

    @Test
    public void testGetItemDefinitionConsumer() {

        final ArgumentCaptor<RefreshDataTypesListEvent> captor = ArgumentCaptor.forClass(RefreshDataTypesListEvent.class);
        final Consumer<List<ItemDefinition>> consumer = command.getItemDefinitionConsumer();
        final List<ItemDefinition> list = mock(List.class);

        consumer.accept(list);

        verify(refreshDataTypesListEvent).fire(captor.capture());

        final RefreshDataTypesListEvent parameter = captor.getValue();

        assertEquals(list, parameter.getNewItemDefinitions());
    }

    @Test
    public void testCreateIncludedModel() {
        final String value = "://namespace";
        final String path = "/src/path/file";
        final String anPackage = "path.file.com";
        final Integer expectedDrgElementsCount = 2;
        final Integer expectedDataTypesCount = 3;
        final Map<String, String> metaData = Stream.of(new AbstractMap.SimpleEntry<>(PATH_METADATA, path),
                                                       new AbstractMap.SimpleEntry<>(IMPORT_TYPE_METADATA, DMNImportTypes.DMN.getDefaultNamespace()),
                                                       new AbstractMap.SimpleEntry<>(DRG_ELEMENT_COUNT_METADATA, expectedDrgElementsCount.toString()),
                                                       new AbstractMap.SimpleEntry<>(ITEM_DEFINITION_COUNT_METADATA, expectedDataTypesCount.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final BaseIncludedModelActiveRecord includedModel = command.createIncludedModel(new KieAssetsDropdownItem(modelName, anPackage, value, metaData));
        assertTrue(includedModel instanceof DMNIncludedModelActiveRecord);

        final DMNIncludedModelActiveRecord dmnIncludedModel = (DMNIncludedModelActiveRecord) includedModel;

        assertEquals(modelName, dmnIncludedModel.getName());
        assertEquals(value, dmnIncludedModel.getNamespace());
        assertEquals(path, dmnIncludedModel.getPath());
        assertEquals(expectedDrgElementsCount, dmnIncludedModel.getDrgElementsCount());
        assertEquals(expectedDataTypesCount, dmnIncludedModel.getDataTypesCount());
    }

    @Test
    public void testRefreshDecisionComponents() {
        command.refreshDecisionComponents(DMNImportTypes.DMN);

        verify(refreshDecisionComponentsEvent).fire(any(RefreshDecisionComponents.class));
    }

    @Test
    public void testRefreshPMMLComponents() {
        command.refreshDecisionComponents(DMNImportTypes.PMML);

        verify(refreshPMMLComponentsEvent).fire(any(RefreshDecisionComponents.class));
    }

    @Test
    public void testRefreshPresenter() {

        command.refreshPresenter();

        verify(presenter).refresh();
    }
}
