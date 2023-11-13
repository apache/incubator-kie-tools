/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.validation.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

public final class ElementViolationImpl
        implements DiagramElementViolation<RuleViolation> {

    private final String uuid;
    private final Collection<RuleViolation> graphViolations;
    private final Collection<ModelBeanViolation> modelViolations;
    private final Collection<DomainViolation> domainViolations;
    private final Type type;

    ElementViolationImpl(final String uuid,
                         final Collection<RuleViolation> graphViolations,
                         final Collection<ModelBeanViolation> modelViolations,
                         final Collection<DomainViolation> domainViolations,
                         final Type type) {
        this.uuid = uuid;
        this.graphViolations = graphViolations;
        this.modelViolations = modelViolations;
        this.domainViolations = domainViolations;
        this.type = type;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public Collection<RuleViolation> getGraphViolations() {
        return graphViolations;
    }

    @Override
    public Collection<ModelBeanViolation> getModelViolations() {
        return modelViolations;
    }

    @Override
    public Collection<DomainViolation> getDomainViolations() {
        return domainViolations;
    }

    @Override
    public String getMessage() {
        throw new IllegalStateException("The message should be handled by the caller");
    }

    @Override
    public Type getViolationType() {
        return type;
    }

    public static class Builder {

        private String uuid;
        private Collection<RuleViolation> graphViolations = Collections.emptyList();
        private Collection<ModelBeanViolation> modelViolations = Collections.emptyList();
        private Collection<DomainViolation> domainViolations = Collections.emptyList();
        private Violation.Type type;

        public Builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder setGraphViolations(Collection<RuleViolation> graphViolations) {
            this.graphViolations = graphViolations;
            return this;
        }

        public Builder setModelViolations(Collection<ModelBeanViolation> modelViolations) {
            this.modelViolations = modelViolations;
            return this;
        }

        public Builder setDomainViolations(Collection<DomainViolation> domainViolations) {
            this.domainViolations = domainViolations;
            return this;
        }

        public Builder setType(Violation.Type type) {
            this.type = type;
            return this;
        }

        public ElementViolationImpl build() {
            if (Objects.isNull(type)) {
                setType(ValidationUtils.getMaxSeverity(new LinkedHashSet<org.kie.workbench.common.stunner.core.validation.Violation>() {{
                    Optional.ofNullable(graphViolations).ifPresent(v -> addAll(v));
                    Optional.ofNullable(modelViolations).ifPresent(v -> addAll(v));
                    Optional.ofNullable(domainViolations).ifPresent(v -> addAll(v));
                }}));
            }
            return new ElementViolationImpl(uuid, graphViolations, modelViolations, domainViolations, type);
        }
    }
}
