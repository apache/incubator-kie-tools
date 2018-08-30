/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.branch;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
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
public class AddBranchPopUpView implements AddBranchPopUpPresenter.View,
                                           IsElement {

    private AddBranchPopUpPresenter presenter;

    @Inject
    private TranslationService ts;

    @Inject
    private ManagedInstance<BranchOptionView> options;

    private BaseModal modal;

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
    @DataField("name")
    HTMLInputElement name;

    @Inject
    @DataField("branch-from")
    HTMLSelectElement branchFrom;

    private Button addButton;

    @Override
    public void init(final AddBranchPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public void setBranches(final List<String> branches) {
        branchFrom.innerHTML = "";
        branches.forEach(branch -> {
            final BranchOptionView option = options.get();
            option.setup(branch);
            branchFrom.appendChild(option.getElement());
        });
    }

    @Override
    public String getName() {
        return name.value;
    }

    @Override
    public String getBranchFrom() {
        return branchFrom.value;
    }

    @Override
    public void setBranchFrom(final String branchFrom) {
        this.branchFrom.value = branchFrom;
    }

    @Override
    public void show() {
        errorSetup();
        modal.show();
    }

    @Override
    public void showError(final String errorMessage) {
        this.errorMessage.innerHTML = errorMessage;
        this.error.hidden = false;
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public String getSavingMessage() {
        return ts.format(LibraryConstants.Saving);
    }

    @Override
    public String getAddBranchSuccessMessage() {
        return ts.format(LibraryConstants.AddBranchSuccess);
    }

    @Override
    public String getDuplicatedBranchMessage() {
        return ts.format(LibraryConstants.DuplicatedBranchValidation);
    }

    @Override
    public String getEmptyNameMessage() {
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         ts.getTranslation(LibraryConstants.Name));
    }

    @Override
    public String getInvalidNameMessage() {
        return ts.format(LibraryConstants.InvalidBranchName);
    }

    @Override
    public void setAddButtonEnabled(final boolean enabled) {
        if (addButton != null) {
            addButton.setEnabled(enabled);
        }
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.AddBranch))
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
        addButton = button(ts.format(LibraryConstants.Add),
                                     () -> presenter.add(),
                                     ButtonType.PRIMARY);
        return addButton;
    }

    private Button cancelButton() {
        return button(ts.format(LibraryConstants.Cancel),
                      () -> presenter.cancel(),
                      ButtonType.DEFAULT);
    }

    private void errorSetup() {
        this.error.hidden = true;
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

    @Templated("AddBranchPopUpView.html#branch")
    public static class BranchOptionView implements IsElement {

        @Inject
        @DataField("branch")
        HTMLOptionElement option;

        public void setup(final String branchName) {
            option.value = branchName;
            option.innerHTML = branchName;
        }
    }
}
