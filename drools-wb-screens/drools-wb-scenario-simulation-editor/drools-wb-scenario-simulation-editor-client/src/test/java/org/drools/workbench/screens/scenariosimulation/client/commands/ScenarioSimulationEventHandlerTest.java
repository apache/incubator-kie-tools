/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AbstractScenarioSimulationCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AppendColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AppendRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DeleteColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DeleteRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DisableTestToolsCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DuplicateInstanceCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DuplicateRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.EnableTestToolsCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.InsertColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.InsertRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.PrependColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.PrependRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.ReloadTestToolsCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.RunSingleScenarioCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetGridCellValueCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetHeaderCellValueCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetInstanceHeaderCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetPropertyHeaderCommand;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateInstanceEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ImportEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RunSingleScenarioEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetGridCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetHeaderCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UnsupportedDMNEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.RedoEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ReloadTestToolsEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.RunSingleScenarioEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetGridCellValueEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetHeaderCellValueEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.UndoEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.UnsupportedDMNEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.FileUploadPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PACKAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationEventHandlerTest extends AbstractScenarioSimulationTest {

    @Mock
    private List<HandlerRegistration> handlerRegistrationListMock;
    @Mock
    private HandlerRegistration appendColumnHandlerRegistrationMock;
    @Mock
    private HandlerRegistration appendRowHandlerRegistrationMock;
    @Mock
    private HandlerRegistration deleteColumnHandlerRegistrationMock;
    @Mock
    private HandlerRegistration deleteRowHandlerRegistrationMock;
    @Mock
    private HandlerRegistration disableTestToolsEventHandlerMock;
    @Mock
    private HandlerRegistration duplicateColumnHandlerRegistrationMock;
    @Mock
    private HandlerRegistration duplicateHandlerRegistrationMock;
    @Mock
    private HandlerRegistration enableTestToolsEventHandlerMock;
    @Mock
    private HandlerRegistration importHandlerRegistrationMock;
    @Mock
    private HandlerRegistration insertColumnHandlerRegistrationMock;
    @Mock
    private HandlerRegistration insertRowHandlerRegistrationMock;
    @Mock
    private HandlerRegistration prependColumnHandlerRegistrationMock;
    @Mock
    private HandlerRegistration prependRowHandlerRegistrationMock;
    @Mock
    private HandlerRegistration redoEventHandlerRegistrationMock;
    @Mock
    private HandlerRegistration reloadTestToolsHandlerRegistrationMock;
    @Mock
    private HandlerRegistration runSingleScenarioHandlerRegistrationMock;
    @Mock
    private HandlerRegistration scenarioGridReloadHandlerRegistrationMock;
    @Mock
    private HandlerRegistration setGridCellValueEventHandlerMock;
    @Mock
    private HandlerRegistration setHeaderCellValueEventHandlerMock;
    @Mock
    private HandlerRegistration setInstanceHeaderEventHandlerMock;
    @Mock
    private HandlerRegistration setPropertyHeaderEventHandlerMock;
    @Mock
    private HandlerRegistration undoEventHandlerRegistrationMock;
    @Mock
    private HandlerRegistration unsupportedDMNEventHandlerRegistrationMock;

    @Mock
    private DeletePopupPresenter deletePopupPresenterMock;
    @Mock
    private PreserveDeletePopupPresenter preserveDeletePopupPresenterMock;
    @Mock
    private ConfirmPopupPresenter confirmPopupPresenterMock;
    @Mock
    private FileUploadPopupPresenter fileUploadPopupPresenterMock;

    private ScenarioSimulationEventHandler scenarioSimulationEventHandler;

    @Before
    public void setup() {
        super.setup();
        when(eventBusMock.addHandler(eq(AppendColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(appendColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(AppendRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(appendRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DeleteColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(deleteColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DeleteRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(deleteRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DisableTestToolsEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(disableTestToolsEventHandlerMock);
        when(eventBusMock.addHandler(eq(DuplicateInstanceEvent.TYPE), isA((ScenarioSimulationEventHandler.class)))).thenReturn(duplicateColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DuplicateRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(duplicateHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(EnableTestToolsEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(enableTestToolsEventHandlerMock);
        when(eventBusMock.addHandler(eq(ImportEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(importHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(InsertColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(insertColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(InsertRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(insertRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(PrependColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(prependColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(PrependRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(prependRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(RedoEvent.TYPE), isA(RedoEventHandler.class))).thenReturn(redoEventHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(ReloadTestToolsEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(reloadTestToolsHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(RunSingleScenarioEvent.TYPE), isA(RunSingleScenarioEventHandler.class))).thenReturn(runSingleScenarioHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(ScenarioGridReloadEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(scenarioGridReloadHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(SetGridCellValueEvent.TYPE), isA(SetGridCellValueEventHandler.class))).thenReturn(setGridCellValueEventHandlerMock);
        when(eventBusMock.addHandler(eq(SetHeaderCellValueEvent.TYPE), isA(SetHeaderCellValueEventHandler.class))).thenReturn(setHeaderCellValueEventHandlerMock);
        when(eventBusMock.addHandler(eq(SetInstanceHeaderEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(setInstanceHeaderEventHandlerMock);
        when(eventBusMock.addHandler(eq(SetPropertyHeaderEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(setPropertyHeaderEventHandlerMock);
        when(eventBusMock.addHandler(eq(UndoEvent.TYPE), isA(UndoEventHandler.class))).thenReturn(undoEventHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(UnsupportedDMNEvent.TYPE), isA(UnsupportedDMNEventHandler.class))).thenReturn(unsupportedDMNEventHandlerRegistrationMock);

        when(scenarioCommandManagerMock.execute(eq(scenarioSimulationContextLocal), isA(AbstractScenarioSimulationCommand.class))).thenReturn(CommandResultBuilder.SUCCESS);
        scenarioSimulationEventHandler = spy(new ScenarioSimulationEventHandler() {
            {
                this.eventBus = eventBusMock;
                this.handlerRegistrationList = handlerRegistrationListMock;
                this.deletePopupPresenter = deletePopupPresenterMock;
                this.preserveDeletePopupPresenter = preserveDeletePopupPresenterMock;
                this.confirmPopupPresenter = confirmPopupPresenterMock;
                this.fileUploadPopupPresenter = fileUploadPopupPresenterMock;
                this.context = scenarioSimulationContextLocal;
                this.scenarioCommandManager = scenarioCommandManagerMock;
                this.scenarioCommandRegistry = scenarioCommandRegistryMock;
                this.notificationEvent = ScenarioSimulationEventHandlerTest.this.notificationEvent;
            }
        });
    }

    @Test
    public void setEventBus() {
        scenarioSimulationEventHandler.setEventBus(eventBusMock);
        verify(scenarioSimulationEventHandler, times(1)).registerHandlers();
        assertEquals(eventBusMock, scenarioSimulationEventHandler.eventBus);
    }

    @Test
    public void unregisterHandlers() {
        scenarioSimulationEventHandler.unregisterHandlers();
        verify(handlerRegistrationListMock, times(1)).forEach(anyObject());
    }

    @Test
    public void onAppendColumnEvent() {
        AppendColumnEvent event = new AppendColumnEvent(COLUMN_GROUP);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(AppendColumnCommand.class),
                                                               eq(true));
    }

    @Test
    public void onAppendRowEvent() {
        AppendRowEvent event = new AppendRowEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(AppendRowCommand.class),
                                                               eq(true));
    }

    @Test
    public void onDeleteColumnEvent() {
        DeleteColumnEvent event = new DeleteColumnEvent(COLUMN_INDEX, COLUMN_GROUP, false);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(DeleteColumnCommand.class),
                                                               eq(true));
    }

    @Test
    public void onDeleteRowEvent() {
        DeleteRowEvent event = new DeleteRowEvent(ROW_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(DeleteRowCommand.class),
                                                               eq(true));
    }

    @Test
    public void onDisableTestToolsEvent() {
        DisableTestToolsEvent event = new DisableTestToolsEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(DisableTestToolsCommand.class),
                                                               eq(false));
    }

    @Test
    public void onDuplicateColumnEvent() {
        DuplicateInstanceEvent event = new DuplicateInstanceEvent(COLUMN_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(DuplicateInstanceCommand.class),
                                                               eq(true));
    }

    @Test
    public void onDuplicateRowEvent() {
        DuplicateRowEvent event = new DuplicateRowEvent(ROW_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(DuplicateRowCommand.class),
                                                               eq(true));
    }

    @Test
    public void onEnableTestToolsEvent() {
        EnableTestToolsEvent event = new EnableTestToolsEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(EnableTestToolsCommand.class),
                                                               eq(false));
    }

    @Test
    public void onImportEvent() {
        ImportEvent event = new ImportEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(fileUploadPopupPresenterMock, times(1))
                .show(anyListOf(String.class),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.selectImportFile()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.importLabel()),
                      isA(org.uberfire.mvp.Command.class));
    }

    @Test
    public void onInsertColumnEvent() {
        InsertColumnEvent event = new InsertColumnEvent(COLUMN_INDEX, true, false);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(InsertColumnCommand.class),
                                                               eq(true));
    }

    @Test
    public void onInsertRowEvent() {
        InsertRowEvent event = new InsertRowEvent(ROW_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(InsertRowCommand.class),
                                                               eq(true));
    }

    @Test
    public void onPrependColumnEvent() {
        PrependColumnEvent event = new PrependColumnEvent(COLUMN_GROUP);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(PrependColumnCommand.class),
                                                               eq(true));
    }

    @Test
    public void onPrependRowEvent() {
        PrependRowEvent event = new PrependRowEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(PrependRowCommand.class),
                                                               eq(true));
    }

    @Test
    public void onRedoEvent() {
        RedoEvent event = new RedoEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioCommandRegistryMock, times(1)).redo(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void onReloadTestToolsEvent() {
        ReloadTestToolsEvent event = new ReloadTestToolsEvent(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(ReloadTestToolsCommand.class),
                                                               eq(false));
    }

    @Test
    public void onRunSingleScenarioPanelEvent() {
        RunSingleScenarioEvent event = new RunSingleScenarioEvent(ROW_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(RunSingleScenarioCommand.class),
                                                               eq(false));
    }

    @Test
    public void onScenarioGridReloadEvent() {
        ScenarioGridReloadEvent event = new ScenarioGridReloadEvent();
        scenarioSimulationEventHandler.handle(event);
        verify(scenarioGridPanelMock, times(1)).onResize();
    }

    @Test
    public void onSetGridCellValueEvent() {
        SetGridCellValueEvent event = new SetGridCellValueEvent(ROW_INDEX, COLUMN_INDEX, MULTIPART_VALUE);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(SetGridCellValueCommand.class),
                                                               eq(false));
    }

    @Test
    public void onSetHeaderCellValueEventInstanceHeader() {
        SetHeaderCellValueEvent event = new SetHeaderCellValueEvent(ROW_INDEX, COLUMN_INDEX, MULTIPART_VALUE, true, false);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(SetHeaderCellValueCommand.class),
                                                               eq(false));
    }

    @Test
    public void onSetHeaderCellValueEventPropertyHeader() {
        SetHeaderCellValueEvent event = new SetHeaderCellValueEvent(ROW_INDEX, COLUMN_INDEX, MULTIPART_VALUE, false, true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(SetHeaderCellValueCommand.class),
                                                               eq(false));
    }

    @Test
    public void onSetInstanceHeaderEvent() {
        SetInstanceHeaderEvent event = new SetInstanceHeaderEvent(FULL_PACKAGE, FULL_CLASS_NAME);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetInstanceHeaderCommand.class),
                                                                        anyBoolean());
        //
        doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
        when(scenarioGridModelMock.isSameSelectedColumnType(anyString())).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetInstanceHeaderCommand.class),
                                                                        anyBoolean());
        //
        when(scenarioGridModelMock.isSameInstanceType(anyString())).thenReturn(false);
        when(gridColumnMock.isInstanceAssigned()).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(deletePopupPresenterMock, times(1))
                .show(eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainTitle()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainQuestion()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeText1()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextQuestion()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextDanger()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.changeType()),
                      isA(org.uberfire.mvp.Command.class));
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetInstanceHeaderCommand.class),
                                                                        anyBoolean());

        when(scenarioGridModelMock.isSameSelectedColumnType(anyString())).thenReturn(false);
        when(gridColumnMock.isInstanceAssigned()).thenReturn(false);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(SetInstanceHeaderCommand.class),
                                                               eq(true));
    }

    @Test
    public void onSetPropertyHeaderEvent() {
        SetPropertyHeaderEvent event = new SetPropertyHeaderEvent(FULL_PACKAGE, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetPropertyHeaderCommand.class),
                                                                        anyBoolean());
        //
        doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
        when(scenarioGridModelMock.isAlreadyAssignedProperty(MULTIPART_VALUE_ELEMENTS)).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).onEvent(isA(ScenarioNotificationEvent.class));
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetPropertyHeaderCommand.class),
                                                                        anyBoolean());
        //
        reset(scenarioSimulationEventHandler);
        when(scenarioGridModelMock.isAlreadyAssignedProperty(MULTIPART_VALUE_ELEMENTS)).thenReturn(false);
        when(scenarioGridModelMock.isSelectedColumnEmpty()).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).onEvent(isA(ScenarioNotificationEvent.class));
        verify(scenarioSimulationEventHandler).commonExecution(eq(scenarioSimulationContextLocal),
                                                               isA(SetPropertyHeaderCommand.class),
                                                               eq(true));
        //
        reset(scenarioSimulationEventHandler);
        when(scenarioGridModelMock.isSelectedColumnEmpty()).thenReturn(false);
        when(scenarioGridModelMock.isSameSelectedColumnProperty(anyListOf(String.class))).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetPropertyHeaderCommand.class),
                                                                        anyBoolean());
        //
        when(scenarioGridModelMock.isSameSelectedColumnProperty(anyListOf(String.class))).thenReturn(false);
        when(scenarioGridModelMock.isSameSelectedColumnType(anyString())).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(preserveDeletePopupPresenterMock, times(1))
                .show(eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioMainTitle()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioMainQuestion()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioText1()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextQuestion()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextOption1()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextOption2()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.preserveValues()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.deleteValues()),
                      isA(org.uberfire.mvp.Command.class),
                      isA(org.uberfire.mvp.Command.class));
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetPropertyHeaderCommand.class),
                                                                        anyBoolean());
        //
        when(scenarioGridModelMock.isSameSelectedColumnType(anyString())).thenReturn(false);
        scenarioSimulationEventHandler.onEvent(event);
        verify(deletePopupPresenterMock, times(1))
                .show(eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainTitle()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainQuestion()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioText1()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextQuestion()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextDanger()),
                      eq(ScenarioSimulationEditorConstants.INSTANCE.deleteValues()),
                      isA(org.uberfire.mvp.Command.class));
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal),
                                                                        isA(SetPropertyHeaderCommand.class),
                                                                        anyBoolean());
    }

    @Test
    public void onUndoEvent() {
        UndoEvent event = new UndoEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioCommandRegistryMock, times(1)).undo(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void onUnsupportedDMNEvent() {
        String DMN_ERROR = "DMN_ERROR";
        UnsupportedDMNEvent event = new UnsupportedDMNEvent(DMN_ERROR);
        scenarioSimulationEventHandler.onEvent(event);
        verify(confirmPopupPresenterMock, times(1)).show(anyString(), eq(DMN_ERROR));
    }

    @Test
    public void commonExecution() {
        when(scenarioCommandManagerMock.execute(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock))).thenReturn(CommandResultBuilder.SUCCESS);
        scenarioSimulationEventHandler.commonExecution(scenarioSimulationContextLocal, appendRowCommandMock, true);
        assertEquals(simulationMock, scenarioSimulationContextLocal.getStatus().getSimulation());
        verify(scenarioCommandRegistryMock, times(1)).register(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock));
        //
        reset(scenarioCommandRegistryMock);
        CommandResult<ScenarioSimulationViolation> status = new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.singletonList(new ScenarioSimulationViolation("FAKE ERROR")));
        when(scenarioCommandManagerMock.execute(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock))).thenReturn(status);
        scenarioSimulationEventHandler.commonExecution(scenarioSimulationContextLocal, appendRowCommandMock, true);
        assertEquals(simulationMock, scenarioSimulationContextLocal.getStatus().getSimulation());
        verify(scenarioCommandRegistryMock, never()).register(eq(appendRowCommandMock));
    }

    @Test
    public void registerHandlers() {
        scenarioSimulationEventHandler.registerHandlers();
        verify(eventBusMock, times(1)).addHandler(eq(AppendColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(appendColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(AppendRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(appendRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(DeleteColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(deleteColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(DeleteRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(deleteRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(DisableTestToolsEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(disableTestToolsEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(DuplicateInstanceEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(duplicateColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(DuplicateRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(duplicateHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(EnableTestToolsEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(enableTestToolsEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(ImportEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(importHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(InsertColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(insertColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(InsertRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(insertRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(PrependColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(prependColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(PrependRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(prependRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(RedoEvent.TYPE), isA(RedoEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(redoEventHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(ReloadTestToolsEvent.TYPE), isA(ReloadTestToolsEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(reloadTestToolsHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(RunSingleScenarioEvent.TYPE), isA(RunSingleScenarioEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(runSingleScenarioHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(ScenarioGridReloadEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(scenarioGridReloadHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(SetGridCellValueEvent.TYPE), isA(SetGridCellValueEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(setGridCellValueEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(SetHeaderCellValueEvent.TYPE), isA(SetHeaderCellValueEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(setHeaderCellValueEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(SetInstanceHeaderEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(setInstanceHeaderEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(SetPropertyHeaderEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(setPropertyHeaderEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(UndoEvent.TYPE), isA(UndoEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(undoEventHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(UnsupportedDMNEvent.TYPE), isA(UnsupportedDMNEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(unsupportedDMNEventHandlerRegistrationMock));
    }
}