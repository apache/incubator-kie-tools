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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.delete;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

public class DeleteOrganizationalUnitPopUpPresenter {

    public interface View extends UberElement<DeleteOrganizationalUnitPopUpPresenter>,
                                  HasBusyIndicator {

        String getConfirmedName();

        void show(String name);

        void showError(final String errorMessage);

        void hide();

        String getWrongConfirmedNameValidationMessage();

        String getDeletingMessage();

        String getDeleteSuccessMessage();
    }

    private View view;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private OrganizationalUnitController organizationalUnitController;

    private Event<NotificationEvent> notificationEvent;

    private LibraryPlaces libraryPlaces;

    OrganizationalUnit organizationalUnit;

    @Inject
    public DeleteOrganizationalUnitPopUpPresenter(final View view,
                                                  final Caller<OrganizationalUnitService> organizationalUnitService,
                                                  final OrganizationalUnitController organizationalUnitController,
                                                  final Event<NotificationEvent> notificationEvent,
                                                  final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.organizationalUnitService = organizationalUnitService;
        this.organizationalUnitController = organizationalUnitController;
        this.notificationEvent = notificationEvent;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final OrganizationalUnit organizationalUnit) {
        if (organizationalUnitController.canDeleteOrgUnit(organizationalUnit)) {
            this.organizationalUnit = organizationalUnit;
            view.show(organizationalUnit.getName());
        }
    }

    public void delete() {
        final String confirmedName = view.getConfirmedName();
        if (!organizationalUnit.getName().equals(confirmedName)) {
            view.showError(view.getWrongConfirmedNameValidationMessage());
            return;
        }

        view.showBusyIndicator(view.getDeletingMessage());
        organizationalUnitService.call(v -> {
                                           view.hideBusyIndicator();
                                           notificationEvent.fire(new NotificationEvent(view.getDeleteSuccessMessage(),
                                                                                        NotificationEvent.NotificationType.SUCCESS));
                                           view.hide();
                                           libraryPlaces.goToOrganizationalUnits();
                                       },
                                       new HasBusyIndicatorDefaultErrorCallback(view)).removeOrganizationalUnit(organizationalUnit.getName());
    }

    public void cancel() {
        view.hide();
    }

    public void onOrganizationalUnitRemoved(@Observes final RemoveOrganizationalUnitEvent removedOrganizationalUnitEvent) {
        if (removedOrganizationalUnitEvent.getOrganizationalUnit().getName().equals(organizationalUnit.getName())) {
            cancel();
        }
    }
}
