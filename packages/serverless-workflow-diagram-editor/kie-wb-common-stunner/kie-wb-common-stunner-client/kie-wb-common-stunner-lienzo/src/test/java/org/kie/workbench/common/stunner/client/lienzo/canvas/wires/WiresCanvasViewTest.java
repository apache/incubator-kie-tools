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


package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresCanvasViewTest {

    @Mock
    private WiresLayer wiresLayer;

    private WiresCanvasView tested;

    @Before
    public void setUp() throws Exception {
        this.tested = new WiresCanvasView(wiresLayer);
    }

    @Test
    public void testUseWiresManager() {
        WiresManager wiresManager = mock(WiresManager.class);
        tested.use(wiresManager);
        verify(wiresLayer, times(1)).use(eq(wiresManager));
    }

    @Test
    public void testAdd() {
        WiresShapeView shapeView = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.add(shapeView);
        verify(wiresLayer, times(1)).add(eq(shapeView));
    }

    @Test
    public void testDelete() {
        WiresShapeView shapeView = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.delete(shapeView);
        verify(wiresLayer, times(1)).delete(eq(shapeView));
    }

    @Test
    public void testAddRoot() {
        WiresShapeView shapeView = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.addRoot(shapeView);
        verify(wiresLayer, times(1)).add(eq(shapeView.getGroup()));
    }

    @Test
    public void testDeleteRoot() {
        WiresShapeView shapeView = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.deleteRoot(shapeView);
        verify(wiresLayer, times(1)).delete(eq(shapeView.getGroup()));
    }

    @Test
    public void testAddChild() {
        WiresShapeView parent = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        WiresShapeView child = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.addChild(parent, child);
        verify(wiresLayer, times(1)).addChild(eq(parent), eq(child));
    }

    @Test
    public void testDeleteChild() {
        WiresShapeView parent = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        WiresShapeView child = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.deleteChild(parent, child);
        verify(wiresLayer, times(1)).deleteChild(eq(parent), eq(child));
    }

    @Test
    public void testDock() {
        WiresShapeView parent = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        WiresShapeView child = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.dock(parent, child);
        verify(wiresLayer, times(1)).dock(eq(parent), eq(child));
    }

    @Test
    public void testUnDock() {
        WiresShapeView parent = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        WiresShapeView child = new WiresShapeView(new MultiPath().rect(0, 0, 50, 50));
        tested.undock(parent, child);
        verify(wiresLayer, times(1)).undock(eq(child));
    }
}
