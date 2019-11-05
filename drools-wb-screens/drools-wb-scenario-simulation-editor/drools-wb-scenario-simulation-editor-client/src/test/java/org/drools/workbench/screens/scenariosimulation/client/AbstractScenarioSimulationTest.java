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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.IntStream;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandManager;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandRegistry;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AppendRowCommand;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.BackgroundGridModel;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
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

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_NUMBER;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_ALIAS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_IDENTIFIER_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FIRST_INDEX_LEFT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FIRST_INDEX_RIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PACKAGE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_PROPERTY_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationTest {

    // Simulation tab
    protected ScenarioGridModel scenarioGridModelMock;

    @Mock
    protected Simulation simulationMock;
    @Mock
    protected Simulation clonedSimulationMock;

    @Mock
    protected ScesimModelDescriptor simulationDescriptorMock;
    @Mock
    protected ScenarioGridColumn gridColumnMock;
    @Mock
    protected List<GridRow> rowsMock;
    @Mock
    protected ScenarioGridPanel scenarioGridPanelMock;

    @Mock
    protected ScenarioGridLayer scenarioGridLayerMock;

    protected ScenarioGridWidget scenarioGridWidgetSpy;

    @Mock
    protected ScenarioGrid scenarioGridMock;

    @Mock
    protected List<GridColumn.HeaderMetaData> headerMetaDatasMock;

    @Mock
    protected ScenarioHeaderMetaData informationHeaderMetaDataMock;
    @Mock
    protected ScenarioHeaderMetaData propertyHeaderMetaDataMock;


    // Background tab
    @Mock
    protected BackgroundGridModel backgroundGridModelMock;

    @Mock
    protected Background backgroundMock;
    @Mock
    protected Background clonedBackgroundMock;

    @Mock
    protected ScenarioGridPanel backgroundGridPanelMock;

    @Mock
    protected ScenarioGridLayer backgroundGridLayerMock;

    protected ScenarioGridWidget backgroundGridWidgetSpy;

    @Mock
    protected ScenarioGrid backgroundGridMock;

    // Common
    @Mock
    protected EventBus eventBusMock;
    @Mock
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenterMock;
    @Mock
    protected ScenarioSimulationView scenarioSimulationViewMock;

    protected Event<NotificationEvent> notificationEvent = new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(final NotificationEvent event) {
            //Do nothing. Default implementation throws a RuntimeException
        }
    };

    @Mock
    protected ScenarioCommandRegistry scenarioCommandRegistryMock;
    @Mock
    protected ScenarioCommandManager scenarioCommandManagerMock;
    @Mock
    protected ScenarioSimulationModel scenarioSimulationModelMock;
    @Mock
    protected SimulationRunResult simulationRunResultMock;
    @Mock
    protected DataManagementStrategy dataManagementStrategyMock;
    @Mock
    protected SortedMap<String, FactModelTree> dataObjectFieldsMapMock;

    @Mock
    protected ViewsProvider viewsProviderMock;

    @Mock
    protected FactMapping factMappingMock;

    @Mock
    protected FactIdentifier factIdentifierMock;

    @Mock
    protected FactMappingValue factMappingValueMock;

    protected List<FactMappingValue> factMappingValuesLocal = new ArrayList<>();

    protected List<ScenarioWithIndex> scenarioWithIndexLocal;

    protected ScenarioSimulationContext scenarioSimulationContextLocal;
    protected AppendRowCommand appendRowCommandMock;
    protected CollectionEditorSingletonDOMElementFactory collectionEditorSingletonDOMElementFactoryTest;
    protected ScenarioCellTextAreaSingletonDOMElementFactory scenarioCellTextAreaSingletonDOMElementFactoryTest;
    protected ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactoryTest;

    protected final Set<FactIdentifier> factIdentifierSet = new HashSet<>();
    protected final List<FactMapping> factMappingLocal = new ArrayList<>();
    protected final FactMappingType factMappingType = FactMappingType.valueOf(COLUMN_GROUP);
    protected List<GridColumn<?>> gridColumns = new ArrayList<>();
    protected Settings settingsLocal;

    @Before
    public void setup() {
        settingsLocal = new Settings();
        scenarioWithIndexLocal = new ArrayList<>();

        scenarioGridWidgetSpy = spy(new ScenarioGridWidget() {
            {
                this.scenarioGridPanel = scenarioGridPanelMock;
            }
        });

        backgroundGridWidgetSpy = spy(new ScenarioGridWidget() {
            {
                this.scenarioGridPanel = backgroundGridPanelMock;
            }
        });

        when(simulationMock.getScesimModelDescriptor()).thenReturn(simulationDescriptorMock);
        when(simulationMock.getScenarioWithIndex()).thenReturn(scenarioWithIndexLocal);
        when(backgroundMock.getScesimModelDescriptor()).thenReturn(simulationDescriptorMock);
        when(simulationRunResultMock.getScenarioWithIndex()).thenReturn(scenarioWithIndexLocal);
        GridData.Range range = new GridData.Range(FIRST_INDEX_LEFT, FIRST_INDEX_RIGHT - 1);
        collectionEditorSingletonDOMElementFactoryTest = new CollectionEditorSingletonDOMElementFactory(scenarioGridPanelMock,
                                                                                                        scenarioGridLayerMock,
                                                                                                        scenarioGridMock,
                                                                                                        scenarioSimulationContextLocal, viewsProviderMock);
        scenarioCellTextAreaSingletonDOMElementFactoryTest = new ScenarioCellTextAreaSingletonDOMElementFactory(scenarioGridPanelMock,
                                                                                                                scenarioGridLayerMock,
                                                                                                                scenarioGridMock);
        scenarioHeaderTextBoxSingletonDOMElementFactoryTest = new ScenarioHeaderTextBoxSingletonDOMElementFactory(scenarioGridPanelMock,
                                                                                                                  scenarioGridLayerMock,
                                                                                                                  scenarioGridMock);

        scenarioGridModelMock = spy(new ScenarioGridModel(false) {
            {
                this.abstractScesimModel = simulationMock;
                this.columns = gridColumns;
                this.rows = rowsMock;
                this.collectionEditorSingletonDOMElementFactory = collectionEditorSingletonDOMElementFactoryTest;
                this.scenarioCellTextAreaSingletonDOMElementFactory = scenarioCellTextAreaSingletonDOMElementFactoryTest;
                this.scenarioHeaderTextBoxSingletonDOMElementFactory = scenarioHeaderTextBoxSingletonDOMElementFactoryTest;
                this.eventBus = eventBusMock;
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
                                          final GridRow row,
                                          final Scenario scenario) {
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
            public void validateInstanceHeaderUpdate(String instanceHeaderCellValue, int columnIndex, boolean isADataType) throws Exception {
                //
            }

            @Override
            public void validatePropertyHeaderUpdate(String propertyHeaderCellValue, int columnIndex, boolean isPropertyType) throws Exception {
                //
            }
        });
        when(scenarioGridMock.getEventBus()).thenReturn(eventBusMock);
        when(scenarioGridMock.getModel()).thenReturn(scenarioGridModelMock);
        when(scenarioGridMock.getLayer()).thenReturn(scenarioGridLayerMock);
        when(scenarioGridMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        when(scenarioGridLayerMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(scenarioGridPanelMock.getScenarioGridLayer()).thenReturn(scenarioGridLayerMock);
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);

        when(backgroundGridMock.getEventBus()).thenReturn(eventBusMock);
        when(backgroundGridMock.getModel()).thenReturn(backgroundGridModelMock);
        when(backgroundGridMock.getLayer()).thenReturn(backgroundGridLayerMock);
        when(backgroundGridMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE);
        when(backgroundGridLayerMock.getScenarioGrid()).thenReturn(backgroundGridMock);
        when(backgroundGridPanelMock.getScenarioGridLayer()).thenReturn(backgroundGridLayerMock);
        when(backgroundGridPanelMock.getScenarioGrid()).thenReturn(backgroundGridMock);

        final Point2D computedLocation = mock(Point2D.class);
        when(computedLocation.getX()).thenReturn(0.0);
        when(computedLocation.getY()).thenReturn(0.0);
        when(scenarioGridMock.getComputedLocation()).thenReturn(computedLocation);



        scenarioSimulationContextLocal = new ScenarioSimulationContext(scenarioGridWidgetSpy, backgroundGridWidgetSpy);
        scenarioSimulationContextLocal.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        scenarioSimulationContextLocal.getStatus().setSimulation(simulationMock);
        scenarioSimulationContextLocal.getStatus().setBackground(backgroundMock);
        scenarioSimulationContextLocal.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        scenarioSimulationContextLocal.setDataObjectFieldsMap(dataObjectFieldsMapMock);
        scenarioSimulationContextLocal.setSettings(settingsLocal);
        when(scenarioSimulationEditorPresenterMock.getView()).thenReturn(scenarioSimulationViewMock);
        when(scenarioSimulationEditorPresenterMock.getModel()).thenReturn(scenarioSimulationModelMock);
        scenarioSimulationContextLocal.setScenarioSimulationEditorPresenter(scenarioSimulationEditorPresenterMock);
        when(scenarioSimulationEditorPresenterMock.getDataManagementStrategy()).thenReturn(dataManagementStrategyMock);
        when(scenarioSimulationEditorPresenterMock.getContext()).thenReturn(scenarioSimulationContextLocal);

        when(simulationMock.cloneModel()).thenReturn(clonedSimulationMock);
        when(backgroundMock.cloneModel()).thenReturn(clonedBackgroundMock);
        scenarioSimulationContextLocal.getStatus().setSimulation(simulationMock);

        when(scenarioSimulationModelMock.getSimulation()).thenReturn(simulationMock);

        when(scenarioCommandRegistryMock.undo(scenarioSimulationContextLocal)).thenReturn(CommandResultBuilder.SUCCESS);
        when(scenarioCommandRegistryMock.redo(scenarioSimulationContextLocal)).thenReturn(CommandResultBuilder.SUCCESS);

        appendRowCommandMock = spy(new AppendRowCommand() {

            {
                this.restorableStatus = scenarioSimulationContextLocal.getStatus();
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
        when(informationHeaderMetaDataMock.getTitle()).thenReturn(MULTIPART_VALUE);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(propertyHeaderMetaDataMock.getMetadataType()).thenReturn(ScenarioHeaderMetaData.MetadataType.PROPERTY);
        when(propertyHeaderMetaDataMock.getTitle()).thenReturn(GRID_PROPERTY_TITLE);
        when(propertyHeaderMetaDataMock.getColumnGroup()).thenReturn(GRID_COLUMN_GROUP);
        when(propertyHeaderMetaDataMock.getColumnId()).thenReturn(GRID_COLUMN_ID);
        when(headerMetaDatasMock.get(anyInt())).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getHeaderMetaData()).thenReturn(headerMetaDatasMock);
        when(gridColumnMock.getInformationHeaderMetaData()).thenReturn(informationHeaderMetaDataMock);
        when(gridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetaDataMock);
        when(gridColumnMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        settingsLocal.setType(ScenarioSimulationModel.Type.RULE);
        IntStream.range(0, COLUMN_NUMBER).forEach(columnIndex -> {
            gridColumns.add(gridColumnMock);
            factMappingValuesLocal.add(factMappingValueMock);
            factIdentifierSet.add(factIdentifierMock);
            factMappingLocal.add(factMappingMock);
            when(simulationDescriptorMock.getFactMappingByIndex(columnIndex)).thenReturn(factMappingMock);
        });
        when(factIdentifierMock.getClassNameWithoutPackage()).thenReturn(CLASS_NAME);
        when(factIdentifierMock.getPackageWithoutClassName()).thenReturn(FULL_PACKAGE);
        when(factIdentifierMock.getClassName()).thenReturn(FULL_CLASS_NAME);
        when(factIdentifierMock.getName()).thenReturn(FACT_IDENTIFIER_NAME);
        when(simulationDescriptorMock.getFactIdentifiers()).thenReturn(factIdentifierSet);
        when(simulationDescriptorMock.getUnmodifiableFactMappings()).thenReturn(factMappingLocal);
        when(scenarioGridModelMock.nextColumnCount()).thenReturn(factMappingValuesLocal.size());
        when(factMappingMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        when(factMappingMock.getFactAlias()).thenReturn(FACT_ALIAS);
        when(factMappingMock.getGenericTypes()).thenReturn(new ArrayList<>());
        doReturn(factMappingMock).when(simulationDescriptorMock).addFactMapping(anyInt(), anyString(), anyObject(), anyObject());
        when(scenarioSimulationViewMock.getScenarioGridWidget()).thenReturn(scenarioGridWidgetSpy);
    }

    /**
     * Common method to add a new column in the model
     */
    protected void addNewColumn(ScenarioGridColumn gridColumn, List<GridColumn.HeaderMetaData> metaData, ScenarioHeaderMetaData informationHeaderMetaData,
                                ScenarioHeaderMetaData propertyHeaderMetaData, FactIdentifier factIdentifier, FactMapping factMapping, FactMappingValue factMappingValue,
                                int factStartingRange, int factEndingRange, int columnIndex, String value, String propertyTitle, String columnId, String factAlias,
                                String valueClassName, String propertyAlias, String fullClassName, String factIdentfierName) {
        when(gridColumn.getHeaderMetaData()).thenReturn(metaData);
        when(gridColumn.getInformationHeaderMetaData()).thenReturn(informationHeaderMetaData);
        when(gridColumn.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetaData);
        when(gridColumn.getFactIdentifier()).thenReturn(factIdentifier);
        when(gridColumn.isInstanceAssigned()).thenReturn(Boolean.TRUE);
        when(gridColumn.isPropertyAssigned()).thenReturn(Boolean.TRUE);

        when(scenarioGridModelMock.getInstanceLimits(columnIndex)).thenReturn(new GridData.Range(factStartingRange, factEndingRange));

        when(metaData.get(columnIndex)).thenReturn(informationHeaderMetaDataMock);

        when(informationHeaderMetaData.getTitle()).thenReturn(value);
        when(informationHeaderMetaData.getColumnGroup()).thenReturn(COLUMN_GROUP);

        when(propertyHeaderMetaData.getMetadataType()).thenReturn(ScenarioHeaderMetaData.MetadataType.PROPERTY);
        when(propertyHeaderMetaData.getTitle()).thenReturn(propertyTitle);
        when(propertyHeaderMetaData.getColumnGroup()).thenReturn(COLUMN_GROUP);
        when(propertyHeaderMetaData.getColumnId()).thenReturn(columnId);

        when(factMapping.getFactIdentifier()).thenReturn(factIdentifier);
        when(factMapping.getFactAlias()).thenReturn(factAlias);
        when(factMapping.getClassName()).thenReturn(valueClassName);

        final ExpressionElement factAliasExpressionElement = new ExpressionElement(factAlias);
        final ExpressionElement propertyAliasExpressionElement = new ExpressionElement(propertyAlias);
        List<ExpressionElement> expressionElements = new ArrayList<>();
        expressionElements.add(factAliasExpressionElement);
        expressionElements.add(propertyAliasExpressionElement);
        when(factMapping.getExpressionElements()).thenReturn(expressionElements);
        when(factMapping.getExpressionElementsWithoutClass()).thenReturn(Collections.singletonList(propertyAliasExpressionElement));

        when(factIdentifier.getClassName()).thenReturn(fullClassName);
        when(factIdentifier.getClassNameWithoutPackage()).thenReturn(fullClassName.substring(fullClassName.lastIndexOf(".") + 1));
        when(factIdentifier.getPackageWithoutClassName()).thenReturn(fullClassName.substring(0, fullClassName.lastIndexOf(".")));
        when(factIdentifier.getName()).thenReturn(factIdentfierName);

        gridColumns.add(gridColumn);
        factMappingValuesLocal.add(factMappingValue);
        factIdentifierSet.add(factIdentifier);
        factMappingLocal.add(factMapping);
        when(simulationDescriptorMock.getFactMappingByIndex(columnIndex)).thenReturn(factMapping);
        when(scenarioGridModelMock.nextColumnCount()).thenReturn(factMappingValuesLocal.size());
    }
}
