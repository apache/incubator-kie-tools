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

package org.kie.workbench.common.stunner.core.validation.impl;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

public class ElementViolationImplBuilder {

    private String uuid;
    private Collection<RuleViolation> graphViolations;
    private Collection<ModelBeanViolation> modelViolations;
    private Collection<DomainViolation> domainViolations;
    private Violation.Type type;

    public ElementViolationImplBuilder setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public ElementViolationImplBuilder setGraphViolations(Collection<RuleViolation> graphViolations) {
        this.graphViolations = graphViolations;
        return this;
    }

    public ElementViolationImplBuilder setModelViolations(Collection<ModelBeanViolation> modelViolations) {
        this.modelViolations = modelViolations;
        return this;
    }

    public ElementViolationImplBuilder setDomainViolations(Collection<DomainViolation> domainViolations) {
        this.domainViolations = domainViolations;
        return this;
    }

    public ElementViolationImplBuilder setType(Violation.Type type) {
        this.type = type;
        return this;
    }

    public ElementViolationImpl createElementViolationImpl() {
        return new ElementViolationImpl(uuid, graphViolations, modelViolations, domainViolations, type);
    }
}