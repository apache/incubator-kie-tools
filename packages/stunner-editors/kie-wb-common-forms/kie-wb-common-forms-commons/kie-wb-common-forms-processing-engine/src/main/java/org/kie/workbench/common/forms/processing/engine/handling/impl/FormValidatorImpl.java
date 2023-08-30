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

import javax.inject.Inject;

import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.Form;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.ModelValidator;

public class FormValidatorImpl implements FormValidator {

    private ModelValidator modelValidator;

    private FieldStateValidator fieldStateValidator;

    @Inject
    public FormValidatorImpl(ModelValidator modelValidator,
                             FieldStateValidator fieldStateValidator) {
        this.modelValidator = modelValidator;
        this.fieldStateValidator = fieldStateValidator;
    }

    @Override
    public boolean validate(Form form, Object model) {

        form.getFields().forEach(FormField::clearError);

        boolean isModelValid = modelValidator.validate(form.getFields(),
                                                       model);
        boolean isFieldStateValid = fieldStateValidator.validate(form.getFields());

        return isFieldStateValid && isModelValid;
    }

    @Override
    public boolean validate(FormField formField,
                            Object model) {

        if(formField == null) {
            throw new IllegalArgumentException("FormField cannot be null");
        }
        formField.clearError();

        if (!fieldStateValidator.validate(formField)) {
            return false;
        }

        return modelValidator.validate(formField, model);
    }

    public ModelValidator getModelValidator() {
        return this.modelValidator;
    }

    public void setModelValidator(ModelValidator modelValidator) {
        this.modelValidator = modelValidator;
    }
}
