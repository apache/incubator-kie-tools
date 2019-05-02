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

import java.util.stream.IntStream;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COMPUTED_LOCATION_X;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COMPUTED_LOCATION_Y;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MOUSE_EVENT_X;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MOUSE_EVENT_Y;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationGridWidgetMouseEventHandlerTest extends AbstractScenarioSimulationGridHandlerTest {



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

    @Mock
    private GridCellEditAction gridCellEditActionMock;

    private NodeMouseClickEvent clickEvent;

    private NodeMouseDoubleClickEvent doubleClickEvent;

    private ScenarioSimulationGridWidgetMouseEventHandler handler;

    private Point2D relativeLocation = new Point2D(MOUSE_EVENT_X, MOUSE_EVENT_Y);

    private Point2D computedLocation = new Point2D(GRID_COMPUTED_LOCATION_X, GRID_COMPUTED_LOCATION_Y);

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        super.setUp();
        this.clickEvent = new NodeMouseClickEvent(nativeClickEvent);
        this.doubleClickEvent = new NodeMouseDoubleClickEvent(nativeDoubleClickEvent);
        when(scenarioGridMock.getRendererHelper()).thenReturn(rendererHelper);
        when(scenarioGridMock.getViewport()).thenReturn(viewportMock);
        when(scenarioGridMock.getComputedLocation()).thenReturn(computedLocation);
        when(rendererHelper.getRenderingInformation()).thenReturn(renderingInformation);
        when(rendererHelper.getColumnInformation(anyDouble())).thenReturn(columnInformation);
        when(columnInformation.getColumn()).thenReturn((GridColumn) gridColumnMock);
        when(renderingInformation.getBodyBlockInformation()).thenReturn(renderingBlockInformation);
        when(renderingInformation.getFloatingBlockInformation()).thenReturn(renderingBlockInformation);
        when(renderingInformation.getAllColumns()).thenReturn(columnsMock);
        when(informationHeaderMetaDataMock.getSupportedEditAction()).thenReturn(GridCellEditAction.SINGLE_CLICK);
        this.handler = spy(new ScenarioSimulationGridWidgetMouseEventHandler());
    }

    @Test
    public void handleHeaderCell_NullColumn() {
        when(columnInformation.getColumn()).thenReturn(null);
        commonHandleHeaderCell(true, true, 1, false, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleHeaderCell_NonEditableColumn() {
        commonHandleHeaderCell(false, false, 1, false, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleHeaderCell_EditableColumn_NotStartEdit() {
        commonHandleHeaderCell(true, false, 1, true, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleHeaderCell_EditableColumn_StartEdit() {
        commonHandleHeaderCell(true, true, 1, true, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleHeaderCell_EditableColumn_WrongSelectedSize() {
        commonHandleHeaderCell(true, true, 3, false, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleBodyCell_NotEditSupportedLocal() {
        commonHandleBodyCell(false, true, 1, false, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleBodyCell_NotStartEditLocal() {
        commonHandleBodyCell(true, false, 1, true, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleBodyCell_StartEditLocal() {
        commonHandleBodyCell(true, true, 1, true, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleBodyCell_WrongSelectedSize() {
        commonHandleBodyCell(true, true, 3, false, false);
    }

    private void commonHandleHeaderCell(boolean editSupportedLocal, boolean startEditLocal, int selectedHeaderCellsSize, boolean startEditLocalCalled, boolean expectedResult) {
        int uiHeaderColumnIndex = 0;
        int uiHeaderRowIndex = 0;
        scenarioGridMock.getModel().getSelectedHeaderCells().clear();
        IntStream.range(0, selectedHeaderCellsSize).forEach(i -> scenarioGridMock.getModel().getSelectedHeaderCells().add(mock(GridData.SelectedCell.class)));
        doReturn(editSupportedLocal).when(handler).editSupportedLocal(any(), any());
        doReturn(startEditLocal).when(handler).startEditLocal(eq(scenarioGridMock), eq(uiHeaderColumnIndex), eq(gridColumnMock), eq(uiHeaderRowIndex), eq(true));
        if (expectedResult) {
            assertTrue(handler.handleHeaderCell(scenarioGridMock,
                                                relativeLocation,
                                                uiHeaderRowIndex,
                                                uiHeaderColumnIndex,
                                                clickEvent));
        } else {
            assertFalse(handler.handleHeaderCell(scenarioGridMock,
                                                 relativeLocation,
                                                 uiHeaderRowIndex,
                                                 uiHeaderColumnIndex,
                                                 clickEvent));
        }
        if (startEditLocalCalled) {
            verify(handler, times(1)).startEditLocal(eq(scenarioGridMock), eq(uiHeaderColumnIndex), eq(gridColumnMock), eq(uiHeaderRowIndex), eq(true));
        } else {
            verify(handler, never()).startEditLocal(eq(scenarioGridMock), eq(uiHeaderColumnIndex), eq(gridColumnMock), eq(uiHeaderRowIndex), eq(true));
        }
    }

    private void commonHandleBodyCell(boolean editSupportedLocal, boolean startEditLocal, int selectedCellsSize, boolean startEditLocalCalled, boolean expectedResult) {
        int uiHeaderColumnIndex = 0;
        int uiHeaderRowIndex = 0;
        scenarioGridMock.getModel().getSelectedCells().clear();
        IntStream.range(0, selectedCellsSize).forEach(i -> scenarioGridMock.getModel().getSelectedCells().add(mock(GridData.SelectedCell.class)));
        doReturn(editSupportedLocal).when(handler).editSupportedLocal(any(), any());
        doReturn(startEditLocal).when(handler).startEditLocal(eq(scenarioGridMock), eq(uiHeaderColumnIndex), eq(gridColumnMock), eq(uiHeaderRowIndex), eq(false));
        if (expectedResult) {
            assertTrue(handler.handleBodyCell(scenarioGridMock,
                                                relativeLocation,
                                                uiHeaderRowIndex,
                                                uiHeaderColumnIndex,
                                                clickEvent));
        } else {
            assertFalse(handler.handleBodyCell(scenarioGridMock,
                                                 relativeLocation,
                                                 uiHeaderRowIndex,
                                                 uiHeaderColumnIndex,
                                                 clickEvent));
        }
        if (startEditLocalCalled) {
            verify(handler, times(1)).startEditLocal(eq(scenarioGridMock), eq(uiHeaderColumnIndex), eq(gridColumnMock), eq(uiHeaderRowIndex), eq(false));
        } else {
            verify(handler, never()).startEditLocal(eq(scenarioGridMock), eq(uiHeaderColumnIndex), eq(gridColumnMock), eq(uiHeaderRowIndex), eq(false));
        }
    }

}
