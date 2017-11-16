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

package org.kie.workbench.common.screens.library.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportRepositoryPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit.EditContributorsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.delete.DeleteOrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.lifecycle.OnClose;

@WorkbenchScreen(identifier = LibraryPlaces.LIBRARY_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class LibraryScreen {

    public interface View extends UberElement<LibraryScreen> {

        void setTitle(String title);

        void setProjectsCount(int count);

        void setContributorsCount(int count);

        void updateContent(HTMLElement content);
    }

    private View view;

    private ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenters;

    private ManagedInstance<DeleteOrganizationalUnitPopUpPresenter> deleteOrganizationalUnitPopUpPresenters;

    private ManagedInstance<ImportRepositoryPopUpPresenter> importRepositoryPopUpPresenters;

    private ProjectContext projectContext;

    private OrganizationalUnitController organizationalUnitController;

    private ProjectController projectController;

    private EmptyLibraryScreen emptyLibraryScreen;

    private PopulatedLibraryScreen populatedLibraryScreen;

    private OrgUnitsMetricsScreen orgUnitsMetricsScreen;

    private ContributorsListPresenter contributorsListPresenter;

    private Caller<LibraryService> libraryService;

    private LibraryPlaces libraryPlaces;

    @Inject
    public LibraryScreen(final View view,
                         final ManagedInstance<DeleteOrganizationalUnitPopUpPresenter> deleteOrganizationalUnitPopUpPresenters,
                         final ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenters,
                         final ManagedInstance<ImportRepositoryPopUpPresenter> importRepositoryPopUpPresenters,
                         final ProjectContext projectContext,
                         final OrganizationalUnitController organizationalUnitController,
                         final ProjectController projectController,
                         final EmptyLibraryScreen emptyLibraryScreen,
                         final PopulatedLibraryScreen populatedLibraryScreen,
                         final OrgUnitsMetricsScreen orgUnitsMetricsScreen,
                         final ContributorsListPresenter contributorsListPresenter,
                         final Caller<LibraryService> libraryService,
                         final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.deleteOrganizationalUnitPopUpPresenters = deleteOrganizationalUnitPopUpPresenters;
        this.editContributorsPopUpPresenters = editContributorsPopUpPresenters;
        this.importRepositoryPopUpPresenters = importRepositoryPopUpPresenters;
        this.projectContext = projectContext;
        this.organizationalUnitController = organizationalUnitController;
        this.projectController = projectController;
        this.emptyLibraryScreen = emptyLibraryScreen;
        this.populatedLibraryScreen = populatedLibraryScreen;
        this.orgUnitsMetricsScreen = orgUnitsMetricsScreen;
        this.contributorsListPresenter = contributorsListPresenter;
        this.libraryService = libraryService;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setTitle(libraryPlaces.getSelectedOrganizationalUnit().getName());
        showProjects();
        view.setContributorsCount(contributorsListPresenter.getContributorsCount());
    }

    public void trySamples() {
        if (userCanCreateProjects()) {
            libraryPlaces.goToTrySamples();
        }
    }

    public void importProject() {
        if (userCanCreateProjects()) {
            final ImportRepositoryPopUpPresenter importRepositoryPopUpPresenter = importRepositoryPopUpPresenters.get();
            importRepositoryPopUpPresenter.show();
        }
    }

    public void editContributors() {
        if (userCanUpdateOrganizationalUnit()) {
            final EditContributorsPopUpPresenter editContributorsPopUpPresenter = editContributorsPopUpPresenters.get();
            editContributorsPopUpPresenter.show(projectContext.getActiveOrganizationalUnit());
        }
    }

    public void delete() {
        if (userCanDeleteOrganizationalUnit()) {
            final DeleteOrganizationalUnitPopUpPresenter deleteOrganizationalUnitPopUpPresenter = deleteOrganizationalUnitPopUpPresenters.get();
            deleteOrganizationalUnitPopUpPresenter.show(projectContext.getActiveOrganizationalUnit());
        }
    }

    public void showProjects() {
        libraryService.call((Boolean hasProjects) -> {
            if (hasProjects) {
                view.updateContent(populatedLibraryScreen.getView().getElement());
                view.setProjectsCount(populatedLibraryScreen.getProjectsCount());
            } else {
                view.updateContent(emptyLibraryScreen.getView().getElement());
                view.setProjectsCount(0);
            }
        }).hasProjects(libraryPlaces.getSelectedRepository(),
                       libraryPlaces.getSelectedBranch());
    }

    public void showContributors() {
        view.updateContent(contributorsListPresenter.getView().getElement());
    }

    public void showMetrics() {
        view.updateContent(orgUnitsMetricsScreen.getView().getElement());
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects();
    }

    public boolean userCanUpdateOrganizationalUnit() {
        return organizationalUnitController.canUpdateOrgUnit(projectContext.getActiveOrganizationalUnit());
    }

    public boolean userCanDeleteOrganizationalUnit() {
        return organizationalUnitController.canDeleteOrgUnit(projectContext.getActiveOrganizationalUnit());
    }

    public void organizationalUnitEdited(@Observes final AfterEditOrganizationalUnitEvent afterEditOrganizationalUnitEvent) {
        view.setContributorsCount(afterEditOrganizationalUnitEvent.getEditedOrganizationalUnit().getContributors().size());
    }

    @OnClose
    public void onClose() {
        orgUnitsMetricsScreen.onClose();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Library Screen";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }
}
