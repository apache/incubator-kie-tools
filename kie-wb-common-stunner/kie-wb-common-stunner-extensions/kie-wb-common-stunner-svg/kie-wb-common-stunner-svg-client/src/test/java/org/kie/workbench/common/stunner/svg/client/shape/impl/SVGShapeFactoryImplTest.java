/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.client.shape.impl;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.GlyphBuilderFactory;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SVGShapeFactoryImplTest {

    @Mock
    SyncBeanManager beanManager;
    @Mock
    GlyphBuilderFactory glyphBuilderFactory;
    @Mock
    DefinitionManager definitionManager;
    @Mock
    FactoryManager factoryManager;
    @Mock
    Object definition;
    @Mock
    AbstractCanvasHandler canvasHandler;
    @Mock
    Object viewFactory;
    @Mock
    SVGShapeDef shapeDef;
    @Mock
    SVGShapeView shapeView;

    private SVGShapeFactoryImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.tested = spy(new SVGShapeFactoryImpl(beanManager,
                                                  glyphBuilderFactory,
                                                  definitionManager,
                                                  factoryManager));
        this.tested.addShapeDef(Object.class,
                                shapeDef);
        when(shapeDef.getViewFactoryType()).thenReturn(Object.class);
        doAnswer(invocationOnMock -> "java.lang.Object").when(tested).getDefinitionId(eq(definition));
        doAnswer(invocationOnMock -> viewFactory).when(tested).getViewFactory(eq(shapeDef));
        doAnswer(invocationOnMock -> shapeView).when(shapeDef).newViewInstance(eq(viewFactory),
                                                                               any(Object.class));
    }

    @Test
    public void test() {
        final Shape<ShapeView> shape = tested.build(definition,
                                                    canvasHandler);
        assertNotNull(shape);
        assertEquals(shapeView,
                     shape.getShapeView());
    }
}
