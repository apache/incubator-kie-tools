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

package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.junit.Before;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public abstract class AbstractCommandTest {

    @Mock
    protected ScenarioGridModel mockScenarioGridModel;

    @Mock
    protected ScenarioGridPanel mockScenarioGridPanel;
    @Mock
    protected ScenarioGridLayer mockScenarioGridLayer;
    @Mock
    protected ScenarioGrid mockScenarioGrid;

    @Mock
    protected RightPanelPresenter mockRightPanelPresenter;

    @Mock
    protected EventBus mockEventBus;

    @Mock
    protected List<ScenarioGridColumn> mockColumns;

    @Mock
    protected ScenarioGridColumn mockGridColumn;

    @Mock
    protected List<GridColumn.HeaderMetaData> mockHeaderMetaDatas;
    @Mock
    protected ScenarioHeaderMetaData mockHeaderMetaData;

    protected final String COLUMN_ID = "COLUMN ID";

    protected final String COLUMN_GROUP = FactMappingType.EXPECTED.name();

    protected final String FULL_PACKAGE = "test.scesim";

    protected final String VALUE = "VALUE";

    protected final String VALUE_CLASS_NAME = String.class.getName();

    protected final int ROW_INDEX = 2;
    protected final int COLUMN_INDEX = 3;

    protected final int FIRST_INDEX_LEFT = 2;
    protected final int FIRST_INDEX_RIGHT = 4;

    @Before
    public void setup() {
        when(mockHeaderMetaData.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(mockHeaderMetaDatas.get(1)).thenReturn(mockHeaderMetaData);
        when(mockGridColumn.getHeaderMetaData()).thenReturn(mockHeaderMetaDatas);
        when(mockGridColumn.getInformationHeaderMetaData()).thenReturn(mockHeaderMetaData);
        when(mockColumns.get(COLUMN_INDEX)).thenReturn(mockGridColumn);
        doReturn(mockColumns).when(mockScenarioGridModel).getColumns();
        when(mockScenarioGrid.getModel()).thenReturn(mockScenarioGridModel);
        when(mockScenarioGridLayer.getScenarioGrid()).thenReturn(mockScenarioGrid);
        when(mockScenarioGridPanel.getScenarioGridLayer()).thenReturn(mockScenarioGridLayer);
        when(mockScenarioGridModel.getFirstIndexLeftOfGroup(eq(COLUMN_GROUP))).thenReturn(FIRST_INDEX_LEFT);
        when(mockScenarioGridModel.getFirstIndexRightOfGroup(eq(COLUMN_GROUP))).thenReturn(FIRST_INDEX_RIGHT);
    }
}