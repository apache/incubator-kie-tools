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

package org.kie.workbench.common.screens.library.client.monitor;

import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LibraryMonitorImplTest {

    private Caller<LibraryService> libraryServiceCaller;

    private Workbench workbench;

    private LibraryMonitorImpl libraryMonitor;
    private LibraryService libraryService;

    @Before
    public void setup() {
        workbench = mock( Workbench.class );
        libraryService = mock( LibraryService.class );
        libraryServiceCaller = new CallerMock<>( libraryService );

        libraryMonitor = new LibraryMonitorImpl( libraryServiceCaller, workbench );
    }

    @Test
    public void initializeWithProjectsTest() {
        thereIsAProjectInTheWorkbench( true );
    }

    @Test
    public void initializeWithoutProjectsTest() {
        thereIsAProjectInTheWorkbench( false );
    }

    @Test
    public void initializeErrorTest() {
        thereIsAProjectInTheWorkbench( null );
    }

    @Test
    public void thereIsAtLeastOneProjectAccessibleDefaultsToTrue() {
        assertTrue( libraryMonitor.thereIsAtLeastOneProjectAccessible() );
    }

    @Test
    public void thereIsAtLeastOneProjectAccessibleSetToTrue() {
        libraryMonitor.setThereIsAtLeastOneProjectAccessible( true );

        assertTrue( libraryMonitor.thereIsAtLeastOneProjectAccessible() );
    }

    @Test
    public void thereIsAtLeastOneProjectAccessibleSetToFalse() {
        libraryMonitor.setThereIsAtLeastOneProjectAccessible( false );

        assertFalse( libraryMonitor.thereIsAtLeastOneProjectAccessible() );
    }

    @Test
    public void onProjectCreationTest() {
        libraryMonitor.setThereIsAtLeastOneProjectAccessible( false );
        libraryMonitor.onProjectCreation( mock( NewProjectEvent.class ) );

        assertTrue( libraryMonitor.thereIsAtLeastOneProjectAccessible() );
    }

    @Test
    public void onOneOfManyProjectsDeletionTest() {
        deleteProject( false );
    }

    @Test
    public void onLastProjectDeletionTest() {
        deleteProject( true );
    }

    private void deleteProject( final boolean lastProject ) {
        libraryMonitor.setThereIsAtLeastOneProjectAccessible( true );
        doReturn( !lastProject ).when( libraryService ).thereIsAProjectInTheWorkbench();

        libraryMonitor.onProjectDeletion( mock( DeleteProjectEvent.class ) );

        assertEquals( !lastProject, libraryMonitor.thereIsAtLeastOneProjectAccessible() );
    }

    private void thereIsAProjectInTheWorkbench( final Boolean thereIsAProjectInTheWorkbench ) {
        if ( thereIsAProjectInTheWorkbench != null ) {
            doReturn( thereIsAProjectInTheWorkbench ).when( libraryService ).thereIsAProjectInTheWorkbench();
        } else {
            doThrow( new RuntimeException( "Error" ) ).when( libraryService ).thereIsAProjectInTheWorkbench();
        }

        libraryMonitor.initialize();

        assertEquals( thereIsAProjectInTheWorkbench == null || thereIsAProjectInTheWorkbench, libraryMonitor.thereIsAtLeastOneProjectAccessible() );
        verify( workbench ).addStartupBlocker( LibraryMonitor.class );
        verify( workbench ).removeStartupBlocker( LibraryMonitor.class );
    }
}
