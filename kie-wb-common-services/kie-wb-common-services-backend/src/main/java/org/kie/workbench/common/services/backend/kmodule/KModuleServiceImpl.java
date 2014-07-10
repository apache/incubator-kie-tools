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

package org.kie.workbench.common.services.backend.kmodule;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.backend.server.KModuleContentHandler;
import org.guvnor.common.services.project.model.KModuleModel;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class KModuleServiceImpl
        implements KModuleService {

    private IOService ioService;
    private ProjectService<KieProject> projectService;
    private MetadataService metadataService;
    private KModuleContentHandler moduleContentHandler;
    private Identity identity;
    private SessionInfo sessionInfo;

    public KModuleServiceImpl() {
        // Weld needs this for proxying.
    }

    @Inject
    public KModuleServiceImpl( final @Named("ioStrategy") IOService ioService,
                               final ProjectService projectService,
                               final MetadataService metadataService,
                               final KModuleContentHandler moduleContentHandler,
                               final Identity identity,
                               final SessionInfo sessionInfo ) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.metadataService = metadataService;
        this.moduleContentHandler = moduleContentHandler;
        this.identity = identity;
        this.sessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @Override
    public boolean isKModule( final Path resource ) {
        try {
            //Null resource paths cannot resolve to a Project
            if ( resource == null ) {
                return false;
            }

            //Check if path equals kmodule.xml
            final KieProject project = projectService.resolveProject(resource);
            //It's possible that the Incremental Build attempts to act on a Project file before the project has been fully created.
            //This should be a short-term issue that will be resolved when saving a project batches pom.xml, kmodule.xml and project.imports
            //etc into a single git-batch. At present they are saved individually leading to multiple Incremental Build requests.
            if ( project == null ) {
                return false;
            }

            final org.uberfire.java.nio.file.Path path = Paths.convert( resource ).normalize();
            final org.uberfire.java.nio.file.Path kmoduleFilePath = Paths.convert( project.getKModuleXMLPath() );
            return path.startsWith( kmoduleFilePath );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path setUpKModuleStructure( final Path projectRoot ) {
        try {
            // Create project structure
            final org.uberfire.java.nio.file.Path nioRoot = Paths.convert( projectRoot );

            ioService.createDirectory( nioRoot.resolve( "src/main/java" ) );
            ioService.createDirectory( nioRoot.resolve( "src/main/resources" ) );
            ioService.createDirectory( nioRoot.resolve( "src/test/java" ) );
            ioService.createDirectory( nioRoot.resolve( "src/test/resources" ) );

            final org.uberfire.java.nio.file.Path pathToKModuleXML = nioRoot.resolve( "src/main/resources/META-INF/kmodule.xml" );
            if ( ioService.exists( pathToKModuleXML ) ) {
                throw new FileAlreadyExistsException( pathToKModuleXML.toString() );
            }

            ioService.write( pathToKModuleXML,
                             moduleContentHandler.toString( new KModuleModel() ) );

            //Don't raise a NewResourceAdded event as this is handled at the Project level in ProjectServices

            return Paths.convert( pathToKModuleXML );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public KModuleModel load( final Path path ) {
        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
            final String content = ioService.readAllString( nioPath );

            return moduleContentHandler.toModel( content );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path save( final Path path,
                      final KModuleModel content,
                      final Metadata metadata,
                      final String comment ) {
        try {
            if ( metadata == null ) {
                ioService.write( Paths.convert( path ),
                                 moduleContentHandler.toString( content ) );
            } else {
                ioService.write(
                        Paths.convert( path ),
                        moduleContentHandler.toString( content ),
                        metadataService.setUpAttributes( path,
                                                         metadata ) );
            }

            //The pom.xml, kmodule.xml and project.imports are all saved from ProjectScreenPresenter
            //We only raise InvalidateDMOProjectCacheEvent and ResourceUpdatedEvent(pom.xml) events once
            //in POMService.save to avoid duplicating events (and re-construction of DMO).

            return path;

        } catch ( Exception e ) {
            e.printStackTrace();
            throw ExceptionUtilities.handleException( e );
        }
    }

}