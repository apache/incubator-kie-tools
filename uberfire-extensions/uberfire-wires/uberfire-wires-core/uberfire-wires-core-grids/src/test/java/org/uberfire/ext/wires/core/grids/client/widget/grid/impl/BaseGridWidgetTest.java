/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.List;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridWidgetTest {

    private BaseGridWidget gridWidget;

    private GridData model;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private CellSelectionManager cellSelectionManager;

    @Mock
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    @Before
    public void setup() {
        this.model = new BaseGridData();
        final BaseGridWidget wrapped = new BaseGridWidget(model,
                                                          selectionManager,
                                                          pinnedModeManager,
                                                          renderer) {
            @Override
            public CellSelectionManager getCellSelectionManager() {
                return cellSelectionManager;
            }
        };
        gridWidget = spy(wrapped);
    }

    @Test
    public void selectCellMouseClick() {
        final Point2D cp = new Point2D(10,
                                       20);
        gridWidget.selectCell(cp,
                              true,
                              false);

        final ArgumentCaptor<Point2D> pointArgumentCaptor = ArgumentCaptor.forClass(Point2D.class);

        verify(cellSelectionManager,
               times(1)).selectCell(pointArgumentCaptor.capture(),
                                    eq(true),
                                    eq(false));
        final Point2D point = pointArgumentCaptor.getValue();
        assertEquals(cp,
                     point);
    }

    @Test
    public void selectCellKeyboardNavigation() {
        gridWidget.selectCell(0,
                              1,
                              true,
                              false);
        verify(cellSelectionManager,
               times(1)).selectCell(eq(0),
                                    eq(1),
                                    eq(true),
                                    eq(false));
    }

    @Test
    public void adjustSelection() {
        gridWidget.adjustSelection(SelectionExtension.LEFT,
                                   false);

        verify(cellSelectionManager,
               times(1)).adjustSelection(eq(SelectionExtension.LEFT),
                                         eq(false));
    }

    @Test
    public void startEditingCellMouseClick() {
        final Point2D rp = new Point2D(10,
                                       20);

        gridWidget.startEditingCell(rp);

        final ArgumentCaptor<Point2D> pointArgumentCaptor = ArgumentCaptor.forClass(Point2D.class);

        verify(cellSelectionManager,
               times(1)).startEditingCell(pointArgumentCaptor.capture());
        final Point2D point = pointArgumentCaptor.getValue();
        assertEquals(rp,
                     point);
    }

    @Test
    public void startEditingCellKeyboardNavigation() {
        gridWidget.startEditingCell(0,
                                    1);

        verify(cellSelectionManager,
               times(1)).startEditingCell(eq(0),
                                          eq(1));
    }

    @Test
    public void testDefaultNodeMouseClickHandlers() {
        final List<NodeMouseEventHandler> handlers = gridWidget.getNodeMouseClickEventHandlers(selectionManager);

        assertThat(handlers).hasSize(3);
        assertThat(handlers.get(0)).isInstanceOf(DefaultGridWidgetCellSelectorMouseEventHandler.class);
        assertThat(handlers.get(1)).isInstanceOf(DefaultGridWidgetCollapsedCellMouseEventHandler.class);
        assertThat(handlers.get(2)).isInstanceOf(DefaultGridWidgetLinkedColumnMouseEventHandler.class);
    }

    @Test
    public void testDefaultNodeMouseDoubleClickHandlers() {
        final List<NodeMouseEventHandler> handlers = gridWidget.getNodeMouseDoubleClickEventHandlers(selectionManager,
                                                                                                     pinnedModeManager);

        assertThat(handlers).hasSize(2);
        assertThat(handlers.get(0)).isInstanceOf(DefaultGridWidgetEditCellMouseEventHandler.class);
        assertThat(handlers.get(1)).isInstanceOf(DefaultGridWidgetPinnedModeMouseEventHandler.class);
    }
}
