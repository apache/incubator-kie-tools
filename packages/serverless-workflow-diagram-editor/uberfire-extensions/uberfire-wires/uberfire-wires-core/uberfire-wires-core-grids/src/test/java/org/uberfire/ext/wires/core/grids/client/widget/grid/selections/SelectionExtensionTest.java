/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.selections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SelectionExtensionTest {

    @Parameterized.Parameter(0)
    public SelectionExtension extension;
    @Parameterized.Parameter(1)
    public int deltaX;
    @Parameterized.Parameter(2)
    public int deltaY;
    @Parameterized.Parameter(3)
    public int expectedX1;
    @Parameterized.Parameter(4)
    public int expectedX2;
    @Parameterized.Parameter(5)
    public int expectedY1;
    @Parameterized.Parameter(6)
    public int expectedY2;

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
                {SelectionExtension.LEFT, -1, 0, 9, 4, 10, 5},
                {SelectionExtension.RIGHT, 1, 0, 11, 6, 10, 5},
                {SelectionExtension.UP, 0, -1, 10, 5, 9, 4},
                {SelectionExtension.DOWN, 0, 1, 10, 5, 11, 6}
        };
    }

    @Test
    public void check() {
        assertEquals(deltaX,
                     extension.getDeltaX());
        assertEquals(deltaY,
                     extension.getDeltaY());
        assertEquals(expectedX1,
                     extension.getNextX(5,
                                        10,
                                        5));
        assertEquals(expectedX2,
                     extension.getNextX(5,
                                        10,
                                        10));
        assertEquals(expectedY1,
                     extension.getNextY(5,
                                        10,
                                        5));
        assertEquals(expectedY2,
                     extension.getNextY(5,
                                        10,
                                        10));
    }
}
