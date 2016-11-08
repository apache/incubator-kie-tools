/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormFieldProvider;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;

@Dependent
public class FormValidatorImpl implements FormValidator {

    public static final String NESTED_PROPERTY_SEPARATOR = "_";

    private Validator validator;

    private FormFieldProvider formFieldProvider;

    @Inject
    public FormValidatorImpl( Validator validator ) {
        this.validator = validator;
    }

    @Override
    public boolean validate( Object model ) {
        boolean isValid = true;

        clearAllFieldErrors();

        try {
            Set<ConstraintViolation<Object>> result = validator.validate( model );

            for ( ConstraintViolation<Object> constraintViolation : result ) {
                FormField formField = findFormField( constraintViolation );
                if ( formField == null ) {
                    continue;
                }
                isValid = false;
                formField.setError( constraintViolation.getMessage() );
            }
        } catch ( IllegalArgumentException ex ) {
            GWT.log( "Error trying to validate model: model does not any validation constraint. " );
        }

        return isValid;
    }

    @Override
    public boolean validate( String propertyName, Object model ) {

        clearFieldError( propertyName );

        try {
            Set<ConstraintViolation<Object>> result = validator.validate( model );

            for ( ConstraintViolation<Object> constraintViolation : result ) {
                if ( propertyName.equals( getFieldNameFromConstraint( constraintViolation,
                        propertyName.contains( NESTED_PROPERTY_SEPARATOR ) ) ) ) {
                    FormField formField = formFieldProvider.findFormField( propertyName );
                    if ( formField == null ) {
                        continue;
                    }
                    formField.setError( constraintViolation.getMessage() );
                    return false;
                }
            }
        } catch ( IllegalArgumentException ex ) {
            GWT.log( "Error trying to validate model: model does not any validation constraint. " );
        }

        return true;
    }

    public void setFormFieldProvider( FormFieldProvider formFieldProvider ) {
        this.formFieldProvider = formFieldProvider;
    }

    protected void clearAllFieldErrors() {
        for ( FormField formField : formFieldProvider.getAll() ) {
            formField.clearError();
        }
    }

    protected void clearFieldError( String fieldName ) {
        FormField field = formFieldProvider.findFormField( fieldName );
        if ( field != null ) {
            field.clearError();
        }
    }


    private FormField findFormField( ConstraintViolation constraintViolation ) {
        assert constraintViolation != null;

        Path propertyPath = constraintViolation.getPropertyPath();

        FormField field = formFieldProvider.findFormField( propertyPath.iterator().next().getName() );

        if ( field == null ) {
            String fieldName = propertyPath.toString().replace( ".", NESTED_PROPERTY_SEPARATOR );

            field = formFieldProvider.findFormField( fieldName );
        }
        return field;
    }

    private String getFieldNameFromConstraint( ConstraintViolation<Object> constraintViolation, boolean includeNested ) {
        if ( includeNested ) {
            return constraintViolation.getPropertyPath().toString().replace( ".", "_" );
        }
        return constraintViolation.getPropertyPath().iterator().next().getName();
    }
}
