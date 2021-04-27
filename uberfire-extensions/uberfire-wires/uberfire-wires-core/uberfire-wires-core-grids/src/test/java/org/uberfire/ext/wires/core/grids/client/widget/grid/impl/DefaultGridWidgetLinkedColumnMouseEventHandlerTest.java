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

import java.util.Optional;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultGridWidgetLinkedColumnMouseEventHandlerTest extends BaseGridWidgetMouseClickHandlerTest {

    private DefaultGridWidgetLinkedColumnMouseEventHandler handler;

    @Before
    public void setup() {
        super.setup();

        final DefaultGridWidgetLinkedColumnMouseEventHandler wrapped = new DefaultGridWidgetLinkedColumnMouseEventHandler(selectionManager, renderer);
        handler = spy(wrapped);
    }

    @Test
    public void selectVisibleGridHeaderNonLinkedColumn() {
        when(gridWidget.isVisible()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        when(gridWidget.getLocation()).thenReturn(new Point2D(100,
                                                              100));
        when(uiColumn.isLinked()).thenReturn(false);

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation(uiColumn,
                                                                                                         0,
                                                                                                         0);
        when(helper.getColumnInformation(any(Double.class))).thenReturn(ci);

        handler.onNodeMouseEvent(gridWidget,
                                 relativeLocation,
                                 Optional.of(0),
                                 Optional.of(0),
                                 Optional.empty(),
                                 Optional.empty(),
                                 event);

        verify(handler,
               times(1)).handleHeaderCell(eq(gridWidget),
                                          eq(relativeLocation),
                                          eq(0),
                                          eq(0),
                                          eq(event));
        verify(selectionManager,
               never()).select(any(GridWidget.class));
        verify(selectionManager,
               never()).selectLinkedColumn(eq(uiColumn));
    }

    @Test
    public void selectVisibleGridHeaderLinkedColumn() {
        when(gridWidget.isVisible()).thenReturn(true);

        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(100);

        final Point2D computedLocation = new Point2D(100.0, 100.0);
        when(gridWidget.getComputedLocation()).thenReturn(computedLocation);
        when(uiColumn.isLinked()).thenReturn(true);
        when(uiColumn.getLink()).thenAnswer(invocation -> uiLinkedColumn);

        final BaseGridRendererHelper.ColumnInformation ci = new BaseGridRendererHelper.ColumnInformation(uiColumn,
                                                                                                         0,
                                                                                                         0);
        when(helper.getColumnInformation(any(Double.class))).thenReturn(ci);

        handler.onNodeMouseEvent(gridWidget,
                                 relativeLocation,
                                 Optional.of(0),
                                 Optional.of(0),
                                 Optional.empty(),
                                 Optional.empty(),
                                 event);

        verify(handler,
               times(1)).handleHeaderCell(eq(gridWidget),
                                          eq(relativeLocation),
                                          eq(0),
                                          eq(0),
                                          eq(event));
        verify(selectionManager,
               never()).select(any(GridWidget.class));
        verify(selectionManager,
               times(1)).selectLinkedColumn(eq(uiLinkedColumn));
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
