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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.event.IndexEvent;
import org.uberfire.ext.metadata.event.IndexEvent.DeletedEvent;
import org.uberfire.ext.metadata.event.IndexEvent.NewlyIndexedEvent;
import org.uberfire.ext.metadata.event.IndexEvent.RenamedEvent;
import org.uberfire.ext.metadata.io.util.MultiIndexerLock;
import org.uberfire.ext.metadata.metamodel.MetaModelBuilder;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.provider.IndexProvider;

import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class MetadataIndexEngine implements MetaIndexEngine {

    private Map<KCluster, MultiIndexerLock> batchLocks = new ConcurrentHashMap<>();
    private final MetaModelBuilder metaModelBuilder;
    private final Logger logger = LoggerFactory.getLogger(MetadataIndexEngine.class);
    private final IndexProvider provider;
    private final ThreadLocal<Map<KCluster, List<IndexEvent>>> batchSets = ThreadLocal.withInitial(() -> new HashMap<>());
    private final Collection<Runnable> beforeDispose = new ArrayList<>();
    private final Supplier<MultiIndexerLock> lockSupplier;

    public MetadataIndexEngine(IndexProvider provider,
                               MetaModelStore metaModelStore,
                               Supplier<MultiIndexerLock> lockSupplier) {
        this.provider = provider;
        this.metaModelBuilder = new MetaModelBuilder(metaModelStore);
        this.lockSupplier = lockSupplier;
        PriorityDisposableRegistry.register(this);
        this.provider.observerInitialization(this::cleanBatchLocks);
    }

    public MetadataIndexEngine(IndexProvider provider,
                               MetaModelStore metaModelStore) {
        this(provider,
             metaModelStore,
             () -> new MultiIndexerLock(new ReentrantLock()));
    }

    @Override
    public synchronized boolean freshIndex(KCluster cluster) {
        boolean containsKey = batchLocks.containsKey(cluster);
        boolean isFreshIndex = this.provider.isFreshIndex(cluster) && !containsKey;
        if (logger.isDebugEnabled()) {
            logger.debug(MessageFormat.format("Cluster: {0} | Batch Locks contains key? {1} | Is Fresh Index? {2}",
                                              cluster.getClusterId(),
                                              containsKey,
                                              isFreshIndex));
        }
        return isFreshIndex;
    }

    @Override
    public boolean isIndexReady(KCluster cluster,
                                String indexerId) {
        final MultiIndexerLock lock;
        return !provider.isFreshIndex(cluster) && ((lock = batchLocks.get(cluster)) == null || !lock.isLockedBy(indexerId));
    }

    @Override
    public void prepareBatch(KCluster cluster) {
        batchLocks.computeIfAbsent(cluster,
                                   ignore -> lockSupplier.get());
    }

    @Override
    public void startBatch(KCluster cluster) {
        prepareBatch(cluster);
        Map<KCluster, List<IndexEvent>> batchSet = batchSets.get();
        if (batchSet.containsKey(cluster)) {
            throw new IllegalStateException(String.format("Cannot start a batch for cluster [id=%s] when there is already a batch started on this thread [%s]",
                                                          cluster.getClusterId(),
                                                          Thread.currentThread().getName()));
        } else {
            batchSet.put(cluster,
                         new ArrayList<>());
        }
    }

    private void doOrDeferAction(KCluster index,
                                 IndexEvent event) {
        if (this.isBatch(index)) {
            List<IndexEvent> store = this.batchSets.get().get(index);
            store.add(event);
        } else {
            doAction(event);
        }
    }

    @Override
    public void index(KObject kObject) {
        KCluster index = new KClusterImpl(kObject.getClusterId());
        doOrDeferAction(index,
                        new NewlyIndexedEvent(kObject));
    }

    private void doAction(IndexEvent event) {
        switch (event.getKind()) {
            case NewlyIndexed: {
                NewlyIndexedEvent newlyIndexedEvent = (NewlyIndexedEvent) event;
                doIndex(newlyIndexedEvent.getKObject());
                break;
            }
            case Renamed: {
                RenamedEvent renamedEvent = (RenamedEvent) event;
                doRename(renamedEvent.getSource(),
                         renamedEvent.getTarget());
                break;
            }
            case Deleted: {
                DeletedEvent deletedEvent = (DeletedEvent) event;
                doDelete(deletedEvent.getDeleted());
                break;
            }
            default:
                throw new UnsupportedOperationException("Unrecognized index event kind: " + event.getKind());
        }
    }

    private void doIndex(KObject kObject) {
        this.metaModelBuilder.updateMetaModel(kObject);
        this.provider.index(kObject);
    }

    private boolean isBatch(KCluster cluster) {
        Map<KCluster, List<IndexEvent>> batchSet = batchSets.get();
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

        KCluster index = new KClusterImpl(from.getClusterId());
        doOrDeferAction(index,
                        new RenamedEvent(from,
                                         to));
    }

    private void doRename(KObjectKey from,
                          KObject to) {
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
        this.batchLocks.remove(cluster);
        this.provider.delete(cluster.getClusterId());
    }

    @Override
    public void delete(KObjectKey objectKey) {
        KCluster index = new KClusterImpl(objectKey.getClusterId());
        doOrDeferAction(index,
                        new DeletedEvent(objectKey));
    }

    private void doDelete(KObjectKey objectKey) {
        this.provider.delete(objectKey.getClusterId(),
                             objectKey.getId());
    }

    @Override
    public void commit(KCluster cluster,
                       String indexerId) {
        final MultiIndexerLock lock = batchLocks.get(cluster);
        final List<IndexEvent> batchSet = batchSets.get().get(cluster);
        final boolean clusterDeleted = lock == null && batchSet != null;

        if (clusterDeleted) {
            logger.info("Cluster [{}] was deleted. Aborting commit for indexer [{}].",
                        cluster.getClusterId(),
                        indexerId);
            abort(cluster);
            return;
        }

        try {
            if (batchSet == null) {
                throw new IllegalStateException(String.format("Cannot commit batch for cluster [id=%s] when no batch has been started in thread [%s].",
                                                              cluster.getClusterId(),
                                                              Thread.currentThread().getName()));
            } else if (batchSet.isEmpty()) {
                removeThreadLocalBatchState(cluster);
            } else {
                doCommit(cluster,
                         batchSet,
                         lock,
                         indexerId);
            }
        } catch (Throwable t) {
            abort(cluster);
            throw t;
        }
    }

    private void doCommit(KCluster cluster,
                          List<IndexEvent> batchSet,
                          MultiIndexerLock lock,
                          String indexerId) {
        try {
            lock.lock(indexerId);
            batchSet.forEach(this::doAction);
            removeThreadLocalBatchState(cluster);
        } finally {
            lock.unlock(indexerId);
        }
    }

    @Override
    public void abort(KCluster cluster) {
        removeThreadLocalBatchState(cluster);
    }

    private void removeThreadLocalBatchState(KCluster cluster) {
        Map<KCluster, List<IndexEvent>> batchSet = batchSets.get();
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
    public boolean isAlive() {
        return this.provider.isAlive();
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

    public void cleanBatchLocks() {
        this.batchLocks = new ConcurrentHashMap<>();
    }
}
