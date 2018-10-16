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

import java.util.Collections;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationGridPanelClickHandlerTest {

    protected final Double GRID_WIDTH = 100.0;
    protected final Double HEADER_HEIGHT = 10.0;
    protected final Double HEADER_ROW_HEIGHT = 10.0;
    protected final int UI_COLUMN_INDEX = 0;
    protected final int UI_ROW_INDEX = 1;
    protected final int CLICK_POINT_X = 5;
    protected final int CLICK_POINT_Y = 5;
    protected final boolean SHIFT_PRESSED = false;
    protected final boolean CTRL_PRESSED = false;
    protected final int OFFSET_X = 0;
    protected final int NATIVE_EVENT_CLIENT_X = 100;
    protected final int NATIVE_EVENT_CLIENT_Y = 100;
    protected final int TARGET_ABSOLUTE_LEFT = 50;
    protected final int TARGET_SCROLL_LEFT = 20;
    protected final int TARGET_ABSOLUTE_TOP = 50;
    protected final int TARGET_SCROLL_TOP = 20;
    protected final int DOCUMENT_SCROLL_LEFT = 10;
    protected final int DOCUMENT_SCROLL_TOP = 10;

    @Mock
    protected Point2D point2DMock;

    @Mock
    protected ScenarioGrid mockScenarioGrid;

    @Mock
    protected ScenarioGridCell scenarioGridCellMock;

    @Mock
    protected ScenarioHeaderMetaData headerMetaData;

    @Mock
    protected ContextMenuEvent mockContextMenuEvent;

    @Mock
    private ScenarioGridPanel mockScenarioGridPanel;

    @Mock
    private ScenarioGridModel scenarioGridModelMock;

    @Mock
    private GridRenderer scenarioGridRenderer;

    @Mock
    private BaseGridRendererHelper scenarioGridRendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation scenarioRenderingInformation;

    @Mock
    private Element mockTarget;

    @Mock
    private NativeEvent mockNativeEvent;

    @Mock
    private Document mockDocument;



    @Before
    public void setUp() throws Exception {
        doReturn(scenarioGridCellMock).when(scenarioGridModelMock).getCell(UI_ROW_INDEX, UI_COLUMN_INDEX);
        when(mockScenarioGridPanel.getScenarioGrid()).thenReturn(mockScenarioGrid);
        when(mockScenarioGrid.getWidth()).thenReturn(GRID_WIDTH);
        when(mockScenarioGrid.getModel()).thenReturn(scenarioGridModelMock);
        when(mockScenarioGrid.getRenderer()).thenReturn(scenarioGridRenderer);
        when(mockScenarioGrid.getRendererHelper()).thenReturn(scenarioGridRendererHelper);
        when(scenarioGridRenderer.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(scenarioGridRenderer.getHeaderRowHeight()).thenReturn(HEADER_ROW_HEIGHT);
        when(scenarioGridRendererHelper.getRenderingInformation()).thenReturn(scenarioRenderingInformation);

        // mock single column in grid
        ScenarioGridColumn column = mock(ScenarioGridColumn.class);
        when(scenarioGridModelMock.getColumns()).thenReturn(Collections.singletonList(column));
        when(scenarioGridModelMock.getColumnCount()).thenReturn(1);

        // presence of header metadata is prerequisite to handle header click
        // to simplify test, return just one header metadata
        // it simulates just one row in column header rows
        when(column.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaData));
        when(headerMetaData.getColumnGroup()).thenReturn(FactMappingType.GIVEN.name());

        // mock that column to index 0
        BaseGridRendererHelper.ColumnInformation columnInformation =
                new BaseGridRendererHelper.ColumnInformation(column, UI_COLUMN_INDEX, OFFSET_X);
        when(scenarioGridRendererHelper.getColumnInformation(CLICK_POINT_X)).thenReturn(columnInformation);

        when(mockNativeEvent.getClientX()).thenReturn(NATIVE_EVENT_CLIENT_X);
        when(mockNativeEvent.getClientY()).thenReturn(NATIVE_EVENT_CLIENT_Y);

        when(mockTarget.getOwnerDocument()).thenReturn(mockDocument);
        when(mockTarget.getAbsoluteLeft()).thenReturn(TARGET_ABSOLUTE_LEFT);
        when(mockTarget.getScrollLeft()).thenReturn(TARGET_SCROLL_LEFT);
        when(mockTarget.getAbsoluteTop()).thenReturn(TARGET_ABSOLUTE_TOP);
        when(mockTarget.getScrollTop()).thenReturn(TARGET_SCROLL_TOP);

        when(mockDocument.getScrollLeft()).thenReturn(DOCUMENT_SCROLL_LEFT);
        when(mockDocument.getScrollTop()).thenReturn(DOCUMENT_SCROLL_TOP);

        when(mockContextMenuEvent.getNativeEvent()).thenReturn(mockNativeEvent);
        when(mockContextMenuEvent.getRelativeElement()).thenReturn(mockTarget);
    }
}