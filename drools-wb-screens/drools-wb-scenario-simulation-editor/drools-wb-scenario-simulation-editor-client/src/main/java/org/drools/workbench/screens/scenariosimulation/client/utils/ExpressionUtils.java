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

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.EXPRESSION_VALUE_PREFIX;

/**
 * This utils class holds all shared functions used by <b>Expression</b> handling, where expressions are
 * used defined expression code put on GIVEN/EXPECTED data cells
 */
public class ExpressionUtils {

    private ExpressionUtils() {
        // Not instantiable
    }

    /**
     * It ensures if the given string starts with <code>EXPRESSION_VALUE_PREFIX</code>. If TRUE, it simply returns
     * the given string. Otherwise, it adds <code>EXPRESSION_VALUE_PREFIX</code> as prefix of the given string.
     * @param expressionValue
     * @return
     */
    public static String ensureExpressionSyntax(String expressionValue) {
        if (expressionValue != null && !expressionValue.startsWith(EXPRESSION_VALUE_PREFIX)) {
            if (expressionValue.startsWith(MVEL_ESCAPE_SYMBOL)) {
                return expressionValue.replaceFirst(MVEL_ESCAPE_SYMBOL, EXPRESSION_VALUE_PREFIX);
            }
            if (expressionValue.startsWith(" ")) {
                return expressionValue.replaceFirst(" ", EXPRESSION_VALUE_PREFIX);
            }
            return EXPRESSION_VALUE_PREFIX + expressionValue;
        }
        return expressionValue;
    }
}