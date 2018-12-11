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
                {1, 1, int.class},
                {1, "1", int.class},
                {2, "!= 1", int.class},
                {-1, "- 1", int.class},
                {-2, "< -  1", int.class},
                {-2L, "< -  1", long.class},
                {-2D, "< -  1", double.class},
                {-2F, "< -  1", float.class},
                {(short) -2, "< - 1", short.class},
                {"String", "<> Test", String.class},
                {"Test", "= Test", String.class},
                {1, "<2", int.class},
                {1, "<2; >0", int.class},
                {2, " <= 2 ", int.class},
                {2, " >= 2", int.class},
                {1, "[ 1, 2 ,3]", int.class},
                {2, "[ 1, 2 ,3]", int.class},
                {"3", "[ 1, 2 ,3]", String.class},
                {4, "![ 1, 2 ,3]", int.class},
                {4, "! < 1", int.class},
                {1, "> -1", int.class},
                {10, "!= <10;!= >11", int.class},
                {10, "= 10; >9", int.class},
                {null, null, Integer.class},
                {null, "!1", Integer.class},
                {'b', "!a", Character.class},
                {"0".getBytes()[0], "!b", Byte.class},
                {(short) 1, ">0", Short.class},
                {null, "[ !false]", boolean.class},
                {null, "[! false, ! true]", boolean.class},
                {10, "[> 1]", int.class},
                {10, "[< 1, > 1]", int.class},
                {Error.class, "!= false; <> false, ! false", boolean.class},
                {Error.class, "<> false, ! false", boolean.class},
                {Error.class, "! tru", void.class},
                {Error.class, "fals", void.class},
                {Error.class, "!= fals", void.class},
                {Error.class, "tru", void.class},
                {Error.class, "<> fals", void.class},
                {Error.class, "tru", void.class},
                {Error.class, "!m= false", void.class},
                {Error.class, ">> 3", void.class},
                {Error.class, "< - 1 1", int.class}
        });
    }

    @Parameterized.Parameter(0)
    public Object resultValue;

    @Parameterized.Parameter(1)
    public Object exprToTest;

    @Parameterized.Parameter(2)
    public Class<?> clazz;

    @Test
    public void evaluate() {

        if (!(resultValue instanceof Class)) {
            assertTrue(baseExpressionEvaluator.evaluate(exprToTest, resultValue, clazz));
        } else {
            try {
                baseExpressionEvaluator.evaluate(exprToTest, true, clazz);
                fail();
            } catch (Exception ignored) {
            }
        }
    }
}