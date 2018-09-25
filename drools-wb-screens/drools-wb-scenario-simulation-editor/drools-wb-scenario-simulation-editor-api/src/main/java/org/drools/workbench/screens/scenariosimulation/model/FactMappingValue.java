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

import java.util.Optional;

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
        return operator != null ? operator : extractOperator(rawValue);
    }

    public Object getCleanValue() {
        return cleanValue(rawValue);
    }

    FactMappingValue cloneFactMappingValue() {
        FactMappingValue cloned = new FactMappingValue();
        cloned.expressionIdentifier = expressionIdentifier;
        cloned.factIdentifier = factIdentifier;
        cloned.rawValue = rawValue;
        return cloned;
    }

    public static Object cleanValue(Object rawValue) {
        if (!(rawValue instanceof String)) {
            return rawValue;
        }
        String value = ((String) rawValue).trim();

        FactMappingValueOperator operator = FactMappingValueOperator.findOperator(value);
        Optional<String> first = operator.getSymbols().stream().filter(value::startsWith).findFirst();
        if (first.isPresent()) {
            String symbolToRemove = first.get();
            int index = value.indexOf(symbolToRemove);
            value = value.substring(index + symbolToRemove.length()).trim();
        }

        return value.trim();
    }

    public static FactMappingValueOperator extractOperator(Object rawValue) {
        if (!(rawValue instanceof String)) {
            return FactMappingValueOperator.EQUALS;
        }

        String value = (String) rawValue;

        return FactMappingValueOperator.findOperator(value);
    }

    public static String getPlaceHolder() {
        return "Empty value";
    }

    public static String getPlaceHolder(int index) {
        return getPlaceHolder() + " " + index;
    }

    public static String getPlaceHolder(int rowIndex, int colIndex) {
        return getPlaceHolder() + " " + rowIndex + " " + colIndex;
    }
}
