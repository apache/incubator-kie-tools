/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.ext.metadata.io.common;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.io.index.MetadataIndexEngine;
import org.uberfire.ext.metadata.io.util.MultiIndexerLock;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.impl.KObjectImpl;
import org.uberfire.ext.metadata.model.impl.KObjectKeyImpl;
import org.uberfire.ext.metadata.provider.IndexProvider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataIndexEngineBatchTest {

    private static final String TEST_INDEXER = "test-indexer";

    private MetadataIndexEngine indexEngine;

    @Mock
    private IndexProvider provider;

    @Mock
    private MetaModelStore metaModelStore;

    @Mock
    private MultiIndexerLock lock;

    private KCluster cluster;

    private KObject kObject;

    private KObjectKey kObjectKey;

    @Before
    public void setup() {
        cluster = new KClusterImpl("test-cluster");
        kObject = new KObjectImpl("1",
                                  "java",
                                  cluster.getClusterId(),
                                  "segment",
                                  "key",
                                  Collections.emptyList(),
                                  true);
        kObjectKey = new KObjectKeyImpl("key",
                                        "2",
                                        "java",
                                        cluster.getClusterId(),
                                        "segment");
        indexEngine = new MetadataIndexEngine(provider, metaModelStore, () -> lock);
    }


    @Test
    public void indexDeferredInBatchMode() throws Exception {
        indexEngine.startBatch(cluster);
        indexEngine.index(kObject);

        verify(provider, never()).index(same(kObject));
    }

    @Test
    public void indexCalledWhenBatchCommitted() throws Exception {
        indexDeferredInBatchMode();

        indexEngine.commit(cluster, TEST_INDEXER);
        verify(provider).index(same(kObject));
    }

    @Test
    public void indexNeverCalledWhenBatchAborted() throws Exception {
        indexDeferredInBatchMode();

        indexEngine.abort(cluster);
        verify(provider, never()).index(any(KObject.class));
    }

    @Test
    public void renameDeferredInBatchMode() throws Exception {
        indexEngine.startBatch(cluster);
        indexEngine.rename(kObjectKey, kObject);

        verify(provider, never()).rename(any(), any(), same(kObject));
    }

    @Test
    public void renameCalledWhenBatchCommitted() throws Exception {
        renameDeferredInBatchMode();

        indexEngine.commit(cluster, TEST_INDEXER);
        verify(provider).rename(any(), any(), same(kObject));
    }

    @Test
    public void renameNeverCalledWhenBatchAborted() throws Exception {
        renameDeferredInBatchMode();

        indexEngine.abort(cluster);
        verify(provider, never()).rename(any(), any(), same(kObject));
    }

    @Test
    public void deleteDeferredInBatchMode() throws Exception {
        indexEngine.startBatch(cluster);
        indexEngine.delete(kObjectKey);

        verify(provider, never()).delete(kObjectKey.getClusterId(), kObjectKey.getId());
    }

    @Test
    public void deleteCalledWhenBatchCommitted() throws Exception {
        deleteDeferredInBatchMode();

        indexEngine.commit(cluster, TEST_INDEXER);
        verify(provider).delete(kObjectKey.getClusterId(), kObjectKey.getId());
    }

    @Test
    public void deleteNeverCalledWhenBatchAborted() throws Exception {
        deleteDeferredInBatchMode();

        indexEngine.abort(cluster);
        verify(provider, never()).delete(kObjectKey.getClusterId(), kObjectKey.getId());
    }

    @Test
    public void indexIsReadyForClusterIndexedInPreviousServerExecution() throws Exception {
        when(provider.isFreshIndex(cluster)).thenReturn(false);

        assertTrue(indexEngine.isIndexReady(cluster, TEST_INDEXER));
    }

    @Test
    public void indexIsReadyAfterCommit() throws Exception {
        when(provider.isFreshIndex(cluster)).thenReturn(false);

        indexEngine.startBatch(cluster);
        indexEngine.index(kObject);
        indexEngine.commit(cluster, TEST_INDEXER);

        assertTrue(indexEngine.isIndexReady(cluster, TEST_INDEXER));
    }

    @Test
    public void indexIsNotReadyForIndexerDuringCommit() throws Exception {
        when(provider.isFreshIndex(cluster)).thenReturn(false);
        // Simulate commit in progress by making lock appear held.
        when(lock.isLockedBy(TEST_INDEXER)).thenReturn(true);

        indexEngine.startBatch(cluster);

        assertFalse(indexEngine.isIndexReady(cluster, TEST_INDEXER));
    }

    @Test
    public void indexIsReadyForOtherIndexerDuringCommit() throws Exception {
        when(provider.isFreshIndex(cluster)).thenReturn(false);
        // Simulate commit in progress by making lock appear held.
        when(lock.isLockedBy(any())).then(inv -> TEST_INDEXER.equals(inv.getArgumentAt(0, String.class)));

        indexEngine.startBatch(cluster);

        assertTrue(indexEngine.isIndexReady(cluster, "other-indexer"));
    }

    @Test
    public void commitAbortedWhenClusterIsDeleted() throws Exception {
        indexEngine.startBatch(cluster);
        indexEngine.index(kObject);
        indexEngine.delete(cluster);
        indexEngine.commit(cluster, "test-indexer");

        verify(provider, never()).index(any(KObject.class));
    }

}
