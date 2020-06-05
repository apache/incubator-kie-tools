/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.kieserver.backend;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.impl.DataColumnImpl;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.kieserver.ConsoleDataSetLookup;
import org.dashbuilder.kieserver.KieServerConnectionInfo;
import org.dashbuilder.kieserver.KieServerConnectionInfoProvider;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.dashbuilder.kieserver.RuntimeKieServerDataSetProviderType;
import org.dashbuilder.kieserver.backend.rest.KieServerQueryClient;
import org.dashbuilder.kieserver.backend.rest.QueryDefinition;
import org.dashbuilder.kieserver.backend.rest.QueryFilterSpec;
import org.dashbuilder.kieserver.backend.rest.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RuntimeKieServerDataSetProvider implements DataSetProvider {

    public static final DataSetProviderType TYPE = new RuntimeKieServerDataSetProviderType();
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeKieServerDataSetProvider.class);

    @Inject
    KieServerQueryClient queryClient;

    @Inject
    KieServerConnectionInfoProvider connectionInfoProvider;

    @Override
    public DataSetProviderType getType() {
        return TYPE;
    }

    @Override
    public DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception {
        List<String> columnNames = new ArrayList<>();
        List<ColumnType> columnTypes = new ArrayList<>();
        if (def.getColumns() == null && def instanceof RemoteDataSetDef) {
            KieServerConnectionInfo connectionInfo = connectionInfoProvider.verifiedConnectionInfo((RemoteDataSetDef) def);
            QueryDefinition definition = queryClient.getQuery(connectionInfo, def.getUUID());
            if (definition.getColumns() != null) {
                addColumnsToDefinition(def, definition);
            }
        }
        def.getColumns().forEach(column -> {
            columnNames.add(column.getId());
            columnTypes.add(column.getColumnType());
        });
        return new DataSetMetadataImpl(def,
                                       def.getUUID(),
                                       -1,
                                       def.getColumns().size(),
                                       columnNames,
                                       columnTypes,
                                       -1);
    }

    @Override
    public DataSet lookupDataSet(DataSetDef def,
                                 DataSetLookup lookup) throws Exception {

        ConsoleDataSetLookup dataSetLookup = adoptLookup(def,
                                                         lookup);

        LOGGER.debug("Data Set lookup using Server Template Id: {}",
                     dataSetLookup.getServerTemplateId());
        if (dataSetLookup.getServerTemplateId() == null || dataSetLookup.getServerTemplateId().isEmpty()) {
            return buildDataSet(def,
                                new ArrayList<>(),
                                new ArrayList<>());
        }

        List<QueryParam> filterParams = new ArrayList<>();
        QueryFilterSpec filterSpec = new QueryFilterSpec();
        // apply filtering

        for (DataSetFilter filter : dataSetLookup.getOperationList(DataSetFilter.class)) {
            if (filter != null) {

                for (ColumnFilter cFilter : filter.getColumnFilterList()) {
                    if (cFilter instanceof CoreFunctionFilter) {

                        CoreFunctionFilter coreFunctionFilter = (CoreFunctionFilter) cFilter;

                        filterParams.add(new QueryParam(coreFunctionFilter.getColumnId(),
                                                        coreFunctionFilter.getType().toString(),
                                                        coreFunctionFilter.getParameters()));
                    } else if (cFilter instanceof LogicalExprFilter) {
                        LogicalExprFilter logicalExprFilter = (LogicalExprFilter) cFilter;
                        filterParams.add(new QueryParam(logicalExprFilter.getColumnId(),
                                                        logicalExprFilter.getLogicalOperator().toString(),
                                                        logicalExprFilter.getLogicalTerms()));
                    }
                }
            }
        }
        List<DataColumn> extraColumns = new ArrayList<DataColumn>();

        List<DataSetGroup> dataSetGroups = lookup.getFirstGroupOpSelections();
        for (DataSetGroup group : dataSetGroups) {
            if (group.getSelectedIntervalList() != null && group.getSelectedIntervalList().size() > 0) {
                appendIntervalSelection(group,
                                        filterParams);
            }
        }

        DataSetGroup dataSetGroup = dataSetLookup.getLastGroupOp();
        handleDataSetGroup(def,
                           dataSetGroup,
                           filterParams,
                           extraColumns);

        if (!filterParams.isEmpty()) {
            filterSpec.setParameters(filterParams.toArray(new QueryParam[filterParams.size()]));
        }

        // apply sorting
        DataSetSort sort = dataSetLookup.getFirstSortOp();
        if (sort != null) {
            SortOrder sortOrder = SortOrder.UNSPECIFIED;
            StringBuilder orderBy = new StringBuilder();
            for (ColumnSort cSort : sort.getColumnSortList()) {
                orderBy.append(cSort.getColumnId()).append(",");
                sortOrder = cSort.getOrder();
            }
            // remove last ,
            orderBy.deleteCharAt(orderBy.length() - 1);

            filterSpec.setOrderBy(orderBy.toString());
            filterSpec.setAscending(sortOrder.equals(SortOrder.ASCENDING));
        }
        final List<List> instances = performQuery((RemoteDataSetDef) def,
                                                  dataSetLookup,
                                                  filterSpec);
        LOGGER.debug("Query client returned {} row(s)",
                     instances.size());

        return buildDataSet(def,
                            instances,
                            extraColumns);
    }

    protected ConsoleDataSetLookup adoptLookup(DataSetDef def,
                                               DataSetLookup lookup) {
        ConsoleDataSetLookup dataSetLookup = null;
        if (!(lookup instanceof ConsoleDataSetLookup)) {

            if (def instanceof RemoteDataSetDef) {
                dataSetLookup = (ConsoleDataSetLookup) ConsoleDataSetLookup.fromInstance(lookup,
                                                                                         ((RemoteDataSetDef) def).getServerTemplateId());
                DataSetFilter filter = def.getDataSetFilter();
                if (filter != null) {
                    dataSetLookup.addOperation(filter);
                }
            } else {
                throw new IllegalArgumentException("DataSetLookup is of incorrect type " + lookup.getClass().getName());
            }
        } else {
            dataSetLookup = (ConsoleDataSetLookup) lookup;
        }

        return dataSetLookup;
    }

    protected List<List> performQuery(RemoteDataSetDef def,
                                      ConsoleDataSetLookup lookup,
                                      QueryFilterSpec filterSpec) {
        KieServerConnectionInfo connectionInfo = connectionInfoProvider.verifiedConnectionInfo(def);
        int pageNumber = lookup.getRowOffset() / lookup.getNumberOfRows();
        if (lookup.testMode() || connectionInfo.isReplaceQuery()) {
            QueryDefinition queryDefinition = QueryDefinition.builder()
                                                             .name(lookup.getDataSetUUID())
                                                             .source(def.getDataSource())
                                                             .target(def.getQueryTarget())
                                                             .expression(def.getDbSQL())
                                                             .build();
            QueryDefinition registered = queryClient.replaceQuery(connectionInfo, queryDefinition);
            if (registered.getColumns() != null) {
                addColumnsToDefinition(def, registered);
            }

            try {
                return queryClient.query(connectionInfo,
                                         lookup.getDataSetUUID(),
                                         filterSpec,
                                         pageNumber,
                                         lookup.getNumberOfRows());
            } catch (Exception e) {
                queryClient.unregisterQuery(connectionInfo, lookup.getDataSetUUID());
                throw new RuntimeException(e);
            }
        } else {
            if (def.getColumns() != null && def.getColumns().isEmpty()) {
                QueryDefinition query = queryClient.getQuery(connectionInfo, def.getUUID());
                addColumnsToDefinition(def, query);
            }
            return queryClient.query(connectionInfo,
                                     lookup.getDataSetUUID(),
                                     filterSpec,
                                     pageNumber,
                                     lookup.getNumberOfRows());
        }
    }

    @Override
    public boolean isDataSetOutdated(DataSetDef def) {
        return false;
    }

    protected DataSet buildDataSet(DataSetDef def,
                                   List<List> instances,
                                   List<DataColumn> extraColumns) throws Exception {
        DataSet dataSet = DataSetFactory.newEmptyDataSet();
        dataSet.setUUID(def.getUUID());
        dataSet.setDefinition(def);

        if (extraColumns != null && !extraColumns.isEmpty()) {
            for (DataColumn extraColumn : extraColumns) {
                dataSet.addColumn(extraColumn);
            }
        } else {
            for (DataColumnDef column : def.getColumns()) {
                DataColumn numRows = new DataColumnImpl(column.getId(),
                                                        column.getColumnType());
                dataSet.addColumn(numRows);
            }
        }

        for (List<Object> row : instances) {

            int columnIndex = 0;
            for (Object value : row) {
                DataColumn intervalBuilder = dataSet.getColumnByIndex(columnIndex);
                ColumnType columnType = intervalBuilder.getColumnType();
                if (columnType.equals(ColumnType.DATE) && isNumberValue(value)) {
                    Number dateNumber = NumberFormat.getInstance().parse(value.toString());
                    Date dateValue = new Date(dateNumber.longValue());
                    intervalBuilder.getValues().add(dateValue);
                } else {
                    intervalBuilder.getValues().add(value);
                }
                columnIndex++;
            }
        }
        // set size of the results to allow paging to be more then the actual size
        //        dataSet.setRowCountNonTrimmed(instances.size() == 0 ? 0 : instances.size() + 1);
        dataSet.setRowCountNonTrimmed(instances.size());
        return dataSet;
    }

    protected void appendIntervalSelection(DataSetGroup intervalSel,
                                           List<QueryParam> filterParams) {
        if (intervalSel != null && intervalSel.isSelect()) {
            ColumnGroup cg = intervalSel.getColumnGroup();
            List<Interval> intervalList = intervalSel.getSelectedIntervalList();

            // Get the filter values
            List<Comparable> names = new ArrayList<Comparable>();
            Comparable min = null;
            Comparable max = null;
            for (Interval interval : intervalList) {
                names.add(interval.getName());
                Comparable intervalMin = (Comparable) interval.getMinValue();
                Comparable intervalMax = (Comparable) interval.getMaxValue();

                if (intervalMin != null) {
                    if (min == null) {
                        min = intervalMin;
                    } else if (min.compareTo(intervalMin) > 0) {
                        min = intervalMin;
                    }
                }
                if (intervalMax != null) {
                    if (max == null) {
                        max = intervalMax;
                    } else if (max.compareTo(intervalMax) > 0) {
                        max = intervalMax;
                    }
                }
            }
            // Min can't be greater than max.
            if (min != null && max != null && min.compareTo(max) > 0) {
                min = max;
            }

            ColumnFilter filter;
            if (min != null && max != null) {
                filter = FilterFactory.between(cg.getSourceId(),
                                               min,
                                               max);
            } else if (min != null) {
                filter = FilterFactory.greaterOrEqualsTo(cg.getSourceId(),
                                                         min);
            } else if (max != null) {
                filter = FilterFactory.lowerOrEqualsTo(cg.getSourceId(),
                                                       max);
            } else {
                filter = FilterFactory.equalsTo(cg.getSourceId(),
                                                names);
            }
            CoreFunctionFilter coreFunctionFilter = (CoreFunctionFilter) filter;
            filterParams.add(new QueryParam(coreFunctionFilter.getColumnId(),
                                            coreFunctionFilter.getType().toString(),
                                            coreFunctionFilter.getParameters()));
        }
    }

    protected void handleDataSetGroup(final DataSetDef def,
                                      final DataSetGroup dataSetGroup,
                                      final List<QueryParam> filterParams,
                                      final List<DataColumn> extraColumns) {
        if (dataSetGroup != null) {
            if (dataSetGroup.getColumnGroup() != null) {
                // handle group
                if (dataSetGroup.getColumnGroup().getIntervalSize() != null) {
                    filterParams.add(new QueryParam(dataSetGroup.getColumnGroup().getSourceId(),
                                                    "group",
                                                    Arrays.asList(dataSetGroup.getColumnGroup().getColumnId(),
                                                                  dataSetGroup.getColumnGroup().getIntervalSize(),
                                                                  dataSetGroup.getColumnGroup().getMaxIntervals())));
                } else {
                    filterParams.add(new QueryParam(dataSetGroup.getColumnGroup().getSourceId(),
                                                    "group",
                                                    Arrays.asList(dataSetGroup.getColumnGroup().getColumnId())));
                }
            }

            // handle additional columns
            for (GroupFunction groupFunction : dataSetGroup.getGroupFunctions()) {
                if (groupFunction.getFunction() != null) {
                    filterParams.add(new QueryParam(groupFunction.getSourceId(),
                                                    groupFunction.getFunction().toString(),
                                                    Arrays.asList(groupFunction.getColumnId())));
                    extraColumns.add(new DataColumnImpl(groupFunction.getSourceId(),
                                                        ColumnType.NUMBER));
                } else {
                    filterParams.add(new QueryParam(groupFunction.getSourceId(),
                                                    null,
                                                    Arrays.asList(groupFunction.getColumnId())));
                    extraColumns.add(new DataColumnImpl(groupFunction.getSourceId(),
                                                        getGroupFunctionColumnType(def,
                                                                                   dataSetGroup.getColumnGroup(),
                                                                                   groupFunction)));
                }
            }
        }
    }

    protected ColumnType getGroupFunctionColumnType(final DataSetDef def,
                                                    final ColumnGroup columnGroup,
                                                    final GroupFunction groupFunction) {
        ColumnType type = def.getColumnById(groupFunction.getColumnId()).getColumnType();
        if (type != ColumnType.DATE || columnGroup == null || groupFunction == null) {
            return type;
        } else {
            return columnGroup.getSourceId().equals(groupFunction.getSourceId()) ? ColumnType.LABEL : type;
        }
    }

    protected void addColumnsToDefinition(DataSetDef def, QueryDefinition queryDef) {
        Map<String, String> columns = queryDef.getColumns();
        if (columns != null) {
            columns.entrySet().stream()
                   .filter(e -> def.getColumnById(e.getKey()) == null)
                   .forEach(e -> def.addColumn(e.getKey(), ColumnType.valueOf(e.getValue())));
        }
    }

    protected boolean isNumberValue(Object value) {
        try {
            NumberFormat.getInstance().parse(value.toString());
            return true;
        } catch (Exception e) {
            LOGGER.debug("Date value is not parseable to number", e);
        }
        return false;
    }
}