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

package org.kie.workbench.common.screens.library.client.widgets.organizationalunit;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup.OrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public class OrganizationalUnitTileWidget {

    public interface View extends UberElement<OrganizationalUnitTileWidget>,
                                  HasBusyIndicator {

        void setup(String iconClass,
                   String iconOnHoverClass,
                   OrganizationalUnit organizationalUnit,
                   Command selectCommand,
                   Command editCommand,
                   Command removeCommand);

        String getRemovingBusyIndicatorMessage();

        String getRemoveWarningMessage(String ouName);

        String getRemoveSuccessMessage();

        void hideEditAction();

        void hideRemoveAction();
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private Caller<LibraryService> libraryService;

    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    private OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private Event<AfterDeleteOrganizationalUnitEvent> afterDeleteOrganizationalUnitEvent;

    private Event<NotificationEvent> notificationEvent;

    private OrganizationalUnitController organizationalUnitController;

    private LibraryInternalPreferences libraryInternalPreferences;

    @Inject
    public OrganizationalUnitTileWidget(final View view,
                                        final LibraryPlaces libraryPlaces,
                                        final Caller<LibraryService> libraryService,
                                        final Event<ProjectContextChangeEvent> projectContextChangeEvent,
                                        final OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter,
                                        final Caller<OrganizationalUnitService> organizationalUnitService,
                                        final Event<AfterDeleteOrganizationalUnitEvent> afterDeleteOrganizationalUnitEvent,
                                        final Event<NotificationEvent> notificationEvent,
                                        final OrganizationalUnitController organizationalUnitController,
                                        final LibraryInternalPreferences libraryInternalPreferences) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.projectContextChangeEvent = projectContextChangeEvent;
        this.organizationalUnitPopUpPresenter = organizationalUnitPopUpPresenter;
        this.organizationalUnitService = organizationalUnitService;
        this.afterDeleteOrganizationalUnitEvent = afterDeleteOrganizationalUnitEvent;
        this.notificationEvent = notificationEvent;
        this.organizationalUnitController = organizationalUnitController;
        this.libraryInternalPreferences = libraryInternalPreferences;
    }

    public void init(final OrganizationalUnit organizationalUnit) {
        view.init(this);
        view.setup("fa-folder",
                   "fa-folder-open",
                   organizationalUnit,
                   () -> open(organizationalUnit),
                   () -> edit(organizationalUnit),
                   () -> remove(organizationalUnit));

        if (!canUpdateOrganizationalUnit(organizationalUnit)) {
            view.hideEditAction();
        }

        if (!canRemoveOrganizationalUnit(organizationalUnit)) {
            view.hideRemoveAction();
        }
    }

    public OrganizationalUnitRepositoryInfo open(OrganizationalUnit organizationalUnit) {
        return libraryService.call((OrganizationalUnitRepositoryInfo info) -> {
            libraryInternalPreferences.load(loadedLibraryInternalPreferences -> {
                                                loadedLibraryInternalPreferences.setLastOpenedOrganizationalUnit(info.getSelectedOrganizationalUnit().getIdentifier());
                                                loadedLibraryInternalPreferences.setLastOpenedRepository(info.getSelectedRepository().getAlias());
                                                loadedLibraryInternalPreferences.save();
                                            },
                                            error -> {
                                            });

            if (teamAlreadySelected(info)) {
                libraryPlaces.goToLibrary(() -> {
                });
            } else {
                final ProjectContextChangeEvent event = new ProjectContextChangeEvent(info.getSelectedOrganizationalUnit(),
                                                                                      info.getSelectedRepository(),
                                                                                      info.getSelectedRepository().getDefaultBranch());
                projectContextChangeEvent.fire(event);
            }
        }).getOrganizationalUnitRepositoryInfo(organizationalUnit);
    }

    private boolean teamAlreadySelected(OrganizationalUnitRepositoryInfo info) {
        return info.getSelectedOrganizationalUnit().equals(libraryPlaces.getSelectedOrganizationalUnit())
                && info.getSelectedRepository().equals(libraryPlaces.getSelectedRepository())
                && info.getSelectedRepository().getDefaultBranch().equals(libraryPlaces.getSelectedBranch());
    }

    public void edit(final OrganizationalUnit organizationalUnit) {
        if (canUpdateOrganizationalUnit(organizationalUnit)) {
            organizationalUnitPopUpPresenter.showEditPopUp(organizationalUnit);
        }
    }

    public void remove(final OrganizationalUnit organizationalUnit) {
        if (canRemoveOrganizationalUnit(organizationalUnit)) {
            if (confirmRemove(organizationalUnit)) {
                view.showBusyIndicator(view.getRemovingBusyIndicatorMessage());
                organizationalUnitService.call(v -> {
                                                   afterDeleteOrganizationalUnitEvent.fire(new AfterDeleteOrganizationalUnitEvent(organizationalUnit));
                                                   view.hideBusyIndicator();
                                                   notificationEvent.fire(new NotificationEvent(view.getRemoveSuccessMessage(),
                                                                                                NotificationEvent.NotificationType.SUCCESS));
                                               },
                                               new HasBusyIndicatorDefaultErrorCallback(view)).removeOrganizationalUnit(organizationalUnit.getName());
            }
        }
    }

    boolean confirmRemove(OrganizationalUnit organizationalUnit) {
        return Window.confirm(view.getRemoveWarningMessage(organizationalUnit.getName()));
    }

    boolean canUpdateOrganizationalUnit(final OrganizationalUnit organizationalUnit) {
        return organizationalUnitController.canUpdateOrgUnit(organizationalUnit);
    }

    boolean canRemoveOrganizationalUnit(final OrganizationalUnit organizationalUnit) {
        return organizationalUnitController.canDeleteOrgUnit(organizationalUnit);
    }

    public View getView() {
        return view;
    }
}
