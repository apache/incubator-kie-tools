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

package org.drools.workbench.screens.scenariosimulation.client.producers;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.CommandExecutor;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationProducerTest extends AbstractProducerTest {

    private ScenarioSimulationProducer scenarioSimulationProducer;

    @Before
    public void setup() {
        super.setup();
        scenarioSimulationProducer = spy(new ScenarioSimulationProducer() {
            {
                this.commandExecutor = commandExecutorMock;
                this.deletePopupPresenter = deletePopupPresenterMock;
                this.preserveDeletePopupPresenter = preserveDeletePopupPresenterMock;
                this.eventBusProducer = eventBusProducerMock;
                this.scenarioSimulationViewProducer = scenarioSimulationViewProducerMock;
            }
        });
    }

    @Test
    public void getEventBus() {
        EventBus retrieved = scenarioSimulationProducer.getEventBus();
        assertNotNull(retrieved);
        assertEquals(retrieved, eventBusMock);
        verify(eventBusProducerMock, times(1)).getEventBus();
    }

    @Test
    public void getScenarioSimulationView() {
        ScenarioSimulationView retrieved = scenarioSimulationProducer.getScenarioSimulationView();
        assertNotNull(retrieved);
        assertEquals(retrieved, scenarioSimulationViewMock);
        verify(scenarioSimulationViewProducerMock, times(1)).getScenarioSimulationView(eq(eventBusMock));
    }

    @Test
    public void getCommandExecutor() {
        CommandExecutor retrieved = scenarioSimulationProducer.getCommandExecutor();
        assertNotNull(retrieved);
        assertEquals(retrieved, commandExecutorMock);
        verify(commandExecutorMock, times(1)).setEventBus(eq(eventBusMock));
        verify(commandExecutorMock, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
        verify(commandExecutorMock, times(1)).setDeletePopupPresenter(eq(deletePopupPresenterMock));
        verify(commandExecutorMock, times(1)).setPreserveDeletePopupPresenter(eq(preserveDeletePopupPresenterMock));
    }
}