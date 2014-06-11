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

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.metadata.engine.MetaIndexEngine;
import org.uberfire.metadata.model.KCluster;
import org.uberfire.metadata.model.KObject;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.*;
import static org.uberfire.metadata.io.KObjectUtil.*;

/**
 *
 */
public final class BatchIndex {

    private static final Logger LOG = LoggerFactory.getLogger( BatchIndex.class );

    private final MetaIndexEngine indexEngine;
    private final Set<Indexer> additionalIndexers;
    private final IOService ioService;
    private final Class<? extends FileAttributeView>[] views;
    private final AtomicBoolean indexDisposed = new AtomicBoolean( false );

    public BatchIndex( final MetaIndexEngine indexEngine,
                       final Set<Indexer> additionalIndexers,
                       final IOService ioService,
                       final Class<? extends FileAttributeView>... views ) {
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.additionalIndexers = checkNotNull( "additionalIndexers",
                                                additionalIndexers );
        this.ioService = checkNotNull( "ioService",
                                       ioService );
        this.views = views;
    }

    public void runAsync( final FileSystem fs ) {
        if ( fs != null && fs.getRootDirectories().iterator().hasNext() ) {
            SimpleAsyncExecutorService.getUnmanagedInstance().execute( new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return "FS BatchIndex [" + ( (FileSystemId) fs ).id() + "]";
                }

                @Override
                public void run() {
                    final AtomicBoolean indexFinished = new AtomicBoolean( false );
                    indexEngine.beforeDispose( new Runnable() {
                        @Override
                        public void run() {
                            indexDisposed.set( true );

                            if ( !indexFinished.get() ) {
                                indexEngine.delete( KObjectUtil.toKCluster( fs ) );
                            }
                        }
                    } );

                    try {
                        for ( final Path root : fs.getRootDirectories() ) {
                            BatchIndex.this.run( root );
                        }
                        indexFinished.set( true );
                    } catch ( Exception ex ) {
                        if ( !indexDisposed.get() ) {
                            LOG.error( "FileSystem Index fails. [@" + fs.toString() + "]", ex );
                        }
                    }
                }
            } );
        }
    }

    public void runAsync( final Path root ) {
        SimpleAsyncExecutorService.getUnmanagedInstance().execute( new DescriptiveRunnable() {
            @Override
            public String getDescription() {
                return "Path BatchIndex [" + root.toString() + "]";
            }

            @Override
            public void run() {
                BatchIndex.this.run( root );
            }
        } );
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
            walkFileTree( checkNotNull( "root", root ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  if ( indexDisposed.get() ) {
                                      return FileVisitResult.TERMINATE;
                                  }
                                  try {
                                      checkNotNull( "file",
                                                    file );
                                      checkNotNull( "attrs",
                                                    attrs );

                                      if ( !file.getFileName().toString().startsWith( "." ) ) {

                                          //Default indexing
                                          for ( final Class<? extends FileAttributeView> view : views ) {
                                              ioService.getFileAttributeView( file,
                                                                              view );
                                          }
                                          final FileAttribute<?>[] allAttrs = ioService.convert( ioService.readAttributes( file ) );
                                          if ( !indexDisposed.get() ) {
                                              indexEngine.index( KObjectUtil.toKObject( file,
                                                                                        allAttrs ) );
                                          } else {
                                              return FileVisitResult.TERMINATE;
                                          }

                                          //Additional indexing
                                          for ( Indexer indexer : additionalIndexers ) {
                                              if ( indexer.supportsPath( file ) ) {
                                                  final KObject kObject = indexer.toKObject( file );
                                                  if ( kObject != null ) {
                                                      if ( !indexDisposed.get() ) {
                                                          indexEngine.index( kObject );
                                                      } else {
                                                          return FileVisitResult.TERMINATE;
                                                      }
                                                  }
                                              }
                                          }

                                      }
                                  } catch ( final Exception ex ) {
                                      if ( indexDisposed.get() ) {
                                          LOG.warn( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
                                          return FileVisitResult.TERMINATE;
                                      } else {
                                          LOG.error( "Index fails. [@" + file.toString() + "]", ex );
                                      }
                                  }
                                  if ( indexDisposed.get() ) {
                                      return FileVisitResult.TERMINATE;
                                  }
                                  return FileVisitResult.CONTINUE;
                              }
                          } );
            if ( !indexDisposed.get() ) {
                indexEngine.commit( cluster );
                if ( callback != null ) {
                    callback.run();
                }
            } else {
                LOG.warn( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
            }
        } catch ( final IllegalStateException ex ) {
            if ( indexDisposed.get() ) {
                LOG.warn( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
            } else {
                LOG.error( "Index fails - Index has an invalid state. [@" + root.toUri().toString() + "]", ex );
            }
        } catch ( final Exception ex ) {
            if ( indexDisposed.get() ) {
                LOG.warn( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
            } else {
                LOG.error( "Index fails. [@" + root.toUri().toString() + "]", ex );
            }
        }
    }

    public void dispose() {
        indexEngine.dispose();
    }

}
