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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsViewImpl.DMN_MODEL_LABEL;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsViewImpl.DMN_NAMESPACE_LABEL;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsViewImpl.DMN_NAME_LABEL;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsViewImpl.STATELESS_LABEL;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SettingsViewImplTest extends AbstractSettingsTest {

    private SettingsViewImpl settingsViewSpy;

    @Mock
    private ChangeEvent changeEventMock;

    @Mock
    private BlurEvent blurEventMock;

    @Mock
    private SettingsPresenter settingsPresenterMock;

    @Before
    public void setup() {
        super.setup();
        this.settingsViewSpy = spy(new SettingsViewImpl() {
            {
                this.kieSettingsContent = kieSettingsContentMock;
                this.nameLabel = nameLabelMock;
                this.fileName = fileNameMock;
                this.typeLabel = typeLabelMock;
                this.scenarioType = scenarioTypeMock;
                this.ruleSettings = ruleSettingsMock;
                this.dmoSession = dmoSessionMock;
                this.ruleFlowGroup = ruleFlowGroupMock;
                this.dmnSettings = dmnSettingsMock;
                this.dmnFileLabel = dmnModelLabelMock;
                this.dmnFilePathPlaceholder = dmnFilePathPlaceholderMock;
                this.dmnFilePathErrorLabel = dmnFilePathErrorLabelMock;
                this.dmnNamespaceLabel = dmnNamespaceLabelMock;
                this.dmnNamespace = dmnNamespaceMock;
                this.dmnNameLabel = dmnNameLabelMock;
                this.dmnName = dmnNameMock;
                this.skipFromBuild = skipFromBuildMock;
                this.stateless = statelessMock;
                this.statelessLabel = statelessLabelMock;
            }
        });
        settingsViewSpy.init(settingsPresenterMock);
    }

    @Test
    public void init() {
        settingsViewSpy.init(settingsPresenterMock);
        assertEquals(settingsPresenterMock, settingsViewSpy.presenter);
        nameLabelMock.setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.name()));
        typeLabelMock.setInnerText(eq(ScenarioSimulationEditorConstants.INSTANCE.type()));
        statelessLabelMock.setInnerText(eq(STATELESS_LABEL));
        dmnModelLabelMock.setInnerText(eq(DMN_MODEL_LABEL));
        dmnNamespaceLabelMock.setInnerText(eq(DMN_NAMESPACE_LABEL));
        dmnNameLabelMock.setInnerText(eq(DMN_NAME_LABEL));
    }

    @Test
    public void resetTest() {
        settingsViewSpy.reset();
        verify(scenarioTypeMock, times(1)).setInnerText(eq(""));
        verify(fileNameMock, times(1)).setValue(eq(""));
        verify(dmnNameMock, times(1)).setValue(eq(""));
        verify(dmnNamespaceMock, times(1)).setValue(eq(""));
        verify(dmnFilePathErrorLabelStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnFilePathErrorLabelMock, times(1)).setInnerText(eq(""));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(statelessMock, times(1)).setChecked(eq(false));
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void syncDmoSession() {
        settingsViewSpy.syncDmoSession(blurEventMock);
        verify(settingsPresenterMock, times(1)).syncDmoSession();
    }

    @Test
    public void syncRuleFlowGroup() {
        settingsViewSpy.syncRuleFlowGroup(blurEventMock);
        verify(settingsPresenterMock, times(1)).syncRuleFlowGroup();
    }

    @Test
    public void syncStateless() {
        settingsViewSpy.syncStateless(changeEventMock);
        verify(settingsPresenterMock, times(1)).syncStateless();
    }

    @Test
    public void syncSkipFromBuild() {
        settingsViewSpy.syncSkipFromBuild(changeEventMock);
        verify(settingsPresenterMock, times(1)).syncSkipFromBuild();
    }
}