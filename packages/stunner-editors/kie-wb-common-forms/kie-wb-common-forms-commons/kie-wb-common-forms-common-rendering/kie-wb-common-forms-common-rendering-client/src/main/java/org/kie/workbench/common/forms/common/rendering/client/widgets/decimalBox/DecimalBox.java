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


package org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class DecimalBox implements IsWidget,
                                   HasValue<Double> {

    private DecimalBoxView view;

    @Inject
    public DecimalBox(DecimalBoxView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public Double getValue() {
        return this.toDouble(view.getTextValue());
    }

    @Override
    public void setValue(Double value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(Double value,
                         boolean fireEvents) {
        if (value == null && this.getValue() == null) {
            return;
        }

        if (this.getValue() == null || !this.getValue().equals(value)) {
            view.setValue(Objects.toString(value,
                                           null));
            if (fireEvents) {
                notifyValueChange(Objects.toString(value,
                                                   null));
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> valueChangeHandler) {
        return view.asWidget().addHandler(valueChangeHandler,
                                          ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.asWidget().fireEvent(event);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void notifyValueChange(String value) {
        ValueChangeEvent.fire(this,
                              this.toDouble(value));
    }

    public void setEnabled(boolean enabled) {
        view.setEnabled(enabled);
    }

    public boolean isInvalidKeyCode(int key,
                                    boolean isShiftPressed) {

        boolean isNumPadDigit = (key >= KeyCodes.KEY_NUM_ZERO && key <= KeyCodes.KEY_NUM_NINE);
        boolean isKeyboardDigit = (key >= KeyCodes.KEY_ZERO && key <= KeyCodes.KEY_NINE);
        boolean isBackspace = (key == KeyCodes.KEY_BACKSPACE);
        boolean isDecimalSeparator = (key == KeyCodes.KEY_NUM_PERIOD || key == 190);
        boolean isArrowKey = (key == 37 || key == 39);
        boolean isTabKey = (key == KeyCodes.KEY_TAB);

        if (isTabKey) {
            return false;
        }

        if (!isShiftPressed) {
            if (isNumPadDigit || isKeyboardDigit || isBackspace || isArrowKey) {
                return false;
            }
            if (isDecimalSeparator) {
                if (!view.getTextValue().contains(".")) {
                    return false;
                }
            }
        }
        return true;
    }

    private Double toDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Double.valueOf(value);
    }

    public void setId(String id) {
        view.setId(id);
    }

    public void setPlaceholder(String placeHolder) {
        view.setPlaceholder(placeHolder);
    }

    public void setMaxLength(Integer maxLength) {
        view.setMaxLength(maxLength);
    }
}
