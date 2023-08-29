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


package org.kie.workbench.common.stunner.core.client.shape.factory;

import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefTestStubs.TestShapeDefType1;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefTestStubs.TestShapeDefType2;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeDefFunctionalFactoryTest {

    @Mock
    private BiFunction<Object, ShapeDef<Object>, Shape> shapeDef1FactoryFunction;

    @Mock
    private BiFunction<Object, ShapeDef<Object>, Shape> shapeDef2FactoryFunction;

    @Mock
    private Shape shape1;

    @Mock
    private Shape shape2;

    @Mock
    private Object definitionBean;

    private ShapeDefFunctionalFactory<Object, ShapeDef<Object>, Shape> tested;
    private static final TestShapeDefType1 shapeDef1 = new TestShapeDefType1();
    private static final TestShapeDefType2 shapeDef2 = new TestShapeDefType2();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(shapeDef1FactoryFunction.apply(eq(definitionBean),
                                            eq(shapeDef1))).thenReturn(shape1);
        when(shapeDef2FactoryFunction.apply(eq(definitionBean),
                                            eq(shapeDef2))).thenReturn(shape2);
        this.tested = new ShapeDefFunctionalFactory();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateShapeForDef1() {
        this.tested.set(TestShapeDefType1.class,
                        shapeDef1FactoryFunction);
        final Shape shape = this.tested.newShape(definitionBean,
                                                 shapeDef1);
        assertNotNull(shape);
        assertEquals(shape1,
                     shape);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateShapeForDef2() {
        this.tested.set(TestShapeDefType2.class,
                        shapeDef2FactoryFunction);
        final Shape shape = this.tested.newShape(definitionBean,
                                                 shapeDef2);
        assertNotNull(shape);
        assertEquals(shape2,
                     shape);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateAll() {
        this.tested.set(TestShapeDefType1.class,
                        shapeDef1FactoryFunction);
        this.tested.set(TestShapeDefType2.class,
                        shapeDef2FactoryFunction);
        final Shape s1 = this.tested.newShape(definitionBean,
                                              shapeDef1);
        final Shape s2 = this.tested.newShape(definitionBean,
                                              shapeDef2);
        assertNotNull(s1);
        assertEquals(shape1,
                     s1);
        assertNotNull(s2);
        assertEquals(shape2,
                     s2);
    }

    @Test(expected = RuntimeException.class)
    @SuppressWarnings("unchecked")
    public void testNoDefinitionRegistered() {
        this.tested.newShape(definitionBean,
                             shapeDef1);
    }
}
