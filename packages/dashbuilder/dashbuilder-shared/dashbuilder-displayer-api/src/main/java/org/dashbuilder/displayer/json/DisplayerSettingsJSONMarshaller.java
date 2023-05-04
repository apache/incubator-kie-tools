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
package org.dashbuilder.displayer.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.json.DataSetJSONMarshaller;
import org.dashbuilder.dataset.json.DataSetLookupJSONMarshaller;
import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonNull;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonType;
import org.dashbuilder.json.JsonValue;

public class DisplayerSettingsJSONMarshaller {

    private static final String DATASET_PREFIX = "dataSet";
    private static final String DATASET_LOOKUP_PREFIX = "dataSetLookup";
    private static final String COLUMNS_PREFIX = "columns";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EXPRESSION = "expression";
    private static final String COLUMN_PATTERN = "pattern";
    private static final String COLUMN_EMPTY = "empty";
    private static final String SETTINGS_UUID = "uuid";

    int displayerId = 0;

    private static final DisplayerSettingsJSONMarshaller SINGLETON = new DisplayerSettingsJSONMarshaller();

    public static DisplayerSettingsJSONMarshaller get() {
        return SINGLETON;
    }

    private DataSetJSONMarshaller dataSetJsonMarshaller;
    private DataSetLookupJSONMarshaller dataSetLookupJsonMarshaller;

    public DisplayerSettingsJSONMarshaller() {
        this(DataSetJSONMarshaller.get(), DataSetLookupJSONMarshaller.get());
    }

    public DisplayerSettingsJSONMarshaller(DataSetJSONMarshaller dataSetJsonMarshaller,
                                           DataSetLookupJSONMarshaller dataSetLookupJsonMarshaller) {
        this.dataSetJsonMarshaller = dataSetJsonMarshaller;
        this.dataSetLookupJsonMarshaller = dataSetLookupJsonMarshaller;
    }

    public DisplayerSettings fromJsonString(String jsonString) {
        DisplayerSettings ds = new DisplayerSettings();

        if (!isBlank(jsonString)) {
            var parseResult = Json.parse(jsonString);
            return fromJsonObject(parseResult);

        }
        return ds;
    }

    public DisplayerSettings fromJsonObject(JsonObject jsonObject) {
        return fromJsonObject(jsonObject, true);
    }

    /**
     * 
     * Parse JSON Object to DisplayerSettings.
     * 
     * @param jsonObject
     * The object to be parsed
     * @param strict
     *  If true then the object will be parsed in a strict mode, not allow mandatory fields out of the parsing.
     * @return
     */
    public DisplayerSettings fromJsonObject(JsonObject jsonObject, boolean strict) {
        var ds = new DisplayerSettings();

        if (jsonObject == null ||
            jsonObject.getType() != JsonType.OBJECT) {
            throw new IllegalArgumentException("Displayer Settings is not using a valid object");
        }

        // UUID
        var uuid = jsonObject.getString(SETTINGS_UUID);
        if (uuid == null) {
            uuid = "D" + displayerId++;
        }

        ds.setUUID(uuid);
        jsonObject.put(SETTINGS_UUID, (String) null);

        // First look if a dataset 'on-the-fly' has been specified
        var lookupNames = Arrays.asList(DATASET_LOOKUP_PREFIX,
                DATASET_LOOKUP_PREFIX.toLowerCase(),
                DATASET_LOOKUP_PREFIX.toUpperCase(),
                "datasetLookup",
                "lookup");
        var data = jsonObject.getObject(DATASET_PREFIX);
        var lookup = dataSetLookupJsonMarshaller.fromJson(jsonObject.getObject(lookupNames));
        if (data != null) {
            var dataSet = dataSetJsonMarshaller.fromJson(data);
            ds.setDataSet(dataSet);

            // Remove from the json input so that it doesn't end up in the settings map.
            jsonObject.put(DATASET_PREFIX, (JsonValue) null);

            // If none was found, look for a dataset lookup definition
        } else if (lookup != null) {
            ds.setDataSetLookup(lookup);
            // Remove from the json input so that it doesn't end up in the settings map.
            jsonObject.put(DATASET_LOOKUP_PREFIX, (JsonValue) null);
        } else if (strict) {
            throw new RuntimeException("Dataset lookup for displayer settings is missing.");
        }

        // Parse the columns settings
        var columns = jsonObject.getArray(COLUMNS_PREFIX);
        if (columns != null && columns.getType() == JsonType.ARRAY) {
            List<ColumnSettings> columnSettingsList = parseColumnsFromJson(columns);
            ds.setColumnSettingsList(columnSettingsList);

            // Remove from the json input so that it doesn't end up in the settings map.
            jsonObject.put(COLUMNS_PREFIX, (JsonValue) null);
        }

        // Now parse all other settings
        ds.setSettingsFlatMap(parseSettingsFromJson(jsonObject));

        // fix settings without a type
        if (ds.getTypeString() == null) {
            if (ds.getComponentId() != null) {
                ds.setType(DisplayerType.EXTERNAL_COMPONENT);
            } else {
                ds.setType(DisplayerType.TABLE);
            }
        }
        if (ds.getTypeString() != null && ds.getType() == null) {
            throw new IllegalArgumentException("Unknown settings type. These are the valids types: " +
                    Arrays.toString(DisplayerType.values()));
        }
        return ds;
    }

    public String toJsonString(DisplayerSettings displayerSettings) {
        return toJsonObject(displayerSettings).toString();
    }

    public JsonObject toJsonObject(DisplayerSettings displayerSettings) {
        var json = Json.createObject();

        // UUID
        json.put(SETTINGS_UUID, displayerSettings.getUUID());

        for (var entry : displayerSettings.getSettingsFlatMap().entrySet()) {
            setNodeValue(json, entry.getKey(), entry.getValue());
        }

        // Data set
        DataSetLookup dataSetLookup = displayerSettings.getDataSetLookup();
        DataSet dataSet = displayerSettings.getDataSet();
        if (dataSet != null) {
            json.put(DATASET_PREFIX, dataSetJsonMarshaller.toJson(dataSet));
        } else if (dataSetLookup != null) {
            json.put(DATASET_LOOKUP_PREFIX, dataSetLookupJsonMarshaller.toJson(dataSetLookup));
        } else {
            throw new RuntimeException("Displayer settings data set lookup not specified or data set is empty.");
        }

        // Column settings
        List<ColumnSettings> columnSettingsList = displayerSettings.getColumnSettingsList();
        if (!columnSettingsList.isEmpty()) {
            json.put(COLUMNS_PREFIX, formatColumnSettings(columnSettingsList));
        }

        return json;
    }

    private void setNodeValue(JsonObject node, String path, String value) {
        if (node == null || isBlank(path) || value == null) {
            return;
        }

        int separatorIndex = path.lastIndexOf('.');
        String nodesPath = separatorIndex > 0 ? path.substring(0, separatorIndex) : "";
        String leaf = separatorIndex > 0 ? path.substring(separatorIndex + 1) : path;

        JsonObject _node = findNode(node, nodesPath, true);
        _node.put(leaf, value);
    }

    private JsonObject findNode(JsonObject parent, String path, boolean createPath) {
        if (parent == null) {
            return null;
        }
        if (isBlank(path)) {
            return parent;
        }

        int separatorIndex = path.indexOf('.');
        String strChildNode = separatorIndex > 0 ? path.substring(0, separatorIndex) : path;
        String remainingNodes = separatorIndex > 0 ? path.substring(separatorIndex + 1) : "";

        JsonObject childNode = parent.getObject(strChildNode);
        if (childNode == null && createPath) {
            childNode = Json.createObject();
            parent.put(strChildNode, childNode);
        }
        return findNode(childNode, remainingNodes, createPath);
    }

    private JsonArray formatColumnSettings(List<ColumnSettings> columnSettingsList) {
        JsonArray jsonArray = Json.createArray();
        for (int i = 0; i < columnSettingsList.size(); i++) {
            ColumnSettings columnSettings = columnSettingsList.get(i);
            String id = columnSettings.getColumnId();
            String name = columnSettings.getColumnName();
            String expression = columnSettings.getValueExpression();
            String pattern = columnSettings.getValuePattern();
            String empty = columnSettings.getEmptyTemplate();

            JsonObject columnJson = Json.createObject();
            if (!isBlank(id)) {
                columnJson.put(COLUMN_ID, id);
                if (!isBlank(name))
                    columnJson.put(COLUMN_NAME, name);
                if (!isBlank(expression))
                    columnJson.put(COLUMN_EXPRESSION, expression);
                if (!isBlank(pattern))
                    columnJson.put(COLUMN_PATTERN, pattern);
                if (!isBlank(empty))
                    columnJson.put(COLUMN_EMPTY, empty);
                jsonArray.set(i, columnJson);
            }
        }
        return jsonArray;
    }

    private List<ColumnSettings> parseColumnsFromJson(JsonArray columnsJsonArray) {
        var columnSettingsList = new ArrayList<ColumnSettings>();
        if (columnsJsonArray == null) {
            return columnSettingsList;
        }

        for (int i = 0; i < columnsJsonArray.length(); i++) {
            var columnJson = columnsJsonArray.getObject(i);
            var columnSettings = new ColumnSettings();
            columnSettingsList.add(columnSettings);
            var columndId = columnJson.getString(COLUMN_ID);
            if (columndId == null) {
                throw new RuntimeException(
                        "Column settings has an invalid column id. Use the field \"id\" to configure a column.");
            }
            columnSettings.setColumnId(columndId);
            columnSettings.setColumnName(columnJson.getString(COLUMN_NAME));
            columnSettings.setValueExpression(columnJson.getString(COLUMN_EXPRESSION));
            columnSettings.setValuePattern(columnJson.getString(COLUMN_PATTERN));
            columnSettings.setEmptyTemplate(columnJson.getString(COLUMN_EMPTY));
        }
        return columnSettingsList;
    }

    private Map<String, String> parseSettingsFromJson(JsonObject settingsJson) {
        Map<String, String> flatSettingsMap = new HashMap<String, String>(30);

        if (settingsJson != null && settingsJson.size() > 0) {
            fillRecursive("", settingsJson, flatSettingsMap);
        }
        return flatSettingsMap;
    }

    private void fillRecursive(String parentPath, JsonObject json, Map<String, String> settings) {
        String sb = isBlank(parentPath) ? "" : parentPath + ".";
        for (String key : json.keys()) {
            String path = sb + key;
            JsonValue value = json.get(key);
            if (value instanceof JsonObject) {
                fillRecursive(path, (JsonObject) value, settings);
            } else if (!(value instanceof JsonNull)) {
                settings.put(path, value.asString());
            }
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
