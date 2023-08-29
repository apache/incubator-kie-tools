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


package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import java.util.List;

import com.google.gwt.event.dom.client.FocusEvent;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;

/**
 * ComboBox based on a ValueListBox<String> and a TextBox
 */
public class ComboBoxViewImpl implements ComboBoxView {

    protected ComboBoxView.ComboBoxPresenter presenter;
    protected ComboBoxView.ModelPresenter modelPresenter;
    protected ValueListBox<String> listBox;
    protected TextBox textBox;

    @Override
    public void init(final ComboBoxView.ComboBoxPresenter presenter,
                     final ComboBoxView.ModelPresenter modelPresenter,
                     final ValueListBox<String> listBox,
                     final TextBox textBox,
                     final String placeholder) {
        this.presenter = presenter;
        this.modelPresenter = modelPresenter;
        this.listBox = listBox;
        this.textBox = textBox;
        this.textBox.setPlaceholder(placeholder);
        textBox.setVisible(false);
        listBox.addValueChangeHandler(valueChangeEvent -> presenter.listBoxValueChanged(valueChangeEvent.getValue()));
        listBox.addDomHandler(focusEvent -> listBoxGotFocus(),
                              FocusEvent.getType());
        textBox.addFocusHandler(focusEvent -> textBoxGotFocus());
        textBox.addBlurHandler(blurEvent -> {
            // Update ListBoxValues and set model values when textBox loses focus
            textBoxLostFocus();
        });
    }

    @Override
    public String getModelValue() {
        return modelPresenter.getModelValue(listBox);
    }

    @Override
    public void setTextBoxModelValue(final String value) {
        modelPresenter.setTextBoxModelValue(textBox,
                                            value);
    }

    @Override
    public void setListBoxModelValue(final String value) {
        modelPresenter.setListBoxModelValue(listBox,
                                            value);
    }

    @Override
    public String getListBoxValue() {
        return listBox.getValue();
    }

    @Override
    public void setListBoxValue(final String value) {
        listBox.setValue(value);
    }

    @Override
    public void setTextBoxValue(final String value) {
        textBox.setValue(value);
    }

    @Override
    public void setTextBoxVisible(final boolean visible) {
        textBox.setVisible(visible);
    }

    @Override
    public void setListBoxVisible(final boolean visible) {
        listBox.setVisible(visible);
    }

    @Override
    public void setTextBoxFocus(final boolean focus) {
        textBox.setFocus(focus);
    }

    @Override
    public void textBoxGotFocus() {
        presenter.setCurrentTextValue(textBox.getValue());
    }

    @Override
    public void textBoxLostFocus() {
        presenter.textBoxValueChanged(textBox.getValue());
    }

    @Override
    public void listBoxGotFocus() {
        presenter.updateListBoxValues(listBox.getValue());
    }

    @Override
    public void setAcceptableValues(final List<String> acceptableValues) {
        listBox.setAcceptableValues(acceptableValues);
    }

    @Override
    public String getValue() {
        if (textBox.isVisible()) {
            return textBox.getValue();
        } else {
            return listBox.getValue();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        listBox.setEnabled(!readOnly);
        textBox.setEnabled(!readOnly);
    }
}
