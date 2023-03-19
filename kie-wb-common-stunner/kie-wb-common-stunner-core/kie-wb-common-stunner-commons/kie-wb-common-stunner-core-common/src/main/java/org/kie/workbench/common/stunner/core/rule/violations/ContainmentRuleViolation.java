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
public class ContainmentRuleViolation extends AbstractRuleViolation {

    private String parent;
    private String candidate;

    public ContainmentRuleViolation(final @MapsTo("parent") String parent,
                                    final @MapsTo("candidate") String candidate) {
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    public Optional<Object[]> getArguments() {
        return of(parent, candidate);
    }

    @Override
    public String getMessage() {
        return "The '" + parent + "' does not accepts containment for the candidate with labels ['" + candidate + "'].";
    }
}
