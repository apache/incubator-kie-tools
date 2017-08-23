/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueBranchNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.search.ClusterSegment;
import org.uberfire.paging.PageResponse;

import static org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil.toKObject;

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
        this.config = PortablePreconditions.checkNotNull( "config", config );
        this.namedQueries = PortablePreconditions.checkNotNull( "namedQueries", namedQueries );
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

    public Set<String> getQueries() {
        return namedQueries.getQueries();
    }

    @Override
    public PageResponse<RefactoringPageRow> query( final RefactoringPageRequest request ) {
        PortablePreconditions.checkNotNull( "request",
                                            request );
        final String queryName = PortablePreconditions.checkNotNull( "queryName",
                                                                     request.getQueryName() );
        final NamedQuery namedQuery = namedQueries.findNamedQuery( queryName );

        //Validate provided terms against those required for the named query
        namedQuery.validateTerms(request.getQueryTerms());

        final Query query = namedQuery.toQuery( request.getQueryTerms() );
        final Sort sort = namedQuery.getSortOrder();

        final int pageSize = request.getPageSize();
        final int startIndex = request.getStartRowIndex();

        final List<KObject> kObjects
            = search(query,
                     sort,
                     () -> ( startIndex ), // start index of docs to get
                     (numHits) -> ( numHits - startIndex > pageSize ? pageSize : numHits - startIndex ) // num docs to add to response
                    );

        if( ! kObjects.isEmpty() ) {
            final ResponseBuilder responseBuilder = namedQuery.getResponseBuilder();
            return responseBuilder.buildResponse( pageSize,
                                                  startIndex,
                                                  kObjects );
        } else {
            return emptyResponse;
        }
    }

    @Override
    public List<RefactoringPageRow> query( final String queryName, final Set<ValueIndexTerm> queryTerms ) {
        PortablePreconditions.checkNotNull( "queryName",
                                            queryName );
        PortablePreconditions.checkNotNull( "queryTerms",
                                            queryTerms );

        final NamedQuery namedQuery = namedQueries.findNamedQuery( queryName );

        //Validate provided terms against those required for the named query
        namedQuery.validateTerms(queryTerms);

        final Query query = namedQuery.toQuery( queryTerms );
        final Sort sort = namedQuery.getSortOrder();

        final List<KObject> kObjects
            = search(query,
                     sort,
                     () -> (0), // start index of docs to get
                     (numHits) -> (numHits) // num docs to add to response
                    );

        if( ! kObjects.isEmpty() ) {
            final ResponseBuilder responseBuilder = namedQuery.getResponseBuilder();
            return responseBuilder.buildResponse( kObjects );
        } else {
            return Collections.emptyList();
        }
    }

    private List<KObject> search(final Query query,
                                 final Sort sort,
                                 final Supplier<Integer> startIndexSupplier,
                                 final IntFunction<Integer> numOfHitsToReturnSupplier,
                                 final ClusterSegment... clusterSegments) {

        final LuceneIndexManager indexManager = ( (LuceneIndexManager) config.getIndexManager() );
        final IndexSearcher index = indexManager.getIndexSearcher( clusterSegments );


        final List<KObject> result = new ArrayList<KObject>();
        try {
            final TotalHitCountCollector totalHitCountCollector = new TotalHitCountCollector();
            index.search( query,
                          totalHitCountCollector );

            int numHits = totalHitCountCollector.getTotalHits();
            if( numHits > 0 ) {
                final TopFieldDocs docsHit = index.search(query,
                                                          Integer.MAX_VALUE,
                                                          sort );
                final int startIndex = startIndexSupplier.get();
                final int numOfHitsToReturn = numOfHitsToReturnSupplier.apply(docsHit.totalHits);

                for ( int i = startIndex; i < startIndex+numOfHitsToReturn; i++ ) {
                    result.add( toKObject( index.doc(docsHit.scoreDocs[ i ].doc) ) );
                }
            }
        } catch ( final Exception ex ) {
            throw new RuntimeException( "Error during Query!",
                                        ex );
        } finally {
            indexManager.release( index );
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.service.RefactoringQueryService#queryToPageResponse(org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest)
     */
    @Override
    public PageResponse<RefactoringPageRow> queryToPageResponse(QueryOperationRequest queryOpRequest) {
        final RefactoringPageRequest request = convertToRefactoringPageRequest(queryOpRequest);

        final PageResponse<RefactoringPageRow> response = query( request );

        return response;
    }

    @Override
    public List<RefactoringPageRow> queryToList( final QueryOperationRequest queryOpRequest ) {
        final RefactoringPageRequest request = convertToRefactoringPageRequest(queryOpRequest);

        final List<RefactoringPageRow> response = query( request.getQueryName(), request.getQueryTerms() );

        return response;
    }

    private RefactoringPageRequest convertToRefactoringPageRequest(QueryOperationRequest refOpRequest) {
        RefactoringPageRequest request = new RefactoringPageRequest(
                FindAllChangeImpactQuery.NAME,
                new HashSet<>(),
                refOpRequest.getStartRowIndex(),
                refOpRequest.getPageSize());

        request.getQueryTerms().addAll(refOpRequest.getQueryTerms());

        // add project info
        String projectName = refOpRequest.getProjectName();
        if( projectName != null && projectName != QueryOperationRequest.ALL ) {
            ValueProjectNameIndexTerm  valueIndexTerm = new ValueProjectNameIndexTerm(projectName);
            Set<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>(1);
            queryTerms.add(valueIndexTerm);
            request.getQueryTerms().addAll(queryTerms);
        }

        String projectRootPathURI = refOpRequest.getProjectRootPathURI();
        if( projectRootPathURI != null && projectRootPathURI != QueryOperationRequest.ALL ) {
            ValueProjectRootPathIndexTerm valueIndexTerm = new ValueProjectRootPathIndexTerm( projectRootPathURI );
            Set<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>(1);
            queryTerms.add(valueIndexTerm);
            request.getQueryTerms().addAll(queryTerms);
        }

        String branchName = refOpRequest.getBranchName();
        if( branchName != null && branchName != QueryOperationRequest.ALL ) {
            ValueBranchNameIndexTerm  valueIndexTerm = new ValueBranchNameIndexTerm(branchName);
            Set<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>(1);
            queryTerms.add(valueIndexTerm);
            request.getQueryTerms().addAll(queryTerms);
        }

        return request;
    }


}
