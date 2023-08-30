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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("ActivityDataIOEditorWidget.html#widget")
public class ActivityDataIOEditorWidgetViewImpl extends Composite implements ActivityDataIOEditorWidgetView {

    private Presenter presenter;
    private static final String DATA_CONTENT_ATTR = "data-content";

    @Inject
    @DataField
    protected Button addVarButton;

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @DataField
    private HeadingElement tabletitle = Document.get().createHElement(3);

    @DataField
    protected TableCellElement nameth = Document.get().createTHElement();

    @DataField
    protected TableCellElement datatypeth = Document.get().createTHElement();

    @DataField
    private final TableCellElement processvarorexpressionth = Document.get().createTHElement();

    @Inject
    @DataField("pop-up")
    ParagraphElement popup;

    @Inject
    private JQueryProducer.JQuery<Popover> sourceTargetHelpPopover;

    @Inject
    @DataField("source-target")
    private Anchor sourceTargetHelp;

    /**
     * The list of assignments that currently exist.
     */
    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<AssignmentRow, AssignmentListItemWidgetViewImpl> assignments;

    @Inject
    protected Event<NotificationEvent> notification;

    private boolean readOnly = false;

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        addVarButton.setText(StunnerFormsClientFieldsConstants.CONSTANTS.Add());
        addVarButton.setIcon(IconType.PLUS);
        nameth.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Name());
        datatypeth.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Type());
    }

    @Override
    public void showOnlySingleEntryAllowed() {
        notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.Only_single_entry_allowed(),
                                                NotificationEvent.NotificationType.ERROR));
    }

    @Override
    public int getAssignmentsCount() {
        return assignments.getValue().size();
    }

    @Override
    public void setTableTitleInputSingle() {
        tabletitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Input_and_Assignment());
    }

    @Override
    public void setTableTitleInputMultiple() {
        tabletitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Inputs_and_Assignments());
    }

    @Override
    public void setTableTitleOutputSingle() {
        tabletitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Output_and_Assignment());
    }

    @Override
    public void setTableTitleOutputMultiple() {
        tabletitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Outputs_and_Assignments());
    }

    @Override
    public void setProcessVarAsSource() {
        sourceTargetHelp.setAttribute(DATA_CONTENT_ATTR,
                                      StunnerFormsClientFieldsConstants.CONSTANTS.assignment_source_help());
        popup.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Source());
        sourceTargetHelpPopover.wrap(sourceTargetHelp).popover();
    }

    @Override
    public void setProcessVarAsTarget() {
        sourceTargetHelp.setAttribute(DATA_CONTENT_ATTR,
                                      StunnerFormsClientFieldsConstants.CONSTANTS.assignment_target_help());
        popup.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Target());
        sourceTargetHelpPopover.wrap(sourceTargetHelp).popover();
    }

    @Override
    public void setTableDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.TABLE);
    }

    @Override
    public void setNoneDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void setAssignmentRows(final List<AssignmentRow> rows) {
        assignments.setValue(rows);
        setReadOnly(readOnly);
    }

    @Override
    public List<AssignmentRow> getAssignmentRows() {
        return assignments.getValue();
    }

    @Override
    public AssignmentListItemWidgetView getAssignmentWidget(final int index) {
        return assignments.getComponent(index);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        addVarButton.setEnabled(!readOnly);
        for (int i = 0; i < getAssignmentsCount(); i++) {
            getAssignmentWidget(i).setReadOnly(readOnly);
        }
    }

    @EventHandler("addVarButton")
    public void handleAddVarButton(final ClickEvent e) {
        presenter.handleAddClick();
    }
}
