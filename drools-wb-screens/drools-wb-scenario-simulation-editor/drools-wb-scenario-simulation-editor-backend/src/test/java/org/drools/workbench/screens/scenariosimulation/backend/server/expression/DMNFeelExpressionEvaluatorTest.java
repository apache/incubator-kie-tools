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

package org.drools.workbench.screens.scenariosimulation.backend.server.expression;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DMNFeelExpressionEvaluatorTest {

    DMNFeelExpressionEvaluator expressionEvaluator = new DMNFeelExpressionEvaluator(this.getClass().getClassLoader());

    @Test
    public void evaluate() {
        assertTrue(expressionEvaluator.evaluate("not( true )", false, boolean.class));
        assertTrue(expressionEvaluator.evaluate(">2, >5", BigDecimal.valueOf(6), BigDecimal.class));
        assertThatThrownBy(() -> expressionEvaluator.evaluate(new Object(), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Raw expression should be a string");
    }

    @Test
    public void getValueForGiven() {
        assertEquals(BigDecimal.valueOf(5), expressionEvaluator.getValueForGiven(BigDecimal.class.getCanonicalName(), "2 + 3"));
        Object nonStringObject = new Object();
        assertEquals(nonStringObject, expressionEvaluator.getValueForGiven("class", nonStringObject));
    }
}