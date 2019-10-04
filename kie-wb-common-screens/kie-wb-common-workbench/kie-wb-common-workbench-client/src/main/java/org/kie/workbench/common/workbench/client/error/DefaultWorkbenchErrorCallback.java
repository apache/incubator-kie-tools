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

package org.kie.workbench.common.workbench.client.error;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;

import org.dashbuilder.dataset.exception.DataSetLookupException;
import org.jboss.errai.bus.client.api.InvalidBusContentException;
import org.kie.server.api.exception.KieServicesException;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.workbench.common.workbench.client.entrypoint.GenericErrorPopup;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

@Dependent
public class DefaultWorkbenchErrorCallback {

    @Inject
    private GenericErrorPopup genericErrorPopup;

    private Queue<Throwable> queue = new ArrayDeque<>();

    public static boolean isKieServerForbiddenException(final Throwable throwable) {
        if (throwable instanceof KieServicesHttpException && ((KieServicesHttpException) throwable).getHttpCode() == 403) {
            return true;
        }
        //Cause type isn't propagated, manually checking the message content
        //Expect to get a string like: 'Unexpected HTTP response code when requesting URI ''! Error code: 403, message: <html><head><title>Error</title></head><body>Forbidden</body></html>'
        if (throwable instanceof DataSetLookupException && (throwable.getCause() != null && throwable.getCause().getMessage().contains("Error code: 403"))) {
            return true;
        }
        return false;
    }

    public static boolean isKieServerUnauthorizedException(final Throwable throwable) {
        if (throwable instanceof KieServicesHttpException && ((KieServicesHttpException) throwable).getHttpCode() == 401) {
            return true;
        }
        //Cause type isn't propagated, manually checking the message content
        //Expect to get a string like: 'Unexpected HTTP response code when requesting URI ''! Error code: 401, message: <html><head><title>Error</title></head><body>Unauthorized</body></html>'
        if (throwable instanceof DataSetLookupException && (throwable.getCause() != null && throwable.getCause().getMessage().contains("Error code: 401"))) {
            return true;
        }
        return false;
    }

    public static boolean isInvalidBusContentException(final Throwable throwable) {
        return throwable instanceof InvalidBusContentException;
    }

    public static boolean isServerOfflineException(final Throwable throwable) {
        Throwable cause = throwable.getCause();
        String message = throwable.getMessage();
        List<String> messages = Arrays.asList(
                "Script error. (:0)",
                "Error parsing JSON: SyntaxError: Unexpected token � in JSON at position 1",
                "Error parsing JSON: SyntaxError: JSON.parse: unexpected character at line 1 column 2 of the JSON data");

        return cause == null
            && message != null
            && messages.stream().anyMatch(m -> message.equals(m));
    }

    public void error(final Throwable throwable) {
        BusyPopup.close();
        queue(throwable);

        if (queue.size() == 1) {
            processQueue();
        }
    }
    
    public void queue(final Throwable throwable) {
        queue.add(throwable);
    }

    public void dequeue() {
        queue.poll();
        processQueue();
    }

    public void processQueue() {
        Throwable throwable = queue.peek();

        if (throwable == null) {
            return;
        }

        if (isServerOfflineException(throwable)) {
            final YesNoCancelPopup result = YesNoCancelPopup.newYesNoCancelPopup(
                    DefaultWorkbenchConstants.INSTANCE.DisconnectedFromServer(),
                    DefaultWorkbenchConstants.INSTANCE.CouldNotConnectToServer(),
                    Window.Location::reload,
                    null,
                    this::dequeue);

            result.clearScrollHeight();
            result.show();
            return;
        }

        if (isInvalidBusContentException(throwable)) {
            final YesNoCancelPopup result = YesNoCancelPopup.newYesNoCancelPopup(
                    DefaultWorkbenchConstants.INSTANCE.SessionTimeout(),
                    DefaultWorkbenchConstants.INSTANCE.InvalidBusResponseProbablySessionTimeout(),
                    Window.Location::reload,
                    null,
                    this::dequeue);

            result.clearScrollHeight();
            result.show();
            return;
        }

        if (isKieServerForbiddenException(throwable)) {
            ErrorPopup.showMessage(
                    DefaultWorkbenchConstants.INSTANCE.KieServerError403(),
                    () -> {},
                    this::dequeue);
            return;
        }

        if (isKieServerUnauthorizedException(throwable)) {
            ErrorPopup.showMessage(
                    DefaultWorkbenchConstants.INSTANCE.KieServerError401(),
                    () -> {},
                    this::dequeue);
            return;
        }

        if (throwable instanceof DataSetLookupException) {
            DataSetLookupException ex = (DataSetLookupException) throwable;
            ErrorPopup.showMessage(
                    CommonConstants.INSTANCE.ExceptionGeneric0(ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage()),
                    () -> {},
                    this::dequeue);
            return;
        }

        if (throwable instanceof KieServicesHttpException) {
            KieServicesHttpException ex = (KieServicesHttpException) throwable;
            ErrorPopup.showMessage(
                    CommonConstants.INSTANCE.ExceptionGeneric0(ex.getExceptionMessage()),
                    () -> {},
                    this::dequeue);
            return;
        }

        if (throwable instanceof KieServicesException) {
            KieServicesException ex = (KieServicesException) throwable;
            ErrorPopup.showMessage(
                    CommonConstants.INSTANCE.ExceptionGeneric0(ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage()),
                    () -> {},
                    this::dequeue);
            return;
        }

        genericErrorPopup.show();
        genericErrorPopup.setup("Uncaught exception: " + extractMessageRecursively(throwable),
                                this::dequeue);
    }

    private String extractMessageRecursively(final Throwable t) {
        if (t.getCause() == null) {
            return t.getMessage();
        }

        return t.getMessage() + " Caused by: " + extractMessageRecursively(t.getCause());
    }
}
