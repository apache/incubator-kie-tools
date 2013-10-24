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

package org.drools.workbench.screens.scorecardxls.backend.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.drools.workbench.screens.scorecardxls.service.ScoreCardXLSService;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
// Implementation needs to implement both interfaces even though one extends the other
// otherwise the implementation discovery mechanism for the @Service annotation fails.
public class ScoreCardXLSServiceImpl implements ScoreCardXLSService,
                                                ExtendedScoreCardXLSService {

    private static final Logger log = LoggerFactory.getLogger( ScoreCardXLSServiceImpl.class );

    private static final JavaFileFilter FILTER_JAVA = new JavaFileFilter();

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
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Identity identity;

    @Inject
    private GenericValidator genericValidator;

    public InputStream load( final Path path,
                             final String sessionId ) {
        try {
            final InputStream inputStream = ioService.newInputStream( Paths.convert( path ),
                                                                      StandardOpenOption.READ );

            //Signal opening to interested parties
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path,
                                                               new SessionInfo() {
                                                                   @Override
                                                                   public String getId() {
                                                                       return sessionId;
                                                                   }

                                                                   @Override
                                                                   public Identity getIdentity() {
                                                                       return identity;
                                                                   }
                                                               } ) );

            return inputStream;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    public Path create( final Path resource,
                        final InputStream content,
                        final String sessionId,
                        final String comment ) {
        log.info( "USER:" + identity.getName() + " CREATING asset [" + resource.getFileName() + "]" );

        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( resource );
            ioService.createFile( nioPath );
            final OutputStream outputStream = ioService.newOutputStream( nioPath,
                                                                         makeCommentedOption( sessionId,
                                                                                              comment ) );
            IOUtils.copy( content,
                          outputStream );
            outputStream.flush();
            outputStream.close();

            return resource;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );

        } finally {
            try {
                content.close();
            } catch ( IOException e ) {
                throw new org.uberfire.java.nio.IOException( e.getMessage() );
            }
        }
    }

    public Path save( final Path resource,
                      final InputStream content,
                      final String sessionId,
                      final String comment ) {
        log.info( "USER:" + identity.getName() + " UPDATING asset [" + resource.getFileName() + "]" );

        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( resource );
            final OutputStream outputStream = ioService.newOutputStream( nioPath,
                                                                         makeCommentedOption( sessionId,
                                                                                              comment ) );
            IOUtils.copy( content,
                          outputStream );
            outputStream.flush();
            outputStream.close();

            return resource;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );

        } finally {
            try {
                content.close();
            } catch ( IOException e ) {
                throw new org.uberfire.java.nio.IOException( e.getMessage() );
            }
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
    public List<ValidationMessage> validate( final Path path,
                                             final Path resource ) {
        try {
            final InputStream inputStream = ioService.newInputStream( Paths.convert( path ),
                                                                      StandardOpenOption.READ );
            return genericValidator.validate( path,
                                              inputStream,
                                              FILTER_JAVA );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private CommentedOption makeCommentedOption( final String sessionId,
                                                 final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( sessionId,
                                                        name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

}
