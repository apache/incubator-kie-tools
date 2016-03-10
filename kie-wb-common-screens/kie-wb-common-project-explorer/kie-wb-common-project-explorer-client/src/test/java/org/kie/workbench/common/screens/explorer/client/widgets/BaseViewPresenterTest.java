/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.backend.server.preferences.ExplorerPreferencesLoader;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewWidget;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopupView;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopupView;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BaseViewPresenterTest {

    @GwtMock
    CommonConstants commonConstants;

    private ExplorerService explorerServiceActual = mock( ExplorerService.class );
    private BuildService buildServiceActual = mock( BuildService.class );

    @Spy
    private Caller<ExplorerService> explorerService = new CallerMock<ExplorerService>( explorerServiceActual );

    @Spy
    private Caller<BuildService> buildService = new CallerMock<BuildService>( buildServiceActual );

    @Spy
    private Caller<VFSService> vfsService = new CallerMock<VFSService>( mock( VFSService.class ) );

    @Spy
    private Caller<ValidationService> validationService = new CallerMock<ValidationService>( mock( ValidationService.class ) );

    @Mock
    private EventSourceMock<BuildResults> buildResultsEvent;

    @Mock
    private EventSourceMock<ProjectContextChangeEvent> contextChangedEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private User identity;

    @Mock
    private RuntimeAuthorizationManager authorizationManager;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private BusinessViewWidget view;

    @Mock
    private ActiveContextItems activeContextItems;

    @Mock
    private ActiveContextManager activeContextManager;

    @Mock
    private ActiveContextOptions activeOptions;

    private boolean isPresenterVisible = true;

    @InjectMocks
    private BaseViewPresenter presenter = new BaseViewPresenter( view ) {
        @Override
        protected boolean isViewVisible() {

            return isPresenterVisible;
        }

        @Override
        protected RenamePopupView getRenameView() {
            return mock( RenamePopupView.class );
        }

        @Override
        protected CopyPopupView getCopyView() {
            return mock( CopyPopupView.class );
        }
    };

    private ProjectExplorerContent content = new ProjectExplorerContent( Collections.<OrganizationalUnit>emptySet(),
                                                                         new OrganizationalUnitImpl(),
                                                                         Collections.<Repository>emptySet(),
                                                                         new GitRepository(),
                                                                         "master",
                                                                         Collections.<Project>emptySet(),
                                                                         new Project(),
                                                                         new FolderListing(),
                                                                         Collections.<FolderItem, List<FolderItem>>emptyMap() );

    @Before
    public void setup() {
        when( view.getExplorer() ).thenReturn( mock( Explorer.class ) );
        when( explorerServiceActual.getContent( any( ProjectExplorerContentQuery.class ) ) ).thenReturn( content );
        when( activeOptions.getOptions() ).thenReturn( new ActiveOptions() );
    }

    @Test
    public void testInitCalled() throws Exception {
        presenter.init();
        verify(view).init( presenter );
    }

    @Test
    public void testDeleteNotification() {
        final ArgumentCaptor<ParameterizedCommand> commandCaptor = ArgumentCaptor.forClass( ParameterizedCommand.class );

        doAnswer( new Answer<Void>() {

            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                commandCaptor.getValue().execute( "message" );
                return null;
            }
        } ).when( view ).deleteItem( commandCaptor.capture() );

        final FolderItem item = mock( FolderItem.class );
        presenter.deleteItem( item );

        verify( notification,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }

    @Test
    public void testCopyNotification() {
        final ArgumentCaptor<CommandWithFileNameAndCommitMessage> commandCaptor = ArgumentCaptor.forClass( CommandWithFileNameAndCommitMessage.class );

        doAnswer( new Answer<Void>() {

            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                commandCaptor.getValue().execute( new FileNameAndCommitMessage( "fileName",
                                                                                "message" ) );
                return null;
            }
        } ).when( view ).copyItem( any( Path.class ),
                                   any( Validator.class ),
                                   commandCaptor.capture(),
                                   any( CopyPopupView.class ) );

        final FolderItem item = mock( FolderItem.class );
        presenter.copyItem( item );

        verify( notification,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }

    @Test
    public void testRenameNotification() {
        final ArgumentCaptor<CommandWithFileNameAndCommitMessage> commandCaptor = ArgumentCaptor.forClass( CommandWithFileNameAndCommitMessage.class );

        doAnswer( new Answer<Void>() {

            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                commandCaptor.getValue().execute( new FileNameAndCommitMessage( "fileName",
                                                                                "message" ) );
                return null;
            }
        } ).when( view ).renameItem( any( Path.class ),
                                     any( Validator.class ),
                                     commandCaptor.capture(),
                                     any( RenamePopupView.class ) );

        final FolderItem item = mock( FolderItem.class );
        presenter.renameItem( item );

        verify( notification,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }

    @Test
    public void testAutomaticProjectBuildDisabled() {
        final OrganizationalUnit ou = mock( OrganizationalUnit.class );
        final Repository repository = mock( Repository.class );
        final Project project = mock( Project.class );

        ApplicationPreferences.setUp( new HashMap<String, String>() {{
            put( ExplorerService.BUILD_PROJECT_PROPERTY_NAME,
                 "true" );
        }} );

        when( activeContextItems.setupActiveProject( content ) ).thenReturn( true );
        when( activeContextItems.getActiveOrganizationalUnit() ).thenReturn( ou );
        when( activeContextItems.getActiveRepository() ).thenReturn( repository );
        when( activeContextItems.getActiveBranch() ).thenReturn( "master" );
        when( activeContextItems.getActiveProject() ).thenReturn( project );

        presenter.doContentCallback( content );

        verify( buildServiceActual,
                never() ).build( any( Project.class ) );
    }

    @Test
    public void testAutomaticProjectBuildEnabled() {
        final OrganizationalUnit ou = mock( OrganizationalUnit.class );
        final Repository repository = mock( Repository.class );
        final Project project = mock( Project.class );

        ApplicationPreferences.setUp( new HashMap<String, String>() );

        when( activeContextItems.setupActiveProject( content ) ).thenReturn( true );
        when( activeContextItems.getActiveOrganizationalUnit() ).thenReturn( ou );
        when( activeContextItems.getActiveRepository() ).thenReturn( repository );
        when( activeContextItems.getActiveBranch() ).thenReturn( "master" );
        when( activeContextItems.getActiveProject() ).thenReturn( project );

        presenter.doContentCallback( content );

        verify( buildServiceActual,
                times( 1 ) ).build( project );
    }

    @Test
    public void testAutomaticProjectBuildDisabledSystemProperty() {
        final OrganizationalUnit ou = mock( OrganizationalUnit.class );
        final Repository repository = mock( Repository.class );
        final Project project = mock( Project.class );

        String spBuildDisableProjectExplorer = null;
        try {
            spBuildDisableProjectExplorer = System.getProperty( ExplorerService.BUILD_PROJECT_PROPERTY_NAME );

            System.setProperty( ExplorerService.BUILD_PROJECT_PROPERTY_NAME,
                                "true" );

            final ExplorerPreferencesLoader preferencesLoader = new ExplorerPreferencesLoader();
            ApplicationPreferences.setUp( preferencesLoader.load() );

            when( activeContextItems.setupActiveProject( content ) ).thenReturn( true );
            when( activeContextItems.getActiveOrganizationalUnit() ).thenReturn( ou );
            when( activeContextItems.getActiveRepository() ).thenReturn( repository );
            when( activeContextItems.getActiveBranch() ).thenReturn( "master" );
            when( activeContextItems.getActiveProject() ).thenReturn( project );

            presenter.doContentCallback( content );

            verify( buildServiceActual,
                    never() ).build( any( Project.class ) );

        } finally {
            if ( spBuildDisableProjectExplorer != null ) {
                System.setProperty( ExplorerService.BUILD_PROJECT_PROPERTY_NAME,
                                    spBuildDisableProjectExplorer );
            }
        }
    }

    @Test
    public void testChangeOfBranchActivatesContextChange() {

        ApplicationPreferences.setUp( new ExplorerPreferencesLoader().load() );

        when( activeContextItems.setupActiveBranch( content ) ).thenReturn( true );
        presenter.doContentCallback( content );
        verify( activeContextItems ).fireContextChangeEvent();

        reset( activeContextItems );
        when( activeContextItems.setupActiveBranch( content ) ).thenReturn( false );
        presenter.doContentCallback( content );
        verify( activeContextItems, never() ).fireContextChangeEvent();

        reset( activeContextItems );
        when( activeContextItems.setupActiveBranch( content ) ).thenReturn( true );
        presenter.doContentCallback( content );
        verify( activeContextItems ).fireContextChangeEvent();
    }

    @Test
    public void testOnActiveOptionsChange() throws Exception {

        presenter.onActiveOptionsChange( new ActiveOptionsChangedEvent() );
        verify( view ).setVisible( true );

        isPresenterVisible = false;
        presenter.onActiveOptionsChange( new ActiveOptionsChangedEvent() );
        verify( view ).setVisible( false );

    }

}
