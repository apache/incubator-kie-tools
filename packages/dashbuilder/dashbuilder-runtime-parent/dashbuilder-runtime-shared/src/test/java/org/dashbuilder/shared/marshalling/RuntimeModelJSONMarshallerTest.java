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
import java.util.List;

import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.shared.model.RuntimeModel;
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
                                        "  ]\n" +
                                        "}";

    @Test
    public void toJsonTest() {
        List<LayoutTemplate> templates = new ArrayList<>();

        templates.add(new LayoutTemplate("My Template"));
        RuntimeModel model = new RuntimeModel(new NavTreeBuilder().item("TestId", "TestItem", "Item Description", false).build(),
                                              templates,
                                              123l);

        String parsed = RuntimeModelJSONMarshaller.get().toJson(model).toJson();

        assertEquals(RUNTIME_MODEL_JSON, parsed);
    }

    @Test
    public void fromJsonTest() {
        RuntimeModel model = RuntimeModelJSONMarshaller.get().fromJson(RUNTIME_MODEL_JSON);

        NavItem item = model.getNavTree().getItemById("TestId");
        assertEquals(123l, model.getLastModified().longValue());
        assertEquals("My Template", model.getLayoutTemplates().get(0).getName());

        assertNotNull(item);
        assertEquals("TestItem", item.getName());
        assertEquals("Item Description", item.getDescription());

    }

}
