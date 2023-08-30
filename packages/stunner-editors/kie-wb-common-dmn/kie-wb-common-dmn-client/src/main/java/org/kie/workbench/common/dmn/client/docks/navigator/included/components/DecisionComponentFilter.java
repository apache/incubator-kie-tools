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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DecisionComponentFilter {

    private String drgElement;

    private String term;

    void setDrgElement(final String drgElement) {
        this.drgElement = drgElement;
    }

    void setTerm(final String term) {
        this.term = term;
    }

    Optional<String> getDrgElement() {
        return getOptionalString(drgElement);
    }

    Optional<String> getTerm() {
        return getOptionalString(term);
    }

    Stream<DecisionComponentsItem> query(final Stream<DecisionComponentsItem> stream) {
        return stream.filter(byDrgElement()).filter(byTerm());
    }

    private Optional<String> getOptionalString(final String value) {
        if (isEmpty(value)) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    private Predicate<DecisionComponentsItem> byTerm() {
        return item -> getTerm()
                .map(term -> containsIgnoringCase(item.getDecisionComponent().getName(), term))
                .orElse(true);
    }

    private Predicate<DecisionComponentsItem> byDrgElement() {
        return item -> getDrgElement()
                .map(filterDrgElement -> Objects.equals(getDrgElementClass(item), filterDrgElement))
                .orElse(true);
    }

    private String getDrgElementClass(final DecisionComponentsItem item) {
        return item.getDecisionComponent().getDrgElement().getClass().getSimpleName();
    }

    private boolean containsIgnoringCase(final String container,
                                         final String contained) {
        return container.toUpperCase().contains(contained.toUpperCase());
    }
}
