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

package org.drools.workbench.screens.guided.scorecard.backend.server.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardXMLPersistence;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.type.GuidedScoreCardResourceTypeDefinition;
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

public class IndexGuidedScoreCardTest extends BaseIndexingTest<GuidedScoreCardResourceTypeDefinition> {

    @Test
    public void testIndexGuidedScoreCard() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = basePath.resolve("scorecard1.scgd");
        final ScoreCardModel model1 = GuidedScoreCardFactory.makeScoreCardWithCharacteristics("org.drools.workbench.screens.guided.scorecard.backend.server.indexing",
                                                                                              new ArrayList<Import>() {{
                                                                                                  add(new Import("org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Applicant"));
                                                                                                  add(new Import("org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Mortgage"));
                                                                                              }},
                                                                                              "scorecard1");
        final String xml1 = GuidedScoreCardXMLPersistence.getInstance().marshal(model1);
        ioService().write(path1,
                          xml1);
        final Path path2 = basePath.resolve("scorecard2.scgd");
        final ScoreCardModel model2 = GuidedScoreCardFactory.makeScoreCardWithoutCharacteristics("org.drools.workbench.screens.guided.scorecard.backend.server.indexing",
                                                                                                 new ArrayList<Import>() {{
                                                                                                     add(new Import("org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Applicant"));
                                                                                                     add(new Import("org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Mortgage"));
                                                                                                 }},
                                                                                                 "scorecard2");
        final String xml2 = GuidedScoreCardXMLPersistence.getInstance().marshal(model2);
        ioService().write(path2,
                          xml2);
        final Path path3 = basePath.resolve("scorecard3.scgd");
        final ScoreCardModel model3 = GuidedScoreCardFactory.makeEmptyScoreCard("org.drools.workbench.screens.guided.scorecard.backend.server.indexing",
                                                                                "scorecard3");
        final String xml3 = GuidedScoreCardXMLPersistence.getInstance().marshal(model3);
        ioService().write(path3,
                          xml3);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath).getClusterId());

        //Score Cards using org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Applicant
        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Applicant", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 2, path1, path2);
        }

        //Score Cards referring to org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Mortgage
        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Mortgage", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 2, path1);
        }

        //Score Cards using org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Mortgage#amount
        {
            final Query query = new SingleTermQueryBuilder(new ValuePartReferenceIndexTerm("org.drools.workbench.screens.guided.scorecard.backend.server.indexing.classes.Mortgage", "amount", PartType.FIELD))
                    .build();
            searchFor(index, query, 1, path1);
        }

        //Score Cards using java.lang.Integer
        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("java.lang.Integer", ResourceType.JAVA))
                    .build();
            searchFor(index, query, 2, path1, path2);
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestGuidedScoreCardFileIndexer();
    }

    @Override
    protected GuidedScoreCardResourceTypeDefinition getResourceTypeDefinition() {
        return new GuidedScoreCardResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
