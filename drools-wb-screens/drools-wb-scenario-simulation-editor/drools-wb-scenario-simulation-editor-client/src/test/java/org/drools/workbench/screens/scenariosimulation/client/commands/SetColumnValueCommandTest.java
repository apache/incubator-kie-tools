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

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetColumnValueCommandTest extends AbstractCommandTest {

    private SetColumnValueCommand setColumnValueCommand;

    @Mock
    private List<GridColumn<?>> mockGridColumns;

    @Before
    public void setup() {
        super.setup();
        when(mockGridColumns.indexOf(mockGridColumn)).thenReturn(COLUMN_INDEX);
        when(mockScenarioGridModel.getColumns()).thenReturn(mockGridColumns);
        setColumnValueCommand = spy(new SetColumnValueCommand(mockScenarioGridModel, COLUMN_ID, FULL_PACKAGE, VALUE, VALUE_CLASS_NAME, mockScenarioGridPanel, mockScenarioGridLayer) {

            @Override
            protected ScenarioHeaderTextBoxSingletonDOMElementFactory getHeaderTextBoxFactoryLocal() {
                return scenarioHeaderTextBoxSingletonDOMElementFactoryMock;
            }

            @Override
            protected ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilderLocal(String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader) {
                return headerBuilderMock;
            }

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder) {
                return mockGridColumn;
            }
        });

    }

    @Test
    public void execute() {
        setColumnValueCommand.execute();
        verify(setColumnValueCommand, times(1)).getHeaderTextBoxFactoryLocal();
        verify(setColumnValueCommand, times(1)).getHeaderBuilderLocal(eq(COLUMN_GROUP), eq(factMappingType), eq(scenarioHeaderTextBoxSingletonDOMElementFactoryMock));
        verify(setColumnValueCommand, times(1)).getScenarioGridColumnLocal(eq(headerBuilderMock));
        verify(mockScenarioGridModel, times(1)).updateColumnType(eq(COLUMN_INDEX), eq(mockGridColumn), eq(FULL_PACKAGE), eq(VALUE), eq(VALUE_CLASS_NAME));
    }
}