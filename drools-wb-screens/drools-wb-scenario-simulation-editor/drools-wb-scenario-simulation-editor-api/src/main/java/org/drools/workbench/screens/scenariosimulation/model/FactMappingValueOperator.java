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

import java.util.Arrays;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * @see org.drools.workbench.screens.scenariosimulation.backend.server.OperatorEvaluator
 */
@Portable
public enum FactMappingValueOperator {

    EQUALS("="),
    NOT_EQUALS("!", "!=", "<>");

    final List<String> symbols;

    FactMappingValueOperator(String... symbols) {
        this.symbols = Arrays.asList(symbols);
        // sort symbols by descending length to match longer symbols first
        this.symbols.sort((a, b) -> Integer.compare(a.length(), b.length()) * -1);
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public static FactMappingValueOperator findOperator(String rawValue) {
        String value = rawValue.trim();
        for (FactMappingValueOperator factMappingValueOperator : FactMappingValueOperator.values()) {
            if (factMappingValueOperator.getSymbols().stream().anyMatch(value::startsWith)) {
                return factMappingValueOperator;
            }
        }

        // Equals is the default
        return FactMappingValueOperator.EQUALS;
    }
}