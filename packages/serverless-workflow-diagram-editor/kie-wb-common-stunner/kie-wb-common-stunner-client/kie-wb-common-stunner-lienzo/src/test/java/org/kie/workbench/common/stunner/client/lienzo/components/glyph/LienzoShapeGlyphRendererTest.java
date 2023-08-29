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


package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.function.Function;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoShapeGlyphRendererTest {

    private static final String DEF_ID = "def1";

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private Object definition;

    @Mock
    private Shape shape;

    @Mock
    private ShapeView<?> view;

    @Mock
    private BoundingBox boundingBox;

    @Mock
    private Group group;

    @Mock
    private Function<ShapeView<?>, BoundingBox> boundingBoxProvider;

    @Mock
    private Function<ShapeView<?>, Group> groupFunctionProvider;

    private LienzoShapeGlyphRenderer tested;
    private ShapeGlyph glyph;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(shape.getShapeView()).thenReturn(view);
        when(factoryManager.newDefinition(eq(DEF_ID))).thenReturn(definition);
        when(shapeFactory.newShape(eq(definition))).thenReturn(shape);
        when(boundingBoxProvider.apply(eq(view))).thenReturn(boundingBox);
        when(groupFunctionProvider.apply(eq(view))).thenReturn(group);
        when(boundingBox.getX()).thenReturn(0d);
        when(boundingBox.getY()).thenReturn(0d);
        when(boundingBox.getWidth()).thenReturn(150d);
        when(boundingBox.getHeight()).thenReturn(351d);
        this.glyph = ShapeGlyph.create();
        this.glyph.setDefinitionId(DEF_ID);
        this.glyph.setFactorySupplier(() -> shapeFactory);
        this.tested = new LienzoShapeGlyphRenderer(factoryManager,
                                                   boundingBoxProvider,
                                                   groupFunctionProvider);
    }

    @Test
    public void testType() {
        assertEquals(ShapeGlyph.class,
                     tested.getGlyphType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRender() {
        final Group group = tested.render(glyph,
                                          150d,
                                          351d);
        assertEquals(this.group,
                     group);
        verify(this.group,
               times(1)).setScale(eq(1d),
                                  eq(1d));
    }
}
