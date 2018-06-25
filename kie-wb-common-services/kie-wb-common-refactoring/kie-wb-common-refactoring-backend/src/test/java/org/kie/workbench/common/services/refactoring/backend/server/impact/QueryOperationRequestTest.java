/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.impact;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.refactoring.service.impact.DeleteOperationRequest;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.kie.workbench.common.services.refactoring.service.impact.RefactorOperationRequest;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

public class QueryOperationRequestTest extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    // Setup fields, methods and other logic --------------------------------------------------------------------------------------

    @Override
    protected TestIndexer<TestDrlFileTypeDefinition> getIndexer() {
        return new TestDrlFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {

            {
                put(ModuleRootPathIndexTerm.TERM,
                    new FilenameAnalyzer());
            }
        };
    }

    @Override
    protected TestDrlFileTypeDefinition getResourceTypeDefinition() {
        return new TestDrlFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {
            {
                add(new FindAllChangeImpactQuery() {
                    @Override
                    public ResponseBuilder getResponseBuilder() {
                        return new DefaultResponseBuilder(ioService());
                    }
                });
            }
        };
    }

    // Tests ----------------------------------------------------------------------------------------------------------------------

    @Test
    public void workingCodeTest() throws Exception {

        //Add test files
        final Path path1 = basePath.resolve("drl1.drl");
        final String drl1 = loadText("drl1.drl");
        ioService().write(path1,
                          drl1);
        final Path path2 = basePath.resolve("drl2.drl");
        final String drl2 = loadText("drl2.drl");
        ioService().write(path2,
                          drl2);
        final Path path3 = basePath.resolve("drl3.drl");
        final String drl3 = loadText("drl3.drl");
        ioService().write(path3,
                          drl3);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            QueryOperationRequest request = QueryOperationRequest.referencesSharedPart("myRuleFlowGroup",
                                                                                       PartType.RULEFLOW_GROUP).inAllModules().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(2,
                             response.size());
                assertResponseContains(response,
                                       path1);
                assertResponseContains(response,
                                       path3);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest.referencesSharedPart("*",
                                                                                       PartType.RULEFLOW_GROUP,
                                                                                       TermSearchType.WILDCARD).inAllModules().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(3,
                             response.size());
                assertResponseContains(response,
                                       path1);
                assertResponseContains(response,
                                       path2);
                assertResponseContains(response,
                                       path3);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }

    @Test
    @SuppressWarnings("unused")
    public void exampleCode() {
        String className = this.getClass().getName();
        String projectName = "org.my.package:my-project:1.0";
        String projectRootPathURI = "default://repo/my-project";
        String newClassName = Random.class.getName();

        DeleteOperationRequest delReq = DeleteOperationRequest.deleteReferences(className,
                                                                                ResourceType.JAVA).inModule(projectName).onAllBranches();

        delReq = DeleteOperationRequest.deleteReferences(className,
                                                         ResourceType.JAVA).inModuleRootPathURI(projectRootPathURI).onAllBranches();

        delReq = DeleteOperationRequest.deletePartReferences(className,
                                                             "setAge(long)",
                                                             PartType.METHOD).inModule(projectName).onAllBranches();

        delReq = DeleteOperationRequest.deletePartReferences(className,
                                                             "setAge(long)",
                                                             PartType.METHOD).inModuleRootPathURI(projectRootPathURI).onAllBranches();

        RefactorOperationRequest refOp = RefactorOperationRequest.refactorReferences(className,
                                                                                     ResourceType.JAVA,
                                                                                     newClassName).inModule(projectName).onBranch("branch-name");

        refOp = RefactorOperationRequest.refactorReferences(className,
                                                            ResourceType.JAVA,
                                                            newClassName).inModuleRootPathURI(projectRootPathURI).onBranch("branch-name");

        refOp = RefactorOperationRequest.refactorPartReferences(className,
                                                                "toName(int)",
                                                                PartType.METHOD,
                                                                "toSurName(int)").inModule(projectName).onAllBranches();

        refOp = RefactorOperationRequest.refactorPartReferences(className,
                                                                "toName(int)",
                                                                PartType.METHOD,
                                                                "toSurName(int)").inModuleRootPathURI(projectRootPathURI).onAllBranches();

        refOp = RefactorOperationRequest.refactorPartReferences(className,
                                                                "toName(int)",
                                                                PartType.METHOD,
                                                                "toSurName(int)").inModule(projectName).onBranch("branch-name");

        QueryOperationRequest queOp = QueryOperationRequest.references(className,
                                                                       ResourceType.JAVA).inModule(projectName).onBranch("branch-name");

        queOp = QueryOperationRequest.references(className,
                                                 ResourceType.JAVA).inModuleRootPathURI(projectRootPathURI).onBranch("branch-name");
    }
}
