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

package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresLayerTest {

    @Mock
    private WiresManager wiresManager;
    @Mock
    private MagnetManager magnetManager;
    @Mock
    private WiresShape shape;
    @Mock
    private WiresConnector connector;

    private WiresLayer tested;

    @Before
    public void setUp() throws Exception {
        Group shapeGroup = new Group();
        when(shape.getGroup()).thenReturn(shapeGroup);
        Group connectorGroup = new Group();
        when(connector.getGroup()).thenReturn(connectorGroup);
        when(wiresManager.getMagnetManager()).thenReturn(magnetManager);
        tested = new WiresLayer();
        tested.use(wiresManager);
    }

    @Test
    public void testAddShape() {
        tested.add(shape);
        verify(wiresManager, times(1)).register(eq(shape));
        verify(magnetManager, times(1)).createMagnets(eq(shape),
                                                      eq(WiresLayer.MAGNET_CARDINALS));
    }

    @Test
    public void testDeleteShape() {
        tested.delete(shape);
        verify(wiresManager, times(1)).deregister(eq(shape));
    }

    @Test
    public void testAddconnector() {
        tested.add(connector);
        verify(wiresManager, times(1)).register(eq(connector));
    }

    @Test
    public void testDeleteConnector() {
        tested.delete(connector);
        verify(wiresManager, times(1)).deregister(eq(connector));
    }

    @Test
    public void testAddChild() {
        WiresShape parent = mock(WiresShape.class);
        tested.addChild(parent, shape);
        verify(parent, times(1)).add(eq(shape));
    }

    @Test
    public void testDeleteChild() {
        WiresShape parent = mock(WiresShape.class);
        tested.deleteChild(parent, shape);
        verify(parent, times(1)).remove(eq(shape));
    }

    @Test
    public void testDock() {
        WiresShapeControl control = mock(WiresShapeControl.class);
        WiresDockingControl dockingControl = mock(WiresDockingControl.class);
        when(control.getDockingControl()).thenReturn(dockingControl);
        when(shape.getControl()).thenReturn(control);
        WiresShape parent = mock(WiresShape.class);
        tested.dock(parent, shape);
        verify(dockingControl, times(1)).dock(eq(parent));
    }

    @Test
    public void testUnDock() {
        WiresShape child = mock(WiresShape.class);
        WiresShapeControl control = mock(WiresShapeControl.class);
        WiresDockingControl dockingControl = mock(WiresDockingControl.class);
        when(control.getDockingControl()).thenReturn(dockingControl);
        when(child.getControl()).thenReturn(control);
        tested.undock(child);
        verify(dockingControl, times(1)).undock();
    }
}
