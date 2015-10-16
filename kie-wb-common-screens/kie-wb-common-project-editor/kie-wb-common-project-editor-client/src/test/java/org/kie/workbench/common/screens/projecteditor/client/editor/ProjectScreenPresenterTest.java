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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.editor.extension.BuildOptionExtension;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentMatcher;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

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

    private ProjectScreenView view;
    private ProjectContext context;
    private ProjectScreenService projectScreenService = mock( ProjectScreenService.class );
    private BuildService buildService = mock( BuildService.class );
    private Event<NotificationEvent> notificationEvent = mock( EventSourceMock.class );

    private ProjectScreenPresenter presenter;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        ApplicationPreferences.setUp( new HashMap<String, String>() );

        view = mock( ProjectScreenView.class );

        //The BuildOptions widget is manipulated in the Presenter so we need some nasty mocking
        when( view.getBuildOptionsButton() ).thenReturn( buildOptions );
        when( buildOptions.getWidget( eq( 0 ) ) ).thenReturn( buildOptionsButton1 );
        when( buildOptions.getWidget( eq( 1 ) ) ).thenReturn( buildOptionsMenu );
        when( buildOptionsMenu.getWidget( eq( 0 ) ) ).thenReturn( buildOptionsMenuButton1 );
        when( buildOptionsMenu.getWidget( eq( 1 ) ) ).thenReturn( buildOptionsMenuButton1 );

        context = spy( new ProjectContext() );
        presenter = new ProjectScreenPresenter( view,
                                                context,
                                                new CallerMock<ProjectScreenService>( projectScreenService ),
                                                new CallerMock<BuildService>( buildService ),
                                                mock( EventSourceMock.class ),
                                                notificationEvent,
                                                mock( EventSourceMock.class ),
                                                mock( ProjectNameValidator.class ),
                                                mock( PlaceManager.class ),
                                                mock( BusyIndicatorView.class ),
                                                mock( KieWorkbenchACL.class ),
                                                new CallerMock<AssetManagementService>( mock( AssetManagementService.class ) ),
                                                new MockLockManagerInstances(),
                                                mock( EventSourceMock.class ) ) {

            @Override
            protected void setupPathToPomXML() {
                //Do nothing. This method makes direct use of IOC and fails to be mocked
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

        };

        //Mock ProjectScreenService
        final POM pom = new POM( new GAV( "groupId",
                                          "artifactId",
                                          "version" ) );
        final ProjectScreenModel model = mock( ProjectScreenModel.class );
        when( model.getPOM() ).thenReturn( pom );
        when( projectScreenService.load( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( model );

        //Mock BuildService
        when( buildService.buildAndDeploy( any( Project.class ) ) ).thenReturn( new BuildResults() );

        //Mock LockManager initialisation
        final Path path = mock( Path.class );
        final Metadata pomMetadata = mock( Metadata.class );
        when( model.getPOMMetaData() ).thenReturn( pomMetadata );
        when( pomMetadata.getPath() ).thenReturn( path );
        final Metadata kmoduleMetadata = mock( Metadata.class );
        when( model.getKModuleMetaData() ).thenReturn( kmoduleMetadata );
        when( kmoduleMetadata.getPath() ).thenReturn( path );
        final Metadata importsMetadata = mock( Metadata.class );
        when( model.getProjectImportsMetaData() ).thenReturn( importsMetadata );
        when( importsMetadata.getPath() ).thenReturn( path );

        //Mock ProjectContext
        final Repository repository = mock( Repository.class );
        when( context.getActiveRepository() ).thenReturn( repository );
        when( repository.getAlias() ).thenReturn( "repository" );
        when( repository.getCurrentBranch() ).thenReturn( "master" );

        final Project project = mock( Project.class );
        when( context.getActiveProject() ).thenReturn( project );
        when( project.getProjectName() ).thenReturn( "project" );

        //Trigger initialisation of view. Unfortunately this is the only way to initialise a Project in the Presenter
        context.onProjectContextChanged( new ProjectContextChangeEvent( mock( OrganizationalUnit.class ),
                                                                        repository,
                                                                        project ) );

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
    public void testBuildAndInstallCommand() {
        presenter.triggerBuildAndInstall();

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildProcessStarted() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );

        verify( notificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );

        //Check the "Busy" popup has not been shown again
        verify( view,
                times( 1 ) ).showBusyIndicator( any( String.class ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildAndDeployCommand() {
        presenter.triggerBuildAndDeploy( "user",
                                         "password",
                                         "url" );

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildProcessStarted() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );

        verify( notificationEvent,
                times( 1 ) ).fire( any( NotificationEvent.class ) );

        //Check the "Busy" popup has not been shown again
        verify( view,
                times( 1 ) ).showBusyIndicator( any( String.class ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();
    }

}
