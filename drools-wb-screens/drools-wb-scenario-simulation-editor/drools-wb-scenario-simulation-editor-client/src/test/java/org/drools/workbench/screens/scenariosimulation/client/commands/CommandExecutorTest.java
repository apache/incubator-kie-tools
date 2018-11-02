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

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GwtMockitoTestRunner;
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
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CommandExecutorTest extends AbstractCommandTest {

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
    private HandlerRegistration reloadRightPanelHandlerRegistrationMock;
    @Mock
    private HandlerRegistration scenarioGridReloadHandlerRegistrationMock;
    @Mock
    private HandlerRegistration setInstanceHeaderEventHandlerMock;
    @Mock
    private HandlerRegistration setPropertyHeaderEventHandlerMock;

    @Mock
    private DeletePopupPresenter deletePopupPresenterMock;
    @Mock
    private PreserveDeletePopupPresenter preserveDeletePopupPresenterMock;

    private CommandExecutor commandExecutor;

    @Before
    public void setup() {
        super.setup();
        when(eventBusMock.addHandler(eq(AppendColumnEvent.TYPE), isA(CommandExecutor.class))).thenReturn(appendColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(AppendRowEvent.TYPE), isA(CommandExecutor.class))).thenReturn(appendRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DeleteColumnEvent.TYPE), isA(CommandExecutor.class))).thenReturn(deleteColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DeleteRowEvent.TYPE), isA(CommandExecutor.class))).thenReturn(deleteRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(DisableRightPanelEvent.TYPE), isA(CommandExecutor.class))).thenReturn(disableRightPanelEventHandlerMock);
        when(eventBusMock.addHandler(eq(DuplicateRowEvent.TYPE), isA(CommandExecutor.class))).thenReturn(duplicateHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(EnableRightPanelEvent.TYPE), isA(CommandExecutor.class))).thenReturn(enableRightPanelEventHandlerMock);
        when(eventBusMock.addHandler(eq(InsertColumnEvent.TYPE), isA(CommandExecutor.class))).thenReturn(insertColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(InsertRowEvent.TYPE), isA(CommandExecutor.class))).thenReturn(insertRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(PrependColumnEvent.TYPE), isA(CommandExecutor.class))).thenReturn(prependColumnHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(PrependRowEvent.TYPE), isA(CommandExecutor.class))).thenReturn(prependRowHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(ReloadRightPanelEvent.TYPE), isA(CommandExecutor.class))).thenReturn(reloadRightPanelHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(ScenarioGridReloadEvent.TYPE), isA(CommandExecutor.class))).thenReturn(scenarioGridReloadHandlerRegistrationMock);
        when(eventBusMock.addHandler(eq(SetInstanceHeaderEvent.TYPE), isA(CommandExecutor.class))).thenReturn(setInstanceHeaderEventHandlerMock);
        when(eventBusMock.addHandler(eq(SetPropertyHeaderEvent.TYPE), isA(CommandExecutor.class))).thenReturn(setPropertyHeaderEventHandlerMock);
        commandExecutor = spy(new CommandExecutor() {
            {
                this.eventBus = eventBusMock;
                this.handlerRegistrationList = handlerRegistrationListMock;
                this.model = scenarioGridModelMock;
                this.scenarioGridPanel = scenarioGridPanelMock;
                this.scenarioGridLayer = scenarioGridLayerMock;
                this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenterMock;
                this.rightPanelPresenter = rightPanelPresenterMock;
                this.deletePopupPresenter = deletePopupPresenterMock;
                this.preserveDeletePopupPresenter = preserveDeletePopupPresenterMock;
            }
        });
    }

    @Test
    public void setEventBus() {
        commandExecutor.setEventBus(eventBusMock);
        verify(commandExecutor, times(1)).registerHandlers();
        assertEquals(eventBusMock, commandExecutor.eventBus);
    }

    @Test
    public void setScenarioSimulationEditorPresenter() {
        commandExecutor.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        assertEquals(scenarioSimulationEditorPresenterMock, commandExecutor.scenarioSimulationEditorPresenter);
    }

    @Test
    public void setRightPanelPresenter() {
        commandExecutor.setRightPanelPresenter(rightPanelPresenterMock);
        assertEquals(rightPanelPresenterMock, commandExecutor.rightPanelPresenter);
    }

    @Test
    public void setScenarioGridPanel() {
        commandExecutor.setScenarioGridPanel(scenarioGridPanelMock);
        assertEquals(scenarioGridPanelMock, commandExecutor.scenarioGridPanel);
        assertEquals(scenarioGridLayerMock, commandExecutor.scenarioGridLayer);
        assertEquals(scenarioGridModelMock, commandExecutor.model);
    }

    @Test
    public void unregisterHandlers() {
        commandExecutor.unregisterHandlers();
        verify(handlerRegistrationListMock, times(1)).forEach(anyObject());
    }

    @Test
    public void onAppendColumnEvent() {
        AppendColumnEvent event = new AppendColumnEvent(COLUMN_GROUP);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(AppendColumnCommand.class));
    }

    @Test
    public void onAppendRowEvent() {
        AppendRowEvent event = new AppendRowEvent();
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(AppendRowCommand.class));
    }

    @Test
    public void onDeleteColumnEvent() {
        DeleteColumnEvent event = new DeleteColumnEvent(COLUMN_INDEX, COLUMN_GROUP);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(DeleteColumnCommand.class));
        verify(commandExecutor, times(1)).commonExecute(isA(DisableRightPanelCommand.class));
        reset(commandExecutor);
        doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(DeleteColumnCommand.class));
        verify(commandExecutor, never()).commonExecute(isA(DisableRightPanelCommand.class));
    }

    @Test
    public void onDeleteRowEvent() {
        DeleteRowEvent event = new DeleteRowEvent(ROW_INDEX);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(DeleteRowCommand.class));
    }

    @Test
    public void onDisableRightPanelEvent() {
        DisableRightPanelEvent event = new DisableRightPanelEvent();
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(DisableRightPanelCommand.class));
    }

    @Test
    public void onDuplicateRowEvent() {
        DuplicateRowEvent event = new DuplicateRowEvent(ROW_INDEX);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(DuplicateRowCommand.class));
    }

    @Test
    public void onEnableRightPanelEvent() {
        EnableRightPanelEvent event = new EnableRightPanelEvent();
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(EnableRightPanelCommand.class));
    }

    @Test
    public void onInsertColumnEvent() {
        InsertColumnEvent event = new InsertColumnEvent(COLUMN_INDEX, true, false);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(InsertColumnCommand.class));
    }

    @Test
    public void onInsertRowEvent() {
        InsertRowEvent event = new InsertRowEvent(ROW_INDEX);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(InsertRowCommand.class));
    }

    @Test
    public void onPrependColumnEvent() {
        PrependColumnEvent event = new PrependColumnEvent(COLUMN_GROUP);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(PrependColumnCommand.class));
    }

    @Test
    public void onPrependRowEvent() {
        PrependRowEvent event = new PrependRowEvent();
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(PrependRowCommand.class));
    }

    @Test
    public void onReloadRightPanelEvent() {
        ReloadRightPanelEvent event = new ReloadRightPanelEvent(true);
        commandExecutor.onEvent(event);
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(true));
        reset(scenarioSimulationEditorPresenterMock);
        event = new ReloadRightPanelEvent(false);
        commandExecutor.onEvent(event);
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadRightPanel(eq(false));
    }

    @Test
    public void onScenarioGridReloadEvent() {
        commandExecutor.scenarioGridPanel = scenarioGridPanelMock;
        ScenarioGridReloadEvent event = new ScenarioGridReloadEvent();
        commandExecutor.handle(event);
        verify(scenarioGridPanelMock, times(1)).onResize();
    }

    @Test
    public void onSetInstanceHeaderEvent() {
        SetInstanceHeaderEvent event = new SetInstanceHeaderEvent(FULL_PACKAGE, FULL_CLASS_NAME);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        commandExecutor.onEvent(event);
        verify(commandExecutor, never()).commonExecute(isA(SetInstanceHeaderCommand.class));
        doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
        reset(commandExecutor);
        when(scenarioGridModelMock.isSelectedColumnEmpty()).thenReturn(true);
        when(scenarioGridModelMock.isSameSelectedColumnType(event.getClassName())).thenReturn(true);
        commandExecutor.onEvent(event);
        verify(commandExecutor, never()).commonExecute(isA(SetInstanceHeaderCommand.class));
        when(scenarioGridModelMock.isSameSelectedColumnType(event.getClassName())).thenReturn(false);
        when(gridColumnMock.isInstanceAssigned()).thenReturn(true);
        commandExecutor.onEvent(event);
        verify(deletePopupPresenterMock, times(1)).show(eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainTitle()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeMainQuestion()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeText1()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextQuestion()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.changeTypeTextDanger()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.changeType()),
                                                        isA(Command.class));
        when(gridColumnMock.isInstanceAssigned()).thenReturn(false);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(SetInstanceHeaderCommand.class));
    }

    @Test
    public void onSetPropertyHeaderEvent() {
        SetPropertyHeaderEvent event = new SetPropertyHeaderEvent(FULL_PACKAGE, VALUE, VALUE_CLASS_NAME);
        when(scenarioGridModelMock.getSelectedColumn()).thenReturn(null);
        commandExecutor.onEvent(event);
        verify(commandExecutor, never()).commonExecute(isA(SetPropertyHeaderCommand.class));

        doReturn(gridColumnMock).when(scenarioGridModelMock).getSelectedColumn();
        reset(commandExecutor);
        when(scenarioGridModelMock.isSelectedColumnEmpty()).thenReturn(true);
        commandExecutor.onEvent(event);
        verify(commandExecutor, times(1)).commonExecute(isA(SetPropertyHeaderCommand.class));

        when(scenarioGridModelMock.isSelectedColumnEmpty()).thenReturn(false);
        reset(commandExecutor);
        when(scenarioGridModelMock.isSameSelectedColumnProperty(VALUE)).thenReturn(true);
        commandExecutor.onEvent(event);
        verify(commandExecutor, never()).commonExecute(isA(SetPropertyHeaderCommand.class));

        when(scenarioGridModelMock.isSameSelectedColumnProperty(VALUE)).thenReturn(false);
        reset(commandExecutor);
        when(scenarioGridModelMock.isSameSelectedColumnType(VALUE_CLASS_NAME)).thenReturn(true);
        commandExecutor.onEvent(event);
        verify(preserveDeletePopupPresenterMock, times(1)).show(eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioMainTitle()),
                                                                eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioMainQuestion()),
                                                                eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioText1()),
                                                                eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextQuestion()),
                                                                eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextOption1()),
                                                                eq(ScenarioSimulationEditorConstants.INSTANCE.preserveDeleteScenarioTextOption2()),
                                                                eq(ScenarioSimulationEditorConstants.INSTANCE.preserveValues()),
                                                                eq(ScenarioSimulationEditorConstants.INSTANCE.deleteValues()),
                                                                isA(Command.class),
                                                                isA(Command.class));

        when(scenarioGridModelMock.isSameSelectedColumnType(VALUE_CLASS_NAME)).thenReturn(false);
        reset(commandExecutor);
        commandExecutor.onEvent(event);
        verify(deletePopupPresenterMock, times(1)).show(eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainTitle()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioMainQuestion()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioText1()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextQuestion()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.deleteScenarioTextDanger()),
                                                        eq(ScenarioSimulationEditorConstants.INSTANCE.deleteValues()),
                                                        isA(Command.class));
    }

    @Test
    public void commonExecute() {
        commandExecutor.scenarioGridPanel = scenarioGridPanelMock;
        Command mockCommand = mock(Command.class);
        commandExecutor.commonExecute(mockCommand);
        verify(mockCommand, times(1)).execute();
        verify(scenarioGridPanelMock, times(1)).onResize();
        verify(scenarioGridPanelMock, times(1)).select();
    }

    @Test
    public void registerHandlers() {
        commandExecutor.registerHandlers();
        verify(eventBusMock, times(1)).addHandler(eq(AppendColumnEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(appendColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(AppendRowEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(appendRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(DeleteColumnEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(deleteColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(DeleteRowEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(deleteRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(DisableRightPanelEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(disableRightPanelEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(DuplicateRowEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(duplicateHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(EnableRightPanelEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(enableRightPanelEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(InsertColumnEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(insertColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(InsertRowEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(insertRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(PrependColumnEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(prependColumnHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(PrependRowEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(prependRowHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(ReloadRightPanelEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(reloadRightPanelHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(ScenarioGridReloadEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(scenarioGridReloadHandlerRegistrationMock));
        verify(eventBusMock, times(1)).addHandler(eq(SetInstanceHeaderEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(setInstanceHeaderEventHandlerMock));
        verify(eventBusMock, times(1)).addHandler(eq(SetPropertyHeaderEvent.TYPE), isA(CommandExecutor.class));
        verify(handlerRegistrationListMock, times(1)).add(eq(setPropertyHeaderEventHandlerMock));
    }
}