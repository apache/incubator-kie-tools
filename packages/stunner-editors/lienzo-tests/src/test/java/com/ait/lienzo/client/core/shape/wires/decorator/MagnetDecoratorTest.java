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


package com.ait.lienzo.client.core.shape.wires.decorator;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class MagnetDecoratorTest {

    private MagnetDecorator tested;

    private Shape shape;

    @Before
    public void setUp() {
        tested = new MagnetDecorator();
        shape = spy(new Circle(1));
    }

    @Test
    public void decorateTest() {
        tested.decorate(shape, IShapeDecorator.ShapeState.VALID);
        tested.decorate(shape, IShapeDecorator.ShapeState.NONE);
        tested.decorate(shape, IShapeDecorator.ShapeState.INVALID);

        verify(shape, times(3)).setFillColor("#0088CE");
        verify(shape, times(3)).setFillAlpha(0.8);
        verify(shape, times(3)).setStrokeAlpha(0);
        verify(shape, times(3)).moveToTop();
    }
}
