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

    @Test
    public void toJsonTest() {

        LayoutTemplate lt = new LayoutTemplate(LT_NAME);

        lt.setStyle(Style.PAGE);
        lt.addLayoutProperty(PROP_KEY, PROP_VAL);
        LayoutRow lr = new LayoutRow(LR_HEIGHT, Collections.singletonMap(PROP_KEY, PROP_VAL));
        LayoutColumn lc = new LayoutColumn(LC_SPAN, LC_HEIGHT, Collections.singletonMap(PROP_KEY, PROP_VAL));
        LayoutRow lcr = new LayoutRow(LCR_HEIGHT, Collections.emptyMap());
        lc.addRow(lcr);
        LayoutComponent comp = new LayoutComponent(LCOMP_DRAG_TYPE);
        comp.addProperty(PROP_KEY, PROP_VAL);
        lc.add(comp);

        lr.add(Arrays.asList(lc));
        lt.addRow(lr);

        assertEquals(LT_JSON, LayoutTemplateJSONMarshaller.get().toJson(lt).toJson());
    }

    @Test
    public void fromJsonTest() {

        LayoutTemplate lt = LayoutTemplateJSONMarshaller.get().fromJson(LT_JSON);
        assertEquals(LT_NAME, lt.getName());
        assertEquals(PROP_VAL, lt.getLayoutProperties().get(PROP_KEY));

        LayoutRow layoutRow = lt.getRows().get(0);
        assertEquals(LR_HEIGHT, layoutRow.getHeight());
        assertEquals(PROP_VAL, layoutRow.getProperties().get(PROP_KEY));

        LayoutColumn layoutColumn = layoutRow.getLayoutColumns().get(0);
        assertEquals(LC_HEIGHT, layoutColumn.getHeight());
        assertEquals(PROP_VAL, layoutColumn.getProperties().get(PROP_KEY));

        LayoutRow lcr = layoutColumn.getRows().get(0);
        assertEquals(LCR_HEIGHT, lcr.getHeight());

        LayoutComponent layoutComponent = layoutColumn.getLayoutComponents().get(0);
        assertEquals(LCOMP_DRAG_TYPE, layoutComponent.getDragTypeName());
        assertEquals(PROP_VAL, layoutComponent.getProperties().get(PROP_KEY));

    }

}
