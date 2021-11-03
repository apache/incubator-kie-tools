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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;

public class DataSetFormatter {

    protected String numberPattern = "#,##0.00";
    protected String datePattern = "MM/dd/yy HH:mm";
    protected transient DateFormat dateFormat;
    protected transient DecimalFormat numberFormat;

    public DataSetFormatter() {
        DecimalFormatSymbols numberSymbols = new DecimalFormatSymbols();
        numberSymbols.setGroupingSeparator(getNumberGroupSeparator());
        numberSymbols.setDecimalSeparator(getNumberDecimalSeparator());

        this.numberFormat = new DecimalFormat(numberPattern, numberSymbols);
        this.dateFormat = new SimpleDateFormat(datePattern);
    }

    public char getNumberGroupSeparator() {
        if (numberPattern.length() < 2) return ',';
        else return numberPattern.charAt(1);
    }

    public char getNumberDecimalSeparator() {
        if (numberPattern.length() < 6) return '.';
        else return numberPattern.charAt(5);
    }

    public String formatValueAt(DataSet dataSet, int row, int col) {
        Object val = dataSet.getValueAt(row, col);
        if (val == null) {
            return "";
        }
        DataColumn column = dataSet.getColumnByIndex(col);
        if (ColumnType.DATE.equals(column.getColumnType())) {
            return dateFormat.format(val);
        }
        if (ColumnType.NUMBER.equals(column.getColumnType())) {
            return numberFormat.format(val);
        }
        return val.toString();
    }

    /**
     * Get a string representation of this data set.
     */
    public String formatDataSet(DataSet dataSet,
            String rowBegin, String rowEnd, String rowSep,
            String columnBegin, String columnEnd, String columnSep) {

        StringBuilder sb = new StringBuilder();
        List<DataColumn> columns = dataSet.getColumns();
        for (int i=0; i<dataSet.getRowCount(); i++) {
            if (i > 0) sb.append(rowSep);
            sb.append(rowBegin);
            for (int j=0; j<columns.size(); j++) {
                if (j > 0) sb.append(columnSep);
                sb.append(columnBegin);
                sb.append(formatValueAt(dataSet, i, j));
                sb.append(columnEnd);
            }
            sb.append(rowEnd);
        }
        return sb.toString();
    }
}
