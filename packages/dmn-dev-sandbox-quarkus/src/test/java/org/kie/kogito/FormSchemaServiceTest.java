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

package org.kie.kogito;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.form.FormSchemaService;
import org.kie.kogito.form.FormSchemaServiceImpl;
import org.kie.kogito.model.Form;

import static org.junit.Assert.assertEquals;

public class FormSchemaServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String RESOURCES_FOLDER = "src/test/resources";
    private static final String MODEL_FOLDER = "model";

    private final FormSchemaService service = new FormSchemaServiceImpl();

    @Test
    public void testGenerate() throws IOException {
        final ObjectNode expectedLoanSchema = parseJsonFromFile("/schema/loan.json");
        final ObjectNode expectedTrafficSchema = parseJsonFromFile("/schema/traffic.json");

        final List<String> modelsPath = List.of(
                Paths.get(RESOURCES_FOLDER, MODEL_FOLDER, "loan.dmn").toAbsolutePath().toString(),
                Paths.get(RESOURCES_FOLDER, MODEL_FOLDER, "traffic.dmn").toAbsolutePath().toString()
        );

        final List<Form> forms = service.generate(RESOURCES_FOLDER, modelsPath);

        assertEquals(2, forms.size());

        assertEquals("/model/loan.dmn", forms.get(0).getUri());
        assertEquals("/model/traffic.dmn", forms.get(1).getUri());

        assertEquals("loan_pre_qualification", forms.get(0).getModelName());
        assertEquals("Traffic Violation", forms.get(1).getModelName());

        assertJsonEquals(expectedLoanSchema, forms.get(0).getSchema());
        assertJsonEquals(expectedTrafficSchema, forms.get(1).getSchema());
    }

    private void assertJsonEquals(final ObjectNode expected, final ObjectNode actual) throws JsonProcessingException {
        assertEquals(mapper.readTree(expected.toString()), mapper.readTree(actual.toString()));
    }

    private ObjectNode parseJsonFromFile(final String filePath) throws IOException {
        return mapper.readTree(Objects.requireNonNull(this.getClass().getResourceAsStream(filePath))).deepCopy();
    }
}
