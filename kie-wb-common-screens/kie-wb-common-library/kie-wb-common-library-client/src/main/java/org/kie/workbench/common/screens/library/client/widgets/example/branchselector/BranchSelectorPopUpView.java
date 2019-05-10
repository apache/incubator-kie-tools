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

package org.kie.workbench.common.screens.library.client.widgets.example.branchselector;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Templated
public class BranchSelectorPopUpView implements BranchSelectorPopUpPresenter.View,
                                                IsElement {

    private BranchSelectorPopUpPresenter presenter;

    private BaseModal modal;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("body")
    HTMLDivElement body;

    @Inject
    @DataField("error")
    HTMLDivElement error;

    @Inject
    @Named("span")
    @DataField("error-message")
    HTMLElement errorMessage;

    @Inject
    @Named("span")
    @DataField("branch-selector-container")
    HTMLElement branchSelectorContainer;

    @Override
    public void init(final BranchSelectorPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public HTMLElement getBranchSelectorContainer() {
        return branchSelectorContainer;
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.BranchesToBeImported))
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
        return button(ts.format(LibraryConstants.Ok),
                      () -> presenter.defineBranches(),
                      ButtonType.PRIMARY);
    }

    private Button cancelButton() {
        return button(ts.format(LibraryConstants.Cancel),
                      () -> presenter.cancel(),
                      ButtonType.DEFAULT);
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
    public void showError(final String errorMessage) {
        this.errorMessage.textContent = errorMessage;
        this.error.hidden = false;
    }

    private void errorSetup() {
        this.error.hidden = true;
    }

    @Override
    public String getMasterIsRequiredMessage() {
        return ts.format(LibraryConstants.MasterIsRequired);
    }

    @Override
    public String getAtLeastMasterIsRequiredMessage() {
        return ts.format(LibraryConstants.AtLeastMasterIsRequired);
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
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
