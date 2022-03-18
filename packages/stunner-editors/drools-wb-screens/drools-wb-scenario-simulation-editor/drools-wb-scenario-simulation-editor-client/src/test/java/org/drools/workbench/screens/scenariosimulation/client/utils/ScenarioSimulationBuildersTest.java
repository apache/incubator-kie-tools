/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.utils;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INSTANCE_TITLE_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.PLACEHOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.uberfire.ext.wires.core.grids.client.model.GridColumn.ColumnWidthMode;
import static org.uberfire.ext.wires.core.grids.client.model.GridColumn.HeaderMetaData;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationBuildersTest extends AbstractUtilsTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void testScenarioGridColumnBuilder() {
        ScenarioSimulationBuilders.ScenarioGridColumnBuilder builder = ScenarioSimulationBuilders.ScenarioGridColumnBuilder.get(scenarioCellTextAreaSingletonDOMElementFactoryMock, headerBuilderMock);
        builder.setPlaceHolder(PLACEHOLDER);
        ScenarioGridColumn retrieved = builder.build();
        assertNotNull(retrieved);
        assertEquals(PLACEHOLDER, retrieved.getPlaceHolder());
        assertTrue(retrieved.isReadOnly());
        assertFalse(retrieved.isMovable());
        assertNotNull(retrieved.getHeaderMetaData());
        assertFalse(retrieved.getHeaderMetaData().isEmpty());
        assertEquals(ColumnWidthMode.AUTO, retrieved.getColumnWidthMode());
    }

    @Test
    public void testHeaderBuilder() {
        ScenarioSimulationBuilders.HeaderBuilder builder = ScenarioSimulationBuilders.HeaderBuilder.get(scenarioHeaderTextBoxSingletonDOMElementFactoryMock);
        builder.setColumnTitle(COLUMN_INSTANCE_TITLE_FIRST);
        builder.setColumnGroup(COLUMN_GROUP_FIRST);
        builder.setInstanceHeader(true);
        List<HeaderMetaData> retrieved = builder.build();
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        ScenarioHeaderMetaData headerMetaData = (ScenarioHeaderMetaData) retrieved.get(0);
        assertNotNull(headerMetaData);
        assertEquals(COLUMN_INSTANCE_TITLE_FIRST, headerMetaData.getTitle());
        assertEquals(COLUMN_GROUP_FIRST, headerMetaData.getColumnGroup());
        assertTrue(headerMetaData.getMetadataType().equals(ScenarioHeaderMetaData.MetadataType.INSTANCE));
        assertFalse(headerMetaData.isReadOnly());
    }
}