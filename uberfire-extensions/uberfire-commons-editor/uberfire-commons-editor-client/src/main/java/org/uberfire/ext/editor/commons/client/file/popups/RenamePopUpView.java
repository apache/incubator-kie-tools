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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class RenamePopUpView implements RenamePopUpPresenter.View,
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

    private RenamePopUpPresenter presenter;

    private BaseModal modal;

    private Button renameButton;

    private String originalFileName;

    @Override
    public void init(RenamePopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public void show() {
        errorSetup();
        setupComment();
        newNameTextBoxSetup();
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void handleDuplicatedFileName() {
        showError(translate(Constants.RenamePopUpView_FileAlreadyExists,
                            newNameTextBox.getValue()));
    }

    @Override
    public void handleInvalidFileName() {
        showError(translate(Constants.RenamePopUpView_InvalidFileName,
                            newNameTextBox.getValue()));
    }

    @Override
    public void setOriginalFileName(String fileName) {
        originalFileName = fileName;
    }

    @Override
    public void handleRenameNotAllowed() {
        showError(translate(Constants.RenamePopUpView_RenameNotAllowed));
    }

    @EventHandler("newNameTextBox")
    public void onNewFileNameChange(KeyUpEvent event) {
        disableRenameButtonIfNewNameIsNotNew();
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(translate(Constants.RenamePopUpView_RenameAsset))
                .addBody(body)
                .addFooter(footer())
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.add(cancelButton());
        footer.add(renameButton());
        return footer;
    }

    private Button renameButton() {
        renameButton = button(translate(Constants.RenamePopUpView_Rename),
                              renameCommand(),
                              ButtonType.PRIMARY);
        return renameButton;
    }

    private Button cancelButton() {
        return button(translate(Constants.RenamePopUpView_Cancel),
                      cancelCommand(),
                      ButtonType.DEFAULT);
    }

    private Button button(final String text,
                          final Command command,
                          final ButtonType type) {
        Button button = new Button(text,
                                   new ClickHandler() {
                                       @Override
                                       public void onClick(ClickEvent event) {
                                           command.execute();
                                       }
                                   });
        button.setType(type);
        return button;
    }

    private String translate(final String key,
                             Object... args) {
        return translationService.format(key,
                                         args);
    }

    private void newNameTextBoxSetup() {
        newNameTextBox.setValue(originalFileName);
        disableRenameButtonIfNewNameIsNotNew();
    }

    private void errorSetup() {
        this.error.setHidden(true);
    }

    private void disableRenameButtonIfNewNameIsNotNew() {
        renameButton.setEnabled(!newNameTextBox.getValue().equals(originalFileName));
    }

    private void showError(String errorMessage) {
        this.errorMessage.setTextContent(errorMessage);
        this.error.setHidden(false);
    }

    private Command renameCommand() {
        return () -> presenter.rename(newNameTextBox.getValue());
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
