/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CounterTest {

    private Counter counter;
    private static final int INIT_VALUE = 10;

    @Before
    public void setUp() throws Exception {
        counter = new Counter(INIT_VALUE);
    }

    @Test
    public void get() {
        assertEquals(counter.get(), INIT_VALUE);
        counter = new Counter();
        assertEquals(counter.get(), 0);
    }

    @Test
    public void increment() {
        assertEquals(counter.increment(), INIT_VALUE + 1);
        assertEquals(counter.increment(), INIT_VALUE + 2);
    }

    @Test
    public void decrement() {
        assertEquals(counter.decrement(), INIT_VALUE - 1);
        assertEquals(counter.decrement(), INIT_VALUE - 2);
    }

    @Test
    public void equalsToValue() {
        assertTrue(counter.equalsToValue(INIT_VALUE));
        assertFalse(counter.equalsToValue(INIT_VALUE + 1));
    }
}