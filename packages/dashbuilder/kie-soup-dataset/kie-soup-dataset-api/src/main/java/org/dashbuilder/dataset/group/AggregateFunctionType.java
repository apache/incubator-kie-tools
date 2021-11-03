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

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.dataset.ColumnType;

/**
 * List of available aggregate functions used in data set group operations.
 */
public enum AggregateFunctionType {
    COUNT,
    DISTINCT,
    AVERAGE,
    SUM,
    MIN,
    MAX;

    private static AggregateFunctionType[] _typeArray = values();
    private static List<AggregateFunctionType> _numericOnly = Arrays.asList(AVERAGE, SUM, MAX, MIN);

    public int getIndex() {
        for (int i = 0; i < _typeArray.length; i++) {
            AggregateFunctionType type = _typeArray[i];
            if (this.equals(type)) return i;
        }
        return -1;
    }

    public boolean supportType(ColumnType type) {
        if (_numericOnly.contains(this)) {
            return type != null && type.equals(ColumnType.NUMBER);
        }
        return true;
    }

    public ColumnType getResultType(ColumnType columnType) {
        if (columnType != null &&  (MIN.equals(this) || MAX.equals(this))) {
            return columnType;
        }
        return ColumnType.NUMBER;
    }

    public static AggregateFunctionType getByIndex(int index) {
        return _typeArray[index];
    }

    public static AggregateFunctionType getByName(String str) {
        try {
            if (str == null) {
                return null;
            }
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
