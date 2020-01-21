/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.archetype.mgmt.client.modal;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.uberfire.client.views.pfly.widgets.ValidationState;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Templated
public class AddArchetypeModalView implements AddArchetypeModalPresenter.View,
                                              IsElement {

    private static final String EMPTY = "";

    private AddArchetypeModalPresenter presenter;

    private BaseModal modal;

    private Button addButton;

    @Inject
    private TranslationService ts;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    @DataField("content")
    private HTMLDivElement content;

    @Inject
    @DataField("error")
    private HTMLDivElement error;

    @Inject
    @Named("span")
    @DataField("error-message")
    private HTMLElement errorMessage;

    @Inject
    @DataField("archetype-group-id-group")
    private HTMLDivElement archetypeGroupIdGroup;

    @Inject
    @DataField("archetype-group-id-help-block")
    private HelpBlock archetypeGroupIdHelpBlock;

    @Inject
    @DataField("archetype-group-id")
    private HTMLInputElement archetypeGroupId;

    @Inject
    @DataField("archetype-artifact-id-group")
    private HTMLDivElement archetypeArtifactIdGroup;

    @Inject
    @DataField("archetype-artifact-id-help-block")
    private HelpBlock archetypeArtifactIdHelpBlock;

    @Inject
    @DataField("archetype-artifact-id")
    private HTMLInputElement archetypeArtifactId;

    @Inject
    @DataField("archetype-version-group")
    private HTMLDivElement archetypeVersionGroup;

    @Inject
    @DataField("archetype-version-help-block")
    private HelpBlock archetypeVersionHelpBlock;

    @Inject
    @DataField("archetype-version")
    private HTMLInputElement archetypeVersion;

    @Override
    public void init(final AddArchetypeModalPresenter presenter) {
        this.presenter = presenter;
        setupModal();
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void showGeneralError(final String errorMessage) {
        this.errorMessage.innerHTML = errorMessage;
        this.error.hidden = false;
    }

    @Override
    public void showArchetypeGroupIdError(String errorMessage) {
        showFieldError(archetypeGroupIdGroup,
                       archetypeGroupIdHelpBlock,
                       errorMessage);
    }

    @Override
    public void showArchetypeArtifactIdError(final String errorMessage) {
        showFieldError(archetypeArtifactIdGroup,
                       archetypeArtifactIdHelpBlock,
                       errorMessage);
    }

    @Override
    public void showArchetypeVersionError(String errorMessage) {
        showFieldError(archetypeVersionGroup,
                       archetypeVersionHelpBlock,
                       errorMessage);
    }

    private void showFieldError(final HTMLDivElement group,
                                final HelpBlock helpBlock,
                                final String errorMessage) {
        group.classList.add(ValidationState.ERROR.getCssName());
        helpBlock.setText(errorMessage);
    }

    @Override
    public void clearErrors() {
        clearFieldError(archetypeGroupIdGroup, archetypeGroupIdHelpBlock);
        clearFieldError(archetypeArtifactIdGroup, archetypeArtifactIdHelpBlock);
        clearFieldError(archetypeVersionGroup, archetypeVersionHelpBlock);

        error.hidden = true;
    }

    private void clearFieldError(final HTMLDivElement group,
                                 final HelpBlock helpBlock) {
        group.classList.remove(ValidationState.ERROR.getCssName());
        helpBlock.clearError();
    }

    @Override
    public String getArchetypeGroupId() {
        return archetypeGroupId.value;
    }

    @Override
    public String getArchetypeArtifactId() {
        return archetypeArtifactId.value;
    }

    @Override
    public String getArchetypeVersion() {
        return archetypeVersion.value;
    }

    @Override
    public void resetAll() {
        archetypeGroupId.value = EMPTY;
        archetypeArtifactId.value = EMPTY;
        archetypeVersion.value = EMPTY;

        clearErrors();
    }

    @Override
    public void enableAddButton(boolean isEnabled) {
        if (addButton != null) {
            addButton.setEnabled(isEnabled);
        }
    }

    @Override
    public void enableFields(final boolean isEnabled) {
        archetypeGroupId.disabled = !isEnabled;
        archetypeArtifactId.disabled = !isEnabled;
        archetypeVersion.disabled = !isEnabled;
    }

    private void setupModal() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(ArchetypeManagementConstants.ArchetypeManagement_AddArchetype))
                .addBody(content)
                .addFooter(createFooter())
                .build();
    }

    private ModalFooter createFooter() {
        final GenericModalFooter footer = new GenericModalFooter();
        footer.add(createCancelButton());
        footer.add(createAddButton());
        return footer;
    }

    private Button createAddButton() {
        addButton = createButton(ts.format(ArchetypeManagementConstants.ArchetypeManagement_Add),
                                 () -> presenter.add(),
                                 ButtonType.PRIMARY);
        return addButton;
    }

    private Button createCancelButton() {
        return createButton(ts.format(ArchetypeManagementConstants.ArchetypeManagement_Cancel),
                            () -> presenter.cancel(),
                            ButtonType.DEFAULT);
    }

    private Button createButton(final String text,
                                final Command command,
                                final ButtonType type) {
        final Button button = new Button(text,
                                         event -> command.execute());
        button.setType(type);
        return button;
    }
}
