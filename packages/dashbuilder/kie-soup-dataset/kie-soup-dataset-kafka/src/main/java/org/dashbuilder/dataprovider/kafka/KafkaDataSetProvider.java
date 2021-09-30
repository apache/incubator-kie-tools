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
package org.dashbuilder.dataprovider.kafka;

import java.util.List;
import java.util.function.Consumer;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataprovider.StaticDataSetProvider;
import org.dashbuilder.dataprovider.kafka.model.KafkaMetric;
import org.dashbuilder.dataprovider.kafka.model.KafkaMetricsRequest;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.DataSetDefRegistryListener;
import org.dashbuilder.dataset.def.KafkaDataSetDef;
import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaDataSetProvider implements DataSetProvider, DataSetDefRegistryListener {

    public static final String DOMAIN_COLUMN = "DOMAIN";
    public static final String TYPE_COLUMN = "TYPE";
    public static final String NAME_COLUMN = "NAME";
    public static final String ATTRIBUTE_COLUMN = "ATTRIBUTE";
    public static final String VALUE_COLUMN = "VALUE";

    protected StaticDataSetProvider staticDataSetProvider;
    protected Logger log = LoggerFactory.getLogger(KafkaDataSetProvider.class);

    private static KafkaDataSetProvider instance = null;

    public static KafkaDataSetProvider get() {
        if (instance == null) {
            StaticDataSetProvider staticDataSetProvider = DataSetCore.get().getStaticDataSetProvider();
            DataSetDefRegistry dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
            instance = new KafkaDataSetProvider(staticDataSetProvider);
            dataSetDefRegistry.addListener(instance);
        }
        return instance;
    }

    public KafkaDataSetProvider() {
        //empty
    }

    public KafkaDataSetProvider(StaticDataSetProvider staticDataSetProvider) {
        this.staticDataSetProvider = staticDataSetProvider;
    }

    @SuppressWarnings("rawtypes")
    public DataSetProviderType getType() {
        return DataSetProviderType.KAFKA;
    }

    public DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception {
        DataSet dataSet = lookupDataSet(def, null);
        if (dataSet == null) {
            return null;
        }
        return dataSet.getMetadata();
    }

    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception {
        KafkaMetricsRequest request = buildRequestFromDef(def);
        List<KafkaMetric> metrics = loadMetrics(request);
        DataSet dataSet = toDataSet(metrics);
        dataSet.setUUID(def.getUUID());
        dataSet.setDefinition(def);
        staticDataSetProvider.registerDataSet(dataSet);
        return staticDataSetProvider.lookupDataSet(def, lookup);
    }

    List<KafkaMetric> loadMetrics(KafkaMetricsRequest request) {
        List<KafkaMetric> metrics;
        try {
            metrics = KafkaMetricsProvider.get().getMetrics(request);
        } catch (Exception e) {
            log.error("Error retrieving metrics from Kafka: {}", e.getMessage());
            log.debug("Error retrieving metrics from Kafka", e);
            throw new RuntimeException("Error connecting to Kafka, check if the host/port is correct and the server is running. See logs for more details.");
        }

        if (metrics.isEmpty()) {
            throw new RuntimeException(noMetricsErrorMessage(request));
        }
        
        return metrics;
    }

    String noMetricsErrorMessage(KafkaMetricsRequest request) {
        StringBuilder sb = new StringBuilder("No metrics were found. Check if ");
        Consumer<String> appendParamCheck = s -> sb.append(String.format(", %s is correct", s));
        sb.append("the " + request.getMetricsTarget().name() + " has available metrics");
        request.clientId().ifPresent(c -> appendParamCheck.accept("client id " + c));
        request.nodeId().ifPresent(c -> appendParamCheck.accept("node id " + c));
        request.topic().ifPresent(c -> appendParamCheck.accept("topic " + c));
        sb.append(" and the filter matches any metrics");
        return sb.toString();
    }

    private KafkaMetricsRequest buildRequestFromDef(DataSetDef def) {
        if (!(def instanceof KafkaDataSetDef)) {
            throw new IllegalArgumentException("Not a Kafka data set definition");
        }

        KafkaDataSetDef kafkaDef = (KafkaDataSetDef) def;

        if (kafkaDef.getTarget() != MetricsTarget.BROKER && kafkaDef.getClientId() == null) {
            throw new IllegalArgumentException("Client Id is required for producer or consumer metrics");
        }

        return KafkaMetricsRequest.Builder.newBuilder(kafkaDef.getHost(),
                                                      kafkaDef.getPort())
                                          .target(kafkaDef.getTarget())
                                          .filter(kafkaDef.getFilter())
                                          .clientId(kafkaDef.getClientId())
                                          .nodeId(kafkaDef.getNodeId())
                                          .topic(kafkaDef.getTopic())
                                          .partition(kafkaDef.getPartition())
                                          .build();
    }

    DataSet toDataSet(List<KafkaMetric> metrics) {
        DataSet dataSet = DataSetFactory.newEmptyDataSet();

        dataSet.addColumn(DOMAIN_COLUMN, ColumnType.LABEL);
        dataSet.addColumn(TYPE_COLUMN, ColumnType.LABEL);
        dataSet.addColumn(NAME_COLUMN, ColumnType.LABEL);
        dataSet.addColumn(ATTRIBUTE_COLUMN, ColumnType.LABEL);
        dataSet.addColumn(VALUE_COLUMN, findValueColumnType(metrics));

        metrics.stream()
               .map(metric -> new Object[]{
                                           metric.getDomain(),
                                           metric.getType(),
                                           metric.getName(),
                                           metric.getAttribute(),
                                           metric.getValue()})
               .forEach(row -> dataSet.addValuesAt(dataSet.getRowCount(), row));

        return dataSet;
    }

    private ColumnType findValueColumnType(List<KafkaMetric> metrics) {
        if (metrics.stream().allMatch(m -> m.getValue() instanceof Number)) {
            return ColumnType.NUMBER;
        }
        return ColumnType.LABEL;
    }

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
        // consider that the dataset is always outdated to collect latest metrics
        return true;
    }

}
