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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonObject;

public class DataSetMetadataJSONMarshaller {

    public static final String UUID = "uuid";
    public static final String NUMBER_OF_ROWS = "numberOfRows";
    public static final String NUMBER_OF_COLUMNS = "numberOfColumns";
    public static final String COLUMN_IDS = "columnIds";
    public static final String COLUMN_TYPES = "columnTypes";
    public static final String ESTIMATED_SIZE = "estimatedSize";
    public static final String DEFINITION = "definition";

    private DataSetDefJSONMarshaller dataSetDefJSONMarshaller;

    public DataSetMetadataJSONMarshaller() {
        // empty
    }

    public DataSetMetadataJSONMarshaller(DataSetDefJSONMarshaller dataSetDefJSONMarshaller) {
        this.dataSetDefJSONMarshaller = dataSetDefJSONMarshaller;
    }

    public DataSetMetadata fromJSON(String json) {
        JsonObject jsonObj = Json.parse(json);
        return fromJsonObj(jsonObj);
    }

    public DataSetMetadata fromJsonObj(JsonObject json) {
        JsonObject definitionObj = json.getObject(DEFINITION);
        String uuid = json.getString(UUID);
        int numberOfRows = json.getNumber(NUMBER_OF_ROWS).intValue();
        int numberOfColumns = json.getNumber(NUMBER_OF_COLUMNS).intValue();
        JsonArray columnIdsArray = getArray(json, COLUMN_IDS);
        JsonArray columnTypesArray = getArray(json, COLUMN_TYPES);
        int estimatedSize = json.getNumber(ESTIMATED_SIZE).intValue();

        List<String> columnIds = IntStream.range(0, columnIdsArray.length())
                                          .mapToObj(columnIdsArray::getString)
                                          .collect(Collectors.toList());

        List<ColumnType> columnTypes = IntStream.range(0, columnTypesArray.length())
                                                .mapToObj(columnTypesArray::getString)
                                                .map(ColumnType::getByName)
                                                .collect(Collectors.toList());

        DataSetDef definition = null;
        if (definitionObj != null) {
            try {
                definition = dataSetDefJSONMarshaller.fromJsonObj(definitionObj);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing data set definition");
            }
        }
        return new DataSetMetadataImpl(definition, uuid, numberOfRows, numberOfColumns, columnIds, columnTypes, estimatedSize);

    }

    public String toJson(DataSetMetadata dataSetMetadata) {
        return toJSONObj(dataSetMetadata).toJson();
    }

    public JsonObject toJSONObj(DataSetMetadata dataSetMetadata) {

        JsonObject metadataJsonObj = Json.createObject();

        metadataJsonObj.set(UUID, Json.create(dataSetMetadata.getUUID()));
        metadataJsonObj.set(NUMBER_OF_ROWS, Json.create(dataSetMetadata.getNumberOfRows()));
        metadataJsonObj.set(NUMBER_OF_COLUMNS, Json.create(dataSetMetadata.getNumberOfColumns()));

        JsonArray columnIdsJsonArray = Json.createArray();
        listToJsonArray(dataSetMetadata.getColumnIds(), columnIdsJsonArray);
        metadataJsonObj.set(COLUMN_IDS, columnIdsJsonArray);

        JsonArray columnTypesJsonArray = Json.createArray();
        listToJsonArray(dataSetMetadata.getColumnTypes(), columnTypesJsonArray);
        metadataJsonObj.set(COLUMN_TYPES, columnTypesJsonArray);

        metadataJsonObj.set(ESTIMATED_SIZE, Json.create(dataSetMetadata.getEstimatedSize()));
        metadataJsonObj.set(DEFINITION, dataSetDefJSONMarshaller.toJsonObject(dataSetMetadata.getDefinition()));

        return metadataJsonObj;
    }

    private void listToJsonArray(List<?> list, JsonArray columnIdsJsonArray) {
        for (int i = 0; i < list.size(); i++) {
            columnIdsJsonArray.set(i, list.get(i).toString());
        }
    }

    private JsonArray getArray(JsonObject json, String fieldName) {
        JsonArray array = json.getArray(fieldName);
        return array == null ? Json.createArray() : array;
    }

}
