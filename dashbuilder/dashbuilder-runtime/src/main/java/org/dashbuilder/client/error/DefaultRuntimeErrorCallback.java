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

import com.google.gwt.user.client.Window;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.jboss.errai.bus.client.api.InvalidBusContentException;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

import static org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup.newYesNoCancelPopup;
import static org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup.showMessage;

@Dependent
public class DefaultRuntimeErrorCallback {

    public static final String PARSING_JSON_SYNTAX_MSG = "Error parsing JSON: SyntaxError: JSON.parse: unexpected character at line 1 column 2 of the JSON data";
    public static final String PARSING_JSON_MSG = "Error parsing JSON: SyntaxError: Unexpected token � in JSON at position 1";
    public static final String SCRIPT_ERROR_MSG = "Script error. (:0)";

    @Inject
    BusyIndicatorView loading;

    AppConstants i18n = AppConstants.INSTANCE;

    private boolean errorPopUpLock = false;

    public void error(final Throwable throwable) {
        if (errorPopUpLock) {
            return;
        }
        loading.hideBusyIndicator();
        errorPopUpLock = true;
        if (isServerOfflineException(throwable)) {
            showPopup(i18n.disconnectedFromServer(), i18n.couldNotConnectToServer());
        } else if (isInvalidBusContentException(throwable)) {
            showPopup(i18n.sessionTimeout(), i18n.invalidBusResponseProbablySessionTimeout());
        } else {
            showMessage(CommonConstants.INSTANCE.ExceptionGeneric0(extractMessageRecursively(throwable)),
                        this::unlock,
                        this::unlock);
        }

    }

    private void showPopup(String title, String content) {
        final YesNoCancelPopup result = newYesNoCancelPopup(title,
                                                            content,
                                                            Window.Location::reload,
                                                            null,
                                                            this::unlock);
        result.clearScrollHeight();
        result.show();
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