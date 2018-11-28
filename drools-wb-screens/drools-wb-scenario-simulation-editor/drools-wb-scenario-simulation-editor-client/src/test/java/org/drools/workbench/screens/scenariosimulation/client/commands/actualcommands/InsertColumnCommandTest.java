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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class InsertColumnCommandTest extends AbstractScenarioSimulationCommandTest {

    private InsertColumnCommand insertColumnCommand;

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void executeNotIsRightIsAsProperty() {
        insertColumnCommand = spy(new InsertColumnCommand() {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer, String placeHolder) {
                return gridColumnMock;
            }
        });
        scenarioSimulationContext.setColumnId(COLUMN_ID);
        scenarioSimulationContext.setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContext.setRight(false);
        scenarioSimulationContext.setAsProperty(true);
        insertColumnCommand.execute(scenarioSimulationContext);
        verify(insertColumnCommand, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), eq(COLUMN_ID), eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioGridPanelMock), eq(scenarioGridLayerMock), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(scenarioGridModelMock, times(1)).insertColumn(eq(3), eq(gridColumnMock));
    }

    @Test
    public void executeIsRightIsAsProperty() {
        insertColumnCommand = spy(new InsertColumnCommand() {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer, String placeHolder) {
                return gridColumnMock;
            }
        });
        scenarioSimulationContext.setColumnId(COLUMN_ID);
        scenarioSimulationContext.setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContext.setRight(true);
        scenarioSimulationContext.setAsProperty(true);
        insertColumnCommand.execute(scenarioSimulationContext);
        verify(insertColumnCommand, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), eq(COLUMN_ID), eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioGridPanelMock), eq(scenarioGridLayerMock), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(scenarioGridModelMock, times(1)).insertColumn(eq(4), eq(gridColumnMock));
    }

    @Test
    public void executeNotIsRightNotIsAsProperty() {
        insertColumnCommand = spy(new InsertColumnCommand() {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer, String placeHolder) {
                return gridColumnMock;
            }
        });
        scenarioSimulationContext.setColumnId(COLUMN_ID);
        scenarioSimulationContext.setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContext.setRight(false);
        scenarioSimulationContext.setAsProperty(false);
        insertColumnCommand.execute(scenarioSimulationContext);
        verify(insertColumnCommand, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), eq(COLUMN_ID), eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioGridPanelMock), eq(scenarioGridLayerMock), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(scenarioGridModelMock, times(1)).insertColumn(eq(2), eq(gridColumnMock));
    }

    @Test
    public void executeIsRightNotIsAsProperty() {
        insertColumnCommand = spy(new InsertColumnCommand() {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer, String placeHolder) {
                return gridColumnMock;
            }
        });
        scenarioSimulationContext.setColumnId(COLUMN_ID);
        scenarioSimulationContext.setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContext.setRight(true);
        scenarioSimulationContext.setAsProperty(false);
        insertColumnCommand.execute(scenarioSimulationContext);
        verify(insertColumnCommand, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), eq(COLUMN_ID), eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioGridPanelMock), eq(scenarioGridLayerMock), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(scenarioGridModelMock, times(1)).insertColumn(eq(4), eq(gridColumnMock));
    }
}