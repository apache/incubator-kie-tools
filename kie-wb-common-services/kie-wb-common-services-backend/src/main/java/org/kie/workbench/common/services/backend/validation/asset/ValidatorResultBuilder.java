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

package org.kie.workbench.common.services.backend.validation.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;

public class ValidatorResultBuilder {

    final List<ValidationMessage> validationMessages;

    public ValidatorResultBuilder() {
        this.validationMessages = new ArrayList<>();
    }

    public ValidatorResultBuilder add( final List<BuildMessage> messages ) {
        validationMessages.addAll( filterValidationMessages( messages ) );
        return this;
    }

    public ValidatorResultBuilder remove( final List<BuildMessage> messages ) {
        validationMessages.removeAll( filterValidationMessages( messages ) );
        return this;
    }

    public List<ValidationMessage> results() {
        return validationMessages;
    }

    private List<ValidationMessage> filterValidationMessages( List<BuildMessage> messages ) {
        return messages
                .stream()
                .filter( message -> message.getLevel() == Level.ERROR )
                .map( message -> new ValidationMessage( message.getId(),
                                                        message.getLevel(),
                                                        message.getPath(),
                                                        message.getLine(),
                                                        message.getColumn(),
                                                        message.getText() ) )
                .collect( Collectors.toList() );
    }
}
