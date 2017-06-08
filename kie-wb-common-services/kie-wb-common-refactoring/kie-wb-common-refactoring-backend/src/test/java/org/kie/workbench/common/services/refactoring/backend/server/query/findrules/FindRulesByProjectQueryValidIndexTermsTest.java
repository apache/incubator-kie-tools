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

package org.kie.workbench.common.services.refactoring.backend.server.query.findrules;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestPackageNameDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.RuleNameResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRulesByProjectQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FindRulesByProjectQueryValidIndexTermsTest
        extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    private static final String SOME_OTHER_PROJECT_ROOT = "some/other/projectRoot";
    private static final String SOME_OTHER_PROJECT_NAME = "other-mock-project";

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new FindRulesByProjectQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new RuleNameResponseBuilder();
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
    public void testQueryValidIndexTerms() throws IOException, InterruptedException {
        //Add test files
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl1.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl2.drl");
        addTestFile(SOME_OTHER_PROJECT_ROOT,
                    "drl3.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl4.drl");
        addTestFile(BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl5.drl");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest("FindRulesByProjectQuery",
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueProjectRootPathIndexTerm(BaseIndexingTest.TEST_PROJECT_ROOT,
                                                                                                                        TermSearchType.WILDCARD));
                                                                                  add(new ValuePackageNameIndexTerm("",
                                                                                                                    TermSearchType.WILDCARD));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);
                assertEquals(1,
                             response.getPageRowList().size());
                assertResponseContains(response.getPageRowList(),
                                       "noPackage",
                                       "");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                fail("Unable to query: " + e.getMessage());
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest("FindRulesByProjectQuery",
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueProjectRootPathIndexTerm("*",
                                                                                                                        TermSearchType.WILDCARD));
                                                                                  add(new ValuePackageNameIndexTerm("*",
                                                                                                                    TermSearchType.WILDCARD));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);
                assertEquals(5,
                             response.getPageRowList().size());
                assertResponseContains(response.getPageRowList(),
                                       "myRule",
                                       "org.kie.workbench.mock.package");
                assertResponseContains(response.getPageRowList(),
                                       "myRule2",
                                       "org.kie.workbench.mock.package");
                assertResponseContains(response.getPageRowList(),
                                       "myRule3",
                                       "org.kie.workbench.mock.package");
                assertResponseContains(response.getPageRowList(),
                                       "my.Rule4",
                                       "org.kie.workbench.mock.package");
                assertResponseContains(response.getPageRowList(),
                                       "noPackage",
                                       "");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                fail("Unable to query: " + e.getMessage());
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest("FindRulesByProjectQuery",
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueProjectRootPathIndexTerm(BaseIndexingTest.TEST_PROJECT_ROOT));
                                                                                  add(new ValuePackageNameIndexTerm(BaseIndexingTest.TEST_PACKAGE_NAME));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);
                assertEquals(3,
                             response.getPageRowList().size());
                assertResponseContains(response.getPageRowList(),
                                       "myRule",
                                       "org.kie.workbench.mock.package");
                assertResponseContains(response.getPageRowList(),
                                       "myRule2",
                                       "org.kie.workbench.mock.package");
                assertResponseContains(response.getPageRowList(),
                                       "my.Rule4",
                                       "org.kie.workbench.mock.package");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                fail("Unable to query: " + e.getMessage());
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest("FindRulesByProjectQuery",
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueProjectRootPathIndexTerm(SOME_OTHER_PROJECT_ROOT));
                                                                                  add(new ValuePackageNameIndexTerm(BaseIndexingTest.TEST_PACKAGE_NAME));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);
                assertEquals(1,
                             response.getPageRowList().size());
                assertResponseContains(response.getPageRowList(),
                                       "myRule3",
                                       "org.kie.workbench.mock.package");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                fail("Unable to query: " + e.getMessage());
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest("FindRulesByProjectQuery",
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueProjectRootPathIndexTerm(BaseIndexingTest.TEST_PROJECT_ROOT));
                                                                                  add(new ValuePackageNameIndexTerm("non-existent-package-name"));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);
                assertEquals(0,
                             response.getPageRowList().size());
            } catch (IllegalArgumentException e) {
                fail("Unable to query: " + e.getMessage());
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest("FindRulesByProjectQuery",
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new ValueProjectRootPathIndexTerm("non-existent-project-root"));
                                                                                  add(new ValuePackageNameIndexTerm(BaseIndexingTest.TEST_PACKAGE_NAME));
                                                                              }},
                                                                              0,
                                                                              10);

            try {
                final PageResponse<RefactoringPageRow> response = service.query(request);
                assertNotNull(response);
                assertEquals(0,
                             response.getPageRowList().size());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                fail("Unable to query: " + e.getMessage());
            }
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestPackageNameDrlFileIndexer();
    }

    @Override
    protected TestDrlFileTypeDefinition getResourceTypeDefinition() {
        return new TestDrlFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
