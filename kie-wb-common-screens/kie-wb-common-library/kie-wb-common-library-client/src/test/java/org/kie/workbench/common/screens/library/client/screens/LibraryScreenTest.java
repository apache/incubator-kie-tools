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
package org.kie.workbench.common.screens.library.client.screens;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryBreadcrumbs;
import org.kie.workbench.common.screens.library.client.util.LibraryDocks;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.LibraryBreadCrumbToolbarPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class LibraryScreenTest {

    @Mock
    LibraryScreen.View view;

    @InjectMocks
    LibraryScreen libraryScreen;

    @Mock
    LibraryService libraryService;

    @Mock
    PlaceManager placeManager;

    @Mock
    private OrganizationalUnit defaultOU1;

    @Mock
    private OrganizationalUnit defaultOU2;

    @Mock
    private LibraryBreadcrumbs libraryBreadcrumbs;

    @Mock
    private LibraryBreadCrumbToolbarPresenter breadCrumbToolbarPresenter;

    @Mock
    private LibraryDocks libraryDocks;

    CallerMock<LibraryService> libraryServiceCaller;

    private String ouAlias;
    private Project proj1 = mock( Project.class );
    private Project proj2 = mock( Project.class );
    private Project proj3 = mock( Project.class );

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>( libraryService );
        libraryScreen.libraryService = libraryServiceCaller;
        when( libraryService.getDefaultLibraryInfo() ).thenReturn( getDefaultLibraryMock() );
        when( proj1.getProjectName() ).thenReturn( "a" );
        when( proj2.getProjectName() ).thenReturn( "b" );
        when( proj3.getProjectName() ).thenReturn( "c" );
    }

    @Test
    public void onStartupLoadLibraryTest() {
        libraryScreen.onStartup( mock( PlaceRequest.class ) );

        verify( view ).clearProjects();
        verify( view, times( getProjects().size() ) ).addProject( any(), any(), any() );
        verify( libraryBreadcrumbs ).setupToolBar( breadCrumbToolbarPresenter );
        verify( breadCrumbToolbarPresenter ).init( any(), any() );
        verify( libraryDocks ).refresh();
    }

    @Test
    public void filterProjects() {

        libraryScreen.onStartup( mock( PlaceRequest.class ) );

        assertEquals( getDefaultLibraryMock().getProjects().size(), libraryScreen.libraryInfo.getProjects().size() );

        assertEquals( 1, libraryScreen.filterProjects( "a" ).size() );
    }

    @Test
    public void onStartupLoadEmptyLibraryTest() {
        LibraryInfo emptyLibrary = new LibraryInfo();

        when( libraryService.getDefaultLibraryInfo() ).thenReturn( emptyLibrary );

        libraryScreen.onStartup( mock( PlaceRequest.class ) );

        verify( placeManager ).goTo( LibraryPlaces.NEW_PROJECT_PERSPECTIVE );

    }

    private LibraryInfo getDefaultLibraryMock() {

        OrganizationalUnit defaultOrganizationUnit = defaultOU1;
        OrganizationalUnit selectedOrganizationUnit = defaultOU2;

        ouAlias = "alias";

        LibraryInfo libraryInfo = new LibraryInfo( defaultOrganizationUnit, selectedOrganizationUnit, getProjects(),
                                                   getOus(), ouAlias );
        return libraryInfo;
    }

    private List<OrganizationalUnit> getOus() {
        return Arrays.asList( defaultOU1, defaultOU2 );
    }

    private Set<Project> getProjects() {
        Set<Project> projects = new HashSet<>();
        projects.add( proj1 );
        projects.add( proj2 );
        projects.add( proj3 );
        return projects;
    }

}