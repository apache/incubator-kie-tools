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

package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Radio;

public class SourceTypeSelector extends VerticalPanel implements ValueChangeHandler<Boolean> {

    protected static final String SOURCE_TYPE = "SOURCE_TYPE";
    protected static final String[] SOURCE_TYPES = {"DRL", "DMN"};
    protected final TitledAttachmentFileWidget uploadWidget;
    protected final List<Radio> radioButtonList = new ArrayList<Radio>();

    public SourceTypeSelector(TitledAttachmentFileWidget uploadWidget) {
        this.uploadWidget = uploadWidget;
        addRadioButtons();
    }


    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        final boolean dmnSelected = isDMNSelected();
        uploadWidget.setVisible(dmnSelected);
        if (dmnSelected) {
            uploadWidget.updateAssetList();
        }
    }

    public boolean isDMNSelected() {
        return radioButtonList.stream()
                .filter(CheckBox::getValue)
                .anyMatch(radioButton -> radioButton.getText().equalsIgnoreCase("DMN"));
    }

    protected void addRadioButtons() {
        boolean first = true;
        radioButtonList.clear();
        for (String sourceType : SOURCE_TYPES) {
            Radio radioButton = new Radio(SOURCE_TYPE);
            radioButton.setText(sourceType);
            radioButton.setValue(first);
            radioButton.addValueChangeHandler(this);
            first = false;
            radioButtonList.add(radioButton);
            add(radioButton);
        }
        uploadWidget.setVisible(isDMNSelected());
    }
}
