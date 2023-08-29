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

package com.ait.lienzo.client.core.shape.wires.layout.size;

import com.ait.lienzo.client.core.shape.wires.layout.AbstractContainerLayoutTest;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints.Type;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class SizeConstraintsContainerLayoutTest extends AbstractContainerLayoutTest<SizeConstraints, SizeConstraintsContainerLayout> {

    @Before
    public void setUp() {
        super.setUp();
        currentLayout = new SizeConstraints();
    }

    @Override
    protected SizeConstraintsContainerLayout createInstance() {
        return new SizeConstraintsContainerLayout(parent);
    }

    @Test
    public void getMaxSizePercentage() {
        tested.add(child, new SizeConstraints(50, 50, Type.PERCENTAGE));
        final BoundingBox maxSize = tested.getMaxSize(child);
        assertEquals(maxSize.getWidth(), parent.getBoundingBox().getWidth() / 2, 0d);
        assertEquals(maxSize.getHeight(), parent.getBoundingBox().getHeight() / 2, 0d);
    }

    @Test
    public void getMaxSizePx() {
        tested.add(child, new SizeConstraints(30, 15, Type.RAW));
        final BoundingBox maxSize = tested.getMaxSize(child);
        assertEquals(30, maxSize.getWidth(), 0d);
        assertEquals(15, maxSize.getHeight(), 0d);
    }

    @Override
    protected SizeConstraints getDefaultLayoutForTest() {
        return new SizeConstraints(100, 100, Type.PERCENTAGE);
    }
}