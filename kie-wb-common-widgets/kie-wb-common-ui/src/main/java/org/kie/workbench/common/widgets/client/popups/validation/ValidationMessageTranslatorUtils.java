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

package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;

@ApplicationScoped
public class ValidationMessageTranslatorUtils {

    private List<ValidationMessageTranslator> validationMessageTranslators = new ArrayList<>();

    public ValidationMessageTranslatorUtils() {
    }

    @Inject
    public ValidationMessageTranslatorUtils( Instance<ValidationMessageTranslator> checkTranslators ) {
        checkTranslators.forEach( this.validationMessageTranslators::add );
    }

    public List<ValidationMessage> translate( List<ValidationMessage> messages ) {
        return messages.stream().map( m -> lookUpTranslation( m ) ).collect( Collectors.toList() );
    }

    private ValidationMessage lookUpTranslation( ValidationMessage messageToTranslate ) {
        return validationMessageTranslators.stream()
                .filter( t -> t.accept( messageToTranslate ) )
                .map( t -> t.translate( messageToTranslate ) )
                .findFirst()
                .orElse( messageToTranslate );
    }

    // Test purposes
    void setValidationMessageTranslators( List<ValidationMessageTranslator> messageTranslators ) {
        this.validationMessageTranslators = messageTranslators;
    }
}
