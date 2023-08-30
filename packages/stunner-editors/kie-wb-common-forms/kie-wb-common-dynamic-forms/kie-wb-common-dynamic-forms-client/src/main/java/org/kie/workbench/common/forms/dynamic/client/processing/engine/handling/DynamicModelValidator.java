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


package org.kie.workbench.common.forms.dynamic.client.processing.engine.handling;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.validation.DynamicModelConstraints;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.validation.FieldConstraint;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.ModelValidator;

@Dependent
@Alternative
public class DynamicModelValidator implements ModelValidator<Map<String, Object>> {

    protected DynamicValidator validator;

    protected DynamicModelConstraints modelConstraints;

    @Inject
    public DynamicModelValidator(DynamicValidator validator) {
        this.validator = validator;
    }

    @Override
    public boolean validate(Collection<FormField> fields,
                            Map<String, Object> model) {

        if (validator == null) {
            return true;
        }

        boolean isValid = true;

        for (FormField formField : fields) {
            boolean validField = validate(formField,
                                          model);
            if (!validField) {
                isValid = false;
            }
        }

        return isValid;
    }

    @Override
    public boolean validate(FormField formField,
                            Map<String, Object> model) {

        if (validator == null) {
            return true;
        }

        if (modelConstraints != null) {
            List<FieldConstraint> fieldConstraints = modelConstraints.getFieldConstraints().get(formField.getFieldBinding());

            if (fieldConstraints != null) {

                for (FieldConstraint constraint : fieldConstraints) {
                    try {
                        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(constraint.getAnnotationType(),
                                                                                                   constraint.getParams(),
                                                                                                   model.get(formField.getFieldBinding()));
                        if (!constraintViolations.isEmpty()) {
                            formField.showError(constraintViolations.iterator().next().getMessage());
                            return false;
                        }
                    } catch (IllegalArgumentException ex) {
                        // Maybe trying to validate an Annotation which is not a a Validation
                    }
                }
            }
        }
        return true;
    }

    public DynamicModelConstraints getModelConstraints() {
        return modelConstraints;
    }

    public void setModelConstraints(DynamicModelConstraints modelConstraints) {
        this.modelConstraints = modelConstraints;
    }
}
