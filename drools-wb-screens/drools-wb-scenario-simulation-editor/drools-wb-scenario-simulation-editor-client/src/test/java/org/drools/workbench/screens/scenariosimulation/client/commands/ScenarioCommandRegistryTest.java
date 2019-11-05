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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AbstractScenarioSimulationCommand;
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

    private ScenarioCommandRegistry scenarioCommandRegistry;

    @Before
    public void setup() {
        super.setup();
        scenarioCommandRegistry = spy(new ScenarioCommandRegistry() {

        });
    }

    @Test
    public void register() {
        scenarioCommandRegistry.undoneCommands.add(mock(AbstractScenarioSimulationCommand.class));
        assertFalse(scenarioCommandRegistry.undoneCommands.isEmpty());
        scenarioCommandRegistry.register(scenarioSimulationContextLocal, mock(AbstractScenarioSimulationCommand.class));
        assertTrue(scenarioCommandRegistry.undoneCommands.isEmpty());
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void undoEmpty() {
        scenarioCommandRegistry.undoneCommands.clear();
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistry.undo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.WARNING, retrieved.getType());
        verify(scenarioCommandRegistry, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void undoNotEmpty() {
        scenarioCommandRegistry.undoneCommands.clear();
        AbstractScenarioSimulationCommand commandMock = mock(AbstractScenarioSimulationCommand.class);
        scenarioCommandRegistry.register(commandMock);
        doReturn(CommandResultBuilder.SUCCESS).when(scenarioCommandRegistry).commonUndoRedoOperation(any(), any(), anyBoolean());
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistry.undo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.INFO, retrieved.getType());
        verify(scenarioCommandRegistry, times(1)).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(commandMock), eq(true));
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoEmpty() {
        scenarioCommandRegistry.undoneCommands.clear();
        scenarioCommandRegistry.redo(scenarioSimulationContextLocal);
        verify(scenarioCommandRegistry, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void redoNotEmpty() {
        scenarioCommandRegistry.undoneCommands.clear();
        AbstractScenarioSimulationCommand commandMock = mock(AbstractScenarioSimulationCommand.class);
        scenarioCommandRegistry.undoneCommands.add(commandMock);
        doReturn(CommandResultBuilder.SUCCESS).when(scenarioCommandRegistry).commonUndoRedoOperation(any(), any(), anyBoolean());
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistry.redo(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.INFO, retrieved.getType());
        verify(scenarioCommandRegistry, never()).commonUndoRedoOperation(eq(scenarioSimulationContextLocal), eq(commandMock), eq(true));
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void setUndoRedoButtonStatus() {
        scenarioCommandRegistry.clear();
        scenarioCommandRegistry.undoneCommands.clear();
        scenarioCommandRegistry.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(false));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        scenarioCommandRegistry.register(appendRowCommandMock);
        scenarioCommandRegistry.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        scenarioCommandRegistry.undoneCommands.push(appendRowCommandMock);
        scenarioCommandRegistry.setUndoRedoButtonStatus(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(true));
    }

    @Test
    public void commonOperationUndo() {
        scenarioCommandRegistry.commonUndoRedoOperation(scenarioSimulationContextLocal, appendRowCommandMock, true);
        verify(appendRowCommandMock, times(1)).undo(eq(scenarioSimulationContextLocal));
        verify(appendRowCommandMock, never()).redo(eq(scenarioSimulationContextLocal));
    }

    @Test
    public void commonOperationRedo() {
        scenarioCommandRegistry.commonUndoRedoOperation(scenarioSimulationContextLocal, appendRowCommandMock, false);
        verify(appendRowCommandMock, times(1)).redo(eq(scenarioSimulationContextLocal));
        verify(appendRowCommandMock, never()).undo(eq(scenarioSimulationContextLocal));
    }
}