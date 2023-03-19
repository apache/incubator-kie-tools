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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.DataSetPostProcessor;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.def.StaticDataSetDef;
import org.dashbuilder.dataset.exception.DataSetLookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backend implementation of the DataSetManager interface. It provides an uniform interface to the data set
 * registration and lookup services on top of the data set provider interface.
 */
public class DataSetManagerImpl implements DataSetManager {

    private static final String DATA_SET_NOT_FOUND = "Data set not found: ";
    protected DataSetDefRegistry dataSetDefRegistry;
    protected DataSetProviderRegistry dataSetProviderRegistry;
    protected StaticDataSetProvider staticDataSetProvider;
    protected boolean pushEnabled = false;
    protected int pushMaxSize = 1024;
    protected Logger log = LoggerFactory.getLogger(DataSetManagerImpl.class);

    public DataSetManagerImpl() {
    }

    public DataSetManagerImpl(DataSetDefRegistry dataSetDefRegistry,
                              DataSetProviderRegistry dataSetProviderRegistry,
                              StaticDataSetProvider staticDataSetProvider,
                              boolean pushEnabled,
                              int pushMaxSize) {

        this.dataSetDefRegistry = dataSetDefRegistry;
        this.dataSetProviderRegistry = dataSetProviderRegistry;
        this.staticDataSetProvider = staticDataSetProvider;
        this.pushEnabled = pushEnabled;
        this.pushMaxSize = pushMaxSize;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public int getPushMaxSize() {
        return pushMaxSize;
    }

    public DataSetDefRegistry getDataSetDefRegistry() {
        return dataSetDefRegistry;
    }

    public DataSetProviderRegistry getDataSetProviderRegistry() {
        return dataSetProviderRegistry;
    }

    public StaticDataSetProvider getStaticDataSetProvider() {
        return staticDataSetProvider;
    }

    public DataSet createDataSet(String uuid) {
        DataSet dataSet = DataSetFactory.newEmptyDataSet();
        dataSet.setUUID(uuid);
        return dataSet;
    }

    public DataSet getDataSet(String uuid) {
        try {
            DataSetDef dataSetDef = dataSetDefRegistry.getDataSetDef(uuid);
            if (dataSetDef == null) {
                throw new RuntimeException(DATA_SET_NOT_FOUND + uuid);
            }

            // Fetch the specified data set
            return resolveProvider(dataSetDef)
                    .lookupDataSet(dataSetDef, null);
        } catch (Exception e) {
            throw new RuntimeException("Can't fetch the specified data set: " + uuid, e);
        }
    }

    public void registerDataSet(DataSet dataSet) {
        if (dataSet != null) {
            StaticDataSetDef def = new StaticDataSetDef();
            def.setUUID(dataSet.getUUID());
            def.setName(dataSet.getUUID());
            def.setDataSet(dataSet);
            def.setPushEnabled(pushEnabled);
            def.setPushMaxSize(pushMaxSize);
            dataSetDefRegistry.registerDataSetDef(def);

            // Register the data set after the definition. It's mandatory to do this right after since
            // the registerDataSetDef will delete any old existing data set matching the given UUID.
            staticDataSetProvider.registerDataSet(dataSet);
        }
    }

    public void registerDataSet(DataSet dataSet, List<DataSetPreprocessor> preprocessors) {
        registerDataSet(dataSet);
        for(DataSetPreprocessor p : preprocessors){
            dataSetDefRegistry.registerPreprocessor(dataSet.getUUID(), p);
        }
    }
    
    public DataSet removeDataSet(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return null;
        }

        dataSetDefRegistry.removeDataSetDef(uuid);
        return staticDataSetProvider.removeDataSet(uuid);
    }

    public DataSet lookupDataSet(DataSetLookup lookup) {
        String uuid = lookup.getDataSetUUID();
        if (StringUtils.isBlank(uuid)) {
            return null;
        }

        DataSetDef dataSetDef = dataSetDefRegistry.getDataSetDef(uuid);
        if (dataSetDef == null) {
            throw new RuntimeException(DATA_SET_NOT_FOUND + uuid);
        }
        List<DataSetPreprocessor> dataSetDefPreProcessors = dataSetDefRegistry.getDataSetDefPreProcessors(uuid);
        if (dataSetDefPreProcessors != null) {
            for(DataSetPreprocessor p : dataSetDefPreProcessors){
                p.preprocess(lookup);
            }
        }
        try {
            final DataSet dataSet = resolveProvider(dataSetDef).lookupDataSet(dataSetDef, lookup);

            List<DataSetPostProcessor> dataSetDefPostProcessors = dataSetDefRegistry.getDataSetDefPostProcessors(uuid);
            if (dataSetDefPostProcessors != null) {
                dataSetDefPostProcessors.forEach(post -> post.postProcess(lookup, dataSet));
            }

            return dataSet;
        } catch (Exception e) {
            throw new DataSetLookupException(uuid, "Can't lookup on specified data set: " + lookup.getDataSetUUID(), e);
        }
    }

    public DataSet[] lookupDataSets(DataSetLookup[] lookup) {
        DataSet[] result = new DataSet[lookup.length];
        for (int i = 0; i < lookup.length; i++) {
            result[i] = lookupDataSet(lookup[i]);
        }
        return result;
    }

    public DataSetMetadata getDataSetMetadata(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return null;
        }

        DataSetDef dataSetDef = dataSetDefRegistry.getDataSetDef(uuid);
        if (dataSetDef == null) {
            throw new RuntimeException(DATA_SET_NOT_FOUND + uuid);
        }

        try {
            return resolveProvider(dataSetDef)
                    .getDataSetMetadata(dataSetDef);
        } catch (Exception e) {
            throw new DataSetLookupException(uuid, "Can't get metadata on specified data set: " + uuid, e);
        }
    }

    public DataSetProvider resolveProvider(DataSetDef dataSetDef) {
        // Get the target data set provider
        DataSetProviderType type = dataSetDef.getProvider();
        if (type != null) {
            DataSetProvider dataSetProvider = dataSetProviderRegistry.getDataSetProvider(type);
            if (dataSetProvider != null) {
                return dataSetProvider;
            }
        }

        // If no provider is defined then return the static one
        log.warn("Please make sure the " + type + " provider has been added to the registry");
        return staticDataSetProvider;
    }
}
