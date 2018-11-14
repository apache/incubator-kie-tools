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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.Before;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class AbstractCommandTest {

    protected ScenarioGridModel scenarioGridModelMock;

    @Mock
    protected Simulation simulationMock;

    @Mock
    protected SimulationDescriptor simulationDescriptorMock;

    @Mock
    protected ScenarioGridPanel scenarioGridPanelMock;
    @Mock
    protected ScenarioGridLayer scenarioGridLayerMock;
    @Mock
    protected ScenarioGrid scenarioGridMock;

    @Mock
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;

    @Mock
    protected RightPanelPresenter rightPanelPresenterMock;

    @Mock
    protected EventBus eventBusMock;

    @Mock
    protected ScenarioGridColumn gridColumnMock;

    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock;
    @Mock
    protected ScenarioHeaderMetaData informationHeaderMetaDataMock;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock;
    @Mock
    protected ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactoryMock;
    @Mock
    protected List<GridRow> rowsMock;

    private List<GridColumn<?>> gridColumns = new ArrayList<>();

    protected final String COLUMN_ID = "COLUMN ID";

    protected final String COLUMN_GROUP = FactMappingType.EXPECT.name();

    protected final String FULL_PACKAGE = "test.scesim";

    protected final String VALUE = "VALUE";

    protected final String FULL_CLASS_NAME = FULL_PACKAGE + ".testclass";

    protected final String VALUE_CLASS_NAME = String.class.getName();

    protected final int ROW_INDEX = 2;
    protected final int COLUMN_INDEX = 3;

    protected final int FIRST_INDEX_LEFT = 2;
    protected final int FIRST_INDEX_RIGHT = 4;
    protected final FactMappingType factMappingType = FactMappingType.valueOf(COLUMN_GROUP);

    @Before
    public void setup() {

        when(simulationMock.getSimulationDescriptor()).thenReturn(simulationDescriptorMock);

        IntStream.range(0, 4).forEach(index -> gridColumns.add(gridColumnMock));

        when(informationHeaderMetaDataMock.getTitle()).thenReturn(VALUE);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(headerMetaDatasMock.get(1)).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getHeaderMetaData()).thenReturn(headerMetaDatasMock);
        when(gridColumnMock.getInformationHeaderMetaData()).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetaDataMock);
        GridData.Range range = new GridData.Range(FIRST_INDEX_LEFT, FIRST_INDEX_RIGHT - 1);

        scenarioGridModelMock = spy(new ScenarioGridModel(false) {
            {
                this.simulation = simulationMock;
                this.columns = gridColumns;
                this.rows = rowsMock;
            }

            @Override
            protected void commonAddColumn(int index, GridColumn<?> column) {
                //
            }

            @Override
            protected void commonAddColumn(final int index, final GridColumn<?> column, ExpressionIdentifier ei) {
                //
            }

            @Override
            protected void commonAddRow(int rowIndex) {
                //
            }

            @Override
            public List<GridColumn<?>> getColumns() {
                return columns;
            }

            @Override
            public Range getInstanceLimits(int columnIndex) {
                return range;
            }

            @Override
            public int getFirstIndexLeftOfGroup(String groupName) {
                return FIRST_INDEX_LEFT;
            }

            @Override
            public int getFirstIndexRightOfGroup(String groupName) {
                return FIRST_INDEX_RIGHT;
            }

            @Override
            public GridColumn<?> getSelectedColumn() {
                return gridColumnMock;
            }

            @Override
            public void deleteColumn(final GridColumn<?> column) {
                //
            }

            @Override
            public Range deleteRow(int rowIndex) {
                return range;
            }

            @Override
            public void insertRowGridOnly(final int rowIndex,
                                          final GridRow row, final Scenario scenario) {
                //
            }

            @Override
            public void insertRow(int rowIndex, GridRow row) {

            }

            @Override
            public List<GridRow> getRows() {
                return rowsMock;
            }
        });

        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridPanelMock.getScenarioGridLayer()).thenReturn(scenarioGridLayerMock);
    }
}