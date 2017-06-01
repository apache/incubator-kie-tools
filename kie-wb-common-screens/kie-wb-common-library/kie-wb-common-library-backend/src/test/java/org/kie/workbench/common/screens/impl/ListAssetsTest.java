/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.search.Query;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.AbstractFindQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.FullFileNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFullFileNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.*;

public class ListAssetsTest
        extends BaseLibraryIndexingTest {

    private static final String TEST_PROJECT_ROOT = "list/assets/test/a/mock/project/root";

    @Override
    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new TestFindFilesQuery());
        }};
    }

    @Test
    public void listFilesShouldNotHaveDuplicatesFromLibraryIndexer() throws IOException, InterruptedException {

        //Add test files
        addTestFile(TEST_PROJECT_ROOT,
                    "rule1.rule");
        addTestFile(TEST_PROJECT_ROOT,
                    "rule2.rule");
        addTestFile(TEST_PROJECT_ROOT,
                    "rule3.rule");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(TestFindFilesQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueFullFileNameIndexTerm("*.rule",
                                                                                                                     ValueIndexTerm.TermSearchType.WILDCARD));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);

                for (RefactoringPageRow refactoringPageRow : response.getPageRowList()) {
                    System.out.println(refactoringPageRow.getValue());
                }

                assertEquals(3,
                             response.getPageRowList().size());
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

    /**
     * This Query searches for "filename" index entries
     */
    private class TestFindFilesQuery extends AbstractFindQuery implements NamedQuery {

        public static final String NAME = "TestFindFilesQuery";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void validateTerms(final Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
            checkInvalidAndRequiredTerms(queryTerms,
                                         NAME,
                                         new String[]{
                                                 FullFileNameIndexTerm.TERM,
                                                 null // not required
                                         },
                                         (t) -> (t instanceof ValueFullFileNameIndexTerm));
        }

        @Override
        public Query toQuery(final Set<ValueIndexTerm> terms) {
            return buildFromMultipleTerms(terms);
        }

        @Override
        public ResponseBuilder getResponseBuilder() {
            return new DefaultResponseBuilder(ioService());
        }
    }
}
