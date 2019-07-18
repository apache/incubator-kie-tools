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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.contributors.SpaceContributorsUpdatedEvent;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.promise.Promises;
import org.uberfire.rpc.SessionInfo;

public class SpaceContributorsListServiceImpl implements ContributorsListService {

    private LibraryPlaces libraryPlaces;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent;

    private Caller<LibraryService> libraryService;

    private SessionInfo sessionInfo;

    private OrganizationalUnitController organizationalUnitController;

    private ContributorsSecurityUtils contributorsSecurityUtils;

    private Promises promises;

    private Consumer<Collection<Contributor>> contributorsConsumerForExternalChange;

    @Inject
    public SpaceContributorsListServiceImpl(final LibraryPlaces libraryPlaces,
                                            final Caller<OrganizationalUnitService> organizationalUnitService,
                                            final Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent,
                                            final Caller<LibraryService> libraryService,
                                            final SessionInfo sessionInfo,
                                            final OrganizationalUnitController organizationalUnitController,
                                            final ContributorsSecurityUtils contributorsSecurityUtils,
                                            final Promises promises) {
        this.libraryPlaces = libraryPlaces;
        this.organizationalUnitService = organizationalUnitService;
        this.afterEditOrganizationalUnitEvent = afterEditOrganizationalUnitEvent;
        this.libraryService = libraryService;
        this.sessionInfo = sessionInfo;
        this.organizationalUnitController = organizationalUnitController;
        this.contributorsSecurityUtils = contributorsSecurityUtils;
        this.promises = promises;
        this.contributorsConsumerForExternalChange = null;
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
    public Promise<Boolean> canEditContributors(final List<Contributor> contributors,
                                                final ContributorType type) {
        if (organizationalUnitController.canUpdateOrgUnit(libraryPlaces.getActiveSpace())) {
            return promises.resolve(true);
        }

        final Optional<Contributor> contributor = contributors.stream().filter(c -> c.getUsername().equals(sessionInfo.getIdentity().getIdentifier())).findFirst();
        if (contributor.isPresent()) {
            final ContributorType userContributorType = contributor.get().getType();
            return promises.resolve(contributorsSecurityUtils.canUserEditContributorOfType(userContributorType, type));
        }

        return promises.resolve(false);
    }

    @Override
    public void getValidUsernames(final Consumer<List<String>> validUsernamesConsumer) {
        libraryService.call((RemoteCallback<List<String>>) validUsernamesConsumer::accept).getAllUsers();
    }

    @Override
    public void onExternalChange(final Consumer<Collection<Contributor>> contributorsConsumer) {
        this.contributorsConsumerForExternalChange = contributorsConsumer;
    }

    @Override
    public boolean requireValidUsername() {
        return false;
    }

    @Override
    public String getInvalidNameMessageConstant() {
        return "";
    }

    public void onSpaceContributorsUpdatedEvent(@Observes final SpaceContributorsUpdatedEvent spaceContributorsUpdatedEvent) {
        if (this.contributorsConsumerForExternalChange != null
                && spaceContributorsUpdatedEvent.getOrganizationalUnit().getName().equals(libraryPlaces.getActiveSpace().getName())) {
            this.contributorsConsumerForExternalChange.accept(spaceContributorsUpdatedEvent.getOrganizationalUnit().getContributors());
        }
    }
}
