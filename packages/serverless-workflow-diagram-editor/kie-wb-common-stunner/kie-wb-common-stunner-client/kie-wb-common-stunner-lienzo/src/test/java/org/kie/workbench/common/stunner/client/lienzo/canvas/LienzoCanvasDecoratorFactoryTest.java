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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasDecoratorFactoryTest {

    @Test
    public void testAuthoringDecorator() {
        IPrimitive<?> decorator = LienzoCanvasDecoratorFactory.AUTHORING.apply(20, 33);
        assertNotNull(decorator);
        assertTrue(decorator instanceof Group);
        Group group = (Group) decorator;
        IPrimitive<?> child0 = group.getChildNodes().get(0);
        assertNotNull(child0);
        assertTrue(child0 instanceof Line);
        Line line0 = (Line) child0;
        assertFalse(line0.isDraggable());
        assertFalse(line0.isListening());
        assertEquals(0, line0.getFillAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_ALPHA, line0.getStrokeAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_AUTHORING_WIDTH, line0.getStrokeWidth(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_AUTHORING_COLOR, line0.getStrokeColor());
        IPrimitive<?> child1 = group.getChildNodes().get(1);
        assertNotNull(child1);
        assertTrue(child1 instanceof Line);
        Line line1 = (Line) child1;
        assertFalse(line1.isDraggable());
        assertFalse(line1.isListening());
        assertEquals(0, line1.getFillAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_ALPHA, line1.getStrokeAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_AUTHORING_WIDTH, line1.getStrokeWidth(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_AUTHORING_COLOR, line1.getStrokeColor());
    }

    @Test
    public void testPreviewDecorator() {
        IPrimitive<?> decorator = LienzoCanvasDecoratorFactory.PREVIEW.apply(20, 33);
        assertNotNull(decorator);
        assertTrue(decorator instanceof Group);
        Group group = (Group) decorator;
        IPrimitive<?> child0 = group.getChildNodes().get(0);
        assertNotNull(child0);
        assertTrue(child0 instanceof Line);
        Line line0 = (Line) child0;
        assertFalse(line0.isDraggable());
        assertFalse(line0.isListening());
        assertEquals(0, line0.getFillAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_ALPHA, line0.getStrokeAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_PREVIEW_WIDTH, line0.getStrokeWidth(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_PREVIEW_COLOR, line0.getStrokeColor());
        IPrimitive<?> child1 = group.getChildNodes().get(1);
        assertNotNull(child1);
        assertTrue(child1 instanceof Line);
        Line line1 = (Line) child1;
        assertFalse(line1.isDraggable());
        assertFalse(line1.isListening());
        assertEquals(0, line1.getFillAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_ALPHA, line1.getStrokeAlpha(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_PREVIEW_WIDTH, line1.getStrokeWidth(), 0d);
        assertEquals(LienzoCanvasDecoratorFactory.DECORATOR_PREVIEW_COLOR, line1.getStrokeColor());
    }
}
