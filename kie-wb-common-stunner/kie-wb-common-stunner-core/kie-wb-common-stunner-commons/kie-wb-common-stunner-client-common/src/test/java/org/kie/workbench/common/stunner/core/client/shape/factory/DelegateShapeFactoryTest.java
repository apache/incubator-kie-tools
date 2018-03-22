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

package org.kie.workbench.common.stunner.core.client.shape.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefTestStubs.TestShapeDefFactoryStub;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefTestStubs.TestShapeDefType1;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefTestStubs.TestShapeDefType2;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DelegateShapeFactoryTest {

    @Mock
    TestShapeDefFactoryStub shapeDefFactoryStub;

    @Mock
    Shape shape1;

    @Mock
    Shape shape2;

    private DelegateShapeFactory tested;
    private static Definition1 definition1 = new Definition1();
    private static TestShapeDefType1 shapeDef1 = new TestShapeDefType1();
    private static Definition2 definition2 = new Definition2();
    private static TestShapeDefType2 shapeDef2 = new TestShapeDefType2();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(shapeDefFactoryStub.newShape(eq(definition1),
                                          eq(shapeDef1))).thenReturn(shape1);
        when(shapeDefFactoryStub.newShape(eq(definition2),
                                          eq(shapeDef2))).thenReturn(shape2);
        this.tested = new DelegateShapeFactory();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDelegate1() {
        final String definition1Id = BindableAdapterUtils.getDefinitionId(Definition1.class);
        tested.delegate(Definition1.class,
                        shapeDef1,
                        () -> shapeDefFactoryStub);
        final Shape shape = tested.newShape(definition1);
        assertNotNull(shape);
        assertEquals(shape1,
                     shape);
        final ShapeGlyph glyph = (ShapeGlyph) tested.getGlyph(definition1Id);
        assertNotNull(glyph);
        assertEquals(definition1Id,
                     glyph.getDefinitionId());
        assertEquals(tested,
                     glyph.getFactorySupplier().get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDelegate2() {
        final String definition2Id = BindableAdapterUtils.getDefinitionId(Definition2.class);
        tested.delegate(Definition2.class,
                        shapeDef2,
                        () -> shapeDefFactoryStub);
        final Shape shape = tested.newShape(definition2);
        assertNotNull(shape);
        assertEquals(shape2,
                     shape);
        final ShapeGlyph glyph = (ShapeGlyph) tested.getGlyph(definition2Id);
        assertNotNull(glyph);
        assertEquals(definition2Id,
                     glyph.getDefinitionId());
        assertEquals(tested,
                     glyph.getFactorySupplier().get());
    }

    @Test(expected = RuntimeException.class)
    @SuppressWarnings("unchecked")
    public void testNoBindingFound() {
        tested.newShape(definition1);
    }

    private static class Definition1 {

    }

    private static class Definition2 {

    }
}
