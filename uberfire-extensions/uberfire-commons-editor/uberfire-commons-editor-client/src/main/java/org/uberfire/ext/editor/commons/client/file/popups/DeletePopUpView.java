/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class DeletePopUpView implements DeletePopUpPresenter.View,
                                        IsElement {

    @Inject
    @DataField("body")
    Div body;

    @Inject
    @DataField("bodyText")
    Paragraph bodyText;

    @Inject
    @DataField("error")
    Div error;

    @Inject
    @DataField("errorMessage")
    Span errorMessage;

    @Inject
    private TranslationService translationService;

    private DeletePopUpPresenter presenter;

    private BaseModal modal;

    @Override
    public void init(DeletePopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
        setupComment();
    }

    @Override
    public void show() {
        errorSetup();
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void setPrompt(final String prompt) {
        bodyText.setTextContent(prompt);
    }

    @Override
    public void handleDeleteNotAllowed() {
        showError(translate(Constants.DeletePopUpView_DeleteNotAllowed));
    }

    @Override
    public void handleUnexpectedError() {
        showError(translate(Constants.DeletePopUpView_UnexpectedError));
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(translate(Constants.DeletePopUpView_ConfirmDelete))
                .addBody(body)
                .addFooter(footer())
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.addButton(translate(Constants.DeletePopUpView_Cancel),
                         cancelCommand(),
                         ButtonType.DEFAULT);
        footer.addButton(translate(Constants.DeletePopUpView_Delete),
                         deleteCommand(),
                         IconType.REMOVE,
                         ButtonType.DANGER);
        return footer;
    }

    private String translate(final String key) {
        return translationService.format(key);
    }

    private Command deleteCommand() {
        return () -> presenter.delete();
    }

    private Command cancelCommand() {
        return () -> presenter.cancel();
    }

    private void setupComment() {
        body.appendChild(toggleCommentPresenter().getViewElement());
    }

    private ToggleCommentPresenter toggleCommentPresenter() {
        return presenter.getToggleCommentPresenter();
    }

    private void showError(final String errorMessage) {
        this.errorMessage.setTextContent(errorMessage);
        this.error.setHidden(false);
    }

    private void errorSetup() {
        this.error.setHidden(true);
    }
}
