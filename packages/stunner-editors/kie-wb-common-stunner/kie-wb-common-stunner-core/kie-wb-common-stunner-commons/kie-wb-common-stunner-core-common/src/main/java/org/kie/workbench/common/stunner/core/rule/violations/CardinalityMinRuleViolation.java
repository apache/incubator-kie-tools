/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.violations;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CardinalityMinRuleViolation extends AbstractRuleViolation {

    private final String candidate;
    private final Integer restrictedOccurrences;
    private final Integer currentOccurrences;

    public CardinalityMinRuleViolation(final @MapsTo("candidate") String candidate,
                                       final @MapsTo("restrictedOccurrences") Integer restrictedOccurrences,
                                       final @MapsTo("currentOccurrences") Integer currentOccurrences,
                                       final @MapsTo("type") Type type) {
        super(type);
        this.candidate = candidate;
        this.restrictedOccurrences = restrictedOccurrences;
        this.currentOccurrences = currentOccurrences;
    }

    @Override
    public Optional<Object[]> getArguments() {
        return of(candidate, restrictedOccurrences, currentOccurrences);
    }

    @Override
    public String getMessage() {
        return "The diagram requires a minimum of '" + restrictedOccurrences + "' occurrences for the candidate with roles '" + candidate + "', but only found '" + currentOccurrences + "' occurrences.";
    }
}
