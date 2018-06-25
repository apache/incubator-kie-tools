/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.refactoring.backend.server.query.findresources;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
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

import static org.junit.Assert.*;

public class FindResourcesQueryValidIndexTermsTest extends BaseIndexingTest<TestDrlFileTypeDefinition> {

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
    public void testQueryValidRuleAttributeIndexTerms() throws IOException, InterruptedException {
        //Add test files
        final Path [] path = {
                basePath.resolve( "drl1.drl" ),
                basePath.resolve( "drl2.drl" ),
                basePath.resolve( "drl3.drl" ),
                basePath.resolve( "functions.drl" )
        };

        final String [] content = {
                loadText( "drl1.drl" ),
                loadText( "drl2.drl" ),
                loadText( "drl3.drl" ),
                loadText( "functions.drl" )
        };

        for( int i = 0; i < path.length; ++i ) {
            ioService().write( path[i], content[i] );
        }

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( FindResourcesQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueResourceIndexTerm(
                                                                                           "org.kie.workbench.mock.package.myRule",
                                                                                           ResourceType.RULE ) );
                                                                               }},
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 1,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path[0] );
            } catch ( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }


        {
            final RefactoringPageRequest request = new RefactoringPageRequest( FindResourcesQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueResourceIndexTerm(
                                                                                           "org.kie.workbench.mock.package.myRule*",
                                                                                           ResourceType.RULE,
                                                                                           TermSearchType.WILDCARD) );
                                                                               }},
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 3,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path[0] );
                assertResponseContains( response.getPageRowList(),
                                        path[1] );
                assertResponseContains( response.getPageRowList(),
                                        path[2] );
            } catch ( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( FindResourcesQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueResourceIndexTerm(
                                                                                           "org.kie.workbench.mock.package.f4",
                                                                                           ResourceType.FUNCTION,
                                                                                           TermSearchType.WILDCARD) );
                                                                               }},
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 1,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path[3] );
            } catch ( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestDrlFileIndexer();
    }

    @Override
    protected TestDrlFileTypeDefinition getResourceTypeDefinition() {
        return new TestDrlFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }

}
