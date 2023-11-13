/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.client.popup;

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
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class YesNoConfirmPopupView implements YesNoConfirmPopup {

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
    @DataField("confirm-yes")
    Button yesButton;

    @Inject
    @DataField("confirm-no")
    Button noButton;

    @Inject
    @DataField("modal")
    Modal modal;

    @Inject
    private TranslationService translationService;

    Command okCommand;

    Command yesCommand;

    Command noCommand;

    @PostConstruct
    public void init() {
        cancelButton.setText("Cancel");
    }

    @Override
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

    @Override
    public void show(final String title,
                     final String inlineNotificationMessage,
                     final InlineNotification.InlineNotificationType inlineNotificationType,
                     final String okButtonText,
                     final Button.ButtonStyleType okButtonType,
                     final String confirmMessage,
                     final Command okCommand) {
        this.okCommand = okCommand;
        okButton.show();
        yesButton.hide();
        noButton.hide();
        okButton.setText(okButtonText);
        if (okButtonType != null) {
            okButton.setButtonStyleType(okButtonType);
        }
        commonShow(title, inlineNotificationMessage, inlineNotificationType, confirmMessage);
    }

    @Override
    public void show(final String title,
                     final String yesButtonText,
                     final String noButtonText,
                     final String confirmMessage,
                     final Command yesCommand,
                     final Command noCommand) {
        show(title,
             null,
             null,
             yesButtonText,
             noButtonText,
             Button.ButtonStyleType.DANGER,
             Button.ButtonStyleType.DEFAULT,
             confirmMessage,
             yesCommand,
             noCommand);
    }

    @Override
    public void show(final String title,
                     final String inlineNotificationMessage,
                     final InlineNotification.InlineNotificationType inlineNotificationType,
                     final String yesButtonText,
                     final String noButtonText,
                     final Button.ButtonStyleType yesButtonType,
                     final Button.ButtonStyleType noButtonType,
                     final String confirmMessage,
                     final Command yesCommand,
                     final Command noCommand) {
        this.yesCommand = yesCommand;
        this.noCommand = noCommand;
        okButton.hide();
        yesButton.show();
        noButton.show();
        yesButton.setText(yesButtonText);
        noButton.setText(noButtonText);
        if (yesButtonType != null) {
            yesButton.setButtonStyleType(yesButtonType);
        }
        if (noButtonType != null) {
            noButton.setButtonStyleType(noButtonType);
        }
        commonShow(title, inlineNotificationMessage, inlineNotificationType, confirmMessage);
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    @Override
    public void hide() {
        modal.hide();
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

    @EventHandler("confirm-yes")
    public void onYesClick(final @ForEvent("click") MouseEvent event) {
        if (yesCommand != null) {
            yesCommand.execute();
        }
        this.hide();
    }

    @EventHandler("confirm-no")
    public void onNoClick(final @ForEvent("click") MouseEvent event) {
        if (noCommand != null) {
            noCommand.execute();
        }
        this.hide();
    }

    protected void commonShow(final String title, final String inlineNotificationMessage,
                              final InlineNotification.InlineNotificationType inlineNotificationType, final String confirmMessage) {
        modalTitle.setTextContent(title);
        if (inlineNotificationMessage != null && inlineNotificationType != null) {
            confirmInlineNotification.setMessage(inlineNotificationMessage);
            confirmInlineNotification.setType(inlineNotificationType);
            confirmInlineNotification.getElement().getStyle().removeProperty("display");
        } else {
            confirmInlineNotification.getElement().getStyle().setProperty("display", "none");
        }
        modalConfirmationMessageLabel.setTextContent(confirmMessage);
        modal.show();
    }
}
