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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.WildcardQuery;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.engine.MetaIndexEngine.*;
import static org.uberfire.ext.metadata.io.KObjectUtil.*;

public class LuceneFullTextSearchIndexTest extends BaseIndexTest {

    @Override
    protected IOService ioService() {
        if ( ioService == null ) {
            config = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();

            ioService = new IOServiceIndexedImpl( config.getIndexEngine(),
                                                  DublinCoreView.class,
                                                  VersionAttributeView.class );

            IndexersFactory.addIndexer( new MockIndexer() );
        }
        return ioService;
    }

    private static class MockIndexer implements Indexer {

        @Override
        public boolean supportsPath( final Path path ) {
            return true;
        }

        @Override
        public KObject toKObject( final Path path ) {
            return new TestKObjectWrapper( KObjectUtil.toKObject( path ) );
        }

        @Override
        public KObjectKey toKObjectKey( final Path path ) {
            return new TestKObjectKeyWrapper( KObjectUtil.toKObjectKey( path ) );
        }

    }

    private static class TestKObjectKeyWrapper implements KObjectKey {

        protected KObjectKey delegate;

        private TestKObjectKeyWrapper( final KObjectKey delegate ) {
            this.delegate = delegate;
        }

        @Override
        public String getId() {
            return delegate.getId()+"-refactoring";
        }

        @Override
        public MetaType getType() {
            return delegate.getType();
        }

        @Override
        public String getClusterId() {
            return delegate.getClusterId();
        }

        @Override
        public String getSegmentId() {
            return delegate.getSegmentId();
        }

        @Override
        public String getKey() {
            return delegate.getKey();
        }
    }

    private static class TestKObjectWrapper extends TestKObjectKeyWrapper implements KObject {

        private TestKObjectWrapper( final KObject delegate ) {
            super( delegate );
        }

        @Override
        public Iterable<KProperty<?>> getProperties() {
            return ( (KObject) delegate ).getProperties();
        }

        @Override
        public boolean fullText() {
            return false;
        }
    }

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{ this.getClass().getSimpleName() };
    }

    @Test
    public void testFullTextIndexedFile() throws IOException, InterruptedException {
        final Path path1 = getBasePath( this.getClass().getSimpleName() ).resolve( "mydrlfile1.drl" );
        ioService().write( path1,
                           "Some cheese" );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = config.getIndexManager().get( toKCluster( path1.getFileSystem() ) );

        final IndexSearcher searcher = ( (LuceneIndex) index ).nrtSearcher();

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new WildcardQuery( new Term( FULL_TEXT_FIELD, "*file*" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;
            listHitPaths( searcher,
                          hits );

            assertEquals( 1,
                          hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new WildcardQuery( new Term( FULL_TEXT_FIELD, "*mydrlfile1*" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;
            listHitPaths( searcher,
                          hits );

            assertEquals( 1,
                          hits.length );
        }


        final Path path2 = getBasePath( this.getClass().getSimpleName() ).resolve( "a.drl" );
        ioService().write( path2,
                           "Some cheese" );

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new WildcardQuery( new Term( FULL_TEXT_FIELD, "a*" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;
            listHitPaths( searcher,
                          hits );

            assertEquals( 1,
                          hits.length );
        }

        ( (LuceneIndex) index ).nrtRelease( searcher );
    }

}
