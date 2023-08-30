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


package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.List;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeViewTest {

    private final static MultiPath PATH = new MultiPath();

    private WiresShapeView tested;

    @Mock
    private WiresShapeControlImpl control;

    @Before
    public void setup() throws Exception {
        this.tested = new WiresShapeView(PATH) {
            @Override
            public WiresShapeControl getControl() {
                return control;
            }
        };
        assertEquals(PATH,
                     tested.getShape());
    }

    @Test
    public void testUUID() {
        final String uuid = "uuid";
        tested.setUUID(uuid);
        assertTrue(tested.getContainer().getUserData() instanceof WiresUtils.UserData);
        assertEquals(uuid,
                     tested.getUUID());
        assertEquals(uuid,
                     ((WiresUtils.UserData) tested.getContainer().getUserData()).getUuid());
    }

    @Test
    public void testCoordinates() {
        tested.setShapeLocation(new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(50.5, 321.65));
        assertEquals(50.5,
                     tested.getShapeX(),
                     0d);
        assertEquals(321.65,
                     tested.getShapeY(),
                     0d);
    }

    @Test
    public void testAlpha() {
        tested.setAlpha(0.53);
        assertEquals(0.53,
                     tested.getAlpha(),
                     0d);
    }

    @Test
    public void testFillAttributes() {
        tested.setFillColor("color1");
        tested.setFillAlpha(0.53);
        assertEquals("color1",
                     tested.getFillColor());
        assertEquals(0.53,
                     tested.getFillAlpha(),
                     0d);
    }

    @Test
    public void testStrokeAttributes() {
        tested.setStrokeColor("color1");
        tested.setStrokeWidth(3.89);
        tested.setStrokeAlpha(0.53);
        assertEquals("color1",
                     tested.getStrokeColor());
        assertEquals(3.89,
                     tested.getStrokeWidth(),
                     0d);
        assertEquals(0.53,
                     tested.getStrokeAlpha(),
                     0d);
    }

    @Test
    public void testSetDragBounds() {
        tested.setDragBounds(Bounds.create(1.5d,
                                           6.4d,
                                           564.78d,
                                           543.84d));
        ArgumentCaptor<OptionalBounds> bbCaptor = ArgumentCaptor.forClass(OptionalBounds.class);
        verify(control, times(1)).setLocationBounds(bbCaptor.capture());
        final OptionalBounds bb = bbCaptor.getValue();
        assertEquals(1.5d, bb.getMinX(), 0d);
        assertEquals(6.4d, bb.getMinY(), 0d);
        assertEquals(564.78d, bb.getMaxX(), 0d);
        assertEquals(543.84d, bb.getMaxY(), 0d);
    }

    @Test
    public void testListening() {
        tested.setListening(true);
        assertTrue(tested.getPath().isListening());
        assertTrue(tested.getPath().isFillBoundsForSelection());
        tested.setListening(false);
        assertFalse(tested.getPath().isListening());
        assertFalse(tested.getPath().isFillBoundsForSelection());
    }

    @Test
    public void testMove() {
        final LayoutContainer container = mock(LayoutContainer.class);
        final Group group = mock(Group.class);
        when(container.getGroup()).thenReturn(group);
        when(container.refresh()).thenReturn(container);
        when(container.execute()).thenReturn(container);
        when(container.setOffset(any(Point2D.class))).thenReturn(container);
        when(container.setSize(anyDouble(),
                               anyDouble())).thenReturn(container);
        this.tested = new WiresShapeView(PATH,
                                         container);
        tested.moveToTop();
        tested.moveToBottom();
        tested.moveUp();
        tested.moveDown();
        verify(group,
               times(1)).moveToTop();
        verify(group,
               times(1)).moveToBottom();
        verify(group,
               times(1)).moveUp();
        verify(group,
               times(1)).moveDown();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConsumeChildrenAndConnectors() {
        // Setup children shapes and connectors.
        final Consumer<IDrawable> consumer = mock(Consumer.class);
        final WiresShapeView child1 = newShape();
        final WiresShapeView child2 = newShape();
        tested.add(child1);
        tested.add(child2);
        final WiresConnectorView connector1 = newConnector();
        final WiresConnectorView connector2 = newConnector();
        final MagnetManager.Magnets magnets1 = mock(MagnetManager.Magnets.class);
        final MagnetManager.Magnets magnets2 = mock(MagnetManager.Magnets.class);
        final WiresMagnet magnet1 = mock(WiresMagnet.class);
        final WiresMagnet magnet2 = mock(WiresMagnet.class);
        final WiresConnection connection1 = mock(WiresConnection.class);
        final WiresConnection connection2 = mock(WiresConnection.class);
        NFastArrayList<WiresConnection> conn1NFastArrayList = new NFastArrayList<>();
        conn1NFastArrayList.add(connection1);
        NFastArrayList<WiresConnection> conn2NFastArrayList = new NFastArrayList<>();
        conn2NFastArrayList.add(connection2);
        final NFastArrayList<WiresConnection> connections1 = conn1NFastArrayList;
        final NFastArrayList<WiresConnection> connections2 = conn2NFastArrayList;
        when(magnets1.size()).thenReturn(1);
        when(magnets1.getMagnet(eq(0))).thenReturn(magnet1);
        when(magnets2.size()).thenReturn(1);
        when(magnets2.getMagnet(eq(0))).thenReturn(magnet2);
        when(magnet1.getConnections()).thenReturn(connections1);
        when(magnet2.getConnections()).thenReturn(connections2);
        when(connection1.getConnector()).thenReturn(connector1);
        when(connection2.getConnector()).thenReturn(connector2);
        child1.setMagnets(magnets1);
        child2.setMagnets(magnets2);
        // Consume.
        tested.consumeChildrenAndConnectors(consumer);
        // Verify.
        verify(consumer, times(1)).accept(eq(tested.getContainer()));
        verify(consumer, times(1)).accept(eq(child1.getContainer()));
        verify(consumer, times(1)).accept(eq(child2.getContainer()));
        verify(consumer, times(1)).accept(eq(connector1.getGroup()));
        verify(consumer, times(1)).accept(eq(connector2.getGroup()));
    }

    private static WiresShapeView newShape() {
        return new WiresShapeView(new MultiPath().circle(10));
    }

    @SuppressWarnings("unchecked")
    private static WiresConnectorView newConnector() {
        return new WiresConnectorView(new OrthogonalPolyLine(new Point2D(0,
                                                                         0)),
                                      new MultiPathDecorator(new MultiPath()),
                                      new MultiPathDecorator(new MultiPath()));
    }

    @Test
    public void testDecorators() {
        final List decorators = tested.getDecorators();
        assertNotNull(decorators);
        assertEquals(1,
                     decorators.size());
        assertEquals(PATH,
                     decorators.get(0));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        assertNull(tested.getParent());
    }
}
