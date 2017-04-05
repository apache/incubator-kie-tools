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

package org.kie.workbench.common.screens.datamodeller.client.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datamodeller.validation.PersistenceDescriptorValidationMessage;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationMessageTranslator;

/**
 * This class manages the translation of the PersistenceDescriptor related validations messages returned by
 * the PersistenceDescriptorValidatorService into client side validation messages properly internationalized.
 */
@ApplicationScoped
public class PersistenceDescriptorValidationMessageTranslator
        implements ValidationMessageTranslator {

    public static final String PREFIX = "persistence_descriptor_validation_";

    private TranslationService translationService;

    @Inject
    public PersistenceDescriptorValidationMessageTranslator( TranslationService translationService ) {
        this.translationService = translationService;
    }

    @Override
    public boolean accept( ValidationMessage checkMessage ) {
        return checkMessage instanceof PersistenceDescriptorValidationMessage;
    }

    @Override
    public ValidationMessage translate( ValidationMessage checkMessage ) {
        PersistenceDescriptorValidationMessage pdValidationMessage = ( PersistenceDescriptorValidationMessage ) checkMessage;
        ValidationMessage result = new ValidationMessage( );
        result.setId( checkMessage.getId( ) );
        result.setLevel( checkMessage.getLevel( ) );
        String translationKey = createTranslationKey( pdValidationMessage.getId( ) );

        if ( translationService.getTranslation( translationKey ) != null ) {
            if ( pdValidationMessage.getParams( ) != null && !pdValidationMessage.getParams( ).isEmpty( ) ) {
                pdValidationMessage.getParams().toArray( new String[ pdValidationMessage.getParams().size() ] );
                result.setText( translationService.format( translationKey, pdValidationMessage.getParams().toArray() ) );
            } else {
                result.setText( translationService.getTranslation( translationKey ) );
            }
        } else {
            result.setText( checkMessage.getText( ) );
        }
        return result;
    }

    private String createTranslationKey( long id ) {
        return PREFIX + id;
    }
}