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
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.guvnor.common.services.project.categories.Model;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.backend.server.indexing.TestJavaFileIndexer;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

public class FindResourcesQueryValidIndexTermsTest extends BaseIndexingTest<JavaResourceTypeDefinition> {

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add( new FindResourcesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            } );
        }};
    }

    @Test
    public void testFindResourcesQueryValidIndexTerms() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = basePath.resolve( "Pojo1.java" );
        final String javaFile1 = loadText( "../Pojo1.java" );

        ioService().write( path1,
                           javaFile1 );

        final Path path2 = basePath.resolve( "Interface1.java" );
        final String javaFile2 = loadText( "../Interface1.java" );

        ioService().write( path2,
                           javaFile2 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {

            HashSet<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>();
            queryTerms.add( new ValueResourceIndexTerm(
                    "*",
                    ResourceType.JAVA,
                    TermSearchType.WILDCARD ) );

            final RefactoringPageRequest request = new RefactoringPageRequest( "FindResourcesQuery",
                                                                               queryTerms,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( "No documents found!", response  );
                assertEquals( 2,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path1 );

            } catch ( IllegalArgumentException e ) {
                e.printStackTrace();
                fail( "Could not execute query: " + e.getMessage());
            }
        }

        {

            HashSet<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>();
            queryTerms.add( new ValueResourceIndexTerm(
                    "org.kie.workbench.common.screens.datamodeller.backend.server.indexing",
                    ResourceType.JAVA,
                    // Prefix! :)
                    TermSearchType.PREFIX ) );

            final RefactoringPageRequest request = new RefactoringPageRequest( "FindResourcesQuery",
                                                                               queryTerms,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( "No documents found!", response  );
                assertEquals( 2,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path1 );

            } catch ( IllegalArgumentException e ) {
                e.printStackTrace();
                fail( "Could not execute query: " + e.getMessage());
            }
        }

        {

            HashSet<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>();
            queryTerms.add( new ValueResourceIndexTerm(
                    "*Pojo1",
                    ResourceType.JAVA,
                    // Wildcard! :)
                    TermSearchType.WILDCARD ) );

            final RefactoringPageRequest request = new RefactoringPageRequest( "FindResourcesQuery",
                                                                               queryTerms,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 1,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path1 );

            } catch ( IllegalArgumentException e ) {
                e.printStackTrace();
                fail( "Could not execute query: " + e.getMessage());
            }
        }

    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestJavaFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return Collections.emptyMap();
    }

    @Override
    protected JavaResourceTypeDefinition getResourceTypeDefinition() {
        return new JavaResourceTypeDefinition(new Model());
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }

}
