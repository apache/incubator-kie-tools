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

package org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConditionTestCommons {

    public static final List<String> variableParams = Arrays.asList("_a",
                                                                    "_a.getA()",
                                                                    "_ä",
                                                                    "_ä.getÄ()",
                                                                    "$a",
                                                                    "$a.getA()",
                                                                    "_someVar",
                                                                    "_someVar.getValue()",
                                                                    "_äwsomeVar1",
                                                                    "_äwsomeVar1.anotherMethod()",
                                                                    "$asdfs",
                                                                    "$asdfs.isMember()");

    public static final List<String> stringParams = Arrays.asList("_a",
                                                                  "_a.getA()",
                                                                  "_ä",
                                                                  "_ä.getÄ()",
                                                                  "$a",
                                                                  "$a.getA()",
                                                                  "_someVar",
                                                                  "_someVar.getValue()",
                                                                  "_äwsomeVar1",
                                                                  "_äwsomeVar1.anotherMethod()",
                                                                  "$asdfs",
                                                                  "$asdfs.isMember()");

    public static final List<String> unaryFunctions = Arrays.asList("isNull",
                                                                    "isEmpty",
                                                                    "isTrue",
                                                                    "isFalse");

    public static final List<String> binaryFunctions = Arrays.asList("equalsTo",
                                                                     "contains",
                                                                     "startsWith",
                                                                     "endsWith",
                                                                     "greaterThan",
                                                                     "greaterOrEqualThan",
                                                                     "lessThan",
                                                                     "lessOrEqualThan");

    public static final List<String> ternaryFunctions = Arrays.asList("between");

    public static final List<String> failingUnaryFunctionExpressions = Arrays.asList("",
                                                                                     "   ",
                                                                                     "return",
                                                                                     "return " + "%s",
                                                                                     "return " + "%s(",
                                                                                     "return " + "%s(variable",
                                                                                     "return " + "%s(variable)",
                                                                                     "return " + "%s(variable.getValue",
                                                                                     "return " + "%s(variable.getValue(",
                                                                                     "return " + "%s(variable.getValue()",
                                                                                     "return " + "%s(variable.getValue())",
                                                                                     "return " + "%s(1wrongVariable));",
                                                                                     "return " + "%s(variable.1wrongMethod());");

    public static final List<String> failingBinaryFunctionExpressions = Arrays.asList("",
                                                                                      "   ",
                                                                                      "return",
                                                                                      "return " + "%s",
                                                                                      "return " + "%s(",
                                                                                      "return " + "%s(variable",
                                                                                      "return " + "%s(variable)",
                                                                                      "return " + "%s(variable.getValue",
                                                                                      "return " + "%s(variable.getValue(",
                                                                                      "return " + "%s(variable.getValue()",
                                                                                      "return " + "%s(variable.getValue(),",
                                                                                      "return " + "%s(1wrongVariable1,);",
                                                                                      "return " + "%s(variable.1wrongMethod1(),);",
                                                                                      "return " + "%s(variable, value1\")",
                                                                                      "return " + "%s(variable, \"value1)",
                                                                                      "return " + "%s(variable, \"value1\")",
                                                                                      "return " + "%s(variable.getValue(), value1\"",
                                                                                      "return " + "%s(variable.getValue(), \"value1",
                                                                                      "return " + "%s(variable.getValue(), \"value1\")");

    public static final List<String> failingTernaryFunctionExpressions = Arrays.asList("",
                                                                                       "   ",
                                                                                       "return",
                                                                                       "return " + "%s",
                                                                                       "return " + "%s(",
                                                                                       "return " + "%s(variable",
                                                                                       "return " + "%s(variable)",
                                                                                       "return " + "%s(variable.getValue",
                                                                                       "return " + "%s(variable.getValue(",
                                                                                       "return " + "%s(variable.getValue()",
                                                                                       "return " + "%s(variable.getValue(),",
                                                                                       "return " + "%s(1wrongVariable1,);",
                                                                                       "return " + "%s(variable.1wrongMethod1(),);",
                                                                                       "return " + "%s(variable, value1\")",
                                                                                       "return " + "%s(variable, \"value1)",
                                                                                       "return " + "%s(variable, \"value1\")",
                                                                                       "return " + "%s(variable.getValue(), value1\"",
                                                                                       "return " + "%s(variable.getValue(), \"value1",
                                                                                       "return " + "%s(variable.getValue(), \"value1\")",
                                                                                       "return " + "%s(variable, \"value1\", value2\")",
                                                                                       "return " + "%s(variable, \"value1\", \"value2)",
                                                                                       "return " + "%s(variable, \"value1\", \"value2\")",
                                                                                       "return " + "%s(variable.getValue(), \"value1\", value2\")",
                                                                                       "return " + "%s(variable.getValue(), \"value1\", \"value2)",
                                                                                       "return " + "%s(variable.getValue(), \"value1\", \"value2\")");

    public static List<String> buildUnaryConditionScripts(String function) {
        List<String> unaryConditionScripts = new ArrayList<>();
        for (String variableParam : variableParams) {
            unaryConditionScripts.add(buildUnaryConditionScript(function, variableParam));
        }
        return unaryConditionScripts;
    }

    public static List<String> buildBinaryConditionScripts(String function) {
        List<String> binaryConditionScripts = new ArrayList<>();
        for (int i = 0; i < variableParams.size(); i++) {
            binaryConditionScripts.add(buildBinaryConditionScript(function, variableParams.get(i), stringParams.get(i)));
        }
        return binaryConditionScripts;
    }

    public static List<String> buildTernaryConditionScripts(String function) {
        List<String> ternaryConditionScripts = new ArrayList<>();
        for (int i = 0; i < variableParams.size(); i++) {
            ternaryConditionScripts.add(buildTernaryConditionScript(function, variableParams.get(i), stringParams.get(i), stringParams.get(i)));
        }
        return ternaryConditionScripts;
    }

    public static String buildUnaryConditionScript(String function, String variable) {
        return String.format("return KieFunctions.%s(%s);", function, variable);
    }

    public static String buildBinaryConditionScript(String function, String variable, String param) {
        return String.format("return KieFunctions.%s(%s, \"%s\");", function, variable, param);
    }

    public static String buildTernaryConditionScript(String function, String variable, String param1, String param2) {
        return String.format("return KieFunctions.%s(%s, \"%s\", \"%s\");", function, variable, param1, param2);
    }
}
