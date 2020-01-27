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
package org.drools.workbench.screens.scenariosimulation.client.utils;

import org.drools.scenariosimulation.api.utils.ConstantsHolder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExpressionUtilsTest {

    @Test
    public void checkExpressionSyntax_Null() {
        assertNull(ExpressionUtils.ensureExpressionSyntax(null));
    }

    @Test
    public void checkExpressionSyntax_Empty() {
        assertEquals(ConstantHolder.EXPRESSION_VALUE_PREFIX, ExpressionUtils.ensureExpressionSyntax(""));
    }

    @Test
    public void checkExpressionSyntax_StringWithNoPrefix() {
        assertEquals(ConstantHolder.EXPRESSION_VALUE_PREFIX + "Test", ExpressionUtils.ensureExpressionSyntax("Test"));
    }

    @Test
    public void checkExpressionSyntax_StringWithSpacePrefix() {
        assertEquals(ConstantHolder.EXPRESSION_VALUE_PREFIX + "Test", ExpressionUtils.ensureExpressionSyntax(" Test"));
    }

    @Test
    public void checkExpressionSyntax_StringWithMVelPrefix() {
        assertEquals(ConstantHolder.EXPRESSION_VALUE_PREFIX + "Test", ExpressionUtils.ensureExpressionSyntax(ConstantsHolder.MVEL_ESCAPE_SYMBOL + "Test"));
    }

    @Test
    public void checkExpressionSyntax_StringWithExpressionValuePrefix() {
        assertEquals(ConstantHolder.EXPRESSION_VALUE_PREFIX + "Test", ExpressionUtils.ensureExpressionSyntax(ConstantHolder.EXPRESSION_VALUE_PREFIX + "Test"));
    }
}