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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.monitor.LibraryMonitor;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class NewProjectScreenTest {

    @Mock
    NewProjectScreen.View view;

    @Spy
    @InjectMocks
    NewProjectScreen newProjectScreen;

    @Mock
    BusyIndicatorView busyIndicatorView;

    @Mock
    Event<NotificationEvent> notificationEvent;

    @Mock
    TranslationService ts;

    @Mock
    AuthorizationManager authorizationManager;

    @Mock
    SessionInfo sessionInfo;

    @Mock
    LibraryService libraryService;

    @Mock
    LibraryMonitor libraryMonitor;

    @Mock
    PlaceManager placeManager;

    @Mock
    private OrganizationalUnit ou1;

    @Mock
    private OrganizationalUnit ou2;

    CallerMock<LibraryService> libraryServiceCaller;
    private String ouAlias;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>( libraryService );
        newProjectScreen.libraryService = libraryServiceCaller;

        doNothing().when( newProjectScreen ).goToProject( any( KieProject.class ) );
        doReturn( "" ).when( view ).getOrganizationUnitSelected();
    }

    @Test
    public void loadTest() throws Exception {
        when( libraryService.getDefaultLibraryInfo() ).thenReturn( getDefaultLibraryMock() );

        newProjectScreen.load();

        verify( view ).setOUAlias( ouAlias );
        verify( view, times( 2 ) ).addOrganizationUnit( any() );
        verify( view ).setOrganizationUnitSelected( ou2.getIdentifier() );
    }

    @Test
    public void projectCreationNotifiesLibraryMonitorTest() {
        newProjectScreen.getSuccessCallback().callback( null );

        verify( libraryMonitor ).setThereIsAtLeastOneProjectAccessible( true );
    }

    @Test
    public void projectCreationArgumentsTest() {
        doReturn( "baseUrl" ).when( newProjectScreen ).getBaseURL();
        doReturn( "selectedOU" ).when( view ).getOrganizationUnitSelected();

        newProjectScreen.createProject( "projectName" );

        verify( libraryService ).newProject( "projectName", "selectedOU", "baseUrl" );
    }

    @Test
    public void openProjectTest() {
        final KieProject project = mock( KieProject.class );
        doReturn( "projectName" ).when( project ).getProjectName();
        doReturn( "projectPath" ).when( project ).getIdentifier();

        newProjectScreen.openProject( project );

        final Map<String, String> params = new HashMap<>();
        params.put( "projectName", project.getProjectName() );
        params.put( "projectPath", project.getIdentifier() );
        verify( placeManager ).goTo( new DefaultPlaceRequest( LibraryPlaces.PROJECT_SCREEN, params ) );
    }

    private LibraryInfo getDefaultLibraryMock() {
        OrganizationalUnit defaultOrganizationUnit = ou1;
        OrganizationalUnit selectedOrganizationUnit = ou2;

        Set<Project> projects = new HashSet<>();
        projects.add( mock( Project.class ) );
        projects.add( mock( Project.class ) );
        projects.add( mock( Project.class ) );
        Collection<OrganizationalUnit> organizationUnits = Arrays.asList( ou1, ou2 );
        ouAlias = "alias";

        LibraryInfo libraryInfo = new LibraryInfo( defaultOrganizationUnit, selectedOrganizationUnit, projects,
                                                   organizationUnits, ouAlias );
        return libraryInfo;
    }
}