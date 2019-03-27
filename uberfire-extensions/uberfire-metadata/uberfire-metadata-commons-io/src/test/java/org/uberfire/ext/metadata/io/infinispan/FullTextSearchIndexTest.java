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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.ext.metadata.io.MetadataConfigBuilder;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;
import static org.uberfire.ext.metadata.engine.MetaIndexEngine.FULL_TEXT_FIELD;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class FullTextSearchIndexTest extends BaseIndexTest {

    @Override
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

            indexersFactory().addIndexer(new MockIndexer());
        }
        return ioService;
    }

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{"indexable_" + this.getClass().getSimpleName()};
    }

    @Test
    public void testFullTextIndexedFile() throws IOException, InterruptedException {
        setupCountDown(2);
        final Path path1 = getBasePath("indexable_" + this.getClass().getSimpleName()).resolve("mydrlfile1.drl");
        ioService().write(path1,
                          "Some cheese");

        waitForCountDown(10000);

        List<String> indices = Arrays.asList(toProtobufFormat(toKCluster(path1).getClusterId()));
        IndexProvider provider = this.config.getIndexProvider();

        {
            WildcardQuery query = new WildcardQuery(new Term(FULL_TEXT_FIELD,
                                                             "*file*"));

            long hits = provider.findHitsByQuery(indices,
                                                 query);

            assertEquals(1,
                         hits);
        }

        {

            WildcardQuery query = new WildcardQuery(new Term(FULL_TEXT_FIELD,
                                                             "*mydrlfile1*"));

            long hits = provider.findHitsByQuery(indices,
                                                 query);

            assertEquals(1,
                         hits);
        }

        setupCountDown(2);

        final Path path2 = getBasePath("indexable_" + this.getClass().getSimpleName()).resolve("a.drl");
        ioService().write(path2,
                          "Some cheese");

        waitForCountDown(10000);

        {
            WildcardQuery query = new WildcardQuery(new Term(FULL_TEXT_FIELD,
                                                             "a.d*"));
            long hits = provider.findHitsByQuery(indices,
                                                 query);

            assertEquals(1,
                         hits);
        }
    }

    private static class MockIndexer implements Indexer {

        @Override
        public boolean supportsPath(final Path path) {
            return true;
        }

        @Override
        public KObject toKObject(final Path path) {
            return new TestKObjectWrapper(KObjectUtil.toKObject(path));
        }

        @Override
        public KObjectKey toKObjectKey(final Path path) {
            return new TestKObjectKeyWrapper(KObjectUtil.toKObjectKey(path));
        }
    }

    private static class TestKObjectKeyWrapper implements KObjectKey {

        protected KObjectKey delegate;

        private TestKObjectKeyWrapper(final KObjectKey delegate) {
            this.delegate = delegate;
        }

        @Override
        public String getId() {
            return delegate.getId() + "-refactoring";
        }

        @Override
        public MetaType getType() {
            return delegate.getType();
        }

        @Override
        public String getClusterId() {
            return delegate.getClusterId();
        }

        @Override
        public String getSegmentId() {
            return delegate.getSegmentId();
        }

        @Override
        public String getKey() {
            return delegate.getKey();
        }
    }

    private static class TestKObjectWrapper extends TestKObjectKeyWrapper implements KObject {

        private TestKObjectWrapper(final KObject delegate) {
            super(delegate);
        }

        @Override
        public Iterable<KProperty<?>> getProperties() {
            return ((KObject) delegate).getProperties();
        }

        @Override
        public boolean fullText() {
            return false;
        }
    }
}
