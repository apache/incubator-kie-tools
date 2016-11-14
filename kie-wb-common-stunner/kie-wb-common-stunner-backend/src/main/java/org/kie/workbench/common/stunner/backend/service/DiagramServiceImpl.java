/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.StandardDeleteOption;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

@Service
public class DiagramServiceImpl
        extends AbstractVFSDiagramService<Diagram>
        implements DiagramService {

    private static final Logger LOG =
            LoggerFactory.getLogger( DiagramServiceImpl.class.getName() );

    private static final String METADATA_EXTENSION = "meta";
    private static final String VFS_ROOT_PATH = "default://stunner";
    private static final String VFS_DIAGRAMS_PATH = "diagrams";
    private static final String APP_DIAGRAMS_PATH = "WEB-INF/diagrams";

    private FileSystem fileSystem;
    private org.uberfire.java.nio.file.Path root;

    protected DiagramServiceImpl() {
        this( null, null, null, null, null );
    }

    @Inject
    public DiagramServiceImpl( final DefinitionManager definitionManager,
                               final FactoryManager factoryManager,
                               final Instance<DefinitionSetService> definitionSetServiceInstances,
                               final @Named( "ioStrategy" ) IOService ioService,
                               final BackendRegistryFactory registryFactory ) {
        super( definitionManager, factoryManager, definitionSetServiceInstances, ioService, registryFactory );
    }

    @PostConstruct
    public void init() {
        // Initialize caches.
        super.initialize();
        // Initialize the application's VFS.
        initFileSystem();
        // Register packaged diagrams into VFS.
        registerAppDefinitions();
        // Load vfs diagrams and put into the parent registry.
        final Collection<Diagram> diagrams = getAllDiagrams();
        if ( null != diagrams ) {
            diagrams.forEach( diagram -> getRegistry().register( diagram ) );
        }
    }

    @Override
    public Path create( Path path, String name, String defSetId ) {
        return super.create( path, name, defSetId, buildMetadataInstance( path, defSetId, name ) );
    }

    @Override
    protected Metadata buildMetadataInstance( org.uberfire.backend.vfs.Path path, String defSetId, String title ) {
        return  new MetadataImpl.MetadataImplBuilder( defSetId, getDefinitionManager() )
                .setPath( path )
                .setTitle( title )
                .build();
    }

    @Override
    protected InputStream loadMetadataForPath( Path path ) {
        return doLoadMetadataStreamByDiagramPath( path );
    }

    @Override
    protected Class<? extends Metadata> getMetadataType() {
        return Metadata.class;
    }

    @Override
    protected void doSave( Diagram diagram, String raw, String metadata ) {
        try {
            getIoService().startBatch( fileSystem );
            final Path _path = diagram.getMetadata().getPath();
            final org.uberfire.java.nio.file.Path path = Paths.convert( _path );
            // Serialize the diagram's raw data.
            LOG.debug( "Serializing raw data: " + raw );
            getIoService().write( path, raw );
            final String metadataFileName = getMetadataFileName( _path.getFileName() );
            final org.uberfire.java.nio.file.Path metadataPath =
                    getDiagramsPath().resolve( metadataFileName );
            LOG.debug( "Serializing raw metadadata: " + metadata );
            getIoService().write( metadataPath, metadata );

        } catch ( Exception e ) {
            LOG.error( "Error serializing diagram with UUID [" + diagram.getName() + "].", e );
        } finally {
            getIoService().endBatch();
        }
    }

    @Override
    protected boolean doDelete( Path _path ) {
        final org.uberfire.java.nio.file.Path path = Paths.convert( _path );
        if ( getIoService().exists( path ) ) {
            getIoService().startBatch( fileSystem );
            try {
                getIoService().deleteIfExists( path, StandardDeleteOption.NON_EMPTY_DIRECTORIES );
            } catch ( Exception e ) {
                LOG.error( "Error deleting diagram for path [" + path + "].", e );
                return false;
            } finally {
                getIoService().endBatch();
            }
        }
        return true;
    }

    private InputStream doLoadMetadataStreamByDiagramPath( final Path dPath ) {
        org.uberfire.java.nio.file.Path path = getDiagramsPath().resolve( getMetadataFileName( dPath.getFileName() ) );
        if ( null != path ) {
            try {
                return loadPath( path );
            } catch ( Exception e ) {
                LOG.warn( "Cannot load metadata for [" + dPath.toString() + "].", e );
            }
        }
        return null;
    }

    private String getMetadataFileName( final String uri ) {
        return uri + "." + METADATA_EXTENSION;
    }

    private Collection<Diagram> getAllDiagrams() {
        return getDiagramsByPath( root );
    }

    private void registerAppDefinitions() {
        deployAppDiagrams( APP_DIAGRAMS_PATH );
    }

    private void initFileSystem() {
        try {
            fileSystem = getIoService().newFileSystem( URI.create( VFS_ROOT_PATH ),
                    new HashMap<String, Object>() {{
                        put( "init", Boolean.TRUE );
                        put( "internal", Boolean.TRUE );
                    }} );
        } catch ( FileSystemAlreadyExistsException e ) {
            fileSystem = getIoService().getFileSystem( URI.create( VFS_ROOT_PATH ) );
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    private void deployAppDiagrams( String path ) {
        ServletContext servletContext = RpcContext.getServletRequest().getServletContext();
        if ( null != servletContext ) {
            String dir = servletContext.getRealPath( path );
            if ( dir != null && new File( dir ).exists() ) {
                dir = dir.replaceAll( "\\\\", "/" );
                findAndDeployDiagrams( dir );
            }

        } else {
            LOG.warn( "No servlet context available. Cannot deploy the application diagrams." );

        }

    }

    private void findAndDeployDiagrams( String directory ) {
        if ( !StringUtils.isBlank( directory ) ) {
            // Look for data sets deploy
            File[] files = new File( directory ).listFiles( _deployFilter );
            if ( files != null ) {
                for ( File f : files ) {
                    try {
                        String name = f.getName();
                        if ( isFileNameAccepted( name ) ) {
                            // Register it into VFS storage.
                            registerIntoVFS( f );
                        }

                    } catch ( Exception e ) {
                        LOG.error( "Error loading the application default diagrams.", e );

                    }
                }
            }
        }

    }

    private void registerIntoVFS( File file ) {
        String name = file.getName();
        org.uberfire.java.nio.file.Path actualPath = getDiagramsPath().resolve( name );
        boolean exists = getIoService().exists( actualPath );
        if ( !exists ) {
            getIoService().startBatch( fileSystem );
            try {
                String content = FileUtils.readFileToString( file );
                org.uberfire.java.nio.file.Path diagramPath = getDiagramsPath().resolve( file.getName() );
                getIoService().write( diagramPath, content );

            } catch ( Exception e ) {
                LOG.error( "Error registering diagram into app's VFS", e );

            } finally {
                getIoService().endBatch();

            }

        } else {
            LOG.warn( "Diagram [" + name + "] already exists on VFS storage. This file should not be longer present here." );

        }

    }

    public org.uberfire.java.nio.file.Path getDiagramsPath() {
        return root.resolve( VFS_DIAGRAMS_PATH );
    }

    private boolean isFileNameAccepted( String name ) {
        if ( name != null && name.trim().length() > 0 ) {
            return getExtensionsAccepted().stream().anyMatch( s -> name.endsWith( "." + s ) );
        }
        return null != name && name.endsWith( "." + METADATA_EXTENSION );
    }

    private FilenameFilter _deployFilter = ( dir, name ) -> true;

}