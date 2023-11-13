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

package com.ait.lienzo.client.core.shape.wires.types;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import elemental2.core.JsArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class JsWiresShapeTest {

    JsWiresShape jsWireShape;

    @Mock
    WiresShape wiresShape;

    @Mock
    WiresShape parent;

    @Mock
    Group group;

    @Mock
    MultiPath path;

    @Mock
    MagnetManager.Magnets magnets;

    @Mock
    WiresMagnet magnet;

    @Mock
    WiresConnection connection1;

    @Mock
    WiresConnection connection2;

    @Mock
    IContainer container;

    @Mock
    Rectangle child1;

    @Mock
    Rectangle child2;

    @Mock
    Rectangle child3;

    @Mock
    Node child3Node;

    @Mock
    Rectangle child4;

    @Mock
    Node child4Node;

    @Test
    public void testGetId() {
        when(wiresShape.getID()).thenReturn("someId");
        final String id = wiresShape.getID();
        assertEquals("Id should be the same", "someId", id);
    }

    @Test
    public void testGetParent() {
        when(parent.getID()).thenReturn("parentId");
        when(wiresShape.getParent()).thenReturn(parent);
        jsWireShape = new JsWiresShape(wiresShape);
        final String theParentId = jsWireShape.getParent().getID();
        assertEquals("Parent should be the same", parent.getID(), theParentId);
    }

    @Test
    public void testGetParentId() {
        when(parent.getID()).thenReturn("parentId");
        when(wiresShape.getParent()).thenReturn(parent);
        jsWireShape = new JsWiresShape(wiresShape);
        final String theParentId = jsWireShape.getParent().getID();
        assertEquals("Parent should be the same", parent.getID(), theParentId);
    }

    @Test
    public void testGetLocation() {
        Point2D point = new Point2D(100, 100);
        when(wiresShape.getLocation()).thenReturn(point);
        jsWireShape = new JsWiresShape(wiresShape);
        final Point2D location = jsWireShape.getLocation();
        assertEquals("Location should be the same", point, location);
    }

    @Test
    public void testGetComputedLocation() {
        Point2D point = new Point2D(100, 100);
        when(wiresShape.getComputedLocation()).thenReturn(point);
        jsWireShape = new JsWiresShape(wiresShape);
        final Point2D location = jsWireShape.getComputedLocation();
        assertEquals("Computed Location should be the same", point, location);
    }

    @Test
    public void testGetBoundingBox() {
        BoundingBox bb = BoundingBox.fromDoubles(100, 100, 200, 200);
        when(wiresShape.getGroup()).thenReturn(group);
        when(group.getBoundingBox()).thenReturn(bb);
        jsWireShape = new JsWiresShape(wiresShape);
        final BoundingBox bb2 = jsWireShape.getBoundingBox();
        assertEquals("Bounding box should be the same", bb, bb2);
    }

    @Test
    public void testGetPath() {
        when(wiresShape.getPath()).thenReturn(path);
        jsWireShape = new JsWiresShape(wiresShape);
        final MultiPath mp = jsWireShape.getPath();
        assertEquals("Paths should be the same", path, mp);
    }

    @Test
    public void testGetMagnetsSize() {
        when(magnets.size()).thenReturn(5);
        when(wiresShape.getMagnets()).thenReturn(magnets);
        jsWireShape = new JsWiresShape(wiresShape);
        final int magnetsSize = jsWireShape.getMagnetsSize();
        assertEquals("Magnets size should be the same", 5, magnetsSize);
    }

    @Test
    public void testGetMagnet() {
        NFastArrayList<WiresConnection> connections = new NFastArrayList<>();
        connections.add(connection1);
        connections.add(connection2);
        when(magnet.getConnections()).thenReturn(connections);
        when(magnets.getMagnet(5)).thenReturn(magnet);
        when(wiresShape.getMagnets()).thenReturn(magnets);
        jsWireShape = new JsWiresShape(wiresShape);
        final JsWiresMagnet magnet2 = jsWireShape.getMagnet(5);
        assertEquals("Magnets should be the same", magnet.getConnections(), magnet2.magnet.getConnections());
    }

    @Test
    public void testGetChild() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        childNodes.add(child1);
        childNodes.add(child2);
        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        final IPrimitive<?> child = jsWireShape.getChild(0);
        assertEquals("Children should be the same", child1, child);
    }

    @Test
    public void testGetShape() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        childNodes.add(child1);
        childNodes.add(child2);
        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        final IPrimitive<?> child = jsWireShape.getShape(0);
        assertEquals("Children should be the same", child1, child);
    }

    @Test
    public void testflatShapes() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        final JsArray<Shape> shapeJsArray = jsWireShape.flatShapes();
        assertEquals("Children should be the same", child1, shapeJsArray.getAt(0));
        assertEquals("Children should be the same", child2, shapeJsArray.getAt(1));
        assertEquals("Children should be the same", child3, shapeJsArray.getAt(2));
        assertEquals("Children should be the same", child4, shapeJsArray.getAt(3));
    }

    @Test
    public void testAsGroup() {
        when(wiresShape.getGroup()).thenReturn(group);
        jsWireShape = new JsWiresShape(wiresShape);
        final Group group2 = jsWireShape.asGroup();
        assertEquals("Group should be the same", group, group2);
    }

    @Test
    public void testSetColorsMap() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);
        when(child3.getUserData()).thenReturn(JsWiresShape.BACKGROUND_KEY);
        when(child4.getUserData()).thenReturn(JsWiresShape.BORDER_FILL_KEY);

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        jsWireShape.setColorsMap();
        assertEquals("Background color shape should be the same", child3, jsWireShape.colorsMap.get(JsWiresShape.BACKGROUND_KEY));
        assertEquals("Border color shape should be the same", child4, jsWireShape.colorsMap.get(JsWiresShape.BORDER_FILL_KEY));
    }

    @Test
    public void testSetBorderColorFill() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);
        when(child4.getUserData()).thenReturn(JsWiresShape.BORDER_FILL_KEY);

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        jsWireShape.setBorderColor("blue");
        assertEquals("Border color shape should be the same", child4, jsWireShape.colorsMap.get(JsWiresShape.BORDER_FILL_KEY));
        verify(child4, times(1)).setFillColor(eq("blue"));
    }

    @Test
    public void testSetBorderColorStroke() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);
        when(child4.getUserData()).thenReturn(JsWiresShape.BORDER_STROKE_KEY);

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        jsWireShape.setBorderColor("blue");
        assertEquals("Border color shape should be the same", child4, jsWireShape.colorsMap.get(JsWiresShape.BORDER_STROKE_KEY));
        verify(child4, times(1)).setStrokeColor(eq("blue"));
    }

    @Test
    public void testGetBorderColorStroke() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);
        when(child4.getUserData()).thenReturn(JsWiresShape.BORDER_STROKE_KEY);
        when(child4.getStrokeColor()).thenReturn("red");

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        final String borderColor = jsWireShape.getBorderColor();
        assertEquals("Border color shape should be the same", child4, jsWireShape.colorsMap.get(JsWiresShape.BORDER_STROKE_KEY));
        assertEquals("Border color should be the same", "red", borderColor);
    }

    @Test
    public void testGetBorderColorFill() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);
        when(child4.getUserData()).thenReturn(JsWiresShape.BORDER_FILL_KEY);
        when(child4.getFillColor()).thenReturn("red");

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        final String borderColor = jsWireShape.getBorderColor();
        assertEquals("Border color shape should be the same", child4, jsWireShape.colorsMap.get(JsWiresShape.BORDER_FILL_KEY));
        assertEquals("Border color should be the same", "red", borderColor);
    }

    @Test
    public void testDraw() {
        jsWireShape = new JsWiresShape(wiresShape);
        jsWireShape.draw();
        verify(wiresShape, times(1)).refresh();
    }

    @Test
    public void testSetBackgroundColor() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);
        when(child4.getUserData()).thenReturn(JsWiresShape.BACKGROUND_KEY);

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        jsWireShape.setBackgroundColor("blue");
        assertEquals("Background color shape should be the same", child4, jsWireShape.colorsMap.get(JsWiresShape.BACKGROUND_KEY));
        verify(child4, times(1)).setFillColor(eq("blue"));
    }


    @Test
    public void testGetBackgroundColor() {
        NFastArrayList<IPrimitive<?>> childNodes = new NFastArrayList<>();
        Group group = new Group();
        when(child3.asNode()).thenReturn(child3Node);
        when(child4.asNode()).thenReturn(child4Node);
        when(child4.getUserData()).thenReturn(JsWiresShape.BACKGROUND_KEY);
        when(child4.getFillColor()).thenReturn("blue");

        group.add(child3);
        group.add(child4);

        childNodes.add(child1);
        childNodes.add(child2);
        childNodes.add(group);

        when(container.getChildNodes()).thenReturn(childNodes);
        when(wiresShape.getContainer()).thenReturn(container);
        jsWireShape = new JsWiresShape(wiresShape);
        final String backgroundColor = jsWireShape.getBackgroundColor();

        assertEquals("Background color shape should be the same", child4, jsWireShape.colorsMap.get(JsWiresShape.BACKGROUND_KEY));
        assertEquals("Background color should be the same", "blue", backgroundColor);
    }

    @Test
    public void testGetBounds() {
        BoundingBox bb = BoundingBox.fromDoubles(100, 100, 200, 200);
        when(wiresShape.getGroup()).thenReturn(group);
        when(group.getBoundingBox()).thenReturn(bb);
        jsWireShape = new JsWiresShape(wiresShape);
        final Point2D bounds = jsWireShape.getBounds();
        assertEquals("Bounds width should be 100", 100, bounds.getX(), 0);
        assertEquals("Bounds height should be 100", 100, bounds.getY(), 0);
    }

    @Test
    public void testGetLocationXY() {
        Point2D point = new Point2D(100, 100);
        when(wiresShape.getLocation()).thenReturn(point);
        jsWireShape = new JsWiresShape(wiresShape);
        final Point2D location = jsWireShape.getLocationXY();
        assertEquals("Location should be the same", point, location);
    }

    @Test
    public void testGetAbsoluteLocation() {
        Point2D location = new Point2D(150, 150);
        when(wiresShape.getGroup()).thenReturn(group);
        when(group.getComputedLocation()).thenReturn(location);
        jsWireShape = new JsWiresShape(wiresShape);
        final Group group2 = jsWireShape.asGroup();
        assertEquals("Group should be the same", group, group2);

        Point2D point = new Point2D(100, 100);
        when(wiresShape.getLocation()).thenReturn(point);
        jsWireShape = new JsWiresShape(wiresShape);
        final Point2D location2 = jsWireShape.getAbsoluteLocation();
        assertEquals("Absolute Location should be the same", location, location2);
    }
}
