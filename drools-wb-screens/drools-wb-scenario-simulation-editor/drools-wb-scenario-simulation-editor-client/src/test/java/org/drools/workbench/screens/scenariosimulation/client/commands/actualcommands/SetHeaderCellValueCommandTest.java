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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetHeaderCellValueCommandTest extends AbstractScenarioSimulationCommandTest {

    @Before
    public void setup() {
        super.setup();
        command = spy(new SetHeaderCellValueCommand());
        scenarioSimulationContextLocal.getStatus().setRowIndex(ROW_INDEX);
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContextLocal.getStatus().setCellValue(VALUE);
        assertTrue(command.isUndoable());
    }

    @Test
    public void executeInstanceHeaderValid() {
        commonExecute(true, false, true);
    }

    @Test
    public void executeInstanceHeaderInvalid() {
        commonExecute(true, false, false);
    }

    @Test
    public void executePropertyHeaderValid() {
        commonExecute(false, true, true);
    }

    @Test
    public void executePropertyHeaderInvalid() {
        commonExecute(false, true, false);
    }

    @Test
    public void executeOtherHeader() {
        commonExecute(false, false, false);
    }

    @Test
    public void validateInstanceHeader() {
        commonValidateInstanceHeader(false);
        commonValidateInstanceHeader(true);
    }

    @Test
    public void validatePropertyHeaderNoFactModelMapped() {
        commonValidatePropertyHeader(false, false);
    }

    @Test
    public void validatePropertyHeaderFactModelMappedNoProperty() {
        commonValidatePropertyHeader(true, true);
    }

    @Test
    public void validatePropertyHeaderFactModelMappedProperty() {
        commonValidatePropertyHeader(true, true);
    }

    @Test
    public void recursivelyFindIsPropertyType() {
        Map<String, String> bookSimpleProperties = new HashMap<>();
        bookSimpleProperties.put("name", "String");
        Map<String, String> bookExpandableProperties = new HashMap<>();
        bookExpandableProperties.put("author", "Author");
        Map<String, String> authorSimpleProperties = new HashMap<>();
        authorSimpleProperties.put("books", "List");
        Map<String, String> authorExpandableProperties = new HashMap<>();
        FactModelTree bookFactModelTreeMock = getMockedFactModelTree(bookSimpleProperties, bookExpandableProperties);
        FactModelTree authorFactModelTreeMock = getMockedFactModelTree(authorSimpleProperties, authorExpandableProperties);
        when(dataObjectFieldsMapMock.get("Author")).thenReturn(authorFactModelTreeMock);
        when(dataObjectFieldsMapMock.get("Book")).thenReturn(bookFactModelTreeMock);
        assertFalse(((SetHeaderCellValueCommand) command).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, "not-existing"));
        assertTrue(((SetHeaderCellValueCommand) command).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, "name"));
        assertFalse(((SetHeaderCellValueCommand) command).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, "author.not-existing"));
        assertTrue(((SetHeaderCellValueCommand) command).recursivelyFindIsPropertyType(scenarioSimulationContextLocal, bookFactModelTreeMock, "author.books"));
    }

    private FactModelTree getMockedFactModelTree(Map<String, String> simpleProperties, Map<String, String> expandableProperties) {
        FactModelTree toReturn = mock(FactModelTree.class);
        when(toReturn.getSimpleProperties()).thenReturn(simpleProperties);
        when(toReturn.getExpandableProperties()).thenReturn(expandableProperties);
        return toReturn;
    }

    private void commonExecute(boolean isInstanceHeader, boolean isPropertyHeader, boolean isValid) {
        ((SetHeaderCellValueCommand) command).isInstanceHeader = isInstanceHeader;
        ((SetHeaderCellValueCommand) command).isPropertyHeader = isPropertyHeader;
        doReturn(isValid).when(((SetHeaderCellValueCommand) command)).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(VALUE), eq(COLUMN_INDEX));
        doReturn(isValid).when(((SetHeaderCellValueCommand) command)).validatePropertyHeader(eq(scenarioSimulationContextLocal), eq(VALUE), eq(COLUMN_INDEX));
        command.execute(scenarioSimulationContextLocal);
        if (isInstanceHeader) {
            verify(((SetHeaderCellValueCommand) command), times(1)).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(VALUE), eq(COLUMN_INDEX));
        } else if (isPropertyHeader) {
            verify(((SetHeaderCellValueCommand) command), times(1)).validatePropertyHeader(eq(scenarioSimulationContextLocal), eq(VALUE), eq(COLUMN_INDEX));
        } else {
            verify(((SetHeaderCellValueCommand) command), never()).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(VALUE), eq(COLUMN_INDEX));
            verify(((SetHeaderCellValueCommand) command), never()).validateInstanceHeader(eq(scenarioSimulationContextLocal), eq(VALUE), eq(COLUMN_INDEX));
        }
        if (isValid) {
            verify(scenarioGridModelMock, times(1)).updateHeader(eq(COLUMN_INDEX), eq(ROW_INDEX), eq(VALUE));
        }
    }

    private void commonValidateInstanceHeader(boolean isADataType) {
        doReturn(isADataType).when(dataObjectFieldsMapMock).containsKey(VALUE);
        ((SetHeaderCellValueCommand) command).validateInstanceHeader(scenarioSimulationContextLocal, VALUE, COLUMN_INDEX);
        verify(dataObjectFieldsMapMock, times(1)).containsKey(eq(VALUE));
        verify(scenarioGridModelMock, times(1)).validateInstanceHeaderUpdate(eq(VALUE), eq(COLUMN_INDEX), eq(isADataType));
        reset(dataObjectFieldsMapMock);
        reset(scenarioGridModelMock);
    }

    private void commonValidatePropertyHeader(boolean factModelPresent, boolean simplePropertyPresent) {
        FactModelTree factModelTreeMock = mock(FactModelTree.class);
        if (factModelPresent) {
            Map<String, String> simplePropertiesMock = mock(SortedMap.class);
            when(factModelTreeMock.getSimpleProperties()).thenReturn(simplePropertiesMock);
            when(factModelTreeMock.getExpandableProperties()).thenReturn(mock(SortedMap.class));
            when(dataObjectFieldsMapMock.get(anyString())).thenReturn(factModelTreeMock);
            doReturn(simplePropertyPresent).when(simplePropertiesMock).containsKey(eq(VALUE));
        } else {
            when(dataObjectFieldsMapMock.get(anyString())).thenReturn(null);
        }
        boolean isPropertyType = factModelPresent && simplePropertyPresent;
        ((SetHeaderCellValueCommand) command).validatePropertyHeader(scenarioSimulationContextLocal, VALUE, COLUMN_INDEX);
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioGridModelMock, times(1)).validatePropertyHeaderUpdate(eq(VALUE), eq(COLUMN_INDEX), eq(isPropertyType));
        reset(simulationDescriptorMock);
        reset(scenarioGridModelMock);
    }
}