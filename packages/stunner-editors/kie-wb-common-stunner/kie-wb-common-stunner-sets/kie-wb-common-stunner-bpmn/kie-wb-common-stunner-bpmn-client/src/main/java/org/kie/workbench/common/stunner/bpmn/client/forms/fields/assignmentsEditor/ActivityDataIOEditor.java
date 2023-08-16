/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentData;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

@Dependent
public class ActivityDataIOEditor implements ActivityDataIOEditorView.Presenter {

    private boolean hasInputVars;
    private boolean isSingleInputVar;
    private boolean hasOutputVars;
    private boolean isSingleOutputVar;

    /**
     * Callback interface which should be implemented by callers to retrieve the
     * edited Assignments data.
     */
    public interface GetDataCallback {

        void getData(AssignmentData assignmentData);

        void addDataType(String dataType, String oldType);
    }

    GetDataCallback callback = null;

    @Inject
    ActivityDataIOEditorView view;

    private List<String> dataTypes = new ArrayList<>();

    private List<String> dataTypeDisplayNames = new ArrayList<>();

    private AssignmentData assignmentData;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setCallback(final GetDataCallback callback) {
        this.callback = callback;
    }

    @Override
    public void handleOkClick() {
        if (callback != null) {
            AssignmentData data = new AssignmentData(view.getInputAssignmentData(),
                                                     view.getOutputAssignmentData(),
                                                     dataTypes,
                                                     dataTypeDisplayNames);
            data.setVariableCountsString(hasInputVars,
                                         isSingleInputVar,
                                         hasOutputVars,
                                         isSingleOutputVar);
            callback.getData(data);
        }
        view.hideView();
    }

    @Override
    public void handleCancelClick() {
        view.hideView();
    }

    public void setDataTypes(final List<String> dataTypes,
                             final List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;
        view.setPossibleInputAssignmentsDataTypes(dataTypeDisplayNames);
        view.setPossibleOutputAssignmentsDataTypes(dataTypeDisplayNames);
    }

    public void setAssignmentData(final AssignmentData assignmentData) {
        this.assignmentData = assignmentData;
    }

    public void configureDialog(final String taskName,
                                final boolean hasInputVars,
                                final boolean isSingleInputVar,
                                final boolean hasOutputVars,
                                final boolean isSingleOutputVar) {
        this.hasInputVars = hasInputVars;
        this.isSingleInputVar = isSingleInputVar;
        this.hasOutputVars = hasOutputVars;
        this.isSingleOutputVar = isSingleOutputVar;
        if (taskName != null && !taskName.isEmpty()) {
            view.setCustomViewTitle(taskName);
        } else {
            view.setDefaultViewTitle();
        }
        view.setInputAssignmentsVisibility(hasInputVars);
        view.setOutputAssignmentsVisibility(hasOutputVars);
        view.setIsInputAssignmentSingleVar(isSingleInputVar);
        view.setIsOutputAssignmentSingleVar(isSingleOutputVar);
    }

    public void setDisallowedPropertyNames(final List<String> disallowedPropertyNames) {
        Set<String> propertyNames = new HashSet<>();
        if (disallowedPropertyNames != null) {
            for (String name : disallowedPropertyNames) {
                propertyNames.add(name.toLowerCase());
            }
        }
        view.setInputAssignmentsDisallowedNames(propertyNames);
    }

    public void setProcessVariables(final List<String> processVariables) {
        view.setInputAssignmentsProcessVariables(processVariables);
        view.setOutputAssignmentsProcessVariables(processVariables);
    }

    public void setInputAssignmentRows(final List<AssignmentRow> inputAssignmentRows) {
        view.setInputAssignmentRows(inputAssignmentRows);
    }

    public void setOutputAssignmentRows(final List<AssignmentRow> outputAssignmentRows) {
        view.setOutputAssignmentRows(outputAssignmentRows);
    }

    public void setReadOnly(final boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    public void show() {
        view.showView();
    }

    @Override
    public ListBoxValues.ValueTester dataTypesTester() {
        return userValue -> {
            if (assignmentData != null) {
                return assignmentData.getDataTypeDisplayNameForUserString(userValue);
            } else {
                return null;
            }
        };
    }

    @Override
    public ListBoxValues.ValueTester processVarTester() {
        return userValue -> null;
    }

    @Override
    public void addDataType(String dataType, String oldType) {
        callback.addDataType(dataType, oldType);
    }
}
