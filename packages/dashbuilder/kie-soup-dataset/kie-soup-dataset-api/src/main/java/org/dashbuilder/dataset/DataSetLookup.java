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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.impl.AbstractDataSetOp;
import org.dashbuilder.dataset.sort.DataSetSort;

/**
 * A data set look up request.
 */
public class DataSetLookup {

    /**
     * The UUID of the data set to retrieve.
     */
    protected String dataSetUUID = null;

    /**
     * The starting row offset of the target data set.
     */
    protected int rowOffset = 0;

    /**
     * The number of rows to get.
     */
    protected int numberOfRows = -1;

    /**
     * Flag indicating this lookup request is in test mode
     */
    protected boolean testMode = false;

    /**
     * The list of operations to apply on the target data set as part of the lookup operation.
     */
    protected List<DataSetOp> operationList = new ArrayList<DataSetOp>();
    
    private final Map<String, Object> metadata = new HashMap<>();

    public DataSetLookup() {
    }

    public boolean testMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public boolean isEmpty() {
        if (!operationList.isEmpty()) return false;
        if (numberOfRows > 0) return false;
        if (rowOffset > 0) return false;
        return true;
    }
    public void setDataSetUUID(String dataSetUUID) {
        this.dataSetUUID = dataSetUUID;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public void setRowOffset(int rowOffset) {
        if (rowOffset < 0) throw new IllegalArgumentException("Offset can't be negative: " + rowOffset);
        this.rowOffset = rowOffset;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public DataSetLookup(String dataSetUUID, DataSetOp... ops) {
        this.dataSetUUID = dataSetUUID;
        for (DataSetOp op : ops) {
            operationList.add(op);
        }
    }

    public String getDataSetUUID() {
        return dataSetUUID;
    }

    public <T extends DataSetOp> T getOperation(int index) {
        return (T) operationList.get(index);
    }

    public <T extends DataSetOp> T removeOperation(int index) {
        return (T) operationList.remove(index);
    }

    public int getOperationIdx(DataSetOp op) {
        return operationList.indexOf(op);
    }

    public List<DataSetOp> getOperationList() {
        return operationList;
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    public void setMetadata(String key, Object value){
        metadata.put(key, value);
    }

    public <T extends DataSetOp> List<T> getOperationList(Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (DataSetOp op : operationList) {
            if (op.getClass().equals(type)) {
                result.add((T) op);
            }
        }
        return result;
    }

    public int removeOperations(DataSetOpType type) {
        int removed = 0;
        Iterator<DataSetOp> it = operationList.iterator();
        while (it.hasNext()) {
            DataSetOp op = it.next();
            if (op.getType().equals(type)) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    public DataSetLookup addOperation(int pos, DataSetOp... ops) {
        for (DataSetOp op : ops) {
            operationList.add(pos, op);
            ((AbstractDataSetOp) op).setDataSetUUID(dataSetUUID);
        }
        return this;
    }

    public DataSetLookup addOperation(DataSetOp... ops) {
        for (DataSetOp op : ops) {
            operationList.add(op);
            ((AbstractDataSetOp) op).setDataSetUUID(dataSetUUID);
        }
        return this;
    }

    public DataSetGroup getLastGroupOp() {
        List<DataSetGroup> ops = getOperationList(DataSetGroup.class);
        if (ops.isEmpty()) {
            return null;
        }
        return ops.get(ops.size()-1);
    }

    public DataSetFilter getFirstFilterOp() {
        List<DataSetFilter> ops = getOperationList(DataSetFilter.class);
        if (ops.isEmpty()) {
            return null;
        }
        return ops.get(0);
    }

    public DataSetSort getFirstSortOp() {
        List<DataSetSort> ops = getOperationList(DataSetSort.class);
        if (ops.isEmpty()) {
            return null;
        }
        return ops.get(0);
    }

    public int getFirstGroupOpIndex(int fromIndex, String columnId, Boolean onlySelections) {
        for (int i = fromIndex; i < operationList.size(); i++) {
            DataSetOp op = operationList.get(i);
            if (DataSetOpType.GROUP.equals(op.getType())) {
                DataSetGroup groupOp = (DataSetGroup) op;
                ColumnGroup cg = groupOp.getColumnGroup();

                boolean hasSelections = groupOp.isSelect();
                boolean matchColumn = columnId == null || (cg != null && cg.getColumnId().equals(columnId));
                boolean matchSelections = onlySelections == null || (onlySelections && hasSelections) || (!onlySelections && !hasSelections);

                if (matchColumn && matchSelections) {
                    return i;
                }
            }
        }
        return -1;
    }

    public List<DataSetGroup> getFirstGroupOpSelections() {
        List<DataSetGroup> result = new ArrayList<DataSetGroup>();
        for (DataSetGroup group : getOperationList(DataSetGroup.class)) {
            if (group.isSelect()) {
                result.add(group);
            } else {
                break;
            }
        }
        return result;
    }

    public int getLastGroupOpIndex(int fromIndex, String columnId, boolean onlySelections) {
        int target = -1;
        for (int i = fromIndex; i < operationList.size(); i++) {
            DataSetOp op = operationList.get(i);
            if (DataSetOpType.GROUP.equals(op.getType())) {
                DataSetGroup groupOp = (DataSetGroup) op;

                ColumnGroup cg = groupOp.getColumnGroup();
                if (cg == null) {
                    // Discard column selection ops
                    continue;
                }
                if (columnId != null && !cg.getColumnId().equals(columnId)) {
                    // Discard group ops related to other columns
                    continue;
                }
                if (onlySelections && !groupOp.isSelect()) {
                    // Discard non-selections
                    continue;
                }
                target= i;
            }
        }
        return target;
    }

    public int getLastGroupOpIndex(int fromIndex) {
        int target = -1;
        for (int i = fromIndex; i < operationList.size(); i++) {
            DataSetOp op = operationList.get(i);
            if (DataSetOpType.GROUP.equals(op.getType())) {
                target = i;
            }
        }
        return target;
    }

    public DataSetLookup cloneInstance() {
        DataSetLookup clone = new DataSetLookup();
        clone.setDataSetUUID(dataSetUUID);
        clone.setRowOffset(rowOffset);
        clone.setNumberOfRows(numberOfRows);
        for (DataSetOp dataSetOp : operationList) {
            clone.operationList.add(dataSetOp.cloneInstance());
        }
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        try {
            DataSetLookup other = (DataSetLookup) obj;
            if (other == this) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (dataSetUUID == null || other.dataSetUUID == null) {
                return false;
            }
            if (!dataSetUUID.equals(other.dataSetUUID)) {
                return false;
            }
            if (rowOffset != other.rowOffset) {
                return false;
            }
            if (numberOfRows != other.numberOfRows) {
                return false;
            }
            if (operationList.size() != other.operationList.size()) {
                return false;
            }
            for (int i = 0; i < operationList.size(); i++) {
                DataSetOp op = operationList.get(i);
                DataSetOp otherOp = other.operationList.get(i);
                if (!op.equals(otherOp)) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
