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

package org.uberfire.ext.metadata.backend.lucene.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.search.ClusterSegment;
import org.uberfire.ext.metadata.search.DateRange;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.ext.metadata.search.SearchIndex;

import static java.util.Collections.*;
import static org.apache.lucene.search.BooleanClause.Occur.*;
import static org.apache.lucene.search.NumericRangeQuery.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil.*;
import static org.uberfire.ext.metadata.engine.MetaIndexEngine.*;

/**
 *
 */
public class LuceneSearchIndex implements SearchIndex {

    private final LuceneIndexManager indexManager;
    private final QueryParser queryParser;

    public LuceneSearchIndex( final LuceneIndexManager indexManager,
                              final Analyzer analyzer ) {
        this.indexManager = checkNotNull( "lucene",
                                          indexManager );
        this.queryParser = new QueryParser( Version.LUCENE_40, FULL_TEXT_FIELD, analyzer );
        this.queryParser.setAllowLeadingWildcard( true );
    }

    @Override
    public List<KObject> searchByAttrs( final Map<String, ?> attrs,
                                        final IOSearchService.Filter filter,
                                        final ClusterSegment... clusterSegments ) {
        if ( clusterSegments == null || clusterSegments.length == 0 ) {
            return emptyList();
        }
        if ( attrs == null || attrs.size() == 0 ) {
            return emptyList();
        }
        final int totalNumHitsEstimate = searchByAttrsHits( attrs,
                                                            clusterSegments );
        return search( buildQuery( attrs,
                                   clusterSegments ),
                       totalNumHitsEstimate,
                       filter,
                       clusterSegments );
    }

    @Override
    public List<KObject> fullTextSearch( final String term,
                                         final IOSearchService.Filter filter,
                                         final ClusterSegment... clusterSegments ) {
        if ( clusterSegments == null || clusterSegments.length == 0 ) {
            return emptyList();
        }
        final int totalNumHitsEstimate = fullTextSearchHits( term,
                                                             clusterSegments );
        return search( buildQuery( term,
                                   clusterSegments ),
                       totalNumHitsEstimate,
                       filter,
                       clusterSegments );
    }

    @Override
    public int searchByAttrsHits( final Map<String, ?> attrs,
                                  final ClusterSegment... clusterSegments ) {
        if ( clusterSegments == null || clusterSegments.length == 0 ) {
            return 0;
        }
        if ( attrs == null || attrs.size() == 0 ) {
            return 0;
        }
        return searchHits( buildQuery( attrs,
                                       clusterSegments ),
                           clusterSegments );
    }

    @Override
    public int fullTextSearchHits( final String term,
                                   final ClusterSegment... clusterSegments ) {
        if ( clusterSegments == null || clusterSegments.length == 0 ) {
            return 0;
        }
        return searchHits( buildQuery( term,
                                       clusterSegments ),
                           clusterSegments );
    }

    private int searchHits( final Query query,
                            final ClusterSegment... clusterSegments ) {
        final IndexSearcher index = indexManager.getIndexSearcher( clusterSegments );
        try {
            final TotalHitCountCollector collector = new TotalHitCountCollector();
            index.search( query, collector );
            return collector.getTotalHits();
        } catch ( final Exception ex ) {
            throw new RuntimeException( "Error during Query!", ex );
        } finally {
            indexManager.release( index );
        }
    }

    private List<KObject> search( final Query query,
                                  final int totalNumHitsEstimate,
                                  final IOSearchService.Filter filter,
                                  final ClusterSegment... clusterSegments ) {
        final TopScoreDocCollector collector = TopScoreDocCollector.create( totalNumHitsEstimate,
                                                                            true );
        final IndexSearcher index = indexManager.getIndexSearcher( clusterSegments );
        final List<KObject> result = new ArrayList<KObject>();
        try {
            index.search( query,
                          collector );
            final ScoreDoc[] hits = collector.topDocs( 0 ).scoreDocs;
            for ( int i = 0; i < hits.length; i++ ) {
                final KObject kObject = toKObject( index.doc( hits[ i ].doc ) );
                if ( filter.accept( kObject ) ) {
                    result.add( kObject );
                }
            }
        } catch ( final Exception ex ) {
            throw new RuntimeException( "Error during Query!", ex );
        } finally {
            indexManager.release( index );
        }

        return result;
    }

    private Query buildQuery( final Map<String, ?> attrs,
                              final ClusterSegment... clusterSegments ) {
        final BooleanQuery query = new BooleanQuery();
        for ( final Map.Entry<String, ?> entry : attrs.entrySet() ) {
            if ( entry.getValue() instanceof DateRange ) {
                final Long from = ( (DateRange) entry.getValue() ).after().getTime();
                final Long to = ( (DateRange) entry.getValue() ).before().getTime();
                query.add( newLongRange( entry.getKey(), from, to, true, true ), MUST );
            } else if ( entry.getValue() instanceof String ) {
                query.add( new WildcardQuery( new Term( buildTerm( entry.getKey() ), entry.getValue().toString() ) ), MUST );
            } else if ( entry.getValue() instanceof Boolean ) {
                query.add( new TermQuery( new Term( buildTerm( entry.getKey() ), ( (Boolean) entry.getValue() ) ? "0" : "1" ) ), MUST );
            }
        }
        return composeQuery( query, clusterSegments );
    }

    private Query buildQuery( final String term,
                              final ClusterSegment... clusterSegments ) {

        Query fullText;
        try {
            fullText = queryParser.parse( term );
            if ( fullText.toString().isEmpty() ) {
                fullText = new WildcardQuery( new Term( FULL_TEXT_FIELD, format( term ) + "*" ) );
            }
        } catch ( ParseException ex ) {
            fullText = new WildcardQuery( new Term( FULL_TEXT_FIELD, format( term ) ) );
        }

        return composeQuery( fullText, clusterSegments );
    }

    // DublinCoreView has field names containing the "[" and "]" characters.
    // Lucene reserves use of "[" and "]" and hence we need to escape them in the Term.
    private String buildTerm( final String term ) {
        String _term = term;
        _term = _term.replaceAll( "\\[",
                                  "\\\\[" );
        _term = _term.replaceAll( "\\]",
                                  "\\\\]" );
        return _term;
    }

    private Query composeQuery( final Query query,
                                final ClusterSegment... clusterSegments ) {
        if ( clusterSegments == null || clusterSegments.length == 0 ) {
            return query;
        }

        final BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add( query,
                          MUST );

        final BooleanClause.Occur occur = ( clusterSegments.length == 1 ? MUST : SHOULD );
        for ( ClusterSegment clusterSegment : clusterSegments ) {
            final BooleanQuery clusterSegmentQuery = new BooleanQuery();
            addClusterIdTerms( clusterSegmentQuery,
                               clusterSegment );
            addSegmentIdTerms( clusterSegmentQuery,
                               clusterSegment );
            booleanQuery.add( clusterSegmentQuery,
                              occur );
        }

        return booleanQuery;
    }

    private void addClusterIdTerms( final BooleanQuery query,
                                    final ClusterSegment clusterSegment ) {
        if ( clusterSegment.getClusterId() != null ) {
            final Query cluster = new TermQuery( new Term( "cluster.id",
                                                           clusterSegment.getClusterId() ) );
            query.add( cluster,
                       MUST );
        }
    }

    private void addSegmentIdTerms( final BooleanQuery query,
                                    final ClusterSegment clusterSegment ) {
        if ( clusterSegment.segmentIds() == null || clusterSegment.segmentIds().length == 0 ) {
            return;
        }

        if ( clusterSegment.segmentIds().length == 1 ) {
            final Query segment = new TermQuery( new Term( "segment.id",
                                                           clusterSegment.segmentIds()[ 0 ] ) );
            query.add( segment,
                       MUST );
        } else {
            final BooleanQuery segments = new BooleanQuery();
            for ( final String segmentId : clusterSegment.segmentIds() ) {
                final Query segment = new TermQuery( new Term( "segment.id",
                                                               segmentId ) );
                segments.add( segment,
                              SHOULD );
            }
            query.add( segments,
                       MUST );
        }
    }

    private String format( final String term ) {
        return term.toLowerCase();
    }

}
