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
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;

/**
 * This class represent the <b>Context</b> inside which the commands will be executed
 */
public class ScenarioSimulationContext {

    private static final AtomicLong COUNTER_ID = new AtomicLong();
    private static final AtomicLong STATUS_COUNTER_ID = new AtomicLong();

    protected ScenarioGridModel model;
    protected ScenarioGridPanel scenarioGridPanel;
    protected ScenarioGridLayer scenarioGridLayer;
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
    protected TestToolsView.Presenter testToolsPresenter;
    protected SortedMap<String, FactModelTree> dataObjectFieldsMap;
    protected Set<String> dataObjectsInstancesName;

    protected Status status = new Status();

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

    protected PlaceManager placeManager;

    protected PlaceRequest testToolsRequest;

    /**
     * Auto-generated incremental identifier used  to uniquely identify each context
     */
    private final long id;

    /**
     * This constructor set <code>ScenarioGridPanel</code>, <code>ScenarioGridLayer</code> and <code>ScenarioGridModel</code>
     * from the give <code>ScenarioGridPanel</code>
     * @param scenarioGridPanel
     */
    public ScenarioSimulationContext(ScenarioGridPanel scenarioGridPanel) {
        this.scenarioGridPanel = scenarioGridPanel;
        this.scenarioGridLayer = scenarioGridPanel.getScenarioGridLayer();
        this.model = scenarioGridLayer.getScenarioGrid().getModel();
        id = COUNTER_ID.getAndIncrement();
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

    /**
     * Set the names of already existing Data Objects/Instances, used inside updateHeaderValidation
     * @param dataObjectsInstancesName
     */
    public void setDataObjectsInstancesName(Set<String> dataObjectsInstancesName) {
        this.dataObjectsInstancesName = dataObjectsInstancesName;
    }

    public ScenarioGridPanel getScenarioGridPanel() {
        return scenarioGridPanel;
    }

    public ScenarioGridModel getModel() {
        return model;
    }

    public ScenarioGridLayer getScenarioGridLayer() {
        return scenarioGridLayer;
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
    public void setStatusSimulationIfEmpty() {
        if (status.getSimulation() == null) {
            status.setSimulation(model.getSimulation().orElseThrow(IllegalStateException::new));
        }
    }

    public long getId() {
        return id;
    }

    public CollectionEditorSingletonDOMElementFactory getCollectionEditorSingletonDOMElementFactory() {
        return model.getCollectionEditorSingletonDOMElementFactory();
    }

    public ScenarioCellTextAreaSingletonDOMElementFactory getScenarioCellTextAreaSingletonDOMElementFactory() {
        return model.getScenarioCellTextAreaSingletonDOMElementFactory();
    }

    public ScenarioHeaderTextBoxSingletonDOMElementFactory getScenarioHeaderTextBoxSingletonDOMElementFactory() {
        return model.getScenarioHeaderTextBoxSingletonDOMElementFactory();
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
            toReturn.simulation = this.simulation.cloneSimulation();
            return toReturn;
        }
    }
}
