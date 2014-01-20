/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.uberfire.metadata.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.metadata.engine.MetaIndexEngine;
import org.uberfire.metadata.model.KCluster;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.*;
import static org.uberfire.metadata.io.KObjectUtil.*;

/**
 *
 */
public final class BatchIndex {

    private static final Logger LOG = LoggerFactory.getLogger( BatchIndex.class );

    private final MetaIndexEngine indexEngine;
    private final IOService ioService;
    private final Class<? extends FileAttributeView>[] views;

    public BatchIndex( final MetaIndexEngine indexEngine,
                       final IOService ioService,
                       final Class<? extends FileAttributeView>... views ) {
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
        this.ioService = checkNotNull( "ioService", ioService );
        this.views = views;
    }

    public void runAsync( final FileSystem fs ) {
        if ( fs != null && fs.getRootDirectories().iterator().hasNext() ) {
            new Thread() {
                public void run() {
                    for ( final Path root : fs.getRootDirectories() ) {
                        BatchIndex.this.run( root );
                    }
                }
            }.start();
        }
    }

    public void runAsync( final Path root ) {
        new Thread() {
            public void run() {
                BatchIndex.this.run( root );
            }
        }.start();
    }

    public void run( final Path root ) {
        run( root, null );
    }

    public void run( final Path root,
                     final Runnable callback ) {
        try {
            if ( root == null ) {
                return;
            }
            final KCluster cluster = toKCluster( root.getFileSystem() );
            indexEngine.startBatch( cluster );
            walkFileTree( checkNotNull( "root", root ), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile( final Path file,
                                                  final BasicFileAttributes attrs ) throws IOException {
                    try {
                        checkNotNull( "file", file );
                        checkNotNull( "attrs", attrs );

                        if ( !file.getFileName().toString().startsWith( "." ) ) {

                            for ( final Class<? extends FileAttributeView> view : views ) {
                                ioService.getFileAttributeView( file, view );
                            }

                            final FileAttribute<?>[] allAttrs = ioService.convert( ioService.readAttributes( file ) );
                            indexEngine.index( KObjectUtil.toKObject( file, allAttrs ) );
                        }
                    } catch ( final Exception ex ) {
                        LOG.error( "Index fails. [@" + file.toString() + "]", ex );
                    }
                    return FileVisitResult.CONTINUE;
                }
            } );
            indexEngine.commit( cluster );
            if ( callback != null ) {
                callback.run();
            }
        } catch ( final IllegalStateException ex ) {
            LOG.error( "Index fails - Index has an invalid state. [@" + root.getFileSystem().toString() + "]", ex );
        } catch ( final Exception ex ) {
            LOG.error( "Index fails. [@" + root.getFileSystem().toString() + "]", ex );
        }
    }

    public void dispose() {
        indexEngine.dispose();
    }

}
