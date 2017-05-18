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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ConfirmPopup {

    @Inject
    @DataField("confirm-title")
    Span modalTitle;

    @Inject
    @DataField("confirm-message")
    Span modalConfirmationMessageLabel;

    @Inject
    @DataField("confirm-ok-button-text")
    Span okButton;

    @Inject
    @DataField("modal")
    private Modal modal;

    private Command okCommand;

    public void show(String title,
                     String okButtonText,
                     String confirmMessage,
                     final Command okCommand) {
        this.okCommand = okCommand;
        modalTitle.setTextContent(title);
        okButton.setTextContent(okButtonText);
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