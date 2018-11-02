/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldDefinition;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

@Dependent
public class VariablesEditorFieldRenderer extends FieldRenderer<VariablesEditorFieldDefinition, DefaultFormGroup>
        implements VariablesEditorWidgetView.Presenter {

    private final SessionManager sessionManager;
    private final VariableDeleteHandler deleteHandler;
    Map<String, String> mapDataTypeNamesToDisplayNames = null;
    Map<String, String> mapDataTypeDisplayNamesToNames = null;
    ListBoxValues dataTypeListBoxValues;
    private VariablesEditorWidgetView view;
    private Variable.VariableType variableType = Variable.VariableType.PROCESS;
    private List<String> dataTypes = new ArrayList<String>();
    private List<String> dataTypeDisplayNames = new ArrayList<String>();
    private Graph graph;
    private Path path;

    @Inject
    private ErrorPopupPresenter errorPopupPresenter;

    @Inject
    public VariablesEditorFieldRenderer(final VariablesEditorWidgetView variablesEditor,
                                        final SessionManager sessionManager,
                                        final VariableDeleteHandler deleteHandler) {
        this.view = variablesEditor;
        this.sessionManager = sessionManager;
        this.deleteHandler = deleteHandler;
    }

    @Override
    public String getName() {
        return VariablesEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        view.init(this);

        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        path = diagram.getMetadata().getPath();
        graph = diagram.getGraph();

        formGroup.render(view.asWidget(), field);

        return formGroup;
    }

    protected void setReadOnly(final boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    @Override
    public String getSupportedCode() {
        return VariablesEditorFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public void doSave() {
        view.doSave();
    }

    @Override
    public void addVariable() {
        List<VariableRow> as = view.getVariableRows();
        if (as.isEmpty()) {
            view.setTableDisplayStyle();
        }
        VariableRow newVariable = new VariableRow();
        newVariable.setVariableType(variableType);
        as.add(newVariable);
        VariableListItemWidgetView widget = view.getVariableWidget(view.getVariableRowsCount() - 1);
        widget.setDataTypes(dataTypeListBoxValues);
        widget.setParentWidget(this);
    }

    @Override
    public void setDataTypes(final List<String> dataTypes,
                             final List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;
        this.mapDataTypeNamesToDisplayNames = createMapDataTypeNamesToDisplayNames(dataTypes,
                                                                                   dataTypeDisplayNames);
        this.mapDataTypeDisplayNamesToNames = createMapDataTypeDisplayNamesToNames(dataTypes,
                                                                                   dataTypeDisplayNames);
        dataTypeListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT,
                                                  "Edit" + " ",
                                                  dataTypesTester());
        dataTypeListBoxValues.addValues(dataTypeDisplayNames);
        view.setVariablesDataTypes(dataTypeListBoxValues);
    }

    private Map<String, String> createMapDataTypeNamesToDisplayNames(final List<String> dataTypes,
                                                                     final List<String> dataTypeDisplayNames) {
        Map<String, String> mapDataTypeNamesToDisplayNames = new HashMap<String, String>();
        for (int i = 0; i < dataTypeDisplayNames.size(); i++) {
            mapDataTypeNamesToDisplayNames.put(dataTypes.get(i),
                                               dataTypeDisplayNames.get(i));
        }
        return mapDataTypeNamesToDisplayNames;
    }

    private Map<String, String> createMapDataTypeDisplayNamesToNames(final List<String> dataTypes,
                                                                     final List<String> dataTypeDisplayNames) {
        Map<String, String> mapDataTypeDisplayNamesToNames = new HashMap<String, String>();
        for (int i = 0; i < dataTypes.size(); i++) {
            mapDataTypeDisplayNamesToNames.put(dataTypeDisplayNames.get(i),
                                               dataTypes.get(i));
        }
        return mapDataTypeDisplayNamesToNames;
    }

    @Override
    public void notifyModelChanged() {
        doSave();
    }

    @Override
    public List<VariableRow> deserializeVariables(final String s) {
        List<VariableRow> variableRows = new ArrayList<VariableRow>();
        if (s != null && !s.isEmpty()) {
            String[] vs = s.split(",");
            for (String v : vs) {
                if (!v.isEmpty()) {
                    Variable var = Variable.deserialize(v,
                                                        Variable.VariableType.PROCESS,
                                                        dataTypes);
                    if (var != null && var.getName() != null && !var.getName().isEmpty()) {
                        variableRows.add(new VariableRow(var,
                                                         mapDataTypeNamesToDisplayNames));
                    }
                }
            }
        }
        return variableRows;
    }

    @Override
    public String serializeVariables(final List<VariableRow> variableRows) {
        List<Variable> variables = new ArrayList<Variable>();
        for (VariableRow row : variableRows) {
            if (row.getName() != null && row.getName().length() > 0) {
                variables.add(new Variable(row,
                                           mapDataTypeDisplayNamesToNames));
            }
        }
        return StringUtils.getStringForList(variables);
    }

    /**
     * Tests whether a Row name occurs more than once in the list of rows
     * @param name
     * @return
     */
    public boolean isDuplicateName(final String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        List<VariableRow> as = view.getVariableRows();
        if (as != null && !as.isEmpty()) {
            int nameCount = 0;
            String currName = name.trim();
            for (VariableRow row : as) {
                String rowName = row.getName();
                if (rowName != null && currName.compareTo(rowName.trim()) == 0) {
                    nameCount++;
                    if (nameCount > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void removeVariable(final VariableRow variableRow) {

        if (deleteHandler.isVariableBoundToNodes(graph,
                                                 variableRow.getName())) {
            errorPopupPresenter.showMessage(StunnerFormsClientFieldsConstants.INSTANCE.DeleteDiagramVariableError());
        } else {
            view.getVariableRows().remove(variableRow);
            doSave();
        }
    }

    @Override
    public ListBoxValues.ValueTester dataTypesTester() {
        return new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString(String dataTypeDisplayName) {
                if (mapDataTypeNamesToDisplayNames != null && mapDataTypeNamesToDisplayNames.containsKey(dataTypeDisplayName)) {
                    return mapDataTypeNamesToDisplayNames.get(dataTypeDisplayName);
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public Path getDiagramPath() {
        return path;
    }
}
