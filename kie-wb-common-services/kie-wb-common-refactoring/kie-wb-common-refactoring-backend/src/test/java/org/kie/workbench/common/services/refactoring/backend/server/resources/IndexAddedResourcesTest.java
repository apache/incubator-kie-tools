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

package org.kie.workbench.common.services.refactoring.backend.server.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.TestPropertiesFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.TestPropertiesFileTypeDefinition;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.ext.metadata.io.KObjectUtil;

import static org.mockito.Mockito.*;

public class IndexAddedResourcesTest extends BaseIndexingTest<TestPropertiesFileTypeDefinition> {

    @Test
    public void testIndexingAddedResources() throws IOException, InterruptedException {
        //Add test files
        loadProperties("file1.properties",
                       basePath);
        loadProperties("file2.properties",
                       basePath);
        loadProperties("file3.properties",
                       basePath);
        loadProperties("file4.properties",
                       basePath);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> indices = Arrays.asList(KObjectUtil.toKCluster(basePath).getClusterId());

        searchFor(indices,
                  (Query) new TermQuery(new Term("title",
                                                 "lucene")),
                  2);
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestPropertiesFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return Collections.EMPTY_MAP;
    }

    @Override
    protected TestPropertiesFileTypeDefinition getResourceTypeDefinition() {
        return new TestPropertiesFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }

    @Override
    protected KieModuleService getModuleService() {
        return mock(KieModuleService.class);
    }
}
