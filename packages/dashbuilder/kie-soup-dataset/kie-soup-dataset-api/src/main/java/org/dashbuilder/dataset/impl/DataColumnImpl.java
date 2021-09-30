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
import java.util.ArrayList;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.GroupFunction;

public class DataColumnImpl implements DataColumn {

    protected DataSetImpl dataSet = null;
    protected String id = null;
    protected ColumnType columnType = ColumnType.LABEL;
    protected List values = new ArrayList();
    protected ColumnGroup columnGroup;
    protected String intervalType;
    protected Object minValue;
    protected Object maxValue;
    protected GroupFunction groupFunction;

    public DataColumnImpl() {
    }

    public DataColumnImpl(String id, ColumnType columnType) {
        this.id = id;
        this.columnType = columnType;
    }

    public DataSetImpl getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSetImpl dataSet) {
        this.dataSet = dataSet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public ColumnGroup getColumnGroup() {
        return columnGroup;
    }

    public void setColumnGroup(ColumnGroup columnGroup) {
        this.columnGroup = columnGroup;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public Object getMinValue() {
        return minValue;
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }

    public GroupFunction getGroupFunction() {
        return groupFunction;
    }

    public void setGroupFunction(GroupFunction groupFunction) {
        this.groupFunction = groupFunction;
    }

    public DataColumn cloneEmpty() {
        DataColumnImpl otherCol = new DataColumnImpl();
        otherCol.setId(getId());
        otherCol.setColumnType(getColumnType());
        if (columnGroup != null) otherCol.setColumnGroup(columnGroup.cloneInstance());
        otherCol.setIntervalType(getIntervalType());
        otherCol.setMinValue(getMinValue());
        otherCol.setMaxValue(getMaxValue());
        if (groupFunction != null) otherCol.setGroupFunction(groupFunction.cloneInstance());
        return otherCol;
    }

    public DataColumn cloneInstance() {
        DataColumnImpl otherCol = (DataColumnImpl) cloneEmpty();
        otherCol.setValues(new ArrayList(values));
        return otherCol;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        if (id != null) out.append(id).append(" ");
        if (columnType != null) out.append(columnType);
        return out.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        try {
            DataColumn d = (DataColumn) obj;
            return getId().equals(d.getId());
        } catch (ClassCastException e) {
            return false;
        }
    }
    
}
