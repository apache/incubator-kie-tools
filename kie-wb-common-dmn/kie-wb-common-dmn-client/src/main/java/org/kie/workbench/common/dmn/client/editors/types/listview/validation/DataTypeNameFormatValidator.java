/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.validation;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.api.editors.types.DMNValidationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsInvalidErrorMessage;
import org.uberfire.mvp.Command;

@Dependent
public class DataTypeNameFormatValidator {

    private final Caller<DMNValidationService> service;

    private final Event<FlashMessage> flashMessageEvent;

    private final DataTypeNameIsInvalidErrorMessage nameIsInvalidErrorMessage;

    @Inject
    public DataTypeNameFormatValidator(final Caller<DMNValidationService> service,
                                       final Event<FlashMessage> flashMessageEvent,
                                       final DataTypeNameIsInvalidErrorMessage nameIsInvalidErrorMessage) {
        this.service = service;
        this.flashMessageEvent = flashMessageEvent;
        this.nameIsInvalidErrorMessage = nameIsInvalidErrorMessage;
    }

    public void ifIsValid(final DataType dataType,
                          final Command onSuccess) {

        final RemoteCallback<Boolean> callback = getCallback(dataType, onSuccess);
        final String dataTypeName = dataType.getName();

        service.call(callback).isValidVariableName(dataTypeName);
    }

    RemoteCallback<Boolean> getCallback(final DataType dataType,
                                        final Command onSuccess) {
        return isValid -> {

            if (isValid) {
                onSuccess.execute();
            } else {
                flashMessageEvent.fire(nameIsInvalidErrorMessage.getFlashMessage(dataType));
            }
        };
    }
}
