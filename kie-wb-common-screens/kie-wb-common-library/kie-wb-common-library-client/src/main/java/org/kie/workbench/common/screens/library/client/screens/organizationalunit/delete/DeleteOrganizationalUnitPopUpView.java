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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.delete;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Label;
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
public class DeleteOrganizationalUnitPopUpView implements DeleteOrganizationalUnitPopUpPresenter.View,
                                                          IsElement {

    private DeleteOrganizationalUnitPopUpPresenter presenter;

    @Inject
    private TranslationService ts;

    @Inject
    private TranslationUtils translationUtils;

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
    @DataField("description")
    Span description;

    @Inject
    @DataField("confirmed-name-label")
    Label confirmedNameLabel;

    @Inject
    @DataField("confirmed-name")
    Input confirmedName;

    @Override
    public void init(final DeleteOrganizationalUnitPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public String getConfirmedName() {
        return confirmedName.getValue();
    }

    @Override
    public void show(final String name) {
        this.description.setTextContent(ts.format(LibraryConstants.DeleteOrganizationalUnitDescription,
                                                  name,
                                                  translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase()));
        this.confirmedNameLabel.setTextContent(ts.format(LibraryConstants.ConfirmOrganizationalUnitName,
                                                         translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase()));
        errorSetup();
        modal.show();
    }

    @Override
    public void showError(final String errorMessage) {
        this.errorMessage.setTextContent(errorMessage);
        this.error.setHidden(false);
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public String getWrongConfirmedNameValidationMessage() {
        return ts.format(LibraryConstants.WrongOrganizationalUnitConfirmedName,
                         translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase());
    }

    @Override
    public String getDeletingMessage() {
        return ts.format(LibraryConstants.DeletingOrganizationalUnit,
                         translationUtils.getOrganizationalUnitAliasInSingular().toLowerCase());
    }

    @Override
    public String getDeleteSuccessMessage() {
        return ts.format(LibraryConstants.DeleteOrganizationalUnitSuccess,
                         translationUtils.getOrganizationalUnitAliasInSingular());
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.DeleteOrganizationalUnit,
                                     translationUtils.getOrganizationalUnitAliasInSingular()))
                .addBody(body)
                .addFooter(footer())
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.add(cancelButton());
        footer.add(deleteButton());
        return footer;
    }

    private Button deleteButton() {
        return button(ts.format(LibraryConstants.DeleteOrganizationalUnit,
                                translationUtils.getOrganizationalUnitAliasInSingular()),
                      () -> presenter.delete(),
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
