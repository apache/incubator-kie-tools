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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.NUMBER_OF_COLUMNS;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.NUMBER_OF_ROWS;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.getHeadersMap;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.getRowsMap;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridTest {

    @Mock
    private ScenarioGridModel model;
    @Mock
    private ScenarioGridLayer scenarioGridLayer;
    @Mock
    private ScenarioGridRenderer renderer;
    @Mock
    private ScenarioGridPanel scenarioGridPanel;

    private ScenarioGrid scenarioGrid;

    @Before
    public void setup() {
        scenarioGrid = new ScenarioGrid(model, scenarioGridLayer, renderer, scenarioGridPanel);
    }

    @Test
    public void getGridMouseDoubleClickHandler() {
        NodeMouseDoubleClickHandler retrieved = scenarioGrid.getGridMouseDoubleClickHandler(mock(GridSelectionManager.class), mock(GridPinnedModeManager.class));
        assertNotNull(retrieved);
    }

    @Test
    public void setContent() {
        scenarioGrid.setContent(getHeadersMap(), getRowsMap());
        verify(model, times(NUMBER_OF_COLUMNS)).insertColumn(anyInt(), anyObject());
        verify(model, times(NUMBER_OF_ROWS)).insertRow(anyInt(), anyObject());
    }
}