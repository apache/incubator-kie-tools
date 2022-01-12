/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.context;

import java.util.Collection;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * This rule evaluation context provides the runtime information
 * that allows the evaluation for cardinality operations in
 * a graph structure.
 */
public interface ElementCardinalityContext extends GraphEvaluationContext {

    /**
     * The candidate element to add or remove from the graph.
     * If not candidate present, it checks the cardinality
     * for the whole graph elements.
     */
    Collection<Element<? extends View<?>>> getCandidates();

    /**
     * The operation to be performed on the candidate.
     * If not candidate present, the operation value is
     * discarded although being present.
     */
    Optional<CardinalityContext.Operation> getOperation();
}
