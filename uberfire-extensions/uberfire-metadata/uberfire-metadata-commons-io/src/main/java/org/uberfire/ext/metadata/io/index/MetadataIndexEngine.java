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

package org.uberfire.ext.metadata.io.index;

import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.metamodel.MetaModelBuilder;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.provider.IndexProvider;

public class MetadataIndexEngine implements MetaIndexEngine {

    private final MetaModelBuilder metaModelBuilder;
    private Logger logger = LoggerFactory.getLogger(MetadataIndexEngine.class);
    private final IndexProvider provider;
    private final Map<KCluster, ReentrantLock> batchLocks = new ConcurrentHashMap<>();
    private final ThreadLocal<Map<KCluster, List<KObject>>> batchSets = ThreadLocal.withInitial(() -> new HashMap<>());
    private final Collection<Runnable> beforeDispose = new ArrayList<>();
    private final Consumer<List<KObject>> kObectBatchObserver;

    public MetadataIndexEngine(IndexProvider provider,
                               MetaModelStore metaModelStore,
                               Consumer<List<KObject>> kObectBatchObserver) {
        this.provider = provider;
        this.kObectBatchObserver = kObectBatchObserver;
        this.metaModelBuilder = new MetaModelBuilder(metaModelStore);
        PriorityDisposableRegistry.register(this);
    }

    @Override
    public boolean freshIndex(KCluster cluster) {
        boolean isFreshIndex = this.provider.isFreshIndex(cluster) && !batchLocks.containsKey(cluster);
        if (logger.isDebugEnabled()) {
            logger.debug("Is fresh index? " + isFreshIndex);
        }
        return isFreshIndex;
    }

    @Override
    public boolean isIndexReady(KCluster cluster) {
        final ReentrantLock lock;
        return !provider.isFreshIndex(cluster) && (lock = batchLocks.get(cluster)) != null && !lock.isLocked();
    }

    @Override
    public void prepareBatch(KCluster cluster) {
        batchLocks.putIfAbsent(cluster, new ReentrantLock());
    }

    @Override
    public void startBatch(KCluster cluster) {
        prepareBatch(cluster);
        Map<KCluster, List<KObject>> batchSet = batchSets.get();
        if (batchSet.containsKey(cluster)) {
            throw new IllegalStateException(String.format("Cannot start a batch for cluster [id=%s] when there is already a batch started on this thread [%s]",
                                                          cluster.getClusterId(),
                                                          Thread.currentThread().getName()));
        } else {
            batchSet.put(cluster, new ArrayList<>());
        }
    }

    @Override
    public void index(KObject kObject) {

        if (this.isBatch(kObject)) {
            KClusterImpl index = new KClusterImpl(kObject.getClusterId());
            List<KObject> store = this.batchSets.get().get(index);
            store.add(kObject);
        } else {
            doIndex(kObject);
        }
    }

    private void doIndex(KObject kObject) {
        this.metaModelBuilder.updateMetaModel(kObject);
        this.provider.index(kObject);
    }

    private boolean isBatch(KObject object) {
        KClusterImpl cluster = new KClusterImpl(object.getClusterId());
        Map<KCluster, List<KObject>> batchSet = batchSets.get();
        if (batchSet.isEmpty()) {
            // Don't hold reference to this map if there are no batches in the thread.
            batchSets.remove();
        }
        return batchSet.containsKey(cluster);
    }

    @Override
    public void rename(KObjectKey from,
                       KObject to) {

        checkNotNull("from",
                     from);
        checkNotNull("to",
                     to);
        checkCondition("renames are allowed only from same cluster",
                       from.getClusterId().equals(to.getClusterId()));

        this.provider.rename(from.getClusterId(),
                             from.getId(),
                             to);
    }

    protected boolean exists(KObjectKey from) {
        return this.provider.exists(from.getClusterId(),
                                    from.getId());
    }

    @Override
    public void delete(KCluster cluster) {
        this.provider.delete(cluster.getClusterId());
    }

    @Override
    public void delete(KObjectKey objectKey) {
        this.provider.delete(objectKey.getClusterId(),
                             objectKey.getId());
    }

    @Override
    public void delete(KObjectKey... objectsKey) {
        Arrays.stream(objectsKey).forEach(kObjectKey -> this.delete(kObjectKey));
    }

    @Override
    public void commit(KCluster cluster) {
        prepareBatch(cluster);
        ReentrantLock lock = batchLocks.get(cluster);
        List<KObject> batchSet = batchSets.get().get(cluster);

        try {
            if (batchSet == null) {
                throw new IllegalStateException(String.format("Cannot commit batch for cluster [id=%s] when no batch has been started in thread [%s].",
                                                              cluster.getClusterId(),
                                                              Thread.currentThread().getName()));
            }
            else if (batchSet.isEmpty()) {
                removeThreadLocalBatchState(cluster);
            } else {
                doCommit(cluster, batchSet, lock);
            }
        } catch (Throwable t) {
            abort(cluster);
            throw t;
        }
    }

    private void doCommit(KCluster cluster, List<KObject> kobjects, ReentrantLock lock) {
        try {
            lock.lock();
            kobjects.forEach(this::doIndex);
            removeThreadLocalBatchState(cluster);
            kObectBatchObserver.accept(kobjects);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void abort(KCluster cluster) {
        removeThreadLocalBatchState(cluster);
    }

    private void removeThreadLocalBatchState(KCluster cluster) {
        Map<KCluster, List<KObject>> batchSet = batchSets.get();
        batchSet.remove(cluster);
        if (batchSet.isEmpty()) {
            batchSets.remove();
        }
    }

    @Override
    public void beforeDispose(Runnable callback) {
        this.beforeDispose.add(checkNotNull("callback",
                                            callback));
    }

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public void dispose() {
        if (!beforeDispose.isEmpty()) {
            for (final Runnable activeDispose : beforeDispose) {
                activeDispose.run();
            }
        }
    }
}
