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

package org.drools.workbench.screens.scenariosimulation.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationViewImplTest {

    @Mock
    private ScenarioGrid scenarioGrid;

    @Mock
    private ScenarioGridModel scenarioGridModel;

    @Mock
    private ScenarioGridLayer scenarioGridLayer;

    @Mock
    private ScenarioGridPanel scenarioGridPanel;

    private ScenarioSimulationViewImpl scenarioView;

    @Before
    public void setUp() throws Exception {
        when(scenarioGridPanel.getScenarioGridLayer()).thenReturn(scenarioGridLayer);
        when(scenarioGridPanel.getScenarioGrid()).thenReturn(scenarioGrid);
        when(scenarioGrid.getModel()).thenReturn(scenarioGridModel);

        scenarioView = new ScenarioSimulationViewImpl();
        scenarioView.setScenarioGridPanel(scenarioGridPanel);
    }

    @Test
    public void testKeyboardNavigationPrepared() {
        final Simulation simulationModel = mock(Simulation.class);
        when(scenarioGridModel.getColumnCount()).thenReturn(1);
        when(scenarioGridModel.getRowCount()).thenReturn(1);

        scenarioView.setContent(simulationModel);

        verify(scenarioGrid).setContent(simulationModel);
        verify(scenarioGridPanel).setFocus(true);
    }

    @Test
    public void testKeyboardNavigationPrepared_noDataInScenario() {
        final Simulation simulationModel = mock(Simulation.class);
        when(scenarioGridModel.getColumnCount()).thenReturn(0);
        when(scenarioGridModel.getRowCount()).thenReturn(0);

        scenarioView.setContent(simulationModel);

        verify(scenarioGrid).setContent(simulationModel);
        verify(scenarioGridPanel).setFocus(true);
    }
}
