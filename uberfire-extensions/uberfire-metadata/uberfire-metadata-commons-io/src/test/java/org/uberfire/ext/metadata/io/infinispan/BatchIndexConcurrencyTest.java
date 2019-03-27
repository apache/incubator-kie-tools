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

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.MetadataConfigBuilder;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class BatchIndexConcurrencyTest extends BaseIndexTest {

    private MetaIndexEngine metaIndexEngine;

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{"indexable-" + this.getClass().getSimpleName()};
    }

    @Override
    protected IOService ioService() {
        if (ioService == null) {
            config = new MetadataConfigBuilder("infinispan")
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();

            metaIndexEngine = spy(config.getIndexEngine());

            ioService = new IOServiceIndexedImpl(metaIndexEngine,
                                                 Executors.newCachedThreadPool(new DescriptiveThreadFactory()),
                                                 indexersFactory(),
                                                 indexerDispatcherFactory(config.getIndexEngine()),
                                                 DublinCoreView.class,
                                                 VersionAttributeView.class) {
                @Override
                protected void setupWatchService(final FileSystem fs) {
                    //No WatchService for this test
                }
            };
        }
        return ioService;
    }

    @Test
    //See https://bugzilla.redhat.com/show_bug.cgi?id=1288132
    public void testSingleConcurrentBatchIndexExecution() throws IOException, InterruptedException {
        //Write a file to ensure the FileSystem has a Root Directory
        final Path path1 = getBasePath("indexable-" + this.getClass().getSimpleName()).resolve("xxx");
        ioService().write(path1,
                          "xxx!");

        setupCountDown(1);

        final URI fsURI = URI.create("git://indexable-" + this.getClass().getSimpleName() + "/file1");

        //Make multiple requests for the FileSystem. We should only have one batch index operation
        final CountDownLatch startSignal = new CountDownLatch(1);
        for (int i = 0; i < 3; i++) {
            Runnable r = () -> {
                try {
                    startSignal.await();
                    ioService().getFileSystem(fsURI);
                } catch (InterruptedException e) {
                    fail(e.getMessage());
                }
            };
            new Thread(r).start();
        }
        startSignal.countDown();

        waitForCountDown(1000);

        assertEquals(1,
                     getStartBatchCount());
        verify(metaIndexEngine,
               times(3)).freshIndex(any(KCluster.class));
    }
}