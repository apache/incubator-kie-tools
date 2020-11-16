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

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.screens.scenariosimulation.client.dropdown.SettingsScenarioSimulationDropdown;
import org.drools.workbench.screens.scenariosimulation.client.events.UpdateSettingsDataEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ValidateSimulationEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_FILE_PATH;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAMESPACE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMO_SESSION;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FILE_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.RULE_FLOW_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SKIP_FROM_BUILD;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.STATELESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SettingsPresenterTest extends AbstractSettingsTest {

    private SettingsPresenter settingsPresenterSpy;

    @Mock
    private Command saveCommandMock;

    @Mock
    private SettingsScenarioSimulationDropdown settingsScenarioSimulationDropdownMock;

    @Mock
    private EventBus eventBusMock;

    @Captor
    private ArgumentCaptor<UpdateSettingsDataEvent> updateSettingsDataEventArgumentCaptor;

    protected Settings settingsSpy;

    @Before
    public void setup() {
        super.setup();
        Settings settings = new Settings();
        settings.setRuleFlowGroup(RULE_FLOW_GROUP);
        settings.setDmoSession(DMO_SESSION);
        settings.setDmnFilePath(DMN_FILE_PATH);
        settings.setDmnNamespace(DMN_NAMESPACE);
        settings.setDmnName(DMN_NAME);
        settings.setStateless(STATELESS);
        settings.setSkipFromBuild(SKIP_FROM_BUILD);
        // spy after initialization to avoid double counting
        settingsSpy = spy(settings);

        KieAssetsDropdownItem item = new KieAssetsDropdownItem("DMNFile", "", DMN_FILE_PATH, null);
        when(settingsScenarioSimulationDropdownMock.getValue()).thenReturn(Optional.of(item));

        this.settingsPresenterSpy = spy(new SettingsPresenter(settingsScenarioSimulationDropdownMock, settingsViewMock) {
            {
                this.settingsScenarioSimulationDropdown = settingsScenarioSimulationDropdownMock;
                this.saveCommand = saveCommandMock;
                this.eventBus = eventBusMock;
            }
        });
    }

    @Test
    public void onSetup() {
        settingsPresenterSpy.setup();
        verify(settingsViewMock, times(1)).init(settingsPresenterSpy);
    }

    @Test
    public void getTitle() {
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.settings(), settingsPresenterSpy.getTitle());
    }

    @Test
    public void setEventBus() {
        settingsPresenterSpy.setEventBus(eventBusMock);
        assertEquals(eventBusMock, settingsPresenterSpy.eventBus);
    }

    @Test
    public void setScenarioTypeRULESkipTrue() {
        settingsSpy.setSkipFromBuild(true);
        settingsPresenterSpy.setScenarioType(ScenarioSimulationModel.Type.RULE, settingsSpy, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(settingsPresenterSpy, times(1)).setRuleSettings(settingsSpy);
    }

    @Test
    public void setScenarioTypeRULESkipFalse() {
        settingsSpy.setSkipFromBuild(false);
        settingsPresenterSpy.setScenarioType(ScenarioSimulationModel.Type.RULE, settingsSpy, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.RULE.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(settingsPresenterSpy, times(1)).setRuleSettings(settingsSpy);
    }

    @Test
    public void setScenarioTypeDMNSkipTrue() {
        settingsSpy.setSkipFromBuild(true);
        settingsPresenterSpy.setScenarioType(ScenarioSimulationModel.Type.DMN, settingsSpy, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(true));
        verify(settingsPresenterSpy, times(1)).setDMNSettings(settingsSpy);
    }

    @Test
    public void setScenarioTypeDMNSkipFalse() {
        settingsSpy.setSkipFromBuild(false);
        settingsPresenterSpy.setScenarioType(ScenarioSimulationModel.Type.DMN, settingsSpy, FILE_NAME);
        verify(scenarioTypeMock, times(1)).setInnerText(eq(ScenarioSimulationModel.Type.DMN.name()));
        verify(fileNameMock, times(1)).setValue(eq(FILE_NAME));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(settingsPresenterSpy, times(1)).setDMNSettings(settingsSpy);
    }

    @Test
    public void setRuleSettings() {
        settingsPresenterSpy.setRuleSettings(settingsSpy);
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(settingsPresenterSpy, times(1)).updateRuleSettings(eq(settingsSpy));

    }

    @Test
    public void updateRuleSettings() {
        settingsPresenterSpy.updateRuleSettings(settingsSpy);
        verify(dmoSessionMock, times(1)).setValue(eq(DMO_SESSION));
        verify(ruleFlowGroupMock, times(1)).setValue(eq(RULE_FLOW_GROUP));
        verify(statelessMock, times(1)).setChecked(eq(settingsSpy.isStateless()));
    }

    @Test
    public void setDMNSettings() {
        settingsPresenterSpy.setDMNSettings(settingsSpy);
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(settingsScenarioSimulationDropdownMock, times(1)).registerOnMissingValueHandler(isA(Command.class));
        verify(settingsScenarioSimulationDropdownMock, times(1)).registerOnChangeHandler(isA(Command.class));
        verify(settingsScenarioSimulationDropdownMock, times(1)).loadAssets(eq(DMN_FILE_PATH));
        verify(settingsPresenterSpy, times(1)).updateDMNSettings(settingsSpy);
    }

    @Test
    public void updateDMNSettings() {
        settingsPresenterSpy.updateDMNSettings(settingsSpy);
        verify(dmnNameMock, times(1)).setValue(eq(DMN_NAME));
        verify(dmnNamespaceMock, times(1)).setValue(eq(DMN_NAMESPACE));
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(eq(""));
        verify(settingsScenarioSimulationDropdownMock, times(1)).loadAssets(eq(DMN_FILE_PATH));
    }

    @Test
    public void updateSettingsDataRule() {
        settingsSpy.setType(ScenarioSimulationModel.Type.RULE);
        when(ruleSettingsStyleMock.getDisplay()).thenReturn(Style.Display.INLINE.getCssName());
        settingsPresenterSpy.updateSettingsData(settingsSpy);
        verify(settingsPresenterSpy, times(1)).setRuleSettings(eq(settingsSpy));
    }

    @Test(expected = IllegalStateException.class)
    public void updateSettingsDataRuleWrongState() {
        settingsSpy.setType(ScenarioSimulationModel.Type.RULE);
        when(ruleSettingsStyleMock.getDisplay()).thenReturn(Style.Display.NONE.getCssName());
        settingsPresenterSpy.updateSettingsData(settingsSpy);
        verify(settingsPresenterSpy, never()).setRuleSettings(eq(settingsSpy));
    }

    @Test
    public void updateSettingsDataDMN() {
        settingsSpy.setType(ScenarioSimulationModel.Type.DMN);
        when(dmnSettingsStyleMock.getDisplay()).thenReturn(Style.Display.INLINE.getCssName());
        settingsPresenterSpy.updateSettingsData(settingsSpy);
        verify(settingsPresenterSpy, times(1)).setDMNSettings(eq(settingsSpy));
    }

    @Test(expected = IllegalStateException.class)
    public void updateSettingsDataDMNWrongState() {
        settingsSpy.setType(ScenarioSimulationModel.Type.DMN);
        when(dmnSettingsStyleMock.getDisplay()).thenReturn(Style.Display.NONE.getCssName());
        settingsPresenterSpy.updateSettingsData(settingsSpy);
        verify(settingsPresenterSpy, never()).setDMNSettings(eq(settingsSpy));
    }

    @Test
    public void resetTest() {
        settingsPresenterSpy.reset();
        verify(settingsViewMock, times(1)).reset();
        verify(settingsScenarioSimulationDropdownMock, times(1)).clear();
    }

    @Test
    public void setDmnErrorPath() {
        settingsPresenterSpy.setDmnErrorPath(settingsSpy.getDmnFilePath());
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(
                eq(ScenarioSimulationEditorConstants.INSTANCE.dmnPathErrorLabel(settingsSpy.getDmnFilePath())));
    }

    @Test
    public void validateDmnPath_Valid() {
        settingsPresenterSpy.validateSimulation();
        verify(settingsScenarioSimulationDropdownMock, times(2)).getValue();
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(eq(""));
        verify(settingsPresenterSpy, times(1)).syncDmnFilePath();
        verify(eventBusMock, times(1)).fireEvent(isA(ValidateSimulationEvent.class));
    }

    @Test
    public void validateDmnPath_Invalid() {
        KieAssetsDropdownItem item = new KieAssetsDropdownItem("DMNFile", "", "", null);
        when(settingsScenarioSimulationDropdownMock.getValue()).thenReturn(Optional.of(item));
        settingsPresenterSpy.validateSimulation();
        verify(settingsScenarioSimulationDropdownMock, times(1)).getValue();
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.INLINE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.chooseValidDMNAsset()));
        verify(settingsPresenterSpy, never()).syncDmnFilePath();
        verify(eventBusMock, never()).fireEvent(isA(ValidateSimulationEvent.class));
    }

    @Test
    public void syncDmoSessionSameValue() {
        settingsPresenterSpy.syncDmoSession();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        assertFalse(updateSettingsDataEventArgumentCaptor.getValue().getSettingsValueChanged().test(settingsSpy));
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setDmoSession(eq(DMO_SESSION));
    }

    @Test
    public void syncDmoSessionChangedValue() {
        InputElement inputElementMock = mock(InputElement.class);
        when(inputElementMock.getValue()).thenReturn(DMO_SESSION + "2");
        doReturn(inputElementMock).when(settingsViewMock).getDmoSession();
        settingsPresenterSpy.syncDmoSession();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        assertTrue(updateSettingsDataEventArgumentCaptor.getValue().getSettingsValueChanged().test(settingsSpy));
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setDmoSession(eq(DMO_SESSION + "2"));
        assertEquals(DMO_SESSION + "2", settingsSpy.getDmoSession());
    }

    @Test
    public void syncRuleFlowGroupSameValue() {
        settingsPresenterSpy.syncRuleFlowGroup();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        assertFalse(updateSettingsDataEventArgumentCaptor.getValue().getSettingsValueChanged().test(settingsSpy));
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setRuleFlowGroup(eq(RULE_FLOW_GROUP));
    }

    @Test
    public void syncRuleFlowGroupChangedValue() {
        InputElement inputElementMock = mock(InputElement.class);
        when(inputElementMock.getValue()).thenReturn(RULE_FLOW_GROUP + "2");
        doReturn(inputElementMock).when(settingsViewMock).getRuleFlowGroup();
        settingsPresenterSpy.syncRuleFlowGroup();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        assertTrue(updateSettingsDataEventArgumentCaptor.getValue().getSettingsValueChanged().test(settingsSpy));
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setRuleFlowGroup(eq(RULE_FLOW_GROUP + "2"));
        assertEquals(RULE_FLOW_GROUP + "2", settingsSpy.getRuleFlowGroup());
    }

    @Test
    public void syncStateless() {
        settingsPresenterSpy.syncStateless();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setStateless(eq(STATELESS));
    }

    @Test
    public void syncDmnFilePathSameValue() {
        settingsPresenterSpy.syncDmnFilePath();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        assertFalse(updateSettingsDataEventArgumentCaptor.getValue().getSettingsValueChanged().test(settingsSpy));
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setDmnFilePath(eq(DMN_FILE_PATH));
    }

    @Test
    public void syncDmnFilePathChangedValue() {
        KieAssetsDropdownItem item = new KieAssetsDropdownItem("DMNFile", "", DMN_FILE_PATH + "2", null);
        when(settingsScenarioSimulationDropdownMock.getValue()).thenReturn(Optional.of(item));
        settingsPresenterSpy.syncDmnFilePath();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        assertTrue(updateSettingsDataEventArgumentCaptor.getValue().getSettingsValueChanged().test(settingsSpy));
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setDmnFilePath(eq(DMN_FILE_PATH + "2"));
        assertEquals(DMN_FILE_PATH + "2", settingsSpy.getDmnFilePath());
    }

    @Test
    public void syncSkipFromBuild() {
        settingsPresenterSpy.syncSkipFromBuild();
        verify(eventBusMock, times(1)).fireEvent(updateSettingsDataEventArgumentCaptor.capture());
        updateSettingsDataEventArgumentCaptor.getValue().getSettingsChangeToApply().accept(settingsSpy);
        verify(settingsSpy, times(1)).setSkipFromBuild(eq(SKIP_FROM_BUILD));
    }
}