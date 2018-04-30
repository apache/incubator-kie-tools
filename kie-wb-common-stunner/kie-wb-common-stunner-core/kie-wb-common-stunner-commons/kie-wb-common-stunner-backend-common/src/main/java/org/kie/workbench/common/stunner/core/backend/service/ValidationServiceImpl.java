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

package org.kie.workbench.common.stunner.core.backend.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.ValidationService;
import org.kie.workbench.common.stunner.core.validation.impl.ElementViolationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Service
public class ValidationServiceImpl implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class.getName());

    private final Instance<DomainValidator> validators;

    protected ValidationServiceImpl() {
        this(null);
    }

    @Inject
    public ValidationServiceImpl(Instance<DomainValidator> validators) {
        this.validators = validators;
    }

    @Override
    public Collection<DiagramElementViolation<RuleViolation>> validate(Diagram diagram) {
        final Collection<DiagramElementViolation<RuleViolation>> violations = new HashSet<>();
        //handle domain violations (BPMN, DMN, CM...)
        domainValidation(diagram, v -> violations.add(v));
        return violations;
    }

    private void domainValidation(Diagram diagram, Consumer<DiagramElementViolation<RuleViolation>> callback) {
        StreamSupport.stream(validators.spliterator(), false)
                .filter(validator -> Objects.equals(validator.getDefinitionSetId(), diagram.getMetadata().getDefinitionSetId()))
                .findFirst()
                .ifPresent(validator -> validator.validate(diagram, domainViolations ->
                        callback.accept(new ElementViolationImpl.Builder()
                                                .setUuid(diagram.getGraph().getUUID())
                                                .setDomainViolations(domainViolations)
                                                .build())));
    }
}
