/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static java.util.Collections.singletonList;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_IDENTIFIER_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PACKAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PROPERTY_NAME_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PROPERTY_PATH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PROPERTY_PATH_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LIST_CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LIST_PROPERTY_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.PROPERTY_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractSelectedColumnCommandTest extends AbstractScenarioGridCommandTest {

    @Mock
    protected List<GridColumn<?>> gridColumnsMock;
    @Mock
    protected FactModelTree factModelTreeMock;

    public void setup() {
        super.setup();

        when(gridColumnsMock.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);
        when(factModelTreeMock.getExpandableProperties()).thenReturn(mock(SortedMap.class));
        when(dataObjectFieldsMapMock.get(anyString())).thenReturn(factModelTreeMock);

        scenarioSimulationContextLocal.getStatus().setFullPackage(FULL_PACKAGE);
        scenarioSimulationContextLocal.getStatus().setValue(MULTIPART_VALUE);
        scenarioSimulationContextLocal.getStatus().setValueClassName(VALUE_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setColumnId(COLUMN_ID);
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContextLocal.getStatus().setPropertyNameElements(MULTIPART_VALUE_ELEMENTS);
        scenarioSimulationContextLocal.getStatus().setClassName(CLASS_NAME);
    }

    @Test
    public void executeIfSelected() {
        commandSpy.execute(scenarioSimulationContextLocal);
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
    }

    @Test
    public void executeIfSelected_NoColumn() {
        gridColumnMock = null;
        commandSpy.execute(scenarioSimulationContextLocal);
        verify((AbstractSelectedColumnCommand) commandSpy, never()).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
    }

    /* Tests related to insertNewColumn method */

    @Test
    public void insertNewColumn_NotToClone() {
        insertNewColumnCommon(COLUMN_INDEX, false);
    }

    @Test
    public void insertNewColumn_ToClone() {
        this.insertNewColumnCommon(COLUMN_INDEX, true);
    }

    protected void insertNewColumnCommon(int columnIndex, boolean cloneInstance) {
        int instanceNum = simulationDescriptorMock.getUnmodifiableFactMappings().size();
        ScenarioGridColumn createdColumn = ((AbstractSelectedColumnCommand) commandSpy).insertNewColumn(scenarioSimulationContextLocal, gridColumnMock, columnIndex, cloneInstance);
        String columnGroup = gridColumnMock.getInformationHeaderMetaData().getColumnGroup();
        String originalInstanceTitle = gridColumnMock.getInformationHeaderMetaData().getTitle();
        String instanceTitle = cloneInstance ? originalInstanceTitle : "INSTANCE " + instanceNum;
        String propertyTitle = "PROPERTY " + instanceNum;
        final FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        verify(commandSpy, times(1)).getScenarioGridColumnLocal(
                eq(instanceTitle),
                eq(propertyTitle),
                anyString(),
                eq(columnGroup),
                eq(factMappingType),
                eq(scenarioHeaderTextBoxSingletonDOMElementFactorySpy),
                eq(scenarioCellTextAreaSingletonDOMElementFactorySpy),
                eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        if (cloneInstance) {
            verify(createdColumn, times(1)).setFactIdentifier(eq(gridColumnMock.getFactIdentifier()));
        } else {
            verify(createdColumn, never()).setFactIdentifier(any());
        }
        verify(createdColumn, times(1)).setInstanceAssigned(eq(cloneInstance));
        verify(scenarioGridModelMock, times(1)).insertColumn(COLUMN_INDEX, createdColumn);
    }

    /* Tests related to setPropertyHeader method */
    @Test
    public void executeKeepDataFalseDMN() {
        scenarioSimulationContextLocal.getStatus().setKeepData(false);
        settingsLocal.setType(ScenarioSimulationModel.Type.DMN);
        commonSetPropertyHeader(ScenarioSimulationModel.Type.DMN, false, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME);
    }

    @Test
    public void executeKeepDataFalseRule() {
        scenarioSimulationContextLocal.getStatus().setKeepData(false);
        commonSetPropertyHeader(ScenarioSimulationModel.Type.RULE, false, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME);
    }

    @Test
    public void executeKeepDataTrue() {
        scenarioSimulationContextLocal.getStatus().setKeepData(true);
        commonSetPropertyHeader(ScenarioSimulationModel.Type.RULE, true, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME);
    }

    @Test
    public void executeWithPropertyAsCollection() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        doReturn(factIdentifierMock).when(((AbstractSelectedColumnCommand) commandSpy)).setEditableHeadersAndGetFactIdentifier(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(CLASS_NAME), eq(FULL_CLASS_NAME));
        ((AbstractSelectedColumnCommand) commandSpy).setPropertyHeader(scenarioSimulationContextLocal, gridColumnMock, FULL_CLASS_NAME, FULL_PROPERTY_PATH_ELEMENTS, LIST_CLASS_NAME);
        verify(((AbstractSelectedColumnCommand) commandSpy), times(2)).setInstanceHeaderMetaData(eq(gridColumnMock), eq(CLASS_NAME), eq(factIdentifierMock));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(eq(LIST_PROPERTY_NAME));
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(eq(false));
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(LIST_CLASS_NAME), anyBoolean(), isA(FactMappingValueType.class), eq(ScenarioSimulationModel.Type.RULE));
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).manageCollectionProperty(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(CLASS_NAME), eq(0), eq(FULL_PROPERTY_PATH_ELEMENTS));
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).navigateComplexObject(eq(factModelTreeMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
    }

    @Test
    public void executePropertyWithSameInstance() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        when(gridColumnMock.isInstanceAssigned()).thenReturn(true);
        when(gridColumnMock.getInformationHeaderMetaData().getTitle()).thenReturn(CLASS_NAME);
        ((AbstractSelectedColumnCommand) commandSpy).setPropertyHeader(scenarioSimulationContextLocal, gridColumnMock, FULL_CLASS_NAME, FULL_PROPERTY_PATH_ELEMENTS, LIST_CLASS_NAME);
        verify(((AbstractSelectedColumnCommand) commandSpy), never()).setInstanceHeaderMetaData(eq(gridColumnMock), eq(CLASS_NAME), eq(factIdentifierMock));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(eq(LIST_PROPERTY_NAME));
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(eq(false));
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(LIST_CLASS_NAME), anyBoolean(), isA(FactMappingValueType.class), eq(ScenarioSimulationModel.Type.RULE));
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).manageCollectionProperty(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(CLASS_NAME), eq(0), eq(FULL_PROPERTY_PATH_ELEMENTS));
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).navigateComplexObject(eq(factModelTreeMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
    }

    @Test
    public void executePropertyWithPropertyTitle() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        when(gridColumnMock.isInstanceAssigned()).thenReturn(true);
        when(gridColumnMock.getInformationHeaderMetaData().getTitle()).thenReturn(CLASS_NAME);
        ((AbstractSelectedColumnCommand) commandSpy).setPropertyHeader(scenarioSimulationContextLocal, gridColumnMock, FULL_CLASS_NAME, FULL_PROPERTY_PATH_ELEMENTS, LIST_CLASS_NAME, PROPERTY_NAME);
        verify(((AbstractSelectedColumnCommand) commandSpy), never()).setInstanceHeaderMetaData(eq(gridColumnMock), eq(CLASS_NAME), eq(factIdentifierMock));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(eq(PROPERTY_NAME));
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(eq(false));
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(LIST_CLASS_NAME), anyBoolean(), isA(FactMappingValueType.class), eq(ScenarioSimulationModel.Type.RULE));
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).manageCollectionProperty(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(CLASS_NAME), eq(0), eq(FULL_PROPERTY_PATH_ELEMENTS));
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).navigateComplexObject(eq(factModelTreeMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void executePropertyWithWrongInstance() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        when(gridColumnMock.isInstanceAssigned()).thenReturn(true);
        ((AbstractSelectedColumnCommand) commandSpy).setPropertyHeader(scenarioSimulationContextLocal, gridColumnMock, FULL_CLASS_NAME, FULL_PROPERTY_PATH_ELEMENTS, LIST_CLASS_NAME);
        verify(((AbstractSelectedColumnCommand) commandSpy), never()).setInstanceHeaderMetaData(eq(gridColumnMock), eq(CLASS_NAME), eq(factIdentifierMock));
        verify(propertyHeaderMetaDataMock, never()).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, never()).setTitle(anyString());
        verify(propertyHeaderMetaDataMock, never()).setReadOnly(anyBoolean());
        verify(scenarioGridModelMock, never()).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(LIST_CLASS_NAME), anyBoolean(), isA(FactMappingValueType.class), eq(ScenarioSimulationModel.Type.RULE));
        verify((AbstractSelectedColumnCommand) commandSpy, never()).manageCollectionProperty(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(CLASS_NAME), eq(0), eq(FULL_PROPERTY_PATH_ELEMENTS));
        verify((AbstractSelectedColumnCommand) commandSpy, never()).navigateComplexObject(eq(factModelTreeMock), eq(FULL_PROPERTY_PATH_ELEMENTS), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
    }

    @Test
    public void setInstanceHeaderMetaData() {
        ((AbstractSelectedColumnCommand) commandSpy).setInstanceHeaderMetaData(gridColumnMock, FACT_NAME, factIdentifierMock);
        verify(((AbstractSelectedColumnCommand) commandSpy)).setInstanceHeaderMetaData(eq(gridColumnMock), eq(FACT_NAME), eq(factIdentifierMock));
        verify(gridColumnMock, times(1)).setInstanceAssigned(true);
        verify(gridColumnMock, times(1)).setFactIdentifier(eq(factIdentifierMock));
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(FACT_NAME));
    }

    @Test
    public void getFullPackage() {
        scenarioSimulationContextLocal.getStatus().setFullPackage(FULL_PACKAGE);
        String fullPackage = ((AbstractSelectedColumnCommand) commandSpy).getFullPackage(scenarioSimulationContextLocal);
        assertEquals(fullPackage, FULL_PACKAGE + ".");
        scenarioSimulationContextLocal.getStatus().setFullPackage("");
        fullPackage = ((AbstractSelectedColumnCommand) commandSpy).getFullPackage(scenarioSimulationContextLocal);
        assertEquals("", fullPackage);
        scenarioSimulationContextLocal.getStatus().setFullPackage(null);
        fullPackage = ((AbstractSelectedColumnCommand) commandSpy).getFullPackage(scenarioSimulationContextLocal);
        assertEquals("", fullPackage);
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    public void manageCollectionProperty() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        final List<String> fullPropertyPathElements = Arrays.asList(FULL_PROPERTY_PATH.split("\\."));
        ((AbstractSelectedColumnCommand) commandSpy).manageCollectionProperty(scenarioSimulationContextLocal, gridColumnMock, FULL_PROPERTY_PATH, 0, fullPropertyPathElements);
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).navigateComplexObject(eq(factModelTreeMock), eq(fullPropertyPathElements), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
        verify(gridColumnMock, times(1)).setFactory(eq(scenarioSimulationContextLocal.getCollectionEditorSingletonDOMElementFactory(GridWidget.SIMULATION)));
        verify(factMappingMock, times(1)).setGenericTypes(eq(factModelTreeMock.getGenericTypeInfo(LIST_PROPERTY_NAME)));
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    public void manageSimpleTypeCollectionProperty() {
        when(factModelTreeMock.isSimple()).thenReturn(true);
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        final List<String> fullPropertyPathElements = Arrays.asList(FULL_PROPERTY_PATH.split("\\."));
        ((AbstractSelectedColumnCommand) commandSpy).manageCollectionProperty(scenarioSimulationContextLocal, gridColumnMock, FULL_PROPERTY_PATH, 0, fullPropertyPathElements);
        verify((AbstractSelectedColumnCommand) commandSpy, never()).navigateComplexObject(any(), any(), any());
        verify(gridColumnMock, times(1)).setFactory(eq(scenarioSimulationContextLocal.getCollectionEditorSingletonDOMElementFactory(GridWidget.SIMULATION)));
        verify(factMappingMock, times(1)).setGenericTypes(eq(factModelTreeMock.getGenericTypeInfo(LOWER_CASE_VALUE)));
    }

    protected void commonSetPropertyHeader(ScenarioSimulationModel.Type type, boolean keepData, List<String> propertyNameElements, String propertyClass) {
        ((AbstractSelectedColumnCommand) commandSpy).setPropertyHeader(scenarioSimulationContextLocal, gridColumnMock, FULL_CLASS_NAME, propertyNameElements, propertyClass);
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(!type.equals(ScenarioSimulationModel.Type.DMN)));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(String.join(".", propertyNameElements.subList(1, propertyNameElements.size())));
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(propertyNameElements), eq(propertyClass), eq(keepData), isA(FactMappingValueType.class), eq(type));
        verify(scenarioSimulationContextLocal.getScenarioSimulationEditorPresenter(), times(1)).reloadTestTools(false);
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    protected void navigateComplexObject() {
        FactModelTree book = new FactModelTree("Book", "com.Book", new HashMap<>(), new HashMap<>());
        book.addExpandableProperty("author", "Author");
        FactModelTree author = new FactModelTree("Author", "com.Author", new HashMap<>(), new HashMap<>());
        SortedMap<String, FactModelTree> sortedMap = spy(new TreeMap<>());
        sortedMap.put("Book", book);
        sortedMap.put("Author", author);
        List<String> elements = Arrays.asList("Book", "author", "currentlyPrinted");
        FactModelTree target = ((AbstractSelectedColumnCommand) commandSpy).navigateComplexObject(book, elements, sortedMap);
        assertEquals(target, author);
        verify(sortedMap, times(1)).get("Author");
    }

    protected void navigateComplexObject3Levels() {
        FactModelTree book = new FactModelTree("Book", "com.Book", new HashMap<>(), new HashMap<>());
        book.addExpandableProperty("author", "Author");
        FactModelTree author = new FactModelTree("Author", "com.Author", new HashMap<>(), new HashMap<>());
        author.addExpandableProperty("firstBook", "Book");
        SortedMap<String, FactModelTree> sortedMap = spy(new TreeMap<>());
        sortedMap.put("Book", book);
        sortedMap.put("Author", author);
        List<String> elements = Arrays.asList("Book", "author", "firstBook", "topics");
        FactModelTree target = ((AbstractSelectedColumnCommand) commandSpy).navigateComplexObject(book, elements, sortedMap);
        assertEquals(target, book);
        verify(sortedMap, times(1)).get("Author");
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    protected void getPropertyHeaderTitle() {
        Optional<String> emptyMatching = Optional.empty();
        doReturn(emptyMatching).when((AbstractSelectedColumnCommand) commandSpy).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(MULTIPART_VALUE_ELEMENTS), eq(factIdentifierMock));
        String retrieved = ((AbstractSelectedColumnCommand) commandSpy).getPropertyHeaderTitle(scenarioSimulationContextLocal, MULTIPART_VALUE_ELEMENTS, factIdentifierMock);
        List<String> propertyNameElements = new ArrayList<>();
        propertyNameElements.add(CLASS_NAME);
        propertyNameElements.addAll(MULTIPART_VALUE_ELEMENTS.subList(1, MULTIPART_VALUE_ELEMENTS.size()));
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(propertyNameElements), eq(factIdentifierMock));
        String expected = String.join(".", MULTIPART_VALUE_ELEMENTS.subList(1, MULTIPART_VALUE_ELEMENTS.size()));
        assertEquals(expected, retrieved);
        String EXPECTED_VALUE_STRING = "EXPECTED_VALUE_STRING";
        Optional<String> expectedValue = Optional.of("EXPECTED_VALUE_STRING");
        doReturn(expectedValue).when((AbstractSelectedColumnCommand) commandSpy).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(FULL_PROPERTY_NAME_ELEMENTS), eq(factIdentifierMock));
        retrieved = ((AbstractSelectedColumnCommand) commandSpy).getPropertyHeaderTitle(scenarioSimulationContextLocal, FULL_PROPERTY_NAME_ELEMENTS, factIdentifierMock);
        verify((AbstractSelectedColumnCommand) commandSpy, times(1)).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(FULL_PROPERTY_NAME_ELEMENTS), eq(factIdentifierMock));
        assertEquals(EXPECTED_VALUE_STRING, retrieved);
        List<String> aliasedPropertyNameElements = Arrays.asList(CLASS_NAME + "_ALIAS", PROPERTY_NAME);
        retrieved = ((AbstractSelectedColumnCommand) commandSpy).getPropertyHeaderTitle(scenarioSimulationContextLocal, aliasedPropertyNameElements, factIdentifierMock);
        verify((AbstractSelectedColumnCommand) commandSpy, times(2)).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(FULL_PROPERTY_NAME_ELEMENTS), eq(factIdentifierMock));
        assertEquals(EXPECTED_VALUE_STRING, retrieved);
    }

    protected void getPropertyHeaderTitle_Value() {
        Optional<String> emptyMatching = Optional.empty();
        doReturn(emptyMatching).when((AbstractSelectedColumnCommand) commandSpy).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(singletonList(CLASS_NAME)), eq(factIdentifierMock));
        String retrieved = ((AbstractSelectedColumnCommand) commandSpy).getPropertyHeaderTitle(scenarioSimulationContextLocal, singletonList(CLASS_NAME), factIdentifierMock);
        assertEquals(VALUE, retrieved);
    }

    protected void getPropertyHeaderTitle_Expression() {
        Optional<String> emptyMatching = Optional.empty();
        doReturn(emptyMatching).when((AbstractSelectedColumnCommand) commandSpy).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(singletonList(CLASS_NAME)), eq(factIdentifierMock));
        ((AbstractSelectedColumnCommand) commandSpy).factMappingValueType = FactMappingValueType.EXPRESSION;
        String retrieved = ((AbstractSelectedColumnCommand) commandSpy).getPropertyHeaderTitle(scenarioSimulationContextLocal, singletonList(CLASS_NAME), factIdentifierMock);
        assertEquals(ConstantHolder.EXPRESSION_INSTANCE_PLACEHOLDER, retrieved);
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    protected void getMatchingExpressionAlias() {
        Optional<String> retrieved = ((AbstractSelectedColumnCommand) commandSpy).getMatchingExpressionAlias(scenarioSimulationContextLocal, MULTIPART_VALUE_ELEMENTS, factIdentifierMock);
        verify(simulationDescriptorMock, times(1)).getFactMappingsByFactName(eq(factIdentifierMock.getName()));
        assertEquals(Optional.empty(), retrieved);
        List<FactMapping> factMappingList = new ArrayList<>();
        factMappingList.add(factMappingMock);
        when(simulationDescriptorMock.getFactMappingsByFactName(FACT_IDENTIFIER_NAME)).thenReturn(factMappingList.stream());
        String EXPRESSION_ALIAS = "EXPRESSION_ALIAS";
        when(factMappingMock.getExpressionAlias()).thenReturn(EXPRESSION_ALIAS);
        retrieved = ((AbstractSelectedColumnCommand) commandSpy).getMatchingExpressionAlias(scenarioSimulationContextLocal, FULL_PROPERTY_NAME_ELEMENTS, factIdentifierMock);
        assertEquals(Optional.empty(), retrieved);
        List<ExpressionElement> expressionElements = FULL_PROPERTY_NAME_ELEMENTS.stream().map(ExpressionElement::new).collect(Collectors.toList());
        when(factMappingMock.getExpressionElements()).thenReturn(expressionElements);
        when(simulationDescriptorMock.getFactMappingsByFactName(FACT_IDENTIFIER_NAME)).thenReturn(factMappingList.stream());
        retrieved = ((AbstractSelectedColumnCommand) commandSpy).getMatchingExpressionAlias(scenarioSimulationContextLocal, FULL_PROPERTY_NAME_ELEMENTS, factIdentifierMock);
        assertEquals(Optional.of(EXPRESSION_ALIAS), retrieved);
    }
}
