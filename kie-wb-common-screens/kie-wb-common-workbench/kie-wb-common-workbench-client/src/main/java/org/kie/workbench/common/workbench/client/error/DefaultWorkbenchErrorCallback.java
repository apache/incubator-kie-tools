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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.dashbuilder.dataset.exception.DataSetLookupException;
import org.jboss.errai.bus.client.api.InvalidBusContentException;
import org.kie.server.api.exception.KieServicesException;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.workbench.common.workbench.client.entrypoint.GenericErrorPopup;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

@Dependent
public class DefaultWorkbenchErrorCallback {

    @Inject
    private GenericErrorPopup genericErrorPopup;

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

    public void error(final Throwable throwable) {

        if (isInvalidBusContentException(throwable)) {
            final YesNoCancelPopup result = YesNoCancelPopup.newYesNoCancelPopup(
                    DefaultWorkbenchConstants.INSTANCE.SessionTimeout(),
                    DefaultWorkbenchConstants.INSTANCE.InvalidBusResponseProbablySessionTimeout(),
                    Window.Location::reload,
                    null,
                    () -> {
                    });

            result.clearScrollHeight();
            result.show();
            return;
        }

        if (isKieServerForbiddenException(throwable)) {
            ErrorPopup.showMessage(DefaultWorkbenchConstants.INSTANCE.KieServerError403());
            return;
        }

        if (isKieServerUnauthorizedException(throwable)) {
            ErrorPopup.showMessage(DefaultWorkbenchConstants.INSTANCE.KieServerError401());
            return;
        }

        if (throwable instanceof DataSetLookupException) {
            DataSetLookupException ex = (DataSetLookupException) throwable;
            ErrorPopup.showMessage(CommonConstants.INSTANCE.ExceptionGeneric0(ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage()));
            return;
        }

        if (throwable instanceof KieServicesHttpException) {
            KieServicesHttpException ex = (KieServicesHttpException) throwable;
            ErrorPopup.showMessage(CommonConstants.INSTANCE.ExceptionGeneric0(ex.getExceptionMessage()));
            return;
        }

        if (throwable instanceof KieServicesException) {
            KieServicesException ex = (KieServicesException) throwable;
            ErrorPopup.showMessage(CommonConstants.INSTANCE.ExceptionGeneric0(ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage()));
            return;
        }

        genericErrorPopup.setup("Uncaught exception: " + extractMessageRecursively(throwable));
        genericErrorPopup.show();
    }


    private String extractMessageRecursively(final Throwable t) {
        if (t.getCause() == null) {
            return t.getMessage();
        }

        return t.getMessage() + " Caused by: " + extractMessageRecursively(t.getCause());
    }
}