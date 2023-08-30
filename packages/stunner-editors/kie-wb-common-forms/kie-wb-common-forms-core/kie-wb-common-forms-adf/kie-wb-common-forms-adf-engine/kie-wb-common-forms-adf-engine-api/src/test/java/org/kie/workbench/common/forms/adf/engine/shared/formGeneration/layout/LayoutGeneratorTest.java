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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutSettings;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LayoutGeneratorTest {

    private LayoutGenerator generator = new LayoutGenerator();

    private Map<LayoutComponent, LayoutSettings> settingsMap = new LinkedHashMap<>();

    @Before
    public void initTest() {
        settingsMap.put(mock(LayoutComponent.class),
                        new LayoutSettings());
        settingsMap.put(mock(LayoutComponent.class),
                        new LayoutSettings());
        settingsMap.put(mock(LayoutComponent.class),
                        new LayoutSettings());
        settingsMap.put(mock(LayoutComponent.class),
                        new LayoutSettings());
        settingsMap.put(mock(LayoutComponent.class),
                        new LayoutSettings());
        settingsMap.put(mock(LayoutComponent.class),
                        new LayoutSettings());
    }

    @Test
    public void testSimpleVerticalLayout() {
        LayoutColumnDefinition[] structure = new LayoutColumnDefinition[]{
                new LayoutColumnDefinition()
        };

        generator.init(structure);

        settingsMap.entrySet().forEach(entry -> generator.addComponent(entry.getKey(),
                                                                       entry.getValue()));

        LayoutTemplate result = generator.build();

        assertNotNull(result);

        assertNotNull(result.getRows());

        assertEquals(settingsMap.size(),
                     result.getRows().size());

        result.getRows().forEach(row -> {
            checkSingleColumnRow(row);
        });
    }

    @Test
    public void testTwoColumnsLayout() {
        LayoutColumnDefinition[] structure = new LayoutColumnDefinition[]{
                new LayoutColumnDefinition(),
                new LayoutColumnDefinition()
        };

        generator.init(structure);

        settingsMap.entrySet().forEach(entry -> generator.addComponent(entry.getKey(),
                                                                       entry.getValue()));

        LayoutTemplate result = generator.build();

        assertNotNull(result);

        assertNotNull(result.getRows());

        assertEquals(settingsMap.size() / 2,
                     result.getRows().size());

        result.getRows().forEach(row -> {

            checkTwoColumnsRow(row);
        });
    }

    @Test
    public void testTwoColumnsLayoutWithSpans() {
        LayoutComponent component = mock(LayoutComponent.class);
        when(component.getDragTypeName()).thenReturn("span");
        LayoutSettings settings = new LayoutSettings();
        settings.setHorizontalSpan(2);
        settingsMap.put(component,
                        settings);

        component = mock(LayoutComponent.class);
        when(component.getDragTypeName()).thenReturn("span");
        settingsMap.put(component,
                        settings);

        LayoutColumnDefinition[] structure = new LayoutColumnDefinition[]{
                new LayoutColumnDefinition(),
                new LayoutColumnDefinition()
        };

        generator.init(structure);

        settingsMap.entrySet().forEach(entry -> generator.addComponent(entry.getKey(),
                                                                       entry.getValue()));

        LayoutTemplate result = generator.build();

        assertNotNull(result);

        assertNotNull(result.getRows());

        assertEquals(settingsMap.size() / 2 + 1,
                     result.getRows().size());

        result.getRows().forEach(row -> {
            assertNotNull(row);

            assertNotNull(row.getLayoutColumns());

            LayoutComponent layoutComponent = row.getLayoutColumns().get(0).getLayoutComponents().get(0);

            if ("span".equals(layoutComponent.getDragTypeName())) {
                checkSingleColumnRow(row);
            } else {
                checkTwoColumnsRow(row);
            }
        });
    }

    @Test
    public void testTwoColumnsWithWraps() {

        LayoutComponent component = mock(LayoutComponent.class);
        when(component.getDragTypeName()).thenReturn("wrap");
        LayoutSettings settings = new LayoutSettings();
        settings.setWrap(true);
        settingsMap.put(component,
                        settings);

        component = mock(LayoutComponent.class);
        when(component.getDragTypeName()).thenReturn("wrap");

        settingsMap.put(component,
                        settings);

        LayoutColumnDefinition[] structure = new LayoutColumnDefinition[]{
                new LayoutColumnDefinition(),
                new LayoutColumnDefinition()
        };

        generator.init(structure);

        settingsMap.entrySet().forEach(entry -> generator.addComponent(entry.getKey(),
                                                                       entry.getValue()));

        LayoutTemplate result = generator.build();

        assertNotNull(result);

        assertNotNull(result.getRows());

        assertEquals(settingsMap.size() / 2 + 1,
                     result.getRows().size());

        result.getRows().forEach(row -> {
            assertNotNull(row);

            assertNotNull(row.getLayoutColumns());

            LayoutComponent layoutComponent = row.getLayoutColumns().get(0).getLayoutComponents().get(0);

            if ("wrap".equals(layoutComponent.getDragTypeName())) {
                checkTwoColumnsRowWraps(row);
            } else {
                checkTwoColumnsRow(row);
            }
        });
    }

    @Test
    public void testLayoutWithEmptyColumn() {
        LayoutColumnDefinition[] structure = new LayoutColumnDefinition[]{
                new LayoutColumnDefinition()
        };

        generator.init(structure);

        LayoutTemplate template = generator.build();

        assertNotNull(template);

        assertTrue(template.getRows().isEmpty());
    }

    protected void checkSingleColumnRow(LayoutRow row) {
        assertNotNull(row);

        assertNotNull(row.getLayoutColumns());

        assertEquals(1,
                     row.getLayoutColumns().size());

        LayoutColumn column = row.getLayoutColumns().get(0);

        assertNotNull(column);

        assertEquals("12",
                     column.getSpan());

        assertNotNull(column.getLayoutComponents());

        assertEquals(1,
                     column.getLayoutComponents().size());

        assertTrue(settingsMap.containsKey(column.getLayoutComponents().get(0)));
    }

    protected void checkTwoColumnsRow(LayoutRow row) {
        assertNotNull(row);

        assertNotNull(row.getLayoutColumns());

        assertEquals(2,
                     row.getLayoutColumns().size());

        row.getLayoutColumns().forEach(column -> {
            assertNotNull(column);

            assertEquals("6",
                         column.getSpan());

            assertNotNull(column.getLayoutComponents());

            assertEquals(1,
                         column.getLayoutComponents().size());

            assertTrue(settingsMap.containsKey(column.getLayoutComponents().get(0)));
        });
    }

    protected void checkTwoColumnsRowWraps(LayoutRow row) {
        assertNotNull(row);

        assertNotNull(row.getLayoutColumns());

        assertEquals(2,
                     row.getLayoutColumns().size());

        LayoutColumn column = row.getLayoutColumns().get(0);

        assertNotNull(column);

        assertEquals("6",
                     column.getSpan());

        assertNotNull(column.getLayoutComponents());

        assertEquals(1,
                     column.getLayoutComponents().size());

        assertTrue(settingsMap.containsKey(column.getLayoutComponents().get(0)));

        // the first element is wrapped so check if the next column is empty
        column = row.getLayoutColumns().get(1);

        assertNotNull(column);

        assertEquals("6",
                     column.getSpan());

        assertNotNull(column.getLayoutComponents());

        assertNotNull(column.getLayoutComponents());

        assertEquals(0,
                     column.getLayoutComponents().size());
    }
}
