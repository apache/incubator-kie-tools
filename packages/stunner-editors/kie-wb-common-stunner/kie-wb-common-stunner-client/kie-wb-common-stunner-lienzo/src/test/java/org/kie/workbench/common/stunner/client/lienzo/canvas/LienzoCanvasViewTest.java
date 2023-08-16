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


package org.kie.workbench.common.stunner.client.lienzo.canvas;

import java.util.function.BiFunction;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.util.CursorMap;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasViewTest {

    @Mock
    private LienzoLayer lienzoLayer;
    @Mock
    private Layer layer;
    @Mock
    private Layer topLayer;
    @Mock
    private IPrimitive<?> decorator;
    @Mock
    private LienzoPanel panel;
    @Mock
    private Style panelStyle;
    @Mock
    private CanvasSettings settings;

    private LienzoCanvasView tested;

    private BiFunction<Integer, Integer, IPrimitive<?>> decoratorFactory;

    @Before
    public void setUp() throws Exception {
        decoratorFactory = (integer, integer2) -> decorator;
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        Widget panelWidget = mock(Widget.class);
        Element panelElement = mock(Element.class);
        when(panel.asWidget()).thenReturn(panelWidget);
        when(panelWidget.getElement()).thenReturn(panelElement);
        when(panelElement.getStyle()).thenReturn(panelStyle);
        when(lienzoLayer.getTopLayer()).thenReturn(topLayer);
        when(panel.show(lienzoLayer)).thenReturn(panel);
        this.tested = new LienzoCanvasViewStub(decoratorFactory);
    }

    @Test
    public void testInitialize() {
        assertEquals(tested, tested.initialize(panel,
                                               settings));
        verify(panel, times(1)).show(eq(lienzoLayer));
        verify(panelStyle, times(1)).setBackgroundColor(eq(LienzoCanvasView.BG_COLOR));
        verify(topLayer, times(1)).add(eq(decorator));
    }

    @Test
    public void testSetGrid() {
        tested.initialize(panel, settings);
        tested.setGrid(CanvasGrid.DEFAULT_GRID);
        verify(panel, times(1)).setBackgroundLayer(any(Layer.class));
    }

    @Test
    public void testCursor() {
        LienzoBoundsPanel panelView = mock(LienzoBoundsPanel.class);
        when(panel.getView()).thenReturn(panelView);
        tested.initialize(panel, settings);
        tested.setCursor(AbstractCanvas.Cursors.MOVE);
        verify(panelView, times(1))
                .setCursor(eq(CursorMap.get().lookup(Style.Cursor.MOVE.getCssName())));
    }

    @Test
    public void testRemoveGrid() {
        tested.initialize(panel, settings);
        tested.setGrid(null);
        verify(panel, times(1)).setBackgroundLayer(eq(null));
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(lienzoLayer, times(1)).clear();
    }

    @Test
    public void testTransform() {
        Transform transform = mock(Transform.class);
        when(lienzoLayer.getTransform()).thenReturn(transform);
        assertEquals(transform, tested.getTransform());
    }

    @Test
    public void testDestroy() {
        tested.initialize(panel,
                          settings);
        tested.destroy();
        verify(lienzoLayer, times(1)).destroy();
    }

    public class LienzoCanvasViewStub extends LienzoCanvasView<LienzoLayer> {

        public LienzoCanvasViewStub(final BiFunction<Integer, Integer, IPrimitive<?>> decoratorFactory) {
            super(decoratorFactory);
        }

        @Override
        public LienzoLayer getLayer() {
            return lienzoLayer;
        }

        @Override
        public LienzoCanvasView addChild(ShapeView<?> parent, ShapeView<?> child) {
            return null;
        }

        @Override
        public LienzoCanvasView deleteChild(ShapeView<?> parent, ShapeView<?> child) {
            return null;
        }

        @Override
        public LienzoCanvasView dock(ShapeView<?> parent, ShapeView<?> child) {
            return null;
        }

        @Override
        public LienzoCanvasView undock(ShapeView<?> childParent, ShapeView<?> child) {
            return null;
        }

        @Override
        public CanvasPanel getPanel() {
            return panel;
        }
    }
}
