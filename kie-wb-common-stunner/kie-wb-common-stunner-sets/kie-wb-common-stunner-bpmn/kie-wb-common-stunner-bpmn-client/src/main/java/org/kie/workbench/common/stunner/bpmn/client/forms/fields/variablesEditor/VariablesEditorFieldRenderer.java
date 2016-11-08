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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldDefinition;

@Dependent
public class VariablesEditorFieldRenderer extends FieldRenderer<VariablesEditorFieldDefinition>
        implements VariablesEditorWidgetView.Presenter {

    private VariablesEditorWidgetView view;

    private Variable.VariableType variableType = Variable.VariableType.PROCESS;

    private List<String> dataTypes = new ArrayList<String>();

    private List<String> dataTypeDisplayNames = new ArrayList<String>();

    ListBoxValues dataTypeListBoxValues;

    @Inject
    public VariablesEditorFieldRenderer( VariablesEditorWidgetView variablesEditor ) {
        this.view = variablesEditor;
    }

    @Override
    public String getName() {
        return VariablesEditorFieldDefinition.CODE;
    }

    @Override
    public void initInputWidget() {
        view.init( this );
    }

    @Override
    public IsWidget getInputWidget() {
        return ( VariablesEditorWidgetViewImpl ) view;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        initInputWidget();
        return getPrettyViewWidget();
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {

    }

    @Override
    public String getSupportedCode() {
        return VariablesEditorFieldDefinition.CODE;
    }

    @Override
    public void doSave() {
        view.doSave();
    }

    @Override
    public void addVariable() {
        List<VariableRow> as = view.getVariableRows();
        if ( as.isEmpty() ) {
            view.setTableDisplayStyle();
        }
        VariableRow newVariable = new VariableRow();
        newVariable.setVariableType( variableType );
        as.add( newVariable );
        VariableListItemWidgetView widget = view.getVariableWidget( view.getVariableRowsCount() - 1 );
        widget.setDataTypes( dataTypeListBoxValues );
        widget.setParentWidget( this );
    }

    @Override
    public void setDataTypes( List<String> dataTypes, List<String> dataTypeDisplayNames ) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;
        dataTypeListBoxValues = new ListBoxValues( VariableListItemWidgetView.CUSTOM_PROMPT, "Edit" + " ", dataTypesTester() );
        dataTypeListBoxValues.addValues( dataTypeDisplayNames );
        view.setVariablesDataTypes( dataTypeListBoxValues );
    }

    @Override
    public void notifyModelChanged() {
        doSave();
    }

    @Override
    public List<VariableRow> deserializeVariables( String s ) {
        List<VariableRow> variableRows = new ArrayList<VariableRow>();
        if ( s != null && !s.isEmpty() ) {
            String[] vs = s.split( "," );
            for ( String v : vs ) {
                if ( !v.isEmpty() ) {
                    Variable var = Variable.deserialize( v, Variable.VariableType.PROCESS, dataTypes );
                    if ( var != null && var.getName() != null && !var.getName().isEmpty() ) {
                        variableRows.add( new VariableRow( var ) );
                    }
                }
            }
        }
        return variableRows;
    }

    @Override
    public String serializeVariables( List<VariableRow> variableRows ) {
        List<Variable> variables = new ArrayList<Variable>();
        for ( VariableRow row : variableRows ) {
            if ( row.getName() != null && row.getName().length() > 0 ) {
                variables.add( new Variable( row ) );
            }
        }
        return StringUtils.getStringForList( variables );
    }

    /**
     * Tests whether a Row name occurs more than once in the list of rows
     *
     * @param name
     * @return
     */
    public boolean isDuplicateName( String name ) {
        if ( name == null || name.trim().isEmpty() ) {
            return false;
        }
        List<VariableRow> as = view.getVariableRows();
        if ( as != null && !as.isEmpty() ) {
            int nameCount = 0;
            for ( VariableRow row : as ) {
                if ( name.trim().compareTo( row.getName() ) == 0 ) {
                    nameCount++;
                    if ( nameCount > 1 ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void removeVariable( VariableRow variableRow ) {
        view.getVariableRows().remove( variableRow );
        doSave();
    }

    @Override
    public ListBoxValues.ValueTester dataTypesTester() {
        return new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString( String userValue ) {
                // TODO
                //if (variablesData != null) {
                //    return variablesData.getDataTypeDisplayNameForUserString(userValue);
                //} else {
                //    return null;
                //}
                return null;
            }
        };
    }

}
