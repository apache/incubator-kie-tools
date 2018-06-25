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

package org.kie.workbench.common.services.refactoring.backend.server.query.findresourceparts;

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
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcePartsQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

import static org.junit.Assert.*;

public class FindResourcePartsQueryInvalidIndexTermsTest extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add( new FindResourcePartsQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            } );
        }};
    }

    @Test
    public void testFindResourcePartsQueryInvalidIndexTerms() throws IOException, InterruptedException {
        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindResourcePartsQuery",
                                                                               new HashSet<ValueIndexTerm>(),
                                                                               0,
                                                                               -1 );

            try {
                service.query( request );
                fail();
            } catch ( IllegalArgumentException e ) {
                assertTrue( "Unexpected exception: " + e.getMessage(),
                        e.getMessage().startsWith("Expected '" + ValuePartIndexTerm.class.getSimpleName() + "' term was not found") );
                // and Swallow. Expected
            }
        }

        ValueIndexTerm ruleRefTerm = new ValueReferenceIndexTerm( "myRule", ResourceType.RULE );
        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindResourcePartsQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( ruleRefTerm );
                                                                               }},
                                                                               0,
                                                                               -1 );

            try {
                service.query( request );
                fail();
            } catch ( IllegalArgumentException e ) {
                assertTrue( "Unexpected exception: " + e.getMessage(),
                        e.getMessage().startsWith( "Index term '" +  ruleRefTerm.getTerm() + "' can not be used with") );
                // and Swallow. Expected
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindResourcePartsQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueReferenceIndexTerm( "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant", ResourceType.JAVA ) );
                                                                                   add( ruleRefTerm );
                                                                               }},
                                                                               0,
                                                                               -1 );

            try {
                service.query( request );
                fail();
            } catch ( IllegalArgumentException e ) {
                assertTrue( "Unexpected exception: " + e.getMessage(),
                        e.getMessage().contains(" can not be used with the " + FindResourcePartsQuery.NAME) );
                // and Swallow. Expected
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
