/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.services;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.client.external.ExternalDataSetClientProvider;
import org.dashbuilder.client.external.ExternalDataSetParserProvider;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.ExternalDataSetDef;

import static org.dashbuilder.common.client.StringUtils.isBlank;

@ApplicationScoped
public class JoinDataSetsService {

    @Inject
    ExternalDataSetClientProvider externalDataSetClientProvider;

    @Inject
    ExternalDataSetParserProvider parserProvider;

    @Inject
    ClientDataSetManager manager;

    static final String DATASET_COLUMN = "dataset";

    public void joinDataSets(ExternalDataSetDef def, DataSetLookup lookup, DataSetReadyCallback listener) {
        var onError = new AtomicBoolean(false);
        var dataSetsMap = new HashMap<String, DataSet>();
        var uuids = def.getJoin();

        for (var uuid : uuids) {
            var dsLookup = DataSetLookupFactory.newDataSetLookupBuilder().dataset(uuid).buildLookup();
            externalDataSetClientProvider.fetchAndRegister(uuid, dsLookup, new DataSetReadyCallback() {

                @Override
                public void callback(DataSet dataSet) {
                    if (onError.get()) {
                        return;
                    }
                    dataSetsMap.put(uuid, dataSet);
                    if (dataSetsMap.size() == uuids.size()) {
                        finishJoin(def, lookup, listener, dataSetsMap);
                    }
                }

                @Override
                public void notFound() {
                    if (onError.get()) {
                        return;
                    }
                    listener.onError(new ClientRuntimeError("Data Set \"" + uuid + "\" was not found"));
                    onError.set(true);
                }

                @Override
                public boolean onError(ClientRuntimeError error) {
                    if (onError.get()) {
                        return false;
                    }
                    listener.onError(
                            new ClientRuntimeError("Error fetching data set " + uuid + ": " + error.getMessage(), error
                                    .getThrowable()));
                    onError.set(true);
                    return false;
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    void join(DataSet joinedDataSet, DataSet dataSet) {

        if (joinedDataSet.getColumns().isEmpty()) {
            initColumns(joinedDataSet, dataSet);
        }

        var initialColumns = joinedDataSet.getColumns().stream()
                .filter(cl -> !DATASET_COLUMN.equals(cl.getId()))
                .collect(Collectors.toList());

        verifyColumnsCompatibility(initialColumns, dataSet);

        dataSet.getColumns()
                .forEach(cl -> joinedDataSet.getColumnById(cl.getId()).getValues().addAll(cl.getValues()));

        for (var i = 0; i < dataSet.getRowCount(); i++) {
            joinedDataSet.getColumnById(DATASET_COLUMN).getValues().add(dataSet.getUUID());
        }

    }

    private void finishJoin(ExternalDataSetDef def,
                            DataSetLookup lookup,
                            DataSetReadyCallback listener,
                            HashMap<String, DataSet> dataSetsMap) {
        var joinedDataSet = DataSetFactory.newEmptyDataSet();
        var uuids = def.getJoin();
        var uuid = lookup.getDataSetUUID();

        uuids.stream()
                .map(dataSetsMap::get)
                .filter(ds -> !isEmpty(ds))
                .forEach(ds -> join(joinedDataSet, ds));

        if (!isBlank(def.getExpression())) {
            var jsonArray = parserProvider.get().toJsonArray(joinedDataSet);
            String transformedArray = "";
            try {
                transformedArray = externalDataSetClientProvider.applyExpression(def.getExpression(), jsonArray);
            } catch (Exception e) {
                listener.onError(new ClientRuntimeError("Error applying expression to joined dataset", e));
                return;
            }
            DataSet transformedDataSet = null;
            try {
                transformedDataSet = parserProvider.get().parseDataSet(transformedArray);
            } catch (Exception e) {
                listener.onError(new ClientRuntimeError("Not able to parse data set after expression was applied", e));
                return;
            }
            transformedDataSet.setUUID(uuid);
            externalDataSetClientProvider.applyColumnsToDataSet(def, transformedDataSet);
            manager.registerDataSet(transformedDataSet);
        } else {
            joinedDataSet.setUUID(uuid);
            manager.registerDataSet(joinedDataSet);
        }

        DataSet result = null;
        try {
            result = manager.lookupDataSet(lookup);
        } catch (Exception e) {
            listener.onError(new ClientRuntimeError("Error during dataset lookup", e));
            return;
        }

        externalDataSetClientProvider.handleCache(uuid);
        listener.callback(result);
    }

    private void verifyColumnsCompatibility(List<DataColumn> initialColumns, DataSet dataSet) {
        var columnsToJoin = dataSet.getColumns();
        if (columnsToJoin.size() != initialColumns.size()) {
            throw new RuntimeException("Data set " + dataSet.getDefinition().getUUID() +
                    " have different number of columns");
        }

        for (var i = 0; i < columnsToJoin.size(); i++) {
            var columnToJoin = columnsToJoin.get(i);
            var initColumn = columnsToJoin.get(i);

            if (columnToJoin.getColumnType() != initColumn.getColumnType()) {
                throw new RuntimeException("Data set " + dataSet.getDefinition().getUUID() + " column " +
                        columnToJoin.getId() + " should be of type " + initColumn.getColumnType());
            }
        }

    }

    private void initColumns(DataSet joinedDataSet, DataSet dataSet) {
        // data columns
        dataSet.getColumns().forEach((cl -> joinedDataSet.addColumn(cl.getId(), cl.getColumnType())));
        joinedDataSet.addColumn(DATASET_COLUMN, ColumnType.LABEL);

    }

    private boolean isEmpty(DataSet dataSet) {
        return dataSet.getRowCount() == 0 && (dataSet.getColumns() == null || dataSet.getColumns().isEmpty());
    }

}
