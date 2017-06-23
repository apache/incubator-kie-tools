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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterCreateOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.search.FilterUpdateEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup.OrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.organizationalunit.OrganizationalUnitTileWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

@WorkbenchScreen(identifier = LibraryPlaces.ORGANIZATIONAL_UNITS_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class OrganizationalUnitsScreen {

    public interface View extends UberElement<OrganizationalUnitsScreen> {

        void clearOrganizationalUnits();

        String getFilterName();

        void setFilterName(String name);

        void hideCreateOrganizationalUnitAction();

        void addOrganizationalUnit(OrganizationalUnitTileWidget organizationalUnitTileWidget);
    }

    private View view;

    private Caller<LibraryService> libraryService;

    private OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter;

    private OrganizationalUnitController organizationalUnitController;

    private ManagedInstance<OrganizationalUnitTileWidget> organizationalUnitTileWidgets;

    List<OrganizationalUnit> organizationalUnits;

    @Inject
    public OrganizationalUnitsScreen(final View view,
                                     final Caller<LibraryService> libraryService,
                                     final OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter,
                                     final OrganizationalUnitController organizationalUnitController,
                                     final ManagedInstance<OrganizationalUnitTileWidget> organizationalUnitTileWidgets) {
        this.view = view;
        this.libraryService = libraryService;
        this.organizationalUnitPopUpPresenter = organizationalUnitPopUpPresenter;
        this.organizationalUnitController = organizationalUnitController;
        this.organizationalUnitTileWidgets = organizationalUnitTileWidgets;
    }

    @PostConstruct
    public void init() {
        setupView();
        setupOrganizationalUnits();
    }

    private void setupView() {
        if (!canCreateOrganizationalUnit()) {
            view.hideCreateOrganizationalUnitAction();
        }
    }

    private void setupOrganizationalUnits() {
        if (organizationalUnitController.canReadOrgUnits()) {
            libraryService.call((List<OrganizationalUnit> allOrganizationalUnits) -> {
                organizationalUnits = allOrganizationalUnits;
                refreshOrganizationalUnits(organizationalUnits);
            }).getOrganizationalUnits();
        }
    }

    private void refreshOrganizationalUnits(final List<OrganizationalUnit> organizationalUnits) {
        view.clearOrganizationalUnits();
        organizationalUnits.forEach(organizationalUnit -> {
            final OrganizationalUnitTileWidget organizationalUnitTileWidget = organizationalUnitTileWidgets.get();
            organizationalUnitTileWidget.init(organizationalUnit);
            view.addOrganizationalUnit(organizationalUnitTileWidget);
        });
    }

    public void createOrganizationalUnit() {
        organizationalUnitPopUpPresenter.showAddPopUp();
    }

    public void organizationalUnitCreated(@Observes final AfterCreateOrganizationalUnitEvent afterCreateOrganizationalUnitEvent) {
        organizationalUnits.add(afterCreateOrganizationalUnitEvent.getOrganizationalUnit());
        refresh();
    }

    public void organizationalUnitEdited(@Observes final AfterEditOrganizationalUnitEvent afterEditOrganizationalUnitEvent) {
        organizationalUnits.remove(afterEditOrganizationalUnitEvent.getPreviousOrganizationalUnit());
        organizationalUnits.add(afterEditOrganizationalUnitEvent.getEditedOrganizationalUnit());
        refresh();
    }

    public void organizationalUnitDeleted(@Observes final AfterDeleteOrganizationalUnitEvent afterDeleteOrganizationalUnitEvent) {
        organizationalUnits.remove(afterDeleteOrganizationalUnitEvent.getOrganizationalUnit());
        refresh();
    }

    public void refresh() {
        final String filterName = view.getFilterName().toUpperCase();
        final List<OrganizationalUnit> filteredOrganizationalUnits = organizationalUnits.stream()
                .filter(ou -> ou.getName().toUpperCase().startsWith(filterName))
                .collect(Collectors.toList());
        filteredOrganizationalUnits.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        refreshOrganizationalUnits(filteredOrganizationalUnits);
    }

    public void filterUpdate(@Observes final FilterUpdateEvent event) {
        view.setFilterName(event.getName());
        refresh();
    }

    public boolean canCreateOrganizationalUnit() {
        return organizationalUnitController.canCreateOrgUnits();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Organizational Units Screen";
    }

    @WorkbenchPartView
    public UberElement<OrganizationalUnitsScreen> getView() {
        return view;
    }
}
