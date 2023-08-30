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

package org.dashbuilder.client.parser;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.GlobalDisplayerSettings;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JSONRuntimeModelClientParserTest {

    private String RUNTIME_MODEL_JSON_WITH_PROPERTY = "{\n" +
            "  \"layoutTemplates\": [\n" +
            "    {\n" +
            "      \"name\": \"${REPLACE_ME}\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"properties\": {\n" +
            "    \"REPLACE_ME\": \"NEW VALUE\"\n" +
            "  }\n" +
            "}";

    private String RUNTIME_MODEL_JSON_WITHOUT_PROPERTY = "{\n" +
            "  \"layoutTemplates\": [\n" +
            "    {\n" +
            "      \"name\": \"${REPLACE_ME}\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    JSONRuntimeModelClientParser parser;

    @Before
    public void setup() {
        parser = new JSONRuntimeModelClientParser();
        parser.replaceService = new PropertyReplacementService();
        parser.globalDisplayerSettings = new GlobalDisplayerSettings() {
            
            @Override
            public void setDisplayerSettings(DisplayerSettings settings) {
                // empty
                
            }
        };
    }

    @Test
    public void testPropertiesReplacement() {
        var runtimeModel = parser.parse(RUNTIME_MODEL_JSON_WITH_PROPERTY);
        assertEquals("NEW VALUE", runtimeModel.getLayoutTemplates().get(0).getName());
    }

    @Test
    public void testPropertiesReplacementWithoutProperty() {
        var runtimeModel = parser.parse(RUNTIME_MODEL_JSON_WITHOUT_PROPERTY);
        assertEquals("${REPLACE_ME}", runtimeModel.getLayoutTemplates().get(0).getName());
    }

}
