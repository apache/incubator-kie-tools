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

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PACKAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SetInstanceHeaderCommandTest extends AbstractScenarioSimulationCommandTest {

    @Mock
    private List<GridColumn<?>> mockGridColumns;

    @Before
    public void setup() {
        super.setup();
        when(mockGridColumns.indexOf(gridColumnMock)).thenReturn(COLUMN_INDEX);
        when(scenarioGridModelMock.getColumns()).thenReturn(mockGridColumns);
        command = spy(new SetInstanceHeaderCommand() {

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader, ScenarioCellTextAreaSingletonDOMElementFactory factoryCell, String placeHolder) {
                return gridColumnMock;
            }

            @Override
            protected Optional<FactIdentifier> getFactIdentifierByColumnTitle(String columnTitle, ScenarioSimulationContext context) {
                return Optional.empty();
            }
        });
        assertTrue(command.isUndoable());
        settingsLocal.setType(ScenarioSimulationModel.Type.RULE);
    }

    @Test
    public void executeNoColumn() {
        gridColumnMock = null;
        command.execute(scenarioSimulationContextLocal);
        verify((SetInstanceHeaderCommand) command, never()).executeIfSelectedColumn(scenarioSimulationContextLocal, gridColumnMock);
    }

    @Test
    public void executeDMN() {
        scenarioSimulationContextLocal.getStatus().setFullPackage(FULL_PACKAGE);
        scenarioSimulationContextLocal.getStatus().setClassName(VALUE_CLASS_NAME);
        settingsLocal.setType(ScenarioSimulationModel.Type.DMN);
        command.execute(scenarioSimulationContextLocal);
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(false));
        verify(gridColumnMock, atLeastOnce()).getInformationHeaderMetaData();
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(VALUE_CLASS_NAME));
        verify(gridColumnMock, times(1)).setInstanceAssigned(eq(true));
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(eq(false));
        verify(scenarioGridModelMock, times(1)).updateColumnInstance(eq(COLUMN_INDEX), eq(gridColumnMock));
    }

    @Test
    public void executeRULE() {
        scenarioSimulationContextLocal.getStatus().setFullPackage(FULL_PACKAGE);
        scenarioSimulationContextLocal.getStatus().setClassName(VALUE_CLASS_NAME);
        settingsLocal.setType(ScenarioSimulationModel.Type.RULE);
        command.execute(scenarioSimulationContextLocal);
        verify(gridColumnMock, times(1)).setEditableHeaders(eq(true));
        verify(gridColumnMock, atLeastOnce()).getInformationHeaderMetaData();
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(VALUE_CLASS_NAME));
        verify(gridColumnMock, times(1)).setInstanceAssigned(eq(true));
        verify(propertyHeaderMetaDataMock, times(1)).setReadOnly(eq(false));
        verify(scenarioGridModelMock, times(1)).updateColumnInstance(eq(COLUMN_INDEX), eq(gridColumnMock));
    }
}