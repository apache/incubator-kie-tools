/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.project.client.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.api.validation.DMNDomainValidator;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.validation.ClientDiagramValidator;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.impl.ElementViolationImpl;

@DMNEditor
@ApplicationScoped
public class DMNClientDiagramValidator extends ClientDiagramValidator {

    private final Caller<DMNDomainValidator> dmnDomainValidator;

    private final DMNMarshallerService dmnMarshallerService;

    // CDI proxy.
    protected DMNClientDiagramValidator() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public DMNClientDiagramValidator(final DefinitionManager definitionManager,
                                     final RuleManager ruleManager,
                                     final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                     final ModelValidator modelValidator,
                                     final ManagedInstance<DomainValidator> validators,
                                     final Caller<DMNDomainValidator> dmnDomainValidator,
                                     final DMNMarshallerService dmnMarshallerService) {
        super(definitionManager,
              ruleManager,
              treeWalkTraverseProcessor,
              modelValidator,
              validators);
        this.dmnDomainValidator = dmnDomainValidator;
        this.dmnMarshallerService = dmnMarshallerService;
    }

    @Override
    public void validate(final Diagram diagram,
                         final Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer) {
        superValidate(diagram, getCollectionConsumer(diagram, resultConsumer));
    }

    Consumer<Collection<DiagramElementViolation<RuleViolation>>> getCollectionConsumer(final Diagram diagram,
                                                                                       final Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer) {
        return diagramElementViolations -> dmnMarshallerService.marshall(diagram, getContentServiceCallback(diagram, resultConsumer, diagramElementViolations));
    }

    ServiceCallback<String> getContentServiceCallback(final Diagram diagram,
                                                      final Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer,
                                                      final Collection<DiagramElementViolation<RuleViolation>> diagramElementViolations) {
        return new ServiceCallback<String>() {
            @Override
            public void onSuccess(final String xml) {
                dmnDomainValidator.call(onValidatorSuccess(diagramElementViolations, resultConsumer),
                                        onValidatorError()).validate(diagram, xml);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                logError("Marshaller service error");
            }
        };
    }

    RemoteCallback<Collection<DomainViolation>> onValidatorSuccess(final Collection<DiagramElementViolation<RuleViolation>> diagramElementViolations,
                                                                   final Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer) {
        return (Collection<DomainViolation> backendViolations) -> {
            final List<DiagramElementViolation<RuleViolation>> violations = getDiagramElementViolations(diagramElementViolations);
            violations.addAll(convert(backendViolations));
            resultConsumer.accept(violations);
        };
    }

    ErrorCallback<Object> onValidatorError() {
        return (msg, error) -> {
            logError("Validation service error");
            return false;
        };
    }

    void superValidate(final Diagram diagram,
                       final Consumer<Collection<DiagramElementViolation<RuleViolation>>> resultConsumer) {
        super.validate(diagram, resultConsumer);
    }

    private List<ElementViolationImpl> convert(final Collection<DomainViolation> backendViolations) {
        return backendViolations
                .stream()
                .filter(v -> Objects.nonNull(v.getUUID()))
                .filter(v -> !"null".equals(v.getUUID()))
                .map(v -> new ElementViolationImpl.Builder().setUuid(v.getUUID()).setDomainViolations(Collections.singletonList(v)).build())
                .collect(Collectors.toList());
    }

    private LinkedList<DiagramElementViolation<RuleViolation>> getDiagramElementViolations(final Collection<DiagramElementViolation<RuleViolation>> diagramElementViolations) {
        return Optional
                .ofNullable(diagramElementViolations)
                .map(LinkedList::new)
                .orElse(new LinkedList<>());
    }

    void logError(final String errorMessage) {
        DomGlobal.console.error("[DMNClientDiagramValidator] Error during validation: " + errorMessage);
    }
}
