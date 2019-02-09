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

package org.kie.workbench.common.dmn.api.editors.types;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RangeValueTest {

    @Test
    public void testDefaultValues() {
        final RangeValue range = new RangeValue();
        assertEquals("", range.getStartValue());
        assertEquals("", range.getEndValue());
        assertFalse(range.getIncludeStartValue());
        assertFalse(range.getIncludeEndValue());
    }

    @Test
    public void testIncludeStartValue() {
        final RangeValue range = new RangeValue();
        final boolean expected = true;
        range.setIncludeStartValue(true);
        assertEquals(expected, range.getIncludeStartValue());
    }

    @Test
    public void testStartValue() {
        final RangeValue range = new RangeValue();
        final String expected = "something";
        range.setStartValue("something");
        assertEquals(expected, range.getStartValue());
    }

    @Test
    public void testEndValue() {
        final RangeValue range = new RangeValue();
        final String expected = "something";
        range.setEndValue("something");
        assertEquals(expected, range.getEndValue());
    }

    @Test
    public void testIncludeEndValue() {
        final RangeValue range = new RangeValue();
        final boolean expected = true;
        range.setIncludeEndValue(true);
        assertEquals(expected, range.getIncludeEndValue());
    }
}