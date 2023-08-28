/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client;

import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;

/**
 * Utility methods to perform user communication
 *
 */
// TODO: implement notification with PF4
@ApplicationScoped
public class RuntimeCommunication {


    public void showError(final String message) {
        showError(message, null);
    }

    public void showError(final String message, Object error) {
        logError(error);
    }

    public void showWarning(final String message) {
        showWarning(message, null);
    }

    public void showWarning(final String message, Object error) {
        logError(error);
    }

    public void showSuccess(final String message) {
    }

    public void showSuccess(final String message, Object error) {
        logError(error);
    }

    private void logError(Object error) {
        if (error != null) {
            DomGlobal.console.log(error);
        }
    }

}
