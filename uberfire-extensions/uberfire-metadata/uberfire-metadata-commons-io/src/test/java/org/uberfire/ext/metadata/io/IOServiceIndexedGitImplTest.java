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

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil.toKObject;
import static org.uberfire.ext.metadata.io.KObjectUtil.*;

public class IOServiceIndexedGitImplTest extends BaseIndexTest {

    protected final Date dateValue = new Date();

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{ this.getClass().getSimpleName() };
    }

    @Test
    public void testIndexedFile() throws IOException, InterruptedException {
        final Path path1 = getBasePath( this.getClass().getSimpleName() ).resolve( "myIndexedFile.txt" );
        ioService().write( path1,
                           "ooooo!",
                           Collections.<OpenOption>emptySet(),
                           new FileAttribute<Object>() {
                               @Override
                               public String name() {
                                   return "custom";
                               }

                               @Override
                               public Object value() {
                                   return dateValue;
                               }
                           },
                           new FileAttribute<String>() {
                               @Override
                               public String name() {
                                   return "int.hello";
                               }

                               @Override
                               public String value() {
                                   return "hello some world jhere";
                               }
                           },
                           new FileAttribute<Integer>() {
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

        final Path path2 = getBasePath( this.getClass().getSimpleName() ).resolve( "myOtherIndexedFile.txt" );
        ioService().write( path2,
                           "ooooo!",
                           Collections.<OpenOption>emptySet(),
                           new FileAttribute<String>() {
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

        final Index index = config.getIndexManager().get( toKCluster( path2.getFileSystem() ) );

        final IndexSearcher searcher = ( (LuceneIndex) index ).nrtSearcher();

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "int.hello", "world" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;
            listHitPaths( searcher,
                          hits );

            assertEquals( 1,
                          hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "int.hello", "jhere" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;
            listHitPaths( searcher,
                          hits );

            assertEquals( 2,
                          hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new MatchAllDocsQuery(), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;
            listHitPaths( searcher,
                          hits );

            assertEquals( 2,
                          hits.length );
        }

        ( (LuceneIndex) index ).nrtRelease( searcher );
    }

}
