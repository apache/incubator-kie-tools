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


package org.kie.workbench.common.stunner.svg.client.shape.view.impl;

import java.util.Collection;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGShapeViewImplTest {

    private SVGShapeViewImpl tested;
    private SVGPrimitiveShape shape;
    private Rectangle rectangle;
    private SVGContainer parent;
    private SVGBasicShapeView child;
    private SVGPrimitiveShape childShape;

    @Before
    public void setup() throws Exception {
        this.parent = new SVGContainer("parent",
                                       new Group().setID("parent"),
                                       false,
                                       null);
        rectangle = new Rectangle(10d, 10d);
        this.childShape = new SVGPrimitiveShape(new Rectangle(10d, 10d));
        this.child = new SVGBasicShapeViewImpl("svgChild1",
                                               childShape,
                                               50d,
                                               23d);
        this.shape = new SVGPrimitiveShape(rectangle);
        this.tested = new SVGShapeViewImpl("svg-test1",
                                           shape,
                                           100,
                                           340,
                                           false);
    }

    @Test
    public void testGetters() {
        assertEquals("svg-test1",
                     tested.getName());
        assertEquals(rectangle,
                     tested.getShape());
        assertEquals(shape,
                     tested.getPrimitive());
    }

    @Test
    public void testAddChild() {
        tested.addChild(childShape);
        final Collection<SVGPrimitive<?>> svgChildren = tested.getChildren();
        assertEquals(1,
                     svgChildren.size());
        assertEquals(childShape,
                     svgChildren.iterator().next());
    }

    @Test
    public void testSVGChild() {
        this.tested.addChild(parent);
        tested.addSVGChild(parent,
                           child);
        final Collection<SVGBasicShapeView> svgChildren = tested.getSVGChildren();
        assertEquals(1,
                     svgChildren.size());
        assertEquals(child,
                     svgChildren.iterator().next());
    }
}
