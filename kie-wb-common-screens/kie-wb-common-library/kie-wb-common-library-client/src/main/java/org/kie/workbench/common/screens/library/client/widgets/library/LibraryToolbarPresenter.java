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

package org.kie.workbench.common.screens.library.client.widgets.library;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class LibraryToolbarPresenter {

    public interface View extends UberElement<LibraryToolbarPresenter> {

        void clearRepositories();

        void addRepository(String alias);

        String getSelectedRepository();

        void setSelectedRepository(String alias);

        void setRepositorySelectorVisibility(final boolean visible);

        void clearBranches();

        void addBranch(final String branchName);

        String getSelectedBranch();

        void setSelectedBranch(final String branchName);

        void setBranchSelectorVisibility(boolean visible);
    }

    private View view;
    private Caller<LibraryService> libraryService;
    private LibraryPreferences libraryPreferences;
    private LibraryInternalPreferences libraryInternalPreferences;
    private PlaceManager placeManager;
    private LibraryPlaces libraryPlaces;
    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    private OrganizationalUnitRepositoryInfo info;
    private Repository selectedRepository;
    private String selectedBranch;

    @Inject
    public LibraryToolbarPresenter(final View view,
                                   final Caller<LibraryService> libraryService,
                                   final LibraryPreferences libraryPreferences,
                                   final LibraryInternalPreferences libraryInternalPreferences,
                                   final PlaceManager placeManager,
                                   final LibraryPlaces libraryPlaces,
                                   final Event<ProjectContextChangeEvent> projectContextChangeEvent) {
        this.view = view;
        this.libraryService = libraryService;
        this.libraryPreferences = libraryPreferences;
        this.libraryInternalPreferences = libraryInternalPreferences;
        this.placeManager = placeManager;
        this.libraryPlaces = libraryPlaces;
        this.projectContextChangeEvent = projectContextChangeEvent;
    }

    public void init(final Command callback) {
        libraryService.call((OrganizationalUnitRepositoryInfo info) -> {
            LibraryToolbarPresenter.this.info = info;
            view.init(LibraryToolbarPresenter.this);

            setupRepositories(info);
            selectedRepository = info.getSelectedRepository();
            selectedBranch = info.getSelectedRepository().getDefaultBranch();

            setBranchSelectorVisibility();
            setRepositorySelectorVisibility();

            final ProjectContextChangeEvent event = new ProjectContextChangeEvent(info.getSelectedOrganizationalUnit());
            projectContextChangeEvent.fire(event);

            callback.execute();
        }).getDefaultOrganizationalUnitRepositoryInfo();
    }

    public void setSelectedInfo(final OrganizationalUnit organizationalUnit,
                                final Repository repository,
                                final Command callback) {
        libraryService.call((OrganizationalUnitRepositoryInfo newInfo) -> {
            newInfo.setSelectedRepository(repository);
            refreshLibrary(newInfo,
                           callback);
        }).getOrganizationalUnitRepositoryInfo(organizationalUnit);
    }

    private void setupRepositories(final OrganizationalUnitRepositoryInfo info) {
        view.clearRepositories();
        info.getRepositories().forEach(repo -> view.addRepository(repo.getAlias()));
        view.setSelectedRepository(info.getSelectedRepository().getAlias());

        setUpBranches(info.getSelectedRepository().getDefaultBranch(),
                      info.getSelectedRepository());
    }

    private void setUpBranches(final String selectedBranch,
                               final Repository repository) {
        view.clearBranches();
        for (final String branchName : repository.getBranches()) {
            view.addBranch(branchName);
        }
        view.setSelectedBranch(selectedBranch);
    }

    void onUpdateSelectedRepository() {
        refreshLibrary(null);
        setUpBranches(selectedBranch,
                      selectedRepository);

        libraryInternalPreferences.load(loadedLibraryInternalPreferences -> {
                                            loadedLibraryInternalPreferences.setLastOpenedRepository(selectedRepository.getAlias());
                                            loadedLibraryInternalPreferences.save();
                                        },
                                        error -> {
                                        });
    }

    void onUpdateSelectedBranch() {
        refreshLibrary(null);
    }

    private void refreshLibrary(final OrganizationalUnitRepositoryInfo newInfo) {
        refreshLibrary(newInfo,
                       null);
    }

    private void refreshLibrary(final OrganizationalUnitRepositoryInfo newInfo,
                                final Command callback) {
        if (placeManager.closeAllPlacesOrNothing()) {
            if (newInfo != null) {
                this.info = newInfo;
                setupRepositories(info);
            }
            selectedRepository = getViewSelectedRepository();
            selectedBranch = getViewSelectedBranch();

            setBranchSelectorVisibility();
            setRepositorySelectorVisibility();

            libraryPlaces.goToLibrary(callback);
        } else {
            view.setSelectedRepository(selectedRepository.getAlias());

            setUpBranches(selectedBranch,
                          selectedRepository);
        }
    }

    private void setRepositorySelectorVisibility() {
        view.setRepositorySelectorVisibility(info.getRepositories().size() > 1);
    }

    private void setBranchSelectorVisibility() {
        view.setBranchSelectorVisibility(selectedRepository.getBranches().size() > 1);
    }

    private String getViewSelectedBranch() {
        if (!selectedRepository.getBranches().contains(view.getSelectedBranch())) {
            return selectedRepository.getDefaultBranch();
        } else {
            return view.getSelectedBranch();
        }
    }

    public Repository getSelectedRepository() {
        return selectedRepository;
    }

    private Repository getViewSelectedRepository() {
        return info.getRepositories().stream()
                .filter(repo -> repo.getAlias().equals(view.getSelectedRepository()))
                .findFirst().get();
    }

    public UberElement<LibraryToolbarPresenter> getView() {
        return view;
    }

    public String getSelectedBranch() {
        return selectedBranch;
    }
}
