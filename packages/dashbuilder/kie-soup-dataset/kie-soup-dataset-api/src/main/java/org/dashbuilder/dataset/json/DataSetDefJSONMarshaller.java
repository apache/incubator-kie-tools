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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonException;
import org.dashbuilder.json.JsonObject;

/**
 * DataSetDef from/to JSON utilities
 */
public class DataSetDefJSONMarshaller {

    public static final String UUID = "uuid";
    public static final String NAME = "name";
    public static final String PROVIDER = "provider";
    public static final String ISPUBLIC = "isPublic";
    public static final String PUSH_ENABLED = "pushEnabled";
    public static final String PUSH_MAXSIZE = "pushMaxSize";
    public static final String COLUMNS = "columns";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_PATTERN = "pattern";
    public static final String FILTERS = "filters";
    public static final String ALL_COLUMNS = "allColumns";
    public static final String CACHE_ENABLED = "cacheEnabled";
    public static final String CACHE_MAXROWS = "cacheMaxRows";
    public static final String REFRESH_TIME = "refreshTime";
    public static final String REFRESH_ALWAYS = "refreshAlways";

    public static final List<String> ROOT_KEYS = Arrays.asList(
                                                               UUID,
                                                               NAME,
                                                               PROVIDER,
                                                               ISPUBLIC,
                                                               PUSH_ENABLED,
                                                               PUSH_MAXSIZE,
                                                               COLUMNS,
                                                               FILTERS,
                                                               ALL_COLUMNS,
                                                               CACHE_ENABLED,
                                                               CACHE_MAXROWS,
                                                               REFRESH_TIME,
                                                               REFRESH_ALWAYS);

    protected DataSetProviderRegistry dataSetProviderRegistry;
    protected DataSetLookupJSONMarshaller dataSetLookupJSONMarshaller;

    public DataSetDefJSONMarshaller(DataSetProviderRegistry dataSetProviderRegistry) {
        this(dataSetProviderRegistry, DataSetLookupJSONMarshaller.get());
    }

    public DataSetDefJSONMarshaller(DataSetProviderRegistry dataSetProviderRegistry, DataSetLookupJSONMarshaller dataSetLookupJSONMarshaller) {
        this.dataSetProviderRegistry = dataSetProviderRegistry;
        this.dataSetLookupJSONMarshaller = dataSetLookupJSONMarshaller;
    }
    
    public DataSetDef fromJson(String jsonString) throws Exception {
        JsonObject json = Json.parse(jsonString);
        return fromJsonObj(json);
    }

    public DataSetDef fromJsonObj(JsonObject json) throws Exception {
        DataSetProviderType type = readProviderType(json);
        DataSetDef dataSetDef = type.createDataSetDef();
        dataSetDef.setProvider(type);

        readGeneralSettings(dataSetDef, json);

        DataSetDefJSONMarshallerExt marshaller = type.getJsonMarshaller();
        if (marshaller != null) {
            marshaller.fromJson(dataSetDef, json);
        } else {
            for (String key : json.keys()) {
                if (!ROOT_KEYS.contains(key)) {
                    String value = json.getString(key);
                    dataSetDef.setProperty(key, value);
                }
            }
        }
        return dataSetDef;
    }

    public DataSetProviderType<?> readProviderType(JsonObject json) throws Exception {
        String provider = json.getString(PROVIDER);
        if (isBlank(provider)) {
            provider = DataSetProviderType.EXTERNAL.getName();
        }
        var type = dataSetProviderRegistry.getProviderTypeByName(provider);
        if (type == null) {
            throw new IllegalArgumentException("Provider not supported: " + provider);
        }
        return type;
    }

    public DataSetDef readGeneralSettings(DataSetDef def, JsonObject json) throws Exception {
        String uuid = json.getString(UUID);
        String name = json.getString(NAME);
        String isPublic = json.getString(ISPUBLIC);
        String pushEnabled = json.getString(PUSH_ENABLED);
        String pushMaxSize = json.getString(PUSH_MAXSIZE);
        String cacheEnabled = json.getString(CACHE_ENABLED);
        String cacheMaxRows = json.getString(CACHE_MAXROWS);
        String refreshTime = json.getString(REFRESH_TIME);
        String refreshAlways = json.getString(REFRESH_ALWAYS);
        String allColumns = json.getString(ALL_COLUMNS);

        if (!isBlank(uuid)) {
            def.setUUID(uuid);
        } else {
            throw new IllegalArgumentException("Data Sets require the uuid field.");
        }
        if (!isBlank(name)) {
            def.setName(name);
        }
        if (!isBlank(isPublic)) {
            def.setPublic(Boolean.parseBoolean(isPublic));
        }
        if (!isBlank(pushEnabled)) {
            def.setPushEnabled(Boolean.parseBoolean(pushEnabled));
        }
        if (!isBlank(pushMaxSize)) {
            def.setPushMaxSize(Integer.parseInt(pushMaxSize));
        }
        if (!isBlank(cacheEnabled)) {
            def.setCacheEnabled(Boolean.parseBoolean(cacheEnabled));
        }
        if (!isBlank(cacheMaxRows)) {
            def.setCacheMaxRows(Integer.parseInt(cacheMaxRows));
        }
        if (!isBlank(refreshTime)) {
            def.setRefreshTime(refreshTime);
        }
        if (!isBlank(refreshAlways)) {
            def.setRefreshAlways(Boolean.parseBoolean(refreshAlways));
        }
        if (!isBlank(allColumns)) {
            def.setAllColumnsEnabled(Boolean.parseBoolean(allColumns));
        }

        if (json.has(COLUMNS)) {
            JsonArray array = json.getArray(COLUMNS);
            for (int i = 0; i < array.length(); i++) {
                JsonObject column = array.getObject(i);
                String columnId = column.getString(COLUMN_ID);
                String columnType = column.getString(COLUMN_TYPE);
                String columnPattern = column.getString(COLUMN_PATTERN);

                if (isBlank(columnId)) {
                    throw new IllegalArgumentException("Column id. attribute is mandatory.");
                }
                if (isBlank(columnType)) {
                    throw new IllegalArgumentException("Missing column 'type' attribute: " + columnId);
                }

                ColumnType type = ColumnType.TEXT;
                if (columnType.equalsIgnoreCase("label")) {
                    type = ColumnType.LABEL;
                } else if (columnType.equalsIgnoreCase("date")) {
                    type = ColumnType.DATE;
                } else if (columnType.equalsIgnoreCase("number")) {
                    type = ColumnType.NUMBER;
                }

                def.addColumn(columnId, type);

                if (!isBlank(columnPattern)) {
                    def.setPattern(columnId, columnPattern);
                }
            }
        }
        if (json.has(FILTERS)) {
            JsonArray array = json.getArray(FILTERS);
            DataSetFilter dataSetFilter = dataSetLookupJSONMarshaller.parseFilterOperation(array);
            def.setDataSetFilter(dataSetFilter);
        }
        return def;
    }

    public String toJsonString(final DataSetDef dataSetDef) throws JsonException {
        return toJsonObject(dataSetDef).toString();
    }

    public JsonObject toJsonObject(final DataSetDef dataSetDef) throws JsonException {
        JsonObject json = Json.createObject();

        // UUID.
        json.put(UUID, dataSetDef.getUUID());

        // Name.
        json.put(NAME, dataSetDef.getName());

        // Provider.
        json.put(PROVIDER, dataSetDef.getProvider().getName());

        // Public.
        json.put(ISPUBLIC, dataSetDef.isPublic());

        // Backend cache.
        json.put(CACHE_ENABLED, dataSetDef.isCacheEnabled());
        json.put(CACHE_MAXROWS, dataSetDef.getCacheMaxRows());

        // Client cache.
        json.put(PUSH_ENABLED, dataSetDef.isPushEnabled());
        json.put(PUSH_MAXSIZE, dataSetDef.getPushMaxSize());

        // Refresh.
        json.put(REFRESH_ALWAYS, dataSetDef.isRefreshAlways());
        json.put(REFRESH_TIME, dataSetDef.getRefreshTime());

        // Specific provider.
        DataSetProviderType type = dataSetDef.getProvider();
        DataSetDefJSONMarshallerExt marshaller = type.getJsonMarshaller();
        if (marshaller != null) {
            marshaller.toJson(dataSetDef, json);
        }

        // Data columns.
        final Collection<DataColumnDef> columns = dataSetDef.getColumns();
        if (columns != null) {
            final JsonArray columnsArray = toJsonObject(columns, dataSetDef);
            if (columnsArray != null) {
                json.put(COLUMNS, columnsArray);
            }
        }

        // Initial filter
        final DataSetFilter filter = dataSetDef.getDataSetFilter();
        if (filter != null) {
            try {
                final JsonArray filters = dataSetLookupJSONMarshaller.formatColumnFilters(filter.getColumnFilterList());
                if (filters != null) {
                    json.put(FILTERS, filters);
                }
            } catch (Exception e) {
                throw new JsonException(e);
            }
        }

        // Extra properties (only when no marshaller is provided)
        if (marshaller == null) {
            for (String key : dataSetDef.getPropertyNames()) {
                if (!ROOT_KEYS.contains(key)) {
                    String value = dataSetDef.getProperty(key);
                    json.put(key, value);
                }
            }
        }
        return json;
    }

    protected JsonArray toJsonObject(final Collection<DataColumnDef> columnList,
                                     final DataSetDef dataSetDef) throws JsonException {
        JsonArray result = null;
        if (columnList != null && !columnList.isEmpty()) {
            result = Json.createArray();
            int idx = 0;
            for (final DataColumnDef column : columnList) {
                final String id = column.getId();
                final ColumnType type = column.getColumnType();
                final JsonObject columnObject = Json.createObject();
                columnObject.put(COLUMN_ID, id);
                columnObject.put(COLUMN_TYPE, type.name().toLowerCase());
                String pattern = dataSetDef.getPattern(id);
                if (pattern != null && pattern.trim().length() > 0) {
                    columnObject.put(COLUMN_PATTERN, pattern);
                }
                result.set(idx++, columnObject);
            }
        }

        return result;
    }

    public static void transferStringValue(String key, JsonObject jsonObject, Consumer<String> setter) {
        transferValue(key, jsonObject::getString, setter);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private static <T> void transferValue(String key, Function<String, T> extractor, Consumer<T> setter) {
        T value = extractor.apply(key);
        if (value != null && !isBlank(value.toString())) {
            setter.accept(value);
        }
    }
}
