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

package org.uberfire.ext.metadata.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.search.ClusterSegment;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.SegmentedPath;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

public class LuceneSearchIndexTest {

    private int seed = new Random( 10L ).nextInt();

    protected boolean created = false;
    private Map<String, Path> basePaths = new HashMap<String, Path>();

    private LuceneConfig config;
    private IOService ioService = null;

    protected static final List<File> tempFiles = new ArrayList<File>();

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for ( final File tempFile : tempFiles ) {
            FileUtils.deleteQuietly( tempFile );
        }
    }

    protected IOService ioService() {
        if ( ioService == null ) {
            config = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();

            ioService = new IOServiceIndexedImpl( config.getIndexEngine() );
        }
        return ioService;
    }

    protected static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );
        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }
        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }
        tempFiles.add( temp );
        return temp;
    }

    @Before
    public void setup() throws IOException {
        if ( !created ) {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty( "org.uberfire.nio.git.dir",
                                path );
            System.out.println( ".niogit: " + path );

            for ( String repositoryName : getRepositoryNames() ) {

                final URI newRepo = URI.create( "git://" + repositoryName );

                try {
                    ioService().newFileSystem( newRepo,
                                               new HashMap<String, Object>() );

                    //Don't ask, but we need to write a single file first in order for indexing to work
                    final Path basePath = getDirectoryPath( repositoryName ).resolveSibling( "someNewOtherPath" );
                    ioService().write( basePath.resolve( "dummy" ),
                                       "<none>" );
                    basePaths.put( repositoryName,
                                   basePath );

                } catch ( final Exception ex ) {
                    ex.fillInStackTrace();
                    System.out.println( ex.getMessage() );
                } finally {
                    created = true;
                }
            }
        }
    }

    private String[] getRepositoryNames() {
        return new String[]{ this.getClass().getSimpleName() + "_1", this.getClass().getSimpleName() + "_2" };
    }

    private Path getBasePath( final String repositoryName ) {
        return basePaths.get( repositoryName );
    }

    private Path getDirectoryPath( final String repositoryName ) {
        final Path dir = ioService().get( URI.create( "git://" + repositoryName + "/_someDir" + seed ) );
        ioService().deleteIfExists( dir );
        return dir;
    }

    @Test
    public void testClusterSegments() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = getBasePath( this.getClass().getSimpleName() + "_1" ).resolve( "indexedFile1.txt" );
        ioService().write( path1,
                           "content1" );
        final Path path2 = getBasePath( this.getClass().getSimpleName() + "_2" ).resolve( "indexedFile2.txt" );
        ioService().write( path2,
                           "content2" );

        //Setup ClusterSegments
        final ClusterSegment cs1 = new ClusterSegment() {
            @Override
            public String getClusterId() {
                return ( (FileSystemId) getBasePath( LuceneSearchIndexTest.this.getClass().getSimpleName() + "_1" ).getFileSystem() ).id();
            }

            @Override
            public String[] segmentIds() {
                return new String[]{ ( (SegmentedPath) getBasePath( LuceneSearchIndexTest.this.getClass().getSimpleName() + "_1" ) ).getSegmentId() };
            }
        };
        final ClusterSegment cs2 = new ClusterSegment() {
            @Override
            public String getClusterId() {
                return ( (FileSystemId) getBasePath( LuceneSearchIndexTest.this.getClass().getSimpleName() + "_2" ).getFileSystem() ).id();
            }

            @Override
            public String[] segmentIds() {
                return new String[]{ ( (SegmentedPath) getBasePath( LuceneSearchIndexTest.this.getClass().getSimpleName() + "_2" ) ).getSegmentId() };
            }
        };

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Map<String, Object> attributes = new HashMap<String, Object>() {{
            put( "filename",
                 "*.txt" );
        }};

        {
            final int hits = config.getSearchIndex().searchByAttrsHits( attributes );
            assertEquals( 2,
                          hits );
        }

        {
            final int hits = config.getSearchIndex().searchByAttrsHits( attributes,
                                                                        cs1 );
            assertEquals( 1,
                          hits );
        }

        {
            final int hits = config.getSearchIndex().searchByAttrsHits( attributes,
                                                                        cs2 );
            assertEquals( 1,
                          hits );
        }

        {
            final int hits = config.getSearchIndex().searchByAttrsHits( attributes,
                                                                        cs1,
                                                                        cs2 );
            assertEquals( 2,
                          hits );
        }
    }

}
