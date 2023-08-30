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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget.ReassignmentWidget;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;

@Dependent
@Templated
public class ReassignmentsEditorWidget extends Composite implements HasValue<ReassignmentTypeListValue> {

    private ReassignmentTypeListValue reassignmentTypeListValue = new ReassignmentTypeListValue();

    @Inject
    @DataField
    private HTMLButtonElement reassignmentsButton;

    @Inject
    private ReassignmentWidget reassignmentWidget;

    @Inject
    @DataField
    private HTMLInputElement reassignmentsTextBox;

    public ReassignmentsEditorWidget() {

    }

    @PostConstruct
    public void init() {
        reassignmentsButton.addEventListener("click", event -> showReassignmentsDialog(), false);
        reassignmentsTextBox.addEventListener("click", event -> showReassignmentsDialog(), false);
    }

    void showReassignmentsDialog() {
        reassignmentWidget.setValue(reassignmentTypeListValue.getValues()
                                            .stream(
                                            ).map(r -> new ReassignmentRow(r))
                                            .collect(Collectors.toList()), true);
        reassignmentWidget.setCallback(data -> setValue(data,
                                                        true));
        reassignmentWidget.show();
    }

    @Override
    public void setValue(ReassignmentTypeListValue value, boolean fireEvents) {
        if (value != null) {
            ReassignmentTypeListValue oldValue = reassignmentTypeListValue;
            reassignmentTypeListValue = value;
            initTextBox();
            if (fireEvents) {
                ValueChangeEvent.fireIfNotEqual(this,
                                                oldValue,
                                                reassignmentTypeListValue);
            }
        }
    }

    private void initTextBox() {
        if (reassignmentTypeListValue == null) {
            reassignmentsTextBox.value = "zero reassignments";
        } else {
            reassignmentsTextBox.value = reassignmentTypeListValue.getValues().size() + " reassignments";
        }
    }

    @Override
    public ReassignmentTypeListValue getValue() {
        return reassignmentTypeListValue;
    }

    @Override
    public void setValue(ReassignmentTypeListValue value) {
        setValue(value,
                 false);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ReassignmentTypeListValue> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void setReadOnly(final boolean readOnly) {
        reassignmentWidget.setReadOnly(readOnly);
    }

    /**
     * Callback interface which should be implemented by callers to retrieve the
     * edited Reassignments data.
     */
    public interface GetReassignmentsCallback {

        void getData(ReassignmentTypeListValue reassignmentTypeListValue);
    }
}
