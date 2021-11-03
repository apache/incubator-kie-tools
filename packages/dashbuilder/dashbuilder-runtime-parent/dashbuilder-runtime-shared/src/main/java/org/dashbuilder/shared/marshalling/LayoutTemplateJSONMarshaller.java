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
package org.dashbuilder.shared.marshalling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonValue;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponentPart;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate.Style;

public class LayoutTemplateJSONMarshaller {

    private static final String PART_ID = "partId";
    private static final String PARTS = "parts";
    private static final String DRAG_TYPE_NAME = "dragTypeName";
    private static final String LAYOUT_COMPONENTS = "layoutComponents";
    private static final String ROWS = "rows";
    private static final String SPAN = "span";
    private static final String LAYOUT_COLUMNS = "layoutColumns";
    private static final String LAYOUT_PROPERTIES = "layoutProperties";
    private static final String PROPERTIES = "properties";
    private static final String NAME = "name";
    private static final String STYLE = "style";
    private static final String HEIGHT = "height";

    private static LayoutTemplateJSONMarshaller instance;

    static {
        instance = new LayoutTemplateJSONMarshaller();
    }

    public static LayoutTemplateJSONMarshaller get() {
        return instance;
    }

    public JsonObject toJson(LayoutTemplate lt) {
        JsonObject jsonObject = Json.createObject();
        jsonObject.set(STYLE, Json.create(lt.getStyle().name()));
        jsonObject.set(NAME, Json.create(lt.getName()));
        jsonObject.set(LAYOUT_PROPERTIES, propertiesToJson(lt.getLayoutProperties()));
        jsonObject.set(ROWS, rowsToJson(lt.getRows()));
        return jsonObject;
    }
    
    public LayoutTemplate fromJson(String json) {
        return fromJson(Json.parse(json));
    }

    public LayoutTemplate fromJson(JsonObject object) {
        LayoutTemplate template = new LayoutTemplate();

        template.setName(object.getString(NAME));
        template.setStyle(Style.valueOf(object.getString(STYLE)));
        extractProperties(object.getObject(LAYOUT_PROPERTIES), template::addLayoutProperty);
        extractRows(object.getArray(ROWS), template::addRow);

        return template;
    }

    private void extractRows(JsonArray array, Consumer<LayoutRow> rowConsumer) {
        extractObjects(array, this::extractRow, rowConsumer);
    }

    private <T> void extractObjects(JsonArray array, Function<JsonObject, T> objectExtractor, Consumer<T> objectConsumer) {
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                objectConsumer.accept(objectExtractor.apply(array.getObject(i)));
            }
        }
    }

    private LayoutRow extractRow(JsonObject object) {
        LayoutRow row = new LayoutRow(object.getString(HEIGHT),
                                      extractProperties(object.getObject(PROPERTIES)));
        extractColumns(object.getArray(LAYOUT_COLUMNS), row::add);
        return row;
    }

    private void extractColumns(JsonArray array, Consumer<LayoutColumn> columnConsumer) {
        extractObjects(array, this::extractColumn, columnConsumer);
    }

    private LayoutColumn extractColumn(JsonObject object) {
        LayoutColumn column = new LayoutColumn(object.getString(SPAN),
                                               object.getString(HEIGHT),
                                               extractProperties(object.getObject(PROPERTIES)));

        extractRows(object.getArray(ROWS), column::addRow);
        extractComponents(object.getArray(LAYOUT_COMPONENTS), column::add);
        return column;
    }

    private void extractComponents(JsonArray array, Consumer<LayoutComponent> componentConsumer) {
        extractObjects(array, this::extractComponent, componentConsumer);
    }

    private LayoutComponent extractComponent(JsonObject object) {
        LayoutComponent component = new LayoutComponent(object.getString(DRAG_TYPE_NAME));
        extractProperties(object.getObject(PROPERTIES), component::addProperty);
        extractParts(object.getArray(PARTS)).forEach(part -> component.addPartProperties(part.getPartId(), part.getCssProperties()));
        return component;
    }

    private List<LayoutComponentPart> extractParts(JsonArray array) {
        List<LayoutComponentPart> parts = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JsonObject object = array.getObject(i);
                parts.add(new LayoutComponentPart(object.getString(PART_ID),
                                                  extractProperties(object.getObject(PROPERTIES))));
            }
        }
        return parts;
    }

    private Map<String, String> extractProperties(JsonObject object) {
        Map<String, String> properties = new HashMap<>();
        extractProperties(object, properties::put);
        return properties;
    }

    private void extractProperties(JsonObject object, BiConsumer<String, String> consumer) {
        if (object != null) {
            for (String key : object.keys()) {
                consumer.accept(key, object.getString(key));
            }
        }
    }

    private JsonArray rowsToJson(List<LayoutRow> rows) {
        return listToJson(rows, this::rowToJson);
    }

    private <T> JsonArray listToJson(List<T> objects, Function<T, JsonObject> toObjectFunction) {
        JsonArray array = Json.createArray();
        AtomicInteger i = new AtomicInteger();
        objects.forEach(obj -> array.set(i.getAndIncrement(), toObjectFunction.apply(obj)));
        return array;
    }

    private JsonObject rowToJson(LayoutRow row) {
        JsonObject object = Json.createObject();
        object.set(HEIGHT, Json.create(row.getHeight()));
        object.set(PROPERTIES, propertiesToJson(row.getProperties()));
        object.set(LAYOUT_COLUMNS, columnsToJson(row.getLayoutColumns()));
        return object;
    }

    private JsonArray columnsToJson(List<LayoutColumn> layoutColumns) {
        return listToJson(layoutColumns, this::columnToJson);
    }

    private JsonObject columnToJson(LayoutColumn column) {
        JsonObject object = Json.createObject();
        object.set(HEIGHT, Json.create(column.getHeight()));
        object.set(SPAN, Json.create(column.getSpan()));
        object.set(PROPERTIES, propertiesToJson(column.getProperties()));
        object.set(ROWS, rowsToJson(column.getRows()));
        object.set(LAYOUT_COMPONENTS, componentsToJson(column.getLayoutComponents()));
        return object;
    }

    private JsonArray componentsToJson(List<LayoutComponent> layoutComponents) {
        return listToJson(layoutComponents, this::componentToJson);
    }

    private JsonObject componentToJson(LayoutComponent component) {
        JsonObject object = Json.createObject();
        object.set(DRAG_TYPE_NAME, Json.create(component.getDragTypeName()));
        object.set(PROPERTIES, propertiesToJson(component.getProperties()));
        object.set(PARTS, partsToJson(component.getParts()));
        return object;
    }

    private JsonArray partsToJson(List<LayoutComponentPart> parts) {
        return listToJson(parts, this::partToJson);
    }

    private JsonObject partToJson(LayoutComponentPart part) {
        JsonObject object = Json.createObject();
        object.set(PART_ID, Json.create(part.getPartId()));
        object.set(PROPERTIES, propertiesToJson(part.getCssProperties()));
        return object;
    }

    private JsonValue propertiesToJson(Map<String, String> layoutProperties) {
        JsonObject object = Json.createObject();
        layoutProperties.forEach((key, value) -> object.set(key, Json.create(value)));
        return object;
    }

}
