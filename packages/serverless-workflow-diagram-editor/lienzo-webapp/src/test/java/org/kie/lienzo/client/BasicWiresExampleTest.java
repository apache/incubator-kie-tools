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


package org.kie.lienzo.client;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.lienzo.client.selenium.JsCanvasDriver;
import org.kie.lienzo.client.selenium.JsCanvasWiresShapeExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.lienzo.client.BasicWiresExample.BLUE_RECTANGLE;
import static org.kie.lienzo.client.BasicWiresExample.CIRCLE;
import static org.kie.lienzo.client.BasicWiresExample.PARENT;
import static org.kie.lienzo.client.BasicWiresExample.RED_RECTANGLE;

@SuppressWarnings("NonJREEmulationClassesInClientCode")
public class BasicWiresExampleTest {

    private JsCanvasDriver lienzoDriver;

    @BeforeClass
    public static void setupClass() {
        JsCanvasDriver.init();
    }

    @Before
    public void openWebapp() {
        // TODO lienzoDriver = JsCanvasDriver.build();
        lienzoDriver = JsCanvasDriver.devMode();
        lienzoDriver.openTest(1);
    }

    @After
    public void closeWebapp() {
        lienzoDriver.closeTest();
    }

    @Test
    public void testMoveRedShape() {
        JsCanvasWiresShapeExecutor redShape = lienzoDriver.forWiresShape(RED_RECTANGLE);
        assertNull(redShape.getParent());
        assertEquals(100d, redShape.getX(), 0d);
        assertEquals(50d, redShape.getY(), 0d);
        redShape.drag(150, 150);
        assertEquals(150d, redShape.getX(), 0d);
        assertEquals(150d, redShape.getY(), 0d);
        assertNull(redShape.getParent());
    }

    @Test
    public void testBlueParentNotAllowed() {
        JsCanvasWiresShapeExecutor blueShape = lienzoDriver.forWiresShape(BLUE_RECTANGLE);
        assertNull(blueShape.getParent());
        blueShape.drag(400, 400);
        assertNull(blueShape.getParent());
        assertEquals(650d, blueShape.getX(), 0d);
        assertEquals(50d, blueShape.getY(), 0d);
        assertEquals(650d, blueShape.getComputedX(), 0d);
        assertEquals(50d, blueShape.getComputedY(), 0d);
    }

    @Test
    public void testRedRectParentAllowed() {
        JsCanvasWiresShapeExecutor redShape = lienzoDriver.forWiresShape(RED_RECTANGLE);
        assertNull(redShape.getParent());
        redShape.drag(400, 400);
        JsCanvasWiresShapeExecutor parent = redShape.getParent();
        assertNotNull(parent);
        assertEquals(PARENT, parent.getID());
        assertEquals(350d, redShape.getX(), 0d);
        assertEquals(100d, redShape.getY(), 0d);
        assertEquals(400d, redShape.getComputedX(), 0d);
        assertEquals(400d, redShape.getComputedY(), 0d);
    }

    @Test
    public void testCircleParentAllowed() {
        JsCanvasWiresShapeExecutor circleShape = lienzoDriver.forWiresShape(CIRCLE);
        assertNull(circleShape.getParent());
        circleShape.drag(100, 400);
        JsCanvasWiresShapeExecutor parent = circleShape.getParent();
        assertNotNull(parent);
        assertEquals(PARENT, parent.getID());
        assertEquals(50d, circleShape.getX(), 0d);
        assertEquals(100d, circleShape.getY(), 0d);
        assertEquals(100d, circleShape.getComputedX(), 0d);
        assertEquals(400d, circleShape.getComputedY(), 0d);
    }

    @Test
    public void testMoveParent() {
        JsCanvasWiresShapeExecutor parent = lienzoDriver.forWiresShape(PARENT);
        parent.drag(50, 200);
        assertEquals(50d, parent.getX(), 0d);
        assertEquals(200d, parent.getY(), 0d);
        assertEquals(50d, parent.getComputedX(), 0d);
        assertEquals(200d, parent.getComputedY(), 0d);
    }

    @Test
    public void testRedParentAllowedAndThenMoveParent() {
        testRedRectParentAllowed();
        testMoveParent();
        JsCanvasWiresShapeExecutor redShape = lienzoDriver.forWiresShape(RED_RECTANGLE);
        assertEquals(350d, redShape.getX(), 0d);
        assertEquals(100d, redShape.getY(), 0d);
        assertEquals(400d, redShape.getComputedX(), 0d);
        assertEquals(300d, redShape.getComputedY(), 0d);
    }

    @Test
    public void testAddConnectedShapesIntoParentAndThenMoveParent() {
        testCircleParentAllowed();
        testRedParentAllowedAndThenMoveParent();
        JsCanvasWiresShapeExecutor redShape = lienzoDriver.forWiresShape(CIRCLE);
        assertEquals(50d, redShape.getX(), 0d);
        assertEquals(100d, redShape.getY(), 0d);
        assertEquals(100d, redShape.getComputedX(), 0d);
        assertEquals(300d, redShape.getComputedY(), 0d);
    }

    // TODO: @Test
    public void testMoveParentWithChildrenTwice() {
        testAddConnectedShapesIntoParentAndThenMoveParent();
        JsCanvasWiresShapeExecutor parent = lienzoDriver.forWiresShape(PARENT);
        parent.drag(50, 400);
        // TODO: Here connections get lost -> ERROR
    }

    @Test
    public void testMagnetsLocation() {
        JsCanvasWiresShapeExecutor rectangle = lienzoDriver.forWiresShape(RED_RECTANGLE);
        assertEquals(150, rectangle.getMagnetX(0), 0d);
        assertEquals(100, rectangle.getMagnetY(0), 0d);
        assertEquals(150, rectangle.getMagnetX(1), 0d);
        assertEquals(50, rectangle.getMagnetY(1), 0d);
        assertEquals(200, rectangle.getMagnetX(2), 0d);
        assertEquals(50, rectangle.getMagnetY(2), 0d);
        assertEquals(200, rectangle.getMagnetX(3), 0d);
        assertEquals(100, rectangle.getMagnetY(3), 0d);
        assertEquals(200, rectangle.getMagnetX(4), 0d);
        assertEquals(150, rectangle.getMagnetY(4), 0d);
        assertEquals(100, rectangle.getMagnetX(6), 0d);
        assertEquals(150, rectangle.getMagnetY(6), 0d);
        assertEquals(100, rectangle.getMagnetX(7), 0d);
        assertEquals(100, rectangle.getMagnetY(7), 0d);
        assertEquals(100, rectangle.getMagnetX(8), 0d);
        assertEquals(50, rectangle.getMagnetY(8), 0d);
        rectangle.move(rectangle.getX() + 100, rectangle.getY() + 100);
        assertEquals(250, rectangle.getMagnetX(0), 0d);
        assertEquals(200, rectangle.getMagnetY(0), 0d);
        assertEquals(250, rectangle.getMagnetX(1), 0d);
        assertEquals(150, rectangle.getMagnetY(1), 0d);
        assertEquals(300, rectangle.getMagnetX(2), 0d);
        assertEquals(150, rectangle.getMagnetY(2), 0d);
        assertEquals(300, rectangle.getMagnetX(3), 0d);
        assertEquals(200, rectangle.getMagnetY(3), 0d);
        assertEquals(300, rectangle.getMagnetX(4), 0d);
        assertEquals(250, rectangle.getMagnetY(4), 0d);
        assertEquals(200, rectangle.getMagnetX(6), 0d);
        assertEquals(250, rectangle.getMagnetY(6), 0d);
        assertEquals(200, rectangle.getMagnetX(7), 0d);
        assertEquals(200, rectangle.getMagnetY(7), 0d);
        assertEquals(200, rectangle.getMagnetX(8), 0d);
        assertEquals(150, rectangle.getMagnetY(8), 0d);
    }
}
