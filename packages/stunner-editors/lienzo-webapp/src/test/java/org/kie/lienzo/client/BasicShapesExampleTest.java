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
import org.kie.lienzo.client.selenium.JsCanvasShapeExecutor;

import static org.junit.Assert.assertEquals;
import static org.kie.lienzo.client.BasicShapesExample.RECTANGLE;

@SuppressWarnings("NonJREEmulationClassesInClientCode")
public class BasicShapesExampleTest {

    private JsCanvasDriver lienzoDriver;
    private JsCanvasShapeExecutor rectangle;

    @BeforeClass
    public static void setupClass() {
        JsCanvasDriver.init();
    }

    @Before
    public void openWebapp() {
        // TODO lienzoDriver = JsCanvasDriver.build();
        lienzoDriver = JsCanvasDriver.devMode();
        rectangle = lienzoDriver.forShape(RECTANGLE);
        lienzoDriver.openTest(0);
    }

    @After
    public void closeWebapp() {
        lienzoDriver.closeTest();
    }

    @Test
    public void testMoveShape() {
        assertEquals(100, rectangle.getX(), 0);
        assertEquals(100, rectangle.getY(), 0);
        rectangle.move(300, 298);
        assertEquals(300, rectangle.getX(), 0);
        assertEquals(298, rectangle.getY(), 0);
    }

    @Test
    public void testClickShape() {
        assertEquals(100, rectangle.getWidth(), 0);
        assertEquals(100, rectangle.getHeight(), 0);
        rectangle.click();
        assertEquals(200, rectangle.getWidth(), 0);
        assertEquals(200, rectangle.getHeight(), 0);
    }

    @Test
    public void testDoubleClickShape() {
        assertEquals(100, rectangle.getWidth(), 0);
        assertEquals(100, rectangle.getHeight(), 0);
        rectangle.doubleClick();
        assertEquals(50, rectangle.getWidth(), 0);
        assertEquals(50, rectangle.getHeight(), 0);
    }

    @Test
    public void testEnterExitShape() {
        assertEquals(BasicShapesExample.COLOR.getValue(), rectangle.getFillColor());
        rectangle.over();
        assertEquals(BasicShapesExample.OVER_COLOR.getValue(), rectangle.getFillColor());
        rectangle.out();
        assertEquals(BasicShapesExample.COLOR.getValue(), rectangle.getFillColor());
    }
}
