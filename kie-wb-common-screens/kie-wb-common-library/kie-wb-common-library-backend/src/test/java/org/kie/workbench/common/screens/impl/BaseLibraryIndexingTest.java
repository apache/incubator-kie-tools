/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.guvnor.common.services.project.model.Package;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.kie.workbench.common.screens.library.api.index.LibraryFileNameIndexTerm;
import org.kie.workbench.common.screens.library.api.index.LibraryRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.ImpactAnalysisAnalyzerWrapperFactory;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.LowerCaseOnlyAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQueries;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.ext.metadata.MetadataConfig;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.engine.IndexerScheduler.Factory;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.io.ConstrainedIndexerScheduler.ConstraintBuilder;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.IndexerDispatcher;
import org.uberfire.ext.metadata.io.IndexerDispatcher.IndexerDispatcherFactory;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.ext.metadata.io.MetadataConfigBuilder;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class BaseLibraryIndexingTest {

    private static final String TEST_PACKAGE_NAME = "org.kie.workbench.mock.package";
    private static final Logger logger = LoggerFactory.getLogger(BaseLibraryIndexingTest.class);

    private static final List<File> tempFiles = new ArrayList<>();

    private static MetadataConfig config;
    protected int seed = new Random(10L).nextInt();
    protected boolean created = false;
    protected Path basePath;
    protected RefactoringQueryServiceImpl service;
    protected IOService ioService = null;

    @Rule
    public TestName testName = new TestName();
    private IndexerDispatcherFactory indexerDispatcherFactory;
    private IndexersFactory indexersFactory;

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        deleteTempFiles();
        if (config != null) {
            config.dispose();
            config = null;
        }
    }

    protected static void deleteTempFiles() {
        for (final File tempFile : tempFiles) {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    private static File createTempDirectory() throws IOException {
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

    protected String getRepositoryRootPath() {
        Path root = basePath;
        while (root.getParent() != null) {
            root = root.getParent();
        }

        return Paths.convert(root).toURI();
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws IOException {
        if (!created) {
            final String repositoryName = getRepositoryName();
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty("org.uberfire.nio.git.dir",
                               path);
            System.setProperty("org.uberfire.nio.git.daemon.enabled",
                               "false");
            System.setProperty("org.uberfire.nio.git.ssh.enabled",
                               "false");
            System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                               "true");
            System.out.println(".niogit: " + path);

            final URI newRepo = URI.create("git://" + repositoryName);

            try {
                IOService ioService = ioService();
                ioService.newFileSystem(newRepo,
                                        new HashMap<>());

                // Don't ask, but we need to write a single file first in order for indexing to work
                basePath = getDirectoryPath().resolveSibling("someNewOtherPath");
                Path dummyFile = basePath.resolve("dummy");
                // Create directory instead of file so we don't add anything indexable.
                ioService.createDirectory(dummyFile);
            } catch (final Exception e) {
                e.printStackTrace();
                logger.warn("Failed to initialize IOService instance: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                            e);
            } finally {
                created = true;
            }

            final Instance<NamedQuery> namedQueriesProducer = mock(Instance.class);
            when(namedQueriesProducer.iterator()).thenReturn(getQueries().iterator());

            service = new RefactoringQueryServiceImpl(config,
                                                      new NamedQueries(namedQueriesProducer));
            service.init();
        }
    }

    @After
    public void dispose() {
        ioService().dispose();
        ioService = null;
        created = false;
    }

    protected Set<NamedQuery> getQueries() {
        // override me if using the RefactoringQueryServiceImpl!
        return Collections.emptySet();
    }

    protected abstract String getRepositoryName();

    private Path getDirectoryPath() {
        final String repositoryName = getRepositoryName();
        final Path dir = ioService().get(URI.create("git://" + repositoryName + "/_someDir" + seed));
        ioService().deleteIfExists(dir);
        return dir;
    }

    protected void addTestFile(final String moduleName,
                               final String pathToFile) throws IOException {
        final Path path = basePath.resolve(moduleName + "/" + pathToFile);
        final String text = loadText(pathToFile);
        ioService().write(path,
                          text);
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

    @SuppressWarnings("unchecked")
    protected IOService ioService() {
        if (ioService == null) {
            final Map<String, Analyzer> analyzers = getAnalyzers();
            MetadataConfigBuilder configBuilder = new MetadataConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .usingAnalyzers(analyzers)
                    .usingAnalyzerWrapperFactory(ImpactAnalysisAnalyzerWrapperFactory.getInstance())
                    .useInMemoryDirectory()
                    // If you want to use Luke to inspect the index,
                    // comment ".useInMemoryDirectory(), and uncomment below..
//                     .useNIODirectory()
                    .useDirectoryBasedIndex();

            if (config == null) {
                config = configBuilder.build();
            }

            ExecutorService executorService = Executors.newCachedThreadPool(new DescriptiveThreadFactory());

            ioService = new IOServiceIndexedImpl(config.getIndexEngine(),
                                                 executorService,
                                                 indexersFactory(),
                                                 indexerDispatcherFactory(config.getIndexEngine()));

            final LibraryIndexer indexer = spy(new LibraryIndexer());
            when(indexer.getVisibleResourceTypes()).thenReturn(new HashSet<>(Arrays.asList(new AnyResourceTypeDefinition())));
            indexersFactory().clear();
            indexersFactory().addIndexer(indexer);

            //Mock CDI injection and setup
            indexer.setIOService(ioService);
            indexer.setModuleService(getModuleService());
        }
        return ioService;
    }

    protected IndexerDispatcherFactory indexerDispatcherFactory(MetaIndexEngine indexEngine) {
        if (indexerDispatcherFactory == null) {
            Factory schedulerFactory = new ConstraintBuilder().createFactory();
            indexerDispatcherFactory = IndexerDispatcher.createFactory(indexEngine, schedulerFactory, testEvent(), LoggerFactory.getLogger(IndexerDispatcher.class));
        }

        return indexerDispatcherFactory;
    }

    protected <T> Event<T> testEvent() {
        return new Event<T>() {

            @Override
            public void fire(T event) {
            }

            @Override
            public Event<T> select(Annotation... qualifiers) {
                return this;
            }

            @Override
            public <U extends T> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
                return testEvent();
            }

            @Override
            public <U extends T> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
                return testEvent();
            }
        };
    }

    protected IndexersFactory indexersFactory() {
        if (indexersFactory == null) {
            indexersFactory = new IndexersFactory();
        }

        return indexersFactory;
    }

    private Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put(LibraryFileNameIndexTerm.TERM,
                new FilenameAnalyzer());
            put(LibraryRepositoryRootIndexTerm.TERM,
                new FilenameAnalyzer());
            put(ModuleRootPathIndexTerm.TERM,
                new FilenameAnalyzer());
            put(PackageNameIndexTerm.TERM,
                new LowerCaseOnlyAnalyzer());
            put(LuceneIndex.CUSTOM_FIELD_FILENAME,
                new FilenameAnalyzer());
        }};
    }

    protected KieModuleService getModuleService() {
        final Package mockPackage = mock(Package.class);
        when(mockPackage.getPackageName()).thenReturn(TEST_PACKAGE_NAME);

        final KieModuleService mockModuleService = mock(KieModuleService.class);
        when(mockModuleService.resolvePackage(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockPackage);

        return mockModuleService;
    }

    protected KieModule getKieModuleMock(final String testModuleRoot,
                                         final String testModuleName) {
        final org.uberfire.backend.vfs.Path mockRoot = mock(org.uberfire.backend.vfs.Path.class);
        when(mockRoot.toURI()).thenReturn(testModuleRoot);

        final KieModule mockModule = mock(KieModule.class);
        when(mockModule.getRootPath()).thenReturn(mockRoot);
        when(mockModule.getModuleName()).thenReturn(testModuleName);
        return mockModule;
    }
}
