/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.Arrays;
import java.util.Optional;

public enum ExpressionType {

    UNDEFINED("<Undefined>"),
    LITERAL_EXPRESSION("Literal"),
    CONTEXT("Context"),
    DECISION_TABLE("Decision table"),
    RELATION("Relation"),
    FUNCTION("Function"),
    FUNCTION_JAVA("Function Java"),
    FUNCTION_PMML("Function PMML"),
    INVOCATION("Invocation"),
    LIST("List");

    private final String text;

    ExpressionType(final String text) {
        this.text = text;
    }

    public static ExpressionType getTypeByText(final String text) {
        final String trimmedText = Optional.ofNullable(text).orElse("").trim();
        return Arrays
                .stream(values())
                .filter(expressionType -> expressionType.getText().equalsIgnoreCase(trimmedText))
                .findAny()
                .orElse(UNDEFINED);
    }

    public String getText() {
        return text;
    }
}
