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
package org.drools.workbench.screens.scenariosimulation.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.enterprise.event.Event;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandManager;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandRegistry;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AppendRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.Before;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationTest {

    protected ScenarioGridModel scenarioGridModelMock;
    @Mock
    protected Simulation simulationMock;
    @Mock
    protected Simulation clonedSimulationMock;
    @Mock
    protected SimulationDescriptor simulationDescriptorMock;
    @Mock
    protected ScenarioGridColumn gridColumnMock;
    @Mock
    protected List<GridRow> rowsMock;
    @Mock
    protected ScenarioGridPanel scenarioGridPanelMock;
    @Mock
    protected EventBus eventBusMock;
    @Mock
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;
    @Mock
    protected ScenarioSimulationView scenarioSimulationViewMock;
    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock;

    @Mock
    protected ScenarioHeaderMetaData informationHeaderMetaDataMock;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock;

    protected Event<NotificationEvent> notificationEvent = new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(final NotificationEvent event) {
            //Do nothing. Default implementation throws a RuntimeException
        }
    };

    @Mock
    protected ScenarioGridLayer scenarioGridLayerMock;

    @Mock
    protected ScenarioGrid scenarioGridMock;

    @Mock
    protected ScenarioCommandRegistry scenarioCommandRegistryMock;
    @Mock
    protected ScenarioCommandManager scenarioCommandManagerMock;
    @Mock
    protected ScenarioSimulationModel scenarioSimulationModelMock;

    protected ScenarioSimulationContext scenarioSimulationContext;
    protected AppendRowCommand appendRowCommandMock;

    protected final int ROW_INDEX = 2;
    protected final int COLUMN_INDEX = 3;

    protected final int FIRST_INDEX_LEFT = 2;
    protected final int FIRST_INDEX_RIGHT = 4;
    protected final String COLUMN_ID = "COLUMN ID";

    protected final String COLUMN_GROUP = FactMappingType.EXPECT.name();

    protected final String FULL_PACKAGE = "test.scesim";

    protected final String VALUE = "value";

    protected final String FULL_CLASS_NAME = FULL_PACKAGE + ".testclass";

    protected final String VALUE_CLASS_NAME = String.class.getName();

    protected final FactMappingType factMappingType = FactMappingType.valueOf(COLUMN_GROUP);
    protected List<GridColumn<?>> gridColumns = new ArrayList<>();

    @Before
    public void setup() {
        when(simulationMock.getSimulationDescriptor()).thenReturn(simulationDescriptorMock);
        IntStream.range(0, 4).forEach(index -> gridColumns.add(gridColumnMock));
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

            @Override
            public Range setCellValue(int rowIndex, int columnIndex, GridCellValue<?> value) {
                return range;
            }

            @Override
            public boolean validateHeaderUpdate(String value, int rowIndex, int columnIndex) {
                return true;
            }
        });

        when(scenarioGridMock.getEventBus()).thenReturn(eventBusMock);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridPanelMock.getScenarioGridLayer()).thenReturn(scenarioGridLayerMock);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        scenarioSimulationContext = new ScenarioSimulationContext(scenarioGridPanelMock);
        when(scenarioSimulationEditorPresenterMock.getView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        scenarioSimulationContext.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        when(simulationMock.cloneSimulation()).thenReturn(clonedSimulationMock);
        scenarioSimulationContext.getStatus().setSimulation(simulationMock);
        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationMock);
        when(scenarioCommandRegistryMock.undo(scenarioSimulationContext)).thenReturn(CommandResultBuilder.SUCCESS);
        when(scenarioCommandRegistryMock.redo(scenarioSimulationContext)).thenReturn(CommandResultBuilder.SUCCESS);

        appendRowCommandMock = spy(new AppendRowCommand() {

            {
                this.restorableStatus = scenarioSimulationContext.getStatus();
            }
            @Override
            public CommandResult<ScenarioSimulationViolation> execute(ScenarioSimulationContext context) {
                return CommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext context) {
                return CommandResultBuilder.SUCCESS;
            }


        });
        when(informationHeaderMetaDataMock.getTitle()).thenReturn(VALUE);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(headerMetaDatasMock.get(anyInt())).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getHeaderMetaData()).thenReturn(headerMetaDatasMock);
        when(gridColumnMock.getInformationHeaderMetaData()).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetaDataMock);
        when(simulationDescriptorMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
    }
}
