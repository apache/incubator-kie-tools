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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * SimulationDescriptor describes a template of a simulation
 */
@Portable
public class SimulationDescriptor {

    private final List<FactMapping> factMappings = new ArrayList<>();

    public List<FactMapping> getFactMappings() {
        return Collections.unmodifiableList(factMappings);
    }

    /**
     * Sort elements based on logicalPosition value
     */
    public void sortByLogicalPosition() {
        factMappings.sort(Comparator.comparing(FactMapping::getLogicalPosition));
    }

    public Set<FactIdentifier> getFactIdentifiers() {
        return factMappings.stream().map(FactMapping::getFactIdentifier).collect(Collectors.toSet());
    }

    public FactMapping getFactMappingByIndex(int index) {
        return factMappings.get(index);
    }

    public int getIndexByIdentifier(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return IntStream.range(0, factMappings.size()).filter(index -> {
            FactMapping factMapping = factMappings.get(index);
            return factMapping.getExpressionIdentifier().equals(expressionIdentifier) &&
                    factMapping.getExpressionIdentifier().equals(expressionIdentifier);
        }).findFirst().orElseThrow(() -> new IllegalArgumentException(
                new StringBuilder().append("Impossible to find a FactMapping with factIdentifier '").append(factIdentifier.getName())
                        .append("' and expressionIdentifier '").append(expressionIdentifier.getName()).append("'").toString()));
    }

    public List<FactMapping> getFactMappingsByFactName(String factName) {
        return internalFilter(e -> e.getFactIdentifier().getName().equalsIgnoreCase(factName));
    }

    public Optional<FactMapping> getFactMapping(FactIdentifier factIdentifier, ExpressionIdentifier ei) {
        List<FactMapping> factMappings = internalFilter(e -> e.getExpressionIdentifier().equals(ei) &&
                e.getFactIdentifier().equals(factIdentifier));
        return factMappings.stream().findFirst();
    }

    private List<FactMapping> internalFilter(Predicate<FactMapping> predicate) {
        return factMappings.stream().filter(predicate).collect(Collectors.toList());
    }

    public FactMapping addFactMapping(FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return addFactMapping(factMappings.size(), factIdentifier, expressionIdentifier);
    }

    public FactMapping addFactMapping(int index, FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        return addFactMapping(index, expressionIdentifier.getName(), factIdentifier, expressionIdentifier);
    }

    public FactMapping addFactMapping(int index, String expressionAlias, FactIdentifier factIdentifier, ExpressionIdentifier expressionIdentifier) {
        if (getFactMapping(factIdentifier, expressionIdentifier).isPresent()) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("An expression with name '").append(expressionIdentifier.getName())
                            .append("' already exists for the fact '").append(factIdentifier.getName()).append("'").toString());
        }
        if (index > factMappings.size()) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("Impossible to add an element at position ").append(index)
                            .append(" because there are only ").append(factMappings.size()).append(" elements").toString());
        }
        FactMapping factMapping = new FactMapping(expressionAlias, factIdentifier, expressionIdentifier, index);
        factMappings.add(index, factMapping);
        return factMapping;
    }

    public void clear() {
        factMappings.clear();
    }
}
