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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.date.TimeFrame;

/**
 * A factory of filter functions
 */
public class FilterFactory {

    // Core filter functions

    public static ColumnFilter isNull() {
        return isNull(null);
    }

    public static ColumnFilter isNull(String columnId) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.IS_NULL);
    }

    public static ColumnFilter notNull() {
        return notNull(null);
    }

    public static ColumnFilter notNull(String columnId) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.NOT_NULL);
    }

    public static ColumnFilter equalsTo(Comparable allowedValue) {
        return equalsTo(null, allowedValue);
    }

    public static ColumnFilter equalsTo(String columnId, Comparable allowedValue) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.EQUALS_TO, allowedValue);
    }

    public static ColumnFilter equalsTo(String columnId, List<Comparable> allowedValues) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.EQUALS_TO, allowedValues);
    }

    public static ColumnFilter likeTo(String columnId, String searchPattern) {
        return likeTo(columnId, searchPattern, true);
    }
    
    public static ColumnFilter likeTo(String columnId, String searchPattern, boolean caseSensitive) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.LIKE_TO, searchPattern, caseSensitive);
    }

    public static ColumnFilter notEqualsTo(Comparable allowedValue) {
        return notEqualsTo(null, allowedValue);
    }

    public static ColumnFilter notEqualsTo(String columnId, Comparable allowedValue) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.NOT_EQUALS_TO, allowedValue);
    }

    public static ColumnFilter lowerThan(Comparable ref) {
        return lowerThan(null, ref);
    }

    public static ColumnFilter lowerThan(String columnId, Comparable ref) {
        if (ref instanceof Number) ref = ((Number) ref).doubleValue();
        return new CoreFunctionFilter(columnId, CoreFunctionType.LOWER_THAN, ref);
    }

    public static ColumnFilter lowerOrEqualsTo(Comparable ref) {
        return lowerOrEqualsTo(null, ref);
    }

    public static ColumnFilter lowerOrEqualsTo(String columnId, Comparable ref) {
        if (ref instanceof Number) ref = ((Number) ref).doubleValue();
        return new CoreFunctionFilter(columnId, CoreFunctionType.LOWER_OR_EQUALS_TO, ref);
    }

    public static ColumnFilter greaterThan(Comparable ref) {
        return greaterThan(null, ref);
    }

    public static ColumnFilter greaterThan(String columnId, Comparable ref) {
        if (ref instanceof Number) ref = ((Number) ref).doubleValue();
        return new CoreFunctionFilter(columnId, CoreFunctionType.GREATER_THAN, ref);
    }

    public static ColumnFilter greaterOrEqualsTo(Comparable ref) {
        return greaterOrEqualsTo(null, ref);
    }

    public static ColumnFilter greaterOrEqualsTo(String columnId, Comparable ref) {
        if (ref instanceof Number) ref = ((Number) ref).doubleValue();
        return new CoreFunctionFilter(columnId, CoreFunctionType.GREATER_OR_EQUALS_TO, ref);
    }

    public static ColumnFilter between(Comparable low, Comparable high) {
        return between(null, low, high);
    }

    public static ColumnFilter between(String columnId, Comparable low, Comparable high) {
        if (low instanceof Number) low = ((Number) low).doubleValue();
        if (high instanceof Number) high = ((Number) high).doubleValue();
        return new CoreFunctionFilter(columnId, CoreFunctionType.BETWEEN, low, high);
    }

    /**
     * @see TimeFrame
     */
    public static ColumnFilter timeFrame(String timeFrame) {
        return timeFrame(null, timeFrame);
    }

    /**
     * @see TimeFrame
     */
    public static ColumnFilter timeFrame(String columnId, String timeFrame) {
        TimeFrame tf = TimeFrame.parse(timeFrame);
        if (tf == null) throw new IllegalArgumentException("Invalid time frame: " + timeFrame);
        return new CoreFunctionFilter(columnId, CoreFunctionType.TIME_FRAME, timeFrame);
    }

    public static ColumnFilter in(String columnId, List<?> allowedValues) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.IN, allowedValues);
    }

    public static ColumnFilter notIn(String columnId, List<?> allowedValues) {
        return new CoreFunctionFilter(columnId, CoreFunctionType.NOT_IN, allowedValues);
    }

    // Boolean operators

    public static ColumnFilter AND(List<ColumnFilter> filters) {
        return AND(null, filters);
    }

    public static ColumnFilter AND(ColumnFilter... filters) {
        return AND(null, filters);
    }

    public static ColumnFilter AND(String columnId, List<ColumnFilter> filters) {
        return new LogicalExprFilter(columnId, LogicalExprType.AND, filters);
    }

    public static ColumnFilter AND(String columnId, ColumnFilter... filters) {
        return new LogicalExprFilter(columnId, LogicalExprType.AND, filters);
    }

    public static ColumnFilter OR(List<ColumnFilter> filters) {
        return OR(null, filters);
    }

    public static ColumnFilter OR(ColumnFilter... filters) {
        return OR(null, filters);
    }

    public static ColumnFilter OR(String columnId, List<ColumnFilter> filters) {
        return new LogicalExprFilter(columnId, LogicalExprType.OR, filters);
    }

    public static ColumnFilter OR(String columnId, ColumnFilter... filters) {
        return new LogicalExprFilter(columnId, LogicalExprType.OR, filters);
    }

    public static ColumnFilter NOT(List<ColumnFilter> filters) {
        return NOT(null, filters);
    }

    public static ColumnFilter NOT(ColumnFilter... filters) {
        return NOT(null, filters);
    }

    public static ColumnFilter NOT(String columnId, List<ColumnFilter> filters) {
        return new LogicalExprFilter(columnId, LogicalExprType.NOT, filters);
    }

    public static ColumnFilter NOT(String columnId, ColumnFilter... filters) {
        return new LogicalExprFilter(columnId, LogicalExprType.NOT, filters);
    }

    public static CoreFunctionFilter createCoreFunctionFilter(String columnId, ColumnType columnType, CoreFunctionType functionType) {
        List params = createParameters(columnType, functionType);
        CoreFunctionFilter columnFilter = new CoreFunctionFilter();
        columnFilter.setColumnId(columnId);
        columnFilter.setType(functionType);
        columnFilter.setParameters(params);
        return columnFilter;
    }

    public static List createParameters(ColumnType columnType, CoreFunctionType functionType) {
        List result = new ArrayList();
        if (CoreFunctionType.LIKE_TO.equals(functionType)) {
            result.add("%Text%");
            result.add(true);
            return result;
        }

        int n = functionType.getParametersCount();

        for (int i=0; i<n; i++) {

            if (ColumnType.NUMBER.equals(columnType)) {
                result.add(100000d * i);
            }
            else if (ColumnType.DATE.equals(columnType)) {
                if (CoreFunctionType.TIME_FRAME.equals(functionType)) {
                    result.add("begin[year] till end[year]");
                }
                else {
                    Date d = new Date();
                    d.setYear(d.getYear()-n+i+1);
                    result.add(d);
                }
            } else {
                result.add("value" + (i + 1));
            }
        }
        return result;
    }

    // Custom filter

    public static ColumnFilter function(String columnId, FilterFunction function) {
        return new CustomFunctionFilter(columnId, function);
    }
}
