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

package org.kie.workbench.common.stunner.core.validation.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramValidator;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;

/**
 * An abstraction of the diagram validator, due to the ModelValidator (based on jsr303)
 * is not available on server side yet.
 */
public abstract class AbstractDiagramValidator
        implements DiagramValidator<Diagram, RuleViolation> {

    private final GraphValidatorImpl graphValidator;
    private final ModelValidator modelValidator;

    protected AbstractDiagramValidator(final DefinitionManager definitionManager,
                                       final RuleManager ruleManager,
                                       final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                       final ModelValidator modelValidator) {
        this.graphValidator = new GraphValidatorImpl(definitionManager,
                                                     ruleManager,
                                                     treeWalkTraverseProcessor);
        this.modelValidator = modelValidator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validate(final Diagram diagram,
                         final Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer) {
        final Graph graph = diagram.getGraph();
        final List<DiagramElementViolation<RuleViolation>> violations = new LinkedList<>();
        graphValidator.validate(graph,
                                Optional.empty(),
                                Optional.of((g, v) -> consumeBeanAndViolations(() -> violations).accept(g, v)),
                                Optional.of((n, v) -> consumeBeanAndViolations(() -> violations).accept(n, v)),
                                Optional.of((e, v) -> consumeBeanAndViolations(() -> violations).accept(e, v)),
                                // At this point all violations have been already consumed, so no need
                                // to use the resulting ones here.
                                vs -> resultConsumer.accept(violations)
        );
    }

    private BiConsumer<Element, Collection<RuleViolation>> consumeBeanAndViolations(final Supplier<List<DiagramElementViolation<RuleViolation>>> violations) {
        return (element, ruleViolations) -> {
            if (Optional.ofNullable(element.getContent()).isPresent()) {
                // If the underlying bean is a Definition, it accomplishes JSR303 validations.
                modelValidator.validate(element,
                                        modelViolations -> {
                                            violations.get().add(new ElementViolationImpl.Builder()
                                                                         .setUuid(element.getUUID())
                                                                         .setGraphViolations(ruleViolations)
                                                                         .setModelViolations(modelViolations)
                                                                         .build());
                                        });
            } else {
                // Otherwise, no need not perform bean validation.
                violations.get().add(new ElementViolationImpl.Builder()
                                             .setUuid(element.getUUID())
                                             .setGraphViolations(ruleViolations)
                                             .build());
            }
        };
    }
}