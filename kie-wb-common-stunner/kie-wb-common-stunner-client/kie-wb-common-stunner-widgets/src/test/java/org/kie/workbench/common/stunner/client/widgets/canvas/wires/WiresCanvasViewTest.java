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

package org.kie.workbench.common.stunner.client.widgets.canvas.wires;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresControlFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresHandlerFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactoryImpl;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresCanvasViewTest {

    private WiresCanvasView canvas;

    private LienzoPanel panel;
    private Layer layer;
    private WiresManagerFactory wiresManagerFactory;

    @Before
    public void setup() {
        this.wiresManagerFactory = spy(new WiresManagerFactoryImpl(new StunnerWiresControlFactory(), new StunnerWiresHandlerFactory()));

        this.canvas = new WiresCanvasView(this.wiresManagerFactory);
        this.panel = new LienzoPanel(100,
                                     100);
        this.layer = new LienzoLayer();
        this.canvas.init();
        this.canvas.show(panel,
                         100,
                         100,
                         layer);
    }

    @Test
    public void testInit() {
        ArgumentCaptor<com.ait.lienzo.client.core.shape.Layer> layerArgumentCaptor
                = ArgumentCaptor.forClass(com.ait.lienzo.client.core.shape.Layer.class);
        verify(wiresManagerFactory).newWiresManager(layerArgumentCaptor.capture());
    }

    @Test
    public void testAddShape() {
        final WiresShapeView view = createShape();
        canvas.addShape(view);
        assertNotNull(canvas.getWiresManager().getShape(view.uuid()));
    }

    @Test
    public void testRemoveShape() {
        final WiresShapeView view = createShape();
        canvas.addShape(view);
        assertNotNull(canvas.getWiresManager().getShape(view.uuid()));
        canvas.removeShape(view);
        assertNull(canvas.getWiresManager().getShape(view.uuid()));
    }

    @Test
    public void testAddConnector() {
        final WiresConnectorView view = createConnector();
        canvas.addShape(view);
        assertTrue(canvas.getWiresManager().getConnectorList().contains(view));
    }

    @Test
    public void testRemoveConnector() {
        final WiresConnectorView view = createConnector();
        canvas.addShape(view);
        assertTrue(canvas.getWiresManager().getConnectorList().contains(view));
        canvas.removeShape(view);
        assertFalse(canvas.getWiresManager().getConnectorList().contains(view));
    }

    @Test
    public void assertStunnerShapeGroupId() {
        final WiresShapeView view = createShape();
        canvas.addShape(view);

        assertEquals(WiresCanvas.WIRES_CANVAS_GROUP_ID,
                     WiresUtils.getShapeGroup(view.getGroup()));
    }

    @Test
    public void assertStunnerConnectorGroupId() {
        final WiresConnectorView view = createConnector();
        canvas.addShape(view);

        assertEquals(WiresCanvas.WIRES_CANVAS_GROUP_ID,
                     WiresUtils.getShapeGroup(view.getGroup()));
    }

    @Test
    public void testConfigureWires() {
        final WiresManager wiresManager = mock(WiresManager.class);
        final WiresManagerFactory factory = mock(WiresManagerFactory.class);
        when(factory.newWiresManager(any(com.ait.lienzo.client.core.shape.Layer.class)))
                .thenReturn(wiresManager);
        new WiresCanvasView(factory).init();
        verify(wiresManager, times(1)).setSpliceEnabled(eq(false));
        verify(wiresManager, times(1)).setLocationAcceptor(eq(ILocationAcceptor.NONE));
        verify(wiresManager, times(1)).setContainmentAcceptor(eq(IContainmentAcceptor.NONE));
        verify(wiresManager, times(1)).setDockingAcceptor(eq(IDockingAcceptor.NONE));
        verify(wiresManager, times(1)).setConnectionAcceptor(eq(IConnectionAcceptor.NONE));
        verify(wiresManager, times(1)).setControlPointsAcceptor(eq(IControlPointsAcceptor.NONE));
    }

    @SuppressWarnings("unchecked")
    private WiresShapeView createShape() {
        return new WiresShapeView(new MultiPath());
    }

    @SuppressWarnings("unchecked")
    private WiresConnectorView createConnector() {
        return new WiresConnectorView(new OrthogonalPolyLine(new Point2D(0,
                                                                         0)),
                                      new MultiPathDecorator(new MultiPath()),
                                      new MultiPathDecorator(new MultiPath()));
    }
}
