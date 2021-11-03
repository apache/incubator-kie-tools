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
package org.dashbuilder.displayer.client;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.*;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetExportReadyCallback;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.*;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.client.export.ExportCallback;
import org.dashbuilder.displayer.client.export.ExportFormat;
import org.uberfire.backend.vfs.Path;

import java.util.*;

public class DataSetHandlerImpl implements DataSetHandler {

    protected DataSetClientServices clientServices;
    protected DataSetLookup lookupBase;
    protected DataSetLookup lookupCurrent;
    protected DataSet lastLookedUpDataSet;

    public DataSetHandlerImpl(DataSetClientServices clientServices, DataSetLookup lookup) {
        this.clientServices = clientServices;
        this.lookupBase = lookup;
        this.lookupCurrent = lookup.cloneInstance();
    }

    @Override
    public DataSet getLastDataSet() {
        return lastLookedUpDataSet;
    }

    @Override
    public DataSetLookup getCurrentDataSetLookup() {
        return lookupCurrent;
    }

    @Override
    public void resetAllOperations() {
        this.lookupCurrent = lookupBase.cloneInstance();
    }

    @Override
    public void limitDataSetRows(int offset, int rows) {
        int offsetBase = lookupBase.getRowOffset();
        int rowsBase = lookupBase.getNumberOfRows();
        lookupCurrent.setRowOffset(offsetBase + offset);

        // base 0 to all, 0 to 20  => offset=0, rows=20
        // base 0 to 1,   0 to 20  => offset=0, rows=1
        // base 50 to 51, 0 to 20  => offset=50, rows=20
        // base 10 to 31, 20 to 10 => offset=30, rows=10
        // base 10 to 31, 0 to 50  => offset=10, rows=31

        if (rowsBase < 1 || rowsBase > rows) {
            lookupCurrent.setNumberOfRows(rows);
        }
    }

    @Override
    public DataSetGroup getGroupOperation(String columnId) {
        String sourceId = _getSourceColumnId(columnId);
        int index = lookupCurrent.getLastGroupOpIndex(0, sourceId, false);
        if (index != -1) {
            return (DataSetGroup) lookupCurrent.getOperation(index).cloneInstance();
        }

        DataSetGroup result = new DataSetGroup();
        result.setColumnGroup(new ColumnGroup(sourceId, sourceId, GroupStrategy.DYNAMIC));
        return result;
    }

    @Override
    public boolean filter(DataSetGroup op) {
        ColumnGroup cg = op.getColumnGroup();
        if (cg == null) {
            throw new RuntimeException("Group ops require a pivot column to be specified.");
        }
        if (!op.isSelect()) {
            throw new RuntimeException("Group intervals not specified.");
        }

        // Avoid duplicates
        for (DataSetGroup next : lookupCurrent.getOperationList(DataSetGroup.class)) {
            if (op.equals(next)) {
                return false;
            }
        }
        // The interval selection op. must be added right before the first existing group op.
        DataSetGroup clone = op.cloneInstance();
        //clone.getGroupFunctions().clear();
        int idx = lookupCurrent.getFirstGroupOpIndex(0, null, null);
        _filter(idx < 0 ? 0 : idx, clone, false);
        return true;
    }

    @Override
    public boolean filter(DataSetFilter op) {
        if (op == null) {
            return false;
        }
        // Avoid duplicates
        for (DataSetFilter next : lookupCurrent.getOperationList(DataSetFilter.class)) {
            if (op.equals(next)) {
                return false;
            }
        }
        lookupCurrent.addOperation(0, op);
        return true;
    }

    @Override
    public boolean drillDown(DataSetGroup op) {
        ColumnGroup cg = op.getColumnGroup();
        if (cg == null) {
            throw new RuntimeException("Group ops require a pivot column to be specified.");
        }
        if (!op.isSelect()) {
            throw new RuntimeException("Group intervals not specified.");
        }
        // Avoid duplicates
        for (DataSetGroup next : lookupCurrent.getOperationList(DataSetGroup.class)) {
            if (op.equals(next)) {
                return false;
            }
        }
        // Get the latest group op. for the target column being selected.
        int lastSelection = lookupCurrent.getLastGroupOpIndex(0, null, true) + 1;
        int targetGroup = lookupCurrent.getLastGroupOpIndex(lastSelection, cg.getColumnId(), false);

        // If the selection does not exists just add it.
        if (targetGroup == -1) {
            DataSetGroup clone = op.cloneInstance();
            //clone.getGroupFunctions().clear();
            _filter(lastSelection, clone, true);
            return true;
        }
        // If there not exists a group op after the target then the target op must be propagated along the selection.
        DataSetGroup targetOp = lookupCurrent.getOperation(targetGroup);
        int latestGroup = lookupCurrent.getLastGroupOpIndex(targetGroup + 1, null, false);
        if (latestGroup == -1) {
            DataSetGroup clone = targetOp.cloneInstance();
            _filter(targetGroup + 1, clone, true);
        }
        // Enable the selection
        _select(targetOp, op.getSelectedIntervalList());
        return true;
    }

    @Override
    public boolean unfilter(DataSetGroup op) {
        return _unfilter(op, false);
    }

    @Override
    public boolean unfilter(DataSetFilter op) {
        if (op == null) {
            return false;
        }
        int idx = lookupCurrent.getOperationIdx(op);
        if (idx != -1) {
            lookupCurrent.removeOperation(idx);
            return true;
        }
        return false;
    }

    @Override
    public boolean drillUp(DataSetGroup op) {
        return _unfilter(op, true);
    }

    @Override
    public void sort(String columnId, SortOrder sortOrder) {
        unsort();
        String sourceId = _getSourceColumnId(columnId);
        DataSetSort sortOp = new DataSetSort();
        sortOp.addSortColumn(new ColumnSort(sourceId, sortOrder));
        lookupCurrent.addOperation(sortOp);
    }

    public boolean unsort() {
        int n = lookupCurrent.removeOperations(DataSetOpType.SORT);
        return n > 0;
    }

    @Override
    public void lookupDataSet(final DataSetReadyCallback callback) throws Exception {
        clientServices.lookupDataSet(lookupCurrent, new DataSetReadyCallback() {
            public void callback(DataSet dataSet) {
                lastLookedUpDataSet = dataSet;
                callback.callback(dataSet);
            }
            public void notFound() {
                callback.notFound();
            }

            @Override
            public boolean onError(final ClientRuntimeError error) {
                return callback.onError(error);
            }
        });
    }

    @Override
    public Interval getInterval(String columnId, int row) {
        if (lastLookedUpDataSet == null) {
            return null;
        }

        DataColumn column = lastLookedUpDataSet.getColumnById(columnId);
        if (column == null) {
            return null;
        }

        // Get the target value
        List values = column.getValues();
        Object value = row < values.size() ? values.get(row) : null;
        if (value == null) {
            return null;
        }

        Interval result = new Interval(value.toString(), row);
        result.setType(column.getIntervalType());
        result.setMinValue(column.getMinValue());
        result.setMaxValue(column.getMaxValue());
        return result;
    }

    @Override
    public void exportCurrentDataSetLookup(ExportFormat format, int maxRows, ExportCallback callback, Map<String,String> columnNameMap) {

        // Export an empty data set does not make sense
        if (lastLookedUpDataSet == null || lastLookedUpDataSet.getRowCount() == 0) {
            callback.noData();
            return;
        }
        // Ensure the entire dataset does not exceed the maximum export limit
        int allRows = lastLookedUpDataSet.getRowCountNonTrimmed();
        if (maxRows > 0 && allRows > maxRows) {
            callback.tooManyRows(allRows);
            return;
        }
        try {
            // Create a backend export callback
            DataSetExportReadyCallback exportReadyCallback = new DataSetExportReadyCallback() {

                @Override
                public void exportReady(Path exportFilePath) {
                    final String u = clientServices.getDownloadFileUrl(exportFilePath);
                    callback.exportFileUrl(u);
                }
                @Override
                public void onError(ClientRuntimeError error) {
                    callback.error(error);
                }
            };

            // Export the entire data set
            DataSetLookup exportLookup = getCurrentDataSetLookup().cloneInstance();
            exportLookup.setRowOffset(0);
            exportLookup.setNumberOfRows(maxRows);

            // Make sure the column names are set as specified
            if (exportLookup.getLastGroupOp() != null && columnNameMap != null) {
                for (GroupFunction groupFunction : exportLookup.getLastGroupOp().getGroupFunctions()) {
                    String columnId = groupFunction.getColumnId();
                    if (columnNameMap.containsKey(columnId)) {
                        String columnName = columnNameMap.get(columnId);
                        groupFunction.setColumnId(columnName);
                    }
                }
            }

            if (ExportFormat.XLS.equals(format)) {
                clientServices.exportDataSetExcel(exportLookup, exportReadyCallback);
            } else {
                clientServices.exportDataSetCSV(exportLookup, exportReadyCallback);
            }
        }
        catch (Exception e) {
            callback.error(new ClientRuntimeError(e));
        }
    }

    // Internal filter/drillDown implementation logic

    protected Map<String,List<GroupOpFilter>> _groupOpsAdded = new HashMap<>();
    protected Map<String,List<GroupOpFilter>> _groupOpsSelected = new HashMap<>();

    protected void _filter(int index, DataSetGroup op, boolean drillDown) {

        ColumnGroup cgroup = op.getColumnGroup();
        String columnId = cgroup.getColumnId();
        if (!_groupOpsAdded.containsKey(columnId)) _groupOpsAdded.put(columnId, new ArrayList<>());
        List<GroupOpFilter> filterOps = _groupOpsAdded.get(columnId);

        // When adding an external filter, look first if it exists an existing filter already.
        if (!drillDown) {
            for (GroupOpFilter filterOp : filterOps) {
                if (!filterOp.drillDown && filterOp.groupOp.getColumnGroup().equals(cgroup)) {
                    filterOp.groupOp.getSelectedIntervalList().clear();
                    filterOp.groupOp.getSelectedIntervalList().addAll(op.getSelectedIntervalList());
                    return;
                }
            }
        }
        GroupOpFilter groupOpFilter = new GroupOpFilter(op, drillDown);
        filterOps.add(groupOpFilter);
        lookupCurrent.addOperation(index, op);
    }

    protected void _select(DataSetGroup op, List<Interval> intervalList) {
        GroupOpFilter groupOpFilter = new GroupOpFilter(op, true);
        op.setSelectedIntervalList(intervalList);

        String columnId = op.getColumnGroup().getColumnId();
        if (!_groupOpsSelected.containsKey(columnId)) {
            _groupOpsSelected.put(columnId, new ArrayList<>());
        }
        _groupOpsSelected.get(columnId).add(groupOpFilter);
    }

    protected boolean _unfilter(DataSetGroup op, boolean drillDown) {
        boolean opFound = false;
        String columnId = op.getColumnGroup().getColumnId();

        if (_groupOpsAdded.containsKey(columnId)) {

            Iterator<GroupOpFilter> it1 = _groupOpsAdded.get(columnId).iterator();
            while (it1.hasNext()) {
                GroupOpFilter target = it1.next();

                Iterator<DataSetOp> it2 = lookupCurrent.getOperationList().iterator();
                while (it2.hasNext()) {
                    DataSetOp next = it2.next();
                    if (next == target.groupOp && target.drillDown == drillDown) {
                        it1.remove();
                        it2.remove();
                        opFound = true;
                    }
                }
            }
        }

        if (_groupOpsSelected.containsKey(columnId)) {

            Iterator<GroupOpFilter> it1 = _groupOpsSelected.get(columnId).iterator();
            while (it1.hasNext()) {
                GroupOpFilter target = it1.next();

                Iterator<DataSetGroup> it2 = lookupCurrent.getOperationList(DataSetGroup.class).iterator();
                while (it2.hasNext()) {
                    DataSetGroup next = it2.next();
                    if (next == target.groupOp && target.drillDown == drillDown) {
                        it1.remove();
                        next.getSelectedIntervalList().clear();
                        next.getGroupFunctions().clear();
                        next.getSelectedIntervalList().addAll(target.intervalList);
                        next.getGroupFunctions().addAll(target.groupFunctions);
                        opFound = true;
                    }
                }
            }
        }
        return opFound;
    }

    protected String _getSourceColumnId(String columnId) {
        if (lastLookedUpDataSet != null) {
            DataColumn column = lastLookedUpDataSet.getColumnById(columnId);
            if (column != null && column.getGroupFunction() != null) {
                String sourceId = column.getGroupFunction().getSourceId();
                if (sourceId != null) {
                    return sourceId;
                }
            }
        }
        for (List<GroupOpFilter> currentSelections : _groupOpsSelected.values()) {
            for (GroupOpFilter groupOpFilter : currentSelections) {
                GroupFunction gf = groupOpFilter.groupOp.getGroupFunction(columnId);
                if (gf != null) {
                    return gf.getSourceId();
                }
            }
        }
        return columnId;
    }

    protected static class GroupOpFilter {
        DataSetGroup groupOp;
        boolean drillDown = false;
        List<GroupFunction> groupFunctions;
        List<Interval> intervalList;

        private GroupOpFilter(DataSetGroup op, boolean drillDown) {
            this.groupOp = op;
            this.drillDown = drillDown;
            this.groupFunctions = new ArrayList<>(op.getGroupFunctions());
            this.intervalList = new ArrayList<>(op.getSelectedIntervalList());
        }

        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append("drillDown(").append(drillDown).append(") ");
            if (groupOp != null) out.append("groupOp(").append(groupOp).append(")");
            return out.toString();
        }
    }
}