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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
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
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.search.ClusterSegment;
import org.uberfire.paging.PageResponse;

import static org.uberfire.metadata.backend.lucene.util.KObjectUtil.*;

@Service
@ApplicationScoped
public class RefactoringQueryServiceImpl implements RefactoringQueryService {

    private LuceneConfig config;
    private Set<NamedQuery> namedQueries = new HashSet<NamedQuery>();
    private PageResponse<RefactoringPageRow> emptyResponse = null;

    public RefactoringQueryServiceImpl() {
        //Make proxyable
    }

    @Inject
    public RefactoringQueryServiceImpl( @Named("luceneConfig") final LuceneConfig config,
                                        @Any final Instance<NamedQuery> namedQueries ) {
        this.config = PortablePreconditions.checkNotNull( "config",
                                                          config );
        PortablePreconditions.checkNotNull( "namedQueries",
                                            namedQueries );
        for ( NamedQuery namedQuery : namedQueries ) {
            this.namedQueries.add( namedQuery );
        }
    }

    @PostConstruct
    private void init() {
        emptyResponse = new PageResponse<RefactoringPageRow>();
        emptyResponse.setPageRowList( Collections.<RefactoringPageRow>emptyList() );
        emptyResponse.setStartRowIndex( 0 );
        emptyResponse.setTotalRowSize( 0 );
        emptyResponse.setLastPage( true );
        emptyResponse.setTotalRowSizeExact( true );
    }

    @Override
    public Set<String> getQueries() {
        final Set<String> queryNames = new HashSet<String>();
        for ( NamedQuery namedQuery : namedQueries ) {
            queryNames.add( namedQuery.getName() );
        }
        return queryNames;
    }

    @Override
    public Set<IndexTerm> getTerms( final String queryName ) {
        for ( NamedQuery namedQuery : namedQueries ) {
            if ( namedQuery.getName().equals( queryName ) ) {
                return namedQuery.getTerms();
            }
        }
        throw new IllegalArgumentException( "Named Query '" + queryName + "' does not exist." );
    }

    @Override
    public PageResponse<RefactoringPageRow> query( final RefactoringPageRequest request ) {
        PortablePreconditions.checkNotNull( "request",
                                            request );
        final String queryName = PortablePreconditions.checkNotNull( "queryName",
                                                                     request.getQueryName() );
        NamedQuery namedQuery = null;
        for ( NamedQuery nq : namedQueries ) {
            if ( nq.getName().equals( queryName ) ) {
                namedQuery = nq;
                break;
            }
        }
        if ( namedQuery == null ) {
            throw new IllegalArgumentException( "Named Query '" + queryName + "' does not exist." );
        }

        //Validate provided terms against those required for the named query
        final Set<IndexTerm> namedQueryTerms = namedQuery.getTerms();
        final Set<ValueIndexTerm> queryTerms = request.getQueryTerms();
        for ( IndexTerm term : namedQueryTerms ) {
            if ( !valueTermsContainsRequiredTerm( queryTerms,
                                                  term ) ) {
                throw new IllegalArgumentException( "Expected IndexTerm '" + term.getTerm() + "' was not found." );
            }
        }

        //Validate provided terms against those required for the named query
        for ( ValueIndexTerm term : queryTerms ) {
            if ( !requiredTermsContainsValueTerm( namedQueryTerms,
                                                  term ) ) {
                //log.warning - term will not be used
            }
        }

        final Query query = namedQuery.toQuery( queryTerms,
                                                request.useWildcards() );

        final int hits = searchHits( query );
        if ( hits > 0 ) {
            final int pageSize = request.getPageSize();
            final int startIndex = request.getStartRowIndex();
            final List<KObject> kObjects = search( query,
                                                   pageSize,
                                                   startIndex );

            final ResponseBuilder responseBuilder = namedQuery.getResponseBuilder();
            return responseBuilder.buildResponse( pageSize,
                                                  startIndex,
                                                  kObjects );
        }
        return emptyResponse;
    }

    @Override
    public List<RefactoringPageRow> query( final String queryName,
                                           final Set<ValueIndexTerm> queryTerms,
                                           final boolean useWildcards ) {
        PortablePreconditions.checkNotNull( "queryName",
                                            queryName );
        PortablePreconditions.checkNotNull( "queryTerms",
                                            queryTerms );

        NamedQuery namedQuery = null;
        for ( NamedQuery nq : namedQueries ) {
            if ( nq.getName().equals( queryName ) ) {
                namedQuery = nq;
                break;
            }
        }
        if ( namedQuery == null ) {
            throw new IllegalArgumentException( "Named Query '" + queryName + "' does not exist." );
        }

        //Validate provided terms against those required for the named query
        final Set<IndexTerm> namedQueryTerms = namedQuery.getTerms();
        for ( IndexTerm term : namedQueryTerms ) {
            if ( !valueTermsContainsRequiredTerm( queryTerms,
                                                  term ) ) {
                throw new IllegalArgumentException( "Expected IndexTerm '" + term.getTerm() + "' was not found." );
            }
        }

        //Validate provided terms against those required for the named query
        for ( ValueIndexTerm term : queryTerms ) {
            if ( !requiredTermsContainsValueTerm( namedQueryTerms,
                                                  term ) ) {
                //log.warning - term will not be used
            }
        }

        final Query query = namedQuery.toQuery( queryTerms,
                                                useWildcards );

        final int hits = searchHits( query );
        if ( hits > 0 ) {
            final List<KObject> kObjects = search( query,
                                                   hits );

            final ResponseBuilder responseBuilder = namedQuery.getResponseBuilder();
            return responseBuilder.buildResponse( kObjects );
        }
        return Collections.emptyList();
    }

    private boolean valueTermsContainsRequiredTerm( final Set<ValueIndexTerm> providedTerms,
                                                    final IndexTerm requiredTerm ) {
        for ( ValueIndexTerm valueTerm : providedTerms ) {
            if ( valueTerm.getTerm().equals( requiredTerm.getTerm() ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean requiredTermsContainsValueTerm( final Set<IndexTerm> requiredTerms,
                                                    final ValueIndexTerm providedTerm ) {
        for ( IndexTerm valueTerm : requiredTerms ) {
            if ( valueTerm.getTerm().equals( providedTerm.getTerm() ) ) {
                return true;
            }
        }
        return false;
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
