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
import org.kie.workbench.common.screens.library.api.index.LibraryValueFileNameIndexTerm;
import org.kie.workbench.common.screens.library.api.index.LibraryValueModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.impl.KObjectImpl;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FindAllLibraryAssetsQueryTest
        extends BaseLibraryIndexingTest {

    private static final String TEST_MODULE_ROOT = "/find/all/library/assets/query/test/a/mock/module/root";
    private static final String TEST_MODULE_NAME = "mock-module";

    private static final String SOME_OTHER_MODULE_ROOT = "/find/all/library/assets/query/test/some/other/moduleRoot";
    private static final String SOME_OTHER_MODULE_NAME = "other-mock-module";

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

    @After
    public void dispose() {
        super.dispose();
        super.cleanup();
    }

    @Override
    protected KieModuleService getModuleService() {

        final KieModuleService moduleService = super.getModuleService();

        when(moduleService.resolveModule(any(org.uberfire.backend.vfs.Path.class)))
                .thenAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        org.uberfire.backend.vfs.Path resource = (org.uberfire.backend.vfs.Path) invocationOnMock.getArguments()[0];
                        if (resource.toURI().contains(TEST_MODULE_ROOT)) {
                            return getKieModuleMock(TEST_MODULE_ROOT,
                                                    TEST_MODULE_NAME);
                        } else if (resource.toURI().contains(SOME_OTHER_MODULE_ROOT)) {
                            return getKieModuleMock(SOME_OTHER_MODULE_ROOT,
                                                    SOME_OTHER_MODULE_NAME);
                        } else {
                            return null;
                        }
                    }
                });

        return moduleService;
    }

    @Test
    public void listAllInModule() throws IOException, InterruptedException {

        //Add test files
        addTestFile(TEST_MODULE_ROOT,
                    "drl1.drl");
        addTestFile(TEST_MODULE_ROOT,
                    "drl2.ext2");
        addTestFile(SOME_OTHER_MODULE_ROOT,
                    "drl3.ext3");
        addTestFile(TEST_MODULE_ROOT,
                    "functions.functions");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new LibraryValueModuleRootPathIndexTerm(TEST_MODULE_ROOT,
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
        addTestFile(SOME_OTHER_MODULE_ROOT,
                    "rule3.rule");
        addTestFile(TEST_MODULE_ROOT,
                    "functions.functions");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                              new HashSet<ValueIndexTerm>() {{
                                                                                  add(new LibraryValueModuleRootPathIndexTerm(TEST_MODULE_ROOT,
                                                                                                                              TermSearchType.WILDCARD));
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
