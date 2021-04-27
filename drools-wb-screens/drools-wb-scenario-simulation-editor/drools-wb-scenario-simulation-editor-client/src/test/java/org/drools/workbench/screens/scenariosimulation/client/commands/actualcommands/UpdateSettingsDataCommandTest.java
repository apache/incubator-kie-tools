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
package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class UpdateSettingsDataCommandTest extends AbstractScenarioSimulationTest {

    @Mock
    private Consumer consumerMock;

    protected UpdateSettingsDataCommand commandSpy;

    @Before
    public void setup() {
        commandSpy = spy(new UpdateSettingsDataCommand(consumerMock, false));
        super.setup();
    }

    @Test
    public void setRestorableStatus() {
        Settings clonedStatus = commandSpy.setRestorableStatusPreExecution(scenarioSimulationContextLocal);
        assertNotNull(clonedStatus);
    }

    @Test
    public void undoWithRestorable() {
        commandSpy.restorableStatus = settingsLocal;
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
        commandSpy.restorableStatus = settingsLocal;
        commandSpy.redo(scenarioSimulationContextLocal);
        verify(commandSpy, times(1)).setCurrentContext(eq(scenarioSimulationContextLocal));
    }

    @Test(expected = IllegalStateException.class)
    public void redoWithoutRestorable() {
        commandSpy.restorableStatus = null;
        commandSpy.redo(scenarioSimulationContextLocal);
    }

    @Test
    public void commonUndoRedoPreExecution() {
        Optional<CommandResult<ScenarioSimulationViolation>> optional = commandSpy.commonUndoRedoPreExecution(scenarioSimulationContextLocal);
        verify(scenarioSimulationEditorPresenterMock, times(1)).expandSettingsDock();
        assertEquals(CommandResultBuilder.SUCCESS, optional.get());
    }

    @Test
    public void internalExecute() {
        commandSpy.internalExecute(scenarioSimulationContextLocal);
        verify(consumerMock, times(1)).accept(eq(settingsLocal));
        verify(scenarioSimulationEditorPresenterMock, never()).getPopulateTestToolsCommand();
        verify(scenarioSimulationEditorPresenterMock, never()).getUpdateDMNMetadataCommand();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadSettingsDock();
    }

    @Test
    public void internalExecuteDMNPathChanged() {
        when(scenarioSimulationEditorPresenterMock.getPopulateTestToolsCommand()).thenReturn(() -> {});
        when(scenarioSimulationEditorPresenterMock.getUpdateDMNMetadataCommand()).thenReturn(() -> {});
        commandSpy = spy(new UpdateSettingsDataCommand(consumerMock, true));
        commandSpy.internalExecute(scenarioSimulationContextLocal);
        verify(consumerMock, times(1)).accept(eq(settingsLocal));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getPopulateTestToolsCommand();
        verify(scenarioSimulationEditorPresenterMock, times(1)).getUpdateDMNMetadataCommand();
        verify(scenarioSimulationEditorPresenterMock, never()).reloadSettingsDock();
    }

    @Test
    public void setCurrentContextNullStatus() {
        commandSpy.restorableStatus = null;
        CommandResult<ScenarioSimulationViolation> returned = commandSpy.setCurrentContext(scenarioSimulationContextLocal);
        assertEquals(CommandResult.Type.ERROR, returned.getType());
    }

    @Test
    public void setCurrentContext() {
        commandSpy.restorableStatus = settingsLocal;
        commandSpy.setCurrentContext(scenarioSimulationContextLocal);
        verify(scenarioSimulationModelMock, times(1)).setSettings(eq(settingsLocal));
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadSettingsDock();
        verify(scenarioSimulationEditorPresenterMock, never()).getUpdateDMNMetadataCommand();
        verify(scenarioSimulationEditorPresenterMock, never()).validateSimulation();
        verify(commandSpy, times(1)).commonExecution(eq(scenarioSimulationContextLocal));
        assertNotEquals(settingsLocal, commandSpy.restorableStatus);
    }

    @Test
    public void setCurrentContextDMNPatchChanged() {
        when(scenarioSimulationEditorPresenterMock.getPopulateTestToolsCommand()).thenReturn(() -> {});
        commandSpy = spy(new UpdateSettingsDataCommand(consumerMock, true));
        commandSpy.restorableStatus = settingsLocal;
        commandSpy.setCurrentContext(scenarioSimulationContextLocal);
        verify(scenarioSimulationModelMock, times(1)).setSettings(eq(settingsLocal));
        verify(scenarioSimulationEditorPresenterMock, times(1)).getPopulateTestToolsCommand();
        verify(scenarioSimulationEditorPresenterMock, times(1)).validateSimulation();
        verify(scenarioSimulationEditorPresenterMock, times(1)).reloadSettingsDock();
        verify(commandSpy, times(1)).commonExecution(eq(scenarioSimulationContextLocal));
        assertNotEquals(settingsLocal, commandSpy.restorableStatus);
    }

}
