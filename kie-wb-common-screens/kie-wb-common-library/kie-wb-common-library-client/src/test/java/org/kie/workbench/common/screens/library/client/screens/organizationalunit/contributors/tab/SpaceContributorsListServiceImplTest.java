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
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
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
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    private SpaceContributorsListServiceImpl service;

    @Before
    public void setup() {
        organizationalUnitServiceCaller = new CallerMock<>(organizationalUnitService);
        libraryServiceCaller = new CallerMock<>(libraryService);

        final OrganizationalUnit activeSpace = mock(OrganizationalUnit.class);
        doReturn(mock(Space.class)).when(activeSpace).getSpace();
        doReturn(activeSpace).when(libraryPlaces).getActiveSpace();

        service = new SpaceContributorsListServiceImpl(libraryPlaces,
                                                       organizationalUnitServiceCaller,
                                                       afterEditOrganizationalUnitEvent,
                                                       organizationalUnitController,
                                                       libraryServiceCaller);
    }

    @Test
    public void getContributorsTest() {
        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("admin", ContributorType.OWNER));
        contributors.add(new Contributor("user", ContributorType.CONTRIBUTOR));
        contributors.add(new Contributor("Director", ContributorType.ADMIN));

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
        contributors.add(new Contributor("admin", ContributorType.OWNER));
        contributors.add(new Contributor("user", ContributorType.CONTRIBUTOR));
        contributors.add(new Contributor("Director", ContributorType.ADMIN));

        service.saveContributors(contributors, () -> {}, null);

        verify(organizationalUnitService).updateOrganizationalUnit("ou", null, contributors);
        verify(afterEditOrganizationalUnitEvent).fire(any());
    }
}
