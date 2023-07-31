/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.error;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.patternfly.busyindicator.BusyIndicator;
import org.jboss.errai.bus.client.api.InvalidBusContentException;

@Dependent
public class DefaultRuntimeErrorCallback {

    public static final String PARSING_JSON_SYNTAX_MSG =
            "Error parsing JSON: SyntaxError: JSON.parse: unexpected character at line 1 column 2 of the JSON data";
    public static final String PARSING_JSON_MSG =
            "Error parsing JSON: SyntaxError: Unexpected token � in JSON at position 1";
    public static final String SCRIPT_ERROR_MSG = "Script error. (:0)";

    @Inject
    BusyIndicator busyIndicator;

    AppConstants i18n = AppConstants.INSTANCE;

    private boolean errorPopUpLock = false;

    public enum DefaultErrorType {
        SERVER_OFFLINE,
        NOT_LOGGED,
        NOT_AUTHORIZED,
        OTHER;
    }

    public void error(final Throwable throwable) {
        String message = extractMessageRecursively(throwable);
        if (isServerOfflineException(throwable)) {
            error(DefaultErrorType.SERVER_OFFLINE, message);
        } else if (isInvalidBusContentException(throwable)) {
            error(DefaultErrorType.NOT_LOGGED, message);
        } else {
            error(DefaultErrorType.OTHER, message);
            DomGlobal.console.error(throwable);
        }
    }

    public void error(DefaultErrorType type) {
        error(type, null);
    }

    public void error(DefaultErrorType type, String message) {
        if (errorPopUpLock) {
            return;
        }
        busyIndicator.hide();
        errorPopUpLock = true;

        switch (type) {
            case NOT_LOGGED:
                showPopup(i18n.invalidBusResponseProbablySessionTimeout());
                break;
            case SERVER_OFFLINE:
                showPopup(i18n.couldNotConnectToServer());
                break;
            case NOT_AUTHORIZED:
                showPopup(i18n.notAuthorized());
                break;
            case OTHER:
            default:
                DomGlobal.console.log(message);
                this.unlock();
                break;
        }
    }

    private void showPopup(String content) {
        var result = DomGlobal.confirm(content);
        if (result) {
            DomGlobal.window.location.reload();
        }

        this.unlock();
    }

    protected static String extractMessageRecursively(final Throwable t) {
        if (t == null) {
            return "";
        }
        if (t.getCause() == null) {
            return t.getMessage();
        }
        return t.getMessage() + " Caused by: " + extractMessageRecursively(t.getCause());
    }

    private static boolean isInvalidBusContentException(final Throwable throwable) {
        return throwable instanceof InvalidBusContentException;
    }

    protected static boolean isServerOfflineException(final Throwable throwable) {
        Throwable cause = throwable.getCause();
        String message = throwable.getMessage();
        List<String> messages = Arrays.asList(SCRIPT_ERROR_MSG,
                PARSING_JSON_MSG,
                PARSING_JSON_SYNTAX_MSG);

        return cause == null && message != null && messages.stream().anyMatch(message::equals);
    }

    public void unlock() {
        errorPopUpLock = false;
    }

}
