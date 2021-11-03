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
package org.dashbuilder.dataprovider.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataColumnDef;

public class CSVParser {

    // Custom pattern for Unix dates (epoch).
    public static final String DATE_FORMAT_EPOCH = "epoch";
    
    protected transient Map<String,DateFormat> _dateFormatMap = new HashMap<String,DateFormat>();
    protected transient Map<String,DecimalFormat> _numberFormatMap = new HashMap<String,DecimalFormat>();
    protected CSVDataSetDef dataSetDef;
    protected CSVFileStorage csvStorage;

    public CSVParser(CSVDataSetDef def, CSVFileStorage csvFileStorage) {
        this.dataSetDef = def;
        this.csvStorage = csvFileStorage;
    }

    protected boolean isColumnIncluded(String columnId) {
        if (dataSetDef.isAllColumnsEnabled()) return true;
        if (dataSetDef.getColumns() == null) return false;
        return dataSetDef.getColumnById(columnId) != null;
    }

    protected DataSet load() throws Exception {
        InputStream is = getCSVInputStream();
        CSVReader csvReader = null; 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            csvReader = new CSVReader(br, dataSetDef.getSeparatorChar(), dataSetDef.getQuoteChar(), dataSetDef.getEscapeChar());

            String[] header = csvReader.readNext();
            if (header == null) throw new IOException("CSV has no header: " + dataSetDef);

            String[] firstRow = csvReader.readNext();
            if (firstRow != null && firstRow.length < header.length) {
                String exceptionMessage = String.format("CSV parse error : The first row has fewer columns (%d) than the header (%d)"
                        , firstRow.length, header.length);
                // Fail fast - see DASHBUILDE-172
                throw new IllegalArgumentException(exceptionMessage);
            }

            // Build the data set structure
            List<Integer> _columnIdxs = new ArrayList<Integer>();
            DataSet dataSet = DataSetFactory.newEmptyDataSet();
            for (int i = 0; i < header.length; i++) {
                String columnId = header[i];
                if (isColumnIncluded(columnId)) {
                    ColumnType type = ColumnType.LABEL;
                    if (firstRow != null) type = calculateType(columnId, firstRow[i]);
                    dataSet.addColumn(columnId, type);
                    _columnIdxs.add(i);
                }
            }

            // Load & insert the CSV rows
            if (firstRow != null) {
                Object[] _rowArray = new Object[dataSet.getColumns().size()];
                _processLine(dataSet, _rowArray, firstRow, _columnIdxs);
                dataSet.setValuesAt(dataSet.getRowCount(), _rowArray);
                String[] _line = csvReader.readNext();
                while (_line != null && _line.length == header.length) {
                    _processLine(dataSet,_rowArray, _line, _columnIdxs);
                    dataSet.setValuesAt(dataSet.getRowCount(), _rowArray);
                    _line = csvReader.readNext();
                }
            }
            return dataSet;
        } finally {
            is.close();
            if (csvReader != null) {
                csvReader.close();
            }
        }
    }

    protected InputStream getCSVInputStream() throws Exception {
        String url = dataSetDef.getFileURL();
        if (!StringUtils.isBlank(url)) {
            return new URL(url).openStream();
        }
        String path = dataSetDef.getFilePath();
        if (!StringUtils.isBlank(path)) {
            File f = new File(path);
            if (f.exists()) {
                return new FileInputStream(f);
            }
        }
        if (csvStorage != null) {
            InputStream is = csvStorage.getCSVInputStream(dataSetDef);
            if (is != null) {
                return is;
            }
        }
        throw new IllegalArgumentException("CSV content not found: " + dataSetDef);
    }

    protected ColumnType calculateType(String columnId, String value) {
        DataColumnDef column = dataSetDef.getColumnById(columnId);
        if (column != null) return column.getColumnType();

        try {
            DateFormat dateFormat = getDateFormat(columnId);
            dateFormat.parse(value);
            return ColumnType.DATE;
        } catch (Exception e) {
            DecimalFormat numberParser = getNumberFormat(columnId);
            if (canBeParsedAsNumber(numberParser, value)) {
                return ColumnType.NUMBER;
            } else {
                return ColumnType.LABEL;
            }
        }
    }

    /* Given value can be parsed as number if the parser successfully consumes the entire input String */
    protected boolean canBeParsedAsNumber(DecimalFormat parser, String value) {
        value = value.trim();
        ParsePosition position = new ParsePosition(0);
        parser.parse(value, position);
        return position.getIndex() == value.length();
    }

    protected void _processLine(DataSet dataSet, Object[] row, String[] line, List<Integer> columnIdxs) throws Exception {
        List<DataColumn> columns = dataSet.getColumns();
        for (int i=0; i<columns.size(); i++) {
            DataColumn column = dataSet.getColumnByIndex(i);
            int columnIdx = columnIdxs.get(i);
            String valueStr = line[columnIdx];
            if (!StringUtils.isBlank(valueStr)){
                row[i] = parseValue(column, valueStr);
            } else {
                row[i] = null;
            }
        }
    }

    protected Object parseValue(DataColumn column, String value) throws Exception {
        ColumnType type = column.getColumnType();
        try {
            if (type.equals(ColumnType.DATE)) {
                String pattern = dataSetDef.getPattern(column.getId());
                // Handle special date pattern "epoch"
                if (pattern != null && DATE_FORMAT_EPOCH.equalsIgnoreCase(pattern)) {
                    Double _epoch = Double.parseDouble(value);
                    return new Date(_epoch.longValue() * 1000);
                } else {
                    DateFormat dateFormat = getDateFormat(column.getId());
                    return dateFormat.parse(value);
                }
            } else if (type.equals(ColumnType.NUMBER)) {
                DecimalFormat numberFormat = getNumberFormat(column.getId());
                return numberFormat.parse(value).doubleValue();
            } else {
                return value;
            }
        } catch (ParseException e) {
            String msg = "Error parsing value: " + value + ", " + e.getMessage() + ". Check column\u0027s data type consistency!";
            throw new Exception(msg);
        }
    }

    protected DateFormat getDateFormat(String columnId) {
        DateFormat format = _dateFormatMap.get(columnId);
        if (format == null) {
            format = new SimpleDateFormat(dataSetDef.getDatePattern(columnId));
            _dateFormatMap.put(columnId, format);
        }
        return format;
    }

    protected DecimalFormat getNumberFormat(String columnId) {
        DecimalFormat format = _numberFormatMap.get(columnId);
        if (format == null) {
            DecimalFormatSymbols numberSymbols = new DecimalFormatSymbols();
            numberSymbols.setGroupingSeparator(dataSetDef.getNumberGroupSeparator(columnId));
            numberSymbols.setDecimalSeparator(dataSetDef.getNumberDecimalSeparator(columnId));
            format = new DecimalFormat(dataSetDef.getNumberPattern(columnId), numberSymbols);
            _numberFormatMap.put(columnId, format);
        }
        return format;
    }
}
