/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;

import static org.kie.workbench.common.screens.datamodeller.backend.server.validation.PersistenceDescriptorValidationMessages.newErrorMessage;

/**
 * Class for validating if a <property, value> can be considered as valid in the context of the persistence descriptor
 * configuration.
 */
public class PropertyValidator {

    public PropertyValidator() {
    }

    /**
     * Validates if a <property, value> in the context of the persistence descriptor.
     * @param name the name of the property.
     * @param value the value for the property.
     * @return a list list of validation messages.
     */
    public List<ValidationMessage> validate( String name, String value ) {
        return validate( name, value, -1 );
    }

    /**
     * Validates if a <property, value> in the context of the persistence descriptor.
     * @param name the name of the property.
     * @param value the value for the property.
     * @param propertyIndex index for the property in the case the property comes from a list of properties.
     * @return a list list of validation messages.
     */

    public List<ValidationMessage> validate( String name, String value, int propertyIndex ) {
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        if ( name == null || name.trim().isEmpty() ) {
            //uncommon case
            if ( propertyIndex >= 0 ) {
                messages.add( newErrorMessage( PersistenceDescriptorValidationMessages.INDEXED_PROPERTY_NAME_EMPTY_ID,
                        MessageFormat.format( PersistenceDescriptorValidationMessages.INDEXED_PROPERTY_NAME_EMPTY, Integer.toString( propertyIndex ) ), Integer.toString( propertyIndex ) ) );
            } else {
                messages.add( newErrorMessage( PersistenceDescriptorValidationMessages.PROPERTY_NAME_EMPTY_ID,
                        PersistenceDescriptorValidationMessages.PROPERTY_NAME_EMPTY ) );
            }
        }
        if ( value == null || value.trim().isEmpty() ) {
            messages.add( PersistenceDescriptorValidationMessages.newWarningMessage( PersistenceDescriptorValidationMessages.PROPERTY_VALUE_EMPTY_ID,
                    MessageFormat.format( PersistenceDescriptorValidationMessages.PROPERTY_VALUE_EMPTY, name ), name ) );
        }
        return messages;
    }
}