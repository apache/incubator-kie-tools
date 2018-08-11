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

package org.drools.workbench.screens.scenariosimulation.client.factories;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioSimulationViewProvider.newScenarioGridPanel;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationViewProviderTest {

    @Test
    public void newScenarioSimulationViewTest() {
        final ScenarioGridLayer scenarioGridLayer = new ScenarioGridLayer();
        final ScenarioGridPanel scenarioGridPanel = newScenarioGridPanel(scenarioGridLayer);
        assertNotNull(scenarioGridPanel);
        DefaultGridLayer defaultGridLayer = scenarioGridPanel.getDefaultGridLayer();
        assertNotNull(defaultGridLayer);
        assertTrue(defaultGridLayer instanceof ScenarioGridLayer);
        ScenarioGrid scenarioGrid = scenarioGridPanel.getScenarioGrid();
        assertNotNull(scenarioGrid);
    }
}