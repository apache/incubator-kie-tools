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

package org.drools.workbench.screens.scenariosimulation.client.utils;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationUtilsTest {

    @Mock
    private ScenarioGridPanel mockScenarioGridPanel;

    @Mock
    private ScenarioGridLayer mockScenarioGridLayer;

    private final String COLUMN_ID = "COLUMN ID";

    private final String COLUMN_TITLE = "COLUMN TITLE";

    private final String COLUMN_GROUP = "COLUMN GROUP";

    @Test
    public void getScenarioGridColumn() {
        ScenarioGridColumn scenarioGridColumn = ScenarioSimulationUtils.getScenarioGridColumn(COLUMN_ID, COLUMN_TITLE, COLUMN_GROUP, mockScenarioGridPanel, mockScenarioGridLayer);
        assertNotNull(scenarioGridColumn);
        List<GridColumn.HeaderMetaData> headerMetaData = scenarioGridColumn.getHeaderMetaData();
        assertNotNull(headerMetaData);
        assertEquals(2, headerMetaData.size());
        // Top-level header should have COLUMN_GROUP as title, and "" as column group
        assertEquals(COLUMN_GROUP, headerMetaData.get(0).getTitle());
        assertEquals("", headerMetaData.get(0).getColumnGroup());
        // Column header should have COLUMN_TITLE as title, and COLUMN_GROUP as column group
        assertEquals(COLUMN_TITLE, headerMetaData.get(1).getTitle());
        assertEquals(COLUMN_GROUP, headerMetaData.get(1).getColumnGroup());
    }

    @Test
    public void getScenarioGridColumn1() {
        ScenarioGridColumn scenarioGridColumn = ScenarioSimulationUtils.getScenarioGridColumn(COLUMN_ID, COLUMN_TITLE, mockScenarioGridPanel, mockScenarioGridLayer);
        assertNotNull(scenarioGridColumn);
        List<GridColumn.HeaderMetaData> headerMetaData = scenarioGridColumn.getHeaderMetaData();
        assertNotNull(headerMetaData);
        // Column header should have COLUMN_TITLE as title, and "" as column group
        assertEquals(1, headerMetaData.size());
        assertEquals("", headerMetaData.get(0).getColumnGroup());
        assertEquals(COLUMN_TITLE, headerMetaData.get(0).getTitle());
    }
}