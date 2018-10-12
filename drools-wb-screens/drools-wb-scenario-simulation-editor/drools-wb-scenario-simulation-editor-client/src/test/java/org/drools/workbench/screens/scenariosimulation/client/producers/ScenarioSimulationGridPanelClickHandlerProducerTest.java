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
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.ExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderExpectedContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.OtherContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.UnmodifiableColumnGridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelClickHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridPanelClickHandlerProducerTest {

    @Mock
    private OtherContextMenu otherContextMenuMock;
    @Mock
    private HeaderGivenContextMenu headerGivenContextMenuMock;
    @Mock
    private HeaderExpectedContextMenu headerExpectedContextMenuMock;
    @Mock
    private GivenContextMenu givenContextMenuMock;
    @Mock
    private ExpectedContextMenu expectedContextMenuMock;
    @Mock
    private GridContextMenu gridContextMenuMock;
    @Mock
    private UnmodifiableColumnGridContextMenu unmodifiableColumnGridContextMenuMock;

    @Mock
    private ScenarioSimulationGridPanelClickHandler scenarioSimulationGridPanelClickHandlerMock;

    private ScenarioSimulationGridPanelClickHandlerProducer scenarioSimulationGridPanelClickHandlerProducer;

    @Before
    public void setUp() {
        scenarioSimulationGridPanelClickHandlerProducer = spy(new ScenarioSimulationGridPanelClickHandlerProducer() {
            {
                this.expectedContextMenu = expectedContextMenuMock;
                this.givenContextMenu = givenContextMenuMock;
                this.gridContextMenu = gridContextMenuMock;
                this.headerExpectedContextMenu = headerExpectedContextMenuMock;
                this.headerGivenContextMenu = headerGivenContextMenuMock;
                this.otherContextMenu = otherContextMenuMock;
                this.unmodifiableColumnGridContextMenu = unmodifiableColumnGridContextMenuMock;
                this.scenarioSimulationGridPanelClickHandler = scenarioSimulationGridPanelClickHandlerMock;
            }
        });
    }

    @Test
    public void getScenarioSimulationGridPanelClickHandler() {
        final ScenarioSimulationGridPanelClickHandler retrieved = scenarioSimulationGridPanelClickHandlerProducer.getScenarioSimulationGridPanelClickHandler();
        assertEquals(scenarioSimulationGridPanelClickHandlerMock, retrieved);
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setExpectedContextMenu(eq(expectedContextMenuMock));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setGivenContextMenu(eq(givenContextMenuMock));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setGridContextMenu(eq(gridContextMenuMock));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setHeaderExpectedContextMenu(eq(headerExpectedContextMenuMock));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setHeaderGivenContextMenu(eq(headerGivenContextMenuMock));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setOtherContextMenu(eq(otherContextMenuMock));
        verify(scenarioSimulationGridPanelClickHandlerMock, times(1)).setUnmodifiableColumnGridContextMenu(eq(unmodifiableColumnGridContextMenuMock));
    }
}