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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;

import static java.util.stream.Collectors.collectingAndThen;

public class FunctionsRegistry {

    private static final String BETWEEN = "between";
    private static final String IS_NULL = "isNull";
    private static final String EQUALS_TO = "equalsTo";
    private static final String IS_EMPTY = "isEmpty";
    private static final String CONTAINS = "contains";
    private static final String STARTS_WITH = "startsWith";
    private static final String ENDS_WITH = "endsWith";
    private static final String GREATER_THAN = "greaterThan";
    private static final String GREATER_OR_EQUAL_THAN = "greaterOrEqualThan";
    private static final String LESS_THAN = "lessThan";
    private static final String LESS_OR_EQUAL_THAN = "lessOrEqualThan";
    private static final String IS_TRUE = "isTrue";
    private static final String IS_FALSE = "isFalse";

    private static FunctionsRegistry instance = new FunctionsRegistry();

    private List<FunctionDef> registry = new ArrayList<>();

    private FunctionsRegistry() {
        initRegistry();
    }

    public static FunctionsRegistry getInstance() {
        if (instance == null) {
            instance = new FunctionsRegistry();
        }
        return instance;
    }

    public List<FunctionDef> getFunctions(String functionName) {
        return registry.stream()
                .filter(functionDef -> functionDef.getName().equals(functionName))
                .collect(collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public List<FunctionDef> getFunctions() {
        return Collections.unmodifiableList(registry);
    }

    private void initRegistry() {

        //Operators for all types:

        FunctionDef isNull = FunctionDef.FunctionDefBuilder.newFunction(IS_NULL)
                .withParam("param1", Object.class.getName())
                .build();
        registry.add(isNull);

        //Operators for String type:

        FunctionDef equalsTo = FunctionDef.FunctionDefBuilder.newFunction(EQUALS_TO)
                .withParam("param1", String.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(equalsTo);

        FunctionDef isEmpty = FunctionDef.FunctionDefBuilder.newFunction(IS_EMPTY)
                .withParam("param1", String.class.getName())
                .build();
        registry.add(isEmpty);

        FunctionDef contains = FunctionDef.FunctionDefBuilder.newFunction(CONTAINS)
                .withParam("param1", String.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(contains);

        FunctionDef startsWith = FunctionDef.FunctionDefBuilder.newFunction(STARTS_WITH)
                .withParam("param1", String.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(startsWith);

        FunctionDef endsWith = FunctionDef.FunctionDefBuilder.newFunction(ENDS_WITH)
                .withParam("param1", String.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(endsWith);

        // Operators for Numeric types:

        FunctionDef equalsToNumeric = FunctionDef.FunctionDefBuilder.newFunction(EQUALS_TO)
                .withParam("param1", Number.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(equalsToNumeric);

        FunctionDef greaterThan = FunctionDef.FunctionDefBuilder.newFunction(GREATER_THAN)
                .withParam("param1", Number.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(greaterThan);

        FunctionDef greaterOrEqualThan = FunctionDef.FunctionDefBuilder.newFunction(GREATER_OR_EQUAL_THAN)
                .withParam("param1", Number.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(greaterOrEqualThan);

        FunctionDef lessThan = FunctionDef.FunctionDefBuilder.newFunction(LESS_THAN)
                .withParam("param1", Number.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(lessThan);

        FunctionDef lessOrEqualThan = FunctionDef.FunctionDefBuilder.newFunction(LESS_OR_EQUAL_THAN)
                .withParam("param1", Number.class.getName())
                .withParam("param2", String.class.getName())
                .build();
        registry.add(lessOrEqualThan);

        FunctionDef between = FunctionDef.FunctionDefBuilder.newFunction(BETWEEN)
                .withParam("param1", Number.class.getName())
                .withParam("param2", String.class.getName())
                .withParam("param3", String.class.getName())
                .build();
        registry.add(between);

        // Operators for Boolean type:

        FunctionDef isTrue = FunctionDef.FunctionDefBuilder.newFunction(IS_TRUE)
                .withParam("param1", Boolean.class.getName())
                .build();
        registry.add(isTrue);

        FunctionDef isFalse = FunctionDef.FunctionDefBuilder.newFunction(IS_FALSE)
                .withParam("param1", Boolean.class.getName())
                .build();
        registry.add(isFalse);
    }
}
