/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.backend;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.wires.bpmn.api.model.impl.BpmnEditorContent;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.service.BpmnService;
import org.uberfire.ext.wires.bpmn.api.service.todo.Metadata;
import org.uberfire.ext.wires.bpmn.api.type.BpmnResourceTypeDefinition;
import org.uberfire.ext.wires.bpmn.backend.todo.CommentedOptionFactory;
import org.uberfire.ext.wires.bpmn.backend.todo.ExceptionUtilities;
import org.uberfire.ext.wires.bpmn.backend.todo.MetadataFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;

@Service
@ApplicationScoped
public class BpmnServiceImpl implements BpmnService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    protected User identity;

    @Inject
    protected SessionInfo sessionInfo;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private BpmnResourceTypeDefinition typeDefinition;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final ProcessNode content,
                        final String comment ) {
        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( context ).resolve( fileName );
            final Path newPath = Paths.convert( nioPath );

            if ( ioService.exists( nioPath ) ) {
                throw new FileAlreadyExistsException( nioPath.toString() );
            }

            ioService.write( nioPath,
                             BpmnPersistence.getInstance().marshal( content ),
                             CommentedOptionFactory.makeCommentedOption( identity,
                                                                         sessionInfo,
                                                                         comment ) );

            return newPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public BpmnEditorContent loadContent( final Path path ) {
        try {
            final ProcessNode graph = load( path );
            final BpmnEditorContent content = new BpmnEditorContent( graph );

            return content;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public ProcessNode load( final Path path ) {
        try {
            final String content = ioService.readAllString( Paths.convert( path ) );

            return BpmnPersistence.getInstance().unmarshal( content );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path save( final Path path,
                      final ProcessNode content,
                      final Metadata metadata,
                      final String comment ) {
        try {
            ioService.write( Paths.convert( path ),
                             BpmnPersistence.getInstance().marshal( content ),
                             MetadataFactory.makeMetadata( metadata ),
                             CommentedOptionFactory.makeCommentedOption( identity,
                                                                         sessionInfo,
                                                                         comment ) );

            return path;

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

    /**
     * TEMPORARY METHODS UNTIL INTEGRATED INTO KIE-WB
     */
    private FileSystem fileSystem;
    private org.uberfire.java.nio.file.Path root;

    @PostConstruct
    public void setup() {
        try {
            fileSystem = ioService.newFileSystem( URI.create( "default://bpmn" ),
                                                  new HashMap<String, Object>() {{
                                                      put( "init", Boolean.TRUE );
                                                      put( "internal", Boolean.TRUE );
                                                  }} );
        } catch ( final FileSystemAlreadyExistsException e ) {
            fileSystem = ioService.getFileSystem( URI.create( "default://bpmn" ) );
        }
        this.root = fileSystem.getRootDirectories().iterator().next();

        ioService.write( root.resolve( "file1.bpmn" ),
                         BpmnPersistence.getInstance().marshal( new ProcessNode() ) );
    }

    @Override
    public List<Path> listFiles() {
        final DirectoryStream<org.uberfire.java.nio.file.Path> stream = ioService.newDirectoryStream( root,
                                                                                                      new DirectoryStream.Filter<org.uberfire.java.nio.file.Path>() {
                                                                                                          @Override
                                                                                                          public boolean accept( org.uberfire.java.nio.file.Path entry ) throws IOException {
                                                                                                              return typeDefinition.accept( Paths.convert( entry ) );
                                                                                                          }
                                                                                                      } );
        final List<Path> files = new ArrayList<Path>();
        final Iterator<org.uberfire.java.nio.file.Path> itr = stream.iterator();
        while ( itr.hasNext() ) {
            files.add( Paths.convert( itr.next() ) );
        }
        return files;
    }
}
