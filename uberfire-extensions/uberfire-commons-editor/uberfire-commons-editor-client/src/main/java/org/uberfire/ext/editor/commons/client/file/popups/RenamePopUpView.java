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
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Node;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
@Templated
public class RenamePopUpView implements RenamePopUpPresenter.View {

    @DataField("modal-view")
    private HTMLDivElement view;

    @DataField("modal-body")
    private HTMLDivElement body;

    @DataField("modal-footer")
    private HTMLDivElement footer;

    @DataField("cancel")
    private HTMLButtonElement cancel;

    @DataField("rename")
    private HTMLButtonElement rename;

    @DataField("saveAndRename")
    private HTMLButtonElement saveAndRename;

    @DataField("newNameTextBox")
    private HTMLInputElement newNameTextBox;

    @DataField("error")
    private HTMLDivElement error;

    @DataField("errorMessage")
    private HTMLElement errorMessage;

    private TranslationService translationService;

    private RenamePopUpPresenter presenter;

    private BaseModal modal;

    private String originalFileName;

    @Inject
    public RenamePopUpView(final HTMLDivElement body,
                           final HTMLDivElement view,
                           final HTMLButtonElement cancel,
                           final HTMLButtonElement rename,
                           final HTMLButtonElement saveAndRename,
                           final HTMLDivElement footer,
                           final HTMLInputElement newNameTextBox,
                           final HTMLDivElement error,
                           final @Named("span") HTMLElement errorMessage,
                           final TranslationService translationService) {
        this.body = body;
        this.view = view;
        this.cancel = cancel;
        this.footer = footer;
        this.rename = rename;
        this.saveAndRename = saveAndRename;
        this.newNameTextBox = newNameTextBox;
        this.error = error;
        this.errorMessage = errorMessage;
        this.translationService = translationService;
    }

    @Override
    public void init(RenamePopUpPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        setup();
        modal.show();
    }

    private void setup() {
        modalSetup();
        errorSetup();
        setupComment();
        newNameTextBoxSetup();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void handleDuplicatedFileName() {
        showError(translate(Constants.RenamePopUpView_FileAlreadyExists, newNameTextBox.value));
    }

    @Override
    public void handleInvalidFileName() {
        showError(translate(Constants.RenamePopUpView_InvalidFileName, newNameTextBox.value));
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
    public void onNewFileNameChange(final KeyUpEvent event) {
        disableRenameButtonsIfNewNameIsNotNew();
    }

    @EventHandler("cancel")
    private void cancelCommand(final ClickEvent event) {
        presenter.cancel();
    }

    @EventHandler("rename")
    private void onRename(final ClickEvent event) {
        presenter.rename(newNameTextBox.value);
    }

    @EventHandler("saveAndRename")
    private void onSaveAndRename(final ClickEvent event) {
        presenter.saveAndRename(newNameTextBox.value);
    }

    @Override
    public void renameAsPrimary() {
        addPrimaryClass(rename);
        removePrimaryClass(saveAndRename);
    }

    @Override
    public void saveAndRenameAsPrimary() {
        addPrimaryClass(saveAndRename);
        removePrimaryClass(rename);
    }

    @Override
    public void hideSaveAndRename(final boolean hidden) {
        saveAndRename.hidden = hidden;
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(translate(Constants.RenamePopUpView_RenameAsset))
                .addBody(body)
                .addFooter(footer)
                .build();
    }

    private void addPrimaryClass(final HTMLButtonElement buttonElement) {
        buttonElement.classList.add(ButtonType.PRIMARY.getCssName());
    }

    private void removePrimaryClass(final HTMLButtonElement buttonElement) {
        buttonElement.classList.remove(ButtonType.PRIMARY.getCssName());
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key, args);
    }

    private void newNameTextBoxSetup() {

        newNameTextBox.value = originalFileName;
        disableRenameButtonsIfNewNameIsNotNew();
    }

    private void errorSetup() {
        this.error.hidden = true;
    }

    private void disableRenameButtonsIfNewNameIsNotNew() {

        final boolean disabled = newNameTextBox.value.equals(originalFileName);

        rename.disabled = disabled;
        saveAndRename.disabled = disabled;
    }

    private void showError(final String errorMessage) {
        this.errorMessage.textContent = errorMessage;
        this.error.hidden = false;
    }

    private void setupComment() {
        body.appendChild(getToggleCommentElement());
    }

    private Node getToggleCommentElement() {

        final ToggleCommentPresenter toggleCommentPresenter = presenter.getToggleCommentPresenter();
        final ToggleCommentPresenter.View view = toggleCommentPresenter.getView();

        return view.getElement();
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }
}
