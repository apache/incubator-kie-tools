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
package org.dashbuilder.dataset.engine.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.impl.MemSizeEstimator;
import org.dashbuilder.dataset.engine.index.visitor.DataSetIndexVisitor;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.sort.DataSetSort;

/**
 * A DataSet index node
 */
public abstract class DataSetIndexNode extends DataSetIndexElement {

    DataSetIndexNode parent = null;
    List<Integer> rows = null;
    List<DataSetGroupIndex> groupIndexes = null;
    List<DataSetSortIndex> sortIndexes = null;
    List<DataSetFilterIndex> filterIndexes = null;
    Map<String, Map<AggregateFunctionType, DataSetFunctionIndex>> functionIndexes = null;

    public DataSetIndexNode() {
        this(null, null, 0);
    }

    public DataSetIndexNode(DataSetIndexNode parent, List<Integer> rows, long buildTime) {
        super(buildTime);
        this.parent = parent;
        this.rows = rows;
    }

    public DataSetIndexNode getParent() {
        return parent;
    }

    public void setParent(DataSetIndexNode parent) {
        this.parent = parent;
    }

    public List<Integer> getRows() {
        return rows;
    }

    public long getEstimatedSize() {
        long result = super.getEstimatedSize();
        if (rows != null) {
            result += rows.size() * MemSizeEstimator.sizeOfInteger;
        }
        return result;
    }

    public void acceptVisitor(DataSetIndexVisitor visitor) {
        super.acceptVisitor(visitor);

        if (groupIndexes != null) {
            for (DataSetGroupIndex index : groupIndexes) {
                index.acceptVisitor(visitor);
            }
        }
        if (filterIndexes != null) {
            for (DataSetFilterIndex index : filterIndexes) {
                index.acceptVisitor(visitor);
            }
        }
        if (sortIndexes != null) {
            for (DataSetSortIndex index : sortIndexes) {
                index.acceptVisitor(visitor);
            }
        }
        if (functionIndexes != null) {
            for (Map<AggregateFunctionType, DataSetFunctionIndex> indexMap : functionIndexes.values()) {
                for (DataSetFunctionIndex index : indexMap.values()) {
                    index.acceptVisitor(visitor);
                }
            }
        }
    }

    // Aggregate function indexes

    public DataSetFunctionIndex indexAggValue(String columnId, AggregateFunctionType type, Object value, long buildTime) {
        if (functionIndexes == null) {
            functionIndexes = new HashMap<>();
        }

        Map<AggregateFunctionType,DataSetFunctionIndex> columnAggFunctions = functionIndexes.get(columnId);
        if (columnAggFunctions == null) {
            functionIndexes.put(columnId, columnAggFunctions = new EnumMap<>(AggregateFunctionType.class));
        }

        DataSetFunctionIndex index = new DataSetFunctionIndex(value, buildTime);
        columnAggFunctions.put(type, index);
        return index;
    }

    public Object getAggValue(String columnId, AggregateFunctionType type) {
        if (functionIndexes == null) return null;

        Map<AggregateFunctionType,DataSetFunctionIndex> columnAggFunctions = functionIndexes.get(columnId);
        if (columnAggFunctions == null) return null;

        DataSetFunctionIndex functionIndex = columnAggFunctions.get(type);
        if (functionIndex == null) return null;

        functionIndex.reuseHit();
        return functionIndex.getValue();
    }

    // TODO: coordinate concurrent index modifications

    // Group indexes

    public DataSetGroupIndex indexGroup(DataSetGroupIndex index) {
        if (groupIndexes == null) groupIndexes = new ArrayList<DataSetGroupIndex>();
        index.setParent(this);
        index.setBuildTime(buildTime);
        groupIndexes.add(index);
        return index;
    }

    public DataSetGroupIndex getGroupIndex(ColumnGroup gc) {
        if (groupIndexes == null) return null;

        String key = getGroupKey(gc);
        for (DataSetGroupIndex groupIndex : groupIndexes) {
            ColumnGroup c = groupIndex.columnGroup;
            if (key.equals(getGroupKey(c))) {
                groupIndex.reuseHit();
                return groupIndex;
            }
        }
        return null;
    }

    public String getGroupKey(ColumnGroup columnGroup) {
        return columnGroup.getSourceId()
                + "_" + columnGroup.getStrategy().toString()
                + "_" + columnGroup.getIntervalSize()
                + "_" + columnGroup.getMaxIntervals()
                + "_" + columnGroup.areEmptyIntervalsAllowed()
                + "_" + columnGroup.isAscendingOrder()
                + (columnGroup.getFirstMonthOfYear() != null ? "_" + columnGroup.getFirstMonthOfYear() : "")
                + (columnGroup.getFirstDayOfWeek() != null ? "_" + columnGroup.getFirstDayOfWeek() : "");
    }

    // Filter indexes

    public DataSetFilterIndex indexFilter(ColumnFilter filter, List<Integer> rows, long buildTime) {
        if (filterIndexes == null) filterIndexes = new ArrayList<DataSetFilterIndex>();

        DataSetFilterIndex index = new DataSetFilterIndex(filter, rows);
        index.setParent(this);
        index.setBuildTime(buildTime);
        filterIndexes.add(index);
        return index;
    }

    public DataSetFilterIndex getFilterIndex(ColumnFilter filter) {
        if (filterIndexes == null) return null;

        for (DataSetFilterIndex index: filterIndexes) {
            if (filter.equals(index.getColumnFilter())) {
                index.reuseHit();
                return index;
            }
        }
        return null;
    }

    // Sort indexes

    public DataSetSortIndex indexSort(DataSetSort sortOp, List<Integer> sortedRows, long buildTime) {
        if (sortIndexes == null) sortIndexes = new ArrayList<DataSetSortIndex>();

        DataSetSortIndex index = new DataSetSortIndex(sortOp, sortedRows);
        index.setParent(this);
        index.setBuildTime(buildTime);
        sortIndexes.add(index);

        // Also create an index for the inverted sort.
        DataSetSort invertedSortOp = sortOp.cloneInstance().invertOrder();
        List<Integer> invertedRows = new ArrayList<Integer>(sortedRows);
        Collections.reverse(invertedRows);
        DataSetSortIndex invertedIndex = new DataSetSortIndex(invertedSortOp, invertedRows);
        invertedIndex.setParent(this);
        sortIndexes.add(invertedIndex);

        return index;
    }

    public DataSetSortIndex getSortIndex(DataSetSort sortOp) {
        if (sortIndexes == null) return null;

        for (DataSetSortIndex sortIndex : sortIndexes) {
            if (sortOp.equals(sortIndex.getSortOp())) {
                sortIndex.reuseHit();
                return sortIndex;
            }
        }
        return null;
    }
}

