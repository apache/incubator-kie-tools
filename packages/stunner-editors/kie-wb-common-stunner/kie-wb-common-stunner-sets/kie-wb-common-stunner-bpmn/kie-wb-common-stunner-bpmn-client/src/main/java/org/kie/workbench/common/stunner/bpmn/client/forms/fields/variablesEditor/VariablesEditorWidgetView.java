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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.uberfire.backend.vfs.Path;

public interface VariablesEditorWidgetView extends IsWidget {

    interface Presenter {

        void doSave();

        void notifyModelChanged();

        List<VariableRow> deserializeVariables(final String s);

        String serializeVariables(final List<VariableRow> variableRows);

        void setDataTypes(final List<String> dataTypes,
                          final List<String> dataTypeDisplayNames);

        void addVariable();

        boolean isDuplicateID(final String id);

        boolean isDuplicateName(final String name);

        boolean isBoundToNodes(final String name);

        void removeVariable(final VariableRow variableRow);

        ListBoxValues.ValueTester dataTypesTester();

        Path getDiagramPath();

        void setLastOverlayOpened(final Button overlayCloseButton);

        void closeLastOverlay();

        Button getLastOverlayOpened();

        void addDataType(String dataType, String oldType);

    }

    void init(final Presenter presenter);

    void doSave();

    int getVariableRowsCount();

    void setTableDisplayStyle();

    void setNoneDisplayStyle();

    void setVariablesDataTypes(final ListBoxValues dataTypeListBoxValues);

    void setVariableRows(final List<VariableRow> rows);

    List<VariableRow> getVariableRows();

    VariableListItemWidgetView getVariableWidget(final int index);

    void setVisible(final boolean visible);

    boolean isDuplicateName(final String name);

    void removeVariable(final VariableRow variableRow);

    void setReadOnly(final boolean readOnly);

    void setTagsNotEnabled();

    void addDataType(String dataType, String oldType);
}
