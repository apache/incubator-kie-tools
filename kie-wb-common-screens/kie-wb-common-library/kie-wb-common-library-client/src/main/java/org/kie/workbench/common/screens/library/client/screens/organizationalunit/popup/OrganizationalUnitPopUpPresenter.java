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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.events.AfterCreateOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

public class OrganizationalUnitPopUpPresenter {

    public interface View extends UberElement<OrganizationalUnitPopUpPresenter>,
                                  HasBusyIndicator {

        void show();

        void hide();

        void clear();

        void showError(final String errorMessage);

        String getName();

        String getEmptyNameValidationMessage();

        String getDuplicatedOrganizationalUnitValidationMessage();

        String getSavingMessage();

        String getSaveSuccessMessage();

        String getInvalidNameValidationMessage();
    }

    private View view;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private Event<AfterCreateOrganizationalUnitEvent> afterCreateOrganizationalUnitEvent;

    private Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent;

    private Event<NotificationEvent> notificationEvent;

    private LibraryPermissions libraryPermissions;

    private SessionInfo sessionInfo;

    private OrganizationalUnit organizationalUnit;

    @Inject
    public OrganizationalUnitPopUpPresenter(final View view,
                                            final Caller<OrganizationalUnitService> organizationalUnitService,
                                            final Event<AfterCreateOrganizationalUnitEvent> afterCreateOrganizationalUnitEvent,
                                            final Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent,
                                            final Event<NotificationEvent> notificationEvent,
                                            final LibraryPermissions libraryPermissions,
                                            final SessionInfo sessionInfo) {
        this.view = view;
        this.organizationalUnitService = organizationalUnitService;
        this.afterCreateOrganizationalUnitEvent = afterCreateOrganizationalUnitEvent;
        this.afterEditOrganizationalUnitEvent = afterEditOrganizationalUnitEvent;
        this.notificationEvent = notificationEvent;
        this.libraryPermissions = libraryPermissions;
        this.sessionInfo = sessionInfo;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show() {
        if (libraryPermissions.userCanCreateOrganizationalUnit()) {
            view.clear();
            view.show();
        }
    }

    public void save() {
        final String name = view.getName();
        final String defaultGroupId = "com." + view.getName().toLowerCase();
        final String owner = sessionInfo.getIdentity().getIdentifier();

        view.showBusyIndicator(view.getSavingMessage());
        validateFields(() -> saveCreation(name,
                                          defaultGroupId,
                                          owner));
    }

    void saveCreation(final String name,
                      final String defaultGroupId,
                      final String owner) {
        final Command saveCommand = () -> {
            final Collection<Repository> repositories = new ArrayList<>();
            final List<Contributor> contributors = Collections.singletonList(new Contributor(owner, ContributorType.OWNER));

            final RemoteCallback<OrganizationalUnit> successCallback = (OrganizationalUnit newOrganizationalUnit) -> {
                afterCreateOrganizationalUnitEvent.fire(new AfterCreateOrganizationalUnitEvent(newOrganizationalUnit));
                view.hideBusyIndicator();
                notificationEvent.fire(new NotificationEvent(view.getSaveSuccessMessage(),
                                                             NotificationEvent.NotificationType.SUCCESS));
                view.hide();
            };

            final HasBusyIndicatorDefaultErrorCallback errorCallback = new HasBusyIndicatorDefaultErrorCallback(view);

            organizationalUnitService.call(successCallback,
                                           errorCallback).createOrganizationalUnit(name,
                                                                                   defaultGroupId,
                                                                                   repositories,
                                                                                   contributors);
        };

        validateDuplicatedOrganizationalUnit(name,
                                             saveCommand);
    }

    private void validateFields(final Command successCallback) {
        final String name = view.getName();
        if (isEmpty(name)) {
            view.hideBusyIndicator();
            view.showError(view.getEmptyNameValidationMessage());
            return;
        }

        final String defaultGroupId = "com." + view.getName().toLowerCase();

        organizationalUnitService.call((Boolean valid) -> {
                                           if (!valid) {
                                               view.hideBusyIndicator();
                                               view.showError(view.getInvalidNameValidationMessage());
                                               return;
                                           }

                                           if (successCallback != null) {
                                               successCallback.execute();
                                           }
                                       },
                                       new HasBusyIndicatorDefaultErrorCallback(view)).isValidGroupId(defaultGroupId);
    }

    private void validateDuplicatedOrganizationalUnit(final String name,
                                                      final Command successCallback) {
        organizationalUnitService.call((OrganizationalUnit existingOrganizationalUnit) -> {
            if (existingOrganizationalUnit != null) {
                view.hideBusyIndicator();
                view.showError(view.getDuplicatedOrganizationalUnitValidationMessage());
                return;
            }

            if (successCallback != null) {
                successCallback.execute();
            }
        }).getOrganizationalUnit(name);
    }

    public void cancel() {
        view.hide();
    }

    private boolean isEmpty(final String text) {
        return text == null || text.trim().isEmpty();
    }
}
