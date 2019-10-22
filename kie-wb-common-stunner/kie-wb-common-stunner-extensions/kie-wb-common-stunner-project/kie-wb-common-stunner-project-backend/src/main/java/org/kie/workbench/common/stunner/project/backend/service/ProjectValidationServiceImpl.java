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

package org.kie.workbench.common.stunner.project.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.impl.ElementViolationImpl;
import org.kie.workbench.common.stunner.project.service.ProjectValidationService;

@ApplicationScoped
@Service
public class ProjectValidationServiceImpl implements ProjectValidationService {

    private final Instance<DomainValidator> validators;

    protected ProjectValidationServiceImpl() {
        this(null);
    }

    @Inject
    public ProjectValidationServiceImpl(Instance<DomainValidator> validators) {
        this.validators = validators;
    }

    @Override
    public Collection<DiagramElementViolation<RuleViolation>> validate(Diagram diagram) {
        return domainViolations(diagram).stream()
                .filter(v -> Objects.nonNull(v.getUUID()))
                .filter(v -> !"null".equals(v.getUUID()))
                .collect(Collectors.groupingBy(DomainViolation::getUUID))
                .entrySet()
                .stream()
                .map(e -> new ElementViolationImpl.Builder().setUuid(e.getKey()).setDomainViolations(e.getValue()).build())
                .collect(Collectors.toList());
    }

    private Collection<DomainViolation> domainViolations(Diagram diagram) {
        return StreamSupport.stream(validators.spliterator(), false)
                .filter(validator -> Objects.equals(validator.getDefinitionSetId(), diagram.getMetadata().getDefinitionSetId()))
                .findFirst()
                .map(validator -> {
                    final List<DomainViolation> domainViolations = new ArrayList<>();
                    validator.validate(diagram, domainViolations::addAll);
                    return domainViolations;
                }).orElseGet(Collections::emptyList);
    }
}
