/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.handlers;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class EditableHeaderGridWidgetEditCellMouseEventHandlerTest {

    private static final int MOUSE_EVENT_X = 32;

    private static final int MOUSE_EVENT_Y = 64;

    private static final double GRID_COMPUTED_LOCATION_X = 100.0;

    private static final double GRID_COMPUTED_LOCATION_Y = 200.0;

    @Mock
    private BaseGrid gridWidget;

    @Mock
    private Group gridWidgetHeader;

    @Mock
    private GridColumn gridColumn;

    @Mock
    private Viewport viewport;

    @Mock
    private GridRenderer renderer;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private BaseGridRendererHelper.RenderingInformation renderingInformation;

    @Mock
    private BaseGridRendererHelper.ColumnInformation columnInformation;

    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation renderingBlockInformation;

    @Mock
    private EditableHeaderMetaData editableHeaderMetaData;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Captor
    private ArgumentCaptor<GridBodyCellEditContext> gridBodyCellEditContextCaptor;

    private NodeMouseClickEvent clickEvent;

    private NodeMouseDoubleClickEvent doubleClickEvent;

    private GridData uiModel;

    private EditableHeaderGridWidgetEditCellMouseEventHandler handler;

    private Point2D relativeLocation = new Point2D(MOUSE_EVENT_X, MOUSE_EVENT_Y);

    private Point2D computedLocation = new Point2D(GRID_COMPUTED_LOCATION_X, GRID_COMPUTED_LOCATION_Y);

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.uiModel = new BaseGridData(false);
        this.uiModel.appendColumn(gridColumn);
        this.clickEvent = new NodeMouseClickEvent(mock(HTMLDivElement.class));
        this.doubleClickEvent = new NodeMouseDoubleClickEvent(mock(HTMLDivElement.class));

        when(gridWidget.getModel()).thenReturn(uiModel);
        when(gridWidget.getRendererHelper()).thenReturn(rendererHelper);
        when(gridWidget.getRenderer()).thenReturn(renderer);
        when(gridWidget.getHeader()).thenReturn(gridWidgetHeader);
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(gridWidget.getComputedLocation()).thenReturn(computedLocation);
        when(gridWidget.getWidth()).thenReturn((double) MOUSE_EVENT_X);

        when(rendererHelper.getRenderingInformation()).thenReturn(renderingInformation);
        when(rendererHelper.getColumnInformation(anyDouble())).thenReturn(columnInformation);
        when(columnInformation.getColumn()).thenReturn(gridColumn);

        when(renderer.getHeaderHeight()).thenReturn((double) MOUSE_EVENT_Y);
        when(renderer.getHeaderRowHeight()).thenReturn((double) MOUSE_EVENT_Y);
        when(renderingInformation.getAllColumns()).thenReturn(uiModel.getColumns());
        when(renderingInformation.getBodyBlockInformation()).thenReturn(renderingBlockInformation);
        when(renderingInformation.getFloatingBlockInformation()).thenReturn(renderingBlockInformation);

        when(editableHeaderMetaData.getSupportedEditAction()).thenReturn(GridCellEditAction.SINGLE_CLICK);

        this.handler = new EditableHeaderGridWidgetEditCellMouseEventHandler();
    }

    @Test
    public void testOnNodeMouseEventWhenOnlyVisualChangeAllowed() {
        when(gridWidget.isOnlyVisualChangeAllowed()).thenReturn(true);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.empty(),
                                            clickEvent)).isFalse();

        verify(gridWidget, never()).startEditingCell(any(Point2D.class));
        verify(editableHeaderMetaData, never()).edit(any(GridBodyCellEditContext.class));
    }

    @Test
    public void testOnNodeMouseEventWhenIsReadOnlyDiagram() {
        when(gridWidget.isOnlyVisualChangeAllowed()).thenReturn(false);
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.empty(),
                                            clickEvent)).isFalse();

        verify(gridWidget, never()).startEditingCell(any(Point2D.class));
        verify(editableHeaderMetaData, never()).edit(any(GridBodyCellEditContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_NonEditableColumn() {
        assertThat(handler.handleHeaderCell(gridWidget,
                                            relativeLocation,
                                            0,
                                            0,
                                            clickEvent)).isFalse();

        verify(editableHeaderMetaData, never()).edit(any(GridBodyCellEditContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_EditableColumn_NotEditableRow() {
        when(gridColumn.getHeaderMetaData()).thenReturn(Collections.singletonList(new BaseHeaderMetaData("column")));

        assertThat(handler.handleHeaderCell(gridWidget,
                                            relativeLocation,
                                            0,
                                            0,
                                            clickEvent)).isFalse();

        verify(editableHeaderMetaData, never()).edit(any(GridBodyCellEditContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_EditableColumn_EditableRow_ClickEvent() {
        when(gridColumn.getHeaderMetaData()).thenReturn(Collections.singletonList(editableHeaderMetaData));

        uiModel.selectHeaderCell(0, 0);

        assertHeaderCellEdited(0);
    }

    @Test
    public void testHandleHeaderCell_EditableMergedColumns_EditableRow_Column0_ClickEvent() {
        setupAdditionalColumn();

        assertHeaderCellEdited(0);
    }

    @Test
    public void testHandleHeaderCell_EditableMergedColumns_EditableRow_Column1_ClickEvent() {
        setupAdditionalColumn();

        assertHeaderCellEdited(1);
    }

    private void setupAdditionalColumn() {
        final GridColumn additionalGridColumn = mock(GridColumn.class);
        uiModel.appendColumn(additionalGridColumn);
        when(gridColumn.getHeaderMetaData()).thenReturn(Collections.singletonList(editableHeaderMetaData));
        when(gridColumn.getIndex()).thenReturn(0);
        when(additionalGridColumn.getHeaderMetaData()).thenReturn(Collections.singletonList(editableHeaderMetaData));
        when(additionalGridColumn.getIndex()).thenReturn(1);

        uiModel.selectHeaderCell(0, 0);
        uiModel.selectHeaderCell(0, 1);
    }

    private void assertHeaderCellEdited(final int uiHeaderColumnIndex) {
        assertThat(handler.handleHeaderCell(gridWidget,
                                            relativeLocation,
                                            0,
                                            uiHeaderColumnIndex,
                                            clickEvent)).isTrue();

        verify(editableHeaderMetaData).edit(gridBodyCellEditContextCaptor.capture());

        final GridBodyCellEditContext gridBodyCellEditContext = gridBodyCellEditContextCaptor.getValue();
        assertThat(gridBodyCellEditContext).isNotNull();
        assertThat(gridBodyCellEditContext.getRelativeLocation()).isPresent();

        final Point2D relativeLocation = gridBodyCellEditContext.getRelativeLocation().get();
        assertThat(relativeLocation.getX()).isEqualTo(MOUSE_EVENT_X + GRID_COMPUTED_LOCATION_X);
        assertThat(relativeLocation.getY()).isEqualTo(MOUSE_EVENT_Y + GRID_COMPUTED_LOCATION_Y);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleHeaderCell_EditableColumn_EditableRow_DoubleClickEvent() {
        when(gridColumn.getHeaderMetaData()).thenReturn(Collections.singletonList(editableHeaderMetaData));

        uiModel.selectHeaderCell(0, 0);

        assertThat(handler.handleHeaderCell(gridWidget,
                                            relativeLocation,
                                            0,
                                            0,
                                            doubleClickEvent)).isFalse();

        verify(editableHeaderMetaData, never()).edit(any(GridBodyCellEditContext.class));
    }
}
