/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens.project.delete;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.common.PopUpUtils;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Templated
public class DeleteProjectPopUpView implements DeleteProjectPopUpScreen.View,
                                               IsElement {

    private DeleteProjectPopUpScreen presenter;

    @Inject
    private TranslationService ts;

    private BaseModal modal;

    @Inject
    @DataField("body")
    HTMLDivElement body;

    @Inject
    @DataField("error")
    HTMLDivElement error;

    @Inject
    @DataField("error-message")
    @Named("span")
    HTMLElement errorMessage;

    @Inject
    @DataField("description")
    @Named("span")
    HTMLElement description;

    @Inject
    @DataField("confirmed-name-label")
    HTMLLabelElement confirmedNameLabel;

    @Inject
    @DataField("confirmed-name")
    HTMLInputElement confirmedName;

    @Override
    public void init(final DeleteProjectPopUpScreen presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public String getConfirmedName() {
        return confirmedName.value;
    }

    @Override
    public void show(final String name) {
        this.description.textContent = ts.format(LibraryConstants.DeleteProjectDescription,
                                                 name);
        this.confirmedNameLabel.textContent = ts.format(LibraryConstants.ConfirmProjectName);
        errorSetup();
        modal.show();
    }

    @Override
    public void showError(final String errorMessage) {
        this.errorMessage.textContent = errorMessage;
        this.error.hidden = false;
    }

    @Override
    @PreDestroy
    public void hide() {
        modal.hide();
    }

    @Override
    public String getWrongConfirmedNameValidationMessage() {
        return ts.format(LibraryConstants.WrongProjectConfirmedName);
    }

    @Override
    public String getDeletingMessage() {
        return ts.format(LibraryConstants.DeletingProject,
                         this.getConfirmedName());
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.DeleteProject))
                .addBody(body)
                .addFooter(PopUpUtils.footer(cancelButton(),
                                             deleteButton()))
                .build();
    }

    private Button deleteButton() {
        return PopUpUtils.button(ts.format(LibraryConstants.DeleteProject),
                                 () -> presenter.delete(),
                                 ButtonType.PRIMARY);
    }

    private Button cancelButton() {
        return PopUpUtils.button(ts.format(LibraryConstants.Cancel),
                                 () -> presenter.cancel(),
                                 ButtonType.DEFAULT);
    }

    private void errorSetup() {
        this.error.hidden = true;
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
