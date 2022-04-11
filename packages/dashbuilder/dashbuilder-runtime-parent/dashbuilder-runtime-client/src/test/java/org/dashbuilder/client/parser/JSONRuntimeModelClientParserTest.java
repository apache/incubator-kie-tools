package org.dashbuilder.client.parser;

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
    
    @Test
    public void testPropertiesReplacement() {
        var parser = new JSONRuntimeModelClientParser();
        var runtimeModel = parser.parse(RUNTIME_MODEL_JSON_WITH_PROPERTY);
        assertEquals("NEW VALUE", runtimeModel.getLayoutTemplates().get(0).getName());
    }

    @Test
    public void testPropertiesReplacementWithoutProperty() {
        var parser = new JSONRuntimeModelClientParser();
        var runtimeModel = parser.parse(RUNTIME_MODEL_JSON_WITHOUT_PROPERTY);
        assertEquals("${REPLACE_ME}", runtimeModel.getLayoutTemplates().get(0).getName());
    }

}