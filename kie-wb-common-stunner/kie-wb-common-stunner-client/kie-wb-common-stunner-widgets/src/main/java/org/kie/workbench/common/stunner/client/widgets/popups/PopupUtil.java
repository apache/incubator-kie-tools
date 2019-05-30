/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.popups;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class PopupUtil {

    private final ConfirmPopup confirmPopup;

    public PopupUtil() {
        this(null);
    }

    @Inject
    public PopupUtil(final ConfirmPopup confirmPopup) {
        this.confirmPopup = confirmPopup;
    }

    public void showConfirmPopup(final String title,
                                 final String okButtonText,
                                 final String confirmMessage,
                                 final Command okCommand) {
        confirmPopup.show(title,
                          okButtonText,
                          confirmMessage,
                          okCommand);
    }

    public void showConfirmPopup(final String title,
                                 final String inlineNotificationMessage,
                                 final InlineNotification.InlineNotificationType inlineNotificationType,
                                 final String okButtonText,
                                 final Button.ButtonStyleType okButtonType,
                                 final String confirmMessage,
                                 final Command okCommand) {
        confirmPopup.show(title,
                          inlineNotificationMessage,
                          inlineNotificationType,
                          okButtonText,
                          okButtonType,
                          confirmMessage,
                          okCommand);
    }

    public void showYesNoCancelPopup(final String title,
                                     final String message,
                                     final Command yesCommand,
                                     final Command noCommand) {

        final Command cancelCommand = () -> {
            // Do nothing, but let the cancel button be shown.
        };
        final YesNoCancelPopup yesNoCancelPopup = buildYesNoCancelPopup(title,
                                                                        message,
                                                                        yesCommand,
                                                                        noCommand,
                                                                        cancelCommand);
        yesNoCancelPopup.clearScrollHeight();
        yesNoCancelPopup.setClosable(false);
        yesNoCancelPopup.show();
    }

    /**
     * intended for testing purposes
     */
    YesNoCancelPopup buildYesNoCancelPopup(final String title,
                                           final String message,
                                           final Command yesCommand,
                                           final Command noCommand,
                                           final Command cancelCommand) {
        return YesNoCancelPopup.newYesNoCancelPopup(title,
                                                    message,
                                                    yesCommand,
                                                    noCommand,
                                                    cancelCommand);
    }
}
