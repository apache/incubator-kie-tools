/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.Optional;

import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.command.client.CommandResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioGridCommandTest extends AbstractScenarioSimulationTest {

    protected AbstractScenarioGridCommand commandSpy;

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void undoWithRestorable() {
        commandSpy.restorableStatus = scenarioSimulationContextLocal.getStatus();
        commandSpy.undo(scenarioSimulationContextLocal);
        verify(commandSpy, times(1)).setCurrentContext(eq(scenarioSimulationContextLocal));
    }

    @Test(expected = IllegalStateException.class)
    public void undoWithoutRestorable() {
        commandSpy.restorableStatus = null;
        commandSpy.undo(scenarioSimulationContextLocal);
    }

    @Test
    public void redoWithRestorable() {
        commandSpy.restorableStatus = scenarioSimulationContextLocal.getStatus();
        commandSpy.redo(scenarioSimulationContextLocal);
        verify(commandSpy, times(1)).setCurrentContext(eq(scenarioSimulationContextLocal));
    }

    @Test(expected = IllegalStateException.class)
    public void redoWithoutRestorable() {
        commandSpy.restorableStatus = null;
        commandSpy.redo(scenarioSimulationContextLocal);
    }

    @Test
    public void execute() {
        final ScenarioSimulationContext.Status status = scenarioSimulationContextLocal.getStatus();
        commandSpy.execute(scenarioSimulationContextLocal);
        try {
            verify(commandSpy, times(1)).internalExecute(eq(scenarioSimulationContextLocal));
            assertNotEquals(status, commandSpy.restorableStatus);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void setCurrentContextNoSimulationNoBackground() {
        final ScenarioSimulationContext.Status status = scenarioSimulationContextLocal.getStatus();
        commandSpy.restorableStatus = status;
        commandSpy.restorableStatus.setSimulation(null);
        commandSpy.restorableStatus.setBackground(null);
        final CommandResult<ScenarioSimulationViolation> retrieved = commandSpy.setCurrentContext(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.ERROR, retrieved.getType());
        verify(scenarioGridModelMock, never()).clearSelections();
        verify(backgroundGridModelMock, never()).clearSelections();
        verify(scenarioGridMock, never()).setContent(eq(simulationMock), eq(scenarioSimulationContextLocal.getSettings().getType()));
        verify(scenarioSimulationModelMock, never()).setSimulation(eq(simulationMock));
        verify(backgroundGridMock, never()).setContent(eq(simulationMock), eq(scenarioSimulationContextLocal.getSettings().getType()));
        verify(scenarioSimulationModelMock, never()).setBackground(eq(backgroundMock));
        verify(scenarioSimulationEditorPresenterMock, never()).reloadTestTools(eq(true));
        verify(commandSpy, never()).commonExecution(eq(scenarioSimulationContextLocal));
        assertEquals(status, commandSpy.restorableStatus);
    }

    @Test
    public void setCurrentContextSimulationBackground() {
        final ScenarioSimulationContext.Status status = scenarioSimulationContextLocal.getStatus();
        commandSpy.restorableStatus = status;
        final CommandResult<ScenarioSimulationViolation> retrieved = commandSpy.setCurrentContext(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.INFO, retrieved.getType());
        verify(scenarioGridModelMock, times(1)).clearSelections();
        verify(backgroundGridModelMock, times(1)).clearSelections();
        verify(scenarioGridMock, times(1)).setContent(eq(simulationMock), eq(scenarioSimulationContextLocal.getSettings().getType()));
        verify(scenarioSimulationModelMock, times(1)).setSimulation(eq(simulationMock));
        verify(backgroundGridMock, times(1)).setContent(eq(backgroundMock), eq(scenarioSimulationContextLocal.getSettings().getType()));
        verify(scenarioSimulationModelMock, times(1)).setBackground(eq(backgroundMock));
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadTestTools(eq(true));
        verify(commandSpy, times(1)).commonExecution(eq(scenarioSimulationContextLocal));
        assertNotEquals(status, commandSpy.restorableStatus);
    }

    @Test
    public void commonUndoRedoPreexecutionSameGrid() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        commandSpy.gridWidget = GridWidget.SIMULATION;
        final Optional<CommandResult<ScenarioSimulationViolation>> retrieved = commandSpy.commonUndoRedoPreexecution(scenarioSimulationContextLocal);
        assertFalse(retrieved.isPresent());
        verify(scenarioGridPanelMock, never()).onResize();
        verify(scenarioGridPanelMock, never()).select();
    }

    @Test
    public void commonUndoRedoPreexecutionDifferentGridSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        commandSpy.gridWidget = GridWidget.SIMULATION;
        final Optional<CommandResult<ScenarioSimulationViolation>> retrieved = commandSpy.commonUndoRedoPreexecution(scenarioSimulationContextLocal);
        assertTrue(retrieved.isPresent());
        assertEquals(CommandResult.Type.INFO, retrieved.get().getType());
        verify(scenarioGridPanelMock, times(1)).onResize();
        verify(scenarioGridPanelMock, times(1)).select();
    }

    @Test
    public void commonUndoRedoPreexecutionDifferentGridBACKGROUND() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        commandSpy.gridWidget = GridWidget.BACKGROUND;
        final Optional<CommandResult<ScenarioSimulationViolation>> retrieved = commandSpy.commonUndoRedoPreexecution(scenarioSimulationContextLocal);
        assertTrue(retrieved.isPresent());
        assertEquals(CommandResult.Type.INFO, retrieved.get().getType());
        verify(backgroundGridPanelMock, times(1)).onResize();
        verify(backgroundGridPanelMock, times(1)).select();
    }

    @Test
    public void commonUndoRedoPreexecutionDifferentGridCheckSwitch() {
        // Test to verify there are not new, un-managed, GridWidget
        for (GridWidget gridWidget : GridWidget.values()) {
            when(scenarioGridWidgetSpy.isSelected()).thenReturn(GridWidget.BACKGROUND.equals(gridWidget));
            when(backgroundGridWidgetSpy.isSelected()).thenReturn(GridWidget.SIMULATION.equals(gridWidget));
            commandSpy.gridWidget = gridWidget;
            commandSpy.commonUndoRedoPreexecution(scenarioSimulationContextLocal);
        }
    }

    @Test
    public void commonExecutionSIMULATION() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        commandSpy.commonExecution(scenarioSimulationContextLocal);
        verify(scenarioGridPanelMock, times(1)).onResize();
        verify(scenarioGridPanelMock, times(1)).select();
        verify(backgroundGridPanelMock, never()).onResize();
        verify(backgroundGridPanelMock, never()).select();
    }

    @Test
    public void commonExecutionBACKGROUND() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(true);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        commandSpy.commonExecution(scenarioSimulationContextLocal);
        verify(backgroundGridPanelMock, times(1)).onResize();
        verify(backgroundGridPanelMock, times(1)).select();
        verify(scenarioGridPanelMock, never()).onResize();
        verify(scenarioGridPanelMock, never()).select();
    }

    @Test
    public void commonExecutionNone() {
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(false);
        commandSpy.commonExecution(scenarioSimulationContextLocal);
        verify(scenarioGridPanelMock, never()).onResize();
        verify(scenarioGridPanelMock, never()).select();
        verify(backgroundGridPanelMock, never()).onResize();
        verify(backgroundGridPanelMock, never()).select();
    }
}