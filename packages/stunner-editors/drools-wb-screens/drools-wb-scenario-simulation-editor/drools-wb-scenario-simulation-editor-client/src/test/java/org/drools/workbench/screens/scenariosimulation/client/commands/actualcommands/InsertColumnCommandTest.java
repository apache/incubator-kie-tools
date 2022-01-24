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
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class InsertColumnCommandTest extends AbstractScenarioGridCommandTest {

    @Before
    public void setup() {
        super.setup();
        commandSpy = spy(new InsertColumnCommand(GridWidget.SIMULATION) {
            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup,
                                                                    FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader,
                                                                    ScenarioCellTextAreaSingletonDOMElementFactory factoryCell, String placeHolder) {
                return gridColumnMock;
            }
        });
        scenarioSimulationContextLocal.getStatus().setColumnId(COLUMN_ID);
        scenarioSimulationContextLocal.getStatus().setColumnIndex(COLUMN_INDEX);
    }

    @Test
    public void executeIfSelectedColumn_NotIsRightIsAsProperty() {
        when(gridColumnMock.isInstanceAssigned()).thenReturn(Boolean.TRUE);
        commonTest(false, true, 3);
    }

    @Test
    public void executeIfSelectedColumn_IsRightIsAsProperty() {
        when(gridColumnMock.isInstanceAssigned()).thenReturn(Boolean.TRUE);
        commonTest(true, true, 4);
    }

    @Test
    public void executeIfSelectedColumn_NotIsRightNotIsAsProperty() {
        commonTest(false, false, 2);
    }

    @Test
    public void executeIfSelectedColumn_NotIsAsProperty() {
        commonTest(true, false, 4);
    }

    protected void commonTest(boolean right, boolean asProperty, int expectedIndex) {
        scenarioSimulationContextLocal.getStatus().setRight(right);
        scenarioSimulationContextLocal.getStatus().setAsProperty(asProperty);
        ((InsertColumnCommand) commandSpy).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
        boolean cloneInstance = scenarioSimulationContextLocal.getStatus().isAsProperty() && gridColumnMock.isInstanceAssigned();
        verify((InsertColumnCommand) commandSpy, times(1)).insertNewColumn(eq(scenarioSimulationContextLocal), eq(gridColumnMock), eq(expectedIndex), eq(cloneInstance));
    }

}