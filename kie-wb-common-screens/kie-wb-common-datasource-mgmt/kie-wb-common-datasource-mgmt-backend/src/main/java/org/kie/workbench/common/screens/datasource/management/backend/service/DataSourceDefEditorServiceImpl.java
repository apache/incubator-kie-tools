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
import java.sql.Connection;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.backend.core.DeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.backend.core.UnDeploymentOptions;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefInfo;
import org.kie.workbench.common.screens.datasource.management.model.TestResult;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefEditorService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefQueryService;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.kie.workbench.common.screens.datasource.management.util.DataSourceDefSerializer;
import org.kie.workbench.common.screens.datasource.management.util.MavenArtifactResolver;
import org.kie.workbench.common.screens.datasource.management.util.URLConnectionFactory;
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

import static org.kie.workbench.common.screens.datasource.management.util.ServiceUtil.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class DataSourceDefEditorServiceImpl
        implements DataSourceDefEditorService {

    private static final Logger logger = LoggerFactory.getLogger( DataSourceDefEditorServiceImpl.class );

    @Inject
    @Named( "ioStrategy" )
    private IOService ioService;

    @Inject
    private CommentedOptionFactory optionsFactory;

    @Inject
    protected KieProjectService projectService;

    @Inject
    private DataSourceDefQueryService dataSourceDefQueryService;

    @Inject
    private DataSourceRuntimeManager runtimeManager;

    @Inject
    private DataSourceServicesHelper serviceHelper;

    @Inject
    private DriverDefEditorService driverDefService;

    @Inject
    private MavenArtifactResolver artifactResolver;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<NewDataSourceEvent> newDataSourceEvent;

    @Inject
    private Event<DeleteDataSourceEvent> deleteDataSourceEvent;

    @Inject
    private Event<UpdateDataSourceEvent> updateDataSourceEvent;

    public DataSourceDefEditorServiceImpl() {
    }

    @Override
    public DataSourceDefEditorContent loadContent( final Path path ) {

        checkNotNull( "path", path );

        DataSourceDefEditorContent editorContent = new DataSourceDefEditorContent();
        String content = ioService.readAllString( Paths.convert( path ) );
        DataSourceDef dataSourceDef = DataSourceDefSerializer.deserialize( content );
        editorContent.setDataSourceDef( dataSourceDef );
        editorContent.setProject( projectService.resolveProject( path ) );
        return editorContent;
    }

    @Override
    public Path save( final Path path,
            final DataSourceDefEditorContent editorContent,
            final String comment ) {

        checkNotNull( "path", path );
        checkNotNull( "content", editorContent );

        Path newPath = path;
        try {
            final DataSourceDef originalDataSourceDef = DataSourceDefSerializer.deserialize(
                    ioService.readAllString( Paths.convert( path ) ) );
            final String content = DataSourceDefSerializer.serialize( editorContent.getDataSourceDef() );

            DataSourceDeploymentInfo deploymentInfo = runtimeManager.getDataSourceDeploymentInfo(
                    editorContent.getDataSourceDef().getUuid() );
            if ( deploymentInfo != null ) {
                runtimeManager.unDeployDataSource( deploymentInfo, UnDeploymentOptions.forcedUnDeployment() );
            }
            runtimeManager.deployDataSource( editorContent.getDataSourceDef(), DeploymentOptions.create() );

            ioService.write( Paths.convert( path ), content, optionsFactory.makeCommentedOption( comment ) );

            if ( originalDataSourceDef.getName() != null &&
                    !originalDataSourceDef.getName().equals( editorContent.getDataSourceDef().getName() ) ) {
                newPath = renameService.rename( path, editorContent.getDataSourceDef().getName(), comment );
            }

            updateDataSourceEvent.fire( new UpdateDataSourceEvent( editorContent.getDataSourceDef(),
                    editorContent.getProject(),
                    optionsFactory.getSafeSessionId(),
                    optionsFactory.getSafeIdentityName(),
                    originalDataSourceDef ) );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
        return newPath;
    }

    @Override
    public Path create( final DataSourceDef dataSourceDef, final Project project ) {
        checkNotNull( "dataSourceDef", dataSourceDef );
        checkNotNull( "project", project );

        Path context = serviceHelper.getProjectDataSourcesContext( project );
        Path newPath = create( dataSourceDef, context );

        newDataSourceEvent.fire( new NewDataSourceEvent( dataSourceDef,
                project,
                optionsFactory.getSafeSessionId(),
                optionsFactory.getSafeIdentityName() ) );

        return newPath;
    }

    @Override
    public Path createGlobal( DataSourceDef dataSourceDef ) {
        checkNotNull( "dataSourceDef", dataSourceDef );

        Path context = serviceHelper.getGlobalDataSourcesContext();
        Path newPath = create( dataSourceDef, context );

        newDataSourceEvent.fire( new NewDataSourceEvent( dataSourceDef,
                optionsFactory.getSafeSessionId(),
                optionsFactory.getSafeIdentityName() ) );

        return newPath;
    }

    private Path create( final DataSourceDef dataSourceDef, final Path context ) {
        checkNotNull( "dataSourceDef", dataSourceDef );
        checkNotNull( "context", context );

        if ( dataSourceDef.getUuid() == null ) {
            dataSourceDef.setUuid( UUIDGenerator.generateUUID() );
        }

        String fileName = dataSourceDef.getName() + ".datasource";
        String content = DataSourceDefSerializer.serialize( dataSourceDef );

        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( context ).resolve( fileName );
        final Path newPath = Paths.convert( nioPath );
        boolean fileCreated = false;

        if ( ioService.exists( nioPath ) ) {
            throw new FileAlreadyExistsException( nioPath.toString() );
        }

        try {
            ioService.startBatch( nioPath.getFileSystem() );

            //create the datasource file.
            ioService.write( nioPath, content, new CommentedOption( optionsFactory.getSafeIdentityName() ) );
            fileCreated = true;

            runtimeManager.deployDataSource( dataSourceDef, DeploymentOptions.create() );

        } catch ( Exception e1 ) {
            logger.error( "It was not possible to create data source: {}", dataSourceDef.getName(), e1 );
            if ( fileCreated ) {
                //the file was created, but the deployment failed.
                try {
                    ioService.delete( nioPath );
                } catch ( Exception e2 ) {
                    logger.warn( "Removal of orphan data source file failed: {}", newPath, e2 );
                }
            }
            throw ExceptionUtilities.handleException( e1 );
        } finally {
            ioService.endBatch();
        }
        return newPath;
    }

    @Override
    public TestResult testConnection( DataSourceDef dataSourceDef ) {
        return testConnection( dataSourceDef, null );
    }

    @Override
    public TestResult testConnection( DataSourceDef dataSourceDef, Project project ) {

        TestResult result = new TestResult( false );

        if ( isEmpty( dataSourceDef.getConnectionURL() ) ) {
            result.setMessage( "A valid connection url is required" );
            return result;
        }

        if ( isEmpty( dataSourceDef.getUser() ) || isEmpty( dataSourceDef.getPassword() ) ) {
            result.setMessage( "A valid user and password are required" );
            return result;
        }

        DriverDefInfo driverDefInfo = null;
        if ( isEmpty( dataSourceDef.getDriverUuid() ) ) {
            result.setMessage( "A valid driver is required" );
            return result;
        }
        if ( project != null ) {
            driverDefInfo = dataSourceDefQueryService.findProjectDriver( dataSourceDef.getDriverUuid(), project.getRootPath() );
        } else {
            driverDefInfo = dataSourceDefQueryService.findGlobalDriver( dataSourceDef.getDriverUuid() );
        }

        if ( driverDefInfo == null ) {
            result.setMessage( "Data source driver: " + dataSourceDef.getUuid() + " was not found" );
            return result;
        }

        DriverDefEditorContent driverDefEditorContent = driverDefService.loadContent( driverDefInfo.getPath() );
        DriverDef driverDef = driverDefEditorContent.getDriverDef();
        URI uri;

        try {
            uri = artifactResolver.resolve( driverDef.getGroupId(), driverDef.getArtifactId(), driverDef.getVersion() );
        } catch ( Exception e ) {
            result.setMessage( "Connection could not be tested due to the following error: " + e.getMessage() );
            return result;
        }

        if ( uri == null ) {
            result.setMessage( "Driver artifact: " + driverDef.getGroupId() + ":"
                    + driverDef.getArtifactId() + ":" + driverDef.getVersion() + " was not found" );
            return result;
        }

        try {
            Properties properties = new Properties(  );
            properties.put( "user", dataSourceDef.getUser() );
            properties.put("password", dataSourceDef.getPassword() );

            URLConnectionFactory connectionFactory = new URLConnectionFactory( uri.toURL(), driverDef.getDriverClass(),
                    dataSourceDef.getConnectionURL(), properties );

            Connection conn = connectionFactory.createConnection();

            if ( conn == null ) {
                result.setMessage( "It was not possible to open connection" );
            } else {
                StringBuilder stringBuilder = new StringBuilder(  );
                stringBuilder.append( "Connection was successfully obtained: " + conn );
                stringBuilder.append( "\n" );
                stringBuilder.append( "*** DatabaseProductName: " + conn.getMetaData().getDatabaseProductName() );
                stringBuilder.append( "\n" );
                stringBuilder.append( "*** DatabaseProductVersion: " + conn.getMetaData().getDatabaseProductVersion() );
                stringBuilder.append( "\n" );
                stringBuilder.append( "*** DriverName: " + conn.getMetaData().getDriverName() );
                stringBuilder.append( "\n" );
                stringBuilder.append( "*** DriverVersion: " + conn.getMetaData().getDriverVersion() );
                stringBuilder.append( "\n" );
                conn.close();
                stringBuilder.append( "Connection was successfully released." );
                stringBuilder.append( "\n" );

                result.setTestPassed( true );
                result.setMessage( stringBuilder.toString() );
            }

            return result;

        } catch ( Exception e ) {
            result.setMessage( e.getMessage() );
            return result;
        }
    }

    @Override
    public void delete( final Path path, final String comment ) {
        checkNotNull( "path", path );

        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        if ( ioService.exists( nioPath ) ) {
            String content = ioService.readAllString( Paths.convert( path ) );
            DataSourceDef dataSourceDef = DataSourceDefSerializer.deserialize( content );
            Project project = projectService.resolveProject( path );
            try {

                DataSourceDeploymentInfo deploymentInfo = runtimeManager.getDataSourceDeploymentInfo( dataSourceDef.getUuid() );
                if ( deploymentInfo != null ) {
                    runtimeManager.unDeployDataSource( deploymentInfo, UnDeploymentOptions.forcedUnDeployment() );
                }

                ioService.delete( Paths.convert( path ), optionsFactory.makeCommentedOption( comment ) );
                deleteDataSourceEvent.fire( new DeleteDataSourceEvent( dataSourceDef,
                        project, optionsFactory.getSafeSessionId(), optionsFactory.getSafeIdentityName() ) );

            } catch ( Exception e ) {
                throw ExceptionUtilities.handleException( e );
            }
        }
    }
}