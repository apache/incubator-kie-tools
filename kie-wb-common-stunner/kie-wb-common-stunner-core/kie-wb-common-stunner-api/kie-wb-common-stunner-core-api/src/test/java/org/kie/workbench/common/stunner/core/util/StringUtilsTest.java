/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void testNull() {
        assertTrue(StringUtils.isEmpty(null));
        assertFalse(StringUtils.nonEmpty(null));
    }

    @Test
    public void testEmpty() {
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.nonEmpty(""));
    }

    @Test
    public void testNonNullNonEmpty() {
        assertFalse(StringUtils.isEmpty("string"));
        assertTrue(StringUtils.nonEmpty("string"));
    }

    @Test
    public void testHasNonEmpty() {
        assertFalse(StringUtils.hasNonEmpty((String[]) null));
        assertFalse(StringUtils.hasNonEmpty((String) null));
        assertFalse(StringUtils.hasNonEmpty(null, null, null));
        assertFalse(StringUtils.hasNonEmpty(null, "", null));
        assertTrue(StringUtils.hasNonEmpty(null, "", "someValue"));
    }

    @Test
    public void testCreateQuotedConstantNull() {
        assertNull(StringUtils.createQuotedString(null));
    }

    @Test
    public void testCreateQuotedConstantEmpty() {
        assertEquals("",
                     StringUtils.createQuotedString(""));
    }

    @Test
    public void testCreateQuotedConstantNumber() {
        assertEquals("\"-123\"",
                     StringUtils.createQuotedString("-123"));
    }

    @Test
    public void testCreateQuotedConstant() {
        assertEquals("\" abc \"",
                     StringUtils.createQuotedString(" abc "));
    }

    @Test
    public void testCreateQuotedConstantOptionalNumericNull() {
        assertNull(StringUtils.createQuotedStringIfNotNumeric(null));
    }

    @Test
    public void testCreateQuotedConstantOptionalNumericEmpty() {
        assertEquals("",
                     StringUtils.createQuotedStringIfNotNumeric(""));
    }

    @Test
    public void testCreateQuotedConstantOptionalNumericNumber() {
        assertEquals("-123",
                     StringUtils.createQuotedStringIfNotNumeric("-123"));
    }

    @Test
    public void testCreateQuotedConstantOptionalNumeric() {
        assertEquals("\" abc \"",
                     StringUtils.createQuotedStringIfNotNumeric(" abc "));
    }

    @Test
    public void testIsQuotedConstantNull() {
        assertFalse(StringUtils.isQuoted(null));
    }

    @Test
    public void testIsQuotedConstantEmpty() {
        assertFalse(StringUtils.isQuoted(""));
    }

    @Test
    public void testIsQuotedConstantOpeningQuote() {
        assertFalse(StringUtils.isQuoted("\"a"));
    }

    @Test
    public void testIsQuotedConstantClosingQuote() {
        assertFalse(StringUtils.isQuoted("a\""));
    }

    @Test
    public void testIsQuotedConstantOpeningAndClosingQuote() {
        assertTrue(StringUtils.isQuoted("\"a\""));
    }

    @Test
    public void testCreateUnquotedConstantNull() {
        assertNull(StringUtils.createUnquotedString(null));
    }

    @Test
    public void testCreateUnquotedConstantEmpty() {
        assertEquals("",
                     StringUtils.createUnquotedString(""));
    }

    @Test
    public void testCreateUnquotedConstantNoAction() {
        assertEquals("-123",
                     StringUtils.createUnquotedString("-123"));
    }

    @Test
    public void testCreateUnquotedConstant() {
        assertEquals(" abc ",
                     StringUtils.createUnquotedString("\" abc \""));
    }
}
