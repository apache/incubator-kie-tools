/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.List;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

public interface VariablesEditorWidgetView {

    interface Presenter {

        void doSave();

        void notifyModelChanged();

        List<VariableRow> deserializeVariables( String s );

        String serializeVariables( List<VariableRow> variableRows );

        void setDataTypes( List<String> dataTypes, List<String> dataTypeDisplayNames);

        void addVariable();

        boolean isDuplicateName( String name );

        void removeVariable( VariableRow variableRow );

        ListBoxValues.ValueTester dataTypesTester();
    }

    void init( Presenter presenter );

    void doSave();

    int getVariableRowsCount();

    void setTableDisplayStyle();

    void setNoneDisplayStyle();

    void setVariablesDataTypes( ListBoxValues dataTypeListBoxValues );

    void setVariableRows( List<VariableRow> rows );

    List<VariableRow> getVariableRows();

    VariableListItemWidgetView getVariableWidget( int index );

    void setVisible( boolean visible );

    boolean isDuplicateName( String name );

    void removeVariable( VariableRow variableRow );
}
