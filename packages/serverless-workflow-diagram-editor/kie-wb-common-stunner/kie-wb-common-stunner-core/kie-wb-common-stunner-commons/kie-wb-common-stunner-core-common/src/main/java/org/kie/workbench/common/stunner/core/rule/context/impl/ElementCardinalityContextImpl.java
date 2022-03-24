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

package org.kie.workbench.common.stunner.core.rule.context.impl;

import java.util.Collection;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;

class ElementCardinalityContextImpl
        extends AbstractGraphEvaluationContext<ElementCardinalityContextImpl>
        implements ElementCardinalityContext {

    private final Collection<Element<? extends View<?>>> candidates;
    private final Optional<CardinalityContext.Operation> operation;

    ElementCardinalityContextImpl(final Collection<Element<? extends View<?>>> candidates,
                                  final Optional<CardinalityContext.Operation> operation) {
        this.candidates = candidates;
        this.operation = operation;
    }

    @Override
    public String getName() {
        return "Cardinality";
    }

    @Override
    public Collection<Element<? extends View<?>>> getCandidates() {
        return candidates;
    }

    @Override
    public Optional<CardinalityContext.Operation> getOperation() {
        return operation;
    }

    @Override
    public boolean isDefaultDeny() {
        return false;
    }

    @Override
    public Class<? extends RuleEvaluationContext> getType() {
        return ElementCardinalityContext.class;
    }
}
