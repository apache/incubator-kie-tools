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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;

@Dependent
@Templated("ComboBoxWidget.html")
public class ComboBoxWidgetViewImpl extends Composite implements ComboBoxWidgetView,
                                                                 ComboBoxView.ModelPresenter,
                                                                 HasValue<String> {

    @Inject
    @DataField
    protected TextBox customValueField;

    @DataField
    protected ValueListBox<String> valueField = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return (object != null) ? object : "";
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    protected ComboBox valueComboBox;

    private ListBoxValues listBoxValues;

    private String currentValue;

    protected class DataModel {

        private String customValue;
        private String nonCustomValue;

        private void setModelValue(String value) {
            if (isCustomValue(value)) {
                customValue = value;
                nonCustomValue = null;
            } else {
                nonCustomValue = value;
                customValue = null;
            }
        }

        private boolean isCustomValue(String value) {
            return (value != null && !value.isEmpty() && listBoxValues != null && listBoxValues.isCustomValue(value));
        }
    }

    protected DataModel dataModel = new DataModel();

    @PostConstruct
    public void init() {
        // Configure valueField and customValueField controls
        valueComboBox.init(this,
                           true,
                           valueField,
                           customValueField,
                           false,
                           false,
                           CUSTOM_PROMPT,
                           ENTER_TYPE_PROMPT);
    }

    @Override
    public String getValue() {
        return currentValue;
    }

    @Override
    public void setValue(final String value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final String newValue,
                         final boolean fireEvents) {
        String oldValue = currentValue;
        currentValue = listBoxValues.getValueForDisplayValue(newValue);
        initView();
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            newValue);
        }
    }

    @Override
    public void setComboBoxValues(final ListBoxValues valueListBoxValues) {
        this.listBoxValues = valueListBoxValues;
        valueComboBox.setCurrentTextValue("");
        valueComboBox.setListBoxValues(valueListBoxValues);
        valueComboBox.setShowCustomValues(true);
        if (dataModel.customValue != null && !dataModel.customValue.isEmpty()) {
            valueComboBox.addCustomValueToListBoxValues(currentValue,
                                                        "");
        }
    }

    protected void initView() {
        dataModel.setModelValue(listBoxValues.getDisplayNameForValue(currentValue));
        if (dataModel.customValue != null) {
            customValueField.setValue(dataModel.customValue);
            valueField.setValue(dataModel.customValue);
        } else {
            valueField.setValue(dataModel.nonCustomValue);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        valueField.setEnabled(!readOnly);
        customValueField.setEnabled(!readOnly);
    }

    /*
        Implementation of ComboBoxView.ModelPresenter
     */
    @Override
    public void setTextBoxModelValue(final TextBox textBox,
                                     final String value) {
        dataModel.customValue = value;
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox,
                                     final String value) {
        dataModel.nonCustomValue = value;
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        return currentValue;
    }

    @Override
    public void notifyModelChanged() {
        setValue(getCurrentValue(),
                 true);
    }

    public String getCurrentValue() {
        if (dataModel.customValue != null && !dataModel.customValue.isEmpty()) {
            return dataModel.customValue;
        } else {
            return dataModel.nonCustomValue;
        }
    }
}
