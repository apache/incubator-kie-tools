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


package org.kie.workbench.common.stunner.forms.client.fields.colorpicker;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldDefinition;

@Dependent
@Templated
public class ColorPickerWidget extends Composite implements HasValue<String> {

    @Inject
    @DataField
    private TextBox colorTextBox;

    private String color;

    @EventHandler("colorTextBox")
    public void onColorTextBoxChange(final ChangeEvent changeEvent) {
        final String newColorValue = getColorTextBox().getValue();
        if (newColorValue != null && newColorValue.matches(ColorPickerFieldDefinition.COLOR_REGEXP)) {
            setValue(newColorValue, true);
        } else {
            setValue(color, false);
        }
    }

    @Override
    public String getValue() {
        return color;
    }

    @Override
    public void setValue(final String value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        String oldValue = color;
        color = value;
        initTextBox();
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            color);
        }
    }

    protected void initTextBox() {
        getColorTextBox().setText(color);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void setReadOnly(boolean readOnly) {
        getColorTextBox().setReadOnly(readOnly);
    }

    /**
     * For testing purposes
     */
    TextBox getColorTextBox() {
        return colorTextBox;
    }
}
