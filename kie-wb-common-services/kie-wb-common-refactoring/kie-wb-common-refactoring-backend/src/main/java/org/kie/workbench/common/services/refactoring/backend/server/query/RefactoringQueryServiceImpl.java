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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.search.ClusterSegment;
import org.uberfire.paging.PageResponse;

import static org.kie.workbench.common.services.refactoring.backend.server.query.TermsCheck.*;
import static org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil.*;

@Service
@ApplicationScoped
public class RefactoringQueryServiceImpl implements RefactoringQueryService {

    private LuceneConfig config;
    private NamedQueries namedQueries;
    private PageResponse<RefactoringPageRow> emptyResponse;

    public RefactoringQueryServiceImpl() {
        //Make proxyable
    }

    @Inject
    public RefactoringQueryServiceImpl( @Named("luceneConfig") final LuceneConfig config,
                                        final NamedQueries namedQueries ) {
        this.config = PortablePreconditions.checkNotNull( "config",
                                                          config );
        this.namedQueries = PortablePreconditions.checkNotNull( "namedQueries",
                                            namedQueries );
    }

    @PostConstruct
    public void init() {
        emptyResponse = new PageResponse<RefactoringPageRow>();
        emptyResponse.setPageRowList( Collections.<RefactoringPageRow>emptyList() );
        emptyResponse.setStartRowIndex( 0 );
        emptyResponse.setTotalRowSize( 0 );
        emptyResponse.setLastPage( true );
        emptyResponse.setTotalRowSizeExact( true );
    }

    @Override
    public Set<String> getQueries() {
        return namedQueries.getQueries();
    }

    @Override
    public Set<IndexTerm> getTerms( final String queryName ) {
        return namedQueries.getTerms( queryName );
    }

    @Override
    public PageResponse<RefactoringPageRow> query( final RefactoringPageRequest request ) {
        PortablePreconditions.checkNotNull( "request",
                                            request );
        final String queryName = PortablePreconditions.checkNotNull( "queryName",
                                                                     request.getQueryName() );
        NamedQuery namedQuery = namedQueries.findNamedQuery( queryName );

        //Validate provided terms against those required for the named query
        checkTermsMatch( request.getQueryTerms(),
                         namedQuery.getTerms() );

        final Query query = namedQuery.toQuery( request.getQueryTerms(),
                                                request.useWildcards() );

        if ( searchHits( query ) > 0 ) {
            final int pageSize = request.getPageSize();
            final int startIndex = request.getStartRowIndex();
            final List<KObject> kObjects = search( query,
                                                   pageSize,
                                                   startIndex );

            final ResponseBuilder responseBuilder = namedQuery.getResponseBuilder();
            return responseBuilder.buildResponse( pageSize,
                                                  startIndex,
                                                  kObjects );
        } else {
            return emptyResponse;
        }
    }

    @Override
    public List<RefactoringPageRow> query( final String queryName,
                                           final Set<ValueIndexTerm> queryTerms,
                                           final boolean useWildcards ) {
        PortablePreconditions.checkNotNull( "queryName",
                                            queryName );
        PortablePreconditions.checkNotNull( "queryTerms",
                                            queryTerms );

        NamedQuery namedQuery = namedQueries.findNamedQuery( queryName );

        //Validate provided terms against those required for the named query
        checkTermsMatch( queryTerms,
                         namedQuery.getTerms() );

        final Query query = namedQuery.toQuery( queryTerms,
                                                useWildcards );

        final int hits = searchHits( query );
        if ( hits > 0 ) {
            final List<KObject> kObjects = search( query,
                                                   hits );

            final ResponseBuilder responseBuilder = namedQuery.getResponseBuilder();
            return responseBuilder.buildResponse( kObjects );
        } else {
            return Collections.emptyList();
        }
    }

    private int searchHits( final Query query,
                            final ClusterSegment... clusterSegments ) {
        final LuceneIndexManager indexManager = ( (LuceneIndexManager) config.getIndexManager() );
        final IndexSearcher index = indexManager.getIndexSearcher( clusterSegments );
        try {
            final TotalHitCountCollector collector = new TotalHitCountCollector();
            index.search( query,
                          collector );
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
        final LuceneIndexManager indexManager = ( (LuceneIndexManager) config.getIndexManager() );
        final TopScoreDocCollector collector = TopScoreDocCollector.create( ( startIndex + 1 ) * pageSize,
                                                                            true );
        final IndexSearcher index = indexManager.getIndexSearcher( clusterSegments );
        final List<KObject> result = new ArrayList<KObject>( pageSize );
        try {
            index.search( query,
                          collector );
            final ScoreDoc[] hits = collector.topDocs( startIndex ).scoreDocs;
            int iterations = hits.length > pageSize ? pageSize : hits.length;
            for ( int i = 0; i < iterations; i++ ) {
                result.add( toKObject( index.doc( hits[ i ].doc ) ) );
            }
        } catch ( final Exception ex ) {
            throw new RuntimeException( "Error during Query!",
                                        ex );
        } finally {
            indexManager.release( index );
        }

        return result;
    }

    private List<KObject> search( final Query query,
                                  final int totalHits,
                                  final ClusterSegment... clusterSegments ) {
        final LuceneIndexManager indexManager = ( (LuceneIndexManager) config.getIndexManager() );
        final TopScoreDocCollector collector = TopScoreDocCollector.create( totalHits,
                                                                            true );
        final IndexSearcher index = indexManager.getIndexSearcher( clusterSegments );
        final List<KObject> result = new ArrayList<KObject>();
        try {
            index.search( query,
                          collector );
            final ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for ( int i = 0; i < hits.length; i++ ) {
                result.add( toKObject( index.doc( hits[ i ].doc ) ) );
            }
        } catch ( final Exception ex ) {
            throw new RuntimeException( "Error during Query!",
                                        ex );
        } finally {
            indexManager.release( index );
        }

        return result;
    }

}
