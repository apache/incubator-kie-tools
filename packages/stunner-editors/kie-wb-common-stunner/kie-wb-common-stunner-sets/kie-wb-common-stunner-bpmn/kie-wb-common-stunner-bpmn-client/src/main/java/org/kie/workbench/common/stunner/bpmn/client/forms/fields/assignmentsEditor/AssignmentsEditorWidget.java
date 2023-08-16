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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentData;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentParser;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.util.DataObjectUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated
public class AssignmentsEditorWidget extends Composite implements HasValue<String> {

    public static final String DEFAULT_IGNORED_PROPERTY_NAMES = "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify";
    @Inject
    protected ActivityDataIOEditor activityDataIOEditor;
    @Inject
    protected Event<NotificationEvent> notification;
    protected String assignmentsInfo;
    protected boolean hasInputVars = false;
    protected boolean isSingleInputVar = false;
    protected boolean hasOutputVars = false;
    protected boolean isSingleOutputVar = false;

    @Inject
    SessionManager canvasSessionManager;
    @Inject
    @DataField
    private Button assignmentsButton;
    @Inject
    @DataField
    private TextBox assignmentsTextBox;
    @Inject
    private GraphUtils graphUtils;
    @Inject
    private DataTypeNamesService clientDataTypesService;
    private BPMNDefinition bpmnModel;

    AssignmentsEditorWidget(final BPMNDefinition bpmnModel,
                            final String assignmentsInfo,
                            final boolean hasInputVars,
                            final boolean isSingleInputVar,
                            final boolean hasOutputVars,
                            final boolean isSingleOutputVar,
                            final SessionManager canvasSessionManager,
                            final GraphUtils graphUtils) {
        this.bpmnModel = bpmnModel;
        this.assignmentsInfo = assignmentsInfo;
        this.hasInputVars = hasInputVars;
        this.isSingleInputVar = isSingleInputVar;
        this.hasOutputVars = hasOutputVars;
        this.isSingleOutputVar = isSingleOutputVar;
        this.canvasSessionManager = canvasSessionManager;
        this.graphUtils = graphUtils;
    }

    public AssignmentsEditorWidget() {
    }

    // create bean to hold cache and inject to source
    @EventHandler("assignmentsButton")
    public void onClickAssignmentsButton(final ClickEvent clickEvent) {
        showAssignmentsDialog();
    }

    @EventHandler("assignmentsTextBox")
    public void onClickAssignmentsTextBox(final ClickEvent clickEvent) {
        showAssignmentsDialog();
    }

    @Override
    public String getValue() {
        return assignmentsInfo;
    }

    @Override
    public void setValue(final String value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        String oldValue = assignmentsInfo;
        assignmentsInfo = value;
        initTextBox();
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            assignmentsInfo);
        }
    }

    protected void initTextBox() {

        Map<String, String> assignmentsProperties = AssignmentParser.parseAssignmentsInfo(assignmentsInfo);

        String variableCountsString = getVariableCountsString(assignmentsProperties.get(AssignmentParser.DATAINPUT),
                                                              assignmentsProperties.get(AssignmentParser.DATAINPUTSET),
                                                              assignmentsProperties.get(AssignmentParser.DATAOUTPUT),
                                                              assignmentsProperties.get(AssignmentParser.DATAOUTPUTSET),
                                                              getProcessVariables(),
                                                              assignmentsProperties.get(AssignmentParser.ASSIGNMENTS),
                                                              getDisallowedPropertyNames());
        assignmentsTextBox.setText(variableCountsString);
    }

    String getVariableCountsString(final String datainput,
                                   final String datainputset,
                                   final String dataoutput,
                                   final String dataoutputset,
                                   final String processvars,
                                   final String assignments,
                                   final String disallowedpropertynames) {
        String inputvars = null;
        if (datainput != null) {
            inputvars = datainput;
        }
        if (datainputset != null) {
            inputvars = datainputset;
        }
        String outputvars = null;
        if (dataoutput != null) {
            outputvars = dataoutput;
        }
        if (dataoutputset != null) {
            outputvars = dataoutputset;
        }
        AssignmentData assignmentData = new AssignmentData(inputvars,
                                                           outputvars,
                                                           processvars,
                                                           assignments,
                                                           disallowedpropertynames);
        return assignmentData.getVariableCountsString(hasInputVars,
                                                      isSingleInputVar,
                                                      hasOutputVars,
                                                      isSingleOutputVar);
    }

    protected String getProcessVariables() {
        Diagram diagram = canvasSessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        Node selectedElement = getSelectedElement();
        String variables = VariableUtils.encodeProcessVariables(diagram, selectedElement);
        StringBuilder sb = new StringBuilder();
        if (!variables.isEmpty()) {
            sb.append(variables);
        }

        String dataObjects =
                DataObjectUtils.findDataObjects(canvasSessionManager.getCurrentSession(), graphUtils, getSelectedElement(), getParentIds()).stream()
                        .map(AssignmentsEditorWidget::dataObjectToProcessVariableFormat)
                        .collect(Collectors.joining(","));
        if (!dataObjects.isEmpty()) {
            sb.append(dataObjects);
        }

        return sb.toString();
    }

    String getDisallowedPropertyNames() {
        if (bpmnModel instanceof BaseUserTask) {
            return DEFAULT_IGNORED_PROPERTY_NAMES;
        } else {
            return "";
        }
    }

    protected Node getSelectedElement() {
        String elementUUID = getSelectedElementUUID(canvasSessionManager.getCurrentSession());
        if (elementUUID != null) {
            return canvasSessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph().getNode(elementUUID);
        }
        return null;
    }

    Set<String> getParentIds() {
        Set<String> parentIds = new HashSet<>();
        final Node selectedElement = getSelectedElement();
        Element parent = GraphUtils.getParent(selectedElement);

        if (selectedElement != null) {
            final String mainDiagramId = canvasSessionManager.getCurrentSession().getCanvasHandler().getDiagram().getMetadata().getCanvasRootUUID();
            parentIds.add(parent.getUUID());

            while (!parent.getUUID().equals(mainDiagramId)) {
                parent = graphUtils.getParent(parent.asNode());
                parentIds.add(parent.getUUID());
            }
        }
        return parentIds;
    }

    protected static String dataObjectToProcessVariableFormat(DataObject dataObject) {
        return dataObject.getName().getValue().replace("\n", "") + ":" + dataObject.getType().getValue().getType();
    }

    protected String getSelectedElementUUID(ClientSession clientSession) {
        if (clientSession instanceof EditorSession) {
            final SelectionControl selectionControl = ((EditorSession) clientSession).getSelectionControl();
            if (null != selectionControl) {
                final Collection<String> selectedItems = selectionControl.getSelectedItems();
                if (null != selectedItems && !selectedItems.isEmpty()) {
                    return selectedItems.iterator().next();
                }
            }
        }
        return null;
    }

    protected boolean isBPMNDefinition(Node node) {
        return node.getContent() instanceof View &&
                ((View) node.getContent()).getDefinition() instanceof BPMNDefinition;
    }

    protected void setBPMNModel(final BPMNDefinition bpmnModel) {
        this.bpmnModel = bpmnModel;

        if (bpmnModel instanceof DataIOModel) {
            DataIOModel dataIOModel = (DataIOModel) bpmnModel;
            hasInputVars = dataIOModel.hasInputVars();
            isSingleInputVar = dataIOModel.isSingleInputVar();
            hasOutputVars = dataIOModel.hasOutputVars();
            isSingleOutputVar = dataIOModel.isSingleOutputVar();
        } else {
            hasInputVars = false;
            isSingleInputVar = false;
            hasOutputVars = false;
            isSingleOutputVar = false;
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void showAssignmentsDialog() {
        // Get data types to show the editor
        getDataTypes();
    }

    protected void getDataTypes() {
        final String simpleDataTypes = "Boolean:Boolean,Float:Float,Integer:Integer,Object:Object,String:String";
        clientDataTypesService
                .call(getDiagramPath())
                .then(dataTypes -> {
                    String formattedDataTypes = formatDataTypes(dataTypes);
                    String allDataTypes = simpleDataTypes + "," + formattedDataTypes;
                    showDataIOEditor(allDataTypes);
                    return null;
                })
                .catch_(exception -> {
                    notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.Error_retrieving_datatypes(),
                                                            NotificationEvent.NotificationType.ERROR));
                    showDataIOEditor(simpleDataTypes);
                    return null;
                });
    }

    private Path getDiagramPath() {
        final Diagram diagram = canvasSessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        return diagram.getMetadata().getPath();
    }

    private Set<String> getSetDataTypes() {

        Map<String, String> assignmentsProperties = AssignmentParser.parseAssignmentsInfo(assignmentsInfo);

        String datainputset = assignmentsProperties.get(AssignmentParser.DATAINPUTSET);
        String dataoutputset = assignmentsProperties.get(AssignmentParser.DATAOUTPUTSET);

        Set<String> set = new HashSet<>();
        set.addAll(StringUtils.getSetDataTypes(datainputset));
        set.addAll(StringUtils.getSetDataTypes(dataoutputset));

        return set;
    }

    public void showDataIOEditor(final String datatypes) {
        String taskName = getTaskName();
        String processvars = getProcessVariables();

        Map<String, String> assignmentsProperties = AssignmentParser.parseAssignmentsInfo(assignmentsInfo);
        String datainput = assignmentsProperties.get(AssignmentParser.DATAINPUT);
        String datainputset = assignmentsProperties.get(AssignmentParser.DATAINPUTSET);
        String dataoutput = assignmentsProperties.get(AssignmentParser.DATAOUTPUT);
        String dataoutputset = assignmentsProperties.get(AssignmentParser.DATAOUTPUTSET);
        String assignments = assignmentsProperties.get(AssignmentParser.ASSIGNMENTS);

        String disallowedpropertynames = getDisallowedPropertyNames();

        String inputvars = null;
        if (datainput != null) {
            inputvars = datainput;
        }
        if (datainputset != null) {
            inputvars = datainputset;
        }
        String outputvars = null;
        if (dataoutput != null) {
            outputvars = dataoutput;
        }
        if (dataoutputset != null) {
            outputvars = dataoutputset;
        }
        AssignmentData assignmentData = new AssignmentData(inputvars,
                                                           outputvars,
                                                           processvars,
                                                           assignments,
                                                           datatypes,
                                                           disallowedpropertynames);

        assignmentData.setVariableCountsString(hasInputVars,
                                               isSingleInputVar,
                                               hasOutputVars,
                                               isSingleOutputVar);

        ActivityDataIOEditor.GetDataCallback callback = new ActivityDataIOEditor.GetDataCallback() {
            @Override
            public void getData(AssignmentData data) {
                String assignmentsInfoString = createAssignmentsInfoString(data);
                setValue(assignmentsInfoString,
                         true);
            }

            @Override
            public void addDataType(String dataType, String oldType) {
                if (dataType != null && !dataType.isEmpty()) {
                    clientDataTypesService.add(dataType, oldType);
                }
            }
        };

        activityDataIOEditor.setCallback(callback);
        activityDataIOEditor.setAssignmentData(assignmentData);
        activityDataIOEditor.setDisallowedPropertyNames(assignmentData.getDisallowedPropertyNames());
        activityDataIOEditor.setInputAssignmentRows(assignmentData.getAssignmentRows(Variable.VariableType.INPUT));
        activityDataIOEditor.setOutputAssignmentRows(assignmentData.getAssignmentRows(Variable.VariableType.OUTPUT));
        activityDataIOEditor.setDataTypes(assignmentData.getDataTypes(),
                                          assignmentData.getDataTypeDisplayNames());
        activityDataIOEditor.setProcessVariables(assignmentData.getProcessVariableNames().stream()
                                                         .distinct()
                                                         .collect(Collectors.toList()));
        activityDataIOEditor.configureDialog(taskName,
                                             hasInputVars,
                                             isSingleInputVar,
                                             hasOutputVars,
                                             isSingleOutputVar);
        activityDataIOEditor.show();
    }

    public void setReadOnly(final boolean readOnly) {
        activityDataIOEditor.setReadOnly(readOnly);
    }

    protected String getTaskName() {
        String taskName = "Task";
        if (bpmnModel instanceof BaseTask) {
            BaseTask task = (BaseTask) bpmnModel;
            if (task.getGeneral() != null && task.getGeneral().getName() != null &&
                    task.getGeneral().getName().getValue() != null && task.getGeneral().getName().getValue().length() > 0) {
                taskName = task.getGeneral().getName().getValue();
            }
        }
        return taskName;
    }

    protected String formatDataTypes(final List<String> dataTypes) {

        StringBuilder sb = new StringBuilder();
        if (dataTypes != null && !dataTypes.isEmpty()) {
            List<String> formattedDataTypes = new ArrayList<>(dataTypes.size());
            for (String dataType : dataTypes) {
                formattedDataTypes.add(StringUtils.createDataTypeDisplayName(dataType) + ":" + dataType);
            }
            Collections.sort(formattedDataTypes);
            for (String formattedDataType : formattedDataTypes) {
                sb.append(formattedDataType).append(',');
            }
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    protected String createAssignmentsInfoString(final AssignmentData assignmentData) {
        StringBuilder sb = new StringBuilder();
        String dataInput = "";
        String dataInputs = "";
        String dataOutput = "";
        String dataOutputs = "";
        if (hasInputVars) {
            if (isSingleInputVar) {
                dataInput = assignmentData.getInputVariablesString();
            } else {
                dataInputs = assignmentData.getInputVariablesString();
            }
        }
        if (hasOutputVars) {
            if (isSingleOutputVar) {
                dataOutput = assignmentData.getOutputVariablesString();
            } else {
                dataOutputs = assignmentData.getOutputVariablesString();
            }
        }

        sb.append(dataInput).append('|').append(dataInputs).append('|').append(dataOutput).append('|').
                append(dataOutputs)
                .append('|').append(assignmentData.getAssignmentsString());
        return sb.toString();
    }
}