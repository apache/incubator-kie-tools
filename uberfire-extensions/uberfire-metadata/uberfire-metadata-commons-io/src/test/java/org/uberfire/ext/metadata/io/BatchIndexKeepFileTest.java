/*
 * Copyright 2020 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.metadata.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.ext.metadata.engine.BatchIndexListener;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.engine.Observer;
import org.uberfire.ext.metadata.io.lucene.BaseIndexTest;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

public class BatchIndexKeepFileTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{"temp-repo-test"};
    }

    @Test
    public void testIndex() {

        {
            final Path file = ioService().get("git://temp-repo-test/xxx/.keep");
            ioService().write(file,
                              "");
        }

        final IndexersFactory indexersFactory = indexersFactory();
        final MockIndexer indexer = new MockIndexer();
        indexersFactory.addIndexer(indexer);

        new BatchIndex(config.getIndexEngine(),
                       new Observer() {
                           @Override
                           public void information(final String message) {

                           }

                           @Override
                           public void warning(final String message) {

                           }

                           @Override
                           public void error(final String message) {

                           }
                       },
                       Executors.newCachedThreadPool(new DescriptiveThreadFactory()),
                       indexersFactory,
                       indexerDispatcherFactory(config.getIndexEngine()),
                       new BatchIndexListener() {
                           @Override
                           public void notifyIndexIngStarted(KCluster kCluster, Path path) {

                           }

                           @Override
                           public void notifyIndexIngFinished(KCluster kCluster, Path path) {

                           }
                       },
                       DublinCoreView.class).run(ioService().get("git://temp-repo-test/").getFileSystem(),
                                                 () -> {
                                                     try {
                                                         final String index = toKCluster(ioService().get("git://temp-repo-test/")).getClusterId();

                                                         {

                                                             long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                                                                                   new MatchAllDocsQuery());

                                                             assertEquals(4,
                                                                          hits);
                                                         }

                                                         {

                                                             TermQuery query = new TermQuery(new Term("dcore.author",
                                                                                                      "name"));

                                                             long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                                                                                   query);
                                                             assertEquals(2,
                                                                          hits);
                                                         }

                                                         {

                                                             TermQuery query = new TermQuery(new Term("dcore.author",
                                                                                                      "second"));
                                                             long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                                                                                   query);

                                                             assertEquals(1,
                                                                          hits);
                                                         }

                                                         config.dispose();
                                                     } catch (Exception ex) {
                                                         ex.printStackTrace();
                                                         fail();
                                                     }
                                                 });

        assertEquals(1, indexer.getFoundPaths().size());
        assertEquals("/xxx/keep", indexer.getFoundPaths().iterator().next().toString());
    }

    class MockIndexer implements Indexer {

        private Set<Path> foundPaths = new HashSet<>();

        @Override
        public boolean supportsPath(final Path path) {
            foundPaths.add(path);
            return false;
        }

        @Override
        public KObject toKObject(final Path path) {
            return null;
        }

        @Override
        public KObjectKey toKObjectKey(final Path path) {
            return null;
        }

        private Set<Path> getFoundPaths() {
            return foundPaths;
        }
    }
}