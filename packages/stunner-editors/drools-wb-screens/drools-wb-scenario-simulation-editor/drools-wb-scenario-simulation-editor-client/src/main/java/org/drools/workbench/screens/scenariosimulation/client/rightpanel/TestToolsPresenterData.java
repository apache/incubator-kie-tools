/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;

public class TestToolsPresenterData {

    private SortedMap<String, FactModelTree> dataObjectFieldsMap;
    private SortedMap<String, FactModelTree> simpleJavaTypeFieldsMap;
    private SortedMap<String, FactModelTree> instanceFieldsMap;
    private SortedMap<String, FactModelTree> simpleJavaInstanceFieldsMap;
    private SortedMap<String, FactModelTree> hiddenFieldsMap;
    private Map<String, List<List<String>>> propertiesToHide;
    private GridWidget gridWidget;

    public TestToolsPresenterData(SortedMap<String, FactModelTree> dataObjectFieldsMap,
                                  SortedMap<String, FactModelTree> simpleJavaTypeFieldsMap,
                                  SortedMap<String, FactModelTree> instanceFieldsMap,
                                  SortedMap<String, FactModelTree> simpleJavaInstanceFieldsMap,
                                  SortedMap<String, FactModelTree> hiddenFieldsMap,
                                  Map<String, List<List<String>>> propertiesToHide, GridWidget gridWidget) {
        this.dataObjectFieldsMap = dataObjectFieldsMap;
        this.simpleJavaTypeFieldsMap = simpleJavaTypeFieldsMap;
        this.instanceFieldsMap = instanceFieldsMap;
        this.simpleJavaInstanceFieldsMap = simpleJavaInstanceFieldsMap;
        this.hiddenFieldsMap = hiddenFieldsMap;
        this.propertiesToHide = propertiesToHide;
        this.gridWidget = gridWidget;
    }

    public SortedMap<String, FactModelTree> getDataObjectFieldsMap() {
        return dataObjectFieldsMap;
    }

    public void setDataObjectFieldsMap(SortedMap<String, FactModelTree> dataObjectFieldsMap) {
        this.dataObjectFieldsMap = dataObjectFieldsMap;
    }

    public SortedMap<String, FactModelTree> getSimpleJavaTypeFieldsMap() {
        return simpleJavaTypeFieldsMap;
    }

    public void setSimpleJavaTypeFieldsMap(SortedMap<String, FactModelTree> simpleJavaTypeFieldsMap) {
        this.simpleJavaTypeFieldsMap = simpleJavaTypeFieldsMap;
    }

    public SortedMap<String, FactModelTree> getInstanceFieldsMap() {
        return instanceFieldsMap;
    }

    public void setInstanceFieldsMap(SortedMap<String, FactModelTree> instanceFieldsMap) {
        this.instanceFieldsMap = instanceFieldsMap;
    }

    public SortedMap<String, FactModelTree> getSimpleJavaInstanceFieldsMap() {
        return simpleJavaInstanceFieldsMap;
    }

    public void setSimpleJavaInstanceFieldsMap(SortedMap<String, FactModelTree> simpleJavaInstanceFieldsMap) {
        this.simpleJavaInstanceFieldsMap = simpleJavaInstanceFieldsMap;
    }

    public SortedMap<String, FactModelTree> getHiddenFieldsMap() {
        return hiddenFieldsMap;
    }

    public void setHiddenFieldsMap(SortedMap<String, FactModelTree> hiddenFieldsMap) {
        this.hiddenFieldsMap = hiddenFieldsMap;
    }

    public Map<String, List<List<String>>> getPropertiesToHide() {
        return propertiesToHide;
    }

    public void setPropertiesToHide(Map<String, List<List<String>>> propertiesToHide) {
        this.propertiesToHide = propertiesToHide;
    }

    public GridWidget getGridWidget() {
        return gridWidget;
    }

    public void setGridWidget(GridWidget gridWidget) {
        this.gridWidget = gridWidget;
    }
}
