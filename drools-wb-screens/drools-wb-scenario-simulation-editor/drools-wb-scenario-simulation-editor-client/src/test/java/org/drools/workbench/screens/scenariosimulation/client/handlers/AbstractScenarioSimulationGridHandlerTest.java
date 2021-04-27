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
import java.util.List;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.junit.Before;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLICK_POINT_X;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_WIDTH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_HEIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_ROW_HEIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.OFFSET_X;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UI_COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UI_ROW_INDEX;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationGridHandlerTest extends AbstractScenarioSimulationTest {

    @Mock
    protected Point2D point2DMock;

    @Mock
    protected Viewport viewportMock;

    @Mock
    protected ScenarioGridCell scenarioGridCellMock;

    @Mock
    protected ContextMenuEvent contextMenuEventMock;

    protected List<GridColumn<?>> columnsMock;

    @Mock
    protected BaseGridRendererHelper scenarioGridRendererHelperMock;

    @Mock
    private BaseGridRendererHelper.RenderingInformation scenarioRenderingInformationMock;

    @Mock
    private GridRenderer scenarioGridRendererMock;

    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformationMock;

    @Before
    public void setUp() {
        super.setup();
        doReturn(scenarioGridCellMock).when(scenarioGridModelMock).getCell(UI_ROW_INDEX, UI_COLUMN_INDEX);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridMock.getWidth()).thenReturn(GRID_WIDTH);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridMock.getRenderer()).thenReturn(scenarioGridRendererMock);
        when(scenarioGridMock.getRendererHelper()).thenReturn(scenarioGridRendererHelperMock);
        when(scenarioGridMock.getViewport()).thenReturn(viewportMock);
        when(scenarioGridMock.getComputedLocation()).thenReturn(new Point2D(0.0, 0.0));
        when(scenarioGridRendererMock.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(scenarioGridRendererMock.getHeaderRowHeight()).thenReturn(HEADER_ROW_HEIGHT);
        when(scenarioGridRendererHelperMock.getRenderingInformation()).thenReturn(scenarioRenderingInformationMock);

        // mock single column in grid
        when(scenarioGridModelMock.getHeaderRowCount()).thenReturn(1);
        columnsMock = Arrays.asList(gridColumnMock, gridColumnMock);
        when(scenarioGridModelMock.getColumns()).thenReturn(columnsMock);
        when(scenarioGridModelMock.getColumnCount()).thenReturn(2);

        // mock that column to index 0
        BaseGridRendererHelper.ColumnInformation columnInformation =
                new BaseGridRendererHelper.ColumnInformation(gridColumnMock, UI_COLUMN_INDEX, OFFSET_X);
        when(scenarioGridRendererHelperMock.getColumnInformation(AdditionalMatchers.or(eq(0.0), eq(CLICK_POINT_X))))
                .thenReturn(columnInformation);
        when(scenarioRenderingInformationMock.getFloatingBlockInformation()).thenReturn(floatingBlockInformationMock);
    }
}