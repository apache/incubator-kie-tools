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
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DisableRightPanelCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.DuplicateRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.EnableRightPanelCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.InsertColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.InsertRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.PrependColumnCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.PrependRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.ReloadRightPanelCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetCellValueCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetHeaderValueCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetInstanceHeaderCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.SetPropertyHeaderCommand;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DisableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.RedoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioNotificationEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UndoEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.UnsupportedDMNEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.RedoEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ReloadRightPanelEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.SetCellValueEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.UndoEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.UnsupportedDMNEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
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
    private HandlerRegistration disableRightPanelEventHandlerMock;
    @Mock
    private HandlerRegistration duplicateHandlerRegistrationMock;
    @Mock
    private HandlerRegistration enableRightPanelEventHandlerMock;
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
    private HandlerRegistration reloadRightPanelHandlerRegistrationMock;
    @Mock
    private HandlerRegistration scenarioGridReloadHandlerRegistrationMock;
    @Mock
    private HandlerRegistration setCellValueEventHandlerMock;
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

    private ScenarioSimulationEventHandler scenarioSimulationEventHandler;

    @Before
    public void setup() {
        super.setup();
        when(eventBusMock.addHandler(eq(AppendColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(appendColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(AppendRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(appendRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DeleteColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(deleteColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DeleteRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(deleteRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DisableRightPanelEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(disableRightPanelEventHandlerMock);
        when(eventBusMock.addHandler(eq(DuplicateRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(duplicateHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(EnableRightPanelEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(enableRightPanelEventHandlerMock);
        when(eventBusMock.addHandler(eq(InsertColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(insertColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(InsertRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(insertRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(PrependColumnEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(prependColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(PrependRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(prependRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(RedoEvent.TYPE), isA(RedoEventHandler.class))).thenReturn(redoEventHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(ReloadRightPanelEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(reloadRightPanelHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(ScenarioGridReloadEvent.TYPE), isA(ScenarioSimulationEventHandler.class))).thenReturn(scenarioGridReloadHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(SetCellValueEvent.TYPE), isA(SetCellValueEventHandler.class))).thenReturn(setCellValueEventHandlerMock);
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
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(AppendColumnCommand.class));
    }

    @Test
    public void onAppendRowEvent() {
        AppendRowEvent event = new AppendRowEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(AppendRowCommand.class));
    }

    @Test
    public void onDeleteColumnEvent() {
        DeleteColumnEvent event = new DeleteColumnEvent(COLUMN_INDEX, COLUMN_GROUP);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(DeleteColumnCommand.class));
    }

    @Test
    public void onDeleteRowEvent() {
        DeleteRowEvent event = new DeleteRowEvent(ROW_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(DeleteRowCommand.class));
    }

    @Test
    public void onDisableRightPanelEvent() {
        DisableRightPanelEvent event = new DisableRightPanelEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(DisableRightPanelCommand.class));
    }

    @Test
    public void onDuplicateRowEvent() {
        DuplicateRowEvent event = new DuplicateRowEvent(ROW_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(DuplicateRowCommand.class));
    }

    @Test
    public void onEnableRightPanelEvent() {
        EnableRightPanelEvent event = new EnableRightPanelEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(EnableRightPanelCommand.class));
    }

    @Test
    public void onInsertColumnEvent() {
        InsertColumnEvent event = new InsertColumnEvent(COLUMN_INDEX, true, false);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(InsertColumnCommand.class));
    }

    @Test
    public void onInsertRowEvent() {
        InsertRowEvent event = new InsertRowEvent(ROW_INDEX);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(InsertRowCommand.class));
    }

    @Test
    public void onPrependColumnEvent() {
        PrependColumnEvent event = new PrependColumnEvent(COLUMN_GROUP);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(PrependColumnCommand.class));
    }

    @Test
    public void onPrependRowEvent() {
        PrependRowEvent event = new PrependRowEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(PrependRowCommand.class));
    }

    @Test
    public void onRedoEvent() {
        RedoEvent event = new RedoEvent();
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioCommandRegistryMock, times(1)).redo(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void onReloadRightPanelEvent() {
        ReloadRightPanelEvent event = new ReloadRightPanelEvent(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(ReloadRightPanelCommand.class));
    }

    @Test
    public void onScenarioGridReloadEvent() {
        ScenarioGridReloadEvent event = new ScenarioGridReloadEvent();
        scenarioSimulationEventHandler.handle(event);
        verify(scenarioGridPanelMock, times(1)).onResize();
    }

    @Test
    public void onSetCellValueEventGrid() {
        SetCellValueEvent event = new SetCellValueEvent(ROW_INDEX, COLUMN_INDEX, VALUE, false);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(SetCellValueCommand.class));
    }

    @Test
    public void onSetCellValueEventHeader() {
        SetCellValueEvent event = new SetCellValueEvent(ROW_INDEX, COLUMN_INDEX, VALUE, true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(SetHeaderValueCommand.class));
    }

    @Test
    public void onSetInstanceHeaderEvent() {
        SetInstanceHeaderEvent event = new SetInstanceHeaderEvent(FULL_PACKAGE, FULL_CLASS_NAME);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetInstanceHeaderCommand.class));
        //
        doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
        when(scenarioGridModelMock.isSameSelectedColumnType(anyString())).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetInstanceHeaderCommand.class));
        //
        when(scenarioGridModelMock.isSameSelectedColumnType(anyString())).thenReturn(false);
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
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetInstanceHeaderCommand.class));
        //
        when(scenarioGridModelMock.isSameSelectedColumnType(anyString())).thenReturn(false);
        when(gridColumnMock.isInstanceAssigned()).thenReturn(false);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(SetInstanceHeaderCommand.class));
    }

    @Test
    public void onSetPropertyHeaderEvent() {
        SetPropertyHeaderEvent event = new SetPropertyHeaderEvent(FULL_PACKAGE, VALUE, VALUE_CLASS_NAME);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetPropertyHeaderCommand.class));
        //
        doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
        when(scenarioGridModelMock.isAlreadyAssignedProperty(VALUE)).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, times(1)).onEvent(isA(ScenarioNotificationEvent.class));
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetPropertyHeaderCommand.class));
        //
        reset(scenarioSimulationEventHandler);
        when(scenarioGridModelMock.isAlreadyAssignedProperty(VALUE)).thenReturn(false);
        when(scenarioGridModelMock.isSelectedColumnEmpty()).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).onEvent(isA(ScenarioNotificationEvent.class));
        verify(scenarioSimulationEventHandler, times(1)).commonExecution(eq(scenarioSimulationContextLocal), isA(SetPropertyHeaderCommand.class));
        //
        reset(scenarioSimulationEventHandler);
        when(scenarioGridModelMock.isSelectedColumnEmpty()).thenReturn(false);
        when(scenarioGridModelMock.isSameSelectedColumnProperty(anyString())).thenReturn(true);
        scenarioSimulationEventHandler.onEvent(event);
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetPropertyHeaderCommand.class));
        //
        when(scenarioGridModelMock.isSameSelectedColumnProperty(anyString())).thenReturn(false);
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
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetPropertyHeaderCommand.class));
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
        verify(scenarioSimulationEventHandler, never()).commonExecution(eq(scenarioSimulationContextLocal), isA(SetPropertyHeaderCommand.class));
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
        scenarioSimulationEventHandler.commonExecution(scenarioSimulationContextLocal, appendRowCommandMock);
        assertEquals(simulationMock, scenarioSimulationContextLocal.getStatus().getSimulation());
        verify(scenarioCommandRegistryMock, times(1)).register(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock));
        //
        reset(scenarioCommandRegistryMock);
        CommandResult<ScenarioSimulationViolation> status = new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.singletonList(new ScenarioSimulationViolation("FAKE ERROR")));
        when(scenarioCommandManagerMock.execute(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock))).thenReturn(status);
        scenarioSimulationEventHandler.commonExecution(scenarioSimulationContextLocal, appendRowCommandMock);
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
        verify(eventBusMock, times(1)).addHandler(eq(DisableRightPanelEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(disableRightPanelEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(DuplicateRowEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(duplicateHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(EnableRightPanelEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(enableRightPanelEventHandlerMock));
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
        verify(eventBusMock, times(1)).addHandler(eq(ReloadRightPanelEvent.TYPE), isA(ReloadRightPanelEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(reloadRightPanelHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(ScenarioGridReloadEvent.TYPE), isA(ScenarioSimulationEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(scenarioGridReloadHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(SetCellValueEvent.TYPE), isA(SetCellValueEventHandler.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(setCellValueEventHandlerMock));
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