/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.backend.server.validation;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;

import static org.kie.workbench.common.screens.datamodeller.backend.server.validation.PersistenceDescriptorValidationMessages.newErrorMessage;

/**
 * Class for validating if a class has the minimal configuration to be considered as persistable.
 */
public class PersistableClassValidator {

    public PersistableClassValidator( ) {
    }

    /**
     * Validates if a class can be considered persistable.
     * @param className a class name to validate.
     * @param classLoader a classloader from where the class className and the referenced types by the className can be loaded.
     * @return a list of validation messages.
     */
    public List<ValidationMessage> validate( String className, ClassLoader classLoader ) {
        List<ValidationMessage> result = new ArrayList<>( );
        Class< ? > clazz;
        try {
            if ( className == null || className.trim( ).isEmpty( ) ) {
                result.add( newErrorMessage( PersistenceDescriptorValidationMessages.PERSISTABLE_CLASS_NAME_EMPTY_ID,
                        PersistenceDescriptorValidationMessages.PERSISTABLE_CLASS_NAME_EMPTY ) );
                return result;
            }
            clazz = classLoader.loadClass( className );
            Annotation[] annotations = clazz.getAnnotations( );
            Optional< Annotation > persistable = Arrays.stream( annotations )
                    .filter( annotation ->
                            Entity.class.equals( annotation.annotationType( ) ) ||
                                    Embeddable.class.equals( annotation.annotationType( ) ) ||
                                    MappedSuperclass.class.equals( annotation.annotationType( ) ) )
                    .findFirst( );
            if ( !persistable.isPresent( ) ) {
                result.add( newErrorMessage( PersistenceDescriptorValidationMessages.CLASS_NOT_PERSISTABLE_ID,
                        MessageFormat.format( PersistenceDescriptorValidationMessages.CLASS_NOT_PERSISTABLE, className ), className ) );
            }
        } catch ( ClassNotFoundException e ) {
            result.add( newErrorMessage( PersistenceDescriptorValidationMessages.CLASS_NOT_FOUND_ID,
                    MessageFormat.format( PersistenceDescriptorValidationMessages.CLASS_NOT_FOUND, className ), className ) );
        }
        return result;
    }
}