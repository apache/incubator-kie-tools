/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.forms.client.fields.colorpicker;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.HasValue;
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
