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

import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelView;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;

/**
 * This class represent the <b>Context</b> inside which the commands will be executed
 */
public class ScenarioSimulationContext {

    protected ScenarioGridModel model;
    protected ScenarioGridPanel scenarioGridPanel;
    protected ScenarioGridLayer scenarioGridLayer;
    protected ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter;
    protected RightPanelView.Presenter rightPanelPresenter;

    protected String columnId;
    protected int columnIndex;
    /**
     * When <code>true</code>, column will be inserted to the right of the given index (i.e. at position columnIndex +1), otherwise to the left (i.e. at position columnIndex)
     */
    protected boolean isRight;
    /**
     * When <code>true</code>, column will use the <b>instance</b> header of the original one, so to create a new "property" header under the same instance
     */
    protected boolean asProperty;

    protected String columnGroup;

    protected String fullPackage;
    protected String className;

    protected String value;
    protected String valueClassName;
    /**
     * When <code>true</code>, data from changed column will be kept
     */
    protected boolean keepData;

    protected int rowIndex;

    /**
     * The string to use for filtering in right panel
     */
    protected String filterTerm;

    /**
     * flag to decide which kind of filter (<b>equals</b> or <b>not equals</b>) is to be applied.
     * Default to false (= <b>equals</b> filter)
     */
    protected boolean notEqualsSearch = false;

    /**
     * The string to <b>eventually</b> use to select the property in the right panel
     */
    protected String propertyName;

    /**
     * Set this to <code>true</code> to disable the <code>RightPanel</code>
     */
    protected boolean disable = true;

    /**
     * Set this to <code>true</code> to open the dock in case it is closed
     */
    protected boolean openDock = false;

    protected PlaceManager placeManager;

    protected PlaceRequest rightPanelRequest;

    /**
     * This constructor set <code>ScenarioGridPanel</code>, <code>ScenarioGridLayer</code> and <code>ScenarioGridModel</code>
     * from the give <code>ScenarioGridPanel</code>
     * @param scenarioGridPanel
     */
    public ScenarioSimulationContext(ScenarioGridPanel scenarioGridPanel) {
        this.scenarioGridPanel = scenarioGridPanel;
        this.scenarioGridLayer = scenarioGridPanel.getScenarioGridLayer();
        this.model = scenarioGridLayer.getScenarioGrid().getModel();
    }

    public ScenarioSimulationEditorPresenter getScenarioSimulationEditorPresenter() {
        return scenarioSimulationEditorPresenter;
    }

    public void setScenarioSimulationEditorPresenter(ScenarioSimulationEditorPresenter scenarioSimulationEditorPresenter) {
        this.scenarioSimulationEditorPresenter = scenarioSimulationEditorPresenter;
    }

    public RightPanelView.Presenter getRightPanelPresenter() {
        return rightPanelPresenter;
    }

    public void setRightPanelPresenter(RightPanelView.Presenter rightPanelPresenter) {
        this.rightPanelPresenter = rightPanelPresenter;
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

    /**
     *
     * @param right set <code>true</code> to insert column to the right of the given index (i.e. at position columnIndex +1), otherwise to the left (i.e. at position columnIndex)
     */
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

    /**
     *
     * @param keepData set <code>true</code> to keep data in the column
     */
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

    /**
     *
     * @param filterTerm the string to use for filtering in right panel
     */
    public void setFilterTerm(String filterTerm) {
        this.filterTerm = filterTerm;
    }

    public boolean isNotEqualsSearch() {
        return notEqualsSearch;
    }

    /**
     *
     * @param notEqualsSearch set to <code>true</code> to have a <b>not equals</b> filter, <code>false</code> for an <b>equals</b> one
     */
    public void setNotEqualsSearch(boolean notEqualsSearch) {
        this.notEqualsSearch = notEqualsSearch;
    }

    public String getPropertyName() {
        return propertyName;
    }

    /**
     *
     * @param propertyName The string to <b>eventually</b> use to select the property in the right panel
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isDisable() {
        return disable;
    }

    /**
     *
     * @param disable Set this to <code>true</code> to disable the <code>RightPanel</code>
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isOpenDock() {
        return openDock;
    }

    /**
     *
     * @param openDock Set this to <code>true</code> to open the dock in case it is closed
     */
    public void setOpenDock(boolean openDock) {
        this.openDock = openDock;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public void setPlaceManager(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    public PlaceRequest getRightPanelRequest() {
        return rightPanelRequest;
    }

    public void setRightPanelRequest(PlaceRequest rightPanelRequest) {
        this.rightPanelRequest = rightPanelRequest;
    }
}
