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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_NUMBER;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_ALIAS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_ALIAS_1;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_IDENTIFIER_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_IDENTIFIER_NAME_1;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_CLASS_NAME_1;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_ID_1;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_PROPERTY_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_PROPERTY_TITLE_1;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_1;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(GwtMockitoTestRunner.class)
public class DuplicateInstanceCommandTest extends AbstractSelectedColumnCommandTest {



    @Mock
    protected ScenarioGridColumn scenarioGridColumnMock1;
    @Mock
    protected ScenarioGridColumn scenarioGridColumnMock2;
    @Mock
    protected ScenarioGridColumn scenarioGridColumnMock3;
    @Mock
    protected FactMapping factMappingMock1;
    @Mock
    protected FactMapping factMappingMock2;
    @Mock
    protected FactMapping factMappingMock3;
    @Mock
    protected FactMappingValue factMappingValueMock1;
    @Mock
    protected FactMappingValue factMappingValueMock2;
    @Mock
    protected FactMappingValue factMappingValueMock3;
    @Mock
    protected FactIdentifier factIdentifierMock1;
    @Mock
    protected FactIdentifier factIdentifierMock2;
    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock1;
    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock2;
    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock3;
    @Mock
    protected ScenarioHeaderMetaData informationHeaderMetaDataMock1;
    @Mock
    protected ScenarioHeaderMetaData informationHeaderMetaDataMock2;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock1;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock2;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock3;

    @Before
    public void setup() {
        super.setup();
        command = spy(new DuplicateInstanceCommand() {
            @Override
            protected void setInstanceHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String alias, String fullClassName) {
                //Do nothing
            }

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader,
                                                                    ScenarioCellTextAreaSingletonDOMElementFactory factoryCell, String placeHolder) {
                return gridColumnMock;
            }
        });
        assertTrue(command.isUndoable());
        addNewColumn(scenarioGridColumnMock1, headerMetaDatasMock1, informationHeaderMetaDataMock1, propertyHeaderMetaDataMock1, factIdentifierMock1, factMappingMock1,
                          factMappingValueMock1, COLUMN_NUMBER, COLUMN_NUMBER, COLUMN_NUMBER, VALUE_1, GRID_PROPERTY_TITLE_1, GRID_COLUMN_ID_1, FACT_ALIAS_1, VALUE_CLASS_NAME,
                          "test", FULL_CLASS_NAME_1, FACT_IDENTIFIER_NAME_1);
    }

    @Test
    public void executeIfSelectedColumn_WithoutInstanceAndProperty() {
        when(scenarioGridColumnMock1.isInstanceAssigned()).thenReturn(Boolean.FALSE);
        when(scenarioGridColumnMock1.isPropertyAssigned()).thenReturn(Boolean.FALSE);
        ((DuplicateInstanceCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, scenarioGridColumnMock1);
        verify(scenarioGridModelMock, times(1)).getInstancesCount(eq(scenarioGridColumnMock1.getFactIdentifier().getClassName()));
        verify((DuplicateInstanceCommand) command, times(1)).insertNewColumn(eq(scenarioSimulationContextLocal), eq(scenarioGridColumnMock1), eq(COLUMN_NUMBER + 1), eq(Boolean.FALSE));
        verify((DuplicateInstanceCommand) command, never()).setInstanceHeader(any(), any(), any(), any());
        verify((DuplicateInstanceCommand) command, never()).setPropertyHeader(any(), any(), any(), any(), any());
        verify(scenarioGridModelMock, never()).duplicateColumnValues(anyInt(), eq(0)); // The created column is mocked as gridColumnMock, with position 0
    }

    @Test
    public void executeIfSelectedColumn_WithInstanceOnly() {
        when(scenarioGridColumnMock1.isPropertyAssigned()).thenReturn(Boolean.FALSE);
        ((DuplicateInstanceCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, scenarioGridColumnMock1);
        verify(scenarioGridModelMock, times(1)).getInstancesCount(eq(scenarioGridColumnMock1.getFactIdentifier().getClassName()));
        verify((DuplicateInstanceCommand) command, times(1)).insertNewColumn(eq(scenarioSimulationContextLocal), eq(scenarioGridColumnMock1), eq(COLUMN_NUMBER + 1), eq(Boolean.FALSE));
        verify((DuplicateInstanceCommand) command, times(1)).setInstanceHeader(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(VALUE_1 + DuplicateInstanceCommand.COPY_LABEL + "1"), eq(FULL_CLASS_NAME_1));
        verify((DuplicateInstanceCommand) command, never()).setPropertyHeader(any(), any(), any(), any(), any());
        verify(scenarioGridModelMock, never()).duplicateColumnValues(anyInt(), eq(0)); // The created column is mocked as gridColumnMock, with position 0
    }

    @Test
    public void executeIfSelectedColumn_WithInstanceAndProperty() {
        ((DuplicateInstanceCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, scenarioGridColumnMock1);
        verify(scenarioGridModelMock, times(1)).getInstancesCount(eq(scenarioGridColumnMock1.getFactIdentifier().getClassName()));
        verify((DuplicateInstanceCommand) command, times(1)).insertNewColumn(eq(scenarioSimulationContextLocal), eq(scenarioGridColumnMock1), eq(COLUMN_NUMBER + 1), eq(Boolean.FALSE));
        String expectedDuplicatedLabel = VALUE_1 + DuplicateInstanceCommand.COPY_LABEL + "1";
        verify((DuplicateInstanceCommand) command, times(1)).setInstanceHeader(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(expectedDuplicatedLabel), eq(FULL_CLASS_NAME_1));
        List<String> expectedPropertyNameElements = Arrays.asList(expectedDuplicatedLabel, "test");
        verify((DuplicateInstanceCommand) command, times(1)).setPropertyHeader(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(expectedPropertyNameElements), eq(VALUE_CLASS_NAME), eq(Optional.of(GRID_PROPERTY_TITLE_1)));
        verify(scenarioGridModelMock, times(1)).duplicateColumnValues(eq(COLUMN_NUMBER), eq(0)); // The created column is mocked as gridColumnMock, with position 0
    }

    @Test
    public void executeIfSelectedColumn_WithInstanceAndPropertyAndThreeColumns() {
        final String SECOND = "SECOND";
        addNewColumn(scenarioGridColumnMock2, headerMetaDatasMock2, informationHeaderMetaDataMock2, propertyHeaderMetaDataMock2, factIdentifierMock2, factMappingMock2,
                     factMappingValueMock2, COLUMN_NUMBER + 1, COLUMN_NUMBER + 2, COLUMN_NUMBER + 1, MULTIPART_VALUE + "_2", GRID_PROPERTY_TITLE + "_2", GRID_COLUMN_ID + "_2", FACT_ALIAS + "_2", VALUE_CLASS_NAME,
                     SECOND, FULL_CLASS_NAME + "_2", FACT_IDENTIFIER_NAME + "_2");
        final String THIRD = "THIRD";
        addNewColumn(scenarioGridColumnMock3, headerMetaDatasMock3, informationHeaderMetaDataMock2, propertyHeaderMetaDataMock3, factIdentifierMock2, factMappingMock3,
                     factMappingValueMock3, COLUMN_NUMBER + 1, COLUMN_NUMBER + 2, COLUMN_NUMBER + 2, MULTIPART_VALUE + "_2", GRID_PROPERTY_TITLE + "_3", GRID_COLUMN_ID + "_3", FACT_ALIAS + "_3", VALUE_CLASS_NAME,
                     THIRD, FULL_CLASS_NAME + "_2", FACT_IDENTIFIER_NAME + "_3");
        ((DuplicateInstanceCommand) command).executeIfSelectedColumn(scenarioSimulationContextLocal, scenarioGridColumnMock2);
        verify(scenarioGridModelMock, times(1)).getInstancesCount(eq(scenarioGridColumnMock2.getFactIdentifier().getClassName()));
        verify((DuplicateInstanceCommand) command, times(1)).insertNewColumn(eq(scenarioSimulationContextLocal), eq(scenarioGridColumnMock2), eq(7), eq(Boolean.FALSE));
        verify((DuplicateInstanceCommand) command, times(1)).insertNewColumn(eq(scenarioSimulationContextLocal), eq(scenarioGridColumnMock3), eq(8), eq(Boolean.FALSE));
        String expectedDuplicatedLabelSecond = MULTIPART_VALUE + "_2" + DuplicateInstanceCommand.COPY_LABEL + "2";
        verify((DuplicateInstanceCommand) command, times(2)).setInstanceHeader(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(expectedDuplicatedLabelSecond), eq(FULL_CLASS_NAME + "_2"));
        List<String> expectedPropertyNameElementsSecond = Arrays.asList(expectedDuplicatedLabelSecond, SECOND);
        List<String> expectedPropertyNameElementsThird = Arrays.asList(expectedDuplicatedLabelSecond, THIRD);
        verify((DuplicateInstanceCommand) command, times(1)).setPropertyHeader(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(expectedPropertyNameElementsSecond), eq(VALUE_CLASS_NAME), eq(Optional.of(GRID_PROPERTY_TITLE + "_2")));
        verify((DuplicateInstanceCommand) command, times(1)).setPropertyHeader(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(expectedPropertyNameElementsThird), eq(VALUE_CLASS_NAME), eq(Optional.of(GRID_PROPERTY_TITLE + "_3")));
        verify(scenarioGridModelMock, times(1)).duplicateColumnValues(eq(COLUMN_NUMBER + 1), eq(0)); // The created column is mocked as gridColumnMock, with position 0
        verify(scenarioGridModelMock, times(1)).duplicateColumnValues(eq(COLUMN_NUMBER + 2), eq(0)); // The created column is mocked as gridColumnMock, with position 0
    }

}