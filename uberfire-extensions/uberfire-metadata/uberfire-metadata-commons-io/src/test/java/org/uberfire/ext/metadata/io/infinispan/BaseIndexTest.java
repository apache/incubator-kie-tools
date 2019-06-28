/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.ext.metadata.io.infinispan;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.util.TypeLiteral;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.ext.metadata.MetadataConfig;
import org.uberfire.ext.metadata.engine.IndexerScheduler.Factory;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.io.ConstrainedIndexerScheduler.ConstraintBuilder;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.IndexerDispatcher;
import org.uberfire.ext.metadata.io.IndexerDispatcher.IndexerDispatcherFactory;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.ext.metadata.io.MetadataConfigBuilder;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

public abstract class BaseIndexTest {

    protected static final Map<String, Path> basePaths = new HashMap<>();
    protected static final List<File> tempFiles = new ArrayList<>();
    protected boolean created = false;
    protected MetadataConfig config;
    protected IOService ioService = null;
    private int seed = new Random(10L).nextInt();
    private IndexersFactory indexersFactory;
    private IndexerDispatcherFactory indexerDispatcherFactory;

    @BeforeClass
    public static void beforeClass() throws Throwable {
        cleanup();
    }

    @AfterClass
    public static void afterClass() {
        cleanup();
    }

    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            FileUtils.deleteQuietly(tempFile);
        }
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

    protected IOService ioService() {

        if (ioService == null) {
            config = new MetadataConfigBuilder("infinispan")
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();

            ioService = new IOServiceIndexedImpl(config.getIndexEngine(),
                                                 Executors.newCachedThreadPool(new DescriptiveThreadFactory()),
                                                 indexersFactory(),
                                                 indexerDispatcherFactory(config.getIndexEngine()),
                                                 DublinCoreView.class,
                                                 VersionAttributeView.class);
        }
        return ioService;
    }

    protected IndexersFactory indexersFactory() {
        if (indexersFactory == null) {
            indexersFactory = new IndexersFactory();
        }

        return indexersFactory;
    }

    protected IndexerDispatcherFactory indexerDispatcherFactory(MetaIndexEngine indexEngine) {
        if (indexerDispatcherFactory == null) {
            Factory schedulerFactory = new ConstraintBuilder().createFactory();
            indexerDispatcherFactory = IndexerDispatcher.createFactory(indexEngine,
                                                                       schedulerFactory,
                                                                       testEvent(),
                                                                       LoggerFactory.getLogger(IndexerDispatcher.class));
        }

        return indexerDispatcherFactory;
    }

    private <T> Event<T> testEvent() {
        return new Event<T>() {

            @Override
            public void fire(T event) {
            }

            @Override
            public <U extends T> CompletionStage<U> fireAsync(U u) {
                return null;
            }

            @Override
            public <U extends T> CompletionStage<U> fireAsync(U u,
                                                              NotificationOptions notificationOptions) {
                return null;
            }

            @Override
            public Event<T> select(Annotation... qualifiers) {
                return this;
            }

            @Override
            public <U extends T> Event<U> select(Class<U> subtype,
                                                 Annotation... qualifiers) {
                return (Event<U>) this;
            }

            @Override
            public <U extends T> Event<U> select(TypeLiteral<U> subtype,
                                                 Annotation... qualifiers) {
                return (Event<U>) this;
            }
        };
    }

    protected String getSimpleName() {
        return "indexable" + this.getClass().getSimpleName();
    }

    @Before
    public void setup() throws IOException {
        indexersFactory().clear();
        if (!created) {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty("org.uberfire.nio.git.dir",
                               path);
            System.out.println(".niogit: " + path);

            for (String repositoryName : getRepositoryNames()) {
                final URI newRepo = URI.create("git://" + repositoryName);

                try {
                    ioService().newFileSystem(newRepo,
                                              new HashMap<>());

                    final Path basePath = getDirectoryPath(repositoryName).resolveSibling("root");
                    basePaths.put(repositoryName,
                                  basePath);
                } catch (final FileSystemAlreadyExistsException ex) {
                    // ignored
                } finally {
                    created = true;
                }
            }
        }
    }

    @After
    public void tearDown() {
        this.ioService.dispose();
    }

    protected abstract String[] getRepositoryNames();

    protected Path getBasePath(final String repositoryName) {
        return basePaths.get(repositoryName);
    }

    private Path getDirectoryPath(final String repositoryName) {
        final Path dir = ioService().get(URI.create("git://" + repositoryName + "/_someDir" + seed));
        ioService().deleteIfExists(dir);
        return dir;
    }

    public void setupCountDown(final int i) {
        // do nothing -- Byteman will inject code here
    }

    public void waitForCountDown(final int timout) {
        // do nothing -- Byteman will inject code here
    }

    public int getStartBatchCount() {
        // do nothing -- Byteman will inject code here
        return 0;
    }
}
