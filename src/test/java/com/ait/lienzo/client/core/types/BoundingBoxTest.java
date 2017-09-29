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

        assertEquals(box.getX(), -1000d, Double.MIN_VALUE);
        assertEquals(box.getY(), -200d, Double.MIN_VALUE);
        assertEquals(box.getWidth(), 900d, Double.MIN_VALUE);
        assertEquals(box.getHeight(), 500d, Double.MIN_VALUE);
    }
}
