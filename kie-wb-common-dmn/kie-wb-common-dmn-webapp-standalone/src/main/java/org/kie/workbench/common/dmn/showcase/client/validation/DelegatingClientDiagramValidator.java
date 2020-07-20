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
package org.kie.workbench.common.dmn.showcase.client.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.showcase.api.ValidationService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.validation.ClientDiagramValidator;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;

@Specializes
@ApplicationScoped
public class DelegatingClientDiagramValidator extends ClientDiagramValidator {

    private final Caller<ValidationService> validationService;

    // CDI proxy.
    protected DelegatingClientDiagramValidator() {
        this(null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DelegatingClientDiagramValidator(final DefinitionManager definitionManager,
                                            final RuleManager ruleManager,
                                            final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                            final ModelValidator modelValidator,
                                            final Caller<ValidationService> validationService,
                                            final ManagedInstance<DomainValidator> validators) {
        super(definitionManager,
              ruleManager,
              treeWalkTraverseProcessor,
              modelValidator,
              validators);
        this.validationService = validationService;
    }

    @Override
    public void validate(Diagram diagram, Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer) {
        super.validate(diagram, diagramElementViolations -> {
            final List<DiagramElementViolation<RuleViolation>> violations =
                    (Objects.nonNull(diagramElementViolations) ? new LinkedList<>(diagramElementViolations) : new LinkedList<>());
            backendValidation(diagram, backendViolations -> {
                violations.addAll(backendViolations);
                resultConsumer.accept(violations);
            });
        });
    }

    @SuppressWarnings("unchecked")
    private void backendValidation(Diagram diagram, final Consumer<Collection<DiagramElementViolation<RuleViolation>>> callback) {
        validationService.call(result -> callback.accept((Collection<DiagramElementViolation<RuleViolation>>) result),
                               (msg, error) -> {
                                   callback.accept(Collections.emptyList());
                                   return false;
                               }).validate(diagram);
    }
}
