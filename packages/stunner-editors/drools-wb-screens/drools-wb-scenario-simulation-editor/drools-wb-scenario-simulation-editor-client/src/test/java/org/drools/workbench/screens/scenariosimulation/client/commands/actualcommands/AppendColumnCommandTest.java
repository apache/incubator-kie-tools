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
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FIRST_INDEX_RIGHT;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AppendColumnCommandTest extends AbstractScenarioGridCommandTest {

    @Before
    public void setup() {
        super.setup();
        commandSpy = spy(new AppendColumnCommand(GridWidget.SIMULATION) {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader,
                                                                    ScenarioCellTextAreaSingletonDOMElementFactory factoryCell, String placeHolder) {
                return gridColumnMock;
            }
        });
    }

    @Test
    public void execute() {
        scenarioSimulationContextLocal.getStatus().setColumnId(COLUMN_ID);
        scenarioSimulationContextLocal.getStatus().setColumnGroup(COLUMN_GROUP);
        when(backgroundGridWidgetSpy.isSelected()).thenReturn(false);
        when(scenarioGridWidgetSpy.isSelected()).thenReturn(true);
        commandSpy.execute(scenarioSimulationContextLocal);
        verify(commandSpy, times(1)).getScenarioGridColumnLocal(anyString(), anyString(), eq(COLUMN_ID), eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioHeaderTextBoxSingletonDOMElementFactorySpy), eq(scenarioCellTextAreaSingletonDOMElementFactorySpy), eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
        verify(scenarioGridModelMock, times(1)).getFirstIndexRightOfGroup(eq(COLUMN_GROUP));
        verify(scenarioGridModelMock, times(1)).insertColumn(eq(FIRST_INDEX_RIGHT), eq(gridColumnMock));
        verify(scenarioGridMock, times(1)).selectCurrentHeaderCellGroup();
    }
}