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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableRightPanelEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.RenderContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridHeaderUtilitiesTest {

    private static final int HEADER_ROWS = 2;
    private static final double HEADER_HEIGHT = 50.0;
    private static final double HEADER_ROW_HEIGHT = HEADER_HEIGHT / HEADER_ROWS;

    @Mock
    private ScenarioGrid gridWidget;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private BaseGridRendererHelper gridRendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation ri;

    @Mock
    private BaseGridRendererHelper.ColumnInformation ci;

    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation;

    private ScenarioGridModel scenarioGridModel = new ScenarioGridModel();

    private Point2D rp = new Point2D(0, 0);

    @Before
    public void setup() {
        doReturn(gridRenderer).when(gridWidget).getRenderer();
        doReturn(gridRendererHelper).when(gridWidget).getRendererHelper();
        doReturn(ri).when(gridRendererHelper).getRenderingInformation();
        doReturn(HEADER_HEIGHT).when(gridRenderer).getHeaderHeight();
        doReturn(HEADER_ROW_HEIGHT).when(gridRenderer).getHeaderRowHeight();

        doReturn(floatingBlockInformation).when(ri).getFloatingBlockInformation();
        doReturn(0.0).when(floatingBlockInformation).getX();
        doReturn(0.0).when(floatingBlockInformation).getWidth();

        doReturn(mock(Viewport.class)).when(gridWidget).getViewport();
        scenarioGridModel.setHeaderRowCount(HEADER_ROWS);
        doReturn(scenarioGridModel).when(gridWidget).getModel();
    }

    @Test
    public void testMakeRenderContextNoBlock() {
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final GridColumn<?> uiColumn = mockGridColumn(100.0);
        allColumns.add(uiColumn);

        doReturn(allColumns).when(ri).getAllColumns();
        doReturn(uiColumn).when(ci).getColumn();
        doReturn(0.0).when(ci).getOffsetX();
        doReturn(0).when(ci).getUiColumnIndex();

        final GridBodyCellRenderContext context = RenderContextUtilities.makeRenderContext(gridWidget,
                                                                                           ri,
                                                                                           ci,
                                                                                           rp,
                                                                                           0);

        assertNotNull(context);
        assertEquals(0.0,
                     context.getAbsoluteCellX(),
                     0.0);
        assertEquals(100.0,
                     context.getCellWidth(),
                     0.0);
    }

    @Test
    public void testEnableRightPanelEventInstanceNotAssigned() {
        final Integer uiColumnIndex = 0;
        final String columnGroup = "col-group";
        final String columnOneTitle = "column one";
        final String columnTwoTitle = "column two";

        final ScenarioHeaderMetaData clickedScenarioHeaderMetadata = mock(ScenarioHeaderMetaData.class);
        final ScenarioGridColumn scenarioGridColumnOne =
                mockGridColumn(100.0,
                               Collections.singletonList(clickedScenarioHeaderMetadata),
                               columnOneTitle,
                               columnGroup);

        mockGridColumn(100.0,
                       Collections.singletonList(clickedScenarioHeaderMetadata),
                       columnTwoTitle,
                       columnGroup);

        final EnableRightPanelEvent event = ScenarioSimulationGridHeaderUtilities.getEnableRightPanelEvent(gridWidget,
                                                                                                           scenarioGridColumnOne,
                                                                                                           clickedScenarioHeaderMetadata,
                                                                                                           uiColumnIndex,
                                                                                                           columnGroup);

        Assertions.assertThat(event.getFilterTerm()).isEqualTo(columnOneTitle + ";" + columnTwoTitle);
        Assertions.assertThat(event.isNotEqualsSearch()).isTrue();
    }

    @Test
    public void testEnableRightPanelEventInstanceAssigned() {
        final Integer uiColumnIndex = 0;
        final String columnGroup = "col-group";
        final String columnOneTitle = "column one";
        final String columnTwoTitle = "column two";

        final ScenarioHeaderMetaData clickedScenarioHeaderMetadata = mock(ScenarioHeaderMetaData.class);
        final ScenarioGridColumn scenarioGridColumnOne =
                mockGridColumn(100.0,
                               Collections.singletonList(clickedScenarioHeaderMetadata),
                               columnOneTitle,
                               columnGroup);

        mockGridColumn(100.0,
                       Collections.singletonList(mock(ScenarioHeaderMetaData.class)),
                       columnTwoTitle,
                       columnGroup);

        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);

        final EnableRightPanelEvent event = ScenarioSimulationGridHeaderUtilities.getEnableRightPanelEvent(gridWidget,
                                                                                                           scenarioGridColumnOne,
                                                                                                           clickedScenarioHeaderMetadata,
                                                                                                           uiColumnIndex,
                                                                                                           columnGroup);

        Assertions.assertThat(event.getFilterTerm()).isEqualTo(columnOneTitle + ";" + columnTwoTitle);
        Assertions.assertThat(event.isNotEqualsSearch()).isTrue();
    }

    @Test
    public void testEnableRightPanelEventPropertyHeaderPropertyNotAssigned() {
        final Integer uiColumnIndex = 0;
        final String columnGroup = "col-group";
        final String columnOneTitle = "column one";

        final ScenarioHeaderMetaData clickedScenarioHeaderMetadata = mock(ScenarioHeaderMetaData.class);
        final ScenarioGridColumn scenarioGridColumnOne =
                mockGridColumn(100.0,
                               Collections.singletonList(clickedScenarioHeaderMetadata),
                               columnOneTitle,
                               columnGroup);

        when(clickedScenarioHeaderMetadata.isPropertyHeader()).thenReturn(true);
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);

        final EnableRightPanelEvent event = ScenarioSimulationGridHeaderUtilities.getEnableRightPanelEvent(gridWidget,
                                                                                                           scenarioGridColumnOne,
                                                                                                           clickedScenarioHeaderMetadata,
                                                                                                           uiColumnIndex,
                                                                                                           columnGroup);

        Assertions.assertThat(event.getFilterTerm()).isEqualTo(columnOneTitle);
        Assertions.assertThat(event.getPropertyName()).isNull();
        Assertions.assertThat(event.isNotEqualsSearch()).isFalse();
    }

    private ScenarioGridColumn mockGridColumn(final double width) {
        final List<GridColumn.HeaderMetaData> headerMetaData = new ArrayList<>();
        headerMetaData.add(mock(GridColumn.HeaderMetaData.class));
        headerMetaData.add(mock(GridColumn.HeaderMetaData.class));

        return mockGridColumn(width,
                              headerMetaData);
    }

    private ScenarioGridColumn mockGridColumn(final double width,
                                              final List<GridColumn.HeaderMetaData> headerMetaData) {
        return mockGridColumn(width, headerMetaData, null, null);
    }

    private ScenarioGridColumn mockGridColumn(final double width,
                                              final List<GridColumn.HeaderMetaData> headerMetaData,
                                              final String columnTitle,
                                              final String columnGroup) {
        final ScenarioGridColumn uiColumn = mock(ScenarioGridColumn.class);

        doReturn(headerMetaData).when(uiColumn).getHeaderMetaData();
        doReturn(width).when(uiColumn).getWidth();

        scenarioGridModel.appendColumn(uiColumn);

        final ScenarioHeaderMetaData informationHeader = mock(ScenarioHeaderMetaData.class);
        when(informationHeader.getColumnGroup()).thenReturn(columnGroup);
        when(informationHeader.getTitle()).thenReturn(columnTitle);

        when(uiColumn.getInformationHeaderMetaData()).thenReturn(informationHeader);

        return uiColumn;
    }
}