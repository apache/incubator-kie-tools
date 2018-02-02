/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.service.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.bpmn.backend.query.FindBpmnProcessIdsQuery;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BpmnFileIndexerTest extends BaseIndexingTest<BPMNDefinitionSetResourceType> {

    private final static List<String> PROCESS_IDS = Arrays.asList(new String[]{"hiring", "ParentProcess", "SubProcess", "multiple-rule-tasks", "org.jbpm.signal", "org.jbpm.broken"});

    private final static String[] BPMN_FILES = {
            "callActivity.bpmn",
            "callActivityByName.bpmn",
            "callActivityCalledSubProcess.bpmn",
            "hiring.bpmn",
            "multipleRuleTasksWithDataInput.bpmn",
            "signal.bpmn",
            "brokenSignal.bpmn",
    };

    private static final String DEPLOYMENT_ID = "org.kjar:test:1.0";

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new FindResourcesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
            add(new FindAllChangeImpactQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
            add(new FindBpmnProcessIdsQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new FindBpmnProcessIdsQuery.BpmnProcessIdsResponseBuilder(ioService());
                }
            });
        }};
    }

    private static final long WAIT_TIME_MILLIS = 2000;
    private static final int MAX_WAIT_TIMES = 8;

    @Test
    public void testBpmnIndexing() throws Exception {

        List<Path> pathList = new ArrayList<>();
        ioService().startBatch(ioService().getFileSystem(basePath.toUri()));
        for (int i = 0; i < BPMN_FILES.length; ++i) {
            String bpmnFile = BPMN_FILES[i];
            if (bpmnFile.endsWith("bpmn")) {
                Path path = basePath.resolve(bpmnFile);
                pathList.add(path);
                String bpmnStr = loadText(bpmnFile);
                ioService().write(path,
                                  bpmnStr);
            }
        }
        ioService().endBatch();
        Path[] paths = pathList.toArray(new Path[pathList.size()]);

        {
            PageResponse<RefactoringPageRow> response = null;
            try {
                for (int i = 0; i < MAX_WAIT_TIMES; i++) {
                    Thread.sleep(WAIT_TIME_MILLIS);
                    response = queryBPMN2Resources();
                    if (response != null && response.getPageRowList() != null && response.getPageRowList().size() >= paths.length) {
                        break;
                    }
                }
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }

            assertNotNull(response);
            assertEquals(paths.length,
                         response.getPageRowList().size());
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("*",
                                          PartType.RULEFLOW_GROUP,
                                          ValueIndexTerm.TermSearchType.WILDCARD)
                    .inAllModules().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(1,
                             response.size());
                assertResponseContains(response,
                                       paths[4]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("MySignal",
                                          PartType.SIGNAL)
                    .inAllModules().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(1,
                             response.size());
                assertResponseContains(response,
                                       paths[5]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("BrokenSignal",
                                          PartType.SIGNAL)
                    .inAllModules().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(1,
                             response.size());
                assertResponseContains(response,
                                       paths[6]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("name",
                                          PartType.GLOBAL)
                    .inAllModules().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(2,
                             response.size());
                assertResponseContains(response,
                                       paths[5]);
                assertResponseContains(response,
                                       paths[6]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
        {

            final Set<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>() {{
                add(new ValueResourceIndexTerm("*",
                                               ResourceType.BPMN2,
                                               ValueIndexTerm.TermSearchType.WILDCARD));
            }};
            try {
                List<RefactoringPageRow> response = service.query(
                        FindBpmnProcessIdsQuery.NAME,
                        queryTerms);
                assertNotNull(response);
                assertEquals(paths.length,
                             response.size());

                for (String expectedId : PROCESS_IDS) {
                    boolean foundId = false;
                    for (RefactoringPageRow row : response) {
                        Map<String, org.uberfire.backend.vfs.Path> mapRow = (Map<String, org.uberfire.backend.vfs.Path>) row.getValue();
                        for (String rKey : mapRow.keySet()) {
                            assertTrue(PROCESS_IDS.contains(rKey));
                            foundId = true;
                        }
                    }
                    if (!foundId) {
                        fail("Process with ID <" + expectedId + " not found in results for " + FindBpmnProcessIdsQuery.NAME);
                    }
                }
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    private PageResponse<RefactoringPageRow> queryBPMN2Resources() throws IllegalArgumentException {
        final RefactoringPageRequest request = new RefactoringPageRequest(FindResourcesQuery.NAME,
                                                                          new HashSet<ValueIndexTerm>() {{
                                                                              add(new ValueResourceIndexTerm("*",
                                                                                                             ResourceType.BPMN2,
                                                                                                             ValueIndexTerm.TermSearchType.WILDCARD));
                                                                          }},
                                                                          0,
                                                                          10);

        return service.query(request);
    }

    @Override
    protected KieModuleService getModuleService() {
        final org.uberfire.backend.vfs.Path mockRoot = mock(org.uberfire.backend.vfs.Path.class);
        when(mockRoot.toURI()).thenReturn(TEST_MODULE_ROOT);

        final KieModule mockModule = mock(KieModule.class);
        when(mockModule.getRootPath()).thenReturn(mockRoot);
        when(mockModule.getModuleName()).thenReturn(TEST_MODULE_NAME);

        POM mockPom = mock(POM.class);
        when(mockModule.getPom()).thenReturn(mockPom);
        GAV mockGAV = mock(GAV.class);
        when(mockPom.getGav()).thenReturn(mockGAV);
        when(mockGAV.toString()).thenReturn(DEPLOYMENT_ID);

        final Package mockPackage = mock(Package.class);
        when(mockPackage.getPackageName()).thenReturn(TEST_PACKAGE_NAME);

        final KieModuleService mockModuleService = mock(KieModuleService.class);
        when(mockModuleService.resolveModule(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockModule);
        when(mockModuleService.resolvePackage(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockPackage);

        return mockModuleService;
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestBpmnFileIndexer();
    }

    @Override
    protected BPMNDefinitionSetResourceType getResourceTypeDefinition() {
        return new BPMNDefinitionSetResourceType();
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }
}
