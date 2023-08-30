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

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;

public interface ActivityDataIOEditorWidgetView {

    interface Presenter {

        void handleAddClick();

        void addDataType(String dataType, String oldType);
    }

    void init(final Presenter presenter);

    void showOnlySingleEntryAllowed();

    int getAssignmentsCount();

    void setTableTitleInputSingle();

    void setTableTitleInputMultiple();

    void setTableTitleOutputSingle();

    void setTableTitleOutputMultiple();

    void setProcessVarAsSource();

    void setProcessVarAsTarget();

    void setTableDisplayStyle();

    void setNoneDisplayStyle();

    void setAssignmentRows(final List<AssignmentRow> rows);

    List<AssignmentRow> getAssignmentRows();

    AssignmentListItemWidgetView getAssignmentWidget(final int index);

    void setVisible(final boolean visible);

    void setReadOnly(final boolean readOnly);
}
