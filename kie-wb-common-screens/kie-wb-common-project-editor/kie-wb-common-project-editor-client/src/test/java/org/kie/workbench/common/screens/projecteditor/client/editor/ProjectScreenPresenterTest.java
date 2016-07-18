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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;

import com.google.common.collect.Sets;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.editor.extension.BuildOptionExtension;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectScreenPresenterTest {

    @GwtMock
    @SuppressWarnings("unused")
    private ButtonGroup buildOptions;

    @GwtMock
    @SuppressWarnings("unused")
    private Button buildOptionsButton1;

    @GwtMock
    @SuppressWarnings("unused")
    private DropDownMenu buildOptionsMenu;

    @GwtMock
    @SuppressWarnings("unused")
    private AnchorListItem buildOptionsMenuButton1;

    @GwtMock
    @SuppressWarnings("unused")
    private com.google.gwt.user.client.ui.Widget dependenciesPart;

    @Mock
    private DeploymentScreenPopupViewImpl deploymentScreenPopupView;

    @Spy
    private MockLockManagerInstances lockManagerInstanceProvider = new MockLockManagerInstances();

    private SpecManagementService specManagementServiceMock = mock( SpecManagementService.class );
    private ProjectScreenView view = mock( ProjectScreenView.class );
    private ProjectContext context = spy( new ProjectContext() );
    private ProjectScreenService projectScreenService = mock( ProjectScreenService.class );
    private BuildService buildService = mock( BuildService.class );
    private Event<NotificationEvent> notificationEvent = mock( EventSourceMock.class );
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup = mock( ConflictingRepositoriesPopup.class );

    private Repository repository = mock( Repository.class );
    private KieProject project = mock( KieProject.class );
    private Path pomPath = mock( Path.class );

    private ProjectScreenModel model;
    private ProjectScreenPresenter presenter;

    @Before
    public void setup() {
        ApplicationPreferences.setUp( new HashMap<String, String>() );

        //The BuildOptions widget is manipulated in the Presenter so we need some nasty mocking
        when( view.getBuildButtons() ).thenReturn( buildOptions );
        when( buildOptions.getWidget( eq( 0 ) ) ).thenReturn( buildOptionsButton1 );
        when( buildOptions.getWidget( eq( 1 ) ) ).thenReturn( buildOptionsMenu );
        when( buildOptionsMenu.getWidget( eq( 0 ) ) ).thenReturn( buildOptionsMenuButton1 );
        when( buildOptionsMenu.getWidget( eq( 1 ) ) ).thenReturn( buildOptionsMenuButton1 );

        constructProjectScreenPresenter( new CallerMock<BuildService>( buildService ),
                                         new CallerMock<SpecManagementService>( specManagementServiceMock ) );

        //Mock ProjectScreenService
        final POM pom = new POM( new GAV( "groupId",
                                          "artifactId",
                                          "version" ) );
        model = new ProjectScreenModel();
        model.setPOM( pom );
        when( projectScreenService.load( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( model );

        //Mock BuildService
        when( buildService.build( any( KieProject.class ) ) ).thenReturn( new BuildResults() );
        when( buildService.buildAndDeploy( any( KieProject.class ),
                                           any( DeploymentMode.class ) ) ).thenReturn( new BuildResults() );

        //Mock LockManager initialisation
        final Path path = mock( Path.class );
        final Metadata pomMetadata = mock( Metadata.class );
        model.setPOMMetaData( pomMetadata );
        when( pomMetadata.getPath() ).thenReturn( path );
        final Metadata kmoduleMetadata = mock( Metadata.class );
        model.setKModuleMetaData( kmoduleMetadata );
        when( kmoduleMetadata.getPath() ).thenReturn( path );
        final Metadata importsMetadata = mock( Metadata.class );
        model.setProjectImportsMetaData( importsMetadata );
        when( importsMetadata.getPath() ).thenReturn( path );

        //Mock ProjectContext
        when( context.getActiveRepository() ).thenReturn( repository );
        when( context.getActiveBranch() ).thenReturn( "master" );
        when( repository.getAlias() ).thenReturn( "repository" );

        when( project.getProjectName() ).thenReturn( "project" );
        when( project.getPomXMLPath() ).thenReturn( pomPath );
        when( project.getPom() ).thenReturn( pom );
        when( pomPath.getFileName() ).thenReturn( "pom.xml" );
        when( context.getActiveProject() ).thenReturn( project );

        //Trigger initialisation of view. Unfortunately this is the only way to initialise a Project in the Presenter
        context.onProjectContextChanged( new ProjectContextChangeEvent( mock( OrganizationalUnit.class ),
                                                                        repository,
                                                                        "master",
                                                                        project ) );

        verify( view,
                times( 1 ) ).setGAVCheckDisabledSetting( eq( false ) );
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildCommand() {
        presenter.triggerBuild();

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildSuccessful() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );

        verify( view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        //There are two calls to "hide" by this stage; one from the view initialisation one for the build
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildCommandFail() {
        BuildMessage message = mock( BuildMessage.class );
        List<BuildMessage> messages = new ArrayList<BuildMessage>();
        messages.add( message );

        BuildResults results = mock( BuildResults.class );
        when( results.getErrorMessages() ).thenReturn( messages );

        when( buildService.build(any(KieProject.class)) ).thenReturn( results );

        presenter.triggerBuild();

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildFailed() ) &&
                        type.equals( NotificationEvent.NotificationType.ERROR );
            }
        } ) );

        verify( view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        //There are two calls to "hide" by this stage; one from the view initialisation one for the build
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildAndDeployCommandSingleServerTemplate() {
        final ServerTemplate serverTemplate = new ServerTemplate("id", "name");
        when(specManagementServiceMock.listServerTemplates()).thenReturn(Collections.singletonList(serverTemplate));

        presenter.triggerBuildAndDeploy();

        ArgumentCaptor<ContainerSpec> containerSpecArgumentCaptor = ArgumentCaptor.forClass(ContainerSpec.class);
        verify( specManagementServiceMock ).saveContainerSpec( eq(serverTemplate.getId()), containerSpecArgumentCaptor.capture() );
        final ContainerSpec containerSpec = containerSpecArgumentCaptor.getValue();
        assertEquals(project.getPom().getGav().getArtifactId(), containerSpec.getContainerName());

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildSuccessful() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );
        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.DeploySuccessful() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );
        verify( notificationEvent, times( 2 ) ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 2, 2 );
    }

    @Test
    public void testBuildAndDeployCommandSingleServerTemplateContainerExists() {
        final String containerName = project.getPom().getGav().getArtifactId();
        final ServerTemplate serverTemplate = new ServerTemplate("id", "name");
        serverTemplate.addContainerSpec(new ContainerSpec(containerName, containerName, null, null, null, null));
        when(specManagementServiceMock.listServerTemplates()).thenReturn(Collections.singletonList(serverTemplate));

        presenter.triggerBuildAndDeploy();

        verify(deploymentScreenPopupView).setValidateExistingContainerCallback(any(DeploymentScreenPopupViewImpl.ValidateExistingContainerCallback.class));
        verify(deploymentScreenPopupView).setContainerId(containerName);
        verify(deploymentScreenPopupView).setStartContainer(true);
        verify(deploymentScreenPopupView).configure(any(com.google.gwt.user.client.Command.class));
        verify(deploymentScreenPopupView).show();
        verifyNoMoreInteractions(deploymentScreenPopupView);
    }

    @Test
    public void testBuildAndDeployCommandMultipleServerTemplate() {
        final String containerName = project.getPom().getGav().getArtifactId();
        final ServerTemplate serverTemplate1 = new ServerTemplate("id1", "name1");
        final ServerTemplate serverTemplate2 = new ServerTemplate("id2", "name2");

        when(specManagementServiceMock.listServerTemplates()).thenReturn(Arrays.asList(serverTemplate1, serverTemplate2));

        presenter.triggerBuildAndDeploy();

        verify(deploymentScreenPopupView).setValidateExistingContainerCallback(any(DeploymentScreenPopupViewImpl.ValidateExistingContainerCallback.class));
        verify(deploymentScreenPopupView).setContainerId(containerName);
        verify(deploymentScreenPopupView).setStartContainer(true);
        verify(deploymentScreenPopupView).addServerTemplates(eq(Sets.newHashSet("id1", "id2")));
        verify(deploymentScreenPopupView).configure(any(com.google.gwt.user.client.Command.class));
        verify(deploymentScreenPopupView).show();
        verifyNoMoreInteractions(deploymentScreenPopupView);
    }

    @Test
    public void testBuildAndDeployCommand() {
        presenter.triggerBuildAndDeploy();

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildSuccessful() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );

        verify( notificationEvent, times( 1 ) ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 2, 2 );
    }

    @Test
    public void testBuildAndDeployCommandFail() {

        BuildMessage message = mock( BuildMessage.class );
        List<BuildMessage> messages = new ArrayList<BuildMessage>();
        messages.add( message );

        BuildResults results = mock( BuildResults.class );
        when( results.getErrorMessages() ).thenReturn( messages );

        when( buildService.buildAndDeploy( any( KieProject.class ),
                any( DeploymentMode.class ) ) ).thenReturn( results );

        presenter.triggerBuildAndDeploy();

        verify( notificationEvent ).fire( argThat(new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches(final Object argument) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals(ProjectEditorResources.CONSTANTS.BuildFailed()) &&
                        type.equals(NotificationEvent.NotificationType.ERROR);
            }
        }) );

        verify( view,
                times( 1 ) ).showBusyIndicator(eq(ProjectEditorResources.CONSTANTS.Building()));
        //There are two calls to "hide" by this stage; one from the view initialisation one for the build
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    public void testAlreadyRunningBuild() {
        constructProjectScreenPresenter( buildServiceCaller(), new CallerMock<SpecManagementService>( specManagementServiceMock ) );

        presenter.triggerBuild();
        presenter.triggerBuild();

        verify( view, times( 1 ) ).showABuildIsAlreadyRunning();
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 2, 1 );
    }

    @Test
    public void testAlreadyRunningBuildAndDeploy() {
        constructProjectScreenPresenter( buildServiceCaller(), new CallerMock<SpecManagementService>(specManagementServiceMock) );
        presenter.onStartup( mock( PlaceRequest.class ) );

        presenter.triggerBuildAndDeploy();
        presenter.triggerBuildAndDeploy();

        verify( view, times( 1 ) ).showABuildIsAlreadyRunning();
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 3, 2 );
    }

    @Test
    public void testIsDirtyBuild() {
        model.setPOM( mock( POM.class ) ); // causes isDirty evaluates as true
        presenter.triggerBuild();

        verify( view, times( 1 ) ).showSaveBeforeContinue( any( Command.class ), any( Command.class ), any( Command.class ) );
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testIsDirtyBuildAndInstall() {
        model.setPOM( mock( POM.class ) ); // causes isDirty evaluates as true
        presenter.triggerBuildAndDeploy();

        verify( view, times( 1 ) ).showSaveBeforeContinue( any( Command.class ), any( Command.class ), any( Command.class ) );
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testIsDirtyBuildAndDeploy() {
        model.setPOM( mock( POM.class ) ); // causes isDirty evaluates as true
        presenter.triggerBuildAndDeploy();

        verify( view, times( 1 ) ).showSaveBeforeContinue(any(Command.class), any(Command.class), any(Command.class));
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testOnDependenciesSelected() throws Exception {

        when( lockManagerInstanceProvider.get() ).thenReturn( mock( LockManager.class ) );

        Path pathToPOM = mock( Path.class );
        model.setPathToPOM( pathToPOM );

        when( view.getDependenciesPart() ).thenReturn( dependenciesPart );

        presenter.onStartup( mock( PlaceRequest.class ) );

        presenter.onDependenciesSelected();

        verify( view ).showDependenciesPanel();
    }

    @Test
    public void testSaveNonClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Command command = presenter.getSaveCommand( DeploymentMode.VALIDATED );
        command.execute();

        verify( projectScreenService,
                times( 1 ) ).save( eq( presenter.pathToPomXML ),
                                   eq( model ),
                                   eq( "" ),
                                   eq( DeploymentMode.VALIDATED ) );
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Saving() ) );
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        doThrow( GAVAlreadyExistsException.class ).when( projectScreenService ).save( presenter.pathToPomXML,
                                                                                      model,
                                                                                      "",
                                                                                      DeploymentMode.VALIDATED );

        final GAV gav = model.getPOM().getGav();
        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );
        final Command command = presenter.getSaveCommand( DeploymentMode.VALIDATED );
        command.execute();

        verify( projectScreenService,
                times( 1 ) ).save( eq( presenter.pathToPomXML ),
                                   eq( model ),
                                   eq( "" ),
                                   eq( DeploymentMode.VALIDATED ) );

        verify( conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( gav ),
                                         any( Set.class ),
                                         commandArgumentCaptor.capture() );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        assertNotNull( commandArgumentCaptor.getValue() );

        //Emulate User electing to force save
        commandArgumentCaptor.getValue().execute();

        verify( projectScreenService,
                times( 1 ) ).save( eq( presenter.pathToPomXML ),
                                   eq( model ),
                                   eq( "" ),
                                   eq( DeploymentMode.FORCED ) );
        //We attempted to save the Project twice
        verify( view,
                times( 2 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Saving() ) );
        //We hid the BusyPopup 1 x loading, 1 x per save attempt
        verify( view,
                times( 3 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildManagedRepository() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put( "managed",
                     true );
            }
        };
        when( repository.getEnvironment() ).thenReturn( env );

        presenter.triggerBuild();

        verify( buildService,
                times( 1 ) ).build( eq( project ) );
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildNotManagedRepositoryNonClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put( "managed",
                     false );
            }
        };
        when( repository.getEnvironment() ).thenReturn( env );

        presenter.triggerBuild();

        verify( buildService,
                times( 1 ) ).build( eq( project ) );
        verify(view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildNotManagedRepositoryClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put( "managed",
                     false );
            }
        };
        when( repository.getEnvironment() ).thenReturn( env );

        doThrow( GAVAlreadyExistsException.class ).when( buildService ).buildAndDeploy( eq( project ),
                                                                                        eq( DeploymentMode.VALIDATED ) );

        final GAV gav = model.getPOM().getGav();
        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );

        presenter.triggerBuildAndDeploy();

        verify( buildService,
                times( 1 ) ).buildAndDeploy( eq( project ),
                eq(DeploymentMode.VALIDATED));

        verify(conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( gav ),
                                         any( Set.class ),
                                         commandArgumentCaptor.capture() );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        assertNotNull( commandArgumentCaptor.getValue() );

        //Emulate User electing to force save
        commandArgumentCaptor.getValue().execute();

        verify( conflictingRepositoriesPopup,
                times( 1 ) ).hide();

        verify( buildService,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( DeploymentMode.FORCED ) );
        //We attempted to build the Project twice
        verify( view,
                times( 2 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        //We hid the BusyPopup 1 x loading, 1 x per build attempt
        verify( view,
                times( 3 ) ).hideBusyIndicator();
    }

    private void verifyBusyShowHideAnyString( int show,
                                              int hide ) {
        //Check the "Busy" popup has not been shown again
        verify( view,
                times( show ) ).showBusyIndicator( any( String.class ) );
        verify( view,
                times( hide ) ).hideBusyIndicator();
    }


    private Caller buildServiceCaller() {
        Caller<BuildService> caller = mock( Caller.class );
        when( caller.call( any( RemoteCallback.class ), any( ErrorCallback.class ) ) ).thenAnswer( new Answer<BuildService>() {
            @Override
            public BuildService answer( InvocationOnMock invocationOnMock ) throws Throwable {
                //not calling callback causes building is still set to true
                return buildService;
            }
        } );

        return caller;
    }

    private void constructProjectScreenPresenter( Caller<BuildService> buildServiceCaller,
                                                  Caller<SpecManagementService> specManagementServiceCaller ) {

        presenter = new ProjectScreenPresenter( view,
                                                context,
                                                new CallerMock<ProjectScreenService>( projectScreenService ),
                                                buildServiceCaller,
                                                mock( EventSourceMock.class ),
                                                notificationEvent,
                                                mock( EventSourceMock.class ),
                                                mock( ProjectNameValidator.class ),
                                                mock( PlaceManager.class ),
                                                mock( BusyIndicatorView.class ),
                                                new CallerMock<ValidationService>( mock( ValidationService.class ) ),
                                                lockManagerInstanceProvider,
                                                mock( EventSourceMock.class ),
                                                conflictingRepositoriesPopup,
                                                specManagementServiceCaller,
                                                deploymentScreenPopupView) {

            @Override
            protected void setupPathToPomXML() {
                //Stub the real implementation that makes direct use of IOC and fails to be mocked
                pathToPomXML = new ObservablePathImpl().wrap( project.getPomXMLPath() );
            }

            @Override
            protected Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>> getBuildExtensions() {
                //Do nothing. This method makes direct use of IOC and fails to be mocked
                return new Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>>( Collections.EMPTY_LIST,
                                                                                                     Collections.EMPTY_LIST );
            }

            @Override
            protected void destroyExtensions( final Collection<BuildOptionExtension> extensions ) {
                //Do nothing. This method makes direct use of IOC and fails to be mocked
            }

            @Override
            SaveOperationService getSaveOperationService() {
                //Stub the real implementation that makes direct use of IOC and fails to be mocked
                return new SaveOperationService() {
                    @Override
                    public void save( final Path path,
                                      final ParameterizedCommand<String> saveCommand ) {
                        saveCommand.execute( "" );
                    }
                };
            }
        };

    }

}
