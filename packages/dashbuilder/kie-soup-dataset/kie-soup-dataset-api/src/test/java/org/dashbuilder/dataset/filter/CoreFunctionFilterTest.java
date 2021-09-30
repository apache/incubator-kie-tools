/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.filter;

import org.junit.Test;

import static org.dashbuilder.dataset.filter.CoreFunctionType.*;
import static org.junit.Assert.*;

public class CoreFunctionFilterTest {

    @Test
    public void testEquals1() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", IS_NULL);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column1", IS_NULL);

        assertEquals(cf1, cf2);
    }

    @Test
    public void testEquals2() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", EQUALS_TO, 1);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column1", EQUALS_TO, 1);

        assertEquals(cf1, cf2);
    }

    @Test
    public void testEquals3() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", BETWEEN, 1, 1000);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column1", BETWEEN, 1, 1000);

        assertEquals(cf1, cf2);
    }

    @Test
    public void testNotEquals1() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", EQUALS_TO, 1);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column2", EQUALS_TO, 1);

        assertNotEquals(cf1, cf2);
    }

    @Test
    public void testNotEquals2() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", EQUALS_TO, 1);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column1", NOT_EQUALS_TO, 1);

        assertNotEquals(cf1, cf2);
    }

    @Test
    public void testNotEquals3() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", EQUALS_TO, 1);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column1", EQUALS_TO, 2);

        assertNotEquals(cf1, cf2);
    }

    @Test
    public void testNotEquals4() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", EQUALS_TO, 1);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column1", EQUALS_TO, 1, 2);

        assertNotEquals(cf1, cf2);
    }

    @Test
    public void testNotEquals5() {
        CoreFunctionFilter cf1 = new CoreFunctionFilter("column1", EQUALS_TO, 1, 1);
        CoreFunctionFilter cf2 = new CoreFunctionFilter("column1", EQUALS_TO, 1, 2);
        CoreFunctionFilter cf3 = new CoreFunctionFilter("column1", EQUALS_TO, 2, 1);

        assertNotEquals(cf1, cf2);
        assertNotEquals(cf2, cf1);
        assertNotEquals(cf2, cf3);
        assertNotEquals(cf3, cf2);
    }
}
