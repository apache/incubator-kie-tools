/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.client.shape.factory;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.svg.client.shape.SVGShape;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.impl.SVGMutableShapeImpl;
import org.kie.workbench.common.stunner.svg.client.shape.impl.SVGShapeImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGShapeFactoryTest {

    @Mock
    SyncBeanManager beanManager;
    @Mock
    Object definition;
    @Mock
    Object viewFactory;
    @Mock
    SVGShapeDef<Object, Object> svgShapeDef;
    @Mock
    SVGMutableShapeDef<Object, Object> svgMutableShapeDef;
    @Mock
    SVGShapeView shapeView;

    @Mock
    SVGShapeViewImpl shapeViewImpl;

    private SVGShapeFactory tested;
    private ShapeDefFunctionalFactory functionalFactory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.functionalFactory = new ShapeDefFunctionalFactory();
        this.tested = spy(new SVGShapeFactory(beanManager,
                                              functionalFactory));
        this.tested.init();
        when(svgShapeDef.getViewFactoryType()).thenReturn(Object.class);
        when(svgMutableShapeDef.getViewFactoryType()).thenReturn(Object.class);
        doAnswer(invocationOnMock -> SVGShapeDef.class).when(svgShapeDef).getType();
        doAnswer(invocationOnMock -> SVGMutableShapeDef.class).when(svgMutableShapeDef).getType();
        doAnswer(invocationOnMock -> shapeView).when(svgShapeDef).newViewInstance(eq(viewFactory),
                                                                                  any(Object.class));
        doAnswer(invocationOnMock -> shapeViewImpl).when(svgMutableShapeDef).newViewInstance(eq(viewFactory),
                                                                                             any(Object.class));
    }

    @Test
    public void testSVGShapeDefBuilder() {
        doAnswer(invocationOnMock -> viewFactory).when(tested).getViewFactory(eq(svgShapeDef));
        final SVGShape shape = tested.newShape(definition,
                                               svgShapeDef);
        assertNotNull(shape);
        assertTrue(shape instanceof SVGShapeImpl);
        assertEquals(shapeView,
                     shape.getShapeView());
    }

    @Test
    public void testSVGMutableShapeDefBuilder() {
        doAnswer(invocationOnMock -> viewFactory).when(tested).getViewFactory(eq(svgMutableShapeDef));
        final SVGShape shape = tested.newShape(definition,
                                               svgMutableShapeDef);
        assertNotNull(shape);
        assertTrue(shape instanceof SVGMutableShapeImpl);
        assertEquals(shapeViewImpl,
                     shape.getShapeView());
    }
}
