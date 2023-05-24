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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonType;
import org.dashbuilder.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponentPart;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate.Style;

public class LayoutTemplateJSONMarshaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutTemplateJSONMarshaller.class);

    private static final String PART_ID = "partId";
    private static final String PARTS = "parts";
    private static final String DRAG_TYPE_NAME = "dragTypeName";
    private static final String LAYOUT_COMPONENTS = "layoutComponents";
    private static final String COMPONENTS = "components";
    private static final String ROWS = "rows";
    private static final String SPAN = "span";
    private static final String LAYOUT_COLUMNS = "layoutColumns";
    private static final String COLUMNS = "columns";
    private static final String LAYOUT_PROPERTIES = "layoutProperties";
    private static final String PROPERTIES = "properties";
    private static final String NAME = "name";
    private static final String STYLE = "style";
    private static final String HEIGHT = "height";
    public static final String SETTINGS = "settings";
    public static final String DISPLAYER = "displayer";

    // default values
    static final String DEFAULT_HEIGHT = "1";
    static final String DEFAULT_SPAN = "12";
    static final String DISPLAYER_DRAG_TYPE = "org.dashbuilder.client.editor.DisplayerDragComponent";
    static final String DEFAULT_DRAG_TYPE = DISPLAYER_DRAG_TYPE;

    // Drag types constants
    static final String HTML_DRAG_TYPE =
            "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent";
    static final String HTML = "HTML";
    static final String HTML_CODE_PROP = "HTML_CODE";

    static final String SCREEN_DRAG_TYPE = "org.dashbuilder.client.navigation.widget.ScreenLayoutDragComponent";
    static final String SCREEN = "SCREEN";
    static final String SCREEN_NAME_PROP = "Screen Name";

    static final String DIV_DRAG_TYPE =
            "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.TargetDivDragComponent";
    static final String DIV = "DIV";
    static final String DIV_ID_PROP = "divId";

    static final String MARKDOWN_DRAG_TYPE =
            "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.MarkdownLayoutDragComponent";
    static final String MARKDOWN = "MARKDOWN";
    static final String MARKDOWN_CODE_PROP = "MARKDOWN_CODE";

    // to make the json more user friendly
    // replacement for Drag type
    private static final String TYPE = "type";

    private static final Map<String, String> TYPES_DRAG;

    private static LayoutTemplateJSONMarshaller instance;

    static {
        TYPES_DRAG = new HashMap<>();
        TYPES_DRAG.put(HTML, HTML_DRAG_TYPE);
        TYPES_DRAG.put(SCREEN, SCREEN_DRAG_TYPE);
        TYPES_DRAG.put(DIV, DIV_DRAG_TYPE);

        TYPES_DRAG.put("Displayer", DISPLAYER_DRAG_TYPE);
        TYPES_DRAG.put("External", "org.dashbuilder.client.editor.external.ExternalDragComponent");

        TYPES_DRAG.put("TABS", "org.dashbuilder.client.navigation.layout.editor.NavTabListDragComponent");
        TYPES_DRAG.put("CAROUSEL", "org.dashbuilder.client.navigation.layout.editor.NavCarouselDragComponent");
        TYPES_DRAG.put("TILES", "org.dashbuilder.client.navigation.layout.editor.NavTilesDragComponent");
        TYPES_DRAG.put("TREE", "org.dashbuilder.client.navigation.layout.editor.NavTreeDragComponent");
        TYPES_DRAG.put("MENU", "org.dashbuilder.client.navigation.layout.editor.NavMenuBarDragComponent");
        instance = new LayoutTemplateJSONMarshaller();
    }

    public static LayoutTemplateJSONMarshaller get() {
        return instance;
    }

    private int pageCounter;

    public JsonObject toJson(LayoutTemplate lt) {
        var jsonObject = Json.createObject();
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
        if (notJsonObject(object)) {
            throw new IllegalArgumentException("Page is invalid");
        }
        var template = new LayoutTemplate();
        var style = object.getString(STYLE);
        var name = object.getString(NAME);
        var rows = object.getArray(ROWS);
        var components = object.getArray(COMPONENTS);
        template.setName(name == null ? "Page " + (++pageCounter) : name);
        template.setStyle(style == null ? Style.FLUID : Style.valueOf(style));
        extractProperties(object.getObject(LAYOUT_PROPERTIES), template::addLayoutProperty);
        extractProperties(object.getObject(PROPERTIES), template::addLayoutProperty);

        if (rows != null) {
            extractRows(object.getArray(ROWS), template::addRow);
        } else if (components != null) {
            var row = new LayoutRow(DEFAULT_HEIGHT, Collections.emptyMap());
            var column = new LayoutColumn(DEFAULT_SPAN);
            row.add(column);
            extractComponents(components, column::add);
            template.addRow(row);
        }
        return template;
    }

    private void extractRows(JsonArray array, Consumer<LayoutRow> rowConsumer) {
        try {
            extractObjects(array, this::extractRow, rowConsumer);
        } catch (Exception e) {
            throw new RuntimeException("Rows are invalid\n" + e.getMessage(), e);
        }
    }

    private LayoutRow extractRow(JsonObject object, int i) {
        if (notJsonObject(object, i)) {
            throw new IllegalArgumentException("Row " + i + " is invalid");
        }
        var height = object.getString(HEIGHT);
        var row = new LayoutRow(height == null ? DEFAULT_HEIGHT : height,
                extractProperties(object.getObject(PROPERTIES)));
        var ltColumns = Optional.ofNullable(object.getArray(LAYOUT_COLUMNS)).orElse(object.getArray(COLUMNS));
        extractColumns(ltColumns, i, row::add);
        return row;
    }

    private void extractColumns(JsonArray array, int rowNumber, Consumer<LayoutColumn> columnConsumer) {
        try {
            extractObjects(array, this::extractColumn, columnConsumer);
        } catch (Exception e) {
            throw new RuntimeException("Columns for row " + rowNumber + " are invalid\n" + e.getMessage());
        }
    }

    private LayoutColumn extractColumn(JsonObject object, int i) {
        if (notJsonObject(object, i)) {
            throw new IllegalArgumentException("Column " + i + " is invalid");
        }
        var span = object.getString(SPAN) == null ? DEFAULT_SPAN : object.getString(SPAN);
        var height = object.getString(HEIGHT);

        try {
            Integer.parseInt(span);
        } catch (NumberFormatException e) {
            span = DEFAULT_SPAN;
        }
        var column = new LayoutColumn(span,
                height == null ? DEFAULT_HEIGHT : height,
                extractProperties(object.getObject(PROPERTIES)));

        extractRows(object.getArray(ROWS), column::addRow);
        var componentsArray = Optional.ofNullable(object.getArray(LAYOUT_COMPONENTS)).orElse(object.getArray(
                COMPONENTS));
        try {
            extractComponents(componentsArray, column::add);
        } catch (Exception e) {
            throw new RuntimeException("Components for column " + i + " are invalid\n" + e.getMessage());
        }
        return column;
    }

    private void extractComponents(JsonArray array, Consumer<LayoutComponent> componentConsumer) {
        try {
            extractObjects(array, this::extractComponent, componentConsumer);
        } catch (Exception e) {
            throw new RuntimeException("Components are invalid\n" + e.getMessage());
        }
    }

    private LayoutComponent extractComponent(JsonObject object, int i) {
        if (notJsonObject(object, i)) {
            throw new IllegalArgumentException("Component " + i + " is invalid");
        }
        var dragTypeName = findDragComponent(object);
        var component = findComponentByShortcut(object).orElse(new LayoutComponent(dragTypeName));
        var propertiesObject = object.getObject(PROPERTIES);
        var settings = object.getObject(Arrays.asList(SETTINGS, DISPLAYER));
        extractProperties(propertiesObject, component::addProperty);
        extractParts(object.getArray(PARTS)).forEach(part -> component.addPartProperties(part.getPartId(), part
                .getCssProperties()));
        if (settings != null) {
            try {
                component.setSettings(DisplayerSettingsJSONMarshaller.get().fromJsonObject(settings));
            } catch (Exception e) {
                // just log the error and let displayers handle missing configuration
                LOGGER.warn("Error reading component settings", e);
                var _displayer = new DisplayerSettings();
                _displayer.setError(e.getMessage());
                component.setSettings(_displayer);
            }
        }
        return component;
    }

    private List<LayoutComponentPart> extractParts(JsonArray array) {
        var parts = new ArrayList<LayoutComponentPart>();
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
        var properties = new HashMap<String, String>();
        extractProperties(object, properties::put);
        return properties;
    }

    private void extractProperties(JsonObject object, BiConsumer<String, String> consumer) {
        try {
            if (!notJsonObject(object)) {
                for (String key : object.keys()) {
                    consumer.accept(key, object.getString(key));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error extracting properties");
        }
    }

    private JsonArray rowsToJson(List<LayoutRow> rows) {
        return listToJson(rows, this::rowToJson);
    }

    private <T> JsonArray listToJson(List<T> objects, Function<T, JsonObject> toObjectFunction) {
        var array = Json.createArray();
        var i = new AtomicInteger();
        objects.forEach(obj -> array.set(i.getAndIncrement(), toObjectFunction.apply(obj)));
        return array;
    }

    private JsonObject rowToJson(LayoutRow row) {
        var object = Json.createObject();
        object.set(HEIGHT, Json.create(row.getHeight()));
        object.set(PROPERTIES, propertiesToJson(row.getProperties()));
        object.set(LAYOUT_COLUMNS, columnsToJson(row.getLayoutColumns()));
        return object;
    }

    private JsonArray columnsToJson(List<LayoutColumn> layoutColumns) {
        return listToJson(layoutColumns, this::columnToJson);
    }

    private JsonObject columnToJson(LayoutColumn column) {
        var object = Json.createObject();
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
        var object = Json.createObject();
        object.set(DRAG_TYPE_NAME, Json.create(component.getDragTypeName()));
        object.set(PARTS, partsToJson(component.getParts()));
        object.set(PROPERTIES, propertiesToJson(component.getProperties()));

        var settings = component.getSettings();
        if (settings != null) {
            var displayerSettings = (DisplayerSettings) settings;
            object.set(SETTINGS, DisplayerSettingsJSONMarshaller.get().toJsonObject(displayerSettings));
        }
        return object;
    }

    private JsonArray partsToJson(List<LayoutComponentPart> parts) {
        return listToJson(parts, this::partToJson);
    }

    private JsonObject partToJson(LayoutComponentPart part) {
        var object = Json.createObject();
        object.set(PART_ID, Json.create(part.getPartId()));
        object.set(PROPERTIES, propertiesToJson(part.getCssProperties()));
        return object;
    }

    private JsonValue propertiesToJson(Map<String, String> layoutProperties) {
        var object = Json.createObject();
        layoutProperties.forEach((key, value) -> object.set(key, Json.create(value)));
        return object;
    }

    protected String findDragComponent(JsonObject object) {
        var dragType = object.getString(DRAG_TYPE_NAME);
        if (dragType != null) {
            return dragType;
        }

        var type = object.getString(TYPE);
        if (type != null) {
            for (var entry : TYPES_DRAG.entrySet()) {
                if (type.equalsIgnoreCase(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return type;
        }
        return DEFAULT_DRAG_TYPE;
    }

    /**
     * Shortcut to easily use some components
     */
    protected Optional<LayoutComponent> findComponentByShortcut(JsonObject object) {
        return Stream.of(
                elementShortcut(object, HTML, HTML_CODE_PROP, HTML_DRAG_TYPE),
                elementShortcut(object, SCREEN, SCREEN_NAME_PROP, SCREEN_DRAG_TYPE),
                elementShortcut(object, DIV, DIV_ID_PROP, DIV_DRAG_TYPE),
                elementShortcut(object, MARKDOWN, MARKDOWN_CODE_PROP, MARKDOWN_DRAG_TYPE))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    private boolean notJsonObject(JsonObject object) {
        return notJsonObject(object, -1);
    }

    private boolean notJsonObject(JsonObject object, int i) {
        try {
            return object == null ||
                   object.getType() != JsonType.OBJECT ||
                   object.keys() == null ||
                   object.keys().length == 0;
        } catch (Exception e) {
            throw new RuntimeException("Error validating object " + (i == -1 ? "" : i));
        }
    }

    private <T> void extractObjects(JsonArray array,
                                    BiFunction<JsonObject, Integer, T> objectExtractor,
                                    Consumer<T> objectConsumer) {
        if (array == null) {
            return;
        }

        if (array.getType() != JsonType.ARRAY) {
            throw new IllegalArgumentException("Not a list of elements");
        }
        // trick GWT compiler to actually check if it is an array
        var n = 0;
        try {
            n = array.length();
        } catch (Exception e) {
            throw new IllegalArgumentException("Not a list of elements", e);
        }
        for (int i = 0; i < n; i++) {
            objectConsumer.accept(objectExtractor.apply(array.getObject(i), i + 1));
        }

    }

    /**
     * Resets the page counter to generate numeric names
     */
    public void resetPageCounter() {
        this.pageCounter = 0;
    }

    Optional<LayoutComponent> elementShortcut(JsonObject object,
                                              String elementName,
                                              String elementProperty,
                                              String elementDragType) {
        var element = object.getString(Arrays.asList(elementName, elementName.toLowerCase()));
        if (element != null) {
            var layoutComponent = new LayoutComponent(elementDragType);
            layoutComponent.getProperties().put(elementProperty, element);
            return Optional.of(layoutComponent);
        }
        return Optional.empty();
    }

}
