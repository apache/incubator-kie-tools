/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.client.core.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ait.lienzo.test.LienzoMockitoTestRunner;

@RunWith(LienzoMockitoTestRunner.class)
public class BoundingBoxTest
{
    @Test
    public void testInit()
    {
        final BoundingBox box = new BoundingBox(-1000, -200, -100, 300);

        assertEquals(box.getX(), -1000d, 0);
        assertEquals(box.getY(), -200d, 0);
        assertEquals(box.getWidth(), 900d, 0);
        assertEquals(box.getHeight(), 500d, 0);
    }

    @Test
    public void testIntersect()
    {
        BoundingBox box1 = new BoundingBox(0, 0, 100, 100);
        BoundingBox box2 = new BoundingBox(50, 50, 150, 150);
        BoundingBox box3 = new BoundingBox(100, 100, 0, 0);
        BoundingBox box4 = new BoundingBox(200, 200, 0, 0);
        BoundingBox box5 = new BoundingBox(10, 10, 25, 25);
        assertTrue(box1.intersects(box2));
        assertTrue(box2.intersects(box1));
        assertTrue(box3.intersects(box2));
        assertTrue(box3.intersects(box1));
        assertTrue(box4.intersects(box2));
        assertTrue(box4.intersects(box1));
        assertTrue(box5.intersects(box1));
        assertFalse(box5.intersects(box2));
        assertTrue(box5.intersects(box3));
        assertTrue(box5.intersects(box4));
        assertTrue(box1.intersects(box5));
        assertFalse(box2.intersects(box5));
        assertTrue(box3.intersects(box5));
        assertTrue(box4.intersects(box5));
    }

}
