/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
public class CorrelationsValue {

    private List<Correlation> correlations;

    public CorrelationsValue() {
        this(new ArrayList<>());
    }

    public CorrelationsValue(@MapsTo("correlations") final List<Correlation> correlations) {
        this.correlations = correlations;
    }

    public List<Correlation> getCorrelations() {
        return correlations;
    }

    public void setCorrelations(final List<Correlation> correlations) {
        this.correlations = correlations;
    }

    public void addCorrelation(final Correlation correlation) {
        correlations.add(correlation);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CorrelationsValue) {
            CorrelationsValue other = (CorrelationsValue) o;
            return Objects.equals(correlations, other.correlations);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(correlations));
    }

    @Override
    public String toString() {
        final Collection<String> correlations = getCorrelations().stream()
                .map(Correlation::toString)
                .collect(Collectors.toList());

        final String serializedValue = Stream.of(correlations)
                .flatMap(Collection::stream)
                .collect(Collectors.joining(","));

        return serializedValue;
    }
}