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
import java.util.Collections;
import java.util.LinkedHashSet;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;

@Portable
public final class ElementViolationImpl
        implements DiagramElementViolation<RuleViolation> {

    private final String uuid;
    private final Collection<RuleViolation> graphViolations;
    private final Collection<ModelBeanViolation> modelViolations;
    private final Type type;

    private ElementViolationImpl(final @MapsTo("uuid") String uuid,
                                 final @MapsTo("graphViolations") Collection<RuleViolation> graphViolations,
                                 final @MapsTo("modelViolations") Collection<ModelBeanViolation> modelViolations,
                                 final @MapsTo("type") Type type) {
        this.uuid = uuid;
        this.graphViolations = graphViolations;
        this.modelViolations = modelViolations;
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
    public Type getViolationType() {
        return type;
    }

    @NonPortable
    public static class Builder {

        public static ElementViolationImpl build(final String uuid,
                                                 final Collection<RuleViolation> graphViolations) {
            return build(uuid,
                         graphViolations,
                         Collections.emptyList());
        }

        public static ElementViolationImpl build(final String uuid,
                                                 final Collection<RuleViolation> graphViolations,
                                                 final Collection<ModelBeanViolation> modelViolations) {
            return new ElementViolationImpl(uuid,
                                            graphViolations,
                                            modelViolations,
                                            ValidationUtils.getMaxSeverity(new LinkedHashSet<org.kie.workbench.common.stunner.core.validation.Violation>() {{
                                                addAll(graphViolations);
                                                addAll(modelViolations);
                                            }}));
        }
    }
}
