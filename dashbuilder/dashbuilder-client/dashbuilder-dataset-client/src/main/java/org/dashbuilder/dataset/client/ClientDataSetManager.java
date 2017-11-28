/**
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
package org.dashbuilder.dataset.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.dataset.def.DataSetPreprocessor;
import org.dashbuilder.dataset.engine.SharedDataSetOpEngine;
import org.dashbuilder.dataset.engine.index.DataSetIndex;

/**
 * Client implementation of a DataSetManager. It hold as map of data sets in memory.
 * It is designed to manipulate not quite big data sets. For big data sets the backend implementation is better,
 */
@ApplicationScoped
public class ClientDataSetManager implements DataSetManager {

    SharedDataSetOpEngine dataSetOpEngine;
    Map<String,List<DataSetPreprocessor>> preprocessorMap = new HashMap<String, List<DataSetPreprocessor>>();

    public ClientDataSetManager() {
        this.dataSetOpEngine = ClientDataSetCore.get().getSharedDataSetOpEngine();
    }

    @Override
    public DataSet createDataSet(String uuid) {
        DataSet dataSet = DataSetFactory.newEmptyDataSet();
        dataSet.setUUID(uuid);
        return dataSet;
    }

    @Override
    public DataSet getDataSet(String uuid) {
        DataSetIndex index = dataSetOpEngine.getIndexRegistry().get(uuid);
        if (index == null) {
            return null;
        }
        return index.getDataSet();
    }

    @Override
    public void registerDataSet(DataSet dataSet) {
        if (dataSet != null) {
            dataSetOpEngine.getIndexRegistry().put(dataSet);
        }
    }

    @Override
    public void registerDataSet(DataSet dataSet, List<DataSetPreprocessor> preprocessors) {
        if (dataSet != null) {
            dataSetOpEngine.getIndexRegistry().put(dataSet);

            for (DataSetPreprocessor preprocessor : preprocessors) {
                registerDataSetPreprocessor(dataSet.getUUID(), preprocessor);
            }
        }
    }

    @Override
    public DataSet removeDataSet(String uuid) {
        DataSetIndex index = dataSetOpEngine.getIndexRegistry().remove(uuid);
        if (index == null) {
            return null;
        }
        return index.getDataSet();
    }

    @Override
    public DataSet lookupDataSet(DataSetLookup lookup) {
        String uuid = lookup.getDataSetUUID();
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }

        // Get the target data set
        DataSetIndex dataSetIndex = dataSetOpEngine.getIndexRegistry().get(uuid);
        if (dataSetIndex == null) {
            return null;
        }
        List<DataSetPreprocessor> dataSetDefPreProcessors = getDataSetPreprocessors(uuid);
        if (dataSetDefPreProcessors != null) {
            for(DataSetPreprocessor p : dataSetDefPreProcessors){
                p.preprocess(lookup);
            }
        }
        DataSet dataSet = dataSetIndex.getDataSet();

        // Apply the list of operations specified (if any).
        if (!lookup.getOperationList().isEmpty()) {
            dataSet = dataSetOpEngine.execute(uuid, lookup.getOperationList());
        }

        // Trim the data set as requested.
        dataSet = dataSet.trim(lookup.getRowOffset(), lookup.getNumberOfRows());
        return dataSet;
    }

    @Override
    public DataSet[] lookupDataSets(DataSetLookup[] lookup) {
        DataSet[] result = new DataSet[lookup.length];
        for (int i = 0; i < lookup.length; i++) {
            result[i] = lookupDataSet(lookup[i]);
        }
        return result;
    }

    @Override
    public DataSetMetadata getDataSetMetadata(String uuid) {
        DataSetLookup lookup = new DataSetLookup(uuid);
        DataSet dataSet = lookupDataSet(lookup);
        if (dataSet == null) {
            return null;
        }
        return dataSet.getMetadata();
    }

    public void registerDataSetPreprocessor(String uuid, DataSetPreprocessor preprocessor) {
        List<DataSetPreprocessor> preprocessors = preprocessorMap.get(uuid);
        if (preprocessors == null) {
            preprocessorMap.put(uuid, preprocessors = new ArrayList<DataSetPreprocessor>());
        }
        preprocessors.add(preprocessor);
    }

    public List<DataSetPreprocessor> getDataSetPreprocessors(String uuid) {
        return preprocessorMap.get(uuid);
    }
}
