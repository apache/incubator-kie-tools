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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.DecoratedShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGChildViewHandlerTest {

    @Mock
    private SVGBasicShapeView svgChild;

    @Mock
    private DecoratedShapeView<?> shapeView;

    private Group svgGroup;
    private SVGChildViewHandler tested;
    private SVGContainer parentShape;
    private Group group;
    private SVGPrimitiveShape primitiveShape;
    private Rectangle rectangle;

    @Before
    public void setup() throws Exception {
        this.svgGroup = new Group().setID("svgGroup1");
        this.group = new Group().setID("parent");
        this.rectangle = new Rectangle(10d, 10d);
        when(svgChild.getContainer()).thenReturn(svgGroup);
        this.parentShape = new SVGContainer("parent",
                                            group,
                                            false,
                                            null);
        this.tested = new SVGChildViewHandler(shapeView);
    }

    @Test
    public void testAddSVGChild() {
        tested.addSVGChild(parentShape,
                           svgChild);
        assertEquals(svgGroup,
                     parentShape.getPrimitive("svgGroup1"));
        assertEquals(1,
                     tested.getSVGChildren().size());
        assertEquals(svgChild,
                     tested.getSVGChildren().iterator().next());
    }

    @Test
    public void testAddChild() {
        primitiveShape = new SVGPrimitiveShape(rectangle);
        tested.addChild(primitiveShape);
        verify(shapeView, times(1)).addChild(eq(rectangle));
        assertEquals(1,
                     tested.getChildren().size());
        assertEquals(primitiveShape,
                     tested.getChildren().iterator().next());
    }

    @Test
    public void testAddChildScalable() {
        primitiveShape = new SVGPrimitiveShape(rectangle,
                                               true,
                                               null);
        tested.addChild(primitiveShape);
        verify(shapeView, times(1)).addScalableChild(eq(rectangle));
        assertEquals(1,
                     tested.getChildren().size());
        assertEquals(primitiveShape,
                     tested.getChildren().iterator().next());
    }

    @Test
    public void testAddChildLayout() {
        primitiveShape = new SVGPrimitiveShape(rectangle,
                                               false,
                                               LayoutContainer.Layout.BOTTOM);
        tested.addChild(primitiveShape);
        verify(shapeView, times(1)).addChild(eq(rectangle), eq(LayoutContainer.Layout.BOTTOM));
        assertEquals(1,
                     tested.getChildren().size());
        assertEquals(primitiveShape,
                     tested.getChildren().iterator().next());
    }
}
