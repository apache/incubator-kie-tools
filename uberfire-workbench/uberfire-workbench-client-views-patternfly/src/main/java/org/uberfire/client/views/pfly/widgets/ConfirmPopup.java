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

package org.uberfire.client.views.pfly.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.resources.i18n.Constants;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ConfirmPopup {

    @Inject
    @DataField("confirm-title")
    Span modalTitle;

    @Inject
    @DataField("confirm-inline-notification")
    InlineNotification confirmInlineNotification;

    @Inject
    @DataField("confirm-message")
    Span modalConfirmationMessageLabel;

    @Inject
    @DataField("confirm-cancel")
    Button cancelButton;

    @Inject
    @DataField("confirm-ok")
    Button okButton;

    @Inject
    @DataField("modal")
    private Modal modal;

    @Inject
    private TranslationService translationService;

    private Command okCommand;

    @PostConstruct
    public void init() {
        cancelButton.setText(translationService.getTranslation(Constants.ConfirmPopup_Cancel));
    }

    public void show(final String title,
                     final String okButtonText,
                     final String confirmMessage,
                     final Command okCommand) {
        show(title,
             null,
             null,
             okButtonText,
             Button.ButtonStyleType.DANGER,
             confirmMessage,
             okCommand);
    }

    public void show(final String title,
                     final String inlineNotificationMessage,
                     final InlineNotification.InlineNotificationType inlineNotificationType,
                     final String okButtonText,
                     final Button.ButtonStyleType okButtonType,
                     final String confirmMessage,
                     final Command okCommand) {
        this.okCommand = okCommand;
        modalTitle.setTextContent(title);
        if (inlineNotificationMessage != null && inlineNotificationType != null) {
            confirmInlineNotification.setMessage(inlineNotificationMessage);
            confirmInlineNotification.setType(inlineNotificationType);
            confirmInlineNotification.getElement().getStyle().removeProperty("display");
        } else {
            confirmInlineNotification.getElement().getStyle().setProperty("display",
                                                                          "none");
        }
        okButton.setText(okButtonText);
        if (okButtonType != null) {
            okButton.setButtonStyleType(okButtonType);
        }
        modalConfirmationMessageLabel.setTextContent(confirmMessage);
        modal.show();
    }

    public void hide() {
        modal.hide();
    }

    public HTMLElement getElement() {
        return modal.getElement();
    }

    @EventHandler("confirm-ok")
    public void onOkClick(final @ForEvent("click") MouseEvent event) {
        if (okCommand != null) {
            okCommand.execute();
        }
        hide();
    }

    @EventHandler("confirm-cancel")
    public void onCancelClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }

    @EventHandler("confirm-close")
    public void onCloseClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }
}