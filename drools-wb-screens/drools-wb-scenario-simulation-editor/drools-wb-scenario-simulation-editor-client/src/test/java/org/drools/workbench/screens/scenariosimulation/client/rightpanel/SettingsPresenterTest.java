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
import org.drools.scenariosimulation.api.model.Settings;
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

    @Mock
    private Command saveCommandMock;

    @Mock
    private SettingsScenarioSimulationDropdown settingsScenarioSimulationDropdownMock;
    
    protected Settings settingsLocal;

    @Before
    public void setup() {
        super.setup();
        settingsLocal = new Settings();
        settingsLocal.setRuleFlowGroup(RULE_FLOW_GROUP);
        settingsLocal.setDmoSession(DMO_SESSION);
        settingsLocal.setDmnFilePath(DMN_FILE_PATH);
        settingsLocal.setDmnNamespace(DMN_NAMESPACE);
        settingsLocal.setDmnName(DMN_NAME);
        settingsLocal.setStateless(true);

        KieAssetsDropdownItem item = new KieAssetsDropdownItem("DMNFile", "", DMN_FILE_PATH, null);
        when(settingsScenarioSimulationDropdownMock.getValue()).thenReturn(Optional.of(item));

        this.settingsPresenter = spy(new SettingsPresenter(settingsScenarioSimulationDropdownMock, settingsViewMock) {
            {
                this.settings = settingsLocal;
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
        assertTrue(settingsLocal.isSkipFromBuild());
        verify(settingsPresenter, times(1)).saveRuleSettings();
        verify(saveCommandMock, times(1)).execute();
        reset(saveCommandMock);
        settingsPresenter.onSaveButton(ScenarioSimulationModel.Type.DMN.name());
        assertTrue(settingsLocal.isSkipFromBuild());
        verify(settingsPresenter, times(1)).saveDMNSettings();
        verify(saveCommandMock, times(1)).execute();
    }

    @Test
    public void onSaveButtonSkipFalse() {
        when(skipFromBuildMock.isChecked()).thenReturn(false);
        settingsPresenter.onSaveButton(ScenarioSimulationModel.Type.RULE.name());
        verify(settingsPresenter, times(1)).saveRuleSettings();
        assertFalse(settingsLocal.isSkipFromBuild());
        verify(saveCommandMock, times(1)).execute();
        reset(saveCommandMock);
        settingsPresenter.onSaveButton(ScenarioSimulationModel.Type.DMN.name());
        assertFalse(settingsLocal.isSkipFromBuild());
        verify(settingsPresenter, times(1)).saveDMNSettings();
        verify(saveCommandMock, times(1)).execute();
    }

    @Test
    public void setScenarioTypeRULESkipTrue() {
        settingsLocal.setSkipFromBuild(true);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.RULE, settingsLocal, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setRuleSettings(settingsLocal);
    }

    @Test
    public void setScenarioTypeRULESkipFalse() {
        settingsLocal.setSkipFromBuild(false);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.RULE, settingsLocal, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setRuleSettings(settingsLocal);
    }

    @Test
    public void setScenarioTypeDMNSkipTrue() {
        settingsLocal.setSkipFromBuild(true);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.DMN, settingsLocal, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setDMNSettings(settingsLocal);
    }

    @Test
    public void setScenarioTypeDMNSkipFalse() {
        settingsLocal.setSkipFromBuild(false);
        settingsPresenter.setScenarioType(ScenarioSimulationModel.Type.DMN, settingsLocal, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(saveButtonMock, times(1)).setDisabled(eq(false));
        verify(settingsPresenter, times(1)).setDMNSettings(settingsLocal);
    }

    @Test
    public void setRuleSettings() {
        settingsPresenter.setRuleSettings(settingsLocal);
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmoSessionMock, times(1)).setValue(eq(DMO_SESSION));
        verify(ruleFlowGroupMock, times(1)).setValue(eq(RULE_FLOW_GROUP));
        verify(statelessMock, times(1)).setChecked(eq(settingsLocal.isStateless()));
    }

    @Test
    public void setDMNSettings() {
        settingsPresenter.setDMNSettings(settingsLocal);
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
        assertEquals(DMO_SESSION, settingsLocal.getDmoSession());
        assertEquals(RULE_FLOW_GROUP, settingsLocal.getRuleFlowGroup());
        assertFalse(settingsLocal.isStateless());
    }

    @Test
    public void saveDMNSettings() {
        settingsPresenter.saveDMNSettings();
        verify(settingsScenarioSimulationDropdownMock, times(1)).getValue();
        assertEquals(DMN_FILE_PATH, settingsLocal.getDmnFilePath());
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
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(
                eq(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorLabel(settingsLocal.getDmnFilePath())));
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