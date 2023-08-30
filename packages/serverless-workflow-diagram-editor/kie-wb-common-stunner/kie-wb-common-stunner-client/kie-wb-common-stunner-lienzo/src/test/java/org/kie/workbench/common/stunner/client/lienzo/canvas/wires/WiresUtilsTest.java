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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresUtilsTest {

    @Test
    public void isWiresLayerWhenWiresLayer() {
        final Layer l = new Layer();
        final WiresLayer wl = new WiresLayer(l);

        assertTrue(WiresUtils.isWiresLayer(wl));
    }

    @Test
    public void isWiresLayerWhenWiresShape() {
        final WiresShape ws = new WiresShape(new MultiPath());

        assertFalse(WiresUtils.isWiresLayer(ws));
    }

    @Test
    public void isWiresShapeWhenWiresContainer() {
        final WiresContainer wc = new WiresContainer(new Group());

        assertFalse(WiresUtils.isWiresShape(wc));
    }

    @Test
    public void isWiresShapeWhenWiresLayer() {
        final Layer l = new Layer();
        final WiresLayer wl = new WiresLayer(l);

        assertTrue(WiresUtils.isWiresShape(wl));
    }

    @Test
    public void isWiresShapeWhenUnregisteredWiresShape() {
        final WiresShape ws = new WiresShape(new MultiPath());

        assertFalse(WiresUtils.isWiresShape(ws));
    }

    @Test
    public void isWiresShapeWhenRegisteredWiresShape() {
        final WiresShape ws = new WiresShape(new MultiPath());

        WiresUtils.assertShapeGroup(ws.getContainer(),
                                    WiresCanvas.WIRES_CANVAS_GROUP_ID);

        assertTrue(WiresUtils.isWiresShape(ws));
    }

    @Test
    public void shapeUUID() {
        final String uuid = "uuid";
        final IDrawable shape = new Rectangle(0,
                                              0);
        WiresUtils.assertShapeUUID(shape,
                                   uuid);

        assertTrue(shape.getUserData() instanceof WiresUtils.UserData);
        assertEquals(uuid,
                     WiresUtils.getShapeUUID(shape));
    }

    @Test
    public void shapeGroup() {
        final String group = "group";
        final IDrawable shape = new Rectangle(0,
                                              0);
        WiresUtils.assertShapeGroup(shape,
                                    group);

        assertTrue(shape.getUserData() instanceof WiresUtils.UserData);
        assertEquals(group,
                     WiresUtils.getShapeGroup(shape));
    }
}
