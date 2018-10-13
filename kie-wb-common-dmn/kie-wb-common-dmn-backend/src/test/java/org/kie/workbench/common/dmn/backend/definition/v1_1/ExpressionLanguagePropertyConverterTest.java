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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExpressionLanguagePropertyConverterTest {

    private static final String EXPRESSION_LANGUAGE = "feel";

    @Test
    public void testWBFromDMNWithNullValue() {
        assertEquals("", ExpressionLanguagePropertyConverter.wbFromDMN(null).getValue());
    }

    @Test
    public void testWBFromDMNWithNonNullValue() {
        assertEquals(EXPRESSION_LANGUAGE, ExpressionLanguagePropertyConverter.wbFromDMN(EXPRESSION_LANGUAGE).getValue());
    }

    @Test
    public void testDMNFromWBWithNull() {
        assertNull(ExpressionLanguagePropertyConverter.dmnFromWB(null));
    }

    @Test
    public void testDMNFromWBWithNullValue() {
        assertNull(ExpressionLanguagePropertyConverter.dmnFromWB(new ExpressionLanguage(null)));
    }

    @Test
    public void testDMNFromWBWithEmptyValue() {
        assertNull(ExpressionLanguagePropertyConverter.dmnFromWB(new ExpressionLanguage()));
    }

    @Test
    public void testDMNFromWBWithNonNullValue() {
        assertEquals(EXPRESSION_LANGUAGE, ExpressionLanguagePropertyConverter.dmnFromWB(new ExpressionLanguage(EXPRESSION_LANGUAGE)));
    }
}
