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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INSTANCE_TITLE_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_PROPERTY_TITLE_FIRST;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.PLACEHOLDER;
import static org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy.SIMPLE_CLASSES_MAP;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.DMN_DATE;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.LOCALDATETIME_CANONICAL_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.LOCALDATE_CANONICAL_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.LOCALTIME_CANONICAL_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationUtilsTest extends AbstractUtilsTest {

    @Test
    public void getColumnSubGroup() {
        String subGroup = ScenarioSimulationUtils.getColumnSubGroup(COLUMN_GROUP);
        assertEquals(subGroup, COLUMN_GROUP + "-0");
        String subGroup2 = ScenarioSimulationUtils.getColumnSubGroup(COLUMN_GROUP);
        assertEquals(subGroup2, COLUMN_GROUP + "-1");
    }

    @Test
    public void getOriginalColumnGroup() {
        String subGroup = ScenarioSimulationUtils.getOriginalColumnGroup(COLUMN_GROUP + "-3");
        assertEquals(COLUMN_GROUP, subGroup);
    }

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
        String packageName = "com";
        String className = "ClassName";
        String fullClassName = packageName + "." + className;
        String propertyName = "propertyName";
        String aliasName = "AliasName";
        when(factIdentifierMock.getClassName()).thenReturn(fullClassName);
        when(factIdentifierMock.getClassNameWithoutPackage()).thenReturn(className);
        List<String> retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Collections.singletonList(propertyName), factIdentifierMock, ScenarioSimulationModel.Type.RULE);
        assertEquals(Collections.singletonList(propertyName), retrieved);
        retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Collections.singletonList(propertyName), factIdentifierMock, ScenarioSimulationModel.Type.DMN);
        assertEquals(Collections.singletonList(propertyName), retrieved);
        retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Arrays.asList(aliasName, propertyName), factIdentifierMock, ScenarioSimulationModel.Type.RULE);
        assertEquals(Arrays.asList(className, propertyName), retrieved);
        retrieved = ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias(Arrays.asList(aliasName, propertyName), factIdentifierMock, ScenarioSimulationModel.Type.DMN);
        assertEquals(Arrays.asList(fullClassName, propertyName), retrieved);
    }

    @Test
    public void getPlaceholder_InstanceNotAssigned() {
        String placeholder = ScenarioSimulationUtils.getPlaceHolder(false, false, FactMappingValueType.NOT_EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(false, true, FactMappingValueType.NOT_EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(false, false, FactMappingValueType.EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(false, true, FactMappingValueType.EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(), placeholder);
    }

    @Test
    public void getPlaceholder_InstanceAssignedPropertyNot() {
        String placeholder = ScenarioSimulationUtils.getPlaceHolder(true, false, FactMappingValueType.NOT_EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(true, false, FactMappingValueType.EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(), placeholder);
    }

    @Test
    public void getPlaceholder_InstanceAndPropertyAssigned() {
        String placeholder = ScenarioSimulationUtils.getPlaceHolder(true, true, FactMappingValueType.EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.insertExpression(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(true, true, FactMappingValueType.NOT_EXPRESSION, "com.Test");
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.insertValue(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(true, true, FactMappingValueType.NOT_EXPRESSION, LOCALDATE_CANONICAL_NAME);
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.dateFormatPlaceholder(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(true, true, FactMappingValueType.NOT_EXPRESSION, LOCALDATETIME_CANONICAL_NAME);
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.dateTimeFormatPlaceholder(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(true, true, FactMappingValueType.NOT_EXPRESSION, LOCALTIME_CANONICAL_NAME);
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.timeFormatPlaceholder(), placeholder);
        placeholder = ScenarioSimulationUtils.getPlaceHolder(true, true, FactMappingValueType.NOT_EXPRESSION, DMN_DATE);
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.dmnDateFormatPlaceholder(), placeholder);
    }
}