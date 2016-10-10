/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.forms.client.fields.widgets;

import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.kie.workbench.common.stunner.forms.client.fields.util.ListBoxValues;

import java.util.List;

public interface ComboBoxView {

    /**
     * Interface implemented by the owner of the model being
     * manipulated by the ComboBox
     */
    interface ModelPresenter {

        void setTextBoxModelValue( final TextBox textBox, String value );

        void setListBoxModelValue( final ValueListBox<String> listBox, String value );

        String getModelValue( final ValueListBox<String> listBox );

        void notifyModelChanged();
    }

    /**
     * Interface for Presenter class of ComboBox
     */
    interface ComboBoxPresenter {

        void init( final ComboBoxView.ModelPresenter modelPresenter,
                   final boolean notifyModelChanges,
                   final ValueListBox<String> listBox, final TextBox textBox,
                   final boolean quoteStringValues,
                   final boolean addCustomValues,
                   final String customPrompt, final String placeholder );

        void setListBoxValues( final ListBoxValues listBoxValues );

        void setShowCustomValues( final boolean showCustomValues );

        void setAddCustomValues( final boolean addCustomValues );

        void setCurrentTextValue( String currentTextValue );

        ListBoxValues getListBoxValues();

        void updateListBoxValues( String listBoxValue );

        String addCustomValueToListBoxValues( String newValue, String oldValue );

        String getValue();

        void listBoxValueChanged( String newValue );

        void textBoxValueChanged( String newValue );

    }

    void init( final ComboBoxView.ComboBoxPresenter presenter, final ComboBoxView.ModelPresenter modelPresenter,
               final ValueListBox<String> listBox, final TextBox textBox,
               final String placeholder );

    String getModelValue();

    void setTextBoxModelValue( String value );

    void setListBoxModelValue( String value );

    void setAcceptableValues( List<String> acceptableValues );

    void setTextBoxVisible( boolean visible );

    void setListBoxVisible( boolean visible );

    void setTextBoxFocus( boolean focus );

    String getListBoxValue();

    void setListBoxValue( String value );

    void setTextBoxValue( String value );

    String getValue();

    void textBoxGotFocus();

    void textBoxLostFocus();

    void listBoxGotFocus();
}
