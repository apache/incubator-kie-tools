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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.command.client.CommandResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
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
    public void undoEmpty() {
        scenarioCommandRegistry.undoneCommands.clear();
        final CommandResult<ScenarioSimulationViolation> retrieved = scenarioCommandRegistry.undo(scenarioSimulationContext);
        assertEquals(CommandResult.Type.WARNING, retrieved.getType());
        verify(scenarioCommandRegistry, never()).commonOperation(eq(scenarioSimulationContext), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContext));
    }

    @Test
    public void undoNotEmpty() {
        int currentSize = scenarioCommandRegistry.undoneCommands.size();
        scenarioCommandRegistry.register(scenarioSimulationContext, appendRowCommandMock);
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContext));
        scenarioCommandRegistry.undo(scenarioSimulationContext);
        assertEquals(currentSize + 1, scenarioCommandRegistry.undoneCommands.size());
        verify(scenarioCommandRegistry, times(1)).commonOperation(eq(scenarioSimulationContext), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistry, times(2)).setUndoRedoButtonStatus(eq(scenarioSimulationContext));
    }

    @Test
    public void redoEmpty() {
        scenarioCommandRegistry.undoneCommands.clear();
        scenarioCommandRegistry.redo(scenarioSimulationContext);
        verify(scenarioCommandRegistry, never()).commonOperation(eq(scenarioSimulationContext), eq(appendRowCommandMock), eq(true));
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContext));
    }

    @Test
    public void redoNotEmpty() {
        scenarioCommandRegistry.undoneCommands.push(appendRowCommandMock);
        int currentSize = scenarioCommandRegistry.undoneCommands.size();
        scenarioCommandRegistry.redo(scenarioSimulationContext);
        assertEquals(currentSize - 1, scenarioCommandRegistry.undoneCommands.size());
        verify(scenarioCommandRegistry, times(1)).commonOperation(eq(scenarioSimulationContext), eq(appendRowCommandMock), eq(false));
        verify(scenarioCommandRegistry, times(1)).setUndoRedoButtonStatus(eq(scenarioSimulationContext));
    }

    @Test
    public void setUndoRedoButtonStatus() {
        scenarioCommandRegistry.clear();
        scenarioCommandRegistry.undoneCommands.clear();
        scenarioCommandRegistry.setUndoRedoButtonStatus(scenarioSimulationContext);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(false));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        scenarioCommandRegistry.register(appendRowCommandMock);
        scenarioCommandRegistry.setUndoRedoButtonStatus(scenarioSimulationContext);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(false));
        //
        reset(scenarioSimulationEditorPresenterMock);
        scenarioCommandRegistry.undoneCommands.push(appendRowCommandMock);
        scenarioCommandRegistry.setUndoRedoButtonStatus(scenarioSimulationContext);
        verify(scenarioSimulationEditorPresenterMock, times(1)).setUndoButtonEnabledStatus(eq(true));
        verify(scenarioSimulationEditorPresenterMock, times(1)).setRedoButtonEnabledStatus(eq(true));
    }

    @Test
    public void commonOperationUndo() {
        scenarioCommandRegistry.commonOperation(scenarioSimulationContext, appendRowCommandMock, true);
        verify(appendRowCommandMock, times(1)).undo(eq(scenarioSimulationContext));
        verify(appendRowCommandMock, never()).redo(eq(scenarioSimulationContext));
    }

    @Test
    public void commonOperationRedo() {
        scenarioCommandRegistry.commonOperation(scenarioSimulationContext, appendRowCommandMock, false);
        verify(appendRowCommandMock, times(1)).redo(eq(scenarioSimulationContext));
        verify(appendRowCommandMock, never()).undo(eq(scenarioSimulationContext));
    }
}