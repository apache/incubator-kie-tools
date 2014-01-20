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

package org.uberfire.metadata.backend.lucene.index.directory;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.uberfire.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.metadata.model.KCluster;

public enum DirectoryType {
    INMEMORY {
        @Override
        public LuceneIndex newIndex( final KCluster cluster,
                                     final IndexWriterConfig config ) {
            final Directory directory = new Directory( new RAMDirectory(), new DeleteCommand() {
                @Override
                public void execute( org.apache.lucene.store.Directory directory ) {
                }
            }, true );
            return new DirectoryLuceneIndex( cluster, directory, config );
        }
    }, NIO {
        @Override
        public LuceneIndex newIndex( final KCluster cluster,
                                     final IndexWriterConfig config ) {

            final File clusterDir = clusterDir( cluster.getClusterId() );
            final NIOFSDirectory luceneDir;
            try {
                luceneDir = new NIOFSDirectory( clusterDir );
            } catch ( IOException e ) {
                throw new org.uberfire.java.nio.IOException( e );
            }

            final Directory directory = new Directory( luceneDir, new DeleteCommand() {
                @Override
                public void execute( org.apache.lucene.store.Directory directory ) {
                    ( (NIOFSDirectory) directory ).close();
                    FileDeleteStrategy.FORCE.deleteQuietly( clusterDir );
                }
            }, freshIndex( clusterDir ) );

            return new DirectoryLuceneIndex( cluster, directory, config );
        }
    }, MMAP {
        @Override
        public LuceneIndex newIndex( final KCluster cluster,
                                     final IndexWriterConfig config ) {
            final File clusterDir = clusterDir( cluster.getClusterId() );
            final MMapDirectory luceneDir;
            try {
                luceneDir = new MMapDirectory( clusterDir );
            } catch ( IOException e ) {
                throw new org.uberfire.java.nio.IOException( e );
            }
            final Directory directory = new Directory( luceneDir, new DeleteCommand() {
                @Override
                public void execute( org.apache.lucene.store.Directory directory ) {
                    ( (MMapDirectory) directory ).close();
                    FileDeleteStrategy.FORCE.deleteQuietly( clusterDir );
                }
            }, freshIndex( clusterDir ) );

            return new DirectoryLuceneIndex( cluster, directory, config );
        }
    };

    public abstract LuceneIndex newIndex( final KCluster cluster,
                                          final IndexWriterConfig config );

    private static File clusterDir( final String clusterId ) {
        return new File( DirectoryFactory.defaultHostingDir(), clusterId );
    }

    private static boolean freshIndex( final File clusterDir ) {
        return !clusterDir.exists();
    }
}
