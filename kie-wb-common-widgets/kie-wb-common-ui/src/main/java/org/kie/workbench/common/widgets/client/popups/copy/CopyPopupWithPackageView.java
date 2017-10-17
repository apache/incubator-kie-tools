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

package org.kie.workbench.common.widgets.client.popups.copy;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.utils.ProjectResourcePaths;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.commons.ToggleCommentPresenter;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class CopyPopupWithPackageView implements CopyPopUpPresenter.View,
                                                 IsElement {

    @Inject
    @DataField("body")
    Div body;

    @Inject
    @DataField("newNameTextBox")
    TextBox newNameTextBox;

    @Inject
    @DataField("newNameLabel")
    Label newNameLabel;

    @Inject
    @DataField("error")
    Div error;

    @Inject
    @DataField("errorMessage")
    Span errorMessage;

    @Inject
    @DataField("packageListBox")
    PackageListBox packageListBox;

    @Inject
    @DataField("packageHelpInline")
    HelpBlock packageHelpInline;

    @Inject
    TranslationService translationService;

    @Inject
    ProjectContext context;

    CopyPopUpPresenter presenter;

    BaseModal modal;

    Button copyButton;

    @Override
    public void init(CopyPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
        setupComment();
    }

    @Override
    public void show() {
        errorSetup();
        packageListBoxSetup();
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
        if (thereIsAnActiveProject()) {
            final String path = presenter.getPath().toURI();
            final Package selectedPackage = packageListBox.getSelectedPackage();

            if (path.contains(ProjectResourcePaths.MAIN_RESOURCES_PATH)) {
                return selectedPackage.getPackageMainResourcesPath();
            } else if (path.contains(ProjectResourcePaths.MAIN_SRC_PATH)) {
                return selectedPackage.getPackageMainSrcPath();
            } else if (path.contains(ProjectResourcePaths.TEST_RESOURCES_PATH)) {
                return selectedPackage.getPackageTestResourcesPath();
            } else if (path.contains(ProjectResourcePaths.TEST_SRC_PATH)) {
                return selectedPackage.getPackageTestSrcPath();
            }
        }

        return null;
    }

    @Override
    public String getPackageName() {
        if (packageListBox.getSelectedPackage() != null) {
            return packageListBox.getSelectedPackage().getPackageName();
        }

        return null;
    }

    @Override
    public void handleCopyNotAllowed() {
        showError(translate(Constants.CopyPopUpView_CopyNotAllowed));
    }

    private void modalSetup() {
        this.newNameLabel.setText(translate(Constants.CopyPopUpView_NewName));
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
        footer.add(copyButton());
        return footer;
    }

    Button copyButton() {
        if (copyButton == null) {
            copyButton = button(translate(Constants.CopyPopUpView_MakeACopy),
                                copyCommand(),
                                ButtonType.PRIMARY);
        }

        return copyButton;
    }

    Button button(final String text,
                  final Command command,
                  final ButtonType type) {
        Button button = new Button(text,
                                   event -> command.execute());
        button.setType(type);
        return button;
    }

    private String translate(final String key,
                             Object... args) {
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

    void packageListBoxSetup() {
        final String path = presenter.getPath().toURI();

        if (thereIsAnActiveProject() && isAProjectResource(path)) {
            copyButton().setEnabled(false);
            packageListBox.setContext(context,
                                      true,
                                      () -> copyButton.setEnabled(true));
        }
    }

    private void setupComment() {
        body.appendChild(toggleCommentPresenter().getViewElement());
    }

    private ToggleCommentPresenter toggleCommentPresenter() {
        return presenter.getToggleCommentPresenter();
    }

    boolean isAProjectResource(final String path) {
        return path.contains(ProjectResourcePaths.MAIN_RESOURCES_PATH)
                || path.contains(ProjectResourcePaths.MAIN_SRC_PATH)
                || path.contains(ProjectResourcePaths.TEST_RESOURCES_PATH)
                || path.contains(ProjectResourcePaths.TEST_SRC_PATH);
    }

    boolean thereIsAnActiveProject() {
        return context.getActiveProject() != null;
    }
}
