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

package org.kie.workbench.common.screens.library.client.screens.project;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextArea;
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
public class AddProjectPopUpView implements AddProjectPopUpPresenter.View,
                                            IsElement {

    private AddProjectPopUpPresenter presenter;

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
    @DataField("show-hide-advanced-options")
    Anchor showHideAdvancedOptions;

    @Inject
    @DataField("advanced-options")
    Div advancedOptions;

    @Inject
    @DataField("name")
    Input name;

    @Inject
    @DataField("description")
    TextArea description;

    @Inject
    @DataField("group-id")
    Input groupId;

    @Inject
    @DataField("artifact-id")
    Input artifactId;

    @Inject
    @DataField("version")
    Input version;

    @Override
    public void init(final AddProjectPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public String getName() {
        return name.getValue();
    }

    @Override
    public String getDescription() {
        return description.getValue();
    }

    @Override
    public String getGroupId() {
        return groupId.getValue();
    }

    @Override
    public String getArtifactId() {
        return artifactId.getValue();
    }

    @Override
    public String getVersion() {
        return version.getValue();
    }

    @Override
    public void setDescription(String description) {
        this.description.setValue(description);
    }

    @Override
    public boolean isAdvancedOptionsSelected() {
        return !advancedOptions.getHidden();
    }

    @Override
    public void show() {
        errorSetup();
        advancedOptions.setHidden(true);
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
    public String getSavingMessage() {
        return ts.format(LibraryConstants.Saving);
    }

    @Override
    public String getAddProjectSuccessMessage() {
        return ts.format(LibraryConstants.AddProjectSuccess);
    }

    @Override
    public String getDuplicatedProjectMessage() {
        return ts.format(LibraryConstants.DuplicatedProjectValidation);
    }

    @Override
    public String getEmptyNameMessage() {
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         ts.getTranslation(LibraryConstants.Name));
    }

    @Override
    public String getInvalidNameMessage() {
        return ts.format(LibraryConstants.InvalidFieldValidation,
                         ts.getTranslation(LibraryConstants.Name));
    }

    @Override
    public String getEmptyGroupIdMessage() {
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         ts.getTranslation(LibraryConstants.GroupId));
    }

    @Override
    public String getInvalidGroupIdMessage() {
        return ts.format(LibraryConstants.InvalidFieldValidation,
                         ts.getTranslation(LibraryConstants.GroupId));
    }

    @Override
    public String getEmptyArtifactIdMessage() {
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         ts.getTranslation(LibraryConstants.ArtifactId));
    }

    @Override
    public String getInvalidArtifactIdMessage() {
        return ts.format(LibraryConstants.InvalidFieldValidation,
                         ts.getTranslation(LibraryConstants.ArtifactId));
    }

    @Override
    public String getEmptyVersionMessage() {
        return ts.format(LibraryConstants.EmptyFieldValidation,
                         ts.getTranslation(LibraryConstants.Version));
    }

    @Override
    public String getInvalidVersionMessage() {
        return ts.format(LibraryConstants.InvalidFieldValidation,
                         ts.getTranslation(LibraryConstants.Version));
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.AddProject))
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
        return button(ts.format(LibraryConstants.Add),
                      () -> presenter.add(),
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

    @EventHandler("show-hide-advanced-options")
    public void showAdvancedOptions(final ClickEvent clickEvent) {
        if (advancedOptions.getHidden()) {
            advancedOptions.setHidden(false);
            showHideAdvancedOptions.setTextContent(ts.format(LibraryConstants.HideAdvancedOptions));
        } else {
            advancedOptions.setHidden(true);
            showHideAdvancedOptions.setTextContent(ts.format(LibraryConstants.ShowAdvancedOptions));
            groupId.setValue("");
            artifactId.setValue("");
            version.setValue("");
        }
    }
}
