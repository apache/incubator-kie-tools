package org.uberfire.metadata.backend.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Test;
import org.uberfire.metadata.backend.lucene.setups.BaseLuceneSetup;

import static org.apache.lucene.util.Version.*;
import static org.junit.Assert.*;

/**
 *
 */
public abstract class BaseLuceneSetupTest {

    @Test
    public void test() throws IOException, ParseException {
        final BaseLuceneSetup config = getLuceneSetup();

        final Document doc = new Document();

        final String id = "unique.id.here";

        doc.add( new StringField( "id", id, Field.Store.YES ) );
        doc.add( new StringField( "type", "kie.Path", Field.Store.YES ) );
        doc.add( new TextField( "content", "some value here that i will query for 1900.", Field.Store.YES ) );

        config.indexDocument( id, doc );

        assertNotNull( config.nrtSearcher().doc( 0 ) );

        config.deleteIfExists( id );

        try {
            config.nrtSearcher().doc( 0 );
            fail( "can't find doc" );
        } catch ( IllegalArgumentException e ) {
        }

        config.indexDocument( id, doc );

        assertNotNull( config.nrtSearcher().doc( 0 ) );

        final IndexSearcher searcher = config.nrtSearcher();

        final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

        searcher.search( new TermQuery( new Term( "content", "value" ) ), collector );

        final ScoreDoc[] hits = collector.topDocs().scoreDocs;

        assertEquals( 1, hits.length );

        final TopScoreDocCollector collector2 = TopScoreDocCollector.create( 10, true );

        Query q = new QueryParser( LUCENE_40, "content", config.getAnalyzer() ).parse( "some" );
        searcher.search( q, collector2 );

        final ScoreDoc[] hits2 = collector2.topDocs().scoreDocs;

        assertEquals( 1, hits2.length );
    }

    protected abstract BaseLuceneSetup getLuceneSetup();

}
