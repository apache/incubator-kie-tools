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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.Objects;

import org.drools.workbench.screens.scenariosimulation.model.FactMappingValueOperator;

public class OperatorEvaluator {

    @SuppressWarnings("unchecked")
    public Boolean evaluate(FactMappingValueOperator operator, Object resultValue, Object expectedValue) {
        switch (operator) {
            case EQUALS:
                if (areComparable(resultValue, expectedValue)) {
                    return ((Comparable) resultValue).compareTo(expectedValue) == 0;
                }
                return Objects.equals(resultValue, expectedValue);
            case NOT_EQUALS:
                if (areComparable(resultValue, expectedValue)) {
                    return ((Comparable) resultValue).compareTo(expectedValue) != 0;
                }
                return !Objects.equals(resultValue, expectedValue);
            default:
                throw new UnsupportedOperationException(new StringBuilder().append("Operator ").append(operator.name())
                                                                .append(" is not supported").toString());
        }
    }

    private boolean areComparable(Object a, Object b) {
        return a instanceof Comparable && b instanceof Comparable;
    }
}
