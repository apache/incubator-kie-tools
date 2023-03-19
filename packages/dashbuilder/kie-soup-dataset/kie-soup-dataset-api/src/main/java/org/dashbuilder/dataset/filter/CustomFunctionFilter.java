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
package org.dashbuilder.dataset.filter;

/**
 * A custom provided function filter definition
 */
public class CustomFunctionFilter extends ColumnFilter {

    protected FilterFunction function = null;

    public CustomFunctionFilter() {
    }

    public CustomFunctionFilter(String columnId, FilterFunction function) {
        super(columnId);
        this.function = function;
    }

    public FilterFunction getFunction() {
        return function;
    }

    public void setFunction(FilterFunction function) {
        this.function = function;
    }

    public ColumnFilter cloneInstance() {
        CustomFunctionFilter clone = new CustomFunctionFilter();
        clone.columnId = columnId;
        clone.function = function;
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            CustomFunctionFilter other = (CustomFunctionFilter) obj;
            if (!super.equals(other)) return false;
            if (function != null && !function.getClass().equals(other.getClass())) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
