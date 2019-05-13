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
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.handlers.CommonOnMoveHandler;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.popover.ErrorReportPopoverPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationViewProducerTest extends AbstractProducerTest {

    @Mock
    private ScenarioGridPanelProducer scenarioGridPanelProducerMock;

    @Mock
    private ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandlerMock;

    @Mock
    private CommonOnMoveHandler commonOnMoveHandlerMock;

    @Mock
    private ErrorReportPopoverPresenter errorReportPopupPresenterMock;

    private ScenarioSimulationViewProducer scenarioSimulationViewProducer;

    @Before
    public void setUp() {
        super.setup();
        when(scenarioGridPanelProducerMock.getScenarioGridPanel()).thenReturn(scenarioGridPanelMock);
        when(scenarioGridPanelProducerMock.getScenarioSimulationGridPanelClickHandler()).thenReturn(scenarioSimulationGridPanelClickHandlerMock);
        when(scenarioGridPanelProducerMock.getScenarioContextMenuRegistry()).thenReturn(scenarioContextMenuRegistryMock);
        scenarioSimulationViewProducer = spy(new ScenarioSimulationViewProducer() {
            {
                this.scenarioSimulationView = scenarioSimulationViewMock;
                this.scenarioGridPanelProducer = scenarioGridPanelProducerMock;
                this.commonOnMoveHandler = commonOnMoveHandlerMock;
                this.errorReportPopupPresenter = errorReportPopupPresenterMock;
            }
        });
    }

    @Test
    public void getScenarioSimulationView() {
        final ScenarioSimulationView retrieved = scenarioSimulationViewProducer.getScenarioSimulationView(eventBusMock);
        assertEquals(scenarioSimulationViewMock, retrieved);
        verify(scenarioGridPanelMock, times(1)).setEventBus(eq(eventBusMock));
        verify(scenarioGridPanelProducerMock, times(1)).getScenarioSimulationGridPanelClickHandler();
        verify(scenarioGridPanelProducerMock, times(1)).getScenarioContextMenuRegistry();
        verify(scenarioContextMenuRegistryMock).setEventBus(eventBusMock);
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setScenarioContextMenuRegistry(eq(scenarioSimulationViewProducer.getScenarioContextMenuRegistry()));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setEventBus(eq(eventBusMock));
        verify(scenarioContextMenuRegistryMock, times(1)).setErrorReportPopoverPresenter(errorReportPopupPresenterMock);
        verify(commonOnMoveHandlerMock, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
        verify(commonOnMoveHandlerMock, times(1)).setErrorReportPopupPresenter(eq(errorReportPopupPresenterMock));
        verify(scenarioGridPanelMock, times(1)).addHandlers(eq(scenarioSimulationGridPanelClickHandlerMock), eq(commonOnMoveHandlerMock));
        verify(retrieved, times(1)).setScenarioGridPanel(eq(scenarioGridPanelMock));
    }
}