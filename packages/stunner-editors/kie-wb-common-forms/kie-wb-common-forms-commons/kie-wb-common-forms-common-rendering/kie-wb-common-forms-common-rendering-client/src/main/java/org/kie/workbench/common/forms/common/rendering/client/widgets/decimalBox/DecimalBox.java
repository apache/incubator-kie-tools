/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.legacy.shared.GwtEvent;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.Event;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.HasValue;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

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

    @Override
    public void fireEvent(Event<?> event) {
        view.asWidget().fireEvent(event);
    }
}
