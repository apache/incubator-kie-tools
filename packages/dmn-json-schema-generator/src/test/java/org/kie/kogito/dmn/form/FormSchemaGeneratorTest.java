/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.dmn.form;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormSchemaGeneratorTest {

    private FormSchemaGenerator tested = new FormSchemaGenerator();

    @Test
    public void testGenerate() throws IOException {
        final String dmnFile = Objects.requireNonNull(this.getClass().getResource("/test.dmn")).getFile();
        final ObjectNode expectedFormSchema = parseJsonFromFile("/formSchema.json");
        final Form form = tested.execute(dmnFile, "formUrl", "modelUrl", "swaggerUIUrl");

        assertEquals(expectedFormSchema, form.getSchema());
        assertEquals("test.dmn", form.getFilename());
        assertEquals("xls2dmn", form.getModelName());
        assertEquals("formUrl", form.getFormUrl());
        assertEquals("modelUrl", form.getModelUrl());
        assertEquals("swaggerUIUrl", form.getSwaggerUIUrl());
    }

    private ObjectNode parseJsonFromFile(final String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(Objects.requireNonNull(this.getClass().getResourceAsStream(filePath))).deepCopy();
    }
}
