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

import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

public interface ComboBoxView {

    /**
     * Interface implemented by the owner of the model being
     * manipulated by the ComboBox
     */
    interface ModelPresenter {

        void setTextBoxModelValue(final TextBox textBox,
                                  final String value);

        void setListBoxModelValue(final ValueListBox<String> listBox,
                                  final String value);

        String getModelValue(final ValueListBox<String> listBox);

        void notifyModelChanged();
    }

    /**
     * Interface for Presenter class of ComboBox
     */
    interface ComboBoxPresenter {

        void init(final ComboBoxView.ModelPresenter modelPresenter,
                  final boolean notifyModelChanges,
                  final ValueListBox<String> listBox,
                  final TextBox textBox,
                  final boolean quoteStringValues,
                  final boolean addCustomValues,
                  final String customPrompt,
                  final String placeholder);

        void setListBoxValues(final ListBoxValues listBoxValues);

        void setShowCustomValues(final boolean showCustomValues);

        void setAddCustomValues(final boolean addCustomValues);

        void setCurrentTextValue(final String currentTextValue);

        ListBoxValues getListBoxValues();

        void updateListBoxValues(final String listBoxValue);

        String addCustomValueToListBoxValues(final String newValue,
                                             final String oldValue);

        String getValue();

        void listBoxValueChanged(final String newValue);

        void textBoxValueChanged(final String newValue);

        void setReadOnly(final boolean readOnly);
    }

    void init(final ComboBoxView.ComboBoxPresenter presenter,
              final ComboBoxView.ModelPresenter modelPresenter,
              final ValueListBox<String> listBox,
              final TextBox textBox,
              final String placeholder);

    String getModelValue();

    void setTextBoxModelValue(final String value);

    void setListBoxModelValue(final String value);

    void setAcceptableValues(final List<String> acceptableValues);

    void setTextBoxVisible(final boolean visible);

    void setListBoxVisible(final boolean visible);

    void setTextBoxFocus(final boolean focus);

    String getListBoxValue();

    void setListBoxValue(final String value);

    void setTextBoxValue(final String value);

    String getValue();

    void textBoxGotFocus();

    void textBoxLostFocus();

    void listBoxGotFocus();

    void setReadOnly(final boolean readOnly);
}
