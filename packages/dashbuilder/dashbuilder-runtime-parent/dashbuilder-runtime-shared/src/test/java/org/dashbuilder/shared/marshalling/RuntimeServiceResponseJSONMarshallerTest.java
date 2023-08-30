/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.shared.marshalling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.GlobalSettings;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RuntimeServiceResponseJSONMarshallerTest {

    String JSON = "{\n" +
                  "  \"mode\": \"STATIC\",\n" +
                  "  \"availableModels\": [\n" +
                  "    \"rm1\",\n" +
                  "    \"rm2\"\n" +
                  "  ],\n" +
                  "  \"allowUpload\": true,\n" +
                  "  \"runtimeModel\": {\n" +
                  "    \"lastModified\": 123,\n" +
                  "    \"navTree\": {\n" +
                  "      \"root_items\": [\n" +
                  "        {\n" +
                  "          \"id\": \"TestId\",\n" +
                  "          \"type\": \"ITEM\",\n" +
                  "          \"name\": \"TestItem\",\n" +
                  "          \"description\": \"Item Description\",\n" +
                  "          \"modifiable\": false\n" +
                  "        }\n" +
                  "      ]\n" +
                  "    },\n" +
                  "    \"layoutTemplates\": [\n" +
                  "      {\n" +
                  "        \"style\": \"FLUID\",\n" +
                  "        \"name\": \"My Template\"\n" +
                  "      }\n" +
                  "    ]\n" +
                  "  }\n" +
                  "}";
    
    
    String JSON_NO_MODEL = "{\n" +
            "  \"mode\": \"STATIC\",\n" +
            "  \"allowUpload\": true,\n" +            
            "  \"availableModels\": [\n" +
            "    \"rm1\",\n" +
            "    \"rm2\"\n" +
            "  ]\n" +
            "}";

    @Test
    public void toJsonTest() {
        List<LayoutTemplate> templates = new ArrayList<>();
        templates.add(new LayoutTemplate("My Template"));
        RuntimeModel model = new RuntimeModel(new NavTreeBuilder().item("TestId", "TestItem", "Item Description", false).build(),
                                              templates,
                                              123l,
                                              Collections.emptyList(),
                                              Collections.emptyMap(),
                                              new GlobalSettings());
        RuntimeServiceResponse response = new RuntimeServiceResponse(DashbuilderRuntimeMode.STATIC,
                                                                     Optional.of(model),
                                                                     Arrays.asList("rm1", "rm2"),
                                                                     true);
        assertEquals(JSON, RuntimeServiceResponseJSONMarshaller.get().toJson(response).toJson());
    }

    @Test
    public void fromJsonTest() {
        RuntimeServiceResponse response = RuntimeServiceResponseJSONMarshaller.get().fromJson(JSON);
        assertEquals(DashbuilderRuntimeMode.STATIC, response.getMode());
        assertEquals(Arrays.asList("rm1", "rm2"), response.getAvailableModels());
        assertTrue(response.getRuntimeModelOp().isPresent());
        assertTrue(response.isAllowUpload());
        assertEquals(123l, response.getRuntimeModelOp().get().getLastModified().longValue());
    }

    @Test
    public void fromJsonNoModelTest() {
        RuntimeServiceResponse response = RuntimeServiceResponseJSONMarshaller.get().fromJson(JSON_NO_MODEL);
        assertFalse(response.getRuntimeModelOp().isPresent());
    }
    
}
