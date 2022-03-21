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
package org.drools.workbench.screens.scenariosimulation.kogito.client.commands;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.kogito.bridge.client.stateControl.interop.StateControl;
import org.appformer.kogito.bridge.client.stateControl.interop.StateControlCommand;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandRegistryManager;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationKogitoStateControlManagerTest {

    @Mock
    private ScenarioSimulationContext scenarioSimulationContextMock;
    @Mock
    private ScenarioCommandRegistryManager scenarioCommandRegistryManagerMock;
    @Captor
    private ArgumentCaptor<StateControlCommand> stateControlUndoCommandCaptor;
    @Captor
    private ArgumentCaptor<StateControlCommand> stateControlRedoCommandCaptor;

    private ScenarioSimulationKogitoStateControlManager scenarioSimulationKogitoStateControlManagerSpy;
    private boolean envelopeAvailableBoolean;
    private StateControl stateControlSpy;

    @Before
    public void setup() {
        stateControlSpy = spy(new StateControl());
        envelopeAvailableBoolean = true;
        scenarioSimulationKogitoStateControlManagerSpy = spy(new ScenarioSimulationKogitoStateControlManager() {
            {
                this.envelopeAvailableSupplier = () -> envelopeAvailableBoolean;
                this.stateControlSupplier = () -> stateControlSpy;
            }
        });
    }

    @Test
    public void initialize_EnvelopeAvailable() {
        scenarioSimulationKogitoStateControlManagerSpy.initialize(scenarioCommandRegistryManagerMock, scenarioSimulationContextMock);
        verify(stateControlSpy, times(1)).setUndoCommand(stateControlUndoCommandCaptor.capture());
        verify(stateControlSpy, times(1)).setRedoCommand(stateControlRedoCommandCaptor.capture());
        stateControlUndoCommandCaptor.getValue().execute();
        verify(scenarioCommandRegistryManagerMock, times(1)).undo(eq(scenarioSimulationContextMock));
        stateControlRedoCommandCaptor.getValue().execute();
        verify(scenarioCommandRegistryManagerMock, times(1)).redo(eq(scenarioSimulationContextMock));
    }

    @Test
    public void initialize_EnvelopeNotAvailable() {
        envelopeAvailableBoolean = false;
        scenarioSimulationKogitoStateControlManagerSpy.initialize(scenarioCommandRegistryManagerMock, scenarioSimulationContextMock);
        verify(stateControlSpy, never()).setUndoCommand(any());
        verify(stateControlSpy, never()).setRedoCommand(any());
    }
}
