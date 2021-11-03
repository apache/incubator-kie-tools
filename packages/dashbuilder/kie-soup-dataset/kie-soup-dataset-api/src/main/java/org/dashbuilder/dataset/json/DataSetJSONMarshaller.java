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
package org.dashbuilder.dataset.json;

import java.util.Date;
import java.util.List;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonException;
import org.dashbuilder.json.JsonNull;
import org.dashbuilder.json.JsonObject;

public class DataSetJSONMarshaller {

    private static final String DATASET_COLUMN = "column";
    private static final String DATASET_COLUMN_ID = "id";
    private static final String DATASET_COLUMN_TYPE = "type";
    private static final String DATASET_COLUMN_VALUES = "values";
    
    
    private static final String NULL_VALUE = "";

    private static DataSetJSONMarshaller SINGLETON = new DataSetJSONMarshaller();

    public static DataSetJSONMarshaller get() {
        return SINGLETON;
    }

    public JsonObject toJson(DataSet dataSet) throws JsonException {
        JsonObject json = Json.createObject();
        if (dataSet != null) {
            int i = 0;
            for (DataColumn dataColumn: dataSet.getColumns()) {
                json.put(DATASET_COLUMN + "." + i++, formatDataColumn(dataColumn));
            }
        }
        return json;
    }

    private JsonObject formatDataColumn(DataColumn dataColumn) throws JsonException {
        JsonObject columnJson = Json.createObject();
        if (dataColumn != null) {
            columnJson.put(DATASET_COLUMN_ID, dataColumn.getId());
            columnJson.put(DATASET_COLUMN_TYPE, dataColumn.getColumnType().toString());
            columnJson.put(DATASET_COLUMN_VALUES, formatColumnValues(dataColumn, dataColumn.getValues()));
        }
        return columnJson;
    }

    private JsonArray formatColumnValues(DataColumn dataColumn, List values) throws JsonException {
        JsonArray valuesJson = Json.createArray();
        if ( values != null ) {
            int i = 0;
            for (Object value : values) {
                if (value == null) {
                    valuesJson.set(i++, JsonNull.NULL_INSTANCE);
                    continue;
                }
                switch (dataColumn.getColumnType()) {
                    case DATE: {
                        String l = Long.toString(((Date) value).getTime());
                        valuesJson.set(i++, l);
                        break;
                    }
                    default:
                        valuesJson.set(i++, value.toString());
                        break;
                }
            }
        }
        return valuesJson;
    }

    public DataSet fromJson(String jsonString) throws JsonException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        JsonObject json = Json.parse(jsonString);
        return fromJson(json);
    }

    public DataSet fromJson(JsonObject dataSetJson) throws JsonException {
        if (dataSetJson == null) {
            return null;
        }

        DataSet dataSet = DataSetFactory.newEmptyDataSet();
        for (int i = 0; i < dataSetJson.size(); i++) {
            JsonObject columnJson = dataSetJson.getObject(DATASET_COLUMN + "." + Integer.toString(i));
            parseDataColumn(dataSet, columnJson, i);
        }
        return dataSet;
    }

    private void parseDataColumn( DataSet dataSet, JsonObject columnJson, int i) throws JsonException {
        if ( columnJson != null) {
            String columnId = columnJson.getString(DATASET_COLUMN_ID);
            String columnType = columnJson.getString(DATASET_COLUMN_TYPE);

            if ( columnId == null || columnType == null ) throw new RuntimeException("Dataset column id or type not specified");

            dataSet.addColumn(columnId, ColumnType.valueOf(columnType));
            DataColumn dataColumn = dataSet.getColumnByIndex(i);
            parseColumnValues( dataColumn, columnJson );
        }
    }

    private void parseColumnValues( DataColumn dataColumn, JsonObject columnJson) throws JsonException {
        JsonArray valueArray = columnJson.getArray(DATASET_COLUMN_VALUES);
        if (valueArray != null) {
            List values = dataColumn.getValues();
            for ( int i = 0; i < valueArray.length(); i++ ) {
                String stringJson = valueArray.getString(i);
                if (isJsonNull(stringJson)) {
                    values.add(null);
                    continue;
                } 
                switch ( dataColumn.getColumnType() ) {
                    case DATE: values.add( parseDateValue(stringJson)); break;
                    case NUMBER: values.add( parseNumberValue(stringJson)); break;
                    case LABEL: values.add(stringJson); break;
                    case TEXT: values.add(stringJson); break;
                }
            }
        }
    }

    private boolean isJsonNull(String stringJson) {
        return stringJson == null || 
                JsonNull.NULL_INSTANCE.asString().equals(stringJson);
    }

    private Date parseDateValue(String stringValue) {
        Long dateLong = Long.parseLong(stringValue, 10);
        return new Date(dateLong);
    }

    private Double parseNumberValue(String stringValue) {
        return Double.parseDouble(stringValue);
    }
}
