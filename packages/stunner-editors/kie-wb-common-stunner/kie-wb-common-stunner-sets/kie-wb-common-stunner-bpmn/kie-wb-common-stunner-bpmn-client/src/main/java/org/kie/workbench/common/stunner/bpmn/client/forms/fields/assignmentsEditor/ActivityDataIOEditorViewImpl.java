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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
public class ActivityDataIOEditorViewImpl extends BaseModal implements ActivityDataIOEditorView,
                                                                       NotifyAddDataType {

    protected Presenter presenter;

    @Inject
    protected ActivityDataIOEditorWidget inputAssignmentsWidget;

    @Inject
    protected ActivityDataIOEditorWidget outputAssignmentsWidget;

    protected Button btnOk;

    private Button btnCancel;

    private Container container = new Container();

    private Row row = new Row();

    private Column column = new Column(ColumnSize.MD_12);

    public static final int EXPRESSION_MAX_DISPLAY_LENGTH = 65;

    public ActivityDataIOEditorViewImpl() {
        super();
    }

    public void init(final Presenter presenter) {
        this.presenter = presenter;
        container.setFluid(true);
        container.add(row);
        row.add(column);
        setTitle(StunnerFormsClientFieldsConstants.CONSTANTS.Data_IO());
        inputAssignmentsWidget.setVariableType(Variable.VariableType.INPUT);
        inputAssignmentsWidget.setNotifier(this);
        inputAssignmentsWidget.setAllowDuplicateNames(false,
                                                      StunnerFormsClientFieldsConstants.CONSTANTS.A_Data_Input_with_this_name_already_exists());
        column.add(inputAssignmentsWidget.getWidget());
        outputAssignmentsWidget.setVariableType(Variable.VariableType.OUTPUT);
        outputAssignmentsWidget.setNotifier(this);
        outputAssignmentsWidget.setAllowDuplicateNames(true,
                                                       "");
        column.add(outputAssignmentsWidget.getWidget());
        final Row btnRow = new Row();
        btnRow.getElement().getStyle().setMarginTop(10,
                                                    Style.Unit.PX);
        final Column btnColumn = new Column(ColumnSize.MD_12);
        btnRow.add(btnColumn);
        btnOk = new Button(StunnerFormsClientFieldsConstants.CONSTANTS.Ok());
        btnOk.setType(ButtonType.PRIMARY);
        btnOk.setPull(Pull.RIGHT);
        btnOk.addClickHandler(clickEvent -> presenter.handleOkClick());
        btnColumn.add(btnOk);
        btnCancel = new Button(StunnerFormsClientFieldsConstants.CONSTANTS.Cancel());
        btnCancel.setPull(Pull.RIGHT);
        btnCancel.addClickHandler(event -> presenter.handleCancelClick());
        btnColumn.add(btnCancel);
        container.add(btnRow);
        setWidth("1200px");
        setBody(container);
    }

    @Override
    public void onHide(final Event e) {
    }

    @Override
    public void setCustomViewTitle(final String name) {
        setTitle(name + " " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_IO());
    }

    @Override
    public void setDefaultViewTitle() {
        setTitle(StunnerFormsClientFieldsConstants.CONSTANTS.Data_IO());
    }

    @Override
    public void setInputAssignmentRows(final List<AssignmentRow> inputAssignmentRows) {
        inputAssignmentsWidget.setData(inputAssignmentRows);
    }

    @Override
    public void setOutputAssignmentRows(final List<AssignmentRow> outputAssignmentRows) {
        outputAssignmentsWidget.setData(outputAssignmentRows);
    }

    @Override
    public void setInputAssignmentsVisibility(final boolean visible) {
        inputAssignmentsWidget.setIsVisible(visible);
    }

    @Override
    public void setOutputAssignmentsVisibility(final boolean visible) {
        outputAssignmentsWidget.setIsVisible(visible);
    }

    @Override
    public void setIsInputAssignmentSingleVar(final boolean single) {
        inputAssignmentsWidget.setIsSingleVar(single);
    }

    @Override
    public void setIsOutputAssignmentSingleVar(final boolean single) {
        outputAssignmentsWidget.setIsSingleVar(single);
    }

    @Override
    public void hideView() {
        super.hide();
    }

    @Override
    public void showView() {
        super.show();
    }

    @Override
    public List<AssignmentRow> getInputAssignmentData() {
        return inputAssignmentsWidget.getData();
    }

    @Override
    public List<AssignmentRow> getOutputAssignmentData() {
        return outputAssignmentsWidget.getData();
    }

    @Override
    public void setPossibleInputAssignmentsDataTypes(final List<String> dataTypeDisplayNames) {
        ListBoxValues dataTypeListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CUSTOM_PROMPT,
                                                                StunnerFormsClientFieldsConstants.CONSTANTS.Edit() + " ",
                                                                presenter.dataTypesTester());
        dataTypeListBoxValues.addValues(dataTypeDisplayNames);
        inputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
    }

    @Override
    public void setPossibleOutputAssignmentsDataTypes(final List<String> dataTypeDisplayNames) {
        ListBoxValues dataTypeListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CUSTOM_PROMPT,
                                                                StunnerFormsClientFieldsConstants.CONSTANTS.Edit() + " ",
                                                                presenter.dataTypesTester(),
                                                                EXPRESSION_MAX_DISPLAY_LENGTH);
        dataTypeListBoxValues.addValues(dataTypeDisplayNames);
        outputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
    }

    @Override
    public void setInputAssignmentsProcessVariables(final List<String> processVariables) {
        ListBoxValues processVarListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.EXPRESSION_PROMPT,
                                                                  StunnerFormsClientFieldsConstants.CONSTANTS.Edit() + " ",
                                                                  presenter.processVarTester(),
                                                                  EXPRESSION_MAX_DISPLAY_LENGTH);
        processVarListBoxValues.addValues(processVariables);
        inputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
    }

    @Override
    public void setOutputAssignmentsProcessVariables(final List<String> processVariables) {
        ListBoxValues processVarListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.EXPRESSION_PROMPT,
                                                                  StunnerFormsClientFieldsConstants.CONSTANTS.Edit() + " ",
                                                                  presenter.processVarTester());
        processVarListBoxValues.addValues(processVariables);
        outputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
    }

    @Override
    public void setInputAssignmentsDisallowedNames(final Set<String> names) {
        inputAssignmentsWidget.setDisallowedNames(names,
                                                  StunnerFormsClientFieldsConstants.CONSTANTS.This_input_should_be_entered_as_a_property_for_the_task());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        btnOk.setEnabled(!readOnly);
        inputAssignmentsWidget.setReadOnly(readOnly);
        outputAssignmentsWidget.setReadOnly(readOnly);
    }

    @Override
    public void addDataType(String dataType, String oldType) {
        presenter.addDataType(dataType, oldType);
    }

    @Override
    public void notifyAdd(String dataType, String oldType, final ListBoxValues dataTypeListBoxValues) {
        presenter.addDataType(dataType, oldType);
    }
}
