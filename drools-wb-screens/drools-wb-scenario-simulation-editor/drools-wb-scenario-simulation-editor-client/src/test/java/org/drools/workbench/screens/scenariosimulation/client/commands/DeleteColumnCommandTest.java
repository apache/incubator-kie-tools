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

package org.drools.workbench.screens.scenariosimulation.client.commands;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DeleteColumnCommandTest extends AbstractCommandTest {

    private DeleteColumnCommand deleteColumnCommand;

    @Before
    public void setup() {
        super.setup();
        deleteColumnCommand = spy(new DeleteColumnCommand(mockScenarioGridModel, COLUMN_INDEX, COLUMN_GROUP, mockScenarioGridPanel, mockScenarioGridLayer) {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String title, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer, String placeHolder) {
                return mockGridColumn;
            }
        });
    }

    @Test
    public void execute() {
        when(mockScenarioGridModel.getGroupSize(COLUMN_GROUP)).thenReturn(4L);
        deleteColumnCommand.execute();
        verify(mockScenarioGridModel, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(mockScenarioGridModel, never()).insertColumn(anyInt(), anyObject());
        reset(mockScenarioGridModel);
        when(mockScenarioGridModel.getGroupSize(COLUMN_GROUP)).thenReturn(0L);
        deleteColumnCommand.execute();
        verify(deleteColumnCommand, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), eq(COLUMN_GROUP), eq(factMappingType), eq(mockScenarioGridPanel), eq(mockScenarioGridLayer), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(mockScenarioGridModel, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(mockScenarioGridModel, times(1)).insertColumn(eq(COLUMN_INDEX), eq(mockGridColumn));
    }
}