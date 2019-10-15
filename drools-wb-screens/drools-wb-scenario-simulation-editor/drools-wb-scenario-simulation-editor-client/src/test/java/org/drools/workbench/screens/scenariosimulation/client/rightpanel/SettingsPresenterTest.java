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

import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.dropdown.SettingsScenarioSimulationDropdown;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_FILE_PATH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAMESPACE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMO_SESSION;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FILE_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.RULE_FLOW_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SettingsPresenterTest extends AbstractSettingsTest {

    private SettingsPresenter settingsPresenter;

////    @Mock
//    private SettingsView settingsViewMock;
//
    @Mock
    private SimulationDescriptor simulationDescriptorMock;

    @Mock
    private Command saveCommandMock;

    @Mock
    private SettingsScenarioSimulationDropdown settingsScenarioSimulationDropdownMock;

    @Before
    public void setup() {
        super.setup();
//        when(settingsViewMock.getNameLabel()).thenReturn(nameLabelMock);
//        when(settingsViewMock.getFileName()).thenReturn(fileNameMock);
//        when(settingsViewMock.getTypeLabel()).thenReturn(typeLabelMock);
//        when(settingsViewMock.getScenarioType()).thenReturn(scenarioTypeMock);
//        when(settingsViewMock.getRuleSettings()).thenReturn(ruleSettingsMock);
//        when(settingsViewMock.getDmoSession()).thenReturn(dmoSessionMock);
//        when(settingsViewMock.getRuleFlowGroup()).thenReturn(ruleFlowGroupMock);
//        when(settingsViewMock.getDmnSettings()).thenReturn(dmnSettingsMock);
//        when(settingsViewMock.getDmnFileLabel()).thenReturn(dmnModelLabelMock);
//        when(settingsViewMock.getDmnFilePathPlaceholder()).thenReturn(dmnFilePathPlaceholderMock);
//        when(settingsViewMock.getDmnFilePathErrorLabel()).thenReturn(dmnFilePathErrorLabelMock);
//        when(settingsViewMock.getDmnNamespaceLabel()).thenReturn(dmnNamespaceLabelMock);
//        when(settingsViewMock.getDmnNamespace()).thenReturn(dmnNamespaceMock);
//        when(settingsViewMock.getDmnNameLabel()).thenReturn(dmnNameLabelMock);
//        when(settingsViewMock.getDmnName()).thenReturn(dmnNameMock);
//        when(settingsViewMock.getSkipFromBuild()).thenReturn(skipFromBuildMock);
//        when(settingsViewMock.getSaveButton()).thenReturn(saveButtonMock);
//        when(settingsViewMock.getStateless()).thenReturn(statelessMock);

        when(simulationDescriptorMock.getRuleFlowGroup()).thenReturn(RULE_FLOW_GROUP);
        when(simulationDescriptorMock.getDmoSession()).thenReturn(DMO_SESSION);
        when(simulationDescriptorMock.getDmnFilePath()).thenReturn(DMN_FILE_PATH);
        when(simulationDescriptorMock.getDmnNamespace()).thenReturn(DMN_NAMESPACE);
        when(simulationDescriptorMock.getDmnName()).thenReturn(DMN_NAME);
        when(simulationDescriptorMock.isStateless()).thenReturn(true);

        KieAssetsDropdownItem item = new KieAssetsDropdownItem("DMNFile", "", DMN_FILE_PATH, null);
        when(settingsScenarioSimulationDropdownMock.getValue()).thenReturn(Optional.of(item));

        this.settingsPresenter = spy(new SettingsPresenter(settingsScenarioSimulationDropdownMock, settingsViewMock) {
            {
                this.simulationDescriptor = simulationDescriptorMock;
                this.settingsScenarioSimulationDropdown = settingsScenarioSimulationDropdownMock;
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
    public void setSaveEnabledTrue() {
        settingsPresenter.setSaveEnabled(true);
        assertTrue(settingsPresenter.saveEnabled);
        verify(settingsViewMock, times(1)).restoreSaveButton();
    }

    @Test
    public void setSaveEnabledFalse() {
        settingsPresenter.setSaveEnabled(false);
        assertFalse(settingsPresenter.saveEnabled);
        verify(settingsViewMock, times(1)).removeSaveButton();
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
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setRuleSettings(simulationDescriptorMock);
    }

    @Test
    public void setScenarioTypeRULESkipFalse() {
        when(simulationDescriptorMock.isSkipFromBuild()).thenReturn(false);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.RULE, simulationDescriptorMock, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setRuleSettings(simulationDescriptorMock);
    }

    @Test
    public void setScenarioTypeDMNSkipTrue() {
        when(simulationDescriptorMock.isSkipFromBuild()).thenReturn(true);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.DMN, simulationDescriptorMock, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setDMNSettings(simulationDescriptorMock);
    }

    @Test
    public void setScenarioTypeDMNSkipFalse() {
        when(simulationDescriptorMock.isSkipFromBuild()).thenReturn(false);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.DMN, simulationDescriptorMock, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setDMNSettings(simulationDescriptorMock);
    }

    @Test
    public void setRuleSettings() {
        settingsPresenter.setRuleSettings(simulationDescriptorMock);
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmoSessionMock, times(1)).setValue(eq(DMO_SESSION));
        verify(ruleFlowGroupMock, times(1)).setValue(eq(RULE_FLOW_GROUP));
        verify(statelessMock, times(1)).setChecked(eq(simulationDescriptorMock.isStateless()));
    }

    @Test
    public void setDMNSettings() {
        settingsPresenter.setDMNSettings(simulationDescriptorMock);
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmnNameMock, times(1)).setValue(eq(DMN_NAME));
        verify(dmnNamespaceMock, times(1)).setValue(eq(DMN_NAMESPACE));
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(eq(""));
        verify(settingsScenarioSimulationDropdownMock, times(1)).registerOnMissingValueHandler(isA(Command.class));
        verify(settingsScenarioSimulationDropdownMock, times(1)).registerOnChangeHandler(isA(Command.class));
        verify(settingsScenarioSimulationDropdownMock, times(1)).loadAssets(eq(DMN_FILE_PATH));
    }

    @Test
    public void saveRuleSettings() {
        when(skipFromBuildMock.isChecked()).thenReturn(true);
        settingsPresenter.saveRuleSettings();
        verify(simulationDescriptorMock, times(1)).setDmoSession(eq(DMO_SESSION));
        verify(simulationDescriptorMock, times(1)).setRuleFlowGroup(eq(RULE_FLOW_GROUP));
        verify(simulationDescriptorMock, times(1)).setStateless(eq(false));
    }

    @Test
    public void saveDMNSettings() {
        settingsPresenter.saveDMNSettings();
        verify(settingsScenarioSimulationDropdownMock, times(1)).getValue();
        verify(simulationDescriptorMock, times(1)).setDmnFilePath(eq(DMN_FILE_PATH));
    }

    @Test
    public void resetTest() {
        settingsPresenter.reset();
        verify(settingsViewMock, times(1)).reset();
        verify(settingsScenarioSimulationDropdownMock, times(1)).clear();
    }

    @Test
    public void setDmnErrorPath() {
        settingsPresenter.setDmnErrorPath();
        verify(simulationDescriptorMock, times(1)).getDmnFilePath();
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(
                eq(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorLabel(simulationDescriptorMock.getDmnFilePath())));
        verify(saveButtonMock, times(1)).setDisabled(eq(true));
    }

    @Test
    public void validateDmnPath_Valid() {
        settingsPresenter.validateDmnPath();
        verify(settingsScenarioSimulationDropdownMock, times(1)).getValue();
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(eq(""));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
    }

    @Test
    public void validateDmnPath_Invalid() {
        KieAssetsDropdownItem item = new KieAssetsDropdownItem("DMNFile", "", "", null);
        when(settingsScenarioSimulationDropdownMock.getValue()).thenReturn(Optional.of(item));
        settingsPresenter.validateDmnPath();
        verify(settingsScenarioSimulationDropdownMock, times(1)).getValue();
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.chooseValidDMNAsset()));
        verify(saveButtonMock, times(1)).setDisabled(eq(true));
    }

}