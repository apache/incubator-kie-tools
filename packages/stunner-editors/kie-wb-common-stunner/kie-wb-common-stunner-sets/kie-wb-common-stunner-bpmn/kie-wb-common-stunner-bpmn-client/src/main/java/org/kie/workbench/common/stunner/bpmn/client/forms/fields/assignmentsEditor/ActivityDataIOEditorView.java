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

import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

public interface ActivityDataIOEditorView {

    interface Presenter {

        void handleOkClick();

        void handleCancelClick();

        ListBoxValues.ValueTester dataTypesTester();

        ListBoxValues.ValueTester processVarTester();

        void addDataType(String dataType, String oldType);
    }

    void init(final Presenter presenter);

    void hideView();

    void showView();

    void setCustomViewTitle(final String view);

    void setDefaultViewTitle();

    List<AssignmentRow> getInputAssignmentData();

    List<AssignmentRow> getOutputAssignmentData();

    void setInputAssignmentsVisibility(final boolean visible);

    void setOutputAssignmentsVisibility(final boolean visible);

    void setIsInputAssignmentSingleVar(final boolean single);

    void setIsOutputAssignmentSingleVar(final boolean single);

    void setPossibleInputAssignmentsDataTypes(final List<String> dataTypeDisplayNames);

    void setPossibleOutputAssignmentsDataTypes(final List<String> dataTypeDisplayNames);

    void setInputAssignmentsProcessVariables(final List<String> processVariables);

    void setOutputAssignmentsProcessVariables(final List<String> processVariables);

    void setInputAssignmentRows(final List<AssignmentRow> inputAssignmentRows);

    void setOutputAssignmentRows(final List<AssignmentRow> outputAssignmentRows);

    void setInputAssignmentsDisallowedNames(final Set<String> names);

    void setReadOnly(final boolean readOnly);

    void addDataType(String dataType, String oldType);
}
