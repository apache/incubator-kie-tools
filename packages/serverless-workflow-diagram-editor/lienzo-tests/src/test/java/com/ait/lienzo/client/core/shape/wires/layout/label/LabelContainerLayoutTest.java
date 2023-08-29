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

package com.ait.lienzo.client.core.shape.wires.layout.label;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.layout.AbstractContainerLayoutTest;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionContainerLayout;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraintsContainerLayout;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class LabelContainerLayoutTest extends AbstractContainerLayoutTest<LabelLayout, LabelContainerLayout> {

    private SizeConstraintsContainerLayout sizeConstraintsContainerLayout;

    private DirectionContainerLayout directionContainerLayout;

    @Before
    public void setUp() {
        super.setUp();
        currentLayout = new LabelLayout.Builder().build();
    }

    @Override
    protected LabelContainerLayout createInstance() {
        sizeConstraintsContainerLayout = spy(new SizeConstraintsContainerLayout(parent));
        directionContainerLayout = spy(new DirectionContainerLayout(parent));
        return new LabelContainerLayout(parent, sizeConstraintsContainerLayout, directionContainerLayout);
    }

    @Override
    protected LabelLayout getDefaultLayoutForTest() {
        return new LabelLayout.Builder().build();
    }

    @Test
    public void add() {
        tested.add(child, currentLayout);
        verify(sizeConstraintsContainerLayout).add(child, currentLayout.getSizeConstraints());
        verify(directionContainerLayout).add(child, currentLayout.getDirectionLayout());
    }

    @Test
    public void getMaxSize() {
        tested.add(child);
        final BoundingBox maxSize = tested.getMaxSize(child);
        verify(sizeConstraintsContainerLayout).getMaxSize(child);
        assertEquals(maxSize, parent.getBoundingBox());
    }

    @Test
    public void getMaxSizeFromNull() {
        final BoundingBox maxSize = tested.getMaxSize(child);
        verify(sizeConstraintsContainerLayout, never()).getMaxSize(any(IPrimitive.class));
        assertEquals(maxSize, new BoundingBox());
    }
}