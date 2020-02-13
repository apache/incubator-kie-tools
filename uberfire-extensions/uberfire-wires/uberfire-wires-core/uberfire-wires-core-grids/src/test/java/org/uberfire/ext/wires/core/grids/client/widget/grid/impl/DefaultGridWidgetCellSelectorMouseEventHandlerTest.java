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

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultGridWidgetCellSelectorMouseEventHandlerTest extends BaseGridWidgetMouseClickHandlerTest {

    private DefaultGridWidgetCellSelectorMouseEventHandler handler;

    @Before
    public void setup() {
        super.setup();

        final DefaultGridWidgetCellSelectorMouseEventHandler wrapped = new DefaultGridWidgetCellSelectorMouseEventHandler(selectionManager);
        handler = spy(wrapped);
    }

    @Test
    public void checkHeaderSelectionDelegation() {
        when(gridWidget.selectHeaderCell(eq(relativeLocation), anyBoolean(), anyBoolean())).thenReturn(true);
        when(gridWidget.isSelected()).thenReturn(false);
        when(gridWidget.isVisible()).thenReturn(true);
        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(200);

        handler.onNodeMouseEvent(gridWidget,
                                 relativeLocation,
                                 Optional.of(0),
                                 Optional.of(1),
                                 Optional.empty(),
                                 Optional.empty(),
                                 event);

        verify(handler,
               times(1)).handleHeaderCell(eq(gridWidget),
                                          eq(relativeLocation),
                                          eq(0),
                                          eq(1),
                                          eq(event));
        verify(gridWidget,
               times(1)).selectHeaderCell(eq(relativeLocation),
                                          eq(false),
                                          eq(false));
        verify(layer).batch();
        verify(selectionManager).select(eq(gridWidget));
    }

    @Test
    public void checkBodySelectionDelegation() {
        when(gridWidget.selectCell(eq(relativeLocation), anyBoolean(), anyBoolean())).thenReturn(true);
        when(gridWidget.isSelected()).thenReturn(false);
        when(gridWidget.isVisible()).thenReturn(true);
        when(event.getX()).thenReturn(100);
        when(event.getY()).thenReturn(200);

        handler.onNodeMouseEvent(gridWidget,
                                 relativeLocation,
                                 Optional.empty(),
                                 Optional.empty(),
                                 Optional.of(0),
                                 Optional.of(1),
                                 event);

        verify(handler,
               times(1)).handleBodyCell(eq(gridWidget),
                                        eq(relativeLocation),
                                        eq(0),
                                        eq(1),
                                        eq(event));
        verify(gridWidget,
               times(1)).selectCell(eq(relativeLocation),
                                    eq(false),
                                    eq(false));
        verify(layer).batch();
        verify(selectionManager).select(eq(gridWidget));
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
