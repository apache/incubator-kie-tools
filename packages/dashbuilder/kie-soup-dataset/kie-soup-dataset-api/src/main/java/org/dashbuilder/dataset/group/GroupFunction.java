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

/**
 * A function definition.
 */
public class GroupFunction {

    protected String sourceId;
    protected String columnId;
    protected AggregateFunctionType function;

    public GroupFunction() {
    }

    public GroupFunction(String sourceId, String columnId, AggregateFunctionType function) {
        this.sourceId = sourceId;
        this.columnId = columnId;
        this.function = function;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public AggregateFunctionType getFunction() {
        return function;
    }

    public void setFunction(AggregateFunctionType function) {
        this.function = function;
    }

    public GroupFunction cloneInstance() {
        GroupFunction clone = new GroupFunction();
        clone.sourceId = sourceId;
        clone.columnId = columnId;
        clone.function = function;
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            GroupFunction other = (GroupFunction) obj;
            if (sourceId != null && !sourceId.equals(other.sourceId)) return false;
            if (columnId != null && !columnId.equals(other.columnId)) return false;
            if (function != null && !function.equals(other.function)) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        if (sourceId != null) out.append(" column=").append(sourceId);
        if (columnId != null) out.append(" newColumn=").append(columnId);
        if (function != null) out.append(" function=").append(function);
        return out.toString();
    }
}
