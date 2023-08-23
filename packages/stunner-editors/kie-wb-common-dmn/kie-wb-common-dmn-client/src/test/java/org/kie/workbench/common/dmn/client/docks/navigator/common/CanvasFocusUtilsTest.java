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

package org.kie.workbench.common.dmn.client.docks.navigator.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CanvasFocusUtilsTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private EventSourceMock<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    private CanvasFocusUtils canvasFocusUtils;

    @Before
    public void setup() {
        canvasFocusUtils = spy(new CanvasFocusUtils(dmnGraphUtils, canvasFocusedSelectionEvent, canvasSelectionEvent));
    }

    @Test
    public void testFocus() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Canvas canvas = mock(Canvas.class);
        final String uuid = "uuid";
        final CanvasSelectionEvent canvasSelection = new CanvasSelectionEvent(canvasHandler, uuid);
        final CanvasFocusedShapeEvent canvasFocusedShape = new CanvasFocusedShapeEvent(canvasHandler, uuid);

        when(dmnGraphUtils.getCanvasHandler()).thenReturn(canvasHandler);
        doReturn(canvasSelection).when(canvasFocusUtils).makeCanvasSelectionEvent(canvasHandler, uuid);
        doReturn(canvasFocusedShape).when(canvasFocusUtils).makeCanvasFocusedShapeEvent(canvasHandler, uuid);
        doReturn(canvas).when(canvasHandler).getCanvas();

        canvasFocusUtils.focus(uuid);

        verify(canvasSelectionEvent).fire(canvasSelection);
        verify(canvasFocusedSelectionEvent).fire(canvasFocusedShape);
        verify(canvas).focus();
    }
}
