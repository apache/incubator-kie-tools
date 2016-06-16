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

package org.kie.workbench.common.screens.explorer.client.widgets;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.explorer.client.TestUtils.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ActiveContextManagerOnProjectAddedTest {

    @Mock
    ActiveContextItems activeContextItems;

    @Mock
    ActiveContextOptions activeOptions;

    @Mock
    ExplorerService explorerService;

    @Mock
    AuthorizationManager authorizationManager;

    @Mock
    SessionInfo sessionInfo;

    @Mock
    User identity;

    @Mock
    View view;

    @Mock
    RemoteCallback<ProjectExplorerContent> remoteCallback;

    @Captor
    ArgumentCaptor<ProjectExplorerContentQuery> projectExplorerContentQueryCaptor;

    private ActiveContextManager activeContextManager;

    @Before
    public void setUp() {
        when( view.isVisible() ).thenReturn( true );

        when( sessionInfo.getIdentity() ).thenReturn( identity );
        when( sessionInfo.getId() ).thenReturn( "sessionID" );

        this.activeContextManager = spy( new ActiveContextManager( activeContextItems,
                                                                   activeOptions,
                                                                   new CallerMock<>( explorerService ),
                                                                   authorizationManager,
                                                                   sessionInfo ) );

        this.activeContextManager.init( view,
                                        remoteCallback );
    }

    @Test
    public void testViewNotVisible() throws Exception {
        when( view.isVisible() ).thenReturn( false );

        activeContextManager.onProjectAdded( new NewProjectEvent() );

        verify( explorerService, never() ).getContent( any( ProjectExplorerContentQuery.class ) );
    }

    @Test
    public void testProjectNull() throws Exception {
        activeContextManager.onProjectAdded( new NewProjectEvent() );

        verify( explorerService, never() ).getContent( any( ProjectExplorerContentQuery.class ) );
    }

    @Test
    public void testNotInCurrentSession() throws Exception {
        activeContextManager.onProjectAdded( new NewProjectEvent( mock( Project.class ),
                                                                  "differentID",
                                                                  "userName" ) );

        verifyFullRefresh();
    }

    @Test
    public void testNotInSameBranch() throws Exception {
        final Repository repository = mock( Repository.class );
        when( activeContextItems.getActiveRepository() ).thenReturn( repository );
        when( activeContextItems.getActiveBranch() ).thenReturn( "master" );

        final Path masterRootPath = getPathMock( "default://master@uf-playground/" );

        when( repository.getBranchRoot( "master" ) ).thenReturn( masterRootPath );

        final Project project = getProjectMock( "default://devBranch@uf-playground/myProject" );

        activeContextManager.onProjectAdded( new NewProjectEvent( project,
                                                                  "sessionID",
                                                                  "userName" ) );

        verifyFullRefresh();
    }

    @Test
    public void testProjectRefresh() throws Exception {
        final Repository repository = mock( Repository.class );
        when( activeContextItems.getActiveRepository() ).thenReturn( repository );
        when( activeContextItems.getActiveBranch() ).thenReturn( "master" );

        final Path masterRootPath = getPathMock( "default://master@uf-playground/" );

        when( repository.getBranchRoot( "master" ) ).thenReturn( masterRootPath );

        final Project project = getProjectMock( "default://master@uf-playground/myProject" );

        activeContextManager.onProjectAdded( new NewProjectEvent( project,
                                                                  "sessionID",
                                                                  "userName" ) );

        verifyProjectRefresh( project );
    }

    private void verifyProjectRefresh( final Project project ) {
        verify( explorerService ).getContent( projectExplorerContentQueryCaptor.capture() );
        final ProjectExplorerContentQuery query = projectExplorerContentQueryCaptor.getValue();
        assertNotNull( query.getProject() );
        assertEquals( project, query.getProject() );
    }

    private void verifyFullRefresh() {
        verify( explorerService ).getContent( projectExplorerContentQueryCaptor.capture() );
        final ProjectExplorerContentQuery query = projectExplorerContentQueryCaptor.getValue();
        assertNull( query.getProject() );
    }
}
