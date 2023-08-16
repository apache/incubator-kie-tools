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


package org.kie.workbench.common.stunner.core.graph.content;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BoundsTest {

    public static final double ULX = 50;
    public static final double ULY = 65;
    public static final double BRX = 212;
    public static final double BRY = 430.34;
    public static final double WIDTH = BRX - ULX;
    public static final double HEIGHT = BRY - ULY;

    @Test
    public void testAllValues() {
        Bounds tested = Bounds.create(ULX, ULY, BRX, BRY);
        assertTrue(tested.hasUpperLeft());
        assertTrue(tested.hasLowerRight());
        assertTrue(tested.getUpperLeft().hasX());
        assertTrue(tested.getUpperLeft().hasY());
        assertTrue(tested.getLowerRight().hasX());
        assertTrue(tested.getLowerRight().hasY());
        assertEquals(ULX,
                     tested.getUpperLeft().getX(),
                     0d);
        assertEquals(ULX,
                     tested.getX(),
                     0d);
        assertEquals(ULY,
                     tested.getUpperLeft().getY(),
                     0d);
        assertEquals(ULY,
                     tested.getY(),
                     0d);
        assertEquals(BRX,
                     tested.getLowerRight().getX(),
                     0d);
        assertEquals(BRY,
                     tested.getLowerRight().getY(),
                     0d);
        assertEquals(WIDTH,
                     tested.getWidth(),
                     0d);
        assertEquals(HEIGHT,
                     tested.getHeight(),
                     0d);
    }
}
