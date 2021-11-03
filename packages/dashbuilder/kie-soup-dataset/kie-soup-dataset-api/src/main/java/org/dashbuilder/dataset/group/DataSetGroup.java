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
package org.dashbuilder.dataset.group;

import java.util.List;
import java.util.ArrayList;

import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.impl.AbstractDataSetOp;

/**
 * A data set group operation.
 */
public class DataSetGroup extends AbstractDataSetOp {

    protected boolean join = false;
    protected ColumnGroup columnGroup = null;
    protected List<GroupFunction> groupFunctionList = new ArrayList<GroupFunction>();
    protected List<Interval> selectedIntervalList = new ArrayList<Interval>();

    public DataSetOpType getType() {
        return DataSetOpType.GROUP;
    }

    public void setColumnGroup(ColumnGroup columnGroup) {
        this.columnGroup = columnGroup;
    }

    public void addGroupFunction(GroupFunction... groupFunctions) {
        for (GroupFunction groupFunction : groupFunctions) {
            groupFunctionList.add(groupFunction);
        }
    }

    public ColumnGroup getColumnGroup() {
        return columnGroup;
    }

    public List<GroupFunction> getGroupFunctions() {
        return groupFunctionList;
    }

    public GroupFunction getGroupFunction(String columnId) {
        for (GroupFunction gf : groupFunctionList) {
            if (columnId != null && columnId.equals(gf.getColumnId())) {
                return gf;
            }
        }
        for (GroupFunction gf : groupFunctionList) {
            if (columnId != null && columnId.equals(gf.getSourceId())) {
                return gf;
            }
        }
        return null;
    }

    public Integer getGroupFunctionIdx(GroupFunction groupFunction) {
        for (int i=0; i<groupFunctionList.size(); i++) {
            GroupFunction gf = groupFunctionList.get(i);
            if (gf.equals(groupFunction)) {
                return i;
            }
        }
        return null;
    }

    public void addSelectedIntervalNames(String... names) {
        for (String name : names) {
            Interval interval = new Interval(name);
            selectedIntervalList.add(interval);
        }
    }

    public void setSelectedIntervalList(List<Interval> intervalList) {
        if (intervalList == null) {
            selectedIntervalList.clear();
        } else {
            selectedIntervalList = intervalList;

            // Make sure the interval types match
            String intervalsType = null;
            for (Interval interval : intervalList) {
                if (intervalsType == null) {
                    intervalsType = interval.getType();
                }
                else if (!interval.getType().equals(intervalsType)) {
                    throw new RuntimeException("Different interval types. Expected " + intervalsType + " Found " + interval.getType());
                }
            }
            // Force the group interval type to match the intervals declared. This is required in order to ensure
            // the intervals selection are always applied over a properly grouped data independently of the filters present.
            if (columnGroup != null && columnGroup.getIntervalSize() == null) {
                columnGroup.setIntervalSize(intervalsType);
            }
        }
    }

    public List<Interval> getSelectedIntervalList() {
        return selectedIntervalList;
    }

    public boolean isSelect() {
        return !selectedIntervalList.isEmpty();
    }

    public boolean isJoin() {
        return join;
    }

    public void setJoin(boolean join) {
        this.join = join;
    }

    public List<GroupFunction> getAggregationFunctions() {
        List<GroupFunction> result = new ArrayList<GroupFunction>();
        for (GroupFunction groupFunction : groupFunctionList) {
            if (groupFunction.getFunction() != null) {
                result.add(groupFunction);
            }
        }
        return result;
    }

    public DataSetGroup cloneInstance() {
        DataSetGroup clone = new DataSetGroup();
        clone.dataSetUUID = dataSetUUID;
        if (columnGroup != null) clone.columnGroup = columnGroup.cloneInstance();
        clone.join = join;
        clone.selectedIntervalList = new ArrayList();
        for (Interval interval : selectedIntervalList) {
            clone.selectedIntervalList.add(interval.cloneInstance());
        }
        clone.groupFunctionList = new ArrayList();
        for (GroupFunction groupFunction : groupFunctionList) {
            clone.groupFunctionList.add(groupFunction.cloneInstance());
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        try {
            DataSetGroup other = (DataSetGroup) obj;
            if (join != other.join) return false;
            if (columnGroup != null && other.columnGroup == null) return false;
            if (columnGroup == null && other.columnGroup != null) return false;
            if (columnGroup != null && !columnGroup.equals(other.columnGroup)) return false;
            if (groupFunctionList.size() != other.groupFunctionList.size()) return false;
            if (selectedIntervalList.size() != other.selectedIntervalList.size()) return false;

            for (int i = 0; i < groupFunctionList.size(); i++) {
                GroupFunction el = groupFunctionList.get(i);
                if (!other.groupFunctionList.contains(el)) return false;
            }
            for (int i = 0; i < selectedIntervalList.size(); i++) {
                Interval el = selectedIntervalList.get(i);
                if (!other.selectedIntervalList.contains(el)) return false;
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        if (columnGroup != null) {
            out.append("group(").append(columnGroup).append(") ");
            if (join) out.append(".join()");
        }
        if (!selectedIntervalList.isEmpty()) {
            out.append("select(");
            for (Interval interval : selectedIntervalList) {
                out.append(interval.getName()).append(" ");
            }
            out.append(")");
        }
        return out.toString();
    }
}
