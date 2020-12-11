/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.shared;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class XLSConversionResultMessageTest {

    @Test
    public void testEqualsAndHashCode() {
        final XLSConversionResultMessage a = new XLSConversionResultMessage(XLSConversionResultMessageType.DIALECT_NOT_CONVERTED, "test");
        final XLSConversionResultMessage b = new XLSConversionResultMessage(XLSConversionResultMessageType.DIALECT_NOT_CONVERTED, "test");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    @Test
    public void testEqualsAndHashCodeNotEqual() {
        final XLSConversionResultMessage a = new XLSConversionResultMessage(XLSConversionResultMessageType.DIALECT_NOT_CONVERTED, "test");
        final XLSConversionResultMessage b = new XLSConversionResultMessage(XLSConversionResultMessageType.DIALECT_NOT_CONVERTED, "another");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }
}