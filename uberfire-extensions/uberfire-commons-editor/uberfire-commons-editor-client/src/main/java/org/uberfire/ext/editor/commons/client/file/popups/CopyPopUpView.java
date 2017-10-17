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
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
@Templated
@FallbackImplementation
public class CopyPopUpView implements CopyPopUpPresenter.View,
                                      IsElement {

    @Inject
    @DataField("body")
    Div body;

    @Inject
    @DataField("newNameTextBox")
    TextBox newNameTextBox;

    @Inject
    @DataField("error")
    Div error;

    @Inject
    @DataField("errorMessage")
    Span errorMessage;

    @Inject
    private TranslationService translationService;

    private CopyPopUpPresenter presenter;

    private BaseModal modal;

    @Override
    public void init(CopyPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
        setupComment();
    }

    @Override
    public void show() {
        errorSetup();
        newNameTextBoxSetup();
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void handleDuplicatedFileName() {
        showError(translate(Constants.CopyPopUpView_FileAlreadyExists,
                            newNameTextBox.getValue()));
    }

    @Override
    public void handleInvalidFileName() {
        showError(translate(Constants.CopyPopUpView_InvalidFileName,
                            newNameTextBox.getValue()));
    }

    @Override
    public Path getTargetPath() {
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public void handleCopyNotAllowed() {
        showError(translate(Constants.CopyPopUpView_CopyNotAllowed));
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(translate(Constants.CopyPopUpView_MakeACopy))
                .addBody(body)
                .addFooter(footer())
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.addButton(translate(Constants.CopyPopUpView_Cancel),
                         cancelCommand(),
                         ButtonType.DEFAULT);
        footer.addButton(translate(Constants.CopyPopUpView_MakeACopy),
                         copyCommand(),
                         ButtonType.PRIMARY);
        return footer;
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }

    private Command copyCommand() {
        return () -> presenter.copy(newNameTextBox.getValue());
    }

    private void newNameTextBoxSetup() {
        newNameTextBox.setValue("");
    }

    private void errorSetup() {
        this.error.setHidden(true);
    }

    private void showError(String errorMessage) {
        this.errorMessage.setTextContent(errorMessage);
        this.error.setHidden(false);
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
}
