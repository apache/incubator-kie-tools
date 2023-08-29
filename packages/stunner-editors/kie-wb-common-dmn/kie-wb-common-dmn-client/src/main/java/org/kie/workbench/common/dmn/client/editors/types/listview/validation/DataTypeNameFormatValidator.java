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

package org.kie.workbench.common.dmn.client.editors.types.listview.validation;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsInvalidErrorMessage;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.mvp.Command;

@Dependent
public class DataTypeNameFormatValidator {

    private final DMNClientServicesProxy clientServicesProxy;

    private final Event<FlashMessage> flashMessageEvent;

    private final DataTypeNameIsInvalidErrorMessage nameIsInvalidErrorMessage;

    @Inject
    public DataTypeNameFormatValidator(final DMNClientServicesProxy clientServicesProxy,
                                       final Event<FlashMessage> flashMessageEvent,
                                       final DataTypeNameIsInvalidErrorMessage nameIsInvalidErrorMessage) {
        this.clientServicesProxy = clientServicesProxy;
        this.flashMessageEvent = flashMessageEvent;
        this.nameIsInvalidErrorMessage = nameIsInvalidErrorMessage;
    }

    public void ifIsValid(final DataType dataType,
                          final Command onSuccess) {
        final String dataTypeName = dataType.getName();

        clientServicesProxy.isValidVariableName(dataTypeName,
                                                getCallback(dataType, onSuccess));
    }

    ServiceCallback<Boolean> getCallback(final DataType dataType,
                                         final Command onSuccess) {
        return new ServiceCallback<Boolean>() {
            @Override
            public void onSuccess(final Boolean isValid) {
                if (isValid) {
                    onSuccess.execute();
                } else {
                    flashMessageEvent.fire(nameIsInvalidErrorMessage.getFlashMessage(dataType));
                }
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                clientServicesProxy.logWarning(error);
            }
        };
    }
}
