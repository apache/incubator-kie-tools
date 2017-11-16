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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit.EditContributorsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElement;

public class ContributorsListPresenter {

    public interface View extends UberElement<ContributorsListPresenter> {

        void clearContributors();

        void addContributor(HTMLElement contributor);

        void clearFilterText();

        String getOwnerRoleLabel();

        String getContributorRoleLabel();
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private ManagedInstance<ContributorsListItemPresenter> contributorsListItemPresenters;

    private ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenters;

    private ProjectContext projectContext;

    private OrganizationalUnitController organizationalUnitController;

    List<String> contributors;

    @Inject
    public ContributorsListPresenter(final View view,
                                     final LibraryPlaces libraryPlaces,
                                     final ManagedInstance<ContributorsListItemPresenter> contributorsListItemPresenters,
                                     final ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenters,
                                     final ProjectContext projectContext,
                                     final OrganizationalUnitController organizationalUnitController) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.contributorsListItemPresenters = contributorsListItemPresenters;
        this.editContributorsPopUpPresenters = editContributorsPopUpPresenters;
        this.projectContext = projectContext;
        this.organizationalUnitController = organizationalUnitController;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        updateContributors(libraryPlaces.getSelectedOrganizationalUnit());
    }

    public void updateContributors(final OrganizationalUnit organizationalUnit) {
        contributors = new ArrayList<>(organizationalUnit.getContributors());
        contributors.sort((c1, c2) -> c1.toUpperCase().compareTo(c2.toUpperCase()));
        updateView(contributors);
    }

    public void filterContributors(final String filter) {
        List<String> filteredContributors = contributors.stream()
                .filter(c -> c.toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());

        updateView(filteredContributors);
    }

    private void updateView(final List<String> contributors) {
        final OrganizationalUnit organizationalUnit = libraryPlaces.getSelectedOrganizationalUnit();

        view.clearContributors();

        contributors.stream()
                .forEach(contributor -> {
                    final String role = contributor.equals(organizationalUnit.getOwner()) ? view.getOwnerRoleLabel() : view.getContributorRoleLabel();
                    final ContributorsListItemPresenter contributorsListItemPresenter = contributorsListItemPresenters.get();
                    contributorsListItemPresenter.setup(contributor,
                                                        role);
                    view.addContributor(contributorsListItemPresenter.getView().getElement());
                });
    }

    public void edit() {
        if (userCanUpdateOrganizationalUnit()) {
            final EditContributorsPopUpPresenter editContributorsPopUpPresenter = editContributorsPopUpPresenters.get();
            editContributorsPopUpPresenter.show(projectContext.getActiveOrganizationalUnit());
        }
    }

    public void organizationalUnitEdited(@Observes final AfterEditOrganizationalUnitEvent afterEditOrganizationalUnitEvent) {
        view.clearFilterText();
        updateContributors(afterEditOrganizationalUnitEvent.getEditedOrganizationalUnit());
    }

    public boolean userCanUpdateOrganizationalUnit() {
        return organizationalUnitController.canUpdateOrgUnit(projectContext.getActiveOrganizationalUnit());
    }

    public int getContributorsCount() {
        return libraryPlaces.getSelectedOrganizationalUnit().getContributors().size();
    }

    public View getView() {
        return view;
    }
}
