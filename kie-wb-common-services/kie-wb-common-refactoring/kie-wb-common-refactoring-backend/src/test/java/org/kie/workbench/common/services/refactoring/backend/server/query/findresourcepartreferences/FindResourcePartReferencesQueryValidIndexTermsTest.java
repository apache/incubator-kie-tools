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

package org.kie.workbench.common.services.refactoring.backend.server.query.findresourcepartreferences;

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
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcePartReferencesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.*;

public class FindResourcePartReferencesQueryValidIndexTermsTest
        extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add( new FindResourcePartReferencesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            } );
        }};
    }

    @Test
    public void testFindResourcePartReferencesQueryValidIndexTerms() throws IOException, InterruptedException {
        //Add test files
        Path [] path = {
                basePath.resolve( "drl1.drl" ),
                basePath.resolve( "drl2.drl" ),
                basePath.resolve( "drl3.drl" )
        };
        String [] content = {
                loadText( "../findresourceparts/drl1.drl" ),
                loadText( "../findresourceparts/drl2.drl" ),
                loadText( "../findresourceparts/drl3.drl" )
        };
        for( int i = 0; i < path.length; ++i ) {
            ioService().write( path[i], content[i] );
        }

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( FindResourcePartReferencesQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValuePartReferenceIndexTerm(
                                                                                           "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant",
                                                                                           "age", PartType.FIELD ) );
                                                                               }},
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 2,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path[0] );
                assertResponseContains( response.getPageRowList(),
                                        path[1] );

            } catch ( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( FindResourcePartReferencesQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueSharedPartIndexTerm(
                                                                                           "myRuleFlowGroup",
                                                                                           PartType.RULEFLOW_GROUP ) );
                                                                               }},
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 2,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path[0] );
                assertResponseContains( response.getPageRowList(),
                                        path[2] );

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
