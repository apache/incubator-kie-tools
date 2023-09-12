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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor;

import javax.enterprise.context.Dependent;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.CorrelationsEditor;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.diagram.CorrelationsValue;

@Dependent
@Templated
public class CorrelationsFieldEditorWidget extends Composite implements HasValue<CorrelationsValue> {

    protected CorrelationsValue correlationsValue;

    @Inject
    protected CorrelationsEditor correlationsEditor;

    @Inject
    @DataField
    protected Button correlationsButton;

    @Inject
    @DataField
    protected TextBox correlationsTextBox;

    public CorrelationsFieldEditorWidget() {
    }

    public CorrelationsFieldEditorWidget(final CorrelationsValue correlationsValue) {
        this.correlationsValue = correlationsValue;
    }

    @Override
    public CorrelationsValue getValue() {
        return correlationsValue;
    }

    @Override
    public void setValue(final CorrelationsValue value) {
        setValue(value, false);
    }

    @Override
    public void setValue(final CorrelationsValue value, final boolean fireEvents) {
        CorrelationsValue oldValue = copyCorrelationsValue(correlationsValue);
        correlationsValue = value;
        setCorrelationsCount(value);
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            correlationsValue);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<CorrelationsValue> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    protected void setCorrelationsCount(CorrelationsValue correlationsValue) {
        int correlationsCount = 0;

        if (correlationsValue != null) {
            correlationsCount = correlationsValue.getCorrelations().size();
        }

        String correlationsString = buildCorrelationsCountString(correlationsCount);
        correlationsTextBox.setText(correlationsString);
    }

    protected String buildCorrelationsCountString(final int correlationsCount) {
        if (correlationsCount == 0) {
            return StunnerFormsClientFieldsConstants.CONSTANTS.No_Correlations();
        } else if (correlationsCount == 1) {
            return StunnerFormsClientFieldsConstants.CONSTANTS.Correlation();
        } else {
            return correlationsCount + " " + StunnerFormsClientFieldsConstants.CONSTANTS.Correlations();
        }
    }

    protected CorrelationsValue copyCorrelationsValue(CorrelationsValue correlationsValue) {
        CorrelationsValue copy = new CorrelationsValue();

        if (correlationsValue != null) {
            for (Correlation correlation : correlationsValue.getCorrelations()) {
                Correlation correlationCopy = new Correlation();
                correlationCopy.setId(correlation.getId());
                correlationCopy.setName(correlation.getName());
                correlationCopy.setPropertyId(correlation.getPropertyId());
                correlationCopy.setPropertyName(correlation.getPropertyName());
                correlationCopy.setPropertyType(correlation.getPropertyType());
                copy.addCorrelation(correlationCopy);
            }
        }

        return copy;
    }

    protected void showCorrelationEditor() {
        correlationsEditor.setCorrelationsValue(copyCorrelationsValue(correlationsValue));

        CorrelationsEditor.GetDataCallback callback = value -> setValue(value, true);
        correlationsEditor.setCallback(callback);

        correlationsEditor.show();
    }

    @EventHandler("correlationsButton")
    public void onClickCorrelationsButton(final ClickEvent clickEvent) {
        showCorrelationEditor();
    }

    @EventHandler("correlationsTextBox")
    public void onClickCorrelationsTextBox(final ClickEvent clickEvent) {
        showCorrelationEditor();
    }
}
