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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.junit.Before;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationGridHandlerTest {

    protected final Double GRID_WIDTH = 100.0;
    protected final Double HEADER_HEIGHT = 10.0;
    protected final Double HEADER_ROW_HEIGHT = 10.0;
    protected final int UI_COLUMN_INDEX = 0;
    protected final int UI_ROW_INDEX = 1;
    protected final double CLICK_POINT_X = 5;
    protected final double CLICK_POINT_Y = 5;
    protected final int OFFSET_X = 0;

    @Mock
    protected Point2D point2DMock;

    @Mock
    private Viewport viewport;

    @Mock
    protected ScenarioGrid scenarioGridMock;

    @Mock
    protected ScenarioGridCell scenarioGridCellMock;

    @Mock
    protected ScenarioHeaderMetaData headerMetaDataMock;

    @Mock
    protected ScenarioHeaderMetaData informatioHeaderMetaDataMock;

    @Mock
    protected ContextMenuEvent contextMenuEventMock;

    @Mock
    protected ScenarioGridColumn scenarioGridColumnMock;

    protected List<GridColumn<?>> columnsMock;

    @Mock
    protected ScenarioGridModel scenarioGridModelMock;

    @Mock
    private ScenarioGridPanel scenarioGridPanelMock;

    @Mock
    private GridRenderer scenarioGridRendererMock;

    @Mock
    protected BaseGridRendererHelper scenarioGridRendererHelperMock;

    @Mock
    private BaseGridRendererHelper.RenderingInformation scenarioRenderingInformationMock;

    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformationMock;

    @Before
    public void setUp() throws Exception {
        doReturn(scenarioGridCellMock).when(scenarioGridModelMock).getCell(UI_ROW_INDEX, UI_COLUMN_INDEX);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridMock.getWidth()).thenReturn(GRID_WIDTH);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridMock.getRenderer()).thenReturn(scenarioGridRendererMock);
        when(scenarioGridMock.getRendererHelper()).thenReturn(scenarioGridRendererHelperMock);
        when(scenarioGridMock.getViewport()).thenReturn(viewport);
        when(scenarioGridRendererMock.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(scenarioGridRendererMock.getHeaderRowHeight()).thenReturn(HEADER_ROW_HEIGHT);
        when(scenarioGridRendererHelperMock.getRenderingInformation()).thenReturn(scenarioRenderingInformationMock);

        // mock single column in grid
        when(scenarioGridModelMock.getHeaderRowCount()).thenReturn(1);
        columnsMock = Arrays.asList(scenarioGridColumnMock, scenarioGridColumnMock);
        when(scenarioGridModelMock.getColumns()).thenReturn(columnsMock);
        when(scenarioGridModelMock.getColumnCount()).thenReturn(2);

        // presence of header metadata is prerequisite to handle header click
        // to simplify test, return just one header metadata
        // it simulates just one row in column header rows
        when(scenarioGridColumnMock.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaDataMock));
        when(scenarioGridColumnMock.getInformationHeaderMetaData()).thenReturn(headerMetaDataMock);
        when(headerMetaDataMock.getColumnGroup()).thenReturn(FactMappingType.GIVEN.name());

        when(scenarioGridColumnMock.getInformationHeaderMetaData()).thenReturn(informatioHeaderMetaDataMock);
        when(informatioHeaderMetaDataMock.getTitle()).thenReturn("test1");
        when(informatioHeaderMetaDataMock.getColumnGroup()).thenReturn(FactMappingType.GIVEN.name());

        // mock that column to index 0
        BaseGridRendererHelper.ColumnInformation columnInformation =
                new BaseGridRendererHelper.ColumnInformation(scenarioGridColumnMock, UI_COLUMN_INDEX, OFFSET_X);

        when(scenarioGridRendererHelperMock.getColumnInformation(CLICK_POINT_X)).thenReturn(columnInformation);
    }
}