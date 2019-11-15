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
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DeleteColumnCommandTest extends AbstractScenarioGridCommandTest {

    @Before
    public void setup() {
        super.setup();
        commandSpy = spy(new DeleteColumnCommand(GridWidget.SIMULATION) {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader,
                                                                    ScenarioCellTextAreaSingletonDOMElementFactory factoryCell, String placeHolder) {
                return gridColumnMock;
            }
        });
    }

    @Test
    public void executeAsProperty() {
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContextLocal.getStatus().setColumnGroup(COLUMN_GROUP);
        scenarioSimulationContextLocal.getStatus().setAsProperty(true);
        doReturn(4l).when(scenarioGridModelMock).getGroupSize(COLUMN_GROUP);
        commandSpy.execute(scenarioSimulationContextLocal);
        verify(scenarioGridModelMock, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModelMock, never()).insertColumn(anyInt(), anyObject());
        reset(scenarioGridModelMock);
        doReturn(0l).when(scenarioGridModelMock).getGroupSize(COLUMN_GROUP);
        commandSpy.execute(scenarioSimulationContextLocal);
        verify(commandSpy, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), anyString(), eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioHeaderTextBoxSingletonDOMElementFactoryTest), eq(scenarioCellTextAreaSingletonDOMElementFactoryTest), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(scenarioGridModelMock, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModelMock, times(1)).insertColumn(eq(COLUMN_INDEX), eq(gridColumnMock));
    }

    @Test
    public void executeNotAsProperty() {
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
        scenarioSimulationContextLocal.getStatus().setColumnGroup(COLUMN_GROUP);
        scenarioSimulationContextLocal.getStatus().setAsProperty(false);
        doReturn(4l).when(scenarioGridModelMock).getGroupSize(COLUMN_GROUP);
        commandSpy.execute(scenarioSimulationContextLocal);
        verify(scenarioGridModelMock, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModelMock, never()).insertColumn(anyInt(), anyObject());
        reset(scenarioGridModelMock);
        doReturn(0l).when(scenarioGridModelMock).getGroupSize(COLUMN_GROUP);
        commandSpy.execute(scenarioSimulationContextLocal);
        verify(commandSpy, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), anyString(), eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioHeaderTextBoxSingletonDOMElementFactoryTest), eq(scenarioCellTextAreaSingletonDOMElementFactoryTest), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(scenarioGridModelMock, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModelMock, times(1)).insertColumn(eq(COLUMN_INDEX  -1), eq(gridColumnMock));
    }
}