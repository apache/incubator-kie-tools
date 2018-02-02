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

package org.kie.workbench.common.screens.library.client.screens.importrepository;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Templated
public class ImportRepositoryPopUpView implements ImportRepositoryPopUpPresenter.View,
                                                  IsElement {

    private ImportRepositoryPopUpPresenter presenter;

    @Inject
    private TranslationService ts;

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
    @DataField("repository-url")
    Input repositoryURL;

    @Inject
    @DataField("show-hide-authentication-options")
    Anchor showHideAuthenticationOptions;

    @Inject
    @DataField("authentication-options")
    Div authenticationOptions;

    @Inject
    @DataField("user-name")
    Input userName;

    @Inject
    @DataField("password")
    Input password;

    @Override
    public void init(final ImportRepositoryPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public String getRepositoryURL() {
        return repositoryURL.getValue();
    }

    @Override
    public String getUserName() {
        final String userName = this.userName.getValue();
        return userName == null || userName.isEmpty() ? null : userName;
    }

    @Override
    public String getPassword() {
        final String password = this.password.getValue();
        return password == null || password.isEmpty() ? null : password;
    }

    @Override
    public void show() {
        errorSetup();
        authenticationOptions.setHidden(true);
        modal.show();
    }

    @Override
    public void showError(final String errorMessage) {
        this.errorMessage.setTextContent(errorMessage);
        this.error.setHidden(false);
    }

    @Override
    public String getLoadingMessage() {
        return ts.format(LibraryConstants.Loading);
    }

    @Override
    public String getNoProjectsToImportMessage() {
        return ts.format(LibraryConstants.NoProjectsToImport);
    }

    @Override
    public String getEmptyRepositoryURLValidationMessage() {
        final String repositoryURL = ts.format(LibraryConstants.RepositoryURL);
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         repositoryURL);
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @EventHandler("show-hide-authentication-options")
    public void showAuthenticationOptions(final ClickEvent clickEvent) {
        if (authenticationOptions.getHidden()) {
            authenticationOptions.setHidden(false);
            showHideAuthenticationOptions.setTextContent(ts.format(LibraryConstants.HideAuthenticationOptions));
        } else {
            authenticationOptions.setHidden(true);
            showHideAuthenticationOptions.setTextContent(ts.format(LibraryConstants.ShowAuthenticationOptions));
            userName.setValue("");
            password.setValue("");
        }
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.ImportProject))
                .addBody(body)
                .addFooter(footer())
                .build();
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.add(cancelButton());
        footer.add(addButton());
        return footer;
    }

    private Button addButton() {
        return button(ts.format(LibraryConstants.Import),
                      () -> presenter.importRepository(),
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
