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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup;

import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.TranslationUtils;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Templated
public class OrganizationalUnitPopUpView implements OrganizationalUnitPopUpPresenter.View,
                                                    IsElement {

    @Inject
    private TranslationService ts;

    @Inject
    private TranslationUtils translationUtils;

    private OrganizationalUnitPopUpPresenter presenter;

    private BaseModal modal;

    @Inject
    @DataField("body")
    Div body;

    @Inject
    @DataField("error")
    Div error;

    @Inject
    @DataField("error-message")
    Span errorMessage;

    @Inject
    @DataField("name")
    Input name;

    @Inject
    @DataField("default-group-id")
    Input defaultGroupId;

    @Inject
    @DataField("owner")
    Input owner;

    @Override
    public void init(OrganizationalUnitPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public void showAddPopUp() {
        errorSetup();
        modal.show();
    }

    @Override
    public void showEditPopUp(final OrganizationalUnit organizationalUnit) {
        this.name.setValue(organizationalUnit.getName());
        this.name.setDisabled(true);
        this.defaultGroupId.setValue(organizationalUnit.getDefaultGroupId());
        this.owner.setValue(organizationalUnit.getOwner());

        showAddPopUp();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void clear() {
        name.setValue("");
        defaultGroupId.setValue("");
        owner.setValue("");
    }

    @Override
    public void showError(final String errorMessage) {
        this.errorMessage.setTextContent(errorMessage);
        this.error.setHidden(false);
    }

    @Override
    public String getName() {
        return this.name.getValue();
    }

    @Override
    public String getDefaultGroupId() {
        return this.defaultGroupId.getValue();
    }

    @Override
    public String getOwner() {
        return this.owner.getValue();
    }

    @Override
    public String getEmptyNameValidationMessage() {
        final String name = ts.format(LibraryConstants.Name);
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         name);
    }

    @Override
    public String getEmptyDefaultGroupIdValidationMessage() {
        final String name = ts.format(LibraryConstants.DefaultGroupId);
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         name);
    }

    @Override
    public String getInvalidDefaultGroupIdValidationMessage() {
        final String name = ts.format(LibraryConstants.DefaultGroupId);
        return ts.format(LibraryConstants.InvalidFieldValidation,
                         name);
    }

    @Override
    public String getDuplicatedOrganizationalUnitValidationMessage() {
        return ts.format(LibraryConstants.DuplicatedOrganizationalUnitValidation,
                         translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase());
    }

    @Override
    public String getSavingMessage() {
        return ts.format(LibraryConstants.Saving);
    }

    @Override
    public String getSaveSuccessMessage() {
        return ts.format(LibraryConstants.OrganizationalUnitSaveSuccess,
                         translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase());
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(translationUtils.getOrganizationalUnitAliasInSingular())
                .addBody(body)
                .addFooter(footer())
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.add(saveButton());
        footer.add(cancelButton());
        return footer;
    }

    private Button saveButton() {
        return button(ts.format(LibraryConstants.Save),
                      () -> presenter.save(),
                      ButtonType.PRIMARY);
    }

    private Button cancelButton() {
        return button(ts.format(LibraryConstants.Cancel),
                      () -> presenter.cancel(),
                      ButtonType.DEFAULT);
    }

    private void errorSetup() {
        this.error.setHidden(true);
    }

    private Button button(final String text,
                          final Command command,
                          final ButtonType type) {
        Button button = new Button(text,
                                   event -> command.execute());
        button.setType(type);
        return button;
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
