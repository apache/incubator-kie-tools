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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.client.views.pfly.widgets.InputAutocomplete;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

import static org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.$;

@Templated
public class ContributorsListItemView implements ContributorsListItemPresenter.View,
                                                 IsElement {
    @Inject
    private TranslationService ts;

    private ContributorsListItemPresenter presenter;

    @Inject
    @DataField("container")
    HTMLDivElement container;

    @Inject
    @Named("span")
    @DataField("name")
    HTMLElement name;

    @Inject
    @Named("span")
    @DataField("role")
    HTMLElement role;

    @Inject
    @DataField("name-input-container")
    HTMLDivElement nameInputContainer;

    @Inject
    @DataField("role-select")
    HTMLSelectElement roleSelect;

    @Inject
    @DataField("edit")
    HTMLAnchorElement edit;

    @Inject
    @DataField("remove")
    HTMLAnchorElement remove;

    @Inject
    @DataField("ok")
    HTMLButtonElement ok;

    @Inject
    @DataField("cancel")
    HTMLButtonElement cancel;

    @Inject
    InputAutocomplete nameInput;

    @Override
    public void init(final ContributorsListItemPresenter presenter) {
        this.presenter = presenter;
        setupUsersAutocomplete();
    }

    private void setupUsersAutocomplete() {
        nameInput.setup(presenter::getUserNames);
        nameInputContainer.appendChild(nameInput.getElement());
    }

    public void setupAddMode() {
        editMode();
    }

    public void setupViewMode(final Contributor contributor) {
        this.name.innerHTML = contributor.getUsername();
        this.role.innerHTML = getRoleLabel(contributor.getType());
        viewMode();
    }

    @Override
    public void removeContributor() {
        // Use of removeChild() instead of remove() for IE compatibility purposes
        for (int i = container.childNodes.getLength() - 1; i > 0; i--) {
            container.removeChild(container.childNodes.item(i));
        }
    }

    @Override
    public String getName() {
        return nameInput.getValue();
    }

    @Override
    public ContributorType getRole() {
        return ContributorType.valueOf(roleSelect.value);
    }

    @Override
    public String getSavingMessage() {
        return ts.format(LibraryConstants.Saving);
    }

    @Override
    public String getSaveSuccessMessage() {
        return ts.format(LibraryConstants.EditOrganizationalUnitContributorsSaveSuccess);
    }

    @Override
    public String getEmptyNameMessage() {
        return ts.format(LibraryConstants.EmptyName);
    }

    @Override
    public String getInvalidRoleMessage() {
        return ts.format(LibraryConstants.EmptyFieldValidation, LibraryConstants.Role);
    }

    @Override
    public String getRemoveSuccessMessage() {
        return ts.format(LibraryConstants.EditOrganizationalUnitContributorsRemoveSuccess);
    }

    @Override
    public String getSpaceOwnerChangedMessage() {
        return ts.format(LibraryConstants.SpaceOwnerChanged);
    }

    @Override
    public String getSingleOwnerIsMandatoryMessage() {
        return ts.format(LibraryConstants.SingleOwnerIsMandatory);
    }

    @Override
    public String getDuplicatedContributorMessage() {
        return ts.format(LibraryConstants.DuplicatedContributor);
    }

    @Override
    public String getContributorTypeNotAllowedMessage() {
        return ts.format(LibraryConstants.ContributorTypeNotAllowed);
    }

    @Override
    public String getTranslation(final String key) {
        return ts.format(key);
    }

    @Override
    public void showActions() {
        presenter.canEditContributors().then(canEditContributors -> {
            edit.hidden = !canEditContributors;
            return presenter.promises.resolve();
        });
        presenter.canRemoveContributor().then(canRemoveContributor -> {
            remove.hidden = !canRemoveContributor;
            return presenter.promises.resolve();
        });
    }

    @Override
    public void hideActions() {
        edit.hidden = true;
        remove.hidden = true;
    }

    @EventHandler("edit")
    public void edit(final ClickEvent clickEvent) {
        presenter.edit();
        nameInput.setValue(presenter.getContributor().getUsername());
        roleSelect.value = presenter.getContributor().getType().name();
    }

    @EventHandler("remove")
    public void remove(final ClickEvent clickEvent) {
        presenter.remove();
    }

    @EventHandler("ok")
    public void ok(final ClickEvent clickEvent) {
        presenter.save();
    }

    @EventHandler("cancel")
    public void cancel(final ClickEvent clickEvent) {
        presenter.cancel();
    }

    public void viewMode() {
        name.hidden = false;
        role.hidden = false;
        nameInput.getElement().hidden = true;
        roleSelect.hidden = true;

        showActions();
        ok.hidden = true;
        cancel.hidden = true;
    }

    public void editMode() {
        name.hidden = true;
        role.hidden = true;
        nameInput.getElement().hidden = false;
        roleSelect.hidden = false;

        hideActions();
        ok.hidden = false;
        cancel.hidden = false;
    }

    public String getRoleLabel(final ContributorType role) {
        if (ContributorType.OWNER.equals(role)) {
            return ts.format(LibraryConstants.ContributorTypeOwner);
        } else if (ContributorType.ADMIN.equals(role)) {
            return ts.format(LibraryConstants.ContributorTypeAdmin);
        } else if (ContributorType.CONTRIBUTOR.equals(role)) {
            return ts.format(LibraryConstants.ContributorTypeContributor);
        }

        return role.name();
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}