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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INSTANCE_TITLE_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_PROPERTY_TITLE_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.PLACEHOLDER;
import static org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy.SIMPLE_CLASSES_MAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationUtilsTest extends AbstractUtilsTest {

    @Test
    public void getScenarioGridColumn2() {
        final ScenarioGridColumn retrieved = ScenarioSimulationUtils.getScenarioGridColumn(COLUMN_INSTANCE_TITLE_FIRST, COLUMN_PROPERTY_TITLE_FIRST, COLUMN_ID, COLUMN_GROUP_FIRST, factMappingType, scenarioHeaderTextBoxSingletonDOMElementFactoryMock, scenarioCellTextAreaSingletonDOMElementFactoryMock, PLACEHOLDER);
        assertNotNull(retrieved);
    }

    @Test
    public void getScenarioGridColumn4() {
        final ScenarioGridColumn retrieved = ScenarioSimulationUtils.getScenarioGridColumn(headerBuilderMock, scenarioCellTextAreaSingletonDOMElementFactoryMock, PLACEHOLDER);
        assertNotNull(retrieved);
    }

    @Test
    public void getScenarioGridColumnBuilder() {
        final ScenarioSimulationBuilders.ScenarioGridColumnBuilder retrieved = ScenarioSimulationUtils.getScenarioGridColumnBuilder(scenarioCellTextAreaSingletonDOMElementFactoryMock, headerBuilderMock, PLACEHOLDER);
        assertNotNull(retrieved);
    }

    @Test
    public void getHeaderBuilder() {
        final ScenarioSimulationBuilders.HeaderBuilder retrieved = ScenarioSimulationUtils.getHeaderBuilder(COLUMN_INSTANCE_TITLE_FIRST, COLUMN_PROPERTY_TITLE_FIRST, COLUMN_ID, COLUMN_GROUP_FIRST, factMappingType, scenarioHeaderTextBoxSingletonDOMElementFactoryMock);
        assertNotNull(retrieved);
    }

    @Test
    public void getColumnWidth() {
        assertEquals(70, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Index.name()), 0);
        assertEquals(300, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Description.name()), 0);
        assertEquals(114, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Given.name()), 0);
        assertEquals(114, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Expected.name()), 0);
        assertEquals(114, ScenarioSimulationUtils.getColumnWidth(ExpressionIdentifier.NAME.Other.name()), 0);
    }

    @Test
    public void isSimpleJavaType() {
        SIMPLE_CLASSES_MAP.values().forEach(clazz -> assertTrue(ScenarioSimulationUtils.isSimpleJavaType(clazz.getCanonicalName())));
        assertFalse(ScenarioSimulationUtils.isSimpleJavaType("com.TestBean"));
    }

    @Test
    public void getPropertyNameElementsWithoutAlias() {
        FactIdentifier factIdentifierMock = mock(FactIdentifier.class);
        String packageName = "com.package";
        String className = "ClassName";
        String propertyName = "propertyName";
        String aliasName = "AliasName";
        when(factIdentifierMock.getClassName()).thenReturn(className);
        List<String> retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Collections.singletonList(propertyName), factIdentifierMock);
        assertEquals(Collections.singletonList(propertyName), retrieved);
        when(factIdentifierMock.getClassName()).thenReturn(packageName + "." + className);
        retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Collections.singletonList(propertyName), factIdentifierMock);
        assertEquals(Collections.singletonList(propertyName), retrieved);
        when(factIdentifierMock.getClassName()).thenReturn(className);
        retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Arrays.asList(className, propertyName), factIdentifierMock);
        assertEquals(Arrays.asList(className, propertyName), retrieved);
        when(factIdentifierMock.getClassName()).thenReturn(packageName + "." + className);
        retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Arrays.asList(aliasName, propertyName), factIdentifierMock);
        assertEquals(Arrays.asList(className, propertyName), retrieved);
    }

    @Test
    public void getPlaceholder() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.insertValue(),
                     ScenarioSimulationUtils.getPlaceholder(String.class.getCanonicalName()));

        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.dateFormatPlaceholder(),
                     ScenarioSimulationUtils.getPlaceholder(LocalDate.class.getCanonicalName()));
    }
}