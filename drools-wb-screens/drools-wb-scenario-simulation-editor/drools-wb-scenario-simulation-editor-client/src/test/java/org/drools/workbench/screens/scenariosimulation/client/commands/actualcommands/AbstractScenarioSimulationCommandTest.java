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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractScenarioSimulationCommandTest extends AbstractScenarioSimulationTest {

    @Mock
    protected TestToolsPresenter testToolsPresenterMock;

    @Mock
    protected EventBus eventBusMock;

    protected AbstractScenarioSimulationCommand command;

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void undoOnUndoable() {
        if (command.isUndoable()) {
            command.restorableStatus = scenarioSimulationContextLocal.getStatus();
            command.undo(scenarioSimulationContextLocal);
            verify(command, times(1)).setCurrentContext(eq(scenarioSimulationContextLocal));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void undoOnNotUndoable() {
        if (!command.isUndoable()) {
            command.undo(scenarioSimulationContextLocal);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void redoOnRedoable() {
        if (command.isUndoable()) {
            command.restorableStatus = scenarioSimulationContextLocal.getStatus();
            command.redo(scenarioSimulationContextLocal);
            verify(command, times(1)).setCurrentContext(eq(scenarioSimulationContextLocal));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void redoOnNotUndoable() {
        if (!command.isUndoable()) {
            command.redo(scenarioSimulationContextLocal);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void execute() {
        final ScenarioSimulationContext.Status status = scenarioSimulationContextLocal.getStatus();
        command.execute(scenarioSimulationContextLocal);
        try {
            verify(command, times(1)).internalExecute(eq(scenarioSimulationContextLocal));
            assertNotEquals(status, command.restorableStatus);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void setCurrentContext() {
        if (command.isUndoable()) {
            final ScenarioSimulationContext.Status status = scenarioSimulationContextLocal.getStatus();
            command.restorableStatus = status;
            command.setCurrentContext(scenarioSimulationContextLocal);
            verify(scenarioGridModelMock, times(1)).clearSelections();
            verify(scenarioGridMock, times(1)).setContent(eq(simulationMock), eq(scenarioSimulationContextLocal.getSettings().getType()));
            verify(scenarioSimulationModelMock, times(1)).setSimulation(eq(simulationMock));
            verify(scenarioSimulationEditorPresenterMock, times(1)).reloadTestTools(eq(true));
            assertNotEquals(status, command.restorableStatus);
        }
    }

    @Test
    public void commonExecution() {
        if (command.isUndoable()) {
            command.commonExecution(scenarioSimulationContextLocal);
            verify(scenarioGridPanelMock, times(1)).onResize();
            verify(scenarioGridPanelMock, times(1)).select();
        }
    }
}