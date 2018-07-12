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

package org.guvnor.ala.ui.openshift.client.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.openshift.service.OpenShiftClientService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

/**
 * Service for facilitating validations in client side.
 */
@ApplicationScoped
public class OpenShiftClientValidationService {

    private Caller<OpenShiftClientService> openShiftClientService;

    @Inject
    public OpenShiftClientValidationService(final Caller<OpenShiftClientService> openShiftClientService) {
        this.openShiftClientService = openShiftClientService;
    }

    public void isValidProjectName(final String projectName,
                                   final ValidatorCallback callback) {

        openShiftClientService.call(result -> {
                                        if (Boolean.TRUE.equals(result)) {
                                            callback.onSuccess();
                                        } else {
                                            callback.onFailure();
                                        }
                                    }).isValidProjectName(projectName);
    }
}
