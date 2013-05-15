/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.builder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.guvnor.project.model.ProjectImports;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.workbench.common.services.backend.file.DotFileFilter;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.workbench.common.services.shared.builder.model.BuildMessage;
import org.kie.workbench.common.services.shared.builder.model.BuildResults;
import org.kie.workbench.common.services.shared.builder.model.IncrementalBuildResults;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.client.workbench.widgets.events.ChangeType;
import org.uberfire.client.workbench.widgets.events.ResourceChange;

public class Builder {

    private final static String RESOURCE_PATH = "src/main/resources";

    private final static String ERROR_CLASS_NOT_FOUND = "Class not found";

    private KieBuilder kieBuilder;
    private final KieServices kieServices;
    private final KieFileSystem kieFileSystem;
    private final Path moduleDirectory;
    private final Paths paths;
    private final String artifactId;
    private final IOService ioService;
    private final ProjectService projectService;
    private final DirectoryStream.Filter<Path> filter;

    private final String projectPrefix;

    private Map<String, org.uberfire.backend.vfs.Path> handles = new HashMap<String, org.uberfire.backend.vfs.Path>();
    private KieContainer kieContainer;

    public Builder( final Path moduleDirectory,
                    final String artifactId,
                    final Paths paths,
                    final IOService ioService,
                    final ProjectService projectService ) {
        this( moduleDirectory,
              artifactId,
              paths,
              ioService,
              projectService,
              new DotFileFilter() );
    }

    public Builder( final Path moduleDirectory,
                    final String artifactId,
                    final Paths paths,
                    final IOService ioService,
                    final ProjectService projectService,
                    final DirectoryStream.Filter<Path> filter ) {
        this.moduleDirectory = moduleDirectory;
        this.artifactId = artifactId;
        this.paths = paths;
        this.ioService = ioService;
        this.projectService = projectService;
        this.filter = filter;

        projectPrefix = moduleDirectory.toUri().toString();
        kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();

        DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = Files.newDirectoryStream( moduleDirectory );
        visitPaths( directoryStream );
    }

    public BuildResults build() {
        //KieBuilder is not re-usable for successive "full" builds
        kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        final Results kieResults = kieBuilder.buildAll().getResults();
        final BuildResults results = convertMessages( kieResults );
        if ( !results.getMessages().isEmpty() ) {
            return results;
        }

        kieContainer = kieServices.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        //Check external imports are available. These are loaded when a DMO is requested, but it's better to report them early
        final org.kie.commons.java.nio.file.Path nioExternalImportsPath = moduleDirectory.resolve( "project.imports" );
        if ( Files.exists( nioExternalImportsPath ) ) {
            final org.uberfire.backend.vfs.Path externalImportsPath = paths.convert( nioExternalImportsPath );
            final ProjectImports projectImports = projectService.load( externalImportsPath );
            final Imports imports = projectImports.getImports();
            for ( final Import item : imports.getImports() ) {
                try {
                    Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                } catch ( ClassNotFoundException cnfe ) {
                    results.addBuildMessage( makeMessage( ERROR_CLASS_NOT_FOUND,
                                                          cnfe ) );
                }
            }
        }

        return results;
    }

    public IncrementalBuildResults addResource( final Path resource ) {
        PortablePreconditions.checkNotNull( "resource",
                                            resource );

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }
        //Add new resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        final InputStream is = ioService.newInputStream( resource );
        final BufferedInputStream bis = new BufferedInputStream( is );
        kieFileSystem.write( destinationPath,
                             KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
        handles.put( destinationPath,
                     paths.convert( resource ) );

        //Incremental build
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();

        //Messages from incremental build
        final IncrementalBuildResults results = convertMessages( incrementalResults );

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        return results;
    }

    public IncrementalBuildResults deleteResource( final Path resource ) {
        PortablePreconditions.checkNotNull( "resource",
                                            resource );

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }

        //Delete resource
        final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
        kieFileSystem.delete( destinationPath );

        //Incremental build
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( destinationPath ).build();

        //Messages from incremental build
        final IncrementalBuildResults results = convertMessages( incrementalResults );

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        return results;
    }

    public IncrementalBuildResults updateResource( final Path resource ) {
        return addResource( resource );
    }

    public IncrementalBuildResults applyBatchResourceChanges( final Set<ResourceChange> changes ) {
        PortablePreconditions.checkNotNull( "changes",
                                            changes );

        //Check a full build has been performed
        if ( !isBuilt() ) {
            throw new IllegalStateException( "A full build needs to be performed before any incremental operations." );
        }

        //Add all changes to KieFileSystem before executing the build
        final List<String> changedFilesKieBuilderPaths = new ArrayList<String>();
        for ( ResourceChange change : changes ) {
            final ChangeType type = change.getType();
            final Path resource = paths.convert( change.getPath() );

            PortablePreconditions.checkNotNull( "type",
                                                type );
            PortablePreconditions.checkNotNull( "resource",
                                                resource );

            final String destinationPath = resource.toUri().toString().substring( projectPrefix.length() + 1 );
            switch ( type ) {
                case ADD:
                case UPDATE:
                    //Add/update resource
                    final InputStream is = ioService.newInputStream( resource );
                    final BufferedInputStream bis = new BufferedInputStream( is );
                    kieFileSystem.write( destinationPath,
                                         KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                    handles.put( destinationPath,
                                 paths.convert( resource ) );
                    break;
                case DELETE:
                    //Delete resource
                    kieFileSystem.delete( destinationPath );
                    break;
            }
        }

        //Perform the Incremental build
        final String[] kieBuilderPaths = new String[ changedFilesKieBuilderPaths.size() ];
        changedFilesKieBuilderPaths.toArray( kieBuilderPaths );
        final IncrementalResults incrementalResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( kieBuilderPaths ).build();

        //Messages from incremental build
        final IncrementalBuildResults results = convertMessages( incrementalResults );

        //Tidy-up removed message handles
        for ( Message message : incrementalResults.getRemovedMessages() ) {
            handles.remove( RESOURCE_PATH + "/" + message.getPath() );
        }

        return results;
    }

    public KieModule getKieModule() {
        return kieBuilder.getKieModule();
    }

    public KieModule getKieModuleIgnoringErrors() {
        return ( (InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors();
    }

    public KieContainer getKieContainer() {
        if ( !isBuilt() ) {
            build();
        }
        return kieContainer;
    }

    public boolean isBuilt() {
        return kieBuilder != null;
    }

    private void visitPaths( final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream ) {
        for ( final org.kie.commons.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                visitPaths( Files.newDirectoryStream( path ) );

            } else if ( filter.accept( path ) ) {
                final String destinationPath = path.toUri().toString().substring( projectPrefix.length() + 1 );
                final InputStream is = ioService.newInputStream( path );
                final BufferedInputStream bis = new BufferedInputStream( is );
                kieFileSystem.write( destinationPath,
                                     KieServices.Factory.get().getResources().newInputStreamResource( bis ) );
                handles.put( destinationPath,
                             paths.convert( path ) );
            }
        }
    }

    private BuildResults convertMessages( final Results kieBuildResults ) {
        final BuildResults results = new BuildResults();
        results.setArtifactID( artifactId );

        for ( final Message message : kieBuildResults.getMessages() ) {
            results.addBuildMessage( convertMessage( message ) );
        }

        return results;
    }

    private IncrementalBuildResults convertMessages( final IncrementalResults kieIncrementalResults ) {
        final IncrementalBuildResults results = new IncrementalBuildResults();
        results.setArtifactID( artifactId );

        for ( final Message message : kieIncrementalResults.getAddedMessages() ) {
            results.addAddedMessage( convertMessage( message ) );
        }
        for ( final Message message : kieIncrementalResults.getRemovedMessages() ) {
            results.addRemovedMessage( convertMessage( message ) );
        }

        return results;
    }

    private BuildMessage convertMessage( final Message message ) {
        final BuildMessage m = new BuildMessage();
        switch ( message.getLevel() ) {
            case ERROR:
                m.setLevel( BuildMessage.Level.ERROR );
                break;
            case WARNING:
                m.setLevel( BuildMessage.Level.WARNING );
                break;
            case INFO:
                m.setLevel( BuildMessage.Level.INFO );
                break;
        }

        m.setId( message.getId() );
        m.setArtifactID( artifactId );
        m.setLine( message.getLine() );
        if ( message.getPath() != null && !message.getPath().isEmpty() ) {
            m.setPath( handles.get( RESOURCE_PATH + "/" + message.getPath() ) );
        }
        m.setColumn( message.getColumn() );
        m.setText( message.getText() );
        return m;
    }

    private BuildMessage makeMessage( final String prefix,
                                      final Exception e ) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel( BuildMessage.Level.ERROR );
        buildMessage.setText( prefix + ": " + e.getMessage() );
        return buildMessage;
    }

}
