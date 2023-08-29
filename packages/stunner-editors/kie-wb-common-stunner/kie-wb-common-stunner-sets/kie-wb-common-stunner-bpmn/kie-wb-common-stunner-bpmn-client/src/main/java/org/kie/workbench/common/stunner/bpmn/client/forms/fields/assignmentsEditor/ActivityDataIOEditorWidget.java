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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.util.VariableUsage;
import org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.kie.workbench.common.stunner.core.client.util.ClientUtils.getSelectedNode;

@Dependent
public class ActivityDataIOEditorWidget implements ActivityDataIOEditorWidgetView.Presenter {

    @Inject
    private ActivityDataIOEditorWidgetView view;
    @Inject
    private SessionManager sessionManager;

    ListBoxValues dataTypeListBoxValues;
    ListBoxValues processVarListBoxValues;

    private Variable.VariableType variableType = VariableType.INPUT;

    boolean isSingleVar = false;

    private boolean allowDuplicateNames = true;
    private String duplicateNameErrorMessage = "";

    private Set<String> disallowedNames = new HashSet<>();
    private String disallowedNameErrorMessage = "";

    private NotifyAddDataType notifier;

    // List of rows that won't be shown in the UI
    List<AssignmentRow> hiddenPropertyRows = new ArrayList<>();

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public void handleAddClick() {
        if (isSingleVar && view.getAssignmentRows().size() > 0) {
            view.showOnlySingleEntryAllowed();
        } else {
            addAssignment();
        }
    }

    public void setIsSingleVar(final boolean isSingleVar) {
        this.isSingleVar = isSingleVar;
        if (variableType.equals(VariableType.INPUT)) {
            view.setProcessVarAsSource();
            if (isSingleVar) {
                view.setTableTitleInputSingle();
            } else {
                view.setTableTitleInputMultiple();
            }
        } else {
            view.setProcessVarAsTarget();
            if (isSingleVar) {
                view.setTableTitleOutputSingle();
            } else {
                view.setTableTitleOutputMultiple();
            }
        }
    }

    public void setVariableType(final VariableType variableType) {
        this.variableType = variableType;
    }

    public void setAllowDuplicateNames(final boolean allowDuplicateNames,
                                       final String duplicateNameErrorMessage) {
        this.allowDuplicateNames = allowDuplicateNames;
        this.duplicateNameErrorMessage = duplicateNameErrorMessage;
    }

    private boolean getShowExpression() {
        return true;
    }

    private void addAssignment() {
        List<AssignmentRow> as = view.getAssignmentRows();
        if (as.isEmpty()) {
            view.setTableDisplayStyle();
        }
        AssignmentRow newAssignment = new AssignmentRow();
        newAssignment.setVariableType(variableType);
        as.add(newAssignment);
        AssignmentListItemWidgetView widget = view.getAssignmentWidget(view.getAssignmentsCount() - 1);

        widget.setDataTypes(dataTypeListBoxValues);
        widget.setProcessVariables(processVarListBoxValues);
        widget.setShowExpressions(getShowExpression());
        widget.setDisallowedNames(disallowedNames,
                                  disallowedNameErrorMessage);
        widget.setAllowDuplicateNames(allowDuplicateNames,
                                      duplicateNameErrorMessage);
        widget.setParentWidget(this);
    }

    public void removeAssignment(final AssignmentRow assignmentRow) {
        view.getAssignmentRows().remove(assignmentRow);
        if (view.getAssignmentRows().isEmpty()) {
            view.setNoneDisplayStyle();
        }
    }

    public void setData(final List<AssignmentRow> assignmentRows) {
        // Hide the properties which shouldn't be shown
        hiddenPropertyRows.clear();
        if (disallowedNames != null && !disallowedNames.isEmpty()) {
            for (int i = assignmentRows.size() - 1; i >= 0; i--) {
                AssignmentRow row = assignmentRows.get(i);
                if (row.getName() != null && !row.getName().isEmpty()) {
                    if (disallowedNames.contains(row.getName().toLowerCase())) {
                        assignmentRows.remove(i);
                        hiddenPropertyRows.add(0,
                                               row);
                    }
                }
            }
        }
        if (assignmentRows.isEmpty()) {
            view.setNoneDisplayStyle();
        } else {
            view.setTableDisplayStyle();
        }
        view.setAssignmentRows(assignmentRows);
        for (int i = 0; i < assignmentRows.size(); i++) {
            view.getAssignmentWidget(i).setParentWidget(this);
            view.getAssignmentWidget(i).setDisallowedNames(disallowedNames,
                                                           disallowedNameErrorMessage);
            view.getAssignmentWidget(i).setAllowDuplicateNames(allowDuplicateNames,
                                                               duplicateNameErrorMessage);
        }
    }

    public List<AssignmentRow> getData() {
        List<AssignmentRow> rows = new ArrayList<>();
        if (!view.getAssignmentRows().isEmpty()) {
            rows.addAll(view.getAssignmentRows());
        }
        if (!hiddenPropertyRows.isEmpty()) {
            rows.addAll(hiddenPropertyRows);
        }
        return rows;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setDataTypes(final ListBoxValues dataTypeListBoxValues) {
        this.dataTypeListBoxValues = dataTypeListBoxValues;
        for (int i = 0; i < view.getAssignmentsCount(); i++) {
            view.getAssignmentWidget(i).setDataTypes(dataTypeListBoxValues);
        }
    }

    public void setProcessVariables(final ListBoxValues processVarListBoxValues) {
        this.processVarListBoxValues = processVarListBoxValues;
        for (int i = 0; i < view.getAssignmentsCount(); i++) {
            AssignmentListItemWidgetView widget = view.getAssignmentWidget(i);
            widget.setProcessVariables(processVarListBoxValues);
            widget.setShowExpressions(getShowExpression());
        }
    }

    public void setDisallowedNames(final Set<String> disallowedNames,
                                   final String disallowedNameErrorMessage) {
        this.disallowedNames = disallowedNames;
        this.disallowedNameErrorMessage = disallowedNameErrorMessage;
        for (int i = 0; i < view.getAssignmentsCount(); i++) {
            view.getAssignmentWidget(i).setDisallowedNames(disallowedNames,
                                                           disallowedNameErrorMessage);
        }
    }

    @Override
    public void addDataType(String dataType, String oldType) {
        notifier.notifyAdd(dataType, oldType, dataTypeListBoxValues);
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
        List<AssignmentRow> as = view.getAssignmentRows();
        if (as != null && !as.isEmpty()) {
            int nameCount = 0;
            for (AssignmentRow row : as) {
                if (name.trim().compareTo(row.getName()) == 0) {
                    nameCount++;
                    if (nameCount > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isMultipleInstanceVariable(final String name) {
        ClientSession session = sessionManager.getCurrentSession();
        Diagram diagram = session.getCanvasHandler().getDiagram();
        Node selectedNode = getSelectedNode(diagram, sessionManager.getCurrentSession());
        Collection<VariableUsage> variableUsages = VariableUtils.findVariableUsages(selectedNode, name);
        return variableUsages.stream()
                .anyMatch(variableUsage -> variableUsage.getUsageType() == VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_DATA_INPUT ||
                        variableUsage.getUsageType() == VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_DATA_OUTPUT);
    }

    public void setIsVisible(final boolean visible) {
        view.setVisible(visible);
    }

    public Widget getWidget() {
        return (Widget) view;
    }

    public void setReadOnly(final boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    public void setNotifier(NotifyAddDataType notifier) {
        this.notifier = notifier;
    }
}
