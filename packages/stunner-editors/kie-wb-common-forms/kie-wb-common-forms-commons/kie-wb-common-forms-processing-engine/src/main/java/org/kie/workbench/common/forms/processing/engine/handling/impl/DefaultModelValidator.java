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


package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.databinding.client.BindableProxy;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.ModelValidator;

import static org.kie.workbench.common.forms.processing.engine.handling.FormValidator.NESTED_PROPERTY_SEPARATOR;

@Dependent
@Default
public class DefaultModelValidator<MODEL> implements ModelValidator<MODEL> {

    private Validator validator;

    @Inject
    public DefaultModelValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean validate(Collection<FormField> fields,
                            MODEL model) {
        boolean isValid = true;

        if (model instanceof BindableProxy) {
            model = ((BindableProxy<MODEL>)model).deepUnwrap();
        }

        try {
            Set<ConstraintViolation<Object>> result = validator.validate(model);

            for (ConstraintViolation<Object> constraintViolation : result) {

                Optional<FormField> found = fields.stream().filter(formField -> {
                    Path propertyPath = constraintViolation.getPropertyPath();

                    String propertyName = propertyPath.iterator().next().getName();

                    if (checkBinding(formField,
                                     propertyName)) {
                        return true;
                    }

                    propertyName = propertyPath.toString().replace(".",
                                                                   NESTED_PROPERTY_SEPARATOR);

                    if (checkBinding(formField,
                                     propertyName)) {
                        return true;
                    }

                    return false;
                }).findFirst();

                if (!found.isPresent()) {
                    continue;
                }

                FormField formField = found.get();
                if (formField == null) {
                    continue;
                }
                isValid = false;
                formField.showError(constraintViolation.getMessage());
            }
        } catch (IllegalArgumentException ex) {
            GWT.log("Error trying to validate model: model does not any validation constraint. ");
        }

        return isValid;
    }

    @Override
    public boolean validate(FormField formField,
                            MODEL model) {
        boolean isValid = true;

        if (model instanceof BindableProxy) {
            model = ((BindableProxy<MODEL>)model).deepUnwrap();
        }

        try {
            Set<ConstraintViolation<Object>> result = validator.validate(model);

            for (ConstraintViolation<Object> constraintViolation : result) {

                String propertyName = getFieldNameFromConstraint(constraintViolation,
                                                                 formField.getFieldName().contains(
                                                                         NESTED_PROPERTY_SEPARATOR));

                if (checkBinding(formField,
                                 propertyName)) {
                    formField.showError(constraintViolation.getMessage());
                    return false;
                }
            }
        } catch (IllegalArgumentException ex) {
            GWT.log("Error trying to validate model: model does not any validation constraint. ");
        }

        return isValid;
    }

    protected boolean checkBinding(FormField formField,
                                   String fieldName) {
        if (formField.getFieldName().equals(fieldName) || formField.getFieldBinding().equals(fieldName)) {
            return true;
        }
        return false;
    }

    private String getFieldNameFromConstraint(ConstraintViolation<Object> constraintViolation,
                                              boolean includeNested) {
        if (includeNested) {
            return constraintViolation.getPropertyPath().toString().replace(".",
                                                                            "_");
        }
        return constraintViolation.getPropertyPath().iterator().next().getName();
    }
}
