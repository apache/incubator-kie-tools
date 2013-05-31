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

package org.drools.workbench.screens.factmodel.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.screens.factmodel.backend.server.util.FactModelPersistence;
import org.drools.workbench.screens.factmodel.model.FactMetaModel;
import org.drools.workbench.screens.factmodel.model.FactModelContent;
import org.drools.workbench.screens.factmodel.model.FactModels;
import org.drools.workbench.screens.factmodel.service.FactModelService;
import org.drools.workbench.screens.factmodel.type.FactModelResourceTypeDefinition;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.workbench.common.services.backend.SourceServices;
import org.kie.workbench.common.services.backend.exceptions.ExceptionUtilities;
import org.kie.workbench.common.services.backend.file.FileDiscoveryService;
import org.kie.workbench.common.services.backend.file.FileExtensionFilter;
import org.kie.workbench.common.services.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.service.DataModelService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.file.CopyService;
import org.kie.workbench.common.services.shared.file.DeleteService;
import org.kie.workbench.common.services.shared.file.RenameService;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.services.shared.validation.model.BuilderResult;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 *
 */
@Service
@ApplicationScoped
public class FactModelServiceImpl implements FactModelService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private ProjectService projectService;

    @Inject
    private FactModelResourceTypeDefinition typeDefinition;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final FactModels content,
                        final String comment ) {
        try {
            content.setPackageName( projectService.resolvePackageName( context ) );

            final org.kie.commons.java.nio.file.Path nioPath = paths.convert( context ).resolve( fileName );
            final Path newPath = paths.convert( nioPath,
                                                false );

            ioService.createFile( nioPath );
            ioService.write( nioPath,
                             FactModelPersistence.marshal( content ),
                             makeCommentedOption( comment ) );

            //Signal creation to interested parties
            resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

            return newPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public FactModels load( final Path path ) {
        try {
            final String content = ioService.readAllString( paths.convert( path ) );

            //Signal opening to interested parties
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

            return FactModelPersistence.unmarshal( content );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public FactModelContent loadContent( final Path path ) {
        try {
            final FactModels factModels = load( path );
            final List<FactMetaModel> allAvailableTypes = loadAllAvailableTypes( path );
            allAvailableTypes.addAll( factModels.getModels() );
            final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
            return new FactModelContent( factModels,
                                         allAvailableTypes,
                                         oracle );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private List<FactMetaModel> loadAllAvailableTypes( final Path path ) {
        final List<FactMetaModel> allAvailableTypes = new ArrayList<FactMetaModel>();
        final Path projectRoot = projectService.resolveProject( path );
        if ( projectRoot == null ) {
            return allAvailableTypes;
        }
        final org.kie.commons.java.nio.file.Path nioProjectRoot = paths.convert( projectRoot );
        final org.kie.commons.java.nio.file.Path nioSrcRoot = nioProjectRoot.resolve( "src/main/resources" );
        final Collection<org.kie.commons.java.nio.file.Path> modelNioPaths = fileDiscoveryService.discoverFiles( nioSrcRoot,
                                                                                                                 new FileExtensionFilter( typeDefinition.getSuffix() ),
                                                                                                                 true );
        for ( org.kie.commons.java.nio.file.Path modelNioPath : modelNioPaths ) {
            final Path modelPath = paths.convert( modelNioPath );
            if ( !modelPath.equals( path ) ) {
                final List<FactMetaModel> model = load( modelPath ).getModels();
                allAvailableTypes.addAll( model );
            }
        }

        return allAvailableTypes;
    }

    @Override
    public Path save( final Path resource,
                      final FactModels content,
                      final Metadata metadata,
                      final String comment ) {
        try {
            content.setPackageName( projectService.resolvePackageName( resource ) );

            ioService.write( paths.convert( resource ),
                             FactModelPersistence.marshal( content ),
                             metadataService.setUpAttributes( resource,
                                                              metadata ),
                             makeCommentedOption( comment ) );

            //Invalidate Project-level DMO cache as Model has changed.
            invalidateDMOProjectCache.fire( new InvalidateDMOProjectCacheEvent( resource ) );

            //Signal update to interested parties
            resourceUpdatedEvent.fire( new ResourceUpdatedEvent( resource ) );

            return resource;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        try {
            deleteService.delete( path,
                                  comment );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        try {
            return renameService.rename( path,
                                         newName,
                                         comment );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        try {
            return copyService.copy( path,
                                     newName,
                                     comment );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public String toSource( final Path path,
                            final FactModels model ) {
        try {
            return sourceServices.getServiceFor( paths.convert( path ) ).getSource( paths.convert( path ),
                                                                                    FactModelPersistence.marshal( model ) );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final FactModels content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( Path path,
                            FactModels content ) {
        return !validate( path, content ).hasLines();
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

}
