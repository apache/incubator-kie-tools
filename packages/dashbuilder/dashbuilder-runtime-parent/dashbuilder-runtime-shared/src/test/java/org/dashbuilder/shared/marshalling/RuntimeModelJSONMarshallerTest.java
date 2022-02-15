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

import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.impl.ExternalDataSetDefBuilderImpl;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.shared.model.RuntimeModel;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RuntimeModelJSONMarshallerTest {

    private String RUNTIME_MODEL_JSON = "{\n" + 
            "  \"lastModified\": 123,\n" + 
            "  \"navTree\": {\n" + 
            "    \"root_items\": [\n" + 
            "      {\n" + 
            "        \"id\": \"TestId\",\n" + 
            "        \"type\": \"ITEM\",\n" + 
            "        \"name\": \"TestItem\",\n" + 
            "        \"description\": \"Item Description\",\n" + 
            "        \"modifiable\": false\n" + 
            "      }\n" + 
            "    ]\n" + 
            "  },\n" + 
            "  \"layoutTemplates\": [\n" + 
            "    {\n" + 
            "      \"style\": \"FLUID\",\n" + 
            "      \"name\": \"My Template\"\n" + 
            "    }\n" + 
            "  ],\n" + 
            "  \"datasets\": [\n" + 
            "    {\n" + 
            "      \"uuid\": \"123\",\n" + 
            "      \"provider\": \"External\",\n" + 
            "      \"isPublic\": true,\n" + 
            "      \"cacheEnabled\": true,\n" + 
            "      \"cacheMaxRows\": 1000,\n" + 
            "      \"pushEnabled\": false,\n" + 
            "      \"pushMaxSize\": 1024,\n" + 
            "      \"refreshAlways\": false,\n" + 
            "      \"dynamic\": false,\n" + 
            "      \"url\": \"http://acme.com\"\n" + 
            "    }\n" + 
            "  ]\n" + 
            "}";

    RuntimeModelJSONMarshaller marshaller;

    @Before
    public void setup() {
        marshaller = RuntimeModelJSONMarshaller.get();
    }

    @Test
    public void toJsonTest() {
        var templates = new ArrayList<LayoutTemplate>();
        var externalDefs = new ArrayList<ExternalDataSetDef>();

        templates.add(new LayoutTemplate("My Template"));
        externalDefs.add((ExternalDataSetDef) new ExternalDataSetDefBuilderImpl()
                .uuid("123")
                .cacheOn(1000)
                .url("http://acme.com")
                .buildDef());

        var model = new RuntimeModel(new NavTreeBuilder().item("TestId", "TestItem", "Item Description", false).build(),
                templates,
                123l,
                externalDefs);

        var parsed = marshaller.toJson(model).toJson();
        assertEquals(RUNTIME_MODEL_JSON, parsed);
    }

    @Test
    public void fromJsonTest() {
        var model = marshaller.fromJson(RUNTIME_MODEL_JSON);

        var item = model.getNavTree().getItemById("TestId");
        assertEquals(123l, model.getLastModified().longValue());
        assertEquals("My Template", model.getLayoutTemplates().get(0).getName());

        assertNotNull(item);
        assertEquals("TestItem", item.getName());
        assertEquals("Item Description", item.getDescription());

        assertEquals("123", model.getClientDataSets().get(0).getUUID());
        assertEquals("http://acme.com", model.getClientDataSets().get(0).getUrl());
    }

}
