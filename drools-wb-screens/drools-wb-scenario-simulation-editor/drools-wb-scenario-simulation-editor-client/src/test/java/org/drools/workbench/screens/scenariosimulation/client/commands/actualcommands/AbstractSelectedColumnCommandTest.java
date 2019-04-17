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

import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractSelectedColumnCommandTest extends AbstractScenarioSimulationCommandTest {

    final private String CLASS_NAME = "ClassName";
    final private String FULL_CLASSNAME_CREATED = FULL_PACKAGE + "." + CLASS_NAME;
    final private String LIST_PROPERTY_NAME = "listProperty";
    final private String FULL_PROPERTY_PATH = CLASS_NAME + "." + LIST_PROPERTY_NAME;

    @Mock
    protected List<GridColumn<?>> gridColumnsMock;
    @Mock
    protected FactModelTree factModelTreeMock;

    public void setup() {
        super.setup();

        when(gridColumnsMock.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        when(factModelTreeMock.getExpandableProperties()).thenReturn(mock(SortedMap.class));
        when(dataObjectFieldsMapMock.get(anyString())).thenReturn(factModelTreeMock);

        scenarioSimulationContextLocal.getStatus().setFullPackage(FULL_PACKAGE);
        scenarioSimulationContextLocal.getStatus().setValue(VALUE);
        scenarioSimulationContextLocal.getStatus().setValueClassName(VALUE_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setColumnId(COLUMN_ID);
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
    }

    @Test
    public void executeIfSelected() {
        command.execute(scenarioSimulationContextLocal);
        verify((AbstractSelectedColumnCommand) command, times(1)).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
    }

    @Test
    public void executeIfSelected_NoColumn() {
        gridColumnMock = null;
        command.execute(scenarioSimulationContextLocal);
        verify((AbstractSelectedColumnCommand) command, never()).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
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

    protected void insertNewColumnCommon( int columnIndex, boolean cloneInstance) {
        int instanceNum = simulationDescriptorMock.getUnmodifiableFactMappings().size();
        ScenarioGridColumn createdColumn = ((AbstractSelectedColumnCommand) command).insertNewColumn(scenarioSimulationContextLocal, gridColumnMock, columnIndex, cloneInstance);
        String columnGroup = gridColumnMock.getInformationHeaderMetaData().getColumnGroup();
        String originalInstanceTitle = gridColumnMock.getInformationHeaderMetaData().getTitle();
        String instanceTitle = cloneInstance ? originalInstanceTitle : "INSTANCE " + instanceNum ;
        String propertyTitle = "PROPERTY " + instanceNum ;
        final FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        verify(command, times(1)).getScenarioGridColumnLocal(
                                                             eq(instanceTitle),
                                                             eq(propertyTitle),
                                                             anyString(),
                                                             eq(columnGroup),
                                                             eq(factMappingType),
                                                             eq(scenarioHeaderTextBoxSingletonDOMElementFactoryTest),
                                                             eq(scenarioCellTextAreaSingletonDOMElementFactoryTest),
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
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        commonSetPropertyHeader(ScenarioSimulationModel.Type.DMN, false, VALUE, VALUE_CLASS_NAME);
    }

    @Test
    public void executeKeepDataFalseRule() {
        scenarioSimulationContextLocal.getStatus().setKeepData(false);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        commonSetPropertyHeader(ScenarioSimulationModel.Type.RULE, false, VALUE, VALUE_CLASS_NAME);
    }

    @Test
    public void executeKeepDataTrue() {
        scenarioSimulationContextLocal.getStatus().setKeepData(true);
        commonSetPropertyHeader(ScenarioSimulationModel.Type.RULE, true, VALUE, VALUE_CLASS_NAME);
    }

    @Test
    public void executeWithPropertyAsCollection() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        final List<String> fullPropertyPathElements = Arrays.asList(FULL_PROPERTY_PATH.split("\\."));
        ((AbstractSelectedColumnCommand) command).setPropertyHeader(scenarioSimulationContextLocal, gridColumnMock, FULL_PROPERTY_PATH, LIST_CLASS_NAME, Optional.empty());
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(LIST_PROPERTY_NAME);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(FULL_PROPERTY_PATH), eq(LIST_CLASS_NAME), anyBoolean());
        verify((AbstractSelectedColumnCommand) command, times(1)).manageCollectionProperty(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(FULL_CLASSNAME_CREATED), eq(0), eq(fullPropertyPathElements));
        verify((AbstractSelectedColumnCommand) command, times(1)).navigateComplexObject(eq(factModelTreeMock), eq(fullPropertyPathElements), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    public void manageCollectionProperty() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        scenarioSimulationContextLocal.getStatus().setValue(FULL_PROPERTY_PATH);
        final List<String> fullPropertyPathElements = Arrays.asList(FULL_PROPERTY_PATH.split("\\."));
        ((AbstractSelectedColumnCommand)  command).manageCollectionProperty(scenarioSimulationContextLocal, gridColumnMock, FULL_PROPERTY_PATH, 0, fullPropertyPathElements);
        verify((AbstractSelectedColumnCommand)  command, times(1)).navigateComplexObject(eq(factModelTreeMock), eq(fullPropertyPathElements), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
        verify(gridColumnMock, times(1)).setFactory(eq(scenarioSimulationContextLocal.getCollectionEditorSingletonDOMElementFactory()));
        verify(factMappingMock, times(1)).setGenericTypes(eq(factModelTreeMock.getGenericTypeInfo(LIST_PROPERTY_NAME)));
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    protected void commonSetPropertyHeader(ScenarioSimulationModel.Type type, boolean keepData, String value, String propertyClass) {
        ((AbstractSelectedColumnCommand) command).setPropertyHeader(scenarioSimulationContextLocal, gridColumnMock, value, propertyClass, Optional.empty());
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(!type.equals(ScenarioSimulationModel.Type.DMN)));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(value);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), isA(ScenarioGridColumn.class), eq(value), eq(propertyClass), eq(keepData));
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
        FactModelTree target = ((AbstractSelectedColumnCommand) command).navigateComplexObject(book, elements, sortedMap);
        assertEquals(target, author);
        verify(sortedMap, times(1)).get("Author");
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    protected void getPropertyHeaderTitle() {
        Optional<String> emptyMatching = Optional.empty();
        doReturn(emptyMatching).when((AbstractSelectedColumnCommand) command).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(VALUE), eq(factIdentifierMock));
        String retrieved = ((AbstractSelectedColumnCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, VALUE, factIdentifierMock);
        verify((AbstractSelectedColumnCommand) command, times(1)).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(VALUE), eq(factIdentifierMock));
        assertEquals(VALUE, retrieved);
        String EXPECTED_VALUE_STRING = "EXPECTED_VALUE_STRING";
        Optional<String> expectedValue = Optional.of("EXPECTED_VALUE_STRING");
        doReturn(expectedValue).when((AbstractSelectedColumnCommand) command).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(FULL_PROPERTY_NAME), eq(factIdentifierMock));
        retrieved = ((AbstractSelectedColumnCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, FULL_PROPERTY_NAME, factIdentifierMock);
        verify((AbstractSelectedColumnCommand) command, times(1)).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(FULL_PROPERTY_NAME), eq(factIdentifierMock));
        assertEquals(EXPECTED_VALUE_STRING, retrieved);
        String aliasedPropertyName = CLASS_NAME + "_ALIAS." + PROPERTY_NAME;
        retrieved = ((AbstractSelectedColumnCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, aliasedPropertyName, factIdentifierMock);
        verify((AbstractSelectedColumnCommand) command, times(2)).getMatchingExpressionAlias(eq(scenarioSimulationContextLocal), eq(FULL_PROPERTY_NAME), eq(factIdentifierMock));
        assertEquals(EXPECTED_VALUE_STRING, retrieved);
    }

    /* This test is usable ONLY by <code>SetPropertyCommandTest</code> subclass */
    protected void getMatchingExpressionAlias() {
        Optional<String> retrieved = ((AbstractSelectedColumnCommand) command).getMatchingExpressionAlias(scenarioSimulationContextLocal, VALUE, factIdentifierMock);
        verify(simulationDescriptorMock, times(1)).getFactMappingsByFactName(eq(factIdentifierMock.getName()));
        assertEquals(Optional.empty(), retrieved);
        List<FactMapping> factMappingList = new ArrayList<>();
        when(simulationDescriptorMock.getFactMappingsByFactName(FACT_IDENTIFIER_NAME)).thenReturn(factMappingList);
        factMappingList.add(factMappingMock);
        String EXPRESSION_ALIAS = "EXPRESSION_ALIAS";
        when(factMappingMock.getExpressionAlias()).thenReturn(EXPRESSION_ALIAS);
        retrieved = ((AbstractSelectedColumnCommand) command).getMatchingExpressionAlias(scenarioSimulationContextLocal, FULL_PROPERTY_NAME, factIdentifierMock);
        assertEquals(Optional.empty(), retrieved);
        when(factMappingMock.getFullExpression()).thenReturn(FULL_PROPERTY_NAME);
        retrieved = ((AbstractSelectedColumnCommand) command).getMatchingExpressionAlias(scenarioSimulationContextLocal, FULL_PROPERTY_NAME, factIdentifierMock);
        assertEquals(Optional.of(EXPRESSION_ALIAS), retrieved);
    }


}
