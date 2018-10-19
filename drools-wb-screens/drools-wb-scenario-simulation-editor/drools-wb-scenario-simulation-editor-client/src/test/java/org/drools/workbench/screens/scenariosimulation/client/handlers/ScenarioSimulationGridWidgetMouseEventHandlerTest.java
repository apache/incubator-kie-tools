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

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridWidgetMouseEventHandlerTest extends AbstractScenarioSimulationGridHandlerTest {

    private static final int MOUSE_EVENT_X = 32;

    private static final int MOUSE_EVENT_Y = 64;

    private static final double GRID_COMPUTED_LOCATION_X = 100.0;

    private static final double GRID_COMPUTED_LOCATION_Y = 200.0;

    @Mock
    private Viewport viewport;

    @Mock
    private MouseEvent nativeClickEvent;

    @Mock
    private DoubleClickEvent nativeDoubleClickEvent;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation renderingInformation;

    @Mock
    private BaseGridRendererHelper.ColumnInformation columnInformation;

    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation renderingBlockInformation;

    @Captor
    private ArgumentCaptor<GridBodyCellEditContext> gridBodyCellEditContextCaptor;

    private NodeMouseClickEvent clickEvent;

    private NodeMouseDoubleClickEvent doubleClickEvent;

    private ScenarioSimulationGridWidgetMouseEventHandler handler;

    private Point2D relativeLocation = new Point2D(MOUSE_EVENT_X, MOUSE_EVENT_Y);

    private Point2D computedLocation = new Point2D(GRID_COMPUTED_LOCATION_X, GRID_COMPUTED_LOCATION_Y);

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setUp();

        this.clickEvent = new NodeMouseClickEvent(nativeClickEvent);
        this.doubleClickEvent = new NodeMouseDoubleClickEvent(nativeDoubleClickEvent);

        when(scenarioGridMock.getRendererHelper()).thenReturn(rendererHelper);
        when(scenarioGridMock.getViewport()).thenReturn(viewport);
        when(scenarioGridMock.getComputedLocation()).thenReturn(computedLocation);
        when(rendererHelper.getRenderingInformation()).thenReturn(renderingInformation);
        when(rendererHelper.getColumnInformation(anyDouble())).thenReturn(columnInformation);
        when(columnInformation.getColumn()).thenReturn((GridColumn) scenarioGridColumnMock);
        when(renderingInformation.getBodyBlockInformation()).thenReturn(renderingBlockInformation);
        when(renderingInformation.getFloatingBlockInformation()).thenReturn(renderingBlockInformation);
        when(headerMetaDataMock.getSupportedEditAction()).thenReturn(GridCellEditAction.SINGLE_CLICK);

        this.handler = new ScenarioSimulationGridWidgetMouseEventHandler();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_NonEditableColumn() {
        assertFalse(handler.handleHeaderCell(scenarioGridMock,
                                             relativeLocation,
                                             0,
                                             0,
                                             clickEvent));

        verify(headerMetaDataMock, never()).edit(any(GridBodyCellEditContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_EditableColumn_NotEditableRow() {
        when(scenarioGridColumnMock.getHeaderMetaData()).thenReturn(Collections.singletonList(new BaseHeaderMetaData("column")));

        assertFalse(handler.handleHeaderCell(scenarioGridMock,
                                             relativeLocation,
                                             0,
                                             0,
                                             clickEvent));

        verify(headerMetaDataMock, never()).edit(any(GridBodyCellEditContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_EditableColumn_EditableRow_ClickEvent() {
        when(scenarioGridColumnMock.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaDataMock));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.singletonList(mock(GridData.SelectedCell.class)));

        assertTrue(handler.handleHeaderCell(scenarioGridMock,
                                            relativeLocation,
                                            0,
                                            0,
                                            clickEvent));

        verify(headerMetaDataMock).edit(gridBodyCellEditContextCaptor.capture());

        final GridBodyCellEditContext gridBodyCellEditContext = gridBodyCellEditContextCaptor.getValue();
        assertNotNull(gridBodyCellEditContext);
        assertTrue(gridBodyCellEditContext.getRelativeLocation().isPresent());

        final Point2D relativeLocation = gridBodyCellEditContext.getRelativeLocation().get();
        assertEquals(MOUSE_EVENT_X + GRID_COMPUTED_LOCATION_X, relativeLocation.getX(), 0.0);
        assertEquals(MOUSE_EVENT_Y + GRID_COMPUTED_LOCATION_Y, relativeLocation.getY(), 0.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_EditableColumn_EditableRow_DoubleClickEvent() {
        when(scenarioGridColumnMock.getHeaderMetaData()).thenReturn(Collections.singletonList(headerMetaDataMock));

        scenarioGridModelMock.selectHeaderCell(0, 0);

        assertFalse(handler.handleHeaderCell(scenarioGridMock,
                                             relativeLocation,
                                             0,
                                             0,
                                             doubleClickEvent));

        verify(headerMetaDataMock, never()).edit(any(GridBodyCellEditContext.class));
    }
}
