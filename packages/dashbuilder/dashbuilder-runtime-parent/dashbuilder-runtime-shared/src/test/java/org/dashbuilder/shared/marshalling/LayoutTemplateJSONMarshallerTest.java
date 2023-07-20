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

import java.util.Arrays;
import java.util.Collections;

import org.dashbuilder.json.Json;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate.Style;

import static org.junit.Assert.assertEquals;

public class LayoutTemplateJSONMarshallerTest {

    private static final String PROP_VAL = "LTVALUE";
    private static final String PROP_KEY = "LTPROPERTY";
    private static final String LT_NAME = "test Layout Template";
    private static final String LR_HEIGHT = "ROW HEIGHT";
    private static final String LC_SPAN = "COLUMN SPAN";
    private static final String LC_HEIGHT = "COLUMN HEIGHT";
    private static final String LCR_HEIGHT = "LCR HEIGHT";
    private static final String LCOMP_DRAG_TYPE = "LCOMP_DRAG_TYPE";
    String LT_JSON = "{\n" +
            "  \"style\": \"PAGE\",\n" +
            "  \"name\": \"test Layout Template\",\n" +
            "  \"layoutProperties\": {\n" +
            "    \"LTPROPERTY\": \"LTVALUE\"\n" +
            "  },\n" +
            "  \"rows\": [\n" +
            "    {\n" +
            "      \"height\": \"ROW HEIGHT\",\n" +
            "      \"properties\": {\n" +
            "        \"LTPROPERTY\": \"LTVALUE\"\n" +
            "      },\n" +
            "      \"layoutColumns\": [\n" +
            "        {\n" +
            "          \"height\": \"COLUMN HEIGHT\",\n" +
            "          \"span\": \"COLUMN SPAN\",\n" +
            "          \"properties\": {\n" +
            "            \"LTPROPERTY\": \"LTVALUE\"\n" +
            "          },\n" +
            "          \"rows\": [\n" +
            "            {\n" +
            "              \"height\": \"LCR HEIGHT\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"layoutComponents\": [\n" +
            "            {\n" +
            "              \"dragTypeName\": \"LCOMP_DRAG_TYPE\",\n" +
            "              \"properties\": {\n" +
            "                \"LTPROPERTY\": \"LTVALUE\"\n" +
            "              }\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    String LT_JSON_READABLE = "{\n" +
            "  \"style\": \"PAGE\",\n" +
            "  \"name\": \"test Layout Template\",\n" +
            "  \"properties\": {\n" +
            "    \"LTPROPERTY\": \"LTVALUE\"\n" +
            "  },\n" +
            "  \"rows\": [\n" +
            "    {\n" +
            "      \"height\": \"ROW HEIGHT\",\n" +
            "      \"properties\": {\n" +
            "        \"LTPROPERTY\": \"LTVALUE\"\n" +
            "      },\n" +
            "      \"columns\": [\n" +
            "        {\n" +
            "          \"height\": \"COLUMN HEIGHT\",\n" +
            "          \"span\": \"COLUMN SPAN\",\n" +
            "          \"properties\": {\n" +
            "            \"LTPROPERTY\": \"LTVALUE\"\n" +
            "          },\n" +
            "          \"rows\": [\n" +
            "            {\n" +
            "              \"height\": \"LCR HEIGHT\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"components\": [\n" +
            "            {\n" +
            "              \"dragTypeName\": \"LCOMP_DRAG_TYPE\",\n" +
            "              \"properties\": {\n" +
            "                \"LTPROPERTY\": \"LTVALUE\"\n" +
            "              }\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    String LT_JSON_GENERATE_ROW = "{\n" +
            "  \"style\": \"PAGE\",\n" +
            "  \"name\": \"test Layout Template\",\n" +
            "  \"properties\": {\n" +
            "    \"LTPROPERTY\": \"LTVALUE\"\n" +
            "  },\n" +
            "  \"components\": [\n" +
            "    {\n" +
            "        \"dragTypeName\": \"LCOMP_DRAG_TYPE\",\n" +
            "        \"properties\": {\n" +
            "        \"LTPROPERTY\": \"LTVALUE\"\n" +
            "        }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    String LT_JSON_HTML_SHORTCUT = "{\n" +
            "  \"style\": \"PAGE\",\n" +
            "  \"name\": \"test Layout Template\",\n" +
            "  \"properties\": {\n" +
            "    \"LTPROPERTY\": \"LTVALUE\"\n" +
            "  },\n" +
            "  \"components\": [\n" +
            "    {\n" +
            "        \"html\": \"Hello World\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void toJsonTest() {

        var lt = new LayoutTemplate(LT_NAME);
        var lr = new LayoutRow(LR_HEIGHT, Collections.singletonMap(PROP_KEY, PROP_VAL));
        var lc = new LayoutColumn(LC_SPAN, LC_HEIGHT, Collections.singletonMap(PROP_KEY, PROP_VAL));
        var lcr = new LayoutRow(LCR_HEIGHT, Collections.emptyMap());
        var comp = new LayoutComponent(LCOMP_DRAG_TYPE);
        lt.setStyle(Style.PAGE);
        lt.addLayoutProperty(PROP_KEY, PROP_VAL);
        lc.addRow(lcr);
        comp.addProperty(PROP_KEY, PROP_VAL);
        lc.add(comp);

        lr.add(Arrays.asList(lc));
        lt.addRow(lr);

        assertEquals(LT_JSON, LayoutTemplateJSONMarshaller.get().toJson(lt).toJson());
    }

    @Test
    public void fromJsonTest() {
        var lt = LayoutTemplateJSONMarshaller.get().fromJson(LT_JSON);
        checkLayoutTemplate(lt);
    }

    @Test
    public void fromReadableJsonTest() {
        var lt = LayoutTemplateJSONMarshaller.get().fromJson(LT_JSON_READABLE);
        checkLayoutTemplate(lt);
    }

    @Test
    public void fromGenerateRowJsonTest() {
        var lt = LayoutTemplateJSONMarshaller.get().fromJson(LT_JSON_GENERATE_ROW);
        assertEquals(LT_NAME, lt.getName());
        assertEquals(PROP_VAL, lt.getLayoutProperties().get(PROP_KEY));

        var layoutRow = lt.getRows().get(0);
        assertEquals(LayoutTemplateJSONMarshaller.DEFAULT_HEIGHT, layoutRow.getHeight());

        var layoutColumn = layoutRow.getLayoutColumns().get(0);
        assertEquals(LayoutTemplateJSONMarshaller.DEFAULT_SPAN, layoutColumn.getSpan());

        LayoutComponent layoutComponent = layoutColumn.getLayoutComponents().get(0);
        assertEquals(LCOMP_DRAG_TYPE, layoutComponent.getDragTypeName());
        assertEquals(PROP_VAL, layoutComponent.getProperties().get(PROP_KEY));
    }

    @Test
    public void htmlShortcutTest() {
        var lt = LayoutTemplateJSONMarshaller.get().fromJson(LT_JSON_HTML_SHORTCUT);
        var layoutRow = lt.getRows().get(0);
        var layoutColumn = layoutRow.getLayoutColumns().get(0);
        var layoutComponent = layoutColumn.getLayoutComponents().get(0);
        assertEquals(LayoutTemplateJSONMarshaller.HTML_DRAG_TYPE, layoutComponent.getDragTypeName());
        assertEquals("Hello World", layoutComponent.getProperties().get(LayoutTemplateJSONMarshaller.HTML_CODE_PROP));
    }

    public void checkLayoutTemplate(LayoutTemplate lt) {
        assertEquals(LT_NAME, lt.getName());
        assertEquals(PROP_VAL, lt.getLayoutProperties().get(PROP_KEY));

        var layoutRow = lt.getRows().get(0);
        assertEquals(LR_HEIGHT, layoutRow.getHeight());
        assertEquals(PROP_VAL, layoutRow.getProperties().get(PROP_KEY));

        var layoutColumn = layoutRow.getLayoutColumns().get(0);
        assertEquals(LC_HEIGHT, layoutColumn.getHeight());
        assertEquals(PROP_VAL, layoutColumn.getProperties().get(PROP_KEY));

        var lcr = layoutColumn.getRows().get(0);
        assertEquals(LCR_HEIGHT, lcr.getHeight());

        var layoutComponent = layoutColumn.getLayoutComponents().get(0);
        assertEquals(LCOMP_DRAG_TYPE, layoutComponent.getDragTypeName());
        assertEquals(PROP_VAL, layoutComponent.getProperties().get(PROP_KEY));
    }

    @Test
    public void dragComponentReplacementTest() {
        var object = Json.createObject();
        object.set("type", Json.create("HtmL"));
        var dragType = LayoutTemplateJSONMarshaller.get().findDragComponent(object);

        assertEquals("org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent",
                dragType);

        object.set("type", Json.create("displayer"));
        dragType = LayoutTemplateJSONMarshaller.get().findDragComponent(object);
        assertEquals("org.dashbuilder.client.editor.DisplayerDragComponent", dragType);
    }

    @Test
    public void dragComponentMissingTest() {
        var object = Json.createObject();
        var dragType = LayoutTemplateJSONMarshaller.get().findDragComponent(object);
        assertEquals("org.dashbuilder.client.editor.DisplayerDragComponent", dragType);
    }

    @Test
    public void legacyDragType() {
        var object = Json.createObject();
        object.set("dragTypeName", Json.create("custom"));
        var dragType = LayoutTemplateJSONMarshaller.get().findDragComponent(object);
        assertEquals("custom", dragType);
    }

    @Test
    public void findElementByShortcutTest() {
        final var PROP = "prop";
        final var SHORTCUT = "shortcut";
        final var DRAGTYPE = "drag";
        final var PROP_VAL = "short cut value";

        var object = Json.createObject();
        object.set(SHORTCUT, Json.create(PROP_VAL));

        var comp = LayoutTemplateJSONMarshaller.get().elementShortcut(object, SHORTCUT, PROP, DRAGTYPE).get();
        assertEquals(PROP_VAL, comp.getProperties().get(PROP));
        assertEquals(DRAGTYPE, comp.getDragTypeName());
    }

}
