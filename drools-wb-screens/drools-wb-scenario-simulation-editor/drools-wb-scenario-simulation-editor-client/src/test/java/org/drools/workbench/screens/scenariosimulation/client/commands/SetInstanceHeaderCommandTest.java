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
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetInstanceHeaderCommandTest extends AbstractCommandTest {

    private SetInstanceHeaderCommand setInstanceHeaderCommand;

    @Mock
    private List<GridColumn<?>> mockGridColumns;

    @Before
    public void setup() {
        super.setup();
        when(mockGridColumns.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);
        when(scenarioGridModelMock.getColumns()).thenReturn(mockGridColumns);
        setInstanceHeaderCommand = spy(new SetInstanceHeaderCommand(scenarioGridModelMock, COLUMN_ID, VALUE, scenarioGridPanelMock, scenarioGridLayerMock) {

            @Override
            protected ScenarioHeaderTextBoxSingletonDOMElementFactory getHeaderTextBoxFactoryLocal() {
                return scenarioHeaderTextBoxSingletonDOMElementFactoryMock;
            }

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder) {
                return gridColumnMock;
            }

            @Override
            protected Optional<FactIdentifier> getFactIdentifierByColumnTitle(String columnTitle) {
                return Optional.empty();
            }
        });
    }

    @Test
    public void execute() {
        setInstanceHeaderCommand.execute();
        verify(gridColumnMock, atLeast(1)).getInformationHeaderMetaData();
        verify(informationHeaderMetaDataMock, atLeast(1)).setTitle(eq(VALUE));
        verify(gridColumnMock, atLeast(1)).setInstanceAssigned(eq(true));
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(eq(false));
        verify(scenarioGridModelMock, times(1)).updateColumnInstance(eq(COLUMN_INDEX), eq(gridColumnMock));
    }
}