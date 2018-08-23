/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.type.TestScenarioResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexTestScenarioTest extends BaseIndexingTest<TestScenarioResourceTypeDefinition> {

    @Test
    public void testIndexTestScenario() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = basePath.resolve("scenario1.scenario");
        final Scenario model1 = TestScenarioFactory.makeTestScenarioWithVerifyFact("org.drools.workbench.screens.testscenario.backend.server.indexing",
                                                                                   new ArrayList<Import>() {{
                                                                                       add(new Import("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Applicant"));
                                                                                       add(new Import("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Mortgage"));
                                                                                   }},
                                                                                   "scenario1");
        final String xml1 = ScenarioXMLPersistence.getInstance().marshal(model1);
        ioService().write(path1,
                          xml1);

        final Path path2 = basePath.resolve("scenario2.scenario");
        final Scenario model2 = TestScenarioFactory.makeTestScenarioWithoutVerifyFact("org.drools.workbench.screens.testscenario.backend.server.indexing",
                                                                                      new ArrayList<Import>() {{
                                                                                          add(new Import("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Applicant"));
                                                                                          add(new Import("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Mortgage"));
                                                                                      }},
                                                                                      "scenario2");
        final String xml2 = ScenarioXMLPersistence.getInstance().marshal(model2);
        ioService().write(path2,
                          xml2);

        final Path path3 = basePath.resolve("scenario3.scenario");
        final Scenario model3 = TestScenarioFactory.makeTestScenarioWithVerifyRuleFired("org.drools.workbench.screens.testscenario.backend.server.indexing",
                                                                                        new ArrayList<Import>() {{
                                                                                            add(new Import("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Applicant"));
                                                                                            add(new Import("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Mortgage"));
                                                                                        }},
                                                                                        "scenario3");
        final String xml3 = ScenarioXMLPersistence.getInstance().marshal(model3);
        ioService().write(path3,
                          xml3);

        final Path path4 = basePath.resolve("scenario4.scenario");
        final Scenario model4 = TestScenarioFactory.makeTestScenarioWithGlobalVerifyGlobal("org.drools.workbench.screens.testscenario.backend.server.indexing",
                                                                                           new ArrayList<Import>() {{
                                                                                               add(new Import("java.util.Date"));
                                                                                           }},
                                                                                           "scenario1");
        final String xml4 = ScenarioXMLPersistence.getInstance().marshal(model4);
        ioService().write(path4,
                          xml4);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath.getFileSystem()).getClusterId());

        //Test Scenarios using org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Applicant
        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Applicant", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 2, path1, path2);
        }

        //Test Scenarios using org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Mortgage
        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Mortgage", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 1, path1);
        }

        //Test Scenarios using org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Mortgage#amount
        {
            final Query query = new SingleTermQueryBuilder(new ValuePartReferenceIndexTerm("org.drools.workbench.screens.testscenario.backend.server.indexing.classes.Mortgage", "amount", PartType.FIELD))
                    .build();
            searchFor(index, query, 1, path1);
        }

        //Test Scenarios using java.lang.Integer
        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("java.lang.Integer", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 3, path1, path2);
        }

        //Test Scenarios expecting rule "test" to fire
        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("test", ResourceType.RULE))
                    .build();
            searchFor(index, query, 1, path3);
        }

        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("java.util.Date", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 1, path4);
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestTestScenarioFileIndexer();
    }

    @Override
    protected TestScenarioResourceTypeDefinition getResourceTypeDefinition() {
        return new TestScenarioResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
