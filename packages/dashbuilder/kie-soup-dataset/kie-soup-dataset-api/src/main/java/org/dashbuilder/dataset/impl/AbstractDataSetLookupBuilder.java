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
package org.dashbuilder.dataset.impl;

import java.util.List;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupBuilder;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.GroupStrategy;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;

public abstract class AbstractDataSetLookupBuilder<T> implements DataSetLookupBuilder<T> {

    private static final String SYMBOL_UNDERSCORE = "_";
    private DataSetLookup dataSetLookup = new DataSetLookup();

    protected DataSetOp getCurrentOp() {
        List<DataSetOp> dataSetOps = dataSetLookup.getOperationList();
        if (dataSetOps.isEmpty()) return null;
        return dataSetOps.get(dataSetOps.size() - 1);
    }

    public T dataset(String uuid) {
        dataSetLookup.setDataSetUUID(uuid);
        return (T) this;
    }

    public T rowOffset(int offset) {
        dataSetLookup.setRowOffset(offset);
        return (T) this;
    }

    public T rowNumber(int rows) {
        dataSetLookup.setNumberOfRows(rows);
        return (T) this;
    }

    public T group(String columnId) {
        return group(columnId, columnId);
    }

    public T group(String columnId, String newColumnId) {
        return group(columnId, newColumnId, true);
    }

    public T group(String columnId, String newColumnId, boolean postEnabled) {
        DataSetGroup gOp = new DataSetGroup();
        ColumnGroup cg = new ColumnGroup(columnId, newColumnId);
        cg.setPostEnabled(postEnabled);
        gOp.setColumnGroup(cg);
        dataSetLookup.addOperation(gOp);
        return (T) this;
    }

    public T join() {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        if (gOp == null || gOp.getColumnGroup() == null) {
            throw new RuntimeException("group() must be called first.");
        }

        gOp.setJoin(true);
        return (T) this;
    }

    public T asc() {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        if (gOp == null || gOp.getColumnGroup() == null) {
            throw new RuntimeException("group() must be called first.");
        }

        ColumnGroup columnGroup = gOp.getColumnGroup();
        columnGroup.setAscendingOrder(true);
        return (T) this;
    }

    public T desc() {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        if (gOp == null || gOp.getColumnGroup() == null) {
            throw new RuntimeException("group() must be called first.");
        }

        ColumnGroup columnGroup = gOp.getColumnGroup();
        columnGroup.setAscendingOrder(false);
        return (T) this;
    }

    public T dynamic(int maxIntervals, boolean emptyAllowed) {
        return groupStrategy(GroupStrategy.DYNAMIC, maxIntervals, null, emptyAllowed);
    }

    public T dynamic(int maxIntervals, DateIntervalType intervalSize, boolean emptyAllowed) {
        return groupStrategy(GroupStrategy.DYNAMIC, maxIntervals, intervalSize.toString(), emptyAllowed);
    }

    public T dynamic(DateIntervalType intervalSize, boolean emptyAllowed) {
        return groupStrategy(GroupStrategy.DYNAMIC, -1, intervalSize.toString(), emptyAllowed);
    }

    public T fixed(DateIntervalType intervalSize, boolean emptyAllowed) {
        if (!DateIntervalType.FIXED_INTERVALS_SUPPORTED.contains(intervalSize)) {
            throw new IllegalArgumentException("Fixed group size '" + intervalSize + "' not supported.");
        }
        return groupStrategy(GroupStrategy.FIXED, -1, intervalSize.toString(), emptyAllowed);
    }

    public T firstDay(DayOfWeek dayOfWeek) {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        if (gOp == null || gOp.getColumnGroup() == null) {
            throw new RuntimeException("group() must be called first.");
        }

        ColumnGroup columnGroup = gOp.getColumnGroup();
        if (!GroupStrategy.FIXED.equals(columnGroup.getStrategy())) {
            throw new RuntimeException("A fixed group is required.");
        }
        if (!DateIntervalType.DAY_OF_WEEK.equals(DateIntervalType.getByName(columnGroup.getIntervalSize()))) {
            throw new RuntimeException("A fixed DAY_OF_WEEK date group is required.");
        }
        columnGroup.setFirstDayOfWeek(dayOfWeek);
        return (T) this;
    }

    public T firstMonth(Month month) {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        if (gOp == null || gOp.getColumnGroup() == null) {
            throw new RuntimeException("group() must be called first.");
        }

        ColumnGroup columnGroup = gOp.getColumnGroup();
        if (!GroupStrategy.FIXED.equals(columnGroup.getStrategy())) {
            throw new RuntimeException("A fixed group is required.");
        }
        if (!DateIntervalType.MONTH.equals(DateIntervalType.getByName(columnGroup.getIntervalSize()))) {
            throw new RuntimeException("A fixed MONTH date group is required.");
        }
        columnGroup.setFirstMonthOfYear(month);
        return (T) this;
    }

    private T groupStrategy(GroupStrategy strategy, int maxIntervals, String intervalSize, boolean emptyAllowed) {
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        if (gOp == null || gOp.getColumnGroup() == null) {
            throw new RuntimeException("group() must be called first.");
        }
        ColumnGroup cg = gOp.getColumnGroup();
        cg.setStrategy(strategy);
        cg.setMaxIntervals(maxIntervals);
        cg.setIntervalSize(intervalSize);
        cg.setEmptyIntervalsAllowed(emptyAllowed);
        return (T) this;
    }

    public T select(String... intervalNames) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetGroup)) {
            dataSetLookup.addOperation(new DataSetGroup());
        }
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        gOp.addSelectedIntervalNames(intervalNames);
        return (T) this;
    }

    public T filter(ColumnFilter... filters) {
        return filter(null, filters);
    }

    public T filter(String columnId, ColumnFilter... filters) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetFilter)) {
            dataSetLookup.addOperation(new DataSetFilter());
        }
        DataSetFilter fOp = (DataSetFilter) getCurrentOp();
        for (ColumnFilter filter : filters) {
            if (columnId != null) filter.setColumnId(columnId);
            fOp.addFilterColumn(filter);
        }
        return (T) this;
    }

    public T sort(String columnId, String order) {
        return sort(columnId, SortOrder.getByName(order));
    }

    public T sort(String columnId, SortOrder order) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetSort)) {
            dataSetLookup.addOperation(new DataSetSort());
        }
        DataSetSort sOp = (DataSetSort) getCurrentOp();
        sOp.addSortColumn(new ColumnSort(columnId, order));
        return (T) this;
    }

    public T column(String columnId) {
        return this.column(columnId, null, columnId);
    }

    public T column(String columnId, String newColumnId) {
        return this.column(columnId, null, newColumnId);
    }

    public T column(String columnId, AggregateFunctionType function) {
        String newColumnId = buildColumnId(columnId, function);
        return this.column(columnId, function, newColumnId);
    }

    public T column(AggregateFunctionType function, String newColumnId) {
        return this.column(null, function, newColumnId);
    }

    public T column(String columnId, AggregateFunctionType function, String newColumnId) {
        DataSetOp op = getCurrentOp();
        if (op == null || !(op instanceof DataSetGroup) || ((DataSetGroup) op).isSelect()) {
            dataSetLookup.addOperation(new DataSetGroup());
        }
        DataSetGroup gOp = (DataSetGroup) getCurrentOp();
        gOp.addGroupFunction(new GroupFunction(columnId, newColumnId, function));
        return (T) this;
    }

    public DataSetLookup buildLookup() {
        return dataSetLookup;
    }

    /**
     * <p>Builds a column identifier when applying an aggregate function to the column, but no id is specified.</p>
     * <p>It follows the nomenclature: <code>sourceId_function</code>.</p>
     *
     * @param sourceId The source column identifier.
     * @param function The aggregate function.
     * @return A new column identifier.
     */
    protected String buildColumnId(String sourceId, AggregateFunctionType function) {
        if (sourceId == null || sourceId.trim().length() == 0) {
            return function.name().toLowerCase();
        } else {
            return sourceId + SYMBOL_UNDERSCORE + function.name().toLowerCase();
        }
    }
}
