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
package org.dashbuilder.dataset.json;

import java.util.Arrays;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataSetMetadataJSONMarshallerTest {

    String METADATA_JSON = "{\n" +
                           "  \"uuid\": \"abc\",\n" +
                           "  \"numberOfRows\": 2,\n" +
                           "  \"numberOfColumns\": 1,\n" +
                           "  \"columnIds\": [\n" +
                           "    \"TEST\"\n" +
                           "  ],\n" +
                           "  \"columnTypes\": [\n" +
                           "    \"TEXT\"\n" +
                           "  ],\n" +
                           "  \"estimatedSize\": 10,\n" +
                           "  \"definition\": {\n" +
                           "    \"provider\": \"BEAN\",\n" +
                           "    \"isPublic\": true,\n" +
                           "    \"cacheEnabled\": false,\n" +
                           "    \"cacheMaxRows\": 1000,\n" +
                           "    \"pushEnabled\": false,\n" +
                           "    \"pushMaxSize\": 1024,\n" +
                           "    \"refreshAlways\": false\n" +
                           "  }\n" +
                           "}";

    String METADATA_JSON_WITH_NULL_VALUES = "{\n" +
                                           "  \"definition\": {\n" +
                                           "    \"provider\": \"BEAN\",\n" +
                                           "    \"isPublic\": true,\n" +
                                           "    \"cacheEnabled\": false,\n" +
                                           "    \"cacheMaxRows\": 1000,\n" +
                                           "    \"pushEnabled\": false,\n" +
                                           "    \"pushMaxSize\": 1024,\n" +
                                           "    \"refreshAlways\": false\n" +
                                           "  }\n" +
                                           "}";

    private DataSetMetadataJSONMarshaller marshaller;

    @Before
    public void setup() {
        marshaller = new DataSetMetadataJSONMarshaller(DataSetDefJsonTest.jsonMarshaller);
    }

    @Test
    public void toJsonTest() {
        DataSetMetadataImpl meta = new DataSetMetadataImpl(new BeanDataSetDef(),
                                                           "abc",
                                                           2,
                                                           1,
                                                           Arrays.asList("TEST"),
                                                           Arrays.asList(ColumnType.TEXT),
                                                           10);
        assertEquals(METADATA_JSON, marshaller.toJson(meta));
    }

    @Test
    public void fromJsonTest() throws Exception {
        DataSetMetadata meta = marshaller.fromJSON(METADATA_JSON);

        assertEquals("abc", meta.getUUID());
        assertEquals(2, meta.getNumberOfRows());
        assertEquals(1, meta.getNumberOfColumns());
        assertEquals(Arrays.asList("TEST"), meta.getColumnIds());
        assertEquals(Arrays.asList(ColumnType.TEXT), meta.getColumnTypes());
        assertEquals(10, meta.getEstimatedSize());
        assertNotNull(meta.getDefinition());
    }
    
    @Test
    public void toJsonWithNullValuesTest() {
        DataSetMetadata meta = marshaller.fromJSON(METADATA_JSON_WITH_NULL_VALUES);
        assertEquals(null, meta.getUUID());
        assertEquals(0, meta.getNumberOfRows());
        assertEquals(0, meta.getNumberOfColumns());
        assertTrue(meta.getColumnIds().isEmpty());
        assertTrue(meta.getColumnTypes().isEmpty());
        assertEquals(0, meta.getEstimatedSize());
        assertNotNull(meta.getDefinition());
    }

}
