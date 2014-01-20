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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.metadata.backend.lucene.index.LuceneIndex;

import static org.junit.Assert.*;
import static org.uberfire.metadata.io.KObjectUtil.*;

/**
 *
 */
public class IOServiceIndexedGitImplTest {

    protected IOService ioService = null;
    private static LuceneConfig config;

    public IOService ioService() {
        if ( ioService == null ) {
            config = new LuceneConfigBuilder().withInMemoryMetaModelStore().useDirectoryBasedIndex().useInMemoryDirectory().build();
            ioService = new IOServiceIndexedImpl( config.getIndexEngine(), DublinCoreView.class, VersionAttributeView.class );
        }
        return ioService;
    }

    @Test
    public void testIndexedFile() throws IOException, InterruptedException {
        final Path newOtherPath = getDirectoryPath().resolveSibling( "someNewOtherPath" );
        ioService().write( newOtherPath.resolve( "dummy" ), "<none>" );
        final Path path = newOtherPath.resolve( "myIndexedFile.txt" );
        ioService().write( path, "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
                               @Override
                               public String name() {
                                   return "custom";
                               }

                               @Override
                               public Object value() {
                                   return dateValue;
                               }
                           }, new FileAttribute<String>() {
                               @Override
                               public String name() {
                                   return "int.hello";
                               }

                               @Override
                               public String value() {
                                   return "hello some world jhere";
                               }
                           }, new FileAttribute<Integer>() {
                               @Override
                               public String name() {
                                   return "int";
                               }

                               @Override
                               public Integer value() {
                                   return 10;
                               }
                           }
                         );

        ioService().write( newOtherPath.resolve( "myOtherIndexedFile.txt" ), "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<String>() {
            @Override
            public String name() {
                return "int.hello";
            }

            @Override
            public String value() {
                return "jhere";
            }
        } );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index
        assertNotNull( config.getMetaModelStore().getMetaObject( Path.class.getName() ) );

        assertNotNull( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int" ) );
        assertNotNull( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int.hello" ) );
        assertNotNull( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "custom" ) );

        assertNotNull( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int" ) );
        assertNotNull( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int.hello" ) );
        assertNotNull( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "custom" ) );

        assertEquals( 1, config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int" ).getTypes().size() );
        assertEquals( 1, config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int.hello" ).getTypes().size() );
        assertEquals( 1, config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "custom" ).getTypes().size() );

        assertTrue( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int" ).getTypes().contains( Integer.class ) );
        assertTrue( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "int.hello" ).getTypes().contains( String.class ) );
        assertTrue( config.getMetaModelStore().getMetaObject( Path.class.getName() ).getProperty( "custom" ).getTypes().contains( Date.class ) );

        final LuceneIndex index = config.getIndexManager().get( toKCluster( newOtherPath.getFileSystem() ) );

        final IndexSearcher searcher = index.nrtSearcher();

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "int.hello", "world" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "int.hello", "jhere" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 2, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new MatchAllDocsQuery(), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 2, hits.length );
        }

        index.nrtRelease( searcher );
    }

    public Path getDirectoryPath() {
        final Path dir = ioService().get( URI.create( "git://indexed-repo-test/_someDir" + new Random( 10L ).nextInt() ) );
        ioService().deleteIfExists( dir );

        return dir;
    }

    private Path getRootPath() {
        return ioService().get( URI.create( "git://indexed-repo-test/" ) );
    }

    private static boolean created = false;

    @Before
    public void setup() throws IOException {
        if ( !created ) {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty( "org.uberfire.nio.git.dir", path );
            System.out.println( ".niogit: " + path );

            final URI newRepo = URI.create( "git://indexed-repo-test" );

            try {
                ioService().newFileSystem( newRepo, new HashMap<String, Object>() );
            } catch ( final Exception ex ) {
            } finally {
                created = true;
            }
        }
    }

    protected final Date dateValue = new Date();

    protected static final List<File> tempFiles = new ArrayList<File>();

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for ( final File tempFile : tempFiles ) {
            FileUtils.deleteQuietly( tempFile );
        }
    }

    public static File createTempDirectory()
            throws IOException {
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

}
