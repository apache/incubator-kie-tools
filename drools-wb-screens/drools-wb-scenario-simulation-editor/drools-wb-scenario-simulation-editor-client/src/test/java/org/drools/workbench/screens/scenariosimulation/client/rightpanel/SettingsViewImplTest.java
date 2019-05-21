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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SCENARIO_TYPE;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SettingsViewImplTest extends AbstractSettingsTest {

    private SettingsViewImpl settingsView;

    @Mock
    private SettingsPresenter settingsPresenterMock;

    @Before
    public void setup() {
        super.setup();
        this.settingsView = spy(new SettingsViewImpl() {
            {
                this.nameLabel = nameLabelMock;
                this.fileName = fileNameMock;
                this.typeLabel = typeLabelMock;
                this.scenarioType = scenarioTypeMock;
                this.ruleSettings = ruleSettingsMock;
                this.dmoSession = dmoSessionMock;
                this.ruleFlowGroup = ruleFlowGroupMock;
                this.dmnSettings = dmnSettingsMock;
                this.dmnFileLabel = dmnModelLabelMock;
                this.dmnFilePath = dmnFilePathMock;
                this.dmnNamespaceLabel = dmnNamespaceLabelMock;
                this.dmnNamespace = dmnNamespaceMock;
                this.dmnNameLabel = dmnNameLabelMock;
                this.dmnName = dmnNameMock;
                this.skipFromBuild = skipFromBuildMock;
                this.saveButton = saveButtonMock;
            }
        });
        settingsView.init(settingsPresenterMock);
    }

    @Test
    public void onSaveButtonClickEvent() {
        settingsView.onSaveButtonClickEvent(mock(ClickEvent.class));
        verify(settingsPresenterMock, times(1)).onSaveButton(SCENARIO_TYPE);
    }

    @Test
    public void resetTest() {
        settingsView.reset();
        verify(scenarioTypeMock, times(1)).setInnerText(eq(""));
        verify(fileNameMock, times(1)).setInnerText(eq(""));
        verify(skipFromBuildMock, times(1)).setChecked(eq(false));
        verify(ruleSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
        verify(dmnSettingsStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }
}