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

package org.kie.workbench.common.stunner.bpmn.backend.validation;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.drools.core.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.core.validation.impl.ProcessNodeValidationErrorImpl;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.validation.BPMNValidator;
import org.kie.workbench.common.stunner.bpmn.validation.BPMNViolation;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

@ApplicationScoped
public class BPMNValidatorImpl implements BPMNValidator {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNValidatorImpl.class);
    private final DiagramService diagramService;
    private SemanticModules modules;

    BPMNValidatorImpl() {
        this(null);
    }

    @Inject
    public BPMNValidatorImpl(final @Default DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    @PostConstruct
    protected void init() {
        modules = new SemanticModules();
        modules.addSemanticModule(new BPMNSemanticModule());
        modules.addSemanticModule(new BPMNDISemanticModule());
    }

    @Override
    public void validate(Diagram diagram, Consumer<Collection<DomainViolation>> resultConsumer) {
        String rawContent = diagramService.getRawContent(diagram);
        if (Objects.nonNull(rawContent)) {
            resultConsumer.accept(validate(rawContent, diagram.getMetadata().getTitle()).stream().collect(Collectors.toSet()));
            return;
        }

        resultConsumer.accept(Collections.emptyList());
    }

    protected Collection<BPMNViolation> validate(String serializedProcess, String processUUID) {
        try {
                List<Process> processes = parseProcess(serializedProcess);
            if (Objects.isNull(processes) || processes.size() == 0) {
                return Collections.emptyList();
            }

            return processes.stream()
                    .map(process -> RuleFlowProcessValidator.getInstance().validateProcess(process))
                    .flatMap(processValidationErrors -> Stream.of(processValidationErrors))
                    .filter(Objects::nonNull)
                    .map(error -> Optional.of(error)
                            .filter(ProcessNodeValidationErrorImpl.class::isInstance)
                            .map(ProcessNodeValidationErrorImpl.class::cast)
                            .map(ProcessNodeValidationErrorImpl::getNode)
                            .map(Node::getMetaData)
                            .map(m -> m.get("UniqueId"))
                            .map(String::valueOf)
                            .filter(StringUtils::nonEmpty)
                            .map(uuid -> new BPMNViolation(((ProcessNodeValidationErrorImpl) error).getRawMessage(),
                                                           Violation.Type.WARNING, uuid))
                            .orElseGet(() -> new BPMNViolation(error.getMessage(), Violation.Type.WARNING,
                                                               error.getProcess().getId())))
                    .collect(Collectors.toSet());
        } catch (SAXException | IOException e) {
            LOG.error("Error parsing process", e);
            return getBpmnViolationsFromException(() -> e.getMessage(), processUUID);
        } catch (Exception e) {
            LOG.error("Error validating process", e);
            return getBpmnViolationsFromException(() -> e.getMessage(), processUUID);
        }
    }

    private List<BPMNViolation> getBpmnViolationsFromException(Supplier<String> message, String uuid) {
        return Arrays.asList(new BPMNViolation(message.get(), Violation.Type.WARNING, uuid));
    }

    private List<Process> parseProcess(String serializedProcess) throws SAXException, IOException {
        return new XmlProcessReader(modules, getClass().getClassLoader()).read(new StringReader(serializedProcess));
    }

    @Override
    public String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);
    }
}
