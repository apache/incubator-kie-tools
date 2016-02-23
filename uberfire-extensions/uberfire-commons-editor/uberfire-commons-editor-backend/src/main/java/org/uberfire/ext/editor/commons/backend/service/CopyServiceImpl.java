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

package org.uberfire.ext.editor.commons.backend.service;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.CopyHelper;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;
import org.uberfire.ext.editor.commons.service.restrictor.CopyRestrictor;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.workbench.events.ResourceCopiedEvent;

@Service
@ApplicationScoped
public class CopyServiceImpl implements CopyService {

    private static final Logger LOGGER = LoggerFactory.getLogger( CopyServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private User identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Instance<CopyHelper> helpers;

    @Inject
    private Event<ResourceCopiedEvent> resourceCopiedEvent;

    @Inject
    private Instance<CopyRestrictor> copyRestrictorBeans;

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        LOGGER.info( "User:" + identity.getIdentifier() + " copying file [" + path.getFileName() + "] to [" + newName + "]" );

        checkRestrictions( path );

        try {
            return copyPath( path, newName, comment );
        } catch ( final RuntimeException e ) {
            throw e;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void copyIfExists( final Collection<Path> paths,
                              final String newName,
                              final String comment ) {
        try {
            //Always use a batch as CopyHelpers may be involved with the rename operation
            startBatch( paths );

            for ( final Path path : paths ) {
                LOGGER.info( "User:" + identity.getIdentifier() + " copying file (if exists) [" + path.getFileName() + "] to [" + newName + "]" );

                checkRestrictions( path );
                copyPathIfExists( path, newName, comment );
            }
        } catch ( final RuntimeException e ) {
            throw e;
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            endBatch();
        }
    }

    @Override
    public boolean hasRestriction( final Path path ) {
        for ( CopyRestrictor copyRestrictor : getCopyRestrictors() ) {
            final PathOperationRestriction copyRestriction = copyRestrictor.hasRestriction( path );
            if ( copyRestriction != null ) {
                return true;
            }
        }

        return false;
    }

    private void checkRestrictions( final Path path ) {
        for ( CopyRestrictor copyRestrictor : getCopyRestrictors() ) {
            final PathOperationRestriction copyRestriction = copyRestrictor.hasRestriction( path );
            if ( copyRestriction != null ) {
                throw new RuntimeException( copyRestriction.getMessage( path ) );
            }
        }
    }

    Path copyPath( final Path path,
                   final String newName,
                   final String comment ) {
        final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

        String originalFileName = _path.getFileName().toString();
        final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
        final org.uberfire.java.nio.file.Path _target = _path.resolveSibling( newName + extension );
        final Path targetPath = Paths.convert( _target );

        try {
            ioService.startBatch( _target.getFileSystem() );

            ioService.copy( Paths.convert( path ),
                            Paths.convert( targetPath ),
                            new CommentedOption( sessionInfo != null ? sessionInfo.getId() : "--",
                                                 identity.getIdentifier(),
                                                 null,
                                                 comment ) );

            //Delegate additional changes required for a copy to applicable Helpers
            for ( CopyHelper helper : helpers ) {
                if ( helper.supports( targetPath ) ) {
                    helper.postProcess( path,
                                        targetPath );
                }
            }
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            endBatch();
        }

        resourceCopiedEvent.fire( new ResourceCopiedEvent( path,
                                                           targetPath,
                                                           comment,
                                                           sessionInfo != null ? sessionInfo : new SessionInfoImpl( "--", identity ) ) );

        return targetPath;
    }

    void copyPathIfExists( final Path path,
                           final String newName,
                           final String comment ) {
        final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

        if ( Files.exists( _path ) ) {
            final org.uberfire.java.nio.file.Path _target;
            if ( Files.isDirectory( _path ) ) {
                _target = _path.resolveSibling( newName );
            } else {
                final String originalFileName = _path.getFileName().toString();
                final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
                _target = _path.resolveSibling( newName + extension );
            }

            ioService.copy( _path,
                            _target,
                            new CommentedOption( sessionInfo.getId(),
                                                 identity.getIdentifier(),
                                                 null,
                                                 comment )
                          );

            //Delegate additional changes required for a copy to applicable Helpers
            if ( _target != null ) {
                final Path targetPath = Paths.convert( _target );
                for ( CopyHelper helper : helpers ) {
                    if ( helper.supports( targetPath ) ) {
                        helper.postProcess( path,
                                            targetPath );
                    }
                }
            }
        }
    }

    void startBatch( final Collection<Path> paths ) {
        ioService.startBatch( Paths.convert( paths.iterator().next() ).getFileSystem() );
    }

    void endBatch() {
        ioService.endBatch();
    }

    Iterable<CopyRestrictor> getCopyRestrictors() {
        return copyRestrictorBeans;
    }
}
