/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.validation.model;

import javax.validation.ConstraintViolation;

import org.kie.workbench.common.stunner.core.validation.AbstractValidationViolation;

public final class ModelValidationViolationImpl
        extends AbstractValidationViolation<Object>
        implements ModelValidationViolation {

    private final ConstraintViolation<Object> violation;

    protected ModelValidationViolationImpl(final Object entity,
                                           final ConstraintViolation<Object> violation) {
        super(entity);
        this.violation = violation;
    }

    @Override
    public String getMessage() {
        return violation.getMessage();
    }

    @Override
    public ConstraintViolation<Object> getConstraintViolation() {
        return violation;
    }
}
