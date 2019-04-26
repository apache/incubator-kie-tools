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
import java.util.Collections;
import java.util.List;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpaceContributorsListServiceImplTest {

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private OrganizationalUnitService organizationalUnitService;
    private Caller<OrganizationalUnitService> organizationalUnitServiceCaller;

    @Mock
    private EventSourceMock<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    private SessionInfo sessionInfo;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Spy
    private ContributorsSecurityUtils contributorsSecurityUtils;

    private Promises promises;

    private SpaceContributorsListServiceImpl service;

    @Before
    public void setup() {
        promises = new SyncPromises();
        sessionInfo = new SessionInfoMock("owner");
        organizationalUnitServiceCaller = new CallerMock<>(organizationalUnitService);
        libraryServiceCaller = new CallerMock<>(libraryService);

        final OrganizationalUnit activeSpace = mock(OrganizationalUnit.class);
        doReturn(mock(Space.class)).when(activeSpace).getSpace();
        doReturn(activeSpace).when(libraryPlaces).getActiveSpace();

        service = new SpaceContributorsListServiceImpl(libraryPlaces,
                                                       organizationalUnitServiceCaller,
                                                       afterEditOrganizationalUnitEvent,
                                                       libraryServiceCaller,
                                                       sessionInfo,
                                                       organizationalUnitController,
                                                       contributorsSecurityUtils,
                                                       promises);
    }

    @Test
    public void getContributorsTest() {
        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("owner", ContributorType.OWNER));
        contributors.add(new Contributor("contributor", ContributorType.CONTRIBUTOR));
        contributors.add(new Contributor("admin", ContributorType.ADMIN));

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(contributors).when(organizationalUnit).getContributors();

        doReturn(organizationalUnit).when(organizationalUnitService).getOrganizationalUnit(anyString());

        service.getContributors(repositoryContributors -> {
            assertEquals(3, repositoryContributors.size());
        });
    }

    @Test
    public void saveContributorsTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("ou").when(organizationalUnit).getName();
        doReturn(organizationalUnit).when(organizationalUnitService).getOrganizationalUnit(anyString());
        doReturn(organizationalUnit).when(organizationalUnitService).updateOrganizationalUnit(any(), any(), any());

        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("owner", ContributorType.OWNER));
        contributors.add(new Contributor("contributor", ContributorType.CONTRIBUTOR));
        contributors.add(new Contributor("admin", ContributorType.ADMIN));

        service.saveContributors(contributors, () -> {}, null);

        verify(organizationalUnitService).updateOrganizationalUnit("ou", null, contributors);
        verify(afterEditOrganizationalUnitEvent).fire(any());
    }

    @Test
    public void userCanEditContributors() {
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        service.canEditContributors(Collections.emptyList(), ContributorType.CONTRIBUTOR).then(canEditContributors -> {
            assertTrue(canEditContributors);
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotEditContributors() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());
        service.canEditContributors(Collections.emptyList(), ContributorType.CONTRIBUTOR).then(canEditContributors -> {
            assertFalse(canEditContributors);
            return promises.resolve();
        });
    }

    @Test
    public void ownerCanEditContributors() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());

        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("owner", ContributorType.OWNER));

        service.canEditContributors(contributors, ContributorType.OWNER).then(canEditContributors -> {
            assertTrue(canEditContributors);
            return promises.resolve();
        });
        service.canEditContributors(contributors, ContributorType.ADMIN).then(canEditContributors -> {
            assertTrue(canEditContributors);
            return promises.resolve();
        });
        service.canEditContributors(contributors, ContributorType.CONTRIBUTOR).then(canEditContributors -> {
            assertTrue(canEditContributors);
            return promises.resolve();
        });
    }

    @Test
    public void adminCanEditSomeContributors() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());

        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("owner", ContributorType.ADMIN));

        service.canEditContributors(contributors, ContributorType.OWNER).then(canEditContributors -> {
            assertFalse(canEditContributors);
            return promises.resolve();
        });
        service.canEditContributors(contributors, ContributorType.ADMIN).then(canEditContributors -> {
            assertTrue(canEditContributors);
            return promises.resolve();
        });
        service.canEditContributors(contributors, ContributorType.CONTRIBUTOR).then(canEditContributors -> {
            assertTrue(canEditContributors);
            return promises.resolve();
        });
    }

    @Test
    public void contributorCanNotEditContributors() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());

        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("owner", ContributorType.CONTRIBUTOR));

        service.canEditContributors(contributors, ContributorType.OWNER).then(canEditContributors -> {
            assertFalse(canEditContributors);
            return promises.resolve();
        });
        service.canEditContributors(contributors, ContributorType.ADMIN).then(canEditContributors -> {
            assertFalse(canEditContributors);
            return promises.resolve();
        });
        service.canEditContributors(contributors, ContributorType.CONTRIBUTOR).then(canEditContributors -> {
            assertFalse(canEditContributors);
            return promises.resolve();
        });
    }
}
