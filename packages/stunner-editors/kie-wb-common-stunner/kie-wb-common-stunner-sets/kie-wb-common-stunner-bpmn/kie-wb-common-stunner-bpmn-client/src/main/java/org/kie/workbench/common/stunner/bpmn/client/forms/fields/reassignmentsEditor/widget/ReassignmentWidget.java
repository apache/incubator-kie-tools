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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.ReassignmentsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Dependent
public class ReassignmentWidget implements IsWidget,
                                           ReassignmentWidgetView.Presenter {

    private ReassignmentWidgetView view;

    private ClientTranslationService translationService;

    private List<ReassignmentRow> rows = new ArrayList<>();

    private ReassignmentsEditorWidget.GetReassignmentsCallback callback = null;

    @Inject
    public ReassignmentWidget(ReassignmentWidgetView view,
                              ClientTranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
        this.view.init(this, rows);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getNameHeader() {
        return translationService.getValue(StunnerBPMNConstants.REASSIGNMENTS_LABEL);
    }

    @Override
    public String getToUsersLabel() {
        return translationService.getValue(StunnerBPMNConstants.REASSIGNMENT_TO_USERS);
    }

    @Override
    public String getToGroupsLabel() {
        return translationService.getValue(StunnerBPMNConstants.REASSIGNMENT_TO_GROUPS);
    }

    @Override
    public String getExpiresAtLabel() {
        return translationService.getValue(StunnerBPMNConstants.REASSIGNMENT_EXPIRESAT);
    }

    @Override
    public String getTypeLabel() {
        return translationService.getValue(StunnerBPMNConstants.REASSIGNMENT_TYPE);
    }

    @Override
    public String getDeleteLabel() {
        return translationService.getValue(StunnerBPMNConstants.REASSIGNMENT_DELETE);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    @Override
    public void show() {
        view.show();
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public void ok() {
        if (callback != null) {
            List<ReassignmentValue> reassignments = getValue()
                    .stream()
                    .map(row -> row.toReassignmentValue())
                    .collect(Collectors.toList());
            callback.getData(new ReassignmentTypeListValue(reassignments));
        }
        view.hide();
    }

    @Override
    public List<ReassignmentRow> getValue() {
        return rows;
    }

    @Override
    public void setValue(List<ReassignmentRow> values) {
        setValue(values, false);
    }

    @Override
    public void setValue(List<ReassignmentRow> newValues, boolean fireEvents) {
        List<ReassignmentRow> oldValue = rows;
        rows = newValues;
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, rows);
        }
    }

    public void setCallback(final ReassignmentsEditorWidget.GetReassignmentsCallback callback) {
        this.callback = callback;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ReassignmentRow>> handler) {
        return view.asWidget().addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.asWidget().fireEvent(event);
    }
}
