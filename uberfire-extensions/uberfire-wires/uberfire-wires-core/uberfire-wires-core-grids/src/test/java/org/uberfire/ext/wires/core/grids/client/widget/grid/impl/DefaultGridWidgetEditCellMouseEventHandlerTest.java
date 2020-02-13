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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultGridWidgetEditCellMouseEventHandlerTest extends BaseGridWidgetMouseDoubleClickHandlerTest {

    @Mock
    private GridCell gridCell;

    private DefaultGridWidgetEditCellMouseEventHandler handler;

    @Before
    public void setup() {
        super.setup();

        final DefaultGridWidgetEditCellMouseEventHandler wrapped = new DefaultGridWidgetEditCellMouseEventHandler();
        handler = spy(wrapped);
    }

    @Test
    public void testEditHeaderCell() {
        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.of(0),
                                            Optional.of(1),
                                            Optional.empty(),
                                            Optional.empty(),
                                            event)).isFalse();

        verify(uiModel, never()).selectHeaderCell(anyInt(), anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditBodyCellWithASelectedCell() {
        when(uiModel.getSelectedCells()).thenReturn(Collections.singletonList(new GridData.SelectedCell(0, 1)));
        when(uiModel.getCell(eq(0), eq(1))).thenReturn(gridCell);
        when(gridCell.getSupportedEditAction()).thenReturn(GridCellEditAction.DOUBLE_CLICK);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(1),
                                            event)).isFalse();

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(gridWidget).startEditingCell(eq(relativeLocation));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditBodyCellWithANullSelectedCell() {
        when(uiModel.getSelectedCells()).thenReturn(Collections.singletonList(new GridData.SelectedCell(0, 1)));
        when(uiModel.getCell(eq(0), eq(1))).thenReturn(null);

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(1),
                                            event)).isFalse();

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(gridWidget).startEditingCell(eq(relativeLocation));
    }

    @Test
    public void testEditBodyCellWithMultipleSelectedCells() {
        when(uiModel.getSelectedCells()).thenReturn(Arrays.asList(new GridData.SelectedCell(0, 0),
                                                                  new GridData.SelectedCell(0, 1)));

        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(1),
                                            event)).isFalse();

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(gridWidget, never()).startEditingCell(any(Point2D.class));
    }

    @Test
    public void testEditBodyCellWithoutASelectedCell() {
        assertThat(handler.onNodeMouseEvent(gridWidget,
                                            relativeLocation,
                                            Optional.empty(),
                                            Optional.empty(),
                                            Optional.of(0),
                                            Optional.of(1),
                                            event)).isFalse();

        verify(gridWidget, never()).startEditingCell(anyInt(), anyInt());
        verify(gridWidget, never()).startEditingCell(any(Point2D.class));
    }

    @Test
    public void checkOnNodeMouseEventDuringDragOperation() {
        doReturn(true).when(handler).isDNDOperationInProgress(eq(gridWidget));

        assertFalse(handler.onNodeMouseEvent(gridWidget,
                                             relativeLocation,
                                             Optional.empty(),
                                             Optional.empty(),
                                             Optional.of(0),
                                             Optional.of(1),
                                             event));
    }
}
