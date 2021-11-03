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

import java.util.List;
import java.util.ArrayList;

/**
 * A set of constraints over the structure of a DataSet instance.
 */
public class DataSetConstraints<T> {

    public static final int ERROR_COLUMN_TYPE = 100;
    public static final int ERROR_COLUMN_NUMBER = 101;

    protected List<ColumnType[]> columnTypeList = new ArrayList<ColumnType[]>();
    protected int minColumns = -1;
    protected int maxColumns = -1;
    protected boolean extraColumnsAllowed = false;
    protected ColumnType extraColumnsType = null;

    public ColumnType[] getColumnTypes() {
        if (columnTypeList.isEmpty()) return null;
        return columnTypeList.get(0);
    }

    public ColumnType[] getColumnTypes(int numberOfColumns) {
        if (columnTypeList.isEmpty()) {
            return null;
        }
        for (ColumnType[] types : columnTypeList) {
            if (types.length == numberOfColumns) {
                return types;
            }
        }
        return columnTypeList.get(0);
    }

    public T setColumnTypes(ColumnType[]... typeList) {
        columnTypeList.clear();
        if (typeList != null) {
            for (ColumnType[] types : typeList) {
                _checkSizes(minColumns, maxColumns, types);
                this.columnTypeList.add(types);
            }
        }
        return (T) this;
    }

    public int getMaxColumns() {
        return maxColumns;
    }

    public T setMaxColumns(int maxColumns) {
        for (ColumnType[] types : columnTypeList) {
            _checkSizes(minColumns, maxColumns, types);
        }
        this.maxColumns = maxColumns;
        return (T) this;
    }

    public int getMinColumns() {
        return minColumns;
    }

    public T setMinColumns(int minColumns) {
        for (ColumnType[] types : columnTypeList) {
            _checkSizes(minColumns, maxColumns, types);
        }
        this.minColumns = minColumns;
        return (T) this;
    }

    public boolean areExtraColumnsAllowed() {
        return extraColumnsAllowed;
    }

    public T setExtraColumnsAllowed(boolean extraColumnsAllowed) {
        this.extraColumnsAllowed = extraColumnsAllowed;
        return (T) this;
    }

    public ColumnType getExtraColumnsType() {
        return extraColumnsType;
    }

    public T setExtraColumnsType(ColumnType extraColumnsType) {
        this.extraColumnsType = extraColumnsType;
        return (T) this;
    }

    private void _checkSizes(int min, int max, ColumnType[] types) {
        if (min == 0) {
            throw new IllegalArgumentException("Minimum data set columns must be greater than or equal to 1. Actual=" + min);
        }
        if (max == 0) {
            throw new IllegalArgumentException("Maximum data set columns must be greater than or equal to 1. Actual=" + max);
        }
        if (min != -1 && max != -1 && min > max) {
            throw new IllegalArgumentException("Min=" + min + " data set columns cannot be greater than the max=" + max);
        }
        if (types != null) {
            if (min != -1 && types.length < min) {
                throw new IllegalArgumentException("columnTypes is smaller than " + min);
            }
            if (max != -1 && types.length > max) {
                throw new IllegalArgumentException("columnTypes is greater than " + max);
            }
        }
    }

    public ValidationError check(DataSet dataSet) {

        if (minColumns != -1 && dataSet.getColumns().size() < minColumns) {
            return createValidationError(ERROR_COLUMN_NUMBER);
        }
        if (maxColumns != -1 && dataSet.getColumns().size() > maxColumns) {
            return createValidationError(ERROR_COLUMN_NUMBER);
        }
        ValidationError error = null;
        boolean ok = false;
        int currentColumns  = -1;
        for (ColumnType[] types : columnTypeList) {
            if (currentColumns < 0 || currentColumns < types.length) currentColumns = types.length;
            error = checkTypes(dataSet, types);
            if (!ok && error == null) ok = true;
        }
        if (!ok) return error;

        // Check extra columns type
        if (currentColumns > 0 && extraColumnsAllowed && extraColumnsType != null && dataSet.getColumns().size() > currentColumns) {
            for (int i = currentColumns; i < dataSet.getColumns().size(); i++) {
                ColumnType columnType = dataSet.getColumnByIndex(i).getColumnType();
                if (!columnType.equals(extraColumnsType)) {
                    return createValidationError(ERROR_COLUMN_TYPE, i, extraColumnsType, columnType);
                }
            }
        }
        return null;
    }

    private ValidationError checkTypes(DataSet dataSet, ColumnType[] types) {
        for (int i = 0; i < dataSet.getColumns().size(); i++) {
            ColumnType columnType = dataSet.getColumnByIndex(i).getColumnType();
            if (i < types.length && !columnType.equals(types[i])) {
                return createValidationError(ERROR_COLUMN_TYPE, i, types[i], columnType);
            }
        }
        return null;
    }

    protected ValidationError createValidationError(int error, Object... params) {
        switch (error) {
            case ERROR_COLUMN_NUMBER:
                return new ValidationError(error, "Number of columns exceeds the limits ["
                        + (minColumns == -1 ? 0 : minColumns) + ", " + (maxColumns != -1 ? maxColumns : "unlimited") + "]");
            case ERROR_COLUMN_TYPE:
                Integer idx = (Integer) params[0];
                ColumnType expected = (ColumnType) params[1];
                ColumnType found = (ColumnType) params[2];
                return new ValidationError(error, "Column " + idx + " type=" + found + ", expected=" + expected);
        }
        return new ValidationError(error);
    }
}