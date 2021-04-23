/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Radio;

public class SourceTypeSelector extends VerticalPanel implements ValueChangeHandler<Boolean> {

    protected static final String SOURCE_TYPE = "SOURCE_TYPE";
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

    public boolean validate() {
        if (isDMNSelected()) {
            return uploadWidget.validate();
        } else {
            return true;
        }
    }

    public boolean isDMNSelected() {
        return radioButtonList.stream()
                .filter(CheckBox::getValue)
                .anyMatch(radioButton -> radioButton.getText().equalsIgnoreCase(ScenarioSimulationModel.Type.DMN.name()));
    }

    /**
     * Returns the selected Type. <b>By default, it returns <code>ScenarioSimulationModel.Type.RULE</code></b>
     * @return
     */
    public ScenarioSimulationModel.Type getSelectedType() {
        return radioButtonList.stream()
                .filter(CheckBox::getValue)
                .findFirst()
                .map(selectedText -> ScenarioSimulationModel.Type.valueOf(selectedText.getText()))
                .orElse(ScenarioSimulationModel.Type.RULE);
    }

    protected void addRadioButtons() {
        boolean first = true;
        radioButtonList.clear();
        for (ScenarioSimulationModel.Type sourceType : ScenarioSimulationModel.Type.values()) {
            Radio radioButton = new Radio(SOURCE_TYPE);
            radioButton.setText(sourceType.name());
            radioButton.setValue(first);
            radioButton.addValueChangeHandler(this);
            first = false;
            radioButtonList.add(radioButton);
            add(radioButton);
        }
        uploadWidget.setVisible(isDMNSelected());
    }
}
