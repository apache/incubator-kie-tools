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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDriverEvent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.kie.workbench.common.screens.datasource.management.util.DriverDefSerializer;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.screens.datasource.management.util.UUIDGenerator;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class DriverDefEditorServiceImpl
        implements DriverDefEditorService {

    private static final Logger logger = LoggerFactory.getLogger( DriverDefEditorServiceImpl.class );
    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DataSourceServicesHelper serviceHelper;

    @Inject
    private DataSourceRuntimeManager runtimeManager;

    @Inject
    private KieProjectService projectService;

    @Inject
    private RenameService renameService;

    @Inject
    private CommentedOptionFactory optionsFactory;

    @Inject
    private MavenArtifactResolver artifactResolver;

    @Inject
    private Event<NewDriverEvent> newDriverEvent;

    @Inject
    private Event<DeleteDriverEvent> deleteDriverEvent;

    @Inject
    private Event<UpdateDriverEvent> updateDriverEvent;

    public DriverDefEditorServiceImpl() {
    }

    @Override
    public DriverDefEditorContent loadContent( final Path path ) {

        checkNotNull( "path", path );

        DriverDefEditorContent editorContent = new DriverDefEditorContent();
        String content = ioService.readAllString( Paths.convert( path ) );
        DriverDef driverDef = DriverDefSerializer.deserialize( content );
        editorContent.setDriverDef( driverDef );

        return editorContent;
    }

    @Override
    public Path save( final Path path,
            final DriverDefEditorContent editorContent,
            final String comment ) {

        checkNotNull( "path", path );
        checkNotNull( "content", editorContent );

        Path newPath = path;
        try {
            final Project project = projectService.resolveProject( path );
            final DriverDef originalDriverDef = DriverDefSerializer.deserialize(
                    ioService.readAllString( Paths.convert( path ) ) );
            final String content = DriverDefSerializer.serialize( editorContent.getDriverDef() );

            DriverDeploymentInfo deploymentInfo = runtimeManager.getDriverDeploymentInfo(
                    editorContent.getDriverDef().getUuid() );
            if ( deploymentInfo != null ) {
                runtimeManager.unDeployDriver( deploymentInfo, UnDeploymentOptions.forcedUnDeployment() );
            }
            runtimeManager.deployDriver( editorContent.getDriverDef(), DeploymentOptions.create() );

            ioService.write( Paths.convert( path ), content, optionsFactory.makeCommentedOption( comment ) );

            if ( originalDriverDef.getName() != null &&
                    !originalDriverDef.getName().equals( editorContent.getDriverDef().getName() ) ) {
                newPath = renameService.rename( path, editorContent.getDriverDef().getName(), comment );
            }

            updateDriverEvent.fire( new UpdateDriverEvent( originalDriverDef,
                    editorContent.getDriverDef(),
                    project,
                    optionsFactory.getSafeSessionId(),
                    optionsFactory.getSafeIdentityName() ) );
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
        return newPath;
    }

    @Override
    public Path create( final DriverDef driverDef, final Project project ) {

        checkNotNull( "driverDef", driverDef );
        checkNotNull( "project", project );

        Path context = serviceHelper.getProjectDataSourcesContext( project );
        Path newPath = create( driverDef, context );

        newDriverEvent.fire( new NewDriverEvent( driverDef,
                project,
                optionsFactory.getSafeSessionId(),
                optionsFactory.getSafeIdentityName() ) );

        return newPath;
    }

    @Override
    public Path createGlobal( final DriverDef driverDef ) {

        checkNotNull( "driverDef", driverDef );

        Path context = serviceHelper.getGlobalDataSourcesContext();
        Path newPath = create( driverDef, context );

        newDriverEvent.fire( new NewDriverEvent( driverDef,
                optionsFactory.getSafeSessionId(),
                optionsFactory.getSafeIdentityName() ) );

        return newPath;
    }

    private Path create( final DriverDef driverDef, final Path context ) {

        try {
            validateDriver( driverDef );
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }

        if ( driverDef.getUuid() == null ) {
            driverDef.setUuid( UUIDGenerator.generateUUID() );
        }

        String fileName = driverDef.getName() + ".driver";
        String content = DriverDefSerializer.serialize( driverDef );

        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( context ).resolve( fileName );
        final Path newPath = Paths.convert( nioPath );
        boolean fileCreated = false;

        if ( ioService.exists( nioPath ) ) {
            throw new FileAlreadyExistsException( nioPath.toString() );
        }

        try {
            ioService.startBatch( nioPath.getFileSystem() );
            ioService.write( nioPath, content, new CommentedOption( optionsFactory.getSafeIdentityName() ) );
            fileCreated = true;

            runtimeManager.deployDriver( driverDef, DeploymentOptions.create() );

        } catch ( Exception e1 ) {
            logger.error( "It was not possible to create driver: {}", driverDef.getName(), e1 );
            if ( fileCreated ) {
                try {
                    ioService.delete( nioPath );
                } catch ( Exception e2 ) {
                    logger.warn( "Removal of orphan driver file failed: {}", newPath, e2 );
                }
            }
            throw ExceptionUtilities.handleException( e1 );
        } finally {
            ioService.endBatch();
        }
        return newPath;
    }

    @Override
    public List<ValidationMessage> validate( DriverDef driverDef ) {

        List<ValidationMessage> messages = new ArrayList<>(  );
        ValidationMessage message;
        try {
            validateDriver( driverDef );
        } catch ( Exception e ) {
            message = new ValidationMessage();
            message.setLevel( Level.ERROR );
            message.setText( e.getMessage() );
            messages.add( message );
        }
        return messages;
    }

    private void validateDriver( DriverDef driverDef ) throws Exception {

        final URI uri = artifactResolver.resolve( driverDef.getGroupId(),
                driverDef.getArtifactId(), driverDef.getVersion() );

        if ( uri == null ) {
            throw new Exception( "maven artifact was not found: " + driverDef.getGroupId() + ":"
                    + driverDef.getArtifactId() + ":" + driverDef.getVersion() );
        }

        final URL[] urls = {uri.toURL()};
        final URLClassLoader classLoader = new URLClassLoader( urls );

        try {
            Class driverClass = classLoader.loadClass( driverDef.getDriverClass() );

            if ( !Driver.class.isAssignableFrom( driverClass ) ) {
                throw new Exception( "class: " + driverDef.getDriverClass() + " do not extend from: " + Driver.class.getName() );
            }
        } catch ( ClassNotFoundException e ) {
            throw new Exception( "driver class: " + driverDef.getDriverClass() + " was not found in current gav" );
        }
    }

    @Override
    public void delete( final Path path, final String comment ) {
        checkNotNull( "path", path );

        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        if ( ioService.exists( nioPath ) ) {
            final String content = ioService.readAllString( nioPath );
            DriverDef driverDef = DriverDefSerializer.deserialize( content );
            Project project = projectService.resolveProject( path );

            try {

                DriverDeploymentInfo deploymentInfo = runtimeManager.getDriverDeploymentInfo( driverDef.getUuid() );
                if ( deploymentInfo != null ) {
                    runtimeManager.unDeployDriver( deploymentInfo, UnDeploymentOptions.forcedUnDeployment( ) );
                }

                ioService.delete( Paths.convert( path ), optionsFactory.makeCommentedOption( comment ) );
                deleteDriverEvent.fire( new DeleteDriverEvent( driverDef,
                        project, optionsFactory.getSafeSessionId(), optionsFactory.getSafeIdentityName() ) );
            } catch ( Exception e ) {
                throw ExceptionUtilities.handleException( e );
            }
        }
    }
}