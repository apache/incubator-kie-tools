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

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
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

    @PostConstruct
    public void init() {
        view.getSkipFromBuildLabel().setInnerText(ScenarioSimulationEditorConstants.INSTANCE.skipSimulation());
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
        view.getDmoSession().setValue(Optional.ofNullable(simulationDescriptor.getDmoSession()).orElse(""));
        view.getRuleFlowGroup().setValue(Optional.ofNullable(simulationDescriptor.getRuleFlowGroup()).orElse(""));
    }

    protected void setDMNSettings(SimulationDescriptor simulationDescriptor) {
        view.getRuleSettings().getStyle().setDisplay(Style.Display.NONE);
        view.getDmnSettings().getStyle().setDisplay(Style.Display.INLINE);
        view.getDmnFilePath().setValue(Optional.ofNullable(simulationDescriptor.getDmnFilePath()).orElse(""));
        view.getDmnName().setInnerText(Optional.ofNullable(simulationDescriptor.getDmnName()).orElse(""));
        view.getDmnNamespace().setInnerText(Optional.ofNullable(simulationDescriptor.getDmnNamespace()).orElse(""));
    }

    protected void saveRuleSettings() {
        simulationDescriptor.setDmoSession(getCleanValue(() -> view.getDmoSession().getValue()));
        simulationDescriptor.setRuleFlowGroup(getCleanValue(() -> view.getRuleFlowGroup().getValue()));
    }

    protected void saveDMNSettings() {
        simulationDescriptor.setDmnFilePath(getCleanValue(() -> view.getDmnFilePath().getValue()));
    }

    private String getCleanValue(Supplier<String> supplier) {
        String rawValue = supplier.get();
        return "".equals(rawValue) ? null : rawValue;
    }
}
