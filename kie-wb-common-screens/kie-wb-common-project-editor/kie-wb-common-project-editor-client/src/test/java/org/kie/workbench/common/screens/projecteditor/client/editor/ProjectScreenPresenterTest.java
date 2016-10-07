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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectScreenPresenterTest
        extends ProjectScreenPresenterTestBase {

    @GwtMock
    @SuppressWarnings("unused")
    private com.google.gwt.user.client.ui.Widget dependenciesPart;

    private ProjectScreenModel model;

    @Before
    public void setup() {
        ApplicationPreferences.setUp( new HashMap<String, String>() );

        //The BuildOptions widget is manipulated in the Presenter so we need some nasty mocking
        mockBuildOptions();

        constructProjectScreenPresenter( project,
                                         new CallerMock<BuildService>( buildService ),
                                         new CallerMock<SpecManagementService>( specManagementServiceMock ) );

        //Mock ProjectScreenService
        model = new ProjectScreenModel();
        final POM pom = mockProjectScreenService( model );

        //Mock BuildService
        mockBuildService( buildService );

        //Mock LockManager initialisation
        mockLockManager( model );

        //Mock ProjectContext
        mockProjectContext( pom,
                            repository,
                            project,
                            pomPath );

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
        constructProjectScreenPresenter( project,
                                         buildServiceCaller(),
                                         new CallerMock<SpecManagementService>( specManagementServiceMock ) );

        presenter.triggerBuild();
        presenter.triggerBuild();

        verify( view, times( 1 ) ).showABuildIsAlreadyRunning();
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 2, 1 );
    }

    @Test
    public void testAlreadyRunningBuildAndDeploy() {
        constructProjectScreenPresenter( project,
                                         buildServiceCaller(),
                                         new CallerMock<SpecManagementService>( specManagementServiceMock ) );
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
        savePopUpPresenterShowMock();

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
        savePopUpPresenterShowMock();

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

    private void savePopUpPresenterShowMock() {
        Answer fakeShow = invocation -> {
            ParameterizedCommand<String> cmd = (ParameterizedCommand<String>) invocation.getArguments()[ 1 ];
            cmd.execute( "" );
            return null;
        };
        doAnswer( fakeShow ).when( savePopUpPresenter ).show( any( Path.class ), any( ParameterizedCommand.class ) );
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

    @Test
    public void testGetReimportCommand() throws Exception {
        Command reImportCommand = presenter.getReImportCommand();

        reImportCommand.execute();

        verify( projectScreenService, times( 1 ) ).reImport( eq( presenter.pathToPomXML ) );
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
}
