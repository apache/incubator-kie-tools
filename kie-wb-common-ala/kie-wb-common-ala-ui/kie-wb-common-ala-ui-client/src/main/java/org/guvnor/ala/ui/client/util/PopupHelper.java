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

package org.guvnor.ala.ui.client.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

import static org.uberfire.client.views.pfly.widgets.ErrorPopup.DisplayMode.STANDARD;

@ApplicationScoped
public class PopupHelper
        implements HasBusyIndicator {

    private final ErrorPopup errorPopup;

    @Inject
    public PopupHelper(final ErrorPopup errorPopup) {
        this.errorPopup = errorPopup;
    }

    public void showInformationPopup(final String message) {
        showNotificationPopup(CommonConstants.INSTANCE.Information(),
                              message);
    }

    public void showErrorPopup(final String message) {
        errorPopup.showError(message,
                             STANDARD);
    }

    public void showErrorPopup(final String message,
                               final String detail) {
        errorPopup.showError(message,
                             detail,
                             STANDARD);
    }

    public void showYesNoPopup(final String title,
                               final String message,
                               final Command yesCommand,
                               final Command noCommand) {
        YesNoCancelPopup popup = newYesNoPopup(title,
                                               message,
                                               yesCommand,
                                               noCommand);

        popup.setClosable(false);
        popup.clearScrollHeight();
        popup.show();
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    public ErrorCallback<Message> getPopupErrorCallback() {
        return (message, throwable) -> {
            showErrorPopup(throwable.getMessage());
            return false;
        };
    }

    private void showNotificationPopup(final String title,
                                       final String message) {
        YesNoCancelPopup popup = newNotificationPopup(title,
                                                      message);
        popup.setClosable(false);
        popup.clearScrollHeight();
        popup.show();
    }

    protected YesNoCancelPopup newYesNoPopup(final String title,
                                             final String message,
                                             final Command yesCommand,
                                             final Command noCommand) {
        return YesNoCancelPopup.newYesNoCancelPopup(title,
                                                    message,
                                                    yesCommand,
                                                    noCommand,
                                                    null);
    }

    protected YesNoCancelPopup newNotificationPopup(final String title,
                                                    final String message) {

        return YesNoCancelPopup.newYesNoCancelPopup(title,
                                                    message,
                                                    () -> {
                                                    },
                                                    CommonConstants.INSTANCE.OK(),
                                                    null,
                                                    null,
                                                    null,
                                                    null);
    }
}
