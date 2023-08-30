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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.processing.engine.handling.CustomFieldValidator;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.ValidationResult;
import org.kie.workbench.common.forms.processing.engine.handling.resources.i18n.ProcessingEngineConstants;

@Dependent
public class FieldStateValidatorImpl implements FieldStateValidator {

    protected TranslationService translationService;

    protected Map<Class<?>, Predicate<?>> validators = new HashMap<>();

    @Inject
    public FieldStateValidatorImpl(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    public void initialize() {
        validators.put(String.class,
                       (String string) -> string != null && !string.isEmpty());
        validators.put(ArrayList.class,
                       (ArrayList list) -> list != null && !list.isEmpty());
    }

    @Override
    public boolean validate(Collection<FormField> fields) {
        boolean isValid = true;
        for (FormField field : fields) {
            isValid = validate(field) && isValid;
        }
        return isValid;
    }

    @Override
    public boolean validate(FormField field) {

        Object value = getFieldValue(field);
        return validateFieldValue(field, value) && field.isContentValid();
    }

    protected Object getFieldValue(FormField field) {
        if (field.getWidget() instanceof HasValue) {
            return ((HasValue) field.getWidget()).getValue();
        } else if (field.getWidget() instanceof TakesValue) {
            return ((TakesValue) field.getWidget()).getValue();
        } else if (field.getWidget() instanceof HasText) {
            return ((HasText) field.getWidget()).getText();
        }
        throw new IllegalStateException("Unexpected widget type: impossible to read the value");
    }

    protected boolean validateFieldValue(FormField field,
                                         Object value) {
        if (field.isRequired()) {
            if (!validateFieldValueRequired(field, value)) {
                return false;
            }
        }

        for (CustomFieldValidator validator : field.getCustomValidators()) {
            ValidationResult result = validator.validate(value);
            if (result.getStatus().hasMessage()) {
                if (!result.getStatus().isValid()) {
                    field.showError(result.getMessage());
                    return false;
                } else {
                    field.showWarning(result.getMessage());
                }
            }
        }

        return true;
    }

    protected boolean validateFieldValueRequired(FormField field,
                                                 Object value) {
        String message = translationService.getTranslation(ProcessingEngineConstants.FieldStateValidatorImplFieldIsRequired);
        if (value == null) {
            field.showError(message);
            return false;
        }
        if (validators.containsKey(value.getClass())) {
            Predicate predicate = validators.get(value.getClass());
            if (!predicate.test(value)) {
                field.showError(message);
                return false;
            }
        }
        return true;
    }
}
