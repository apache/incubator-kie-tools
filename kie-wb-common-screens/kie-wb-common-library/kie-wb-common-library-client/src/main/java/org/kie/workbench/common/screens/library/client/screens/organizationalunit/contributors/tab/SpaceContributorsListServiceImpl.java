/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;

public class SpaceContributorsListServiceImpl implements ContributorsListService {

    private LibraryPlaces libraryPlaces;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent;

    private OrganizationalUnitController organizationalUnitController;

    private Caller<LibraryService> libraryService;

    @Inject
    public SpaceContributorsListServiceImpl(final LibraryPlaces libraryPlaces,
                                            final Caller<OrganizationalUnitService> organizationalUnitService,
                                            final Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent,
                                            final OrganizationalUnitController organizationalUnitController,
                                            final Caller<LibraryService> libraryService) {
        this.libraryPlaces = libraryPlaces;
        this.organizationalUnitService = organizationalUnitService;
        this.afterEditOrganizationalUnitEvent = afterEditOrganizationalUnitEvent;
        this.organizationalUnitController = organizationalUnitController;
        this.libraryService = libraryService;
    }

    @Override
    public void getContributors(Consumer<List<Contributor>> contributorsConsumer) {
        organizationalUnitService.call((OrganizationalUnit organizationalUnit) -> {
            contributorsConsumer.accept(new ArrayList<>(organizationalUnit.getContributors()));
        }).getOrganizationalUnit(libraryPlaces.getActiveSpace().getName());
    }

    @Override
    public void saveContributors(final List<Contributor> contributors,
                                 final Runnable successCallback,
                                 final ErrorCallback<Message> errorCallback) {
        organizationalUnitService.call((OrganizationalUnit organizationalUnit) -> {
            organizationalUnitService.call((OrganizationalUnit newOrganizationalUnit) -> {
                successCallback.run();
                afterEditOrganizationalUnitEvent.fire(new AfterEditOrganizationalUnitEvent(organizationalUnit,
                                                                                           newOrganizationalUnit));
            }, errorCallback).updateOrganizationalUnit(organizationalUnit.getName(),
                                                       organizationalUnit.getDefaultGroupId(),
                                                       contributors);
        }).getOrganizationalUnit(libraryPlaces.getActiveSpace().getName());
    }

    @Override
    public boolean canEditContributors() {
        return organizationalUnitController.canUpdateOrgUnit(libraryPlaces.getActiveSpace());
    }

    @Override
    public void getValidUsernames(final Consumer<List<String>> validUsernamesConsumer) {
        libraryService.call((RemoteCallback<List<String>>) validUsernamesConsumer::accept).getAllUsers();
    }
}
