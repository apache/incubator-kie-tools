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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import java.util.Collection;
import java.util.Collections;

import com.google.gwtmockito.GwtMock;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.projecteditor.client.editor.extension.BuildOptionExtension;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

public abstract class ProjectScreenPresenterTestBase {

    protected ProjectScreenPresenter             presenter;
    @Mock
    protected ProjectScreenView                  view;
    @Mock
    protected User                               user;
    @Mock
    protected CopyPopUpPresenter                 copyPopUpPresenter;
    @Mock
    protected RenamePopUpPresenter               renamePopUpPresenter;
    @Mock
    protected DeletePopUpPresenter               deletePopUpPresenter;
    @Mock
    protected SavePopUpPresenter                 savePopUpPresenter;
    @Mock
    protected ProjectScreenService               projectScreenService;
    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;
    @Mock
    protected ConflictingRepositoriesPopup       conflictingRepositoriesPopup;
    @Mock
    protected DeploymentScreenPopupViewImpl      deploymentScreenPopupView;
    @Spy
    protected ProjectContext           context                     = new ProjectContext();
    @Spy
    protected MockLockManagerInstances lockManagerInstanceProvider = new MockLockManagerInstances();
    @GwtMock
    @SuppressWarnings( "unused" )
    private   ButtonGroup           buildOptions;
    @GwtMock
    @SuppressWarnings( "unused" )
    private   Button                buildOptionsButton1;
    @GwtMock
    @SuppressWarnings( "unused" )
    private   DropDownMenu          buildOptionsMenu;
    @GwtMock
    @SuppressWarnings( "unused" )
    private   AnchorListItem        buildOptionsMenuButton1;
    @Mock
    protected SpecManagementService specManagementServiceMock;
    @Mock
    protected KieProject            project;
    @Mock
    protected BuildService          buildService;
    @Mock
    protected Repository repository;
    @Mock
    protected Path pomPath;

    protected ObservablePath        observablePathToPomXML;

    protected void mockBuildOptions() {
        when( view.getBuildButtons() ).thenReturn( buildOptions );
        when( buildOptions.getWidget( eq( 0 ) ) ).thenReturn( buildOptionsButton1 );
        when( buildOptions.getWidget( eq( 1 ) ) ).thenReturn( buildOptionsMenu );
        when( buildOptionsMenu.getWidget( eq( 0 ) ) ).thenReturn( buildOptionsMenuButton1 );
        when( buildOptionsMenu.getWidget( eq( 1 ) ) ).thenReturn( buildOptionsMenuButton1 );
    }

    protected POM mockProjectScreenService( final ProjectScreenModel model ) {
        final POM pom = new POM( new GAV( "groupId",
                                          "artifactId",
                                          "version" ) );
        model.setPOM( pom );
        when( projectScreenService.load( any( Path.class ) ) ).thenReturn( model );
        return pom;
    }

    protected void mockBuildService( final BuildService buildService ) {
        when( buildService.build( any( KieProject.class ) ) ).thenReturn( new BuildResults() );
        when( buildService.buildAndDeploy( any( KieProject.class ),
                                           any( DeploymentMode.class ) ) ).thenReturn( new BuildResults() );
    }

    protected void mockLockManager( final ProjectScreenModel model ) {
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
    }

    protected void mockProjectContext( final POM pom,
                                       final Repository repository,
                                       final KieProject project,
                                       final Path pomPath ) {
        when( context.getActiveRepository() ).thenReturn( repository );
        when( context.getActiveBranch() ).thenReturn( "master" );
        when( repository.getAlias() ).thenReturn( "repository" );

        when( project.getProjectName() ).thenReturn( "project" );
        when( project.getPomXMLPath() ).thenReturn( pomPath );
        when( project.getPom() ).thenReturn( pom );
        when( pomPath.getFileName() ).thenReturn( "pom.xml" );
        when( context.getActiveProject() ).thenReturn( project );
    }

    protected void constructProjectScreenPresenter( final KieProject project,
                                                    final Caller<BuildService> buildServiceCaller,
                                                    final Caller<SpecManagementService> specManagementServiceCaller ) {
        presenter = new ProjectScreenPresenter( view,
                                                context,
                                                new CallerMock<ProjectScreenService>( projectScreenService ),
                                                buildServiceCaller,
                                                user,
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
                                                deploymentScreenPopupView,
                                                copyPopUpPresenter,
                                                renamePopUpPresenter,
                                                deletePopUpPresenter,
                                                savePopUpPresenter ) {

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
            protected void setupPathToPomXML() {
                //Stub the real implementation that makes direct use of IOC and fails to be mocked
                observablePathToPomXML = new ObservablePathImpl().wrap( project.getPomXMLPath() );
                pathToPomXML = observablePathToPomXML;
            }
        };

    }
}
