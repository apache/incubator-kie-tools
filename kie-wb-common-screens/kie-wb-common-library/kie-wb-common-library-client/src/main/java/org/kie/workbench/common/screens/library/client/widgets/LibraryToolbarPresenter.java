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

package org.kie.workbench.common.screens.library.client.widgets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.api.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class LibraryToolbarPresenter {

    public interface View extends UberElement<LibraryToolbarPresenter> {

        void setOrganizationalUnitLabel(String label);

        void clearOrganizationalUnits();

        void addOrganizationUnit(String identifier);

        String getSelectedOrganizationalUnit();

        void setSelectedOrganizationalUnit(String identifier);

        void clearRepositories();

        void addRepository(String alias);

        String getSelectedRepository();

        void setSelectedRepository(String alias);

        void clearBranches();

        void addBranch(final String branchName);

        String getSelectedBranch();

        void setSelectedBranch(final String branchName);

        void setBranchSelectorVisibility(boolean visible);
    }

    private View view;
    private Caller<LibraryService> libraryService;
    private LibraryPreferences libraryPreferences;
    private PlaceManager placeManager;
    private LibraryPlaces libraryPlaces;
    private OrganizationalUnitRepositoryInfo info;
    private OrganizationalUnit selectedOrganizationalUnit;
    private Repository selectedRepository;
    private String selectedBranch;

    @Inject
    public LibraryToolbarPresenter(final View view,
                                   final Caller<LibraryService> libraryService,
                                   final LibraryPreferences libraryPreferences,
                                   final PlaceManager placeManager,
                                   final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.libraryService = libraryService;
        this.libraryPreferences = libraryPreferences;
        this.placeManager = placeManager;
        this.libraryPlaces = libraryPlaces;
    }

    public void init(final Command callback) {
        libraryService.call((OrganizationalUnitRepositoryInfo info) -> {
            LibraryToolbarPresenter.this.info = info;
            view.init(LibraryToolbarPresenter.this);
            setupOrganizationUnits(info);
            setupRepositories(info);
            selectedOrganizationalUnit = info.getSelectedOrganizationalUnit();
            selectedRepository = info.getSelectedRepository();
            selectedBranch = info.getSelectedRepository().getDefaultBranch();
            setBranchSelectorVisibility();
            callback.execute();
        }).getDefaultOrganizationalUnitRepositoryInfo();

        libraryPreferences.load(loadedLibraryPreferences -> {
                                    view.setOrganizationalUnitLabel(loadedLibraryPreferences.getOuAlias());
                                },
                                parameter -> {
                                });
    }

    public void setSelectedInfo(final OrganizationalUnit organizationalUnit,
                                final Repository repository,
                                final Command callback) {
        libraryService.call((OrganizationalUnitRepositoryInfo newInfo) -> {
            setupOrganizationUnits(newInfo);
            newInfo.setSelectedRepository(repository);
            refreshLibrary(newInfo,
                           callback);
        }).getOrganizationalUnitRepositoryInfo(organizationalUnit);
    }

    private void setupOrganizationUnits(final OrganizationalUnitRepositoryInfo info) {
        view.clearOrganizationalUnits();
        info.getOrganizationalUnits().forEach(ou -> view.addOrganizationUnit(ou.getIdentifier()));
        view.setSelectedOrganizationalUnit(info.getSelectedOrganizationalUnit().getIdentifier());
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

    void onUpdateSelectedOrganizationalUnit() {
        final OrganizationalUnit selectedOrganizationalUnit = getViewSelectedOrganizationalUnit();
        libraryService.call((OrganizationalUnitRepositoryInfo newInfo) -> refreshLibrary(newInfo))
                .getOrganizationalUnitRepositoryInfo(selectedOrganizationalUnit);

        libraryPreferences.setOuIdentifier(selectedOrganizationalUnit.getIdentifier());
        libraryPreferences.save();
    }

    void onUpdateSelectedRepository() {
        refreshLibrary(null);
        setUpBranches(selectedBranch,
                      selectedRepository);

        libraryPreferences.setRepositoryAlias(selectedRepository.getAlias());
        libraryPreferences.save();
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
            selectedOrganizationalUnit = getViewSelectedOrganizationalUnit();
            selectedRepository = getViewSelectedRepository();
            selectedBranch = getViewSelectedBranch();

            setBranchSelectorVisibility();

            libraryPlaces.goToLibrary(callback);
        } else {
            view.setSelectedOrganizationalUnit(selectedOrganizationalUnit.getIdentifier());
            view.setSelectedRepository(selectedRepository.getAlias());

            setUpBranches(selectedBranch,
                          selectedRepository);
        }
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

    public OrganizationalUnit getSelectedOrganizationalUnit() {
        return selectedOrganizationalUnit;
    }

    public Repository getSelectedRepository() {
        return selectedRepository;
    }

    private OrganizationalUnit getViewSelectedOrganizationalUnit() {
        return info.getOrganizationalUnits().stream()
                .filter(ou -> ou.getIdentifier().equals(view.getSelectedOrganizationalUnit()))
                .findFirst().get();
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
