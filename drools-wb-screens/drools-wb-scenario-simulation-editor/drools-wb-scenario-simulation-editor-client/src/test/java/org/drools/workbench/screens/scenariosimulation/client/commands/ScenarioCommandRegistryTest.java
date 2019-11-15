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

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AbstractScenarioGridCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioCommandRegistryTest extends AbstractScenarioSimulationTest {

    private ScenarioCommandRegistry scenarioCommandRegistrySpy;

    @Before
    public void setup() {
        super.setup();
        scenarioCommandRegistrySpy = spy(new ScenarioCommandRegistry() {

        });
    }

    @Test
    public void register() {
        scenarioCommandRegistrySpy.undoneCommands.add(mock(AbstractScenarioGridCommand.class));
        assertFalse(scenarioCommandRegistrySpy.undoneCommands.isEmpty());
        scenarioCommandRegistrySpy.register(scenarioSimulationContextLocal, mock(AbstractScenarioGridCommand.class));
        assertTrue(scenarioCommandRegistrySpy.undoneCommands.isEmpty());
        verify(scenarioCommandRegistrySpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void undoEmpty() {
        scenarioCommandRegistrySpy.undoneCommands.clear();
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistrySpy.undo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.WARNING, retrieved.getType());
        verify(scenarioCommandRegistrySpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistrySpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void undoNotEmptySameGrid() {
        scenarioCommandRegistrySpy.undoneCommands.clear();
        AbstractScenarioGridCommand commandMock = mock(AbstractScenarioGridCommand.class);
        scenarioCommandRegistrySpy.register(commandMock);
        doReturn(CommandResultBuilder.SUCCESS).when(scenarioCommandRegistrySpy).commonUndoRedoOperation(any(), any(), anyBoolean());
        doReturn(Optional.empty()).when(scenarioCommandRegistrySpy).commonUndoRedoPreexecution(any(), any());
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistrySpy.undo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.INFO, retrieved.getType());
        verify(scenarioCommandRegistrySpy, times(1)).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(commandMock), eq(true));
        verify(scenarioCommandRegistrySpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void undoNotEmptyDifferentGrid() {
        int currentSize = scenarioCommandRegistrySpy.undoneCommands.size();
        scenarioCommandRegistrySpy.register(scenarioSimulationContextLocal, appendRowCommandMock);
        verify(scenarioCommandRegistrySpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
        doReturn(Optional.of(CommandResultBuilder.SUCCESS)).when(appendRowCommandMock).commonUndoRedoPreexecution(eq(scenarioSimulationContextLocal));
        scenarioCommandRegistrySpy.undo(scenarioSimulationContextLocal);
        assertEquals(currentSize, scenarioCommandRegistrySpy.undoneCommands.size());
        verify(scenarioCommandRegistrySpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistrySpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoEmpty() {
        scenarioCommandRegistrySpy.undoneCommands.clear();
        scenarioCommandRegistrySpy.redo(scenarioSimulationContextLocal);
        verify(scenarioCommandRegistrySpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistrySpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoNotEmptySameGrid() {
        scenarioCommandRegistrySpy.undoneCommands.clear();
        AbstractScenarioGridCommand commandMock = mock(AbstractScenarioGridCommand.class);
        scenarioCommandRegistrySpy.undoneCommands.add(commandMock);
        doReturn(CommandResultBuilder.SUCCESS).when(scenarioCommandRegistrySpy).commonUndoRedoOperation(any(), any(), anyBoolean());
        doReturn(Optional.empty()).when(scenarioCommandRegistrySpy).commonUndoRedoPreexecution(any(), any());
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistrySpy.redo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.INFO, retrieved.getType());
        verify(scenarioCommandRegistrySpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(commandMock), eq(true));
        verify(scenarioCommandRegistrySpy, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoNotEmptyDifferentGrid() {
        scenarioCommandRegistrySpy.undoneCommands.push(appendRowCommandMock);
        doReturn(Optional.of(CommandResultBuilder.SUCCESS)).when(appendRowCommandMock).commonUndoRedoPreexecution(eq(scenarioSimulationContextLocal));
        int currentSize = scenarioCommandRegistrySpy.undoneCommands.size();
        scenarioCommandRegistrySpy.redo(scenarioSimulationContextLocal);
        assertEquals(currentSize, scenarioCommandRegistrySpy.undoneCommands.size());
        verify(scenarioCommandRegistrySpy, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(false));
        verify(scenarioCommandRegistrySpy, never()).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void setUndoRedoButtonStatus() {
        scenarioCommandRegistrySpy.clear();
        scenarioCommandRegistrySpy.undoneCommands.clear();
        scenarioCommandRegistrySpy.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(false));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        scenarioCommandRegistrySpy.register(appendRowCommandMock);
        scenarioCommandRegistrySpy.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        scenarioCommandRegistrySpy.undoneCommands.push(appendRowCommandMock);
        scenarioCommandRegistrySpy.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(true));
    }

    @Test
    public void commonOperationUndo() {
        scenarioCommandRegistrySpy.commonUndoRedoOperation(scenarioSimulationContextLocal, appendRowCommandMock, true);
        verify(appendRowCommandMock, times(1)).undo(eq(scenarioSimulationContextLocal));
        verify(appendRowCommandMock, never()).redo(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void commonOperationRedo() {
        scenarioCommandRegistrySpy.commonUndoRedoOperation(scenarioSimulationContextLocal, appendRowCommandMock, false);
        verify(appendRowCommandMock, times(1)).redo(eq(scenarioSimulationContextLocal));
        verify(appendRowCommandMock, never()).undo(eq(scenarioSimulationContextLocal));
    }
}