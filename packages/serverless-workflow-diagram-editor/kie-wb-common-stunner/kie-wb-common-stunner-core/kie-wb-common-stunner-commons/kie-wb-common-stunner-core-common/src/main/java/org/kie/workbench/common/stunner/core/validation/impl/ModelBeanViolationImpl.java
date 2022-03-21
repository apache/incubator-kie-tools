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

import java.util.Optional;

import javax.validation.ConstraintViolation;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.rule.violations.AbstractRuleViolation;
import org.kie.workbench.common.stunner.core.validation.ModelBeanViolation;

@Portable
public final class ModelBeanViolationImpl extends AbstractRuleViolation implements ModelBeanViolation {

    private final String path;

    private ModelBeanViolationImpl(final @MapsTo("path") String path,
                                   final @MapsTo("message") String message,
                                   final @MapsTo("type") Type type,
                                   final @MapsTo("uuid") String uuid) {
        super(type, message);
        setUUID(uuid);
        this.path = path;
    }

    public String getPropertyPath() {
        return path;
    }

    @Override
    public Optional<Object[]> getArguments() {
        return of(getUUID());
    }

    @NonPortable
    public static class Builder {

        public static ModelBeanViolationImpl build(final ConstraintViolation<?> root, String uuid) {
            return new ModelBeanViolationImpl(root.getPropertyPath().toString(),
                                              root.getMessage(),
                                              Type.WARNING,
                                              uuid);
        }
    }
}
