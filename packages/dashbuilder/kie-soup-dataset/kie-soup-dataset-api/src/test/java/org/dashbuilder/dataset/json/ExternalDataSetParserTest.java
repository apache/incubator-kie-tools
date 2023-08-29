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

package org.dashbuilder.dataset.json;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSetFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExternalDataSetParserTest {

    final static String METADATA = "{\n" +
            "   \"columns\":[\n" +
            "      {\n" +
            "         \"id\":\"CL1\",\n" +
            "         \"type\":\"NUMBER\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL2\",\n" +
            "         \"type\":\"LABEL\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL3\",\n" +
            "         \"type\":\"TEXT\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL4\",\n" +
            "         \"type\":\"DATE\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"numberOfRows\":2\n" +
            "}";

    final static String METADATA_WITHOUT_COLUMNS = "{\n" +
            "   \"numberOfRows\":6\n" +
            "}";

    final static String DATASET = "{\n" +
            "   \"columns\":[\n" +
            "      {\n" +
            "         \"id\":\"CL1\",\n" +
            "         \"type\":\"NUMBER\",\n" +
            "         \"name\":\"Column 1\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL2\",\n" +
            "         \"type\":\"LABEL\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL3\",\n" +
            "         \"type\":\"TEXT\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL4\",\n" +
            "         \"type\":\"date\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"values\":[\n" +
            "      [ \"1.0\", \"L1\", \"JOHN\", \"2021-10-16T01:27:19.430Z\" ],\n" +
            "      [ \"9.0\", \"L2\", \"MARY\", \"2021-10-16T01:48:15.647Z\" ]\n" +
            "   ]\n" +
            "}";

    final static String DATASET_NO_ROWS = "{\n" +
            "   \"columns\":[\n" +
            "      {\n" +
            "         \"id\":\"CL1\",\n" +
            "         \"type\":\"NUMBER\",\n" +
            "         \"name\":\"Column 1\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL2\",\n" +
            "         \"type\":\"LABEL\",\n" +
            "         \"name\":\"Column 2\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL3\",\n" +
            "         \"type\":\"TEXT\",\n" +
            "         \"name\":\"Column 3\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"CL4\",\n" +
            "         \"type\":\"DATE\",\n" +
            "         \"name\":\"Column 4\"\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    final static String DATASET_MATRIX = "[\n" +
            "      [ \"1.0\", \"L1\", \"JOHN\", \"2021-10-16T01:27:19.430Z\" ],\n" +
            "      [ \"9.0\", \"L2\", \"MARY\", \"2021-10-16T01:48:15.647Z\" ]\n" +
            "   ]\n";

    final static String DATASET_ARRAY =
            "[ \"1.0\", \"L1\", \"JOHN\", \"2021-10-16T01:27:19.430Z\" ]";

    final static String DATASET_WITH_INCOMPATIBLE_VALUE = "{\n" +
            "   \"columns\":[\n" +
            "      {\n" +
            "         \"id\":\"CL1\",\n" +
            "         \"type\":\"NUMBER\",\n" +
            "         \"name\":\"Column 1\"\n" +
            "      }" +
            "   ],\n" +
            "   \"values\":[\n" +
            "      [ \"1.0\"],\n" +
            "      [ \"non number value\"]\n" +
            "   ]\n" +
            "}";

    private ExternalDataSetJSONParser parser;

    @Before
    public void setup() {
        parser = new ExternalDataSetJSONParser(value -> {
            var temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(value);
            var instant = Instant.from(temporalAccessor);
            return Date.from(instant);
        });
    }

    @Test
    public void testParseMetadata() {
        var metadata = parser.parseMetadata(METADATA);
        assertEquals(2, metadata.getNumberOfRows());
        assertEquals(-1, metadata.getEstimatedSize());
        assertEquals(4, metadata.getNumberOfColumns());
        assertEquals(4, metadata.getColumnIds().size());
        assertEquals(4, metadata.getColumnTypes().size());
        assertArrayEquals(new String[]{"CL1", "CL2", "CL3", "CL4"},
                metadata.getColumnIds().toArray());
        assertArrayEquals(new ColumnType[]{ColumnType.NUMBER, ColumnType.LABEL, ColumnType.TEXT, ColumnType.DATE},
                metadata.getColumnTypes().stream().toArray());
    }

    @Test
    public void testParseMetadataWithoutColumns() {
        var metadata = parser.parseMetadata(METADATA_WITHOUT_COLUMNS);
        assertEquals(-1, metadata.getEstimatedSize());
        assertEquals(0, metadata.getNumberOfColumns());
        assertEquals(6, metadata.getNumberOfRows());
    }

    @Test
    public void testParseEmptyMetadata() {
        var metadata = parser.parseMetadata("{}");
        assertEquals(-1, metadata.getEstimatedSize());
        assertEquals(0, metadata.getNumberOfColumns());
        assertEquals(0, metadata.getNumberOfRows());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseNullMetadata() {
        parser.parseMetadata(null);
    }

    @Test
    public void testParseDataSetMatrix() {
        var dataset = parser.parseDataSet(DATASET_MATRIX);

        assertEquals(4, dataset.getColumns().size());
        assertEquals(2, dataset.getRowCount());

        assertArrayEquals(new ColumnType[]{ColumnType.NUMBER, ColumnType.LABEL, ColumnType.LABEL, ColumnType.DATE},
                dataset.getColumns().stream().map(DataColumn::getColumnType).toArray());
        assertArrayEquals(new String[]{"Column 0", "Column 1", "Column 2", "Column 3"},
                dataset.getColumns().stream().map(DataColumn::getId).toArray());

        assertArrayEquals(new Object[]{1.0, 9.0},
                dataset.getColumnById("Column 0").getValues().toArray());

        assertArrayEquals(new Object[]{"L1", "L2"},
                dataset.getColumnById("Column 1").getValues().toArray());

        assertArrayEquals(new Object[]{"JOHN", "MARY"},
                dataset.getColumnById("Column 2").getValues().toArray());

        var d1 = parser.convertToDate("2021-10-16T01:27:19.430Z");
        var d2 = parser.convertToDate("2021-10-16T01:48:15.647Z");
        assertArrayEquals(new Object[]{d1, d2},
                dataset.getColumnById("Column 3").getValues().toArray());

        assertEquals(1.0, dataset.getValueAt(0, "Column 0"));
        assertEquals("MARY", dataset.getValueAt(1, "Column 2"));
        assertEquals(d2, dataset.getValueAt(1, "Column 3"));
    }

    @Test
    public void testParseDataSetArray() {
        var dataset = parser.parseDataSet(DATASET_ARRAY);
        assertEquals(4, dataset.getColumns().size());
        assertEquals(1, dataset.getRowCount());
    }

    @Test
    public void testParseDataSetObject() {
        var dataset = parser.parseDataSet(DATASET);

        assertEquals(4, dataset.getColumns().size());
        assertEquals(2, dataset.getRowCount());

        assertArrayEquals(new ColumnType[]{ColumnType.NUMBER, ColumnType.LABEL, ColumnType.TEXT, ColumnType.DATE},
                dataset.getColumns().stream().map(DataColumn::getColumnType).toArray());
        assertArrayEquals(new String[]{"CL1", "CL2", "CL3", "CL4"},
                dataset.getColumns().stream().map(DataColumn::getId).toArray());

        assertArrayEquals(new Object[]{1.0, 9.0},
                dataset.getColumnById("CL1").getValues().toArray());

        assertArrayEquals(new Object[]{"L1", "L2"},
                dataset.getColumnById("CL2").getValues().toArray());

        assertArrayEquals(new Object[]{"JOHN", "MARY"},
                dataset.getColumnById("CL3").getValues().toArray());

        var d1 = parser.convertToDate("2021-10-16T01:27:19.430Z");
        var d2 = parser.convertToDate("2021-10-16T01:48:15.647Z");
        assertArrayEquals(new Object[]{d1, d2},
                dataset.getColumnById("CL4").getValues().toArray());

        assertEquals(1.0, dataset.getValueAt(0, "CL1"));
        assertEquals("MARY", dataset.getValueAt(1, "CL3"));
        assertEquals(d2, dataset.getValueAt(1, "CL4"));
    }

    @Test
    public void testParseNowRowsDataSet() {
        var dataset = parser.parseDataSet(DATASET_NO_ROWS);

        assertEquals(4, dataset.getColumns().size());
        assertEquals(0, dataset.getRowCount());

        assertArrayEquals(new String[]{"NUMBER", "LABEL", "TEXT", "DATE"},
                dataset.getColumns().stream().map(DataColumn::getColumnType).map(ColumnType::name).toArray());

        assertArrayEquals(new String[]{"CL1", "CL2", "CL3", "CL4"},
                dataset.getColumns().stream().map(DataColumn::getId).toArray());
    }

    @Test
    public void testParseEmptyDataSetArray() {
        var dataset = parser.parseDataSet("[ ]");
        assertEquals(0, dataset.getColumns().size());
        assertEquals(0, dataset.getRowCount());
    }

    @Test
    public void testParseEmptyDataSetObject() {
        var dataset = parser.parseDataSet("{}");
        assertEquals(0, dataset.getColumns().size());
        assertEquals(0, dataset.getRowCount());
    }

    @Test
    public void testParseNullDataSet() {
        var dataset = parser.parseDataSet(null);
        assertEquals(0, dataset.getColumns().size());
        assertEquals(0, dataset.getRowCount());
    }

    @Test
    public void testDataSetToJsonArray() {
        var d1 = new Date(0l);
        var d2 = new Date(1l);
        var dataset = DataSetFactory.newDataSetBuilder()
                .label("C1")
                .label("C2")
                .label("C3")
                .row("A", 1.0, d1)
                .row("B", 2.0, d2)
                .buildDataSet();
        ;
        var result = parser.toJsonArray(dataset);
        var parsedDataSet = parser.parseDataSet(result);

        assertEquals("A", parsedDataSet.getValueAt(0, 0));
        assertEquals(1.0, parsedDataSet.getValueAt(0, 1));
        assertEquals(d1.toString(), parsedDataSet.getValueAt(0, 2));

        assertEquals("B", parsedDataSet.getValueAt(1, 0));
        assertEquals(2.0, parsedDataSet.getValueAt(1, 1));
        assertEquals(d2.toString(), parsedDataSet.getValueAt(1, 2));
        assertEquals(2, parsedDataSet.getRowCount());
        assertEquals(3, parsedDataSet.getColumns().size());
    }

    @Test
    public void testDataSetToJsonArrayContent() {
        var dataset = DataSetFactory.newDataSetBuilder()
                .label("C1")
                .label("C2")
                .row("A", 1.0)
                .row("B", 2.0)
                .buildDataSet();
        var result = parser.toJsonArray(dataset);
        assertEquals("[[\"A\",\"1.0\"],[\"B\",\"2.0\"]]", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIncompatibleColumnDataSet() {
        parser.parseDataSet(DATASET_WITH_INCOMPATIBLE_VALUE);

    }

}
