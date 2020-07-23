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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetHeaderCellValueCommandTest extends AbstractScenarioGridCommandTest {

    @Before
    public void setup() {
        super.setup();
        commandSpy = spy(new SetHeaderCellValueCommand(GridWidget.SIMULATION, false, false));
        scenarioSimulationContextLocal.getStatus().setRowIndex(ROW_INDEX);
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContextLocal.getStatus().setGridCellValue(MULTIPART_VALUE);
    }

    @Test
    public void executeInstanceHeaderValid() throws Exception {
        commonExecute(true, false, true);
    }

    @Test
    public void executeInstanceHeaderInvalid() throws Exception {
        commonExecute(true, false, false);
    }

    @Test
    public void executePropertyHeaderValid() throws Exception {
        commonExecute(false, true, true);
    }

    @Test
    public void executePropertyHeaderInvalid() throws Exception {
        commonExecute(false, true, false);
    }

    @Test
    public void executeOtherHeader() throws Exception {
        commonExecute(false, false, false);
    }

    @Test
    public void validateInstanceHeader() throws Exception {
        commonValidateInstanceHeader(false);
        commonValidateInstanceHeader(true);
    }

    @Test
    public void validatePropertyHeaderNoFactModelMapped() throws Exception {
        commonValidatePropertyHeader(false, false);
    }

    @Test
    public void validatePropertyHeaderFactModelMappedNoProperty() throws Exception {
        commonValidatePropertyHeader(true, false);
    }

    @Test
    public void validatePropertyHeaderFactModelMappedProperty() throws Exception {
        commonValidatePropertyHeader(true, true);
    }

    @Test
    public void recursivelyFindIsPropertyType() {
        Map<String, FactModelTree.PropertyTypeName> bookSimpleProperties = new HashMap<>();
        bookSimpleProperties.put("name", new FactModelTree.PropertyTypeName("String"));
        Map<String, String> bookExpandableProperties = new HashMap<>();
        bookExpandableProperties.put("author", "Author");
        Map<String, FactModelTree.PropertyTypeName> authorSimpleProperties = new HashMap<>();
        authorSimpleProperties.put("books", new FactModelTree.PropertyTypeName("List"));
        Map<String, String> authorExpandableProperties = new HashMap<>();
        FactModelTree bookFactModelTreeMock = getMockedFactModelTree(bookSimpleProperties, bookExpandableProperties);
        FactModelTree authorFactModelTreeMock = getMockedFactModelTree(authorSimpleProperties, authorExpandableProperties);
        when(dataObjectFieldsMapMock.get("Author")).thenReturn(authorFactModelTreeMock);
        when(dataObjectFieldsMapMock.get("Book")).thenReturn(bookFactModelTreeMock);
        assertFalse(((SetHeaderCellValueCommand) commandSpy).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, Collections.singletonList("not-existing")));
        assertTrue(((SetHeaderCellValueCommand) commandSpy).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, Collections.singletonList("name")));
        assertFalse(((SetHeaderCellValueCommand) commandSpy).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, Arrays.asList("author", "not-existing")));
        assertTrue(((SetHeaderCellValueCommand) commandSpy).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, Arrays.asList("author", "books")));
    }

    private FactModelTree getMockedFactModelTree(Map<String, FactModelTree.PropertyTypeName> simpleProperties, Map<String, String> expandableProperties) {
        FactModelTree toReturn = mock(FactModelTree.class);
        when(toReturn.getSimpleProperties()).thenReturn(simpleProperties);
        when(toReturn.getExpandableProperties()).thenReturn(expandableProperties);
        return toReturn;
    }

    private void commonExecute(boolean isInstanceHeader, boolean isPropertyHeader, boolean isValid) throws Exception {
        ((SetHeaderCellValueCommand) commandSpy).isInstanceHeader = isInstanceHeader;
        ((SetHeaderCellValueCommand) commandSpy).isPropertyHeader = isPropertyHeader;
        scenarioSimulationContextLocal.getStatus().setHeaderCellValue(MULTIPART_VALUE);
        doNothing().when(((SetHeaderCellValueCommand) commandSpy)).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(MULTIPART_VALUE), eq(COLUMN_INDEX));
        doNothing().when(((SetHeaderCellValueCommand) commandSpy)).validatePropertyHeader(eq(scenarioSimulationContextLocal), eq(MULTIPART_VALUE), eq(COLUMN_INDEX));
        commandSpy.execute(scenarioSimulationContextLocal);
        if (isInstanceHeader) {
            verify(((SetHeaderCellValueCommand) commandSpy), times(1)).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(MULTIPART_VALUE), eq(COLUMN_INDEX));
        } else if (isPropertyHeader) {
            verify(((SetHeaderCellValueCommand) commandSpy), times(1)).validatePropertyHeader(eq(scenarioSimulationContextLocal), eq(MULTIPART_VALUE), eq(COLUMN_INDEX));
        } else {
            verify(((SetHeaderCellValueCommand) commandSpy), never()).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(MULTIPART_VALUE), eq(COLUMN_INDEX));
            verify(((SetHeaderCellValueCommand) commandSpy), never()).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(MULTIPART_VALUE), eq(COLUMN_INDEX));
        }
        if (isValid) {
            verify(scenarioGridModelMock, times(1)).updateHeader(eq(COLUMN_INDEX), eq(ROW_INDEX), eq(MULTIPART_VALUE));
        }
    }

    private void commonValidateInstanceHeader(boolean isADataType) throws Exception {
        doReturn(isADataType).when(dataObjectFieldsMapMock).containsKey(LOWER_CASE_VALUE);
        doNothing().when(scenarioGridModelMock).validateInstanceHeaderUpdate(eq(LOWER_CASE_VALUE), eq(COLUMN_INDEX), eq(isADataType));
        ((SetHeaderCellValueCommand) commandSpy).validateInstanceHeader(scenarioSimulationContextLocal, LOWER_CASE_VALUE, COLUMN_INDEX);
        verify(dataObjectFieldsMapMock, times(1)).containsKey(eq(LOWER_CASE_VALUE));
        verify(scenarioGridModelMock, times(1)).validateInstanceHeaderUpdate(eq(LOWER_CASE_VALUE), eq(COLUMN_INDEX), eq(isADataType));
        reset(dataObjectFieldsMapMock);
        reset(scenarioGridModelMock);
    }

    private void commonValidatePropertyHeader(boolean factModelPresent, boolean simplePropertyPresent) throws Exception {
        FactModelTree factModelTreeMock = mock(FactModelTree.class);
        doReturn(simplePropertyPresent).when((SetHeaderCellValueCommand) commandSpy).recursivelyFindIsPropertyType(eq(scenarioSimulationContextLocal), eq(factModelTreeMock), eq(MULTIPART_VALUE_ELEMENTS));
        if (factModelPresent) {
            Map<String, FactModelTree.PropertyTypeName> simplePropertiesMock = mock(SortedMap.class);
            when(factModelTreeMock.getSimpleProperties()).thenReturn(simplePropertiesMock);
            Map<String, String> expandablePropertiesMock = mock(SortedMap.class);
            when(factModelTreeMock.getExpandableProperties()).thenReturn(expandablePropertiesMock);
            when(dataObjectFieldsMapMock.get(anyString())).thenReturn(factModelTreeMock);
            doReturn(simplePropertyPresent).when(simplePropertiesMock).containsKey(eq(MULTIPART_VALUE_ELEMENTS.get(0)));
            doReturn(simplePropertyPresent).when(expandablePropertiesMock).containsKey(eq(MULTIPART_VALUE_ELEMENTS.get(0)));
        } else {
            when(dataObjectFieldsMapMock.get(anyString())).thenReturn(null);
        }
        boolean isPropertyType = factModelPresent && simplePropertyPresent;
        doNothing().when(scenarioGridModelMock).validatePropertyHeaderUpdate(eq(MULTIPART_VALUE), eq(COLUMN_INDEX), eq(isPropertyType));
        ((SetHeaderCellValueCommand) commandSpy).validatePropertyHeader(scenarioSimulationContextLocal, MULTIPART_VALUE, COLUMN_INDEX);
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioGridModelMock, times(1)).validatePropertyHeaderUpdate(eq(MULTIPART_VALUE), eq(COLUMN_INDEX), eq(isPropertyType));
        reset(simulationDescriptorMock);
        reset(scenarioGridModelMock);
    }
}