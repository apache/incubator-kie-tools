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
package org.drools.workbench.screens.scenariosimulation.model;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * FactMappingValue contains the identifier of a fact mapping + the raw value
 */
@Portable
public class FactMappingValue {

    private FactIdentifier factIdentifier;
    private ExpressionIdentifier expressionIdentifier;
    private Object rawValue;
    /**
     * Each mapping value is bound with an operator. Default is equals that can be used as assignment in an "GIVEN" value or as equality check with "EXPECTED"
     */
    private FactMappingValueOperator operator = FactMappingValueOperator.EQUALS;

    public FactMappingValue() {
    }

    public FactMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier, Object rawValue) {
        this.factIdentifier = factIdentifier;
        this.expressionIdentifier = expressionIdentifier;
        this.rawValue = rawValue;
    }

    public FactMappingValue(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier, Object rawValue, FactMappingValueOperator operator) {
        this(factIdentifier, expressionIdentifier, rawValue);
        this.operator = operator;
    }

    public void setRawValue(Object rawValue) {
        this.rawValue = rawValue;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public ExpressionIdentifier getExpressionIdentifier() {
        return expressionIdentifier;
    }

    public Object getRawValue() {
        return rawValue;
    }

    public FactMappingValueOperator getOperator() {
        return operator;
    }
}
