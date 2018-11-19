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
import java.util.List;

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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.mockito.Mockito.doReturn;
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
    protected ScenarioGrid scenarioGridMock;

    @Mock
    protected ScenarioGridCell scenarioGridCellMock;

    @Mock
    protected ScenarioHeaderMetaData headerMetaDataMock;

    @Mock
    protected ContextMenuEvent contextMenuEventMock;

    @Mock
    protected ScenarioGridColumn scenarioGridColumnMock;

    @Mock
    protected List<GridColumn<?>> columnsMock;

    @Mock
    protected ScenarioGridModel scenarioGridModelMock;

    @Mock
    private ScenarioGridPanel scenarioGridPanelMock;

    @Mock
    private GridRenderer scenarioGridRendererMock;

    @Mock
    private BaseGridRendererHelper scenarioGridRendererHelperMock;

    @Mock
    private BaseGridRendererHelper.RenderingInformation scenarioRenderingInformationMock;

    @Mock
    private Element targetMock;

    @Mock
    private NativeEvent nativeEventMock;

    @Mock
    private Document documentMock;

    @Before
    public void setUp() throws Exception {
        doReturn(scenarioGridCellMock).when(scenarioGridModelMock).getCell(UI_ROW_INDEX, UI_COLUMN_INDEX);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridMock.getWidth()).thenReturn(GRID_WIDTH);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridMock.getRenderer()).thenReturn(scenarioGridRendererMock);
        when(scenarioGridMock.getRendererHelper()).thenReturn(scenarioGridRendererHelperMock);
        when(scenarioGridRendererMock.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(scenarioGridRendererMock.getHeaderRowHeight()).thenReturn(HEADER_ROW_HEIGHT);
        when(scenarioGridRendererHelperMock.getRenderingInformation()).thenReturn(scenarioRenderingInformationMock);

        // mock single column in grid
        when(scenarioGridModelMock.getHeaderRowCount()).thenReturn(1);
        doReturn(scenarioGridColumnMock).when(columnsMock).get(0);
        when(scenarioGridModelMock.getColumns()).thenReturn(columnsMock);
        when(scenarioGridModelMock.getColumnCount()).thenReturn(1);

        // presence of header metadata is prerequisite to handle header click
        // to simplify test, return just one header metadata
        // it simulates just one row in column header rows
        when(scenarioGridColumnMock.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaDataMock));
        when(headerMetaDataMock.getColumnGroup()).thenReturn(FactMappingType.GIVEN.name());

        // mock that column to index 0
        BaseGridRendererHelper.ColumnInformation columnInformation =
                new BaseGridRendererHelper.ColumnInformation(scenarioGridColumnMock, UI_COLUMN_INDEX, OFFSET_X);
        when(scenarioGridRendererHelperMock.getColumnInformation(CLICK_POINT_X)).thenReturn(columnInformation);

        when(nativeEventMock.getClientX()).thenReturn(NATIVE_EVENT_CLIENT_X);
        when(nativeEventMock.getClientY()).thenReturn(NATIVE_EVENT_CLIENT_Y);

        when(targetMock.getOwnerDocument()).thenReturn(documentMock);
        when(targetMock.getAbsoluteLeft()).thenReturn(TARGET_ABSOLUTE_LEFT);
        when(targetMock.getScrollLeft()).thenReturn(TARGET_SCROLL_LEFT);
        when(targetMock.getAbsoluteTop()).thenReturn(TARGET_ABSOLUTE_TOP);
        when(targetMock.getScrollTop()).thenReturn(TARGET_SCROLL_TOP);

        when(documentMock.getScrollLeft()).thenReturn(DOCUMENT_SCROLL_LEFT);
        when(documentMock.getScrollTop()).thenReturn(DOCUMENT_SCROLL_TOP);

        when(contextMenuEventMock.getNativeEvent()).thenReturn(nativeEventMock);
        when(contextMenuEventMock.getRelativeElement()).thenReturn(targetMock);
    }
}