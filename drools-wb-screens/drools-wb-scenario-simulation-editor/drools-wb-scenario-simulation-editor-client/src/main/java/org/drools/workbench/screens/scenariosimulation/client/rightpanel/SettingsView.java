/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.uberfire.mvp.Command;

public interface SettingsView
        extends SubDockView<SettingsView.Presenter> {

    interface Presenter extends SubDockView.Presenter {

        void setScenarioType(ScenarioSimulationModel.Type scenarioType, SimulationDescriptor simulationDescriptor, String fileName);

        void onSaveButton(String type);

        void setSaveCommand(Command saveCommand);
    }

    void setupDropdown(Element dropdownElement);

    LabelElement getNameLabel();

    InputElement getFileName();

    LabelElement getTypeLabel();

    SpanElement getScenarioType();

    DivElement getRuleSettings();

    InputElement getDmoSession();

    InputElement getRuleFlowGroup();

    DivElement getDmnSettings();

    LabelElement getDmnFileLabel();

    DivElement getDmnFilePathPlaceholder();

    SpanElement getDmnFilePathErrorLabel();

    LabelElement getDmnNamespaceLabel();

    InputElement getDmnNamespace();

    LabelElement getDmnNameLabel();

    InputElement getDmnName();

    InputElement getSkipFromBuild();

    SpanElement getSkipFromBuildLabel();

    InputElement getStateless();

    ButtonElement getSaveButton();
}
