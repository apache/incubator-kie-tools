/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonType;
import org.dashbuilder.json.JsonValue;

public class ExternalDataSetJSONParser {

    private static final String ARRAY_START_TOKEN = "[";

    private static final String OBJECT_START_TOKEN = "{";

    private static final String DEFAULT_COLUMN_ID = "";
    private static final String NUMBER_OF_ROWS = "numberOfRows";
    private static final String COLUMNS = "columns";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String VALUES = "values";

    private static final String COLUMN_PREFIX = "Column ";
    private static final ColumnType DEFAULT_COLUMN_TYPE = ColumnType.LABEL;

    private Function<String, Date> dateParser;

    public ExternalDataSetJSONParser() {
        this(s -> null);
    }

    public ExternalDataSetJSONParser(Function<String, Date> dateParser) {
        super();
        this.dateParser = dateParser;
    }

    public DataSetMetadata parseMetadata(String jsonStr) {

        if (jsonStr == null) {
            throw new IllegalArgumentException("Metadata JSON is null");
        }

        var json = Json.parse(jsonStr);
        var columnIds = new ArrayList<String>();
        var columnTypes = new ArrayList<ColumnType>();
        int estimatedSize = -1;
        int numberOfRows = json.getNumber(NUMBER_OF_ROWS).intValue();
        var columns = json.getArray(COLUMNS);

        for (int i = 0; columns != null && i < columns.length(); i++) {
            var column = columns.getObject(i);
            var type = getColumnType(column);
            var id = getColumnId(column);

            columnIds.add(id);
            columnTypes.add(type);
        }

        var dataSetMetadata = new DataSetMetadataImpl(null,
                DEFAULT_COLUMN_ID,
                numberOfRows,
                columnTypes.size(),
                columnIds,
                columnTypes,
                estimatedSize);
        return dataSetMetadata;
    }

    public DataSet parseDataSet(String json) {
        var dataSet = DataSetFactory.newEmptyDataSet();

        if (json != null) {
            var trimedJson = json.trim();
            if (trimedJson.startsWith(OBJECT_START_TOKEN)) {
                try {
                    JsonObject dataSetObject = Json.instance().parse(json);
                    var values = dataSetObject.getArray(VALUES);
                    addColumns(dataSet, dataSetObject);
                    addValues(dataSet, values);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("DataSet JSON is invalid. Expected an array of data columns", e);
                }

            } else if (trimedJson.startsWith(ARRAY_START_TOKEN)) {
                try {
                    JsonArray dataSetArray = Json.instance().parse(json);
                    fillDataSetColumns(dataSet, dataSetArray);
                    addValues(dataSet, dataSetArray);
                } catch (Exception e) {
                    throw new IllegalArgumentException("DataSet JSON is invalid. Please check that the data is in correct format.", e);
                }
            }
        }
        return dataSet;
    }

    private void fillDataSetColumns(DataSet dataSet, JsonArray dataSetArray) {
        if (dataSetArray != null && dataSetArray.length() > 0) {
            var row = dataSetArray;
            var isMatrix = dataSetArray.get(0).getType() == JsonType.ARRAY;
            if (isMatrix) {
                row = dataSetArray.getArray(0);
            }
            for (int i = 0; i < row.length(); i++) {
                var columnId = COLUMN_PREFIX + i;
                var value = row.get(i).asString();
                var type = findValueType(value);
                dataSet.addColumn(columnId, type);
            }
        }
    }

    private void addColumns(DataSet dataSet, JsonObject dataSetObject) {
        var columnsArray = dataSetObject.getArray(COLUMNS);
        if (columnsArray != null) {
            for (int i = 0; i < columnsArray.length(); i++) {
                var dataColumn = columnsArray.getObject(i);
                var id = getColumnId(dataColumn);
                var type = getColumnType(dataColumn);
                dataSet.addColumn(id, type);
            }
        }

    }

    private void addValues(DataSet dataSet, JsonArray valuesArray) {
        if (valuesArray != null && valuesArray.length() > 0) {
            if (dataSet.getColumns().size() == 0 && valuesArray.length() != 0) {
                throw new IllegalArgumentException("DataSet is missing columns.");
            }

            if (valuesArray.get(0).getType() == JsonType.ARRAY) {
                for (int i = 0; i < valuesArray.length(); i++) {
                    var row = valuesArray.getArray(i);
                    fillRow(dataSet, i, row);

                }
            } else {
                fillRow(dataSet, 0, valuesArray);
            }
        }
    }

    private void fillRow(DataSet dataSet, int rowIndex, JsonArray row) {
        for (int j = 0; j < row.length(); j++) {
            var column = dataSet.getColumnByIndex(j);
            var value = row.get(j);
            try {
                var objectValue = convertJsonValue(value, column.getColumnType());
                dataSet.setValueAt(rowIndex, j, objectValue);
            } catch (Exception e) {
                throw new IllegalArgumentException("Incompatible value " + value.asString() + " for column " + column
                        .getId(), e);
            }
        }
    }

    private ColumnType getColumnType(JsonObject column) {
        return Optional.ofNullable(column.getString(COLUMN_TYPE))
                .map(ColumnType::getByName)
                .orElse(DEFAULT_COLUMN_TYPE);
    }

    private String getColumnId(JsonObject column) {
        return Optional.ofNullable(column.getString(COLUMN_ID))
                .orElse(DEFAULT_COLUMN_ID);
    }

    private Object convertJsonValue(JsonValue value, ColumnType type) {
        switch (type) {
            case DATE:
                var valueStr = value.asString();
                if (valueStr != null && valueStr.trim().isEmpty()) {
                    return "";
                }
                return convertToDate(value.asString());
            case NUMBER:
                var number = value.asNumber();
                if (Double.isNaN(number)) {
                    throw new IllegalArgumentException("Not a number: " + value.asString());
                }
                return number;
            case LABEL:
            case TEXT:
            default:
                return value.asString();
        }
    }

    private ColumnType findValueType(String value) {
        try {
            Double.parseDouble(value);
            return ColumnType.NUMBER;
        } catch (NumberFormatException e) {
            // empty
        }

        try {
            convertToDate(value);
            return ColumnType.DATE;
        } catch (Exception e) {
            // empty
        }

        return DEFAULT_COLUMN_TYPE;
    }

    protected Date convertToDate(String value) {
        return dateParser.apply(value);
    }

}
