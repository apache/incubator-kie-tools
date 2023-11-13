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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;

import static org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils.FindVariableUsagesFlag;
import static org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils.FindVariableUsagesFlag.CASE_FILE_VARIABLE;

@Dependent
@Renderer(type = VariablesEditorFieldType.class)
public class VariablesEditorFieldRenderer extends FieldRenderer<VariablesEditorFieldDefinition, DefaultFormGroup>
        implements VariablesEditorWidgetView.Presenter {

    private final SessionManager sessionManager;
    Map<String, String> mapDataTypeNamesToDisplayNames = null;
    Map<String, String> mapDataTypeDisplayNamesToNames = null;
    ListBoxValues dataTypeListBoxValues;

    private VariablesEditorWidgetView view;
    private List<String> dataTypes = new ArrayList<>();
    private Graph graph;
    private Path path;

    private Button lastOverlayOpened = null;

    private Set<FindVariableUsagesFlag> findVariableUsagesFlags;

    private static Set<String> defaultTagsSet = new HashSet<>(Arrays.asList("internal", "required", "readonly", "input", "output", "business_relevant", "tracked"));

    @Inject
    public VariablesEditorFieldRenderer(final VariablesEditorWidgetView variablesEditor,
                                        final SessionManager sessionManager) {
        this.view = variablesEditor;
        this.sessionManager = sessionManager;
        this.findVariableUsagesFlags = EnumSet.noneOf(FindVariableUsagesFlag.class);
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

        checkTagsNotEnabled();

        if (field != null && field.isCaseFileVariable()) {
            findVariableUsagesFlags = EnumSet.of(CASE_FILE_VARIABLE);
        }

        return formGroup;
    }

    @Override
    protected void setReadOnly(final boolean readOnly) {
        view.setReadOnly(readOnly);
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
        as.add(createVariableRow());
        VariableListItemWidgetView widget = view.getVariableWidget(view.getVariableRowsCount() - 1);

        widget.setDataTypes(dataTypeListBoxValues);
        widget.setParentWidget(this);
        checkTagsNotEnabled();
    }

    @Override
    public void setDataTypes(final List<String> dataTypes,
                             final List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
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
        Map<String, String> mapDataTypeNamesToDisplayNames = new HashMap<>();
        for (int i = 0; i < dataTypeDisplayNames.size(); i++) {
            mapDataTypeNamesToDisplayNames.put(dataTypes.get(i),
                                               dataTypeDisplayNames.get(i));
        }
        return mapDataTypeNamesToDisplayNames;
    }

    private Map<String, String> createMapDataTypeDisplayNamesToNames(final List<String> dataTypes,
                                                                     final List<String> dataTypeDisplayNames) {
        Map<String, String> mapDataTypeDisplayNamesToNames = new HashMap<>();
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
    public List<VariableRow> deserializeVariables(String s) {
        if (isGlobalVariables()) {
            s = StringUtils.preFilterVariablesTwoSemicolonForGenerics(s);
        } else {
            s = StringUtils.preFilterVariablesForGenerics(s);
        }
        List<VariableRow> variableRows = new ArrayList<>();
        if (s != null && !s.isEmpty()) {
            String[] vs = s.split(",");
            for (String v : vs) {
                if (!v.isEmpty()) {
                    v = StringUtils.postFilterForGenerics(v);
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
        List<Variable> variables = new ArrayList<>();
        for (VariableRow row : variableRows) {
            if (row.getName() != null && row.getName().length() > 0) {
                String dataType = getRowDataType(row);
                variables.add(new Variable(row.getName(),
                                           row.getVariableType(),
                                           dataType,
                                           row.getCustomDataType(),
                                           row.getTags()));
            }
        }

        return StringUtils.getStringForList(variables);
    }

    private String getRowDataType(VariableRow row) {
        if (row.getDataTypeDisplayName() != null &&
                mapDataTypeDisplayNamesToNames.containsKey(row.getDataTypeDisplayName())) {
            return mapDataTypeDisplayNamesToNames.get(row.getDataTypeDisplayName());
        } else {
            return row.getDataTypeDisplayName();
        }
    }

    /**
     * Tests whether a Row ID is duplicate across the whole process
     * @param id
     * @return
     */
    @Override
    public boolean isDuplicateID(final String id) {
        return VariableUtils.matchesProcessID(graph, id);
    }

    /**
     * Tests whether a Row name occurs more than once in the list of rows
     * @param name
     * @return
     */
    @Override
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

    @Override
    public boolean isBoundToNodes(String name) {
        return VariableUtils.findVariableUsages(graph, name, findVariableUsagesFlags).size() > 0;
    }

    @Override
    public void removeVariable(final VariableRow variableRow) {

        if (isBoundToNodes(variableRow.getName())) {
            // error popup was here
        } else {
            view.getVariableRows().remove(variableRow);
            doSave();
        }
    }

    @Override
    public ListBoxValues.ValueTester dataTypesTester() {
        return dataTypeDisplayName -> {
            if (mapDataTypeNamesToDisplayNames != null && mapDataTypeNamesToDisplayNames.containsKey(dataTypeDisplayName)) {
                return mapDataTypeNamesToDisplayNames.get(dataTypeDisplayName);
            } else {
                return null;
            }
        };
    }

    @Override
    public Path getDiagramPath() {
        return path;
    }

    private void checkTagsNotEnabled() {
        if (isGlobalVariables()) {
            this.view.setTagsNotEnabled();
        }
    }

    private boolean isGlobalVariables() {
        return this.field != null && !this.field.getId().equals("processVariables");
    }

    public void setLastOverlayOpened(final Button overlayCloseButton) {
        this.lastOverlayOpened = overlayCloseButton;
    }

    public Button getLastOverlayOpened() {
        return lastOverlayOpened;
    }

    @Override
    public void addDataType(String dataType, String oldType) {
        view.addDataType(dataType, oldType);
    }

    public void closeLastOverlay() {
        if (lastOverlayOpened != null) {
            lastOverlayOpened.click();
        }
    }

    static VariableRow createVariableRow() {
        VariableRow newVariable = new VariableRow();
        newVariable.setVariableType(Variable.VariableType.PROCESS);
        newVariable.setDataTypeDisplayName("Object");
        return newVariable;
    }

    public static Set<String> getDefaultTagsSet() {
        return defaultTagsSet;
    }
}
