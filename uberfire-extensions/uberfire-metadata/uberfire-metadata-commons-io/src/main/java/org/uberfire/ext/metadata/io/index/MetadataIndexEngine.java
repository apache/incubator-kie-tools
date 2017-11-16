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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
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

import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class MetadataIndexEngine implements MetaIndexEngine {

    private final MetaModelBuilder metaModelBuilder;
    private Logger logger = LoggerFactory.getLogger(MetadataIndexEngine.class);
    private final IndexProvider provider;
    private final Map<KCluster, AtomicInteger> batchMode = new ConcurrentHashMap<>();
    private final Map<KCluster, List<KObject>> batchSet = new ConcurrentHashMap<>();
    private final Collection<Runnable> beforeDispose = new ArrayList<>();

    public MetadataIndexEngine(IndexProvider provider,
                               MetaModelStore metaModelStore) {
        this.provider = provider;
        this.metaModelBuilder = new MetaModelBuilder(metaModelStore);
        PriorityDisposableRegistry.register(this);
    }

    @Override
    public boolean freshIndex(KCluster cluster) {
        boolean isFreshIndex = this.provider.isFreshIndex(cluster) && !batchMode.containsKey(cluster);
        if (logger.isDebugEnabled()) {
            logger.debug("Is fresh index? " + isFreshIndex);
        }
        return isFreshIndex;
    }

    @Override
    public void startBatch(KCluster cluster) {
        final AtomicInteger batchStack = batchMode.get(cluster);
        if (batchStack == null) {
            batchMode.put(cluster,
                          new AtomicInteger());
        } else {
            if (batchStack.get() < 0) {
                batchStack.set(1);
            } else {
                batchStack.incrementAndGet();
            }
        }
    }

    @Override
    public void index(KObject kObject) {

        if (this.isBatch(kObject)) {
            KClusterImpl index = new KClusterImpl(kObject.getClusterId());
            this.batchSet.putIfAbsent(index,
                                      new ArrayList<>());
            List<KObject> store = this.batchSet.get(index);
            store.add(kObject);
            this.batchSet.put(index,
                              store);
        } else {
            this.metaModelBuilder.updateMetaModel(kObject);
            this.provider.index(kObject);
        }
    }

    private boolean isBatch(KObject object) {
        final AtomicInteger batchStack = batchMode.get(new KClusterImpl(object.getClusterId()));
        return batchStack != null && batchStack.get() > 0;
    }

    @Override
    public void index(KObject... objects) {
        List<KObject> kObjects = Lists.newArrayList(objects);
        kObjects.forEach(kObject -> this.metaModelBuilder.updateMetaModel(kObject));
        this.provider.index(kObjects);
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
        final AtomicInteger batchStack = batchMode.get(cluster);
        if (batchStack != null) {
            int value = batchStack.decrementAndGet();
            if (value > 0) {
                this.provider.index(this.batchSet.get(cluster));
                batchMode.remove(cluster);
                batchSet.remove(cluster);
            }
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
