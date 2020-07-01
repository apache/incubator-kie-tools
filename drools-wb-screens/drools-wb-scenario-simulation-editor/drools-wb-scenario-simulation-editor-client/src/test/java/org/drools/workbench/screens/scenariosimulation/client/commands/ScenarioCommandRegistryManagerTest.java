/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AbstractScenarioGridCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioCommandRegistryManagerTest extends AbstractScenarioSimulationTest {

    private ScenarioCommandRegistryManager scenarioCommandRegistryManagerSpy;
    private DefaultRegistryImpl<AbstractScenarioGridCommand> doneCommandsRegistrySpy;
    private DefaultRegistryImpl<AbstractScenarioGridCommand> undoneCommandsRegistrySpy;

    @Mock
    private AbstractScenarioGridCommand abstractScenarioGridCommandMock;

    @Before
    public void setup() {
        super.setup();
        doneCommandsRegistrySpy = spy(new DefaultRegistryImpl<>());
        undoneCommandsRegistrySpy = spy(new DefaultRegistryImpl<>());
        scenarioCommandRegistryManagerSpy = spy(new ScenarioCommandRegistryManager() {
            {
                this.doneCommandsRegistry = doneCommandsRegistrySpy;
                this.undoneCommandsRegistry = undoneCommandsRegistrySpy;
            }
        });
    }

    @Test
    public void register() {
        undoneCommandsRegistrySpy.register(abstractScenarioGridCommandMock);
        assertFalse(undoneCommandsRegistrySpy.isEmpty());
        scenarioCommandRegistryManagerSpy.register(scenarioSimulationContextLocal, abstractScenarioGridCommandMock);
        verify(doneCommandsRegistrySpy, times(1)).register(eq(abstractScenarioGridCommandMock));
        verify(undoneCommandsRegistrySpy, times(1)).clear();
        verify(scenarioCommandRegistryManagerSpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
        assertTrue(undoneCommandsRegistrySpy.isEmpty());
    }

    @Test
    public void undoEmpty() {
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistryManagerSpy.undo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.WARNING, retrieved.getType());
        verify(scenarioCommandRegistryManagerSpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistryManagerSpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void undoNotEmptySameGrid() {
        doneCommandsRegistrySpy.register(abstractScenarioGridCommandMock);
        doReturn(CommandResultBuilder.SUCCESS).when(scenarioCommandRegistryManagerSpy).commonUndoRedoOperation(any(), any(), anyBoolean());
        doReturn(Optional.empty()).when(scenarioCommandRegistryManagerSpy).commonUndoRedoPreexecution(any(), any());
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistryManagerSpy.undo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.INFO, retrieved.getType());
        verify(scenarioCommandRegistryManagerSpy, times(1)).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(abstractScenarioGridCommandMock), eq(true));
        verify(scenarioCommandRegistryManagerSpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void undoNotEmptyDifferentGrid() {
        doneCommandsRegistrySpy.register(appendRowCommandMock);
        doReturn(Optional.of(CommandResultBuilder.SUCCESS)).when(appendRowCommandMock).commonUndoRedoPreexecution(eq(scenarioSimulationContextLocal));
        assertFalse(doneCommandsRegistrySpy.isEmpty());
        scenarioCommandRegistryManagerSpy.undo(scenarioSimulationContextLocal);
        assertTrue(doneCommandsRegistrySpy.isEmpty());
        verify(scenarioCommandRegistryManagerSpy, times(1)).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistryManagerSpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoEmpty() {
        scenarioCommandRegistryManagerSpy.redo(scenarioSimulationContextLocal);
        verify(scenarioCommandRegistryManagerSpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistryManagerSpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoNotEmptySameGrid() {
        undoneCommandsRegistrySpy.register(abstractScenarioGridCommandMock);
        doReturn(CommandResultBuilder.SUCCESS).when(scenarioCommandRegistryManagerSpy).commonUndoRedoOperation(any(), any(), anyBoolean());
        doReturn(Optional.empty()).when(scenarioCommandRegistryManagerSpy).commonUndoRedoPreexecution(any(), any());
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistryManagerSpy.redo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.INFO, retrieved.getType());
        verify(scenarioCommandRegistryManagerSpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(abstractScenarioGridCommandMock), eq(true));
        verify(scenarioCommandRegistryManagerSpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoNotEmptyDifferentGrid() {
        undoneCommandsRegistrySpy.register(appendRowCommandMock);
        doReturn(Optional.of(CommandResultBuilder.SUCCESS)).when(appendRowCommandMock).commonUndoRedoPreexecution(eq(scenarioSimulationContextLocal));
        assertFalse(undoneCommandsRegistrySpy.isEmpty());
        scenarioCommandRegistryManagerSpy.redo(scenarioSimulationContextLocal);
        verify(scenarioCommandRegistryManagerSpy, times(1)).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(false));
        verify(scenarioCommandRegistryManagerSpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
        assertTrue(undoneCommandsRegistrySpy.isEmpty());
    }

    @Test
    public void setUndoRedoButtonStatus() {
        scenarioCommandRegistryManagerSpy.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(false));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        doneCommandsRegistrySpy.register(appendRowCommandMock);
        scenarioCommandRegistryManagerSpy.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        undoneCommandsRegistrySpy.register(appendRowCommandMock);
        scenarioCommandRegistryManagerSpy.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(true));
    }

    @Test
    public void commonOperationUndo() {
        scenarioCommandRegistryManagerSpy.commonUndoRedoOperation(scenarioSimulationContextLocal, appendRowCommandMock, true);
        verify(appendRowCommandMock, times(1)).undo(eq(scenarioSimulationContextLocal));
        verify(appendRowCommandMock, never()).redo(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void commonOperationRedo() {
        scenarioCommandRegistryManagerSpy.commonUndoRedoOperation(scenarioSimulationContextLocal, appendRowCommandMock, false);
        verify(appendRowCommandMock, times(1)).redo(eq(scenarioSimulationContextLocal));
        verify(appendRowCommandMock, never()).undo(eq(scenarioSimulationContextLocal));
    }
}