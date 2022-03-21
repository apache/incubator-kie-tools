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
package org.drools.workbench.screens.scenariosimulation.kogito.client.producer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandRegistryManager;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.producers.EventBusProducer;
import org.drools.workbench.screens.scenariosimulation.client.producers.ScenarioGridPanelProducer;
import org.drools.workbench.screens.scenariosimulation.kogito.client.commands.ScenarioSimulationKogitoStateControlManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationKogitoProducerTest {

    @Mock
    private ScenarioSimulationEventHandler scenarioSimulationEventHandlerMock;
    @Mock
    private ScenarioSimulationKogitoStateControlManager scenarioSimulationKogitoStateControlManagerMock;
    @Mock
    private ScenarioCommandRegistryManager scenarioCommandRegistryManagerMock;
    @Mock
    private ScenarioGridPanelProducer scenarioGridPanelProducerMock;
    @Mock
    private EventBusProducer eventBusProducerMock;
    @Mock
    private ScenarioSimulationContext scenarioSimulationContextMock;
    @Mock
    private ScenarioContextMenuRegistry scenarioContextMenuRegistryMock;

    private ScenarioSimulationKogitoProducer scenarioSimulationKogitoProducerSpy;

    @Before
    public void setup() {
        scenarioSimulationKogitoProducerSpy = spy(new ScenarioSimulationKogitoProducer() {
            {
                this.scenarioSimulationStateControlManager = scenarioSimulationKogitoStateControlManagerMock;
                this.scenarioCommandRegistryManager = scenarioCommandRegistryManagerMock;
                this.scenarioGridPanelProducer = scenarioGridPanelProducerMock;
                this.eventBusProducer = eventBusProducerMock;
                this.scenarioSimulationEventHandler = scenarioSimulationEventHandlerMock;
            }
        });
        when(scenarioGridPanelProducerMock.getScenarioSimulationContext()).thenReturn(scenarioSimulationContextMock);
        when(scenarioGridPanelProducerMock.getScenarioContextMenuRegistry()).thenReturn(scenarioContextMenuRegistryMock);
    }

    @Test
    public void init() {
        scenarioSimulationKogitoProducerSpy.init();
        verify(scenarioSimulationKogitoStateControlManagerMock, times(1)).initialize(eq(scenarioCommandRegistryManagerMock), eq(scenarioSimulationContextMock));
    }
}
