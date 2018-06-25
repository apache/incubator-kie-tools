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

package org.drools.workbench.screens.dtablexls.backend.server.indexing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexDecisionTableXLSAttributeNameAndValueCompositionTest extends BaseIndexingTest<DecisionTableXLSResourceTypeDefinition> {

    @Test
    public void testIndexDecisionTableXLSAttributeNameAndValueComposition() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = loadXLSFile(basePath,
                                       "dtable3.xls");

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath.getFileSystem()).getClusterId());

        //Decision Table defining a RuleFlow-Group named myRuleFlowGroup. This should match dtable3.xls
        //This checks whether there is a Rule Attribute "ruleflow-group" and its Value is "myRuleflowGroup"
        {
            final Query query = new SingleTermQueryBuilder(new ValueSharedPartIndexTerm("myRuleFlowGroup", PartType.RULEFLOW_GROUP))
                    .build();
            searchFor(index, query, 1, path1);
        }

        //Decision Table defining a RuleFlow-Group named myAgendaGroup. This should *NOT* match dtable3.xls
        {
            final Query query = new SingleTermQueryBuilder(new ValueSharedPartIndexTerm("myAgendaGroup", PartType.RULEFLOW_GROUP))
                    .build();
            searchFor(index, query, 0);
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestDecisionTableXLSFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put(ModuleRootPathIndexTerm.TERM,
                new FilenameAnalyzer());
        }};
    }

    @Override
    protected DecisionTableXLSResourceTypeDefinition getResourceTypeDefinition() {
        return new DecisionTableXLSResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

    private Path loadXLSFile(final Path basePath,
                             final String fileName) throws IOException {
        final Path path = basePath.resolve(fileName);
        final InputStream is = this.getClass().getResourceAsStream(fileName);
        final OutputStream os = ioService().newOutputStream(path);
        IOUtils.copy(is,
                     os);
        os.flush();
        os.close();
        return path;
    }
}
