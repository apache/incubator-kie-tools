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
package org.dashbuilder.dataprovider;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.engine.SharedDataSetOpEngine;
import org.dashbuilder.dataset.engine.index.DataSetIndex;
import org.dashbuilder.dataset.filter.DataSetFilter;

/**
 * DataSetProvider implementation for static (in-memory) data sets.
 * <p>It's been designed with several goals in mind:
 * <ul>
 *     <li>To provide a highly reusable data set cache.</li>
 *     <li>To index almost every operation performed over a data set.</li>
 *     <li>Multiple clients requesting the same data set operations will benefit from the indexing/caching services provided.</li>
 * </ul>
 * </p>
 */
public class StaticDataSetProvider implements DataSetProvider {

    private SharedDataSetOpEngine dataSetOpEngine;

    public StaticDataSetProvider() {
    }

    public StaticDataSetProvider(SharedDataSetOpEngine dataSetOpEngine) {
        this.dataSetOpEngine = dataSetOpEngine;
    }

    public DataSetProviderType getType() {
        return DataSetProviderType.STATIC;
    }

    public DataSetMetadata getDataSetMetadata(DataSetDef def) {
        DataSet dataSet = lookupDataSet(def, null);
        if (dataSet == null) return null;
        return dataSet.getMetadata();
    }

    public void registerDataSet(DataSet dataSet) {
        dataSetOpEngine.getIndexRegistry().put(dataSet);
    }

    public DataSet removeDataSet(String uuid) {
        DataSetIndex index = dataSetOpEngine.getIndexRegistry().remove(uuid);
        return (index == null ? null : index.getDataSet());
    }

    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) {
        String uuid = def.getUUID();
        if (StringUtils.isEmpty(uuid)) return null;
        boolean isRoot = (lookup == null || lookup.isEmpty());

        // Be aware of filters on the data set definition
        DataSetFilter filter = def.getDataSetFilter();
        if (filter != null) {
            if (lookup == null) lookup = new DataSetLookup(uuid, filter);
            else lookup.addOperation(0, filter);
        }

        // Lookup the data set (with any existing filters)
        DataSet dataSet = lookupDataSet(uuid, lookup);

        // Add the proper metadata to any root data set retrieval call
        if (dataSet != null && isRoot) {
            dataSet.setUUID(uuid);
            dataSet.setDefinition(def);
        }
        return dataSet;
    }

    public DataSet lookupDataSet(String uuid, DataSetLookup lookup) {
        if (StringUtils.isEmpty(uuid)) return null;

        // Get the target data set
        DataSetIndex dataSetIndex = dataSetOpEngine.getIndexRegistry().get(uuid);
        if (dataSetIndex == null) return null;
        DataSet dataSet = dataSetIndex.getDataSet();
        if (lookup == null) return dataSet;

        // Apply the list of operations specified (if any).
        if (!lookup.getOperationList().isEmpty()) {
            dataSet = dataSetOpEngine.execute(uuid, lookup.getOperationList());
        }

        // Trim the data set as requested.
        dataSet = dataSet.trim(lookup.getRowOffset(), lookup.getNumberOfRows());
        return dataSet;
    }

    public boolean isDataSetOutdated(DataSetDef def) {
        return false;
    }
}
