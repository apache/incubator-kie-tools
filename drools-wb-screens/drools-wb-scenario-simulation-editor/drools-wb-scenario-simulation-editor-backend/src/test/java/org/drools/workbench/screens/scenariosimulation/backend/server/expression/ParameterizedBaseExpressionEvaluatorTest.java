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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ParameterizedBaseExpressionEvaluatorTest {

    private final static ClassLoader classLoader = ParameterizedBaseExpressionEvaluatorTest.class.getClassLoader();
    private final static BaseExpressionEvaluator baseExpressionEvaluator = new BaseExpressionEvaluator(classLoader);

    @Parameterized.Parameters(name = "{index}: Expr \"{0} {1}\" should be true")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1, 1},
                {1, "1"},
                {2, "!= 1"},
                {"String", "<> Test"},
                {"Test", "= Test"},
                {1, "<2"},
                {1, "<2; >0"},
                {2, " <= 2 "},
                {2, " >= 2"},
                {1, "[ 1, 2 ,3]"},
                {2, "[ 1, 2 ,3]"},
                {"3", "[ 1, 2 ,3]"},
                {4, "![ 1, 2 ,3]"},
                {4, "! < 1"},
                {1, "> -1"},
                {10, "!= <10;!= >11"},
                {10, "= 10; >9"},
                {Error.class, "! tru"},
                {Error.class, "fals"},
                {Error.class, "!= fals"},
                {Error.class, "tru"},
                {Error.class, "<> fals"},
                {Error.class, "tru"},
                {Error.class, "!m= false"},
                {Error.class, ">> 3"}
        });
    }

    @Parameterized.Parameter(0)
    public Object resultValue;

    @Parameterized.Parameter(1)
    public Object exprToTest;

    @Test
    public void evaluate() {

        if (!(resultValue instanceof Class)) {
            assertTrue(baseExpressionEvaluator.evaluate(exprToTest, resultValue));
        } else {
            try {
                baseExpressionEvaluator.evaluate(exprToTest, true);
                fail();
            } catch (Exception ignored) {
            }
        }
    }
}