/*
 * Copyright 2015 JBoss Inc
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
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenterImpl;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewWidget;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
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

    private ExplorerService explorerService = mock( ExplorerService.class );

    private BuildService buildService = mock( BuildService.class );

    private VFSService vfsService = mock( VFSService.class );

    private ValidationService validationService = mock( ValidationService.class );

    private CallerMock<ExplorerService> explorerServiceCaller = new CallerMock<ExplorerService>( explorerService );

    private CallerMock<BuildService> buildServiceCaller = new CallerMock<BuildService>( buildService );

    private CallerMock<VFSService> vfsServiceCaller = new CallerMock<VFSService>( vfsService );

    private CallerMock<ValidationService> validationServiceCaller = new CallerMock<ValidationService>( validationService );

    private BaseViewPresenter presenter;

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

    private ProjectExplorerContent content = new ProjectExplorerContent( Collections.<OrganizationalUnit>emptySet(),
                                                                         new OrganizationalUnitImpl(),
                                                                         Collections.<Repository>emptySet(),
                                                                         new GitRepository(),
                                                                         Collections.<Project>emptySet(),
                                                                         new Project(),
                                                                         new FolderListing(),
                                                                         Collections.<FolderItem, List<FolderItem>>emptyMap() );

    @Before
    public void setup() {
        presenter = new BusinessViewPresenterImpl( identity,
                                                   authorizationManager,
                                                   explorerServiceCaller,
                                                   buildServiceCaller,
                                                   vfsServiceCaller,
                                                   validationServiceCaller,
                                                   placeManager,
                                                   buildResultsEvent,
                                                   contextChangedEvent,
                                                   notification,
                                                   sessionInfo,
                                                   view );
        when( view.getExplorer() ).thenReturn( mock( Explorer.class ) );
        when( explorerService.getContent( any( ProjectExplorerContentQuery.class ) ) ).thenReturn( content );
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
                                   commandCaptor.capture() );

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
                                     commandCaptor.capture() );

        final FolderItem item = mock( FolderItem.class );
        presenter.renameItem( item );

        verify( notification,
                times( 1 ) ).fire( any( NotificationEvent.class ) );
    }

}
