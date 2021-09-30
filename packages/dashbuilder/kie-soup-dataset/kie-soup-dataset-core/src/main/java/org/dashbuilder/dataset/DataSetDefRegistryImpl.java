/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDefRegistryListener;
import org.dashbuilder.dataset.def.DataSetPostProcessor;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.scheduler.Scheduler;
import org.dashbuilder.scheduler.SchedulerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data set definitions registry
 */
public class DataSetDefRegistryImpl implements DataSetDefRegistry {

    protected Logger log = LoggerFactory.getLogger(DataSetDefRegistryImpl.class);
    protected DataSetProviderRegistry dataSetProviderRegistry;
    protected Scheduler scheduler;
    protected Map<String, DataSetDefEntry> dataSetDefMap = new HashMap<>();
    protected Set<DataSetDefRegistryListener> listenerSet = new HashSet<>();

    public DataSetDefRegistryImpl() {
    }

    public DataSetDefRegistryImpl(DataSetProviderRegistry dataSetProviderRegistry, Scheduler scheduler) {
        this.dataSetProviderRegistry = dataSetProviderRegistry;
        this.scheduler = scheduler;
        if (scheduler == null) {
            log.warn("No scheduler set. Data set refresh features are disabled.");
        }
    }

    public DataSetProviderRegistry getDataSetProviderRegistry() {
        return dataSetProviderRegistry;
    }

    public void setDataSetProviderRegistry(DataSetProviderRegistry dataSetProviderRegistry) {
        this.dataSetProviderRegistry = dataSetProviderRegistry;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected class DataSetDefEntry extends SchedulerTask {
        DataSetDef def;
        long lastRefreshTime;
        long refreshInMillis;

        List<DataSetPreprocessor> preprocessors;
        List<DataSetPostProcessor> postProcessors;

        public DataSetDefEntry(DataSetDef def) {
            this.def = def;
            this.lastRefreshTime = System.currentTimeMillis();
            this.refreshInMillis = -1;
            if (def.getRefreshTime() != null && def.getRefreshTime().trim().length() > 0) {
                TimeAmount tf = TimeAmount.parse(def.getRefreshTime());
                this.refreshInMillis = tf.toMillis();
            }

        }

        public void registerDataSetPreprocessor(DataSetPreprocessor preprocessor) {
            if (preprocessors == null) {
                preprocessors = new ArrayList<>();
            }
            preprocessors.add(preprocessor);
        }

        public void registerDataSetPostProcessor(DataSetPostProcessor postProcessor) {
            if (postProcessors == null) {
                postProcessors = new ArrayList<>();
            }
            postProcessors.add(postProcessor);
        }

        public List<DataSetPreprocessor> getDataSetPreprocessors() {
            return preprocessors;
        }

        public List<DataSetPostProcessor> getDataSetPostProcessors() {
            return postProcessors;
        }

        public void schedule() {
            if (refreshInMillis != -1 && scheduler != null) {
                scheduler.schedule(this, refreshInMillis / 1000);
            }
        }

        public void unschedule() {
            if (scheduler != null) {
                scheduler.unschedule(getKey());
            }
        }

        public boolean isStale() {
            if (refreshInMillis == -1) {
                return false;
            }
            if (!def.isRefreshAlways()) {
                DataSetProvider provider = resolveProvider(def);
                return provider.isDataSetOutdated(def);
            }
            return System.currentTimeMillis() >= lastRefreshTime + refreshInMillis;
        }

        public void stale() {
            lastRefreshTime = System.currentTimeMillis();
            onDataSetDefStale(def);
        }

        @Override
        public String getDescription() {
            return "DataSetDef refresh task " + def.getUUID();
        }

        @Override
        public String getKey() {
            return def.getUUID();
        }
        @Override
        public void execute() {
            if (isStale()) {
                stale();
            }
        }
    }

    protected DataSetProvider resolveProvider(DataSetDef dataSetDef) {
        DataSetProviderType type = dataSetDef.getProvider();
        if (type != null) {
            DataSetProvider dataSetProvider = dataSetProviderRegistry.getDataSetProvider(type);
            if (dataSetProvider != null) return dataSetProvider;
        }
        throw new IllegalStateException("DataSetProvider not found: " + dataSetDef.getProvider());
    }

    public synchronized List<DataSetDef> getDataSetDefs(boolean onlyPublic) {
        List<DataSetDef> results = new ArrayList<>();
        for (DataSetDefEntry r : dataSetDefMap.values()) {
            if (!onlyPublic || r.def.isPublic()) {
                results.add(r.def);
            }
        }
        return results;
    }

    public synchronized DataSetDef getDataSetDef(String uuid) {
        DataSetDefEntry record = dataSetDefMap.get(uuid);
        if (record == null) return null;
        return record.def;
    }

    public synchronized void registerPreprocessor(String uuid, DataSetPreprocessor preprocessor) {
        DataSetDefEntry record = dataSetDefMap.get(uuid);
        if (record == null) {
            throw new IllegalStateException("DataSetDef not found: " + uuid);
        }
        record.registerDataSetPreprocessor(preprocessor);
    }

    public synchronized void registerPostProcessor(String uuid, DataSetPostProcessor postProcessor) {
        DataSetDefEntry record = dataSetDefMap.get(uuid);
        if (record == null) {
            throw new IllegalStateException("DataSetDef not found: " + uuid);
        }
        record.registerDataSetPostProcessor(postProcessor);
    }

    public synchronized List<DataSetPreprocessor> getDataSetDefPreProcessors(String uuid) {
        DataSetDefEntry record = dataSetDefMap.get(uuid);
        if (record == null) {
            return null;
        }
        return record.getDataSetPreprocessors();
    }

    public synchronized List<DataSetPostProcessor> getDataSetDefPostProcessors(String uuid) {
        DataSetDefEntry record = dataSetDefMap.get(uuid);
        if (record == null) {
            return null;
        }
        return record.getDataSetPostProcessors();
    }

    public synchronized void registerDataSetDef(DataSetDef newDef) {
        registerDataSetDef(newDef, "system", "register(" + newDef.getUUID() + ")");
    }

    public synchronized DataSetDef removeDataSetDef(String uuid) {
        return removeDataSetDef(uuid, "system", "remove(" + uuid + ")");
    }

    public synchronized void registerDataSetDef(DataSetDef newDef, String subjectId, String message) {

        // Register the new entry
        DataSetDefEntry oldEntry = _removeDataSetDef(newDef.getUUID());
        dataSetDefMap.put(newDef.getUUID(), new DataSetDefEntry(newDef));
        DataSetDefEntry newEntry = dataSetDefMap.get(newDef.getUUID());

        // Notify the proper event
        if (oldEntry != null) {
            onDataSetDefModified(oldEntry.def, newDef);
        } else {
            onDataSetDefRegistered(newDef);
        }
        // Register the data set into the scheduler
        newEntry.schedule();
    }

    public synchronized DataSetDef removeDataSetDef(String uuid, String subjectId, String message) {
        DataSetDefEntry oldEntry = _removeDataSetDef(uuid);
        if (oldEntry == null) {
            return null;
        }

        // Notify about the removal
        onDataSetDefRemoved(oldEntry.def);
        return oldEntry.def;
    }

    protected DataSetDefEntry _removeDataSetDef(String uuid) {
        DataSetDefEntry oldEntry = dataSetDefMap.remove(uuid);
        if (oldEntry == null) {
            return null;
        }

        // Remove from the scheduler
        oldEntry.unschedule();

        // Make any data set reference stale
        oldEntry.stale();
        return oldEntry;
    }

    @Override
    public void addListener(DataSetDefRegistryListener listener) {
        listenerSet.add(listener);
    }

    public Set<DataSetDefRegistryListener> getListeners() {
        return listenerSet;
    }

    protected void onDataSetDefStale(DataSetDef def) {
        for (DataSetDefRegistryListener listener : listenerSet) {
            listener.onDataSetDefStale(def);
        }
    }

    protected void onDataSetDefModified(DataSetDef olDef, DataSetDef newDef) {
        for (DataSetDefRegistryListener listener : listenerSet) {
            listener.onDataSetDefModified(olDef, newDef);
        }
    }

    protected void onDataSetDefRegistered(DataSetDef newDef) {
        for (DataSetDefRegistryListener listener : listenerSet) {
            listener.onDataSetDefRegistered(newDef);
        }
    }

    protected void onDataSetDefRemoved(DataSetDef oldDef) {
        for (DataSetDefRegistryListener listener : listenerSet) {
            listener.onDataSetDefRemoved(oldDef);
        }
    }
}
