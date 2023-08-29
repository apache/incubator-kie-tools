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


package com.ait.lienzo.client.core.shape.toolbox.items.decorator;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DecoratorsFactoryTest {

    @Test
    public void testCreateBoxDecorator() {
        BoxDecorator decorator = DecoratorsFactory.INSTANCE.box();
        assertEquals(BoxDecorator.class, decorator.getClass());
    }

    @Test
    public void testCreateButtonDecorator() {
        BoxDecorator decorator = DecoratorsFactory.INSTANCE.button();
        assertEquals(BoxDecorator.class, decorator.getClass());
        MultiPath path = spy(new MultiPath());
        Consumer<MultiPath> showExecutor = decorator.getShowExecutor();
        showExecutor.accept(path);
        verify(path, times(1)).setFillShapeForSelection(eq(true));
        verify(path, times(1)).setFillBoundsForSelection(eq(true));
        verify(path, times(1)).setSelectionBoundsOffset(eq(DecoratorsFactory.SELECTION_OFFSET));
        verify(path, times(1)).setSelectionStrokeOffset(eq(DecoratorsFactory.SELECTION_OFFSET));
        verify(path, times(1)).setAlpha(eq(1d));
        path = spy(new MultiPath());
        Consumer<MultiPath> hideExecutor = decorator.getHideExecutor();
        hideExecutor.accept(path);
        verify(path, times(1)).setFillShapeForSelection(eq(false));
        verify(path, times(1)).setFillBoundsForSelection(eq(false));
        verify(path, times(1)).setAlpha(eq(0d));
    }
}
