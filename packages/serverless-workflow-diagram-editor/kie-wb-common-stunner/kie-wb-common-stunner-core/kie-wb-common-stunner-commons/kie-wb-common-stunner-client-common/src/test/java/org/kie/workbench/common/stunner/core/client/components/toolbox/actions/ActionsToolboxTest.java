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


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ActionsToolboxTest {

    private static final String E_UUID = "e1";
    private static final String ACTION1_TITLE = "action1";
    private static final String ACTION2_TITLE = "action2";

    @Mock
    private ToolboxAction<AbstractCanvasHandler> action1;

    @Mock
    private ToolboxAction<AbstractCanvasHandler> action2;

    @Mock
    private ActionsToolboxView<?> view;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Element<?> element;

    @Mock
    private Shape<?> shape;

    private ActionsToolbox<ActionsToolboxView<?>> tested;
    private ShapeGlyph glyph1;
    private ShapeGlyph glyph2;

    @Before
    public void setup() throws Exception {
        this.glyph1 = ShapeGlyph.create();
        this.glyph1.setDefinitionId("d1");
        this.glyph2 = ShapeGlyph.create();
        this.glyph2.setDefinitionId("d2");
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(element.getUUID()).thenReturn(E_UUID);
        when(canvas.getShape(eq(E_UUID))).thenReturn(shape);
        when(action1.getGlyph(eq(canvasHandler),
                              anyString())).thenReturn(glyph1);
        when(action1.getTitle(eq(canvasHandler),
                              anyString())).thenReturn(ACTION1_TITLE);
        when(action2.getGlyph(eq(canvasHandler),
                              anyString())).thenReturn(glyph2);
        when(action2.getTitle(eq(canvasHandler),
                              anyString())).thenReturn(ACTION2_TITLE);
        this.tested = new ActionsToolbox<>(() -> canvasHandler,
                                           element,
                                           view);
        this.tested.add(action1);
        this.tested.add(action2);
    }

    @Test
    public void testGetters() {
        assertEquals(E_UUID,
                     tested.getElementUUID());
        assertEquals(canvas,
                     tested.getCanvas());
        assertEquals(shape,
                     tested.getShape());
        assertEquals(2,
                     tested.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        tested.init();
        verify(view,
               times(1)).init(eq(tested));
        // TODO: fix properly.
        /*verify(view,
               times(1)).addButton(eq(glyph1),
                                   eq(ACTION1_TITLE),
                                   any(Consumer.class));
        verify(view,
               times(1)).addButton(eq(glyph2),
                                   eq(ACTION2_TITLE),
                                   any(Consumer.class));*/
    }

    @Test
    public void testShow() {
        tested.show();
        verify(view,
               times(1)).show();
    }

    @Test
    public void testShowOnlyOnce() {
        tested.show();
        tested.show();
        verify(view,
               times(1)).show();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(view,
               times(1)).hide();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(view,
               times(1)).destroy();
    }

    @Test
    public void testHideAndDestroy() {
        tested.hideAndDestroy();
        verify(view, times(1)).hideAndDestroy();
    }
}
