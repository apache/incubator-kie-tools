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


package org.kie.workbench.common.stunner.core.client.canvas.event.selection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasFocusedShapeEventTest {

    @Mock
    private Canvas canvas;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Shape shape;

    @Mock
    private ShapeView shapeView;

    private String uuid = "uuid";

    private CanvasFocusedShapeEvent event;

    @Before
    public void setup() {
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(shape.getShapeView()).thenReturn(shapeView);
        event = new CanvasFocusedShapeEvent(canvasHandler, uuid);
    }

    @Test
    public void testGetXWhenShapeIsNotNull() {

        when(shapeView.getShapeX()).thenReturn(170d);
        when(canvas.getShape(uuid)).thenReturn(shape);

        assertEquals(70, event.getX());
    }

    @Test
    public void testGetXWhenShapeIsNull() {

        when(canvas.getShape(uuid)).thenReturn(null);

        assertEquals(0, event.getX());
    }

    @Test
    public void testGetYWhenShapeIsNotNull() {

        when(shapeView.getShapeY()).thenReturn(170d);
        when(canvas.getShape(uuid)).thenReturn(shape);

        assertEquals(70, event.getY());
    }

    @Test
    public void testGetYWhenShapeIsNull() {

        when(canvas.getShape(uuid)).thenReturn(null);

        assertEquals(0, event.getY());
    }
}
