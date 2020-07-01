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
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioExpressionCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridWidget;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;

/**
 * This class represent the <b>Context</b> inside which the commands will be executed
 */
public class ScenarioSimulationContext {

    private static final AtomicLong COUNTER_ID = new AtomicLong();
    private static final AtomicLong STATUS_COUNTER_ID = new AtomicLong();

    protected final ScenarioGridWidget simulationGridWidget;
    protected final ScenarioGridWidget backgroundGridWidget;
    /**
     * Auto-generated incremental identifier used  to uniquely identify each context
     */
    private final long id;
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
    protected TestToolsView.Presenter testToolsPresenter;
    protected SortedMap<String, FactModelTree> dataObjectFieldsMap;
    protected Set<String> dataObjectsInstancesName;
    protected Settings settings;
    protected Status status = new Status();
    protected PlaceManager placeManager;
    protected PlaceRequest testToolsRequest;

    /**
     * This constructor set the <b>Simulation</b> and <b>Background</b> <code>ScenarioGridWidget</code>s
     * @param simulationGridWidget
     * @param backgroundGridWidget
     */
    public ScenarioSimulationContext(ScenarioGridWidget simulationGridWidget, ScenarioGridWidget backgroundGridWidget) {
        this.simulationGridWidget = simulationGridWidget;
        this.backgroundGridWidget = backgroundGridWidget;
        id = COUNTER_ID.getAndIncrement();
    }

    /**
     * Get the current <code>Status</code>
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Used to restore a previous <code>Status</code> inside this context
     * @param status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    public ScenarioSimulationEditorPresenter getScenarioSimulationEditorPresenter() {
        return scenarioSimulationEditorPresenter;
    }

    public void setScenarioSimulationEditorPresenter(ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter) {
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
    }

    public TestToolsView.Presenter getTestToolsPresenter() {
        return testToolsPresenter;
    }

    public void setTestToolsPresenter(TestToolsView.Presenter testToolsPresenter) {
        this.testToolsPresenter = testToolsPresenter;
    }

    public SortedMap<String, FactModelTree> getDataObjectFieldsMap() {
        return dataObjectFieldsMap;
    }

    public void setDataObjectFieldsMap(SortedMap<String, FactModelTree> dataObjectFieldsMap) {
        this.dataObjectFieldsMap = dataObjectFieldsMap;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * Set the names of already existing Data Objects/Instances, used inside updateHeaderValidation
     * @param dataObjectsInstancesName
     */
    public void setDataObjectsInstancesName(Set<String> dataObjectsInstancesName) {
        this.dataObjectsInstancesName = dataObjectsInstancesName;
    }

    public Optional<ScenarioGridWidget> getSelectedScenarioGridWidget() {
        if (backgroundGridWidget.isSelected() && simulationGridWidget.isSelected()) {
            throw new IllegalStateException("Simulation and Background grids can not be selected at the same time");
        }
        if (!backgroundGridWidget.isSelected() && !simulationGridWidget.isSelected()) {
            return Optional.empty();
        }
        // return the actually selected grid
        return backgroundGridWidget.isSelected() ? Optional.of(backgroundGridWidget) : Optional.of(simulationGridWidget);
    }

    public ScenarioGrid getSimulationGrid() {
        return simulationGridWidget.getScenarioGridPanel().getScenarioGrid();
    }

    public ScenarioGrid getBackgroundGrid() {
        return backgroundGridWidget.getScenarioGridPanel().getScenarioGrid();
    }

    public Optional<ScenarioGridPanel> getSelectedScenarioGridPanel() {
        return getSelectedScenarioGridWidget().map(ScenarioGridWidget::getScenarioGridPanel);
    }

    public ScenarioGridPanel getScenarioGridPanelByGridWidget(GridWidget gridWidget) {
        switch (gridWidget) {
            case SIMULATION:
                return simulationGridWidget.getScenarioGridPanel();
            case BACKGROUND:
                return backgroundGridWidget.getScenarioGridPanel();
            default:
                throw new IllegalArgumentException("Illegal GridWidget " + gridWidget);
        }
    }

    public AbstractScesimGridModel getAbstractScesimGridModelByGridWidget(GridWidget gridWidget) {
        switch (gridWidget) {
            case SIMULATION:
                return simulationGridWidget.getModel();
            case BACKGROUND:
                return backgroundGridWidget.getModel();
            default:
                throw new IllegalArgumentException("Illegal GridWidget " + gridWidget);
        }
    }

    public <T extends AbstractScesimData> AbstractScesimModel<T> getAbstractScesimModelByGridWidget(GridWidget gridWidget) {
        switch (gridWidget) {
            case SIMULATION:
                return (AbstractScesimModel<T>) status.getSimulation();
            case BACKGROUND:
                return (AbstractScesimModel<T>) status.getBackground();
            default:
                throw new IllegalArgumentException("Illegal GridWidget " + gridWidget);
        }
    }

    public Optional<AbstractScesimGridModel> getSelectedScenarioGridModel() {
        return getSelectedScenarioGridLayer().map(ScenarioGridLayer::getScenarioGrid).map(ScenarioGrid::getModel);
    }

    public Optional<ScenarioGridLayer> getSelectedScenarioGridLayer() {
        return getSelectedScenarioGridPanel().map(ScenarioGridPanel::getScenarioGridLayer);
    }

    public Optional<GridWidget> getSelectedGridWidget() {
        return getSelectedScenarioGridModel().map(AbstractScesimGridModel::getGridWidget);
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public void setPlaceManager(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    public PlaceRequest getTestToolsRequest() {
        return testToolsRequest;
    }

    public void setTestToolsRequest(PlaceRequest testToolsRequest) {
        this.testToolsRequest = testToolsRequest;
    }

    /**
     * Method to verify that <code>Status</code>' <b>simulation</b> is populated, since <code>Simulation</code>
     * is set inside the model <b>after</b> the creation ot the current instance
     */
    public void setStatusSimulationIfEmpty() throws IllegalStateException {
        if (status.getSimulation() == null) {
            final Optional<AbstractScesimModel> abstractScesimModel = simulationGridWidget.getModel().getAbstractScesimModel();
            status.setSimulation((Simulation) abstractScesimModel.orElseThrow(IllegalStateException::new));
        }
        if (status.getBackground() == null) {
            final Optional<AbstractScesimModel> abstractScesimModel = backgroundGridWidget.getModel().getAbstractScesimModel();
            status.setBackground((Background) abstractScesimModel.orElseThrow(IllegalStateException::new));
        }
    }

    public long getId() {
        return id;
    }

    public CollectionEditorSingletonDOMElementFactory getCollectionEditorSingletonDOMElementFactory(GridWidget gridWidget) {
        return getAbstractScesimGridModelByGridWidget(gridWidget).getCollectionEditorSingletonDOMElementFactory();
    }

    public ScenarioCellTextAreaSingletonDOMElementFactory getScenarioCellTextAreaSingletonDOMElementFactory(GridWidget gridWidget) {
        return getAbstractScesimGridModelByGridWidget(gridWidget).getScenarioCellTextAreaSingletonDOMElementFactory();
    }

    public ScenarioHeaderTextBoxSingletonDOMElementFactory getScenarioHeaderTextBoxSingletonDOMElementFactory(GridWidget gridWidget){
        return getAbstractScesimGridModelByGridWidget(gridWidget).getScenarioHeaderTextBoxSingletonDOMElementFactory();
    }

    public ScenarioExpressionCellTextAreaSingletonDOMElementFactory getScenarioExpressionCellTextAreaSingletonDOMElementFactory(GridWidget gridWidget) {
        return getAbstractScesimGridModelByGridWidget(gridWidget).getScenarioExpressionCellTextAreaSingletonDOMElementFactory();
    }

    public void setUndoButtonEnabledStatus(boolean enabled) {
        scenarioSimulationEditorPresenter.setUndoButtonEnabledStatus(enabled);
    }

    public void setRedoButtonEnabledStatus(boolean enabled) {
        scenarioSimulationEditorPresenter.setRedoButtonEnabledStatus(enabled);
    }

    /**
     * Class representing the variable <b>Status</b> of the context
     */
    public class Status {

        /**
         * Auto-generated incremental identifier used  to uniquely identify each status
         */
        private final long id;

        protected String columnId;
        protected int columnIndex;
        protected boolean isRight;
        protected boolean asProperty;

        protected String columnGroup;

        protected String fullPackage;
        protected String className;

        protected String value;
        protected String valueClassName;
        protected boolean keepData;

        protected String gridCellValue;

        protected int rowIndex;

        protected Simulation simulation;

        protected Background background;

        protected GridWidget currentGrid;

        /**
         * The string to use for filtering in right panel
         */
        protected String filterTerm;

        /**
         * flag to decide which kind of filter (<b>equals</b> or <b>not euals</b>) is to be applied.
         * Default to false (= <b>equals</b> filter)
         */
        protected boolean notEqualsSearch = false;

        /**
         * The <code>List</code> to <b>eventually</b> use to select the property in the test tools  panel
         */
        protected List<String> propertyNameElements;

        /**
         * The <b>content</b> of a header cell
         */
        protected String headerCellValue;

        /**
         * Disable the <b>TestTools</b>
         */
        protected boolean disable = true;

        /**
         * open the Right dock
         */
        protected boolean openDock = false;

        public Status() {
            this.id = STATUS_COUNTER_ID.getAndIncrement();
        }

        public long getId() {
            return id;
        }

        public String getColumnId() {
            return columnId;
        }

        public void setColumnId(String columnId) {
            this.columnId = columnId;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public boolean isAsProperty() {
            return asProperty;
        }

        public void setAsProperty(boolean asProperty) {
            this.asProperty = asProperty;
        }

        public String getColumnGroup() {
            return columnGroup;
        }

        public void setColumnGroup(String columnGroup) {
            this.columnGroup = columnGroup;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public boolean isRight() {
            return isRight;
        }

        public void setRight(boolean right) {
            isRight = right;
        }

        public String getFullPackage() {
            return fullPackage;
        }

        public void setFullPackage(String fullPackage) {
            this.fullPackage = fullPackage;
        }

        public boolean isKeepData() {
            return keepData;
        }

        public void setKeepData(boolean keepData) {
            this.keepData = keepData;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValueClassName() {
            return valueClassName;
        }

        public void setValueClassName(String valueClassName) {
            this.valueClassName = valueClassName;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public String getFilterTerm() {
            return filterTerm;
        }

        public void setFilterTerm(String filterTerm) {
            this.filterTerm = filterTerm;
        }

        public boolean isNotEqualsSearch() {
            return notEqualsSearch;
        }

        public void setNotEqualsSearch(boolean notEqualsSearch) {
            this.notEqualsSearch = notEqualsSearch;
        }

        public List<String> getPropertyNameElements() {
            return propertyNameElements;
        }

        public void setPropertyNameElements(List<String> propertyNameElements) {
            this.propertyNameElements = propertyNameElements;
        }

        public boolean isDisable() {
            return disable;
        }

        public void setDisable(boolean disable) {
            this.disable = disable;
        }

        public boolean isOpenDock() {
            return openDock;
        }

        public void setOpenDock(boolean openDock) {
            this.openDock = openDock;
        }

        public String getGridCellValue() {
            return gridCellValue;
        }

        public void setGridCellValue(String gridCellValue) {
            this.gridCellValue = gridCellValue;
        }

        public String getHeaderCellValue() {
            return headerCellValue;
        }

        public void setHeaderCellValue(String headerCellValue) {
            this.headerCellValue = headerCellValue;
        }

        public Simulation getSimulation() {
            return simulation;
        }

        public void setSimulation(Simulation simulation) {
            this.simulation = simulation;
        }

        public Background getBackground() {
            return background;
        }

        public void setBackground(Background background) {
            this.background = background;
        }

        public GridWidget getCurrentGrid() {
            return currentGrid;
        }

        public void setCurrentGrid(GridWidget currentGrid) {
            this.currentGrid = currentGrid;
        }

        public Status cloneStatus() {
            Status toReturn = new Status();
            toReturn.columnId = this.columnId;
            toReturn.columnIndex = this.columnIndex;
            toReturn.isRight = this.isRight;
            toReturn.asProperty = this.asProperty;
            toReturn.columnGroup = this.columnGroup;
            toReturn.fullPackage = this.fullPackage;
            toReturn.className = this.className;
            toReturn.value = this.value;
            toReturn.valueClassName = this.valueClassName;
            toReturn.keepData = this.keepData;
            toReturn.gridCellValue = this.gridCellValue;
            toReturn.rowIndex = this.rowIndex;
            toReturn.simulation = this.simulation.cloneModel();
            toReturn.background = this.background.cloneModel();
            toReturn.currentGrid = this.currentGrid;
            return toReturn;
        }
    }
}
