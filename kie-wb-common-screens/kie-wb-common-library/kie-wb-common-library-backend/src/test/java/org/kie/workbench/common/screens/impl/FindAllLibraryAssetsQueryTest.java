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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueFileNameIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllLibraryAssetsQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.impl.KObjectImpl;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FindAllLibraryAssetsQueryTest
        extends BaseLibraryIndexingTest {

    private static final String TEST_MODULE_ROOT = "/find/all/library/assets/query/test/a/mock/module/root";

    @Override
    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new FindAllLibraryAssetsQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
        }};
    }

    @Override
    @After
    public void dispose() {
        super.dispose();
        super.cleanup();
    }

    @Test
    public void listAllInModule() throws IOException, InterruptedException {

        //Add test files
        addTestFile(TEST_MODULE_ROOT,
                    "drl1.drl");
        addTestFile(TEST_MODULE_ROOT,
                    "drl2.ext2");
        addTestFile(TEST_MODULE_ROOT,
                    "functions.functions");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new LibraryValueRepositoryRootIndexTerm(getRepositoryRootPath(),
                                                                                                                              TermSearchType.NORMAL));
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

    @Test
    public void cleanupLibraryResults() {
        //This is a temporary way to cleanup index results
        //for library assets list and count.
        //In cluster environment library index each file more than once.
        //The index should be revised on next release (7.6).
        KObject k1 = new KObjectImpl("",
                                     "",
                                     "clusterId1",
                                     "",
                                     "key1",
                                     new ArrayList<>(),
                                     false);
        KObject k2 = new KObjectImpl("",
                                     "",
                                     "clusterId2",
                                     "",
                                     "key2",
                                     new ArrayList<>(),
                                     false);
        List<KObject> kObjects = service.distinct(Arrays.asList(k1,
                                                                k1,
                                                                k2));
        assertEquals(2,
                     kObjects.size());
        assertEquals(k1,
                     kObjects.get(0));
        assertEquals(k2,
                     kObjects.get(1));
    }

    @Test
    public void filterFilesFromModule() throws IOException, InterruptedException {

        //Add test files
        addTestFile(TEST_MODULE_ROOT,
                    "rule1.rule");
        addTestFile(TEST_MODULE_ROOT,
                    "rule2.rule");
        addTestFile(TEST_MODULE_ROOT,
                    "functions.functions");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new LibraryValueRepositoryRootIndexTerm(getRepositoryRootPath(),
                                                                                                                              TermSearchType.NORMAL));
                                                                                  add(new LibraryValueFileNameIndexTerm("*rule*",
                                                                                                                        TermSearchType.WILDCARD));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);

                for (RefactoringPageRow refactoringPageRow : response.getPageRowList()) {
                    System.out.println(refactoringPageRow.getValue());
                }

                assertEquals(2,
                             response.getPageRowList().size());
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }
}
