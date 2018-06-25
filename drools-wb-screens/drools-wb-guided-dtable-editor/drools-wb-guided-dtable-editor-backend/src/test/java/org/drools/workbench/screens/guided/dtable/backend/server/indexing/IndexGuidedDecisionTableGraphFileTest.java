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
package org.drools.workbench.screens.guided.dtable.backend.server.indexing;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Query;
import org.drools.workbench.screens.guided.dtable.backend.server.GuidedDTGraphXMLPersistence;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableGraphResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexGuidedDecisionTableGraphFileTest extends BaseIndexingTest<GuidedDTableGraphResourceTypeDefinition> {

    @Test
    public void testIndexGuidedDecisionTableGraphEntries() throws IOException, InterruptedException {
        //Add test files
        final Path path = basePath.resolve("dtable-graph1.gdst-set");
        final Path dtable1Path = basePath.resolve("dtable1.gdst");
        final Path dtable2Path = basePath.resolve("dtable2.gdst");
        final org.uberfire.backend.vfs.Path vfsDtable1Path = Paths.convert(dtable1Path);
        final org.uberfire.backend.vfs.Path vfsDtable2Path = Paths.convert(dtable2Path);

        final GuidedDecisionTableEditorGraphModel model = new GuidedDecisionTableEditorGraphModel();
        model.getEntries().add(new GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry(vfsDtable1Path,
                                                                                                     vfsDtable1Path));
        model.getEntries().add(new GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry(vfsDtable2Path,
                                                                                                     vfsDtable2Path));
        final String xml = GuidedDTGraphXMLPersistence.getInstance().marshal(model);
        ioService().write(path,
                          xml);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath.getFileSystem()).getClusterId());

        {
            final Query query = new SingleTermQueryBuilder(new ValueSharedPartIndexTerm(vfsDtable1Path.toURI(),
                                                                                        PartType.PATH))
                    .build();
            searchFor(index,
                      query,
                      1,
                      path);
        }

        {
            final Query query = new SingleTermQueryBuilder(new ValueSharedPartIndexTerm(vfsDtable2Path.toURI(),
                                                                                        PartType.PATH))
                    .build();
            searchFor(index,
                      query,
                      1,
                      path);
        }
    }

    @Override
    protected TestIndexer<GuidedDTableGraphResourceTypeDefinition> getIndexer() {
        return new TestGuidedDecisionTableGraphFileIndexer();
    }

    @Override
    protected GuidedDTableGraphResourceTypeDefinition getResourceTypeDefinition() {
        return new GuidedDTableGraphResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}
