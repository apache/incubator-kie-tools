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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter.DEFAULT_PREFERRED_WIDHT;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter.IDENTIFIER;

@ApplicationScoped
@WorkbenchScreen(identifier = IDENTIFIER, preferredWidth = DEFAULT_PREFERRED_WIDHT)
public class SettingsPresenter extends AbstractSubDockPresenter<SettingsView> implements SettingsView.Presenter {

    public static final int DEFAULT_PREFERRED_WIDHT = 300;

    public static final String IDENTIFIER = "org.drools.scenariosimulation.Settings";

    protected SimulationDescriptor simulationDescriptor;

    protected Command saveCommand;


    public SettingsPresenter() {
        //Zero argument constructor for CDI
        title = ScenarioSimulationEditorConstants.INSTANCE.settings();
    }

    @Inject
    public SettingsPresenter(SettingsView view) {
        super(view);
        title = ScenarioSimulationEditorConstants.INSTANCE.settings();
    }

    @Override
    public void setScenarioType(ScenarioSimulationModel.Type scenarioType, SimulationDescriptor simulationDescriptor, String fileName) {
        this.simulationDescriptor = simulationDescriptor;
        view.getScenarioType().setInnerText(scenarioType.name());
        view.getFileName().setInnerText(fileName);
        view.getSkipFromBuild().setChecked(simulationDescriptor.isSkipFromBuild());
        switch (scenarioType) {
            case RULE:
                setRuleSettings(simulationDescriptor);
                break;
            case DMN:
                setDMNSettings(simulationDescriptor);
                break;
        }
    }

    @Override
    public void setSaveCommand(Command saveCommand) {
        this.saveCommand = saveCommand;
    }

    @Override
    public void onSaveButton(String scenarioType) {
        simulationDescriptor.setSkipFromBuild(view.getSkipFromBuild().isChecked());
        simulationDescriptor.setFileName(view.getFileName().getInnerText());
        switch (ScenarioSimulationModel.Type.valueOf(scenarioType)) {
            case RULE:
                saveRuleSettings();
                break;
            case DMN:
                saveDMNSettings();
                break;
        }
        saveCommand.execute();
    }

    @Override
    public void reset() {
        view.reset();
    }

    protected void setRuleSettings(SimulationDescriptor simulationDescriptor) {
        view.getDmnSettings().getStyle().setDisplay(Style.Display.NONE);
        view.getRuleSettings().getStyle().setDisplay(Style.Display.INLINE);
        view.getDmoSession().setValue(simulationDescriptor.getDmoSession());
        view.getKieBase().setValue(simulationDescriptor.getKieBase());
        view.getKieSession().setValue(simulationDescriptor.getKieSession());
        view.getRuleFlowGroup().setValue(simulationDescriptor.getRuleFlowGroup());
    }

    protected void setDMNSettings(SimulationDescriptor simulationDescriptor) {
        view.getRuleSettings().getStyle().setDisplay(Style.Display.NONE);
        view.getDmnSettings().getStyle().setDisplay(Style.Display.INLINE);
        view.getDmnFilePath().setInnerText(simulationDescriptor.getDmnFilePath());
        view.getDmnName().setInnerText(simulationDescriptor.getDmnName());
        view.getDmnNamespace().setInnerText(simulationDescriptor.getDmnNamespace());
    }

    protected void saveRuleSettings() {
        simulationDescriptor.setDmoSession(view.getDmoSession().getValue());
        simulationDescriptor.setKieBase(view.getKieBase().getValue());
        simulationDescriptor.setKieSession(view.getKieSession().getValue());
        simulationDescriptor.setRuleFlowGroup(view.getRuleFlowGroup().getValue());
    }

    protected void saveDMNSettings() {
        simulationDescriptor.setDmnFilePath(view.getDmnFilePath().getInnerText());
        simulationDescriptor.setDmnName(view.getDmnName().getInnerText());
        simulationDescriptor.setDmnNamespace(view.getDmnNamespace().getInnerText());
    }
}
