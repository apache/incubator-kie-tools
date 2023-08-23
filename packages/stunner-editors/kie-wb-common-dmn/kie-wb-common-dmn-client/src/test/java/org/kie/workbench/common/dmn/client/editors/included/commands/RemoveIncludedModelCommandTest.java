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

import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RemoveIncludedModelCommandTest {

    @Mock
    private DMNCardsGridComponent grid;

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    @Mock
    private Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    @Mock
    private ImportRecordEngine recordEngine;

    @Mock
    private BaseIncludedModelActiveRecord includedModel;

    private RemoveIncludedModelCommand command;

    @Before
    public void setup() {
        command = spy(new RemoveIncludedModelCommand(grid,
                                                     includedModel,
                                                     client,
                                                     refreshDecisionComponentsEvent,
                                                     recordEngine,
                                                     refreshDataTypesListEvent));
    }

    @Test
    public void testRefreshDecisionComponents() {

        command.refreshDecisionComponents();

        verify(refreshDecisionComponentsEvent).fire(any(RefreshDecisionComponents.class));
    }

    @Test
    public void testExecute() {

        doNothing().when(command).saveDeletedIncludedModelData();

        final CommandResult result = command.execute(mock(AbstractCanvasHandler.class));

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);
        verify(command).saveDeletedIncludedModelData();
        verify(includedModel).destroy();
        verify(grid).refresh();
        verify(command).refreshDecisionComponents();
    }

    @Test
    public void testSaveDeletedIncludedModelData() {

        final String name = "name";
        final String namespace = "namespace";
        final String importType = "import type";
        final String path = "path";

        when(includedModel.getName()).thenReturn(name);
        when(includedModel.getNamespace()).thenReturn(namespace);
        when(includedModel.getImportType()).thenReturn(importType);
        when(includedModel.getPath()).thenReturn(path);

        command.saveDeletedIncludedModelData();

        assertEquals(name, command.getName());
        assertEquals(namespace, command.getNamespace());
        assertEquals(importType, command.getImportType());
        assertEquals(path, command.getPath());
    }

    @Test
    public void testUndo() {

        final DefaultIncludedModelActiveRecord restoredModel = mock(DefaultIncludedModelActiveRecord.class);
        final InOrder inOrder = inOrder(restoredModel, command, grid);
        final String name = "name";
        final String namespace = "namespace";
        final String importType = "import type";
        final String path = "path";

        doReturn(restoredModel).when(command).restoreDeletedModel();
        doReturn(name).when(command).getName();
        doReturn(namespace).when(command).getNamespace();
        doReturn(importType).when(command).getImportType();
        doReturn(path).when(command).getPath();

        final CommandResult result = command.undo(mock(AbstractCanvasHandler.class));

        assertEquals(CanvasCommandResultBuilder.SUCCESS, result);

        inOrder.verify(command).setIncludedModel(restoredModel);
        inOrder.verify(restoredModel).setName(name);
        inOrder.verify(restoredModel).setNamespace(namespace);
        inOrder.verify(restoredModel).setImportType(importType);
        inOrder.verify(restoredModel).setPath(path);
        inOrder.verify(restoredModel).create();

        inOrder.verify(grid).refresh();
        inOrder.verify(command).refreshDecisionComponents();
        inOrder.verify(command).refreshDataTypesList(restoredModel);
    }

    @Test
    public void testRefreshDataTypesList() {

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

        final Consumer<List<ItemDefinition>> consumer = command.getItemDefinitionConsumer();
        final ArgumentCaptor<RefreshDataTypesListEvent> captor = ArgumentCaptor.forClass(RefreshDataTypesListEvent.class);
        final List<ItemDefinition> list = mock(List.class);

        consumer.accept(list);

        verify(refreshDataTypesListEvent).fire(captor.capture());

        final RefreshDataTypesListEvent parameter = captor.getValue();

        assertEquals(list, parameter.getNewItemDefinitions());
    }
}
