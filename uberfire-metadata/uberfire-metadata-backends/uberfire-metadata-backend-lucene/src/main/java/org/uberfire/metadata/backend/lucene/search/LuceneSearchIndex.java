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

package org.uberfire.metadata.backend.lucene.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.sandbox.queries.regex.RegexQuery;
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
import org.uberfire.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.search.ClusterSegment;
import org.uberfire.metadata.search.DateRange;
import org.uberfire.metadata.search.SearchIndex;

import static java.util.Collections.*;
import static org.apache.lucene.search.BooleanClause.Occur.*;
import static org.apache.lucene.search.NumericRangeQuery.*;
import static org.uberfire.commons.regex.util.GlobToRegEx.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.metadata.backend.lucene.util.KObjectUtil.*;
import static org.uberfire.metadata.engine.MetaIndexEngine.*;

/**
 *
 */
public class LuceneSearchIndex implements SearchIndex {

    private final LuceneIndexManager indexManager;
    private final QueryParser queryParser;

    public LuceneSearchIndex( final LuceneIndexManager indexManager,
                              final Analyzer analyzer ) {
        this.indexManager = checkNotNull( "lucene", indexManager );
        this.queryParser = new QueryParser( Version.LUCENE_40, FULL_TEXT_FIELD, analyzer );
        this.queryParser.setAllowLeadingWildcard( true );
    }

    @Override
    public List<KObject> searchByAttrs( final Map<String, ?> attrs,
                                        final int pageSize,
                                        final int startIndex,
                                        final ClusterSegment... clusterSegments ) {
        if ( attrs == null || attrs.size() == 0 ) {
            return emptyList();
        }
        return search( buildQuery( attrs, clusterSegments ), pageSize, startIndex, clusterSegments );
    }

    @Override
    public List<KObject> fullTextSearch( final String term,
                                         final int pageSize,
                                         final int startIndex,
                                         final ClusterSegment... clusterSegments ) {
        return search( buildQuery( term, clusterSegments ), pageSize, startIndex, clusterSegments );
    }

    @Override
    public int searchByAttrsHits( final Map<String, ?> attrs,
                                  final ClusterSegment... clusterSegments ) {
        if ( attrs == null || attrs.size() == 0 ) {
            return 0;
        }
        return searchHits( buildQuery( attrs, clusterSegments ), clusterSegments );
    }

    @Override
    public int fullTextSearchHits( final String term,
                                   final ClusterSegment... clusterSegments ) {
        return searchHits( buildQuery( term, clusterSegments ), clusterSegments );
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
                                  final int pageSize,
                                  final int startIndex,
                                  final ClusterSegment... clusterSegments ) {
        final TopScoreDocCollector collector = TopScoreDocCollector.create( ( startIndex + 1 ) * pageSize, true );
        final IndexSearcher index = indexManager.getIndexSearcher( clusterSegments );
        final List<KObject> result = new ArrayList<KObject>( pageSize );
        try {
            index.search( query, collector );
            final ScoreDoc[] hits = collector.topDocs( startIndex ).scoreDocs;
            int iterations = hits.length > pageSize ? pageSize : hits.length;
            for ( int i = 0; i < iterations; i++ ) {
                result.add( toKObject( index.doc( hits[ i ].doc ) ) );
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
                if ( entry.getKey().equalsIgnoreCase( LuceneIndex.CUSTOM_FIELD_FILENAME ) ) {
                    query.add( new RegexQuery( new Term( entry.getKey(), globToRegex( entry.getValue().toString().toLowerCase() ) ) ), MUST );
                } else {
                    query.add( new WildcardQuery( new Term( entry.getKey(), entry.getValue().toString() ) ), MUST );
                }
            } else if ( entry.getValue() instanceof Boolean ) {
                query.add( new TermQuery( new Term( entry.getKey(), ( (Boolean) entry.getValue() ) ? "0" : "1" ) ), MUST );
            }
        }
        return composeQuery( query, clusterSegments );
    }

    private Query buildQuery( final String term,
                              final ClusterSegment... clusterSegments ) {

        Query fullText;
        try {
            fullText = queryParser.parse( term );
        } catch ( ParseException ex ) {
            fullText = new WildcardQuery( new Term( FULL_TEXT_FIELD, format( term ) ) );
        }

        return composeQuery( fullText, clusterSegments );
    }

    private Query composeQuery( final Query query,
                                final ClusterSegment... clusterSegments ) {
        if ( clusterSegments != null && clusterSegments.length > 0 ) {
            final BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add( query, MUST );

            final BooleanQuery complement;

            if ( clusterSegments.length == 1 ) {
                complement = booleanQuery;
            } else {
                complement = new BooleanQuery();
            }

            for ( final ClusterSegment clusterSegment : clusterSegments ) {
                final BooleanQuery clusterBoolean = new BooleanQuery();
                if ( clusterSegment.getClusterId() != null ) {
                    final Query cluster = new TermQuery( new Term( "cluster.id", clusterSegment.getClusterId() ) );
                    clusterBoolean.add( cluster, MUST );
                }
                if ( clusterSegment.segmentIds() != null && clusterSegment.segmentIds().length > 0 ) {
                    if ( clusterSegment.segmentIds().length == 1 ) {
                        final Query segment = new TermQuery( new Term( "segment.id", clusterSegment.segmentIds()[ 0 ] ) );
                        clusterBoolean.add( segment, MUST );
                    } else {
                        final BooleanQuery segments = new BooleanQuery();
                        for ( final String segmentId : clusterSegment.segmentIds() ) {
                            final Query segment = new TermQuery( new Term( "segment.id", segmentId ) );
                            segments.add( segment, BooleanClause.Occur.SHOULD );
                        }
                        clusterBoolean.add( segments, MUST );
                    }
                }
                complement.add( clusterBoolean, MUST );
            }

            if ( clusterSegments.length == 1 ) {
                return complement;
            }

            booleanQuery.add( complement, MUST );
            return booleanQuery;
        }

        return query;
    }

    private String format( final String term ) {
        return term.toLowerCase();
    }

}
