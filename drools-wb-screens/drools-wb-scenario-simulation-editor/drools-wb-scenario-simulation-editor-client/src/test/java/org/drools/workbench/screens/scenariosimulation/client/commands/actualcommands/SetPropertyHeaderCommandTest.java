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

package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetPropertyHeaderCommandTest extends AbstractScenarioSimulationCommandTest {

    final protected String FULL_CLASSNAME_CREATED = FULL_PACKAGE + "." + VALUE;

    @Mock
    private List<GridColumn<?>> gridColumnsMock;
    @Mock
    protected FactModelTree factModelTreeMock;

    @Before
    public void setup() {
        super.setup();
        when(gridColumnsMock.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);
        command = spy(new SetPropertyHeaderCommand() {

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder, ScenarioSimulationContext context) {
                return gridColumnMock;
            }
        });
        scenarioSimulationContextLocal.getStatus().setFullPackage(FULL_PACKAGE);
        scenarioSimulationContextLocal.getStatus().setValue(VALUE);
        scenarioSimulationContextLocal.getStatus().setValueClassName(VALUE_CLASS_NAME);
        assertTrue(command.isUndoable());
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);

        Map<String, String> simplePropertiesMock = mock(SortedMap.class);
        when(factModelTreeMock.getSimpleProperties()).thenReturn(simplePropertiesMock);
        when(factModelTreeMock.getExpandableProperties()).thenReturn(mock(SortedMap.class));
        when(dataObjectFieldsMapMock.get(anyString())).thenReturn(factModelTreeMock);
    }

    @Test
    public void executeNoColumn() {
        gridColumnMock = null;
        command.execute(scenarioSimulationContextLocal);
        verify((SetPropertyHeaderCommand) command, never()).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
    }

    @Test
    public void executeKeepDataFalseDMN() {
        scenarioSimulationContextLocal.getStatus().setKeepData(false);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.DMN);
        command.execute(scenarioSimulationContextLocal);
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(false));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), isA(ScenarioGridColumn.class), eq(VALUE), eq(VALUE_CLASS_NAME), eq(false));
    }

    @Test
    public void executeKeepDataFalseRule() {
        scenarioSimulationContextLocal.getStatus().setKeepData(false);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        command.execute(scenarioSimulationContextLocal);
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(true));
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), isA(ScenarioGridColumn.class), eq(VALUE), eq(VALUE_CLASS_NAME), eq(false));
    }

    @Test
    public void executeKeepDataTrue() {
        scenarioSimulationContextLocal.getStatus().setKeepData(true);
        command.execute(scenarioSimulationContextLocal);
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(VALUE), eq(VALUE_CLASS_NAME), eq(true));
    }

    @Test
    public void executeWithPropertyAsCollection() {
        scenarioSimulationContextLocal.getStatus().setValueClassName(LIST_CLASS_NAME);
        command.execute(scenarioSimulationContextLocal);
        verify(propertyHeaderMetaDataMock, times(1)).setColumnGroup(anyString());
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(VALUE);
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(false);
        verify(scenarioGridModelMock, times(1)).updateColumnProperty(anyInt(), eq(gridColumnMock), eq(VALUE), eq(LIST_CLASS_NAME), anyBoolean());
        List<String> elements = Arrays.asList((FULL_CLASSNAME_CREATED).split("\\."));
        verify((SetPropertyHeaderCommand) command, times(1)).navigateComplexObject(eq(factModelTreeMock), eq(elements), eq(scenarioSimulationContextLocal.getDataObjectFieldsMap()));
    }

    @Test
    public void navigateComplexObject() {
        FactModelTree book = new FactModelTree("Book", "com.Book", new HashMap<>(), new HashMap<>());
        book.addExpandableProperty("author", "Author");
        FactModelTree author = new FactModelTree("Author", "com.Author", new HashMap<>(), new HashMap<>());
        SortedMap<String, FactModelTree> sortedMap = spy(new TreeMap<>());
        sortedMap.put("Book", book);
        sortedMap.put("Author", author);
        List<String> elements = Arrays.asList("Book", "author", "currentlyPrinted");
        FactModelTree target = ((SetPropertyHeaderCommand) command).navigateComplexObject(book, elements, sortedMap);
        assertEquals(target, author);
        verify(sortedMap, times(1)).get("Author");
    }

    @Test
    public void getPropertyHeaderTitle() {
        String retrieved = ((SetPropertyHeaderCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, factIdentifierMock);
        assertEquals(VALUE, retrieved);
        List<FactMapping> factMappingList = new ArrayList<>();
        when(simulationDescriptorMock.getFactMappingsByFactName(FACT_IDENTIFIER_NAME)).thenReturn(factMappingList);
        retrieved = ((SetPropertyHeaderCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, factIdentifierMock);
        assertEquals(VALUE, retrieved);
        factMappingList.add(factMappingMock);
        retrieved = ((SetPropertyHeaderCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, factIdentifierMock);
        assertEquals(VALUE, retrieved);
        String EXPRESSION_ALIAS = "EXPRESSION_ALIAS";
        when(factMappingMock.getFullExpression()).thenReturn(VALUE);
        when(factMappingMock.getExpressionAlias()).thenReturn(EXPRESSION_ALIAS);
        retrieved = ((SetPropertyHeaderCommand) command).getPropertyHeaderTitle(scenarioSimulationContextLocal, factIdentifierMock);
        assertEquals(EXPRESSION_ALIAS, retrieved);
    }
}