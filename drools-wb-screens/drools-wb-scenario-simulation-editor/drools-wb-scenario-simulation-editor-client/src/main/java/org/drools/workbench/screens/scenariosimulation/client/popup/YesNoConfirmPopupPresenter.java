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

package org.drools.workbench.screens.scenariosimulation.client.popup;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.mvp.Command;

@Dependent
public class YesNoConfirmPopupPresenter implements YesNoConfirmPopup.Presenter {

    @Inject
    protected YesNoConfirmPopupView yesNoConfirmPopupView;

    @Override
    public void show(final String title,
                     final String okButtonText,
                     final String confirmMessage,
                     final Command okCommand) {
        show(title,
             null,
             null,
             okButtonText,
             org.uberfire.client.views.pfly.widgets.Button.ButtonStyleType.DANGER,
             confirmMessage,
             okCommand);
    }

    @Override
    public void show(final String title,
                     final String inlineNotificationMessage,
                     final InlineNotification.InlineNotificationType inlineNotificationType,
                     final String okButtonText,
                     final Button.ButtonStyleType okButtonType,
                     final String confirmMessage,
                     final Command okCommand) {
        yesNoConfirmPopupView.show(title, inlineNotificationMessage, inlineNotificationType, okButtonText, okButtonType, confirmMessage, okCommand);
    }

    @Override
    public void show(String title, String yesButtonText, String noButtonText, String confirmMessage, Command yesCommand, Command noCommand) {
        show(title, null, null, yesButtonText, noButtonText, Button.ButtonStyleType.DANGER,  Button.ButtonStyleType.DEFAULT, confirmMessage, yesCommand, noCommand);
    }

    @Override
    public void show(String title, String inlineNotificationMessage, InlineNotification.InlineNotificationType inlineNotificationType, String yesButtonText, String noButtonText, Button.ButtonStyleType yesButtonType, Button.ButtonStyleType noButtonType, String confirmMessage, Command yesCommand, Command noCommand) {
        yesNoConfirmPopupView.show(title, inlineNotificationMessage, inlineNotificationType, yesButtonText, noButtonText, yesButtonType,  noButtonType, confirmMessage, yesCommand, noCommand);
    }

    @Override
    public void hide() {
        yesNoConfirmPopupView.hide();
    }
}
