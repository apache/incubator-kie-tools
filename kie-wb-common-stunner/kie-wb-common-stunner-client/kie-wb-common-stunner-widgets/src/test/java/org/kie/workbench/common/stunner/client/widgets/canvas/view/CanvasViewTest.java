/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.canvas.view;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CanvasViewTest {

    @Mock
    com.ait.lienzo.client.widget.LienzoPanel panel;
    @Mock
    Scene scene;
    @Mock
    com.google.gwt.user.client.Element panelElement;
    @Mock
    Style panelStyle;
    @Mock
    org.kie.workbench.common.stunner.core.client.canvas.Layer layer;
    @Mock
    ShapeView shapeView;
    @Mock
    ShapeView parentShapeView;

    private CanvasView tested;

    @Before
    public void setup() throws Exception {
        when(panel.getScene()).thenReturn(scene);
        when(panel.getElement()).thenReturn(panelElement);
        when(panelElement.getStyle()).thenReturn(panelStyle);
        this.tested = new CanvasView();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShow() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        verify(layer,
               times(1)).initialize(any(Layer.class));
        verify(scene,
               times(1)).add(any(Layer.class));
        verify(panelStyle,
               times(1)).setBackgroundColor(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddShape() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        tested.addShape(shapeView);
        verify(layer,
               times(1)).addShape(eq(shapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveShape() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        tested.removeShape(shapeView);
        verify(layer,
               times(1)).removeShape(eq(shapeView));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddChild() {
        final WiresShape wiresShape = mock(WiresShape.class);
        final WiresShape parentWiresShape = mock(WiresShape.class);
        tested.addChildShape(parentWiresShape,
                             wiresShape);
        verify(parentWiresShape,
               times(1)).add(eq(wiresShape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveChild() {
        final WiresShape wiresShape = mock(WiresShape.class);
        final WiresShape parentWiresShape = mock(WiresShape.class);
        tested.removeChildShape(parentWiresShape,
                                wiresShape);
        verify(parentWiresShape,
               times(1)).remove(eq(wiresShape));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDock() {
        final WiresShape wiresShape = mock(WiresShape.class);
        final WiresShape parentWiresShape = mock(WiresShape.class);
        final WiresShapeControl wiresShapeControl = mock(WiresShapeControl.class);
        final WiresDockingControl dockingControl = mock(WiresDockingControl.class);
        final Point2D location = mock(Point2D.class);

        when(wiresShape.getControl()).thenReturn(wiresShapeControl);
        when(wiresShapeControl.getDockingControl()).thenReturn(dockingControl);
        when(wiresShape.getLocation()).thenReturn(location);

        tested.dockShape(parentWiresShape,
                         wiresShape);
        verify(dockingControl, times(1))
                .dock(wiresShape, parentWiresShape, location);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndock() {
        final WiresShape wiresShape = mock(WiresShape.class);
        final WiresShape targetWiresShape = mock(WiresShape.class);
        final WiresShapeControl wiresShapeControl = mock(WiresShapeControl.class);
        final WiresDockingControl dockingControl = mock(WiresDockingControl.class);

        when(wiresShape.getControl()).thenReturn(wiresShapeControl);
        when(wiresShapeControl.getDockingControl()).thenReturn(dockingControl);
        tested.undock(targetWiresShape, wiresShape);

        verify(dockingControl, times(1))
                .undock(wiresShape, targetWiresShape);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAbsX() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        when(panel.getAbsoluteLeft()).thenReturn(15);
        assertEquals(15,
                     tested.getAbsoluteX(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAbsY() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        when(panel.getAbsoluteTop()).thenReturn(15);
        assertEquals(15,
                     tested.getAbsoluteY(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWidth() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        when(panel.getWidth()).thenReturn(600);
        assertEquals(600,
                     tested.getWidth(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHeight() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        when(panel.getHeight()).thenReturn(600);
        assertEquals(600,
                     tested.getHeight(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCursor() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        tested.setCursor(AbstractCanvas.Cursors.AUTO);
        verify(panelStyle,
               times(1)).setCursor(eq(Style.Cursor.AUTO));
        tested.setCursor(AbstractCanvas.Cursors.MOVE);
        verify(panelStyle,
               times(1)).setCursor(eq(Style.Cursor.MOVE));
        tested.setCursor(AbstractCanvas.Cursors.TEXT);
        verify(panelStyle,
               times(1)).setCursor(eq(Style.Cursor.TEXT));
        tested.setCursor(AbstractCanvas.Cursors.POINTER);
        verify(panelStyle,
               times(1)).setCursor(eq(Style.Cursor.POINTER));
        tested.setCursor(AbstractCanvas.Cursors.WAIT);
        verify(panelStyle,
               times(1)).setCursor(eq(Style.Cursor.WAIT));
        tested.setCursor(AbstractCanvas.Cursors.CROSSHAIR);
        verify(panelStyle,
               times(1)).setCursor(eq(Style.Cursor.CROSSHAIR));
        tested.setCursor(AbstractCanvas.Cursors.NOT_ALLOWED);
        verify(panelStyle,
               times(1)).setProperty(eq(CanvasView.CURSOR),
                                     eq(CanvasView.CURSOR_NOT_ALLOWED));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        tested.clear();
        verify(layer,
               times(1)).clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.show(panel,
                    600,
                    400,
                    layer);
        tested.destroy();
        verify(layer,
               times(1)).destroy();
        verify(panel,
               times(1)).removeFromParent();
    }
}
