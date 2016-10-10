/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.diagram;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.errai.bus.server.api.RpcContext;
import org.kie.workbench.common.stunner.backend.definition.marshall.DefaultDiagramMarshaller;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.DiagramManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.annotation.Application;
import org.kie.workbench.common.stunner.core.backend.annotation.VFS;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetServices;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.marshall.DiagramMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.*;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

@ApplicationScoped
@VFS
public class VFSDiagramManagerImpl implements DiagramManager<Diagram> {

    private static final Logger LOG = LoggerFactory.getLogger( VFSDiagramManagerImpl.class );
    private static final String VFS_ROOT_PATH = "default://stunner";
    private static final String VFS_DIAGRAMS_PATH = "diagrams";
    private static final String APP_DIAGRAMS_PATH = "WEB-INF/diagrams";
    private static final String THUMBS_EXTENSION = "thumb";

    private final DefaultDefinitionSetServices DEFAULT_SERVICES = new DefaultDefinitionSetServices();

    @Inject
    DefinitionManager definitionManager;

    @Inject
    @Application
    FactoryManager factoryManager;

    @Inject
    Instance<DefinitionSetServices> definitionSetServiceInstances;

    @Inject
    DefaultDiagramMarshaller defaultDiagramMarshaller;

    @Inject
    @Named( "ioStrategy" )
    IOService ioService;

    private FileSystem fileSystem;
    private Path root;

    private final Collection<DefinitionSetServices> definitionSetServices = new LinkedList<>();

    @PostConstruct
    public void init() {
        for ( DefinitionSetServices definitionSetService : definitionSetServiceInstances ) {
            definitionSetServices.add( definitionSetService );
        }
        // Initialize the application's VFS.
        initFileSystem();
        // Register packaged diagrams into VFS.
        registerAppDefinitions();

    }

    public void registerAppDefinitions() {
        deployAppDiagrams( APP_DIAGRAMS_PATH );
        deployAppDiagramThumbs( APP_DIAGRAMS_PATH );

    }

    @Override
    public void update( Diagram diagram ) {
        save( diagram );

    }

    @Override
    public void register( Diagram diagram ) {
        save( diagram );

    }

    @Override
    public boolean contains( Diagram diagram ) {
        String path = getDiagramFileName( diagram );
        return getDiagramByUUID( path ) != null;

    }

    @Override
    public Diagram getDiagramByUUID( String diagramPath ) {
        try {
            return doLoad( diagramPath );

        } catch ( Exception e ) {
            LOG.error( "Error during diagram load operation.", e );
            throw e;

        }

    }

    @Override
    public Collection<Diagram> getItems() {
        try {
            final Collection<Diagram> result = new ArrayList<Diagram>();
            if ( ioService.exists( root ) ) {
                walkFileTree( checkNotNull( "root", root ),
                        new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile( final Path file, final BasicFileAttributes attrs ) throws IOException {
                                checkNotNull( "file", file );
                                checkNotNull( "attrs", attrs );
                                String name = file.getFileName().toString();
                                if ( isAccepted( name ) ) {
                                    // TODO: Do not load & process the whole bpmn file. Just the necessary to build the
                                    // portable diagram representation.
                                    Diagram diagram = doLoad( file );
                                    if ( null != diagram ) {
                                        result.add( diagram );
                                    }

                                }
                                return FileVisitResult.CONTINUE;
                            }
                        } );
            }
            return result;

        } catch ( Exception e ) {
            LOG.error( "Error while obtaining diagrams.", e );
            throw e;
        }
    }

    @Override
    public boolean remove( Diagram diagram ) {
        // TODO
        return false;
    }

    @Override
    public void clear() {
        // TODO
    }

    private void save( Diagram diagram ) {
        try {
            final String uuid = diagram.getUUID();
            DefinitionSetServices services = getServicesById( diagram.getSettings().getDefinitionSetId() );
            if ( null == services ) {
                throw new RuntimeException( "No service for diagram with UUID [" + uuid + "]" );

            } else {
                String diagramPath = getDiagramFileName( diagram );
                try {
                    // ********** Save the diagram **********************++
                    LOG.debug( "Saving diagram with UUID [" + uuid + "] using path [" + diagramPath + "]" );
                    ioService.startBatch( fileSystem );
                    DiagramMarshaller<Diagram, InputStream, String> marshaller = services.getDiagramMarshaller();
                    String result = marshaller.marshall( diagram );
                    LOG.debug( "Serialized diagram: " + result );
                    Path defPath = getDiagramsPath().resolve( diagramPath );
                    ioService.write( defPath, result );
                    try {
                        String thumbData = diagram.getSettings().getThumbData();
                        if ( null != thumbData ) {
                            final String thumbPath = getThumbFileName( diagramPath );
                            LOG.debug( "Saving thumbnail for diagram with UUID [" + uuid + "] into path [" + thumbPath + "]" );
                            Path defPath1 = getThumbsPath().resolve( thumbPath );
                            ioService.write( defPath1, thumbData );

                        } else {
                            // TODO: Remove current thumb file on VFS, if any.
                        }

                    } catch ( Exception e ) {
                        LOG.error( "Error while saving diagram thumbnail image data.", e );

                    }

                } catch ( Exception e ) {
                    LOG.error( "Error while saving diagram.", e );

                } finally {
                    ioService.endBatch();
                }

            }

        } catch ( Exception e ) {
            LOG.error( "Error while saving diagram.", e );
            throw e;

        }

    }

    private Diagram doLoad( final String fileName ) {
        Path path = getDiagramsPath().resolve( fileName );
        return doLoad( path );

    }

    private Diagram doLoad( final Path file ) {
        final String fileName = file.getFileName().toString();
        // Obtain the concrete definition set service for this kind of diagram.
        DefinitionSetServices services = getServicesByPath( fileName );
        if ( null != services ) {
            // Parse and load the diagram.
            final byte[] bytes = ioService.readAllBytes( file );
            final InputStream is = new ByteArrayInputStream( bytes );
            return doLoad( fileName, services, is );
        }
        throw new UnsupportedOperationException( "Diagram format not supported [" + file.getFileName().toString() + "]" );

    }

    @SuppressWarnings( "unchecked" )
    private Diagram doLoad( String fileName, DefinitionSetServices services, InputStream is ) {
        if ( null != services ) {
            // Parse and load the diagram.
            final DiagramMarshaller marshaller = services.getDiagramMarshaller();
            Diagram diagram = null;
            try {
                diagram = marshaller.unmarhsall( is );
            } catch ( java.io.IOException e ) {
                LOG.error( "Error loading the diagram for file [" + fileName + "]", e );
            }
            if ( null != diagram ) {
                diagram.getSettings().setVFSPath( fileName );
                // Diagram thumbnail, if any.
                setThumbData( diagram );
                return diagram;

            }

        }
        return null;

    }

    private void setThumbData( final Diagram diagram ) {
        String thumbData = loadThumbData( diagram );
        if ( null != thumbData ) {
            diagram.getSettings().setThumbData( thumbData );

        }

    }

    private String loadThumbData( final Diagram diagram ) {
        if ( null != diagram ) {
            final String thumbFileName = getThumbFileName( diagram );
            return doLoadThumbData( thumbFileName );
        }
        return null;
    }

    private String doLoadThumbData( final String fileName ) {
        Path path = null;
        try {
            path = getThumbsPath().resolve( fileName );

        } catch ( final InvalidPathException e ) {
            return null;

        }
        if ( null != path ) {
            return doLoadThumbData( path );

        }
        return null;
    }

    private String doLoadThumbData( final Path file ) {
        try {
            final byte[] bytes = ioService.readAllBytes( file );
            final InputStream is = new ByteArrayInputStream( bytes );
            return read( is );

        } catch ( java.io.IOException e ) {
            LOG.error( "Error loading diagram thumbnail.", e );

        } catch ( org.uberfire.java.nio.file.NoSuchFileException noFileException ) {
            LOG.debug( "No thumb found." );

        }
        return null;

    }

    private String getThumbFileName( final Diagram diagram ) {
        final String path = getDiagramFileName( diagram );
        return getThumbFileName( path );

    }

    private String getThumbFileName( final String diagramPath ) {
        return diagramPath + "." + THUMBS_EXTENSION;

    }

    private static String read( InputStream input ) throws IOException, java.io.IOException {
        try ( BufferedReader buffer = new BufferedReader( new InputStreamReader( input ) ) ) {
            return buffer.lines().collect( Collectors.joining( "\n" ) );
        }
    }

    protected void initFileSystem() {
        try {
            fileSystem = ioService.newFileSystem( URI.create( VFS_ROOT_PATH ),
                    new HashMap<String, Object>() {{
                        put( "init", Boolean.TRUE );
                        put( "internal", Boolean.TRUE );
                    }} );
        } catch ( FileSystemAlreadyExistsException e ) {
            fileSystem = ioService.getFileSystem( URI.create( VFS_ROOT_PATH ) );
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

    private void deployAppDiagramThumbs( String path ) {
        ServletContext servletContext = RpcContext.getServletRequest().getServletContext();
        if ( null != servletContext ) {
            String dir = servletContext.getRealPath( path );
            if ( dir != null && new File( dir ).exists() ) {
                dir = dir.replaceAll( "\\\\", "/" );
                findAndDeployDiagramThumbnails( dir );
            }

        } else {
            LOG.warn( "No servlet context available. Cannot deploy the application diagram thumbnails." );

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
                        if ( isAccepted( name ) ) {
                            // Register it into VFS storage.
                            registerIntoVFS( f );
                            // Delete file after added into app's vfs.
                            f.delete();

                        }

                    } catch ( Exception e ) {
                        LOG.error( "Error loading the application default diagrams.", e );

                    }
                }
            }
        }

    }

    private void findAndDeployDiagramThumbnails( String directory ) {
        if ( !StringUtils.isBlank( directory ) ) {
            // Look for data sets deploy
            File[] files = new File( directory ).listFiles( _deployFilter );
            if ( files != null ) {
                for ( File f : files ) {
                    try {
                        // Register it into VFS storage.
                        registerIntoVFS( f );
                        // Delete file after added into app's vfs.
                        f.delete();

                    } catch ( Exception e ) {
                        LOG.error( "Error loading the application default diagrams.", e );

                    }
                }
            }
        }

    }

    private void registerIntoVFS( File file ) {
        String name = file.getName();
        Path actualPath = getDiagramsPath().resolve( name );
        boolean exists = ioService.exists( actualPath );
        if ( !exists ) {
            ioService.startBatch( fileSystem );
            try {
                String content = FileUtils.readFileToString( file );
                Path diagramPath = getDiagramsPath().resolve( file.getName() );
                ioService.write( diagramPath, content );

            } catch ( Exception e ) {
                LOG.error( "Error registering diagram into app's VFS", e );

            } finally {
                ioService.endBatch();

            }

        } else {
            LOG.warn( "Diagram [" + name + "] already exists on VFS storage. This file should not be longer present here." );

        }

    }

    FilenameFilter _deployFilter = ( dir, name ) -> true;

    protected Path getDiagramsPath() {
        return root.resolve( VFS_DIAGRAMS_PATH );
    }

    protected Path getThumbsPath() {
        return getDiagramsPath();
    }

    protected Path getTempPath() {
        return root.resolve( "tmp" );
    }

    protected Path resolveTempPath( String fileName ) {
        return getTempPath().resolve( fileName );
    }

    protected org.uberfire.backend.vfs.Path convert( Path path ) {
        return Paths.convert( path );
    }

    protected Path convert( org.uberfire.backend.vfs.Path path ) {
        return Paths.convert( path );
    }

    protected InputStream getInputStream( final String path ) {
        return getClass().getClassLoader().getResourceAsStream( path );
    }

    protected boolean isAccepted( final String path ) {
        if ( path != null && path.trim().length() > 0 ) {
            for ( DefinitionSetServices definitionSetService : definitionSetServices ) {
                String ext = definitionSetService.getFileExtension();
                if ( acceptsExtension( path, ext ) ) {
                    return true;
                }
            }

        }
        return acceptsExtension( path, DefaultDefinitionSetServices.EXTENSION );
    }

    private boolean acceptsExtension( final String path,
                                      final String ext ) {
        return path.toLowerCase().endsWith( ext.toLowerCase() );

    }

    protected DefinitionSetServices getServicesById( final String defSetId ) {
        for ( DefinitionSetServices definitionSetService : definitionSetServices ) {
            if ( definitionSetService.accepts( defSetId ) ) {
                return definitionSetService;
            }
        }
        return DEFAULT_SERVICES;
    }

    protected DefinitionSetServices getServicesByPath( final String path ) {
        for ( DefinitionSetServices definitionSetService : definitionSetServices ) {
            final String ext = definitionSetService.getFileExtension();
            if ( path.toLowerCase().endsWith( ext ) ) {
                return definitionSetService;
            }
        }
        return DEFAULT_SERVICES;
    }

    private String getDiagramFileName( final Diagram diagram ) {
        DefinitionSetServices services = getServicesById( diagram.getSettings().getDefinitionSetId() );
        String path = diagram.getSettings().getVFSPath();
        if ( path == null || path.trim().length() == 0 ) {
            path = buildDiagramPath( services, diagram );
        }
        return path;
    }

    private String buildDiagramPath( DefinitionSetServices services, Diagram diagram ) {
        final String ext = services.getFileExtension();
        return diagram.getUUID() + "." + ext;
    }

    private class DefaultDefinitionSetServices implements DefinitionSetServices {

        public static final String EXTENSION = "diagram";

        @Override
        public boolean accepts( final String definitionSetId ) {
            return true;
        }

        @Override
        public String getFileExtension() {
            return EXTENSION;
        }

        @Override
        public DiagramMarshaller<Diagram, InputStream, String> getDiagramMarshaller() {
            return defaultDiagramMarshaller;
        }

    }

}
