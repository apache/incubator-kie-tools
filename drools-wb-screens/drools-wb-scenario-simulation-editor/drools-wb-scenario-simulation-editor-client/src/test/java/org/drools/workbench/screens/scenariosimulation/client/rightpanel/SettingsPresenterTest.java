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

import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_FILE_PATH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAMESPACE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMO_SESSION;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FILE_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.RULE_FLOW_GROUP;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SettingsPresenterTest extends AbstractSettingsTest {

    private SettingsPresenter settingsPresenter;

    @Mock
    private SettingsView settingsViewMock;

    @Mock
    private SimulationDescriptor simulationDescriptorMock;

    @Mock
    private Command saveCommandMock;

    @Before
    public void setup() {
        super.setup();
        when(settingsViewMock.getNameLabel()).thenReturn(nameLabelMock);
        when(settingsViewMock.getFileName()).thenReturn(fileNameMock);
        when(settingsViewMock.getTypeLabel()).thenReturn(typeLabelMock);
        when(settingsViewMock.getScenarioType()).thenReturn(scenarioTypeMock);
        when(settingsViewMock.getRuleSettings()).thenReturn(ruleSettingsMock);
        when(settingsViewMock.getDmoSession()).thenReturn(dmoSessionMock);
        when(settingsViewMock.getRuleFlowGroup()).thenReturn(ruleFlowGroupMock);
        when(settingsViewMock.getDmnSettings()).thenReturn(dmnSettingsMock);
        when(settingsViewMock.getDmnFileLabel()).thenReturn(dmnModelLabelMock);
        when(settingsViewMock.getDmnFilePath()).thenReturn(dmnFilePathMock);
        when(settingsViewMock.getDmnNamespaceLabel()).thenReturn(dmnNamespaceLabelMock);
        when(settingsViewMock.getDmnNamespace()).thenReturn(dmnNamespaceMock);
        when(settingsViewMock.getDmnNameLabel()).thenReturn(dmnNameLabelMock);
        when(settingsViewMock.getDmnName()).thenReturn(dmnNameMock);
        when(settingsViewMock.getSkipFromBuild()).thenReturn(skipFromBuildMock);
        when(settingsViewMock.getSaveButton()).thenReturn(saveButtonMock);

        when(simulationDescriptorMock.getRuleFlowGroup()).thenReturn(RULE_FLOW_GROUP);
        when(simulationDescriptorMock.getDmoSession()).thenReturn(DMO_SESSION);
        when(simulationDescriptorMock.getDmnFilePath()).thenReturn(DMN_FILE_PATH);
        when(simulationDescriptorMock.getDmnNamespace()).thenReturn(DMN_NAMESPACE);
        when(simulationDescriptorMock.getDmnName()).thenReturn(DMN_NAME);

        this.settingsPresenter = spy(new SettingsPresenter(settingsViewMock) {
            {
                this.simulationDescriptor = simulationDescriptorMock;
                this.saveCommand = saveCommandMock;
            }
        });
    }

    @Test
    public void onSetup() {
        settingsPresenter.setup();
        verify(settingsViewMock, times(1)).init(settingsPresenter);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.settings(), settingsPresenter.getTitle());
    }

    @Test
    public void onSaveButtonSkipTrue() {
        when(skipFromBuildMock.isChecked()).thenReturn(true);
        settingsPresenter.onSaveButton(ScenarioSimulationModel.Type.RULE.name());
        verify(simulationDescriptorMock, times(1)).setSkipFromBuild(eq(true));
        verify(settingsPresenter, times(1)).saveRuleSettings();
        verify(saveCommandMock, times(1)).execute();
        reset(saveCommandMock);
        reset(simulationDescriptorMock);
        settingsPresenter.onSaveButton(ScenarioSimulationModel.Type.DMN.name());
        verify(simulationDescriptorMock, times(1)).setSkipFromBuild(eq(true));
        verify(settingsPresenter, times(1)).saveDMNSettings();
        verify(saveCommandMock, times(1)).execute();
    }

    @Test
    public void onSaveButtonSkipFalse() {
        when(skipFromBuildMock.isChecked()).thenReturn(false);
        settingsPresenter.onSaveButton(ScenarioSimulationModel.Type.RULE.name());
        verify(settingsPresenter, times(1)).saveRuleSettings();
        verify(simulationDescriptorMock, times(1)).setSkipFromBuild(eq(false));
        verify(saveCommandMock, times(1)).execute();
        reset(saveCommandMock);
        reset(simulationDescriptorMock);
        settingsPresenter.onSaveButton(ScenarioSimulationModel.Type.DMN.name());
        verify(simulationDescriptorMock, times(1)).setSkipFromBuild(eq(false));
        verify(settingsPresenter, times(1)).saveDMNSettings();
        verify(saveCommandMock, times(1)).execute();
    }

    @Test
    public void setScenarioTypeRULESkipTrue() {
        when(simulationDescriptorMock.isSkipFromBuild()).thenReturn(true);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.RULE, simulationDescriptorMock, FILE_NAME);
        verify(settingsViewMock, times(1)).getScenarioType();
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(settingsViewMock, times(1)).getFileName();
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(settingsPresenter, times(1)).setRuleSettings(simulationDescriptorMock);
    }

    @Test
    public void setScenarioTypeRULESkipFalse() {
        when(simulationDescriptorMock.isSkipFromBuild()).thenReturn(false);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.RULE, simulationDescriptorMock, FILE_NAME);
        verify(settingsViewMock, times(1)).getScenarioType();
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(settingsViewMock, times(1)).getFileName();
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(settingsPresenter, times(1)).setRuleSettings(simulationDescriptorMock);
    }

    @Test
    public void setScenarioTypeDMNSkipTrue() {
        when(simulationDescriptorMock.isSkipFromBuild()).thenReturn(true);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.DMN, simulationDescriptorMock, FILE_NAME);
        verify(settingsViewMock, times(1)).getScenarioType();
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(settingsViewMock, times(1)).getFileName();
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(settingsPresenter, times(1)).setDMNSettings(simulationDescriptorMock);
    }

    @Test
    public void setScenarioTypeDMNSkipFalse() {
        when(simulationDescriptorMock.isSkipFromBuild()).thenReturn(false);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.DMN, simulationDescriptorMock, FILE_NAME);
        verify(settingsViewMock, times(1)).getScenarioType();
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(settingsViewMock, times(1)).getFileName();
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(settingsPresenter, times(1)).setDMNSettings(simulationDescriptorMock);
    }

    @Test
    public void setRuleSettings() {
        settingsPresenter.setRuleSettings(simulationDescriptorMock);
        verify(settingsViewMock, times(1)).getDmnSettings();
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(settingsViewMock, times(1)).getRuleSettings();
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(settingsViewMock, times(1)).getDmoSession();
        verify(dmoSessionMock, times(1)).setValue(eq(DMO_SESSION));
        verify(settingsViewMock, times(1)).getRuleFlowGroup();
        verify(ruleFlowGroupMock, times(1)).setValue(eq(RULE_FLOW_GROUP));
    }

    @Test
    public void setDMNSettings() {
        settingsPresenter.setDMNSettings(simulationDescriptorMock);
        verify(settingsViewMock, times(1)).getRuleSettings();
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(settingsViewMock, times(1)).getDmnSettings();
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(settingsViewMock, times(1)).getDmnFilePath();
        verify(dmnFilePathMock, times(1)).setValue(eq(DMN_FILE_PATH));
        verify(settingsViewMock, times(1)).getDmnName();
        verify(dmnNameMock, times(1)).setValue(eq(DMN_NAME));
        verify(settingsViewMock, times(1)).getDmnNamespace();
        verify(dmnNamespaceMock, times(1)).setValue(eq(DMN_NAMESPACE));
    }

    @Test
    public void saveRuleSettings() {
        when(skipFromBuildMock.isChecked()).thenReturn(true);
        settingsPresenter.saveRuleSettings();
        verify(simulationDescriptorMock, times(1)).setDmoSession(eq(DMO_SESSION));
        verify(settingsViewMock, times(1)).getDmoSession();
        verify(settingsViewMock, times(1)).getRuleFlowGroup();
        verify(simulationDescriptorMock, times(1)).setRuleFlowGroup(eq(RULE_FLOW_GROUP));
    }

    @Test
    public void saveDMNSettings() {
        settingsPresenter.saveDMNSettings();
        verify(settingsViewMock, times(1)).getDmnFilePath();
        verify(simulationDescriptorMock, times(1)).setDmnFilePath(eq(DMN_FILE_PATH));
    }

    @Test
    public void resetTest() {
        settingsPresenter.reset();
        verify(settingsViewMock, times(1)).reset();
    }
}