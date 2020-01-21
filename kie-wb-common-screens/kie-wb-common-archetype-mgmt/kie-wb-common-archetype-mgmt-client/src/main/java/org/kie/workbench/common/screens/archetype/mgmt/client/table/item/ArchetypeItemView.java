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

package org.kie.workbench.common.screens.archetype.mgmt.client.table.item;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLTableCellElement;
import org.gwtbootstrap3.client.ui.ModalSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

@Templated("#archetype-item-root")
public class ArchetypeItemView implements ArchetypeItemPresenter.View {

    private static final String LI_DISABLED_CLASS = "li-disabled";
    private ArchetypeItemPresenter presenter;
    private YesNoCancelPopup deleteConfirmationPopup;
    @Inject
    @Named("td")
    @DataField("cell-include")
    private HTMLTableCellElement cellInclude;

    @Inject
    @Named("td")
    @DataField("cell-kebab-actions")
    private HTMLTableCellElement cellKebabActions;

    @Inject
    @Named("td")
    @DataField("cell-status")
    private HTMLTableCellElement cellStatus;

    @Inject
    @DataField("include")
    private HTMLInputElement includeCheckbox;

    @Inject
    @Named("span")
    @DataField("group-id")
    private HTMLElement groupId;

    @Inject
    @Named("span")
    @DataField("artifact-id")
    private HTMLElement artifactId;

    @Inject
    @Named("span")
    @DataField("version")
    private HTMLElement version;

    @Inject
    @Named("span")
    @DataField("created-date")
    private HTMLElement createdDate;

    @Inject
    @DataField("status-valid")
    private HTMLDivElement statusValid;

    @Inject
    @DataField("status-invalid")
    private HTMLDivElement statusInvalid;

    @Inject
    @DataField("actions-delete")
    private HTMLLIElement deleteButton;

    @Inject
    @DataField("actions-validate")
    private HTMLLIElement validateButton;

    @Inject
    @DataField("actions-set-as-default")
    private HTMLLIElement setAsDefaultButton;

    @Inject
    @DataField("default-badge")
    private HTMLDivElement defaultBadge;

    @Inject
    private TranslationService ts;

    @Override
    public void init(final ArchetypeItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setIncluded(final boolean isIncluded) {
        this.includeCheckbox.checked = isIncluded;
    }

    @Override
    public void showInclude(final boolean isVisible) {
        this.cellInclude.hidden = !isVisible;
    }

    @Override
    public void showStatus(final boolean isVisible) {
        this.cellStatus.hidden = !isVisible;
    }

    @Override
    public void setGroupId(final String groupId) {
        this.groupId.textContent = groupId;
    }

    @Override
    public void setArtifactId(final String artifactId) {
        this.artifactId.textContent = artifactId;
    }

    @Override
    public void setVersion(final String version) {
        this.version.textContent = version;
    }

    @Override
    public void setCreatedDate(final String createdDate) {
        this.createdDate.textContent = createdDate;
    }

    @Override
    public void showInvalidStatus(final boolean isVisible) {
        statusInvalid.hidden = !isVisible;
    }

    @Override
    public void setInvalidTooltip(final String message) {
        statusInvalid.title = message;
    }

    @Override
    public void showValidStatus(final boolean isVisible) {
        statusValid.hidden = !isVisible;
    }

    @Override
    public void setValidTooltip(final String message) {
        statusValid.title = message;
    }

    @Override
    public void setDeleteCommand(final Command deleteCommand) {
        createDeleteConfirmationPopup(deleteCommand);
    }

    @Override
    public void showDeleteAction(final boolean isVisible) {
        deleteButton.hidden = !isVisible;
    }

    @Override
    public void showValidateAction(final boolean isVisible) {
        validateButton.hidden = !isVisible;
    }

    @Override
    public void showDefaultBadge(final boolean isVisible) {
        defaultBadge.hidden = !isVisible;
    }

    @Override
    public void setDefaultBadgeTooltip(final String message) {
        defaultBadge.title = message;
    }

    @Override
    public void enableIncludeCheckbox(final boolean isEnabled) {
        includeCheckbox.disabled = !isEnabled;
    }

    @Override
    public void enableSetDefault(final boolean isEnabled) {
        if (isEnabled) {
            setAsDefaultButton.classList.remove(LI_DISABLED_CLASS);
        } else {
            setAsDefaultButton.classList.add(LI_DISABLED_CLASS);
        }
    }

    @Override
    public void checkIncluded(final boolean isChecked) {
        includeCheckbox.checked = isChecked;
    }

    private void createDeleteConfirmationPopup(final Command deleteCommand) {
        final String title = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_DeleteArchetypeTitle);
        final String message = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_DeleteArchetypeMessage);
        final String cancelLabel = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Cancel);
        final String deleteLabel = ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Delete);

        deleteConfirmationPopup = YesNoCancelPopup.newYesNoCancelPopup(title,
                                                                       message,
                                                                       null,
                                                                       null,
                                                                       null,
                                                                       () -> {
                                                                       },
                                                                       cancelLabel,
                                                                       ButtonType.DEFAULT,
                                                                       deleteCommand,
                                                                       deleteLabel,
                                                                       ButtonType.DANGER);
        deleteConfirmationPopup.setSize(ModalSize.MEDIUM);
        deleteConfirmationPopup.clearScrollHeight();
    }

    @EventHandler("actions-delete")
    public void onDeleteButtonClicked(final ClickEvent event) {
        deleteConfirmationPopup.show();
    }

    @EventHandler("actions-validate")
    public void onValidateButtonClicked(final ClickEvent event) {
        presenter.validate();
    }

    @EventHandler("actions-set-as-default")
    public void onSetAsDefaultButtonClicked(final ClickEvent event) {
        presenter.makeDefault();
    }

    @EventHandler("include")
    public void onIncludeChanged(final ChangeEvent event) {
        presenter.setIncluded(includeCheckbox.checked);
    }
}
