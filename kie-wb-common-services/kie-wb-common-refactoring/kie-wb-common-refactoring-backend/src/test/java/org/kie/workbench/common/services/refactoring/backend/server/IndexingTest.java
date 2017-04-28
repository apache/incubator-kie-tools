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

package org.kie.workbench.common.services.refactoring.backend.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.guvnor.common.services.project.model.Package;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.ImpactAnalysisAnalyzerWrapperFactory;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.backend.lucene.index.CustomAnalyzerWrapperFactory;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public abstract class IndexingTest<T extends ResourceTypeDefinition> {

    public static final String TEST_PROJECT_ROOT = "/a/mock/project/root";
    public static final String TEST_PROJECT_NAME = "mock-project";
    public static final String TEST_PACKAGE_NAME = "org.kie.workbench.mock.package";
    protected static final Logger logger = LoggerFactory.getLogger(IndexingTest.class);
    protected static final List<File> tempFiles = new ArrayList<File>();
    private static LuceneConfig config;
    private IOService ioService = null;

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            FileUtils.deleteQuietly(tempFile);
        }
        if (config != null) {
            config.dispose();
            config = null;
        }
    }

    protected static LuceneConfig getConfig() {
        return config;
    }

    protected static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile("temp",
                                              Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        tempFiles.add(temp);
        return temp;
    }

    protected abstract TestIndexer<T> getIndexer();

    protected abstract Map<String, Analyzer> getAnalyzers();

    protected CustomAnalyzerWrapperFactory getAnalyzerWrapperFactory() {
        return ImpactAnalysisAnalyzerWrapperFactory.getInstance();
    }

    protected abstract T getResourceTypeDefinition();

    protected void loadProperties(final String fileName,
                                  final Path basePath) throws IOException {
        final Path path = basePath.resolve(fileName);
        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(fileName));
        ioService().write(path,
                          propertiesToString(properties));
    }

    protected String loadText(final String fileName) throws IOException {
        InputStream fileInputStream = this.getClass().getResourceAsStream(fileName);
        if (fileInputStream == null) {
            File file = new File(fileName);
            if (file.exists()) {
                fileInputStream = new FileInputStream(file);
            }
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    protected String propertiesToString(final Properties properties) {
        final StringBuilder sb = new StringBuilder();
        for (String name : properties.stringPropertyNames()) {
            sb.append(name).append("=").append(properties.getProperty(name)).append("\n");
        }
        return sb.toString();
    }

    protected IOService ioService() {
        if (ioService == null) {
            final Map<String, Analyzer> analyzers = getAnalyzers();
            LuceneConfigBuilder configBuilder = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .usingAnalyzers(analyzers)
                    .usingAnalyzerWrapperFactory(getAnalyzerWrapperFactory())
                    .useInMemoryDirectory()
                    // If you want to use Luke to inspect the index,
                    // comment ".useInMemoryDirectory(), and uncomment below..
//                     .useNIODirectory()
                    .useDirectoryBasedIndex();

            if (config == null) {
                config = configBuilder.build();
            }

            ioService = new IOServiceIndexedImpl(config.getIndexEngine());
            final TestIndexer indexer = getIndexer();
            IndexersFactory.clear();
            IndexersFactory.addIndexer(indexer);

            //Mock CDI injection and setup
            indexer.setIOService(ioService);
            indexer.setProjectService(getProjectService());
            indexer.setResourceTypeDefinition(getResourceTypeDefinition());
        }
        return ioService;
    }

    public void dispose() {
        ioService().dispose();
        ioService = null;
    }

    protected KieProjectService getProjectService() {

        final KieProject mockProject = getKieProjectMock(TEST_PROJECT_ROOT,
                                                         TEST_PROJECT_NAME);

        final Package mockPackage = mock(Package.class);
        when(mockPackage.getPackageName()).thenReturn(TEST_PACKAGE_NAME);

        final KieProjectService mockProjectService = mock(KieProjectService.class);
        when(mockProjectService.resolveProject(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockProject);
        when(mockProjectService.resolvePackage(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockPackage);

        return mockProjectService;
    }

    protected KieProject getKieProjectMock(final String testProjectRoot,
                                         final String testProjectName) {
        final org.uberfire.backend.vfs.Path mockRoot = mock(org.uberfire.backend.vfs.Path.class);
        when(mockRoot.toURI()).thenReturn(testProjectRoot);

        final KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(mockRoot);
        when(mockProject.getProjectName()).thenReturn(testProjectName);
        return mockProject;
    }

    protected void assertContains(final List<KObject> results,
                                  final Path path) {
        for (KObject kObject : results) {
            final String key = kObject.getKey();
            final String fileName = path.getFileName().toString();
            if (key.endsWith(fileName)) {
                return;
            }
        }
        fail("Results do not contain expected Path '" + path.toUri().toString());
    }

    public void searchFor(Index index,
                          Query query,
                          int expectedNumHits,
                          Path... paths) throws IOException {
        final IndexSearcher searcher = ((LuceneIndex) index).nrtSearcher();
        searchFor(searcher,
                  query,
                  expectedNumHits,
                  paths);
    }

    public void searchFor(Query query,
                          int expectedNumHits) throws IOException {
        final IndexSearcher searcher = ((LuceneIndexManager) getConfig().getIndexManager()).getIndexSearcher();
        searchFor(searcher,
                  query,
                  expectedNumHits);
    }

    private void searchFor(IndexSearcher searcher,
                           Query query,
                           int expectedNumHits,
                           Path... paths) throws IOException {
        try {
            final TopScoreDocCollector collector = TopScoreDocCollector.create(10 > expectedNumHits ? 10 : expectedNumHits);
            searcher.search(query,
                            collector);
            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals("Number of docs fulfilling the given query criteria",
                         expectedNumHits,
                         hits.length);

            if (paths != null && paths.length > 0) {
                final List<KObject> results = new ArrayList<KObject>();
                for (int i = 0; i < hits.length; i++) {
                    results.add(KObjectUtil.toKObject(searcher.doc(hits[i].doc)));
                }
                for (Path path : paths) {
                    assertContains(results,
                                   path);
                }
            }
        } finally {
            ((LuceneIndexManager) getConfig().getIndexManager()).release(searcher);
        }
    }
}
