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
import java.util.TreeSet;

import org.junit.Test;
import org.kie.workbench.common.screens.library.api.index.LibraryValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.uberfire.backend.vfs.Path;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class FindAllLibraryAssetsSortedQueryTest
        extends BaseLibraryIndexingTest {

    private static final String TEST_PROJECT_ROOT1 = "/find/all/library/assets/sorted/query/test/mock/project1/root";

    private static final String[] FILE_NAMES = new String[]{"DRL4.drl", "RULE4.rule", "drl1.drl", "drl2.ext2", "drl3.ext3", "functions.functions", "rule3.rule"};

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

    private void setUp(String path) throws IOException, InterruptedException {
        ioService.startBatch(basePath.getFileSystem());
        //Add test files
        for (String fileName : FILE_NAMES) {
            addTestFile(path,
                        fileName);
        }
        ioService.endBatch();

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index
    }

    @Test
    public void listAllInProjectSorted() throws IOException, InterruptedException {
        setUp(TEST_PROJECT_ROOT1);

        final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                          new HashSet<ValueIndexTerm>() {{
                                                                              add(new LibraryValueRepositoryRootIndexTerm(getRepositoryRootPath(),
                                                                                                                          TermSearchType.NORMAL));
                                                                          }},
                                                                          0,
                                                                          10);

        final PageResponse<RefactoringPageRow> response = service.query(request);
        assertNotNull(response);

        // Remove duplicates and sort file names alphabetically
        Set<String> resultSet = new TreeSet<>();
        for (RefactoringPageRow row : response.getPageRowList()) {
            String fileName = ((Path) row.getValue()).getFileName();
            System.out.println(fileName);

            resultSet.add(fileName);
        }

        assertArrayEquals("Observed: " + resultSet,
                          FILE_NAMES,
                          resultSet.toArray());
    }

    @Test
    public void listAllInProjectSortedPaged() throws IOException, InterruptedException {
        setUp(TEST_PROJECT_ROOT1);

        final RefactoringPageRequest request1 = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                           new HashSet<ValueIndexTerm>() {{
                                                                               add(new LibraryValueRepositoryRootIndexTerm(getRepositoryRootPath(),
                                                                                                                           TermSearchType.NORMAL));
                                                                           }},
                                                                           0,
                                                                           4);

        final PageResponse<RefactoringPageRow> response1 = service.query(request1);
        assertNotNull(response1);

        // Remove duplicates and sort file names alphabetically
        Set<String> resultSet1 = new TreeSet<>();
        for (RefactoringPageRow row : response1.getPageRowList()) {
            String fileName = ((Path) row.getValue()).getFileName();
            System.out.println(fileName);

            resultSet1.add(fileName);
        }

        String[] expectedResult1 = new String[]{"DRL4.drl", "drl1.drl", "drl2.ext2", "drl3.ext3"};
        assertArrayEquals(expectedResult1,
                          resultSet1.toArray());

        final RefactoringPageRequest request2 = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                           new HashSet<ValueIndexTerm>() {{
                                                                               add(new LibraryValueRepositoryRootIndexTerm(getRepositoryRootPath(),
                                                                                                                           TermSearchType.NORMAL));
                                                                           }},
                                                                           4,
                                                                           4);
        final PageResponse<RefactoringPageRow> response2 = service.query(request2);
        assertNotNull(response2);

        // Remove duplicates and sort file names alphabetically
        Set<String> resultSet2 = new TreeSet<>();
        for (RefactoringPageRow row : response2.getPageRowList()) {
            String fileName = ((Path) row.getValue()).getFileName();
            System.out.println(fileName);

            resultSet2.add(fileName);
        }

        String[] expectedResult2 = new String[]{"RULE4.rule", "functions.functions", "rule3.rule"};
        assertArrayEquals(expectedResult2,
                          resultSet2.toArray());
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }
}
