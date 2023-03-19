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
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_ONE_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_TWO_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_HEIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_ROW_HEIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.UI_COLUMN_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridHeaderUtilitiesTest extends AbstractScenarioSimulationTest {

    private ScenarioGridColumn scenarioGridColumnOne;
    private ScenarioGridColumn scenarioGridColumnTwo;

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

    @Mock
    private ScenarioHeaderMetaData clickedScenarioHeaderMetadataMock;

    private Point2D rp = new Point2D(0, 0);

    @Before
    public void setup() {
        super.setup();
        doReturn(gridRenderer).when(scenarioGridMock).getRenderer();
        doReturn(gridRendererHelper).when(scenarioGridMock).getRendererHelper();
        doReturn(ri).when(gridRendererHelper).getRenderingInformation();
        doReturn(HEADER_HEIGHT).when(gridRenderer).getHeaderHeight();
        doReturn(HEADER_ROW_HEIGHT).when(gridRenderer).getHeaderRowHeight();

        doReturn(floatingBlockInformation).when(ri).getFloatingBlockInformation();
        doReturn(0.0).when(floatingBlockInformation).getX();
        doReturn(0.0).when(floatingBlockInformation).getWidth();

        doReturn(mock(Viewport.class)).when(scenarioGridMock).getViewport();
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);

        final ScenarioHeaderMetaData clickedScenarioHeaderMetadataMock = mock(ScenarioHeaderMetaData.class);
        scenarioGridColumnOne =
                mockGridColumn(100.0,
                               Collections.singletonList(clickedScenarioHeaderMetadataMock),
                               COLUMN_ONE_TITLE,
                               COLUMN_GROUP);
        scenarioGridColumnTwo =
                mockGridColumn(100.0,
                               Collections.singletonList(clickedScenarioHeaderMetadataMock),
                               COLUMN_TWO_TITLE,
                               COLUMN_GROUP);
        gridColumns.add(scenarioGridColumnOne);
        gridColumns.add(scenarioGridColumnTwo);
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

        final GridBodyCellRenderContext context = CellContextUtilities.makeHeaderCellRenderContext(scenarioGridMock,
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
    public void testEnableTestToolsEventInstanceNotAssigned() {
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(false);

        final EnableTestToolsEvent event = ScenarioSimulationGridHeaderUtilities.getEnableTestToolsEvent(scenarioGridMock,
                                                                                                         scenarioGridColumnOne,
                                                                                                         clickedScenarioHeaderMetadataMock,
                                                                                                         UI_COLUMN_INDEX,
                                                                                                         COLUMN_GROUP);

        assertThat(event.getFilterTerm()).isEqualTo(COLUMN_ONE_TITLE + ";" + COLUMN_TWO_TITLE + ";" + MULTIPART_VALUE);
        assertThat(event.isNotEqualsSearch()).isTrue();
    }

    @Test
    public void testEnableTestToolsEventInstanceAssigned() {
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);

        final EnableTestToolsEvent event = ScenarioSimulationGridHeaderUtilities.getEnableTestToolsEvent(scenarioGridMock,
                                                                                                         scenarioGridColumnOne,
                                                                                                         clickedScenarioHeaderMetadataMock,
                                                                                                         UI_COLUMN_INDEX,
                                                                                                         COLUMN_GROUP);

        assertThat(event.getFilterTerm()).isEqualTo(COLUMN_ONE_TITLE);
        assertThat(event.isNotEqualsSearch()).isFalse();
    }

    @Test
    public void testEnableTestToolsEventPropertyHeaderPropertyNotAssigned() {
        when(clickedScenarioHeaderMetadataMock.getMetadataType()).thenReturn(ScenarioHeaderMetaData.MetadataType.PROPERTY);
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);

        final EnableTestToolsEvent event = ScenarioSimulationGridHeaderUtilities.getEnableTestToolsEvent(scenarioGridMock,
                                                                                                         scenarioGridColumnOne,
                                                                                                         clickedScenarioHeaderMetadataMock,
                                                                                                         UI_COLUMN_INDEX,
                                                                                                         COLUMN_GROUP);

        assertThat(event.getFilterTerm()).isEqualTo(COLUMN_ONE_TITLE);
        assertThat(event.getPropertyNameElements()).isNull();
        assertThat(event.isNotEqualsSearch()).isFalse();
    }

    @Test
    public void testIsHeaderEditableNorInstanceNorPropertyAssigned() {
        final boolean result = ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumnOne, 0);

        assertThat(result).as("Nor Instance nor Property assigned").isFalse();
    }

    @Test
    public void testIsHeaderEditableInstanceAssignedAndSelected() {
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);

        final ScenarioHeaderMetaData metaDataMock = (ScenarioHeaderMetaData) scenarioGridColumnOne.getHeaderMetaData().get(0);
        when(metaDataMock.isInstanceHeader()).thenReturn(true);

        final boolean result = ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumnOne, 0);

        assertThat(result).as("Instance Assigned and selected").isTrue();
    }

    @Test
    public void testIsHeaderEditableInstanceAssignedButPropertySelected() {
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);

        final ScenarioHeaderMetaData metaDataMock = (ScenarioHeaderMetaData) scenarioGridColumnOne.getHeaderMetaData().get(0);
        when(metaDataMock.isPropertyHeader()).thenReturn(true);

        final boolean result = ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumnOne, 0);

        assertThat(result).as("Instance Assigned but Property selected").isFalse();
    }

    @Test
    public void testIsHeaderEditableInstanceAndPropertyAssignedButNotSelected() {
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);
        when(scenarioGridColumnOne.isPropertyAssigned()).thenReturn(true);

        final ScenarioHeaderMetaData metaDataMock = (ScenarioHeaderMetaData) scenarioGridColumnOne.getHeaderMetaData().get(0);
        when(metaDataMock.isPropertyHeader()).thenReturn(false);
        when(metaDataMock.isInstanceHeader()).thenReturn(false);

        final boolean result = ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumnOne, 0);

        assertThat(result).as("Instance and Property Assigned but not selected").isFalse();
    }

    @Test
    public void testIsHeaderEditableInstanceAndPropertyAssignedAndPropertySelected() {
        when(scenarioGridColumnOne.isInstanceAssigned()).thenReturn(true);
        when(scenarioGridColumnOne.isPropertyAssigned()).thenReturn(true);

        final ScenarioHeaderMetaData metaDataMock = (ScenarioHeaderMetaData) scenarioGridColumnOne.getHeaderMetaData().get(0);
        when(metaDataMock.isPropertyHeader()).thenReturn(true);

        final boolean result = ScenarioSimulationGridHeaderUtilities.isEditableHeader(scenarioGridColumnOne, 0);

        assertThat(result).as("Instance and Property Assigned and Property selected").isTrue();
    }

    @Test
    public void testHeaderMetadataInstanceOf() {
        final ScenarioGridColumn invalidColumn = mockGridColumn(100,
                                                                Collections.singletonList(mock(GridColumn.HeaderMetaData.class)));

        assertThatThrownBy(() -> ScenarioSimulationGridHeaderUtilities.isEditableHeader(invalidColumn,
                                                                                        0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Header metadata has to be an instance of ScenarioHeaderMetaData");
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
                                              final String COLUMN_GROUP) {
        final ScenarioGridColumn uiColumn = mock(ScenarioGridColumn.class);

        doReturn(headerMetaData).when(uiColumn).getHeaderMetaData();
        doReturn(width).when(uiColumn).getWidth();

        final ScenarioHeaderMetaData informationHeader = mock(ScenarioHeaderMetaData.class);
        when(informationHeader.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(informationHeader.getTitle()).thenReturn(columnTitle);

        when(uiColumn.getInformationHeaderMetaData()).thenReturn(informationHeader);
        when(uiColumn.isEditableHeaders()).thenReturn(true);

        return uiColumn;
    }
}