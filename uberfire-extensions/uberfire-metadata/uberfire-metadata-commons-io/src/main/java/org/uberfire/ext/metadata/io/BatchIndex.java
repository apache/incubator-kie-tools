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

package org.uberfire.ext.metadata.io;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.Observer;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
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

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.*;

/**
 *
 */
public final class BatchIndex {

    private static final Logger LOG = LoggerFactory.getLogger( BatchIndex.class );

    private final MetaIndexEngine indexEngine;
    private final IOService ioService;
    private final Class<? extends FileAttributeView>[] views;
    private final AtomicBoolean indexDisposed = new AtomicBoolean( false );
    private final Observer observer;

    public BatchIndex( final MetaIndexEngine indexEngine,
                       final IOService ioService,
                       final Observer observer,
                       final Class<? extends FileAttributeView>... views ) {
        this.indexEngine = checkNotNull( "indexEngine",
                                         indexEngine );
        this.ioService = checkNotNull( "ioService",
                                       ioService );
        this.observer = checkNotNull( "observer",
                                      observer );
        this.views = views;
    }

    public void runAsync( final FileSystem fs ) {
        if ( fs != null && fs.getRootDirectories().iterator().hasNext() ) {
            SimpleAsyncExecutorService.getDefaultInstance().execute( new DescriptiveRunnable() {
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
                            logError( "FileSystem Index fails. [@" + fs.toString() + "]",
                                      ex );
                        }
                    }
                }
            } );
        }
    }

    public void runAsync( final Path root ) {
        SimpleAsyncExecutorService.getDefaultInstance().execute( new DescriptiveRunnable() {
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

            logInformation( "Starting indexing of " + root.toUri() + " ..." );

            final KCluster cluster = KObjectUtil.toKCluster( root.getFileSystem() );

            walkFileTree( checkNotNull( "root",
                                        root ),
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
                                          
                                          LOG.debug( "Indexing " + file.toUri() );

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
                                          for ( Indexer indexer : IndexersFactory.getIndexers() ) {
                                              if ( file.getFileSystem().isOpen() ) {
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

                                      }
                                  } catch ( final Exception ex ) {
                                      if ( indexDisposed.get() ) {
                                          logWarning( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
                                          return FileVisitResult.TERMINATE;
                                      } else {
                                          logError( "Index fails. [@" + file.toString() + "]",
                                                    ex );
                                      }
                                  }
                                  if ( indexDisposed.get() ) {
                                      return FileVisitResult.TERMINATE;
                                  }
                                  return FileVisitResult.CONTINUE;
                              }
                          } );

            if ( !indexDisposed.get() ) {
                logInformation( "Completed indexing of " + root.toUri() );
                indexEngine.commit( cluster );
                if ( callback != null ) {
                    callback.run();
                }
            } else {
                logWarning( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
            }

        } catch ( final IllegalStateException ex ) {
            if ( indexDisposed.get() ) {
                logWarning( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
            } else {
                logError( "Index fails - Index has an invalid state. [@" + root.toUri().toString() + "]",
                          ex );
            }
        } catch ( final Exception ex ) {
            if ( indexDisposed.get() ) {
                logWarning( "Batch index couldn't finish. [@" + root.toUri().toString() + "]" );
            } else {
                logError( "Index fails. [@" + root.toUri().toString() + "]",
                          ex );
            }
        }
    }

    private void logInformation( final String message ) {
        observer.information( message );
        LOG.info( message );
    }

    private void logWarning( final String message ) {
        observer.warning( message );
        LOG.warn( message );
    }

    private void logError( final String message,
                           final Throwable throwable ) {
        observer.error( message );
        LOG.error( message,
                   throwable );
    }

    public void dispose() {
        indexEngine.dispose();
    }

}
