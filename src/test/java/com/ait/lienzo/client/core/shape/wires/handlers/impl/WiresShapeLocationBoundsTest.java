/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeLocationBoundsTest {

    private static BoundingBox SHAPE_BOUNDS = new BoundingBox(1.1d, 2.2d, 333.333d, 444.444d);

    private WiresShapeLocationBounds tested;

    @Before
    public void setup() {
        tested = new WiresShapeLocationBounds(new Supplier<BoundingBox>() {
            @Override
            public BoundingBox get() {
                return SHAPE_BOUNDS;
            }
        });
    }

    @Test
    public void testNoConstraints() {
        assertFalse(tested.isOutOfBounds(0d, 0d));
        assertFalse(tested.isOutOfBounds(1200d, 1200d));
    }

    @Test
    public void testConstraints() {
        tested.setBounds(OptionalBounds.create(0d, 0d, 1200d, 1200d));
        assertFalse(tested.isOutOfBounds(0d, 0d));
        assertFalse(tested.isOutOfBounds(-1d, -1d));
        assertFalse(tested.isOutOfBounds(700d, 600d));
        assertTrue(tested.isOutOfBounds(-3d, -3d));
        assertTrue(tested.isOutOfBounds(-3d, 0d));
        assertTrue(tested.isOutOfBounds(0d, -3d));
        assertTrue(tested.isOutOfBounds(0d, 1000d));
        assertTrue(tested.isOutOfBounds(1000d, 0d));
        assertTrue(tested.isOutOfBounds(1000d, 1000d));
    }

    @Test
    public void testClear() {
        tested.setBounds(OptionalBounds.create(0d, 0d, 1200d, 1200d));
        tested.clear();
        assertNull(tested.getBounds());
    }
}
