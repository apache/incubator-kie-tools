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

import org.junit.Test;
import org.kie.workbench.common.screens.library.api.index.LibraryValueProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFullFileNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FindAllLibraryAssetsQueryTest
        extends BaseLibraryIndexingTest {

    private static final String SOME_OTHER_PROJECT_ROOT = "some/other/projectRoot";
    private static final String SOME_OTHER_PROJECT_NAME = "other-mock-project";

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
    protected KieProjectService getProjectService() {

        final KieProjectService mock = super.getProjectService();

        when(mock.resolveProject(any(org.uberfire.backend.vfs.Path.class)))
                .thenAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        org.uberfire.backend.vfs.Path resource = (org.uberfire.backend.vfs.Path) invocationOnMock.getArguments()[0];
                        if (resource.toURI().contains(TEST_PROJECT_ROOT)) {
                            return getKieProjectMock(TEST_PROJECT_ROOT,
                                                     TEST_PROJECT_NAME);
                        } else if (resource.toURI().contains(SOME_OTHER_PROJECT_ROOT)) {
                            return getKieProjectMock(SOME_OTHER_PROJECT_ROOT,
                                                     SOME_OTHER_PROJECT_NAME);
                        } else {
                            return null;
                        }
                    }
                });

        return mock;
    }

    @Test
    public void listAllInProject() throws IOException, InterruptedException {

        //Add test files
        addTestFile(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
                    "drl1.drl");
        addTestFile(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
                    "drl2.ext2");
        addTestFile(SOME_OTHER_PROJECT_ROOT,
                    "drl3.ext3");
        addTestFile(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
                    "functions.functions");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new LibraryValueProjectRootPathIndexTerm(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
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

                assertEquals(3,
                             response.getPageRowList().size());
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Test
    public void filterFilesFromProject() throws IOException, InterruptedException {

        //Add test files
        addTestFile(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
                    "rule1.rule");
        addTestFile(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
                    "rule2.rule");
        addTestFile(SOME_OTHER_PROJECT_ROOT,
                    "rule3.rule");
        addTestFile(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
                    "functions.functions");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new LibraryValueProjectRootPathIndexTerm(BaseLibraryIndexingTest.TEST_PROJECT_ROOT,
                                                                                                                               TermSearchType.WILDCARD));
                                                                                  add(new ValueFullFileNameIndexTerm("*rule*",
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
        return this.getClass().getSimpleName();
    }
}
