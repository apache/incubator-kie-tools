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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget;

import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;

public interface ReassignmentWidgetView extends IsWidget {

    interface Presenter extends HasValue<List<ReassignmentRow>> {

        String getNameHeader();

        String getToUsersLabel();

        String getToGroupsLabel();

        String getExpiresAtLabel();

        String getTypeLabel();

        String getDeleteLabel();

        void setReadOnly(boolean readOnly);

        void show();

        void hide();

        void ok();

        List<ReassignmentRow> getValue();

        void setValue(List<ReassignmentRow> values);
    }

    void init(final ReassignmentWidgetView.Presenter presenter, List<ReassignmentRow> rows);

    void addOrEdit(ReassignmentRow row);

    void delete(ReassignmentRow row);

    void refreshTable();

    void setReadOnly(final boolean readOnly);

    void show();

    void hide();
}
