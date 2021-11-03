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

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RawDataSet implements Serializable {

    protected String[][] data;
    protected Class[] types;
    protected String[] columnIds;
    protected DataSetFormatter dataSetFormatter;

    public static final List<Class<?>> SUPPORTED_TYPES = Arrays.asList(new Class<?>[]{String.class, Double.class, Integer.class, Date.class});

    public RawDataSet(String[] columnIds, Class[] types, String[][] data) {
        this(columnIds, types, data, new DataSetFormatter());
    }

    public RawDataSet(String[] columnIds, Class[] types, String[][] data, DataSetFormatter formatter) {
        this.columnIds = columnIds;
        this.types = types;
        this.data = data;
        this.dataSetFormatter = formatter;

        for (Class<?> type : types) {
            if (!SUPPORTED_TYPES.contains(type)) {
                throw new IllegalArgumentException("Type not supported: " + type);
            }
        }
    }

    public String getRawValueAt(int x, int y) {
        if (x >= data.length) {
            throw new IndexOutOfBoundsException("Max row index allowed: " + (data.length-1));
        }
        String[] row = data[x];
        if (y >= row.length) {
            throw new IndexOutOfBoundsException("Max column index allowed: " + (row.length-1));
        }
        return row[y];
    }


    public Object parseValue(String rawValue, Class<?> type) throws ParseException {
        if (rawValue == null) {
            return null;
        }
        if (Date.class.isAssignableFrom(type)) {
            return dataSetFormatter.dateFormat.parse(rawValue);
        }
        if (Number.class.isAssignableFrom(type)) {
            return dataSetFormatter.numberFormat.parse(rawValue).doubleValue();
        }
        return rawValue;
    }

    public DataSet toDataSet() throws ParseException {
        DataSet dataSet = DataSetFactory.newEmptyDataSet();
        for (int i = 0; i < columnIds.length; i++) {
            dataSet.addColumn(columnIds[i], getColumnType(types[i]));
            for (int j = 0; j < data.length; j++) {
                String[] row = data[j];
                Object value = parseValue(row[i], types[i]);
                dataSet.setValueAt(j, i, value);
            }
        }
        return dataSet;
    }

    public ColumnType getColumnType(Class<?> type) {
        if (Date.class.isAssignableFrom(type)) return ColumnType.DATE;
        if (Number.class.isAssignableFrom(type)) return ColumnType.NUMBER;
        return ColumnType.LABEL;
    }
}
