/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.client.views.pfly.widgets.ValidationState;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLTextAreaElement;

@Templated
public class SquashChangeRequestPopUpView implements SquashChangeRequestPopUpPresenter.View,
                                                     IsElement {

    private SquashChangeRequestPopUpPresenter presenter;
    private BaseModal modal;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("body")
    HTMLDivElement body;

    @Inject
    @DataField("commitMessage")
    HTMLTextAreaElement commitMessage;

    @Inject
    @DataField("message-input-help-inline")
    HelpBlock messageInputHelpInline;

    @Override
    public void init(final SquashChangeRequestPopUpPresenter presenter) {
        this.presenter = presenter;

        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.SquashAndMerge))
                .addBody(body)
                .addFooter(footer())
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.add(cancelButton());
        footer.add(okButton());
        return footer;
    }

    private Button okButton() {
        Button button = new Button(ts.format(LibraryConstants.Ok),
                                   event -> presenter.squash(commitMessage.value));
        button.setType(ButtonType.PRIMARY);
        return button;
    }

    private Button cancelButton() {
        Button button = new Button(ts.format(LibraryConstants.Cancel),
                                   event -> presenter.cancel());
        button.setType(ButtonType.DEFAULT);
        return button;
    }

    @Override
    public void show(String messages) {
        modal.show();
        commitMessage.value = messages;
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void showMessageInputError() {
        body.classList.add(ValidationState.ERROR.getCssName());
        messageInputHelpInline.setText(ts.getTranslation(LibraryConstants.InvalidCommitMessage));
    }

    @Override
    public void clearMessageInputError() {
        body.classList.remove(ValidationState.ERROR.getCssName());
        messageInputHelpInline.clearError();
    }
}
