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
package org.dashbuilder.dataset.engine;

import java.util.List;
import java.util.ArrayList;

import org.dashbuilder.dataset.DataSetOpEngine;
import org.dashbuilder.dataset.engine.index.DataSetStaticIndex;
import org.dashbuilder.dataset.group.AggregateFunction;
import org.dashbuilder.dataset.group.AggregateFunctionManager;
import org.dashbuilder.dataset.engine.group.IntervalBuilder;
import org.dashbuilder.dataset.engine.group.IntervalBuilderLocator;
import org.dashbuilder.dataset.engine.group.IntervalList;
import org.dashbuilder.dataset.engine.index.DataSetFilterIndex;
import org.dashbuilder.dataset.engine.index.DataSetGroupIndex;
import org.dashbuilder.dataset.engine.index.DataSetIndex;
import org.dashbuilder.dataset.engine.index.DataSetIndexNode;
import org.dashbuilder.dataset.engine.index.DataSetIntervalIndex;
import org.dashbuilder.dataset.engine.index.DataSetIntervalSetIndex;
import org.dashbuilder.dataset.engine.index.DataSetSortIndex;
import org.dashbuilder.dataset.engine.index.spi.DataSetIndexRegistry;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.engine.filter.DataSetFilterAlgorithm;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.impl.DataColumnImpl;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.engine.sort.DataSetSortAlgorithm;
import org.dashbuilder.dataset.sort.SortedList;

/**
 * Engine implementation that can runs both on client and server.
 */
public class SharedDataSetOpEngine implements DataSetOpEngine {

    protected AggregateFunctionManager aggregateFunctionManager;
    protected IntervalBuilderLocator intervalBuilderLocator;
    protected DataSetIndexRegistry indexRegistry;
    protected DataSetSortAlgorithm sortAlgorithm;
    protected DataSetFilterAlgorithm filterAlgorithm;
    protected Chronometer chronometer;

    public SharedDataSetOpEngine(AggregateFunctionManager aggregateFunctionManager,
                                 IntervalBuilderLocator intervalBuilderLocator,
                                 DataSetIndexRegistry indexRegistry,
                                 DataSetSortAlgorithm sortAlgorithm,
                                 DataSetFilterAlgorithm filterAlgorithm,
                                 Chronometer chronometer) {

        this.aggregateFunctionManager = aggregateFunctionManager;
        this.intervalBuilderLocator = intervalBuilderLocator;
        this.indexRegistry = indexRegistry;
        this.sortAlgorithm = sortAlgorithm;
        this.filterAlgorithm = filterAlgorithm;
        this.chronometer = chronometer;
    }

    public DataSetIndexRegistry getIndexRegistry() {
        return indexRegistry;
    }

    public DataSet execute(DataSet dataSet, List<DataSetOp> opList) {
        DataSetOpListProcessor processor = new DataSetOpListProcessor();
        DataSetStaticIndex index = new DataSetStaticIndex(dataSet);
        processor.setDataSetIndex(index);
        processor.setOperationList(opList);
        processor.run();
        return processor.getDataSet();
    }

    public DataSet execute(String uuid, List<DataSetOp> opList) {
        DataSetOpListProcessor processor = new DataSetOpListProcessor();
        processor.setDataSetIndex(indexRegistry.get(uuid));
        processor.setOperationList(opList);
        processor.run();
        return processor.getDataSet();
    }

    private class DataSetOpListProcessor implements Runnable {

        List<DataSetOp> operationList;
        InternalContext context;

        public void setDataSetIndex(DataSetIndex index) {
            context = new InternalContext(index);
        }

        public void setOperationList(List<DataSetOp> opList) {
            operationList = new ArrayList<DataSetOp>(opList);
        }

        /**
         * Ensure the sequence of operations to apply match the following pattern:
         * <ul>
         * <li>(0..N) Filter</li>
         * <li>(0..N) Group</li>
         * <li>(0..1) Sort</li>
         * </ul>
         * @throws IllegalArgumentException If the operation sequence is invalid.
         */
        protected void checkOpList(List<DataSetOp> opList) {
            StringBuilder out = new StringBuilder();
            for (DataSetOp op : opList) {
                if (DataSetOpType.FILTER.equals(op.getType())) out.append("F");
                if (DataSetOpType.GROUP.equals(op.getType())) out.append("G");
                if (DataSetOpType.SORT.equals(op.getType())) out.append("S");
            }
            String pattern = out.toString();
            if (!pattern.matches("F*G*S?")) {
                throw new IllegalArgumentException("Invalid operation sequence order. Valid = (0..N) FILTER > (0..N) GROUP > (0..1) SORT");
            }
        }

        public DataSet getDataSet() {
            return context.dataSet;
        }

        public void run() {
            if (context == null) {
                throw new IllegalStateException("Data set missing");
            }

            checkOpList(operationList);

            boolean group = false;
            boolean sort = false;

            for (int i=0; i<operationList.size(); i++) {
                DataSetOp op = operationList.get(i);

                if (DataSetOpType.GROUP.equals(op.getType())) {
                    if (sort) throw new IllegalStateException("Sort operations must be applied ALWAYS AFTER GROUP.");

                    DataSetGroup gOp = (DataSetGroup) op;
                    ColumnGroup columnGroup = gOp.getColumnGroup();
                    if (columnGroup == null) {
                        // No real group requested only column selections
                        group = true;
                        context.lastOperation = op;
                    } else {
                        if (group(gOp, context)) {
                            // The group will be required if is not an interval selection
                            group = !context.getLastGroupOp().isSelect();
                            context.lastOperation = op;
                        }
                    }
                }
                else if (DataSetOpType.FILTER.equals(op.getType())) {
                    if (group) throw new IllegalStateException("Filter operations must be applied ALWAYS BEFORE GROUP.");
                    if (sort) throw new IllegalStateException("Sort operations must be applied ALWAYS AFTER FILTER.");

                    filter((DataSetFilter) op, context);
                    context.lastOperation = op;
                }
                else if (DataSetOpType.SORT.equals(op.getType())) {
                    if (sort) throw new IllegalStateException("Sort can only be executed once.");

                    if (group) {
                        buildDataSet(context);
                    }

                    sort = true;
                    sort((DataSetSort) op, context);
                    context.lastOperation = op;
                }
                else {
                    throw new IllegalArgumentException("Unsupported operation: " + op.getClass().getName());
                }
            }

            // Build the resulting data set
            buildDataSet(context);
        }

        // GROUP OPERATION

        protected void checkGroupOp(DataSet dataSet, DataSetGroup op) {
            ColumnGroup cg = op.getColumnGroup();
            if (cg != null) {
                String id = cg.getSourceId();
                if (dataSet.getColumnById(id) == null) {
                    throw new IllegalArgumentException("Group column specified not found in the data set: " + id);
                }
            }
        }

        protected boolean group(DataSetGroup op, InternalContext context) {
            checkGroupOp(context.dataSet, op);

            // Group by the specified column (if any).
            ColumnGroup columnGroup = op.getColumnGroup();

            // No real group requested. Only function calculations on the data set.
            if (columnGroup == null) return true;

            // Nested groups are only supported on the presence of an interval selection or group join operation.
            DataSetGroup lastGroupOp = context.getLastGroupOp();
            if (lastGroupOp != null && !lastGroupOp.isSelect() && !op.isJoin()) {
                return false;
            }

            // Create a root or nested group.
            DataSetGroupIndex groupIndex = null;
            if (context.lastGroupIndex == null) groupIndex = singleGroup(op, context);
            else groupIndex = nestedGroup(op, context.lastGroupIndex, context);

            // Select the group intervals (if any)
            groupIndex = selectIntervals(op, groupIndex);

            // Index the group
            context.index(op, groupIndex);
            return true;
        }

        protected DataSetGroupIndex singleGroup(DataSetGroup op, InternalContext context) {

            ColumnGroup columnGroup = op.getColumnGroup();
            DataColumn sourceColumn = context.dataSet.getColumnById(columnGroup.getSourceId());
            ColumnType columnType = sourceColumn.getColumnType();
            GroupStrategy groupStrategy = columnGroup.getStrategy();
            IntervalBuilder intervalBuilder = intervalBuilderLocator.lookup(columnType, groupStrategy);
            if (intervalBuilder == null) throw new RuntimeException("Interval generator not supported.");

            // No index => Build required
            if (context.index == null) {
                IntervalList intervalList = intervalBuilder.build(new InternalHandler(context), columnGroup);
                return new DataSetGroupIndex(columnGroup, intervalList);
            }
            // Index match => Reuse it
            DataSetGroupIndex groupIndex = context.index.getGroupIndex(columnGroup);
            if (groupIndex != null) {
                return groupIndex;
            }
            // No index match => Build required
            chronometer.start();
            IntervalList intervalList = intervalBuilder.build(new InternalHandler(context), columnGroup);
            chronometer.stop();

            // Index before return.
            DataSetGroupIndex index = new DataSetGroupIndex(columnGroup, intervalList);
            index.setBuildTime(chronometer.elapsedTime());
            return context.index.indexGroup(index);
        }

        protected DataSetGroupIndex nestedGroup(DataSetGroup op, DataSetGroupIndex lastGroupIndex, InternalContext context) {

            // Index match => Reuse it
            DataSetGroupIndex nestedGroupIndex = lastGroupIndex.getGroupIndex(op.getColumnGroup());
            if (nestedGroupIndex != null) return nestedGroupIndex;

            // No index match => Create a brand new group index
            nestedGroupIndex = new DataSetGroupIndex(op.getColumnGroup());

            // Apply the nested group operation on each parent group interval.
            InternalContext nestedContext = new InternalContext(context.dataSet, null);
            List<DataSetIntervalIndex> intervalsIdxs = lastGroupIndex.getIntervalIndexes();
            for (DataSetIntervalIndex intervalIndex : intervalsIdxs) {

                // In a nested group the intervals can aggregate other intervals.
                if (intervalIndex instanceof DataSetIntervalSetIndex) {
                    DataSetIntervalSetIndex indexSet = (DataSetIntervalSetIndex) intervalIndex;
                    for (DataSetIntervalIndex subIndex : indexSet.getIntervalIndexes()) {
                        nestedContext.index = subIndex;
                        DataSetGroupIndex sg = singleGroup(op, nestedContext);
                        nestedGroupIndex.indexIntervals(sg.getIntervalIndexes());
                    }
                }
                // Or can just be single intervals.
                else {
                    nestedContext.index = intervalIndex;
                    DataSetGroupIndex sg = singleGroup(op, nestedContext);
                    nestedGroupIndex.indexIntervals(sg.getIntervalIndexes());
                }
            }
            context.index.indexGroup(nestedGroupIndex);
            return nestedGroupIndex;
        }


        protected DataSetGroupIndex selectIntervals(DataSetGroup groupOp, DataSetGroupIndex groupIndex) {
            List<Interval> intervalList = groupOp.getSelectedIntervalList();
            if (intervalList != null && !intervalList.isEmpty()) {

                // Look for an existing selection index.
                DataSetGroupIndex selectionIndex = groupIndex.getSelectionIndex(intervalList);
                if (selectionIndex != null) return selectionIndex;

                // Create a brand new selection index.
                List<DataSetIntervalIndex> intervalIdxs = groupIndex.getIntervalIndexes(intervalList);
                if (intervalIdxs.isEmpty()) {
                    intervalIdxs = new ArrayList<DataSetIntervalIndex>();
                    for (Interval interval : intervalList) {
                        intervalIdxs.add(new DataSetIntervalIndex(groupIndex, interval));
                    }
                }

                //if (intervalIdxs.size() == 1) return intervalIdxs.get(0);
                return groupIndex.indexSelection(intervalList, intervalIdxs);
            }
            return groupIndex;
        }

        // FILTER OPERATION

        protected void checkFilterOp(DataSet dataSet, DataSetFilter op) {
            for (ColumnFilter columnFilter : op.getColumnFilterList()) {
                String id = columnFilter.getColumnId();
                if (id != null && dataSet.getColumnById(id) == null) {
                    throw new IllegalArgumentException("Filter column specified not found in the data set: " + id);
                }
            }
        }

        protected void filter(DataSetFilter op, InternalContext context) {
            checkFilterOp(context.dataSet, op);

            if (context.dataSet.getRowCount() == 0) {
                return;
            }

            // Process the filter requests.
            for (ColumnFilter filter : op.getColumnFilterList()) {

                // No index => Filter required
                if (context.index == null) {
                    List<Integer> rows = filterAlgorithm.filter(new InternalHandler(context), filter);
                    context.index(op, new DataSetFilterIndex(filter, rows));
                    continue;
                }
                // Index match => Reuse it
                DataSetFilterIndex index = context.index.getFilterIndex(filter);
                if (index != null) {
                    context.index(op, index);
                    continue;
                }
                // No index match => Filter required
                chronometer.start();
                List<Integer> rows = filterAlgorithm.filter(new InternalHandler(context), filter);
                chronometer.stop();

                // Index before continue.
                context.index(op, context.index.indexFilter(filter, rows, chronometer.elapsedTime()));
            }
        }

        // SORT OPERATION

        protected void checkSortOp(DataSet dataSet, DataSetSort op) {
            for (ColumnSort columnSort : op.getColumnSortList()) {
                String id = columnSort.getColumnId();
                if (dataSet.getColumnById(id) == null) {
                    throw new IllegalArgumentException("Sort column not found in the data set: " + id);
                }
            }
        }

        protected void sort(DataSetSort op, InternalContext context) {
            checkSortOp(context.dataSet, op);

            // No index => Sort required
            if (context.index == null) {
                List<Integer> orderedRows = sortAlgorithm.sort(context.getDataSet(), context.getRows(), op.getColumnSortList());
                context.index(op, new DataSetSortIndex(op, orderedRows));
                return;

            }
            // Index match => Reuse it
            DataSetSortIndex sortIndex = context.index.getSortIndex(op);
            if (sortIndex != null) {
                context.index(op, sortIndex);
                return;
            }
            // No index match => Sort required
            chronometer.start();
            List<Integer> orderedRows = sortAlgorithm.sort(context.getDataSet(), context.getRows(), op.getColumnSortList());
            chronometer.stop();

            // Index before return.
            context.index(op, context.index.indexSort(op, orderedRows, chronometer.elapsedTime()));
        }

        // DATASET BUILD

        // ColumnGroup==null => columns selection OR agg calculations
        // GroupFunction.function can be null => select first column value available
        // GroupFunction.sourceId => (COUNT functions) select the first column available

        // GroupFunction.function can be null

        public DataSet buildDataSet(InternalContext context) {
            if (context.index == null) {
                // If no index exists then just return the data set from context
                return context.dataSet;
            }
            DataSet result = _buildDataSet(context);
            context.dataSet = result;
            context.index = null;
            return result;
        }

        private DataSet _buildDataSet(InternalContext context) {
            DataSetOp lastOp = context.lastOperation;
            DataSetIndexNode index = context.index;
            DataSet dataSet = context.dataSet;

            if (lastOp instanceof DataSetGroup) {
                DataSetGroup gOp = (DataSetGroup) lastOp;
                ColumnGroup columnGroup = gOp.getColumnGroup();
                if (columnGroup == null) {
                    boolean hasAggregations = !gOp.getAggregationFunctions().isEmpty();
                    return _buildDataSet(context, gOp.getGroupFunctions(), hasAggregations);
                } else {
                    if (gOp.isSelect() && gOp.getGroupFunctions().isEmpty()) {
                        return dataSet.trim(index.getRows());
                    } else {
                        return _buildDataSet(context, gOp);
                    }
                }
            }
            if (lastOp instanceof DataSetFilter) {
                return dataSet.trim(index.getRows());
            }
            if (lastOp instanceof DataSetSort) {
                return _filterDataSet(dataSet, index.getRows());
            }
            return dataSet;
        }

        private DataSet _filterDataSet(DataSet dataSet, List<Integer> rows) {
            DataSet result = DataSetFactory.newEmptyDataSet();
            for (DataColumn column : dataSet.getColumns()) {
                DataColumn sortedColumn = column.cloneEmpty();
                SortedList sortedValues = new SortedList(column.getValues(), rows);
                sortedColumn.setValues(sortedValues);
                result.addColumn(sortedColumn);
            }
            return result;
        }

        private DataSet _buildDataSet(InternalContext context, DataSetGroup op) {
            DataSetGroupIndex index = context.lastGroupIndex;
            DataSet dataSet = context.dataSet;

            ColumnGroup columnGroup = op.getColumnGroup();
            List<GroupFunction> groupFunctions = op.getGroupFunctions();

            // Data set header.
            DataSet result = DataSetFactory.newEmptyDataSet();
            for (GroupFunction gf : op.getGroupFunctions()) {

                String sourceId = gf.getSourceId();
                String columnId = gf.getColumnId() == null ? sourceId : gf.getColumnId();

                // Group columns
                AggregateFunctionType columnFunction = gf.getFunction();
                if (sourceId != null && sourceId.equals(columnGroup.getSourceId()) && columnFunction == null) {
                    DataColumnImpl column = new DataColumnImpl(columnId, ColumnType.LABEL);
                    column.setColumnGroup(columnGroup);
                    column.setIntervalType(index.getIntervalType());
                    column.setMinValue(index.getMinValue());
                    column.setMaxValue(index.getMaxValue());
                    column.setGroupFunction(gf);
                    result.addColumn(column);
                } else {
                    // Columns based on aggregation functions
                    AggregateFunctionType aggF = gf.getFunction();
                    if (aggF != null) {
                        DataColumnImpl column = new DataColumnImpl(columnId, ColumnType.NUMBER);
                        column.setGroupFunction(gf);
                        result.addColumn(column);
                    }
                    // Column values selection
                    else {
                        DataColumn targetColumn = dataSet.getColumnById(sourceId);
                        if (targetColumn == null) throw new IllegalArgumentException("Column not found: " + columnId);

                        DataColumnImpl column = new DataColumnImpl(columnId, targetColumn.getColumnType());
                        column.setGroupFunction(gf);
                        result.addColumn(column);
                    }
                }
            }
            // Add the aggregate calculations to the result.
            List<DataSetIntervalIndex> intervalIdxs = index.getIntervalIndexes();
            int row = 0;
            for (int i=0; i<intervalIdxs.size(); i++) {
                DataSetIntervalIndex intervalIdx = intervalIdxs.get(i);

                // Include/discard empty intervals
                if (intervalIdx.getRows().isEmpty() && !columnGroup.areEmptyIntervalsAllowed()) {
                    continue;
                }

                // Add the aggregate calculations.
                for (int j=0; j< groupFunctions.size(); j++) {
                    GroupFunction groupFunction = groupFunctions.get(j);
                    String sourceId = groupFunction.getSourceId();
                    AggregateFunctionType columnFunction = groupFunction.getFunction();

                    if (sourceId != null && sourceId.equals(columnGroup.getSourceId()) && columnFunction == null) {
                        result.setValueAt(row, j, intervalIdx.getName());
                    } else {
                        DataColumn dataColumn = dataSet.getColumnByIndex(0);
                        if (sourceId != null) dataColumn = dataSet.getColumnById(sourceId);

                        // Columns based on aggregation functions
                        if (columnFunction != null) {
                            Object aggValue = _calculateFunction(dataColumn, groupFunction.getFunction(), intervalIdx);
                            result.setValueAt(row, j, aggValue);
                        }
                        // Pick up the first column value for the interval
                        else {
                            List<Integer> rows = intervalIdx.getRows();
                            if (rows == null || rows.isEmpty()) {
                                result.setValueAt(row, j, null);
                            } else {
                                int intervalRow = rows.get(0);
                                Object firstValue = dataColumn.getValues().get(intervalRow);
                                result.setValueAt(row, j, firstValue);
                            }
                        }
                    }
                }
                row++;
            }
            return result;
        }

        private DataSet _buildDataSet(InternalContext context, List<GroupFunction> groupFunctions, boolean hasAggregations) {
            DataSetIndexNode index = context.index;
            DataSet dataSet = context.dataSet;
            DataSet result = DataSetFactory.newEmptyDataSet();

            if (hasAggregations) {
                for (int i=0; i< groupFunctions.size(); i++) {
                    GroupFunction gf = groupFunctions.get(i);
                    String sourceId = gf.getSourceId();
                    String columnId = gf.getColumnId() == null ? sourceId : gf.getColumnId();
                    DataColumn sourceColumn = context.dataSet.getColumnById(sourceId);
                    ColumnType sourceColumnType = sourceColumn != null ? sourceColumn.getColumnType() : null;
                    ColumnType aggColumnType = gf.getFunction().getResultType(sourceColumnType);

                    DataColumnImpl column = new DataColumnImpl(columnId, aggColumnType);
                    column.setGroupFunction(gf);
                    result.addColumn(column);

                    DataColumn dataColumn = dataSet.getColumnById(sourceId);
                    if (dataColumn == null) dataColumn = dataSet.getColumnByIndex(0);

                    Object aggValue = _calculateFunction(dataColumn, gf.getFunction(), index);
                    result.setValueAt(0, i, aggValue);
                }
            } else {
                DataSet _temp = dataSet.trim(index.getRows());
                for (int i=0; i< groupFunctions.size(); i++) {
                    GroupFunction gf = groupFunctions.get(i);
                    String sourceId = gf.getSourceId();
                    String columnId = gf.getColumnId() == null ? sourceId : gf.getColumnId();

                    DataColumn targetColumn = _temp.getColumnById(sourceId);
                    DataColumnImpl column = new DataColumnImpl(columnId, targetColumn.getColumnType());
                    column.setGroupFunction(gf);
                    column.setValues(targetColumn.getValues());
                    result.addColumn(column);
                }
            }
            return result;
        }

        private Object _calculateFunction(DataColumn column, AggregateFunctionType type, DataSetIndexNode index) {
            // Preconditions
            if (type == null) {
                throw new IllegalArgumentException("No aggregation function specified for the column: " + column.getId());
            }
            // Look into the index first
            if (index != null) {
                Object sv = index.getAggValue(column.getId(), type);
                if (sv != null) {
                    return sv;
                }
            }
            // Do the aggregate calculations.
            chronometer.start();
            AggregateFunction function = aggregateFunctionManager.getFunctionByType(type);
            Object aggValue = function.aggregate(column.getValues(), index.getRows());
            chronometer.stop();

            // Index the result
            if (index != null) {
                index.indexAggValue(column.getId(), type, aggValue, chronometer.elapsedTime());
            }
            return aggValue;
        }

        class InternalContext implements DataSetRowSet {

            DataSet dataSet = null;
            DataSetIndexNode index = null;
            DataSetOp lastOperation = null;

            List<DataSetGroup> groupOpList = new ArrayList<DataSetGroup>();
            DataSetGroupIndex lastGroupIndex = null;
            DataSetFilter lastFilterOp = null;
            DataSetFilterIndex lastFilterIndex = null;
            DataSetSort lastSortOp = null;
            DataSetSortIndex lastSortIndex = null;

            InternalContext(DataSetIndex index) {
                this(index.getDataSet(), index);
            }

            InternalContext(DataSet dataSet, DataSetIndexNode index) {
                this.dataSet = dataSet;
                this.index = index;
            }

            public DataSet getDataSet() {
                return dataSet;
            }

            public List<Integer> getRows() {
                if (index == null) return null;
                return index.getRows();
            }

            public void index(DataSetGroup op, DataSetGroupIndex gi) {
                index = lastGroupIndex = gi;
                lastOperation = op;
                groupOpList.add(op);
            }

            public void index(DataSetFilter op, DataSetFilterIndex i) {
                index = lastFilterIndex = i;
                lastOperation = lastFilterOp = op;
            }

            public void index(DataSetSort op, DataSetSortIndex i) {
                index = lastSortIndex = i;
                lastOperation = lastSortOp = op;
            }

            public DataSetGroup getLastGroupOp() {
                if (groupOpList.isEmpty()) return null;
                return groupOpList.get(groupOpList.size()-1);
            }
        }

        class InternalHandler extends InternalContext implements DataSetHandler {

            InternalHandler(InternalContext context) {
                super(context.dataSet, context.index);
            }
            public DataSetHandler group(DataSetGroup op) {
                DataSetOpListProcessor.this.group(op, this);
                return this;
            }
            public DataSetHandler filter(DataSetFilter op) {
                DataSetOpListProcessor.this.filter(op, this);
                return this;
            }
            public DataSetHandler sort(DataSetSort op) {
                DataSetOpListProcessor.this.sort(op, this);
                return this;
            }
        }
    }
}