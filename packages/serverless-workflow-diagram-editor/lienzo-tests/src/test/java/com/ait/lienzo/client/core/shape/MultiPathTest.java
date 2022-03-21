/*
 *
 *    Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public class MultiPathTest {

    private MultiPath tested;

    @Before
    public void setup() {
        tested = spy(new MultiPath().rect(0, 0, 100, 100));

        assertNull(tested.m_box);
    }

    @Test
    public void testSetMinWidth() {
        tested.setMinWidth(null);
        assertNull(tested.getMinWidth());

        tested.setMinWidth(20d);
        assertEquals(20d, tested.getMinWidth(), 0.0001);
    }

    @Test
    public void testSetMaxWidth() {
        tested.setMaxWidth(null);
        assertNull(tested.getMaxWidth());

        tested.setMaxWidth(150d);
        assertEquals(150d, tested.getMaxWidth(), 0.0001);
    }

    @Test
    public void testSetMinHeight() {
        tested.setMinHeight(null);
        assertNull(tested.getMinHeight());

        tested.setMinHeight(20d);
        assertEquals(20d, tested.getMinHeight(), 0.0001);
    }

    @Test
    public void testSetMaxHeight() {
        tested.setMaxHeight(null);
        assertNull(tested.getMaxHeight());

        tested.setMaxHeight(150d);
        assertEquals(150d, tested.getMaxHeight(), 0.0001);
    }
}
