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

package org.uberfire.metadata.io;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.io.SimpleFSIOServiceDotFileTest;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.metadata.backend.lucene.LuceneIndexEngine;
import org.uberfire.metadata.backend.lucene.fields.SimpleFieldFactory;
import org.uberfire.metadata.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.uberfire.metadata.backend.lucene.setups.BaseLuceneSetup;
import org.uberfire.metadata.backend.lucene.setups.RAMLuceneSetup;
import org.uberfire.metadata.engine.MetaModelStore;

import static org.junit.Assert.*;

/**
 *
 */
@Ignore
public class IOServiceIndexedFSImplTest extends SimpleFSIOServiceDotFileTest {

    protected static IOService ioService = null;
    private MetaModelStore  metaModelStore;
    private BaseLuceneSetup luceneSetup;

    public IOService ioService() {

        if ( ioService == null ) {
            metaModelStore = new InMemoryMetaModelStore();
            luceneSetup = new RAMLuceneSetup();
            ioService = new IOServiceIndexedImpl( new LuceneIndexEngine( metaModelStore, luceneSetup, new SimpleFieldFactory() ), DublinCoreView.class, VersionAttributeView.class );
        }
        return ioService;
    }

    @Test
    public void testIndexedFile() throws IOException {
        final Path dir = ioService().createDirectories( getDirectoryPath().resolveSibling( "someNewOtherPath" ) );

        final Path path = dir.resolve( "myIndexedFile.txt" );

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
                                   return "x_hello";
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

        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ) );

        assertEquals( 3, metaModelStore.getMetaObject( Path.class.getName() ).getProperties().size() );
        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "int" ) );
        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "x_hello" ) );
        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "custom" ) );

        ioService().setAttribute( path, "my_new_key", "some big value here to be able to query for" );

        assertEquals( 4, metaModelStore.getMetaObject( Path.class.getName() ).getProperties().size() );
        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "int" ) );
        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "x_hello" ) );
        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "custom" ) );
        assertNotNull( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "my_new_key" ) );

        assertEquals( 1, metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "int" ).getTypes().size() );
        assertEquals( 1, metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "x_hello" ).getTypes().size() );
        assertEquals( 1, metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "custom" ).getTypes().size() );
        assertEquals( 1, metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "my_new_key" ).getTypes().size() );

        assertTrue( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "int" ).getTypes().contains( Integer.class ) );
        assertTrue( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "x_hello" ).getTypes().contains( String.class ) );
        assertTrue( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "custom" ).getTypes().contains( Date.class ) );
        assertTrue( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "my_new_key" ).getTypes().contains( String.class ) );

        ioService().write( path.resolveSibling( "otherIndexedFile.txt" ), "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
                               @Override
                               public String name() {
                                   return "custom";
                               }

                               @Override
                               public Long value() {
                                   return 10L;
                               }
                           }, new FileAttribute<Object>() {
                               @Override
                               public String name() {
                                   return "my_new_key";
                               }

                               @Override
                               public String value() {
                                   return "some other content here that only this can be found.";
                               }
                           }
                         );

        assertEquals( 2, metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "custom" ).getTypes().size() );
        assertTrue( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "custom" ).getTypes().contains( Date.class ) );
        assertTrue( metaModelStore.getMetaObject( Path.class.getName() ).getProperty( "custom" ).getTypes().contains( Long.class ) );

        final IndexSearcher searcher = luceneSetup.nrtSearcher();

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "my_new_key", "found" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "my_new_key", "query" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "my_new_key", "some" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 2, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new MatchAllDocsQuery(), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 2, hits.length );
        }

        luceneSetup.nrtRelease( searcher );

    }
}
