/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.producers;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.popup.ConfirmPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.FileUploadPopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public abstract class AbstractProducerTest extends AbstractScenarioSimulationTest {

    @Mock
    protected EventBusProducer eventBusProducerMock;
    @Mock
    protected ScenarioGridPanelProducer scenarioGridPanelProducerMock;
    @Mock
    protected DeletePopupPresenter deletePopupPresenterMock;
    @Mock
    protected PreserveDeletePopupPresenter preserveDeletePopupPresenterMock;
    @Mock
    protected ConfirmPopupPresenter confirmPopupPresenterMock;
    @Mock
    protected FileUploadPopupPresenter fileUploadPopupPresenterMock;
    @Mock
    protected ScenarioSimulationEventHandler scenarioSimulationEventHandlerMock;
    @Mock
    protected ScenarioSimulationView scenarioSimulationViewMock;
    @Mock
    protected ScenarioContextMenuRegistry scenarioContextMenuRegistryMock;

    @Before
    public void setup() {
        super.setup();
        when(eventBusProducerMock.getEventBus()).thenReturn(eventBusMock);
        when(scenarioGridPanelProducerMock.getScenarioSimulationView(isA(EventBus.class))).thenReturn(scenarioSimulationViewMock);
        when(scenarioGridPanelProducerMock.getScenarioContextMenuRegistry()).thenReturn(scenarioContextMenuRegistryMock);
    }
}