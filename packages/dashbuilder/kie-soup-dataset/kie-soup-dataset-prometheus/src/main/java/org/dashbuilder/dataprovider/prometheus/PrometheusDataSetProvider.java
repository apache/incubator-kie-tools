/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.prometheus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataprovider.prometheus.client.PrometheusClient;
import org.dashbuilder.dataprovider.prometheus.client.QueryResponse;
import org.dashbuilder.dataprovider.prometheus.client.Result;
import org.dashbuilder.dataprovider.prometheus.client.ResultType;
import org.dashbuilder.dataprovider.prometheus.client.Status;
import org.dashbuilder.dataprovider.prometheus.client.Value;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.DataSetDefRegistryListener;
import org.dashbuilder.dataset.def.PrometheusDataSetDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrometheusDataSetProvider implements DataSetProvider, DataSetDefRegistryListener {

    public static final String VALUE_COLUMN = "VALUE";
    public static final String TIME_COLUMN = "TIME";

    protected StaticDataSetProvider staticDataSetProvider;
    protected Logger log = LoggerFactory.getLogger(PrometheusDataSetProvider.class);

    private static PrometheusDataSetProvider instance = null;

    public static PrometheusDataSetProvider get() {
        if (instance == null) {
            StaticDataSetProvider staticDataSetProvider = DataSetCore.get().getStaticDataSetProvider();
            DataSetDefRegistry dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
            instance = new PrometheusDataSetProvider(staticDataSetProvider);
            dataSetDefRegistry.addListener(instance);
        }
        return instance;
    }

    public PrometheusDataSetProvider() {}

    public PrometheusDataSetProvider(StaticDataSetProvider staticDataSetProvider) {
        this.staticDataSetProvider = staticDataSetProvider;
    }

    @SuppressWarnings("rawtypes")
    public DataSetProviderType getType() {
        return DataSetProviderType.PROMETHEUS;
    }

    public DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception {
        DataSet dataSet = lookupDataSet(def, null);
        if (dataSet == null) {
            return null;
        }
        return dataSet.getMetadata();
    }

    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception {
        String baseUrl = ((PrometheusDataSetDef) def).getServerUrl();
        String query = ((PrometheusDataSetDef) def).getQuery();
        QueryResponse response = new PrometheusClient(baseUrl).query(query);

        if (response.getStatus() == Status.ERROR) {
            throw new IllegalArgumentException("Error response received from Prometheus: " + response.getError());
        }

        DataSet dataSet = toDataSet(response);
        dataSet.setUUID(def.getUUID());
        dataSet.setDefinition(def);
        staticDataSetProvider.registerDataSet(dataSet);
        return staticDataSetProvider.lookupDataSet(def, lookup);
    }

    protected DataSet toDataSet(QueryResponse response) {
        DataSet dataSet = DataSetFactory.newEmptyDataSet();
        List<Result> results = response.getResults();

        Set<String> metricColumns = getMetricColumns(results);

        metricColumns.forEach(c -> dataSet.addColumn(c, ColumnType.LABEL));

        dataSet.addColumn(TIME_COLUMN, ColumnType.NUMBER);
        dataSet.addColumn(VALUE_COLUMN, response.getResultType() == ResultType.STRING
                ? ColumnType.TEXT
                : ColumnType.NUMBER);

        for (Result result : results) {
            for (Value value : result.getValues()) {
                Map<String, String> metric = result.getMetric();
                Object[] row = new Object[metric.size() + 2];
                int i = 0;
                for (String key : metricColumns) {
                    row[i++] = metric.get(key);
                }
                row[i++] = value.getTimestamp();
                row[i] = value.getValue();
                dataSet.addValuesAt(dataSet.getRowCount(), row);
            }
        }
        return dataSet;
    }

    private Set<String> getMetricColumns(List<Result> results) {
        return results.isEmpty() || results.get(0).getMetric() == null
                ? Collections.emptySet()
                : results.get(0).getMetric().keySet();
    }

    // Listen to changes on the data set definition registry
    @Override
    public void onDataSetDefStale(DataSetDef def) {
        staticDataSetProvider.removeDataSet(def.getUUID());
    }

    @Override
    public void onDataSetDefModified(DataSetDef olDef, DataSetDef newDef) {
        staticDataSetProvider.removeDataSet(olDef.getUUID());
    }

    @Override
    public void onDataSetDefRemoved(DataSetDef oldDef) {
        staticDataSetProvider.removeDataSet(oldDef.getUUID());
    }

    @Override
    public void onDataSetDefRegistered(DataSetDef newDef) {
        // empty
    }

    @Override
    public boolean isDataSetOutdated(DataSetDef def) {
        // consider that the dataset is always outdated because Prometheus is about realtime metrics 
        return true;
    }
}
