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

package org.drools.workbench.screens.dtablexls.backend.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.drools.guvnor.models.guided.dtable.shared.conversion.ConversionResult;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.StandardOpenOption;
import org.kie.guvnor.commons.service.validation.model.BuilderResult;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSConversionService;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.kie.guvnor.services.file.CopyService;
import org.kie.guvnor.services.file.DeleteService;
import org.kie.guvnor.services.file.RenameService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceOpenedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
// Implementation needs to implement both interfaces even though one extends the other
// otherwise the implementation discovery mechanism for the @Service annotation fails.
public class DecisionTableXLSServiceImpl implements DecisionTableXLSService,
                                                    ExtendedDecisionTableXLSService {

    private static final Logger log = LoggerFactory.getLogger( DecisionTableXLSServiceImpl.class );

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
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private DecisionTableXLSConversionService conversionService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    public InputStream load( final Path path ) {
        final InputStream inputStream = ioService.newInputStream( paths.convert( path ),
                                                                  StandardOpenOption.READ );

        //Signal opening to interested parties
        resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

        return inputStream;
    }

    public Path create( final Path resource,
                        final InputStream content,
                        final String comment ) {
        log.info( "USER:" + identity.getName() + " CREATING asset [" + resource.getFileName() + "]" );

        try {
            final org.kie.commons.java.nio.file.Path nioPath = paths.convert( resource );
            ioService.createFile( nioPath );
            final OutputStream outputStream = ioService.newOutputStream( nioPath,
                                                                         makeCommentedOption( comment ) );
            IOUtils.copy( content,
                          outputStream );
            outputStream.flush();
            outputStream.close();

            //Read Path to ensure attributes have been set
            final Path newPath = paths.convert( nioPath );

            //Signal creation to interested parties
            resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

            return newPath;

        } catch ( IOException e ) {
            throw new org.kie.commons.java.nio.IOException( e.getMessage() );

        } finally {
            try {
                content.close();
            } catch ( IOException e ) {
                throw new org.kie.commons.java.nio.IOException( e.getMessage() );
            }
        }
    }

    public Path save( final Path resource,
                      final InputStream content,
                      final String comment ) {
        log.info( "USER:" + identity.getName() + " UPDATING asset [" + resource.getFileName() + "]" );

        try {
            final org.kie.commons.java.nio.file.Path nioPath = paths.convert( resource );
            final OutputStream outputStream = ioService.newOutputStream( nioPath,
                                                                         makeCommentedOption( comment ) );
            IOUtils.copy( content,
                          outputStream );
            outputStream.flush();
            outputStream.close();

            //Read Path to ensure attributes have been set
            final Path newPath = paths.convert( nioPath );

            //Signal update to interested parties
            resourceUpdatedEvent.fire( new ResourceUpdatedEvent( newPath ) );

            return newPath;

        } catch ( IOException e ) {
            throw new org.kie.commons.java.nio.IOException( e.getMessage() );

        } finally {
            try {
                content.close();
            } catch ( IOException e ) {
                throw new org.kie.commons.java.nio.IOException( e.getMessage() );
            }
        }
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        deleteService.delete( path,
                              comment );
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        return renameService.rename( path,
                                     newName,
                                     comment );
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        return copyService.copy( path,
                                 newName,
                                 comment );
    }

    @Override
    public ConversionResult convert( final Path path ) {
        return conversionService.convert( path );
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
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
