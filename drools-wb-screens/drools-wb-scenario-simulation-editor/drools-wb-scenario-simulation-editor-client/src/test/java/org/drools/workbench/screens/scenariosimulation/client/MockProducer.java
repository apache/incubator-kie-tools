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
package org.drools.workbench.screens.scenariosimulation.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsView;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAMESPACE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMO_SESSION;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FILE_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.RULE_FLOW_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SCENARIO_TYPE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SKIP_FROM_BUILD;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.STATELESS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockProducer {

    public static SettingsView getSettingsViewMock() {
        SettingsView toReturn = mock(SettingsView.class);
        when(toReturn.getNameLabel()).thenReturn(nameLabelMock());
        doReturn(fileNameMock()).when(toReturn).getFileName();
        when(toReturn.getTypeLabel()).thenReturn(typeLabelMock());
        doReturn(scenarioTypeMock()).when(toReturn).getScenarioType();
        doReturn(ruleSettingsMock()).when(toReturn).getRuleSettings();
        doReturn(dmoSessionMock()).when(toReturn).getDmoSession();
        doReturn(ruleFlowGroupMock()).when(toReturn).getRuleFlowGroup();
        doReturn(dmnSettingsMock()).when(toReturn).getDmnSettings();
        when(toReturn.getDmnFileLabel()).thenReturn(dmnModelLabelMock());
        doReturn(dmnFilePathPlaceholderMock()).when(toReturn).getDmnFilePathPlaceholder();
        doReturn(dmnFilePathErrorLabelMock()).when(toReturn).getDmnFilePathErrorLabel();
        when(toReturn.getDmnNamespaceLabel()).thenReturn(dmnNamespaceLabelMock());
        doReturn(dmnNamespaceMock()).when(toReturn).getDmnNamespace();
        when(toReturn.getDmnNameLabel()).thenReturn(dmnNameLabelMock());
        doReturn(dmnNameMock()).when(toReturn).getDmnName();
        doReturn(skipFromBuildMock()).when(toReturn).getSkipFromBuild();
        doReturn(statelessMock()).when(toReturn).getStateless();
        return toReturn;
    }

    public static DivElement kieSettingsContentMock() {
        return mock(DivElement.class);
    }

    public static LabelElement typeLabelMock() {
        return mock(LabelElement.class);
    }

    public static LabelElement nameLabelMock() {
        return mock(LabelElement.class);
    }

    public static Style dmnFilePathErrorLabelStyleMock() {
        return mock(Style.class);
    }

    public static Style dmnSettingsStyleMock() {
        return mock(Style.class);
    }

    public static InputElement dmoSessionMock() {
        InputElement toReturn = mock(InputElement.class);
        when(toReturn.getValue()).thenReturn(DMO_SESSION);
        return toReturn;
    }

    public static InputElement statelessMock() {
        InputElement toReturn = mock(InputElement.class);
        when(toReturn.isChecked()).thenReturn(STATELESS);
        return toReturn;
    }

    public static InputElement skipFromBuildMock() {
        InputElement toReturn = mock(InputElement.class);
        when(toReturn.isChecked()).thenReturn(SKIP_FROM_BUILD);
        return toReturn;
    }

    public static InputElement dmnNameMock() {
        InputElement toReturn = mock(InputElement.class);
        when(toReturn.getValue()).thenReturn(DMN_NAME);
        return toReturn;
    }

    public static InputElement dmnNamespaceMock() {
        InputElement toReturn = mock(InputElement.class);
        when(toReturn.getValue()).thenReturn(DMN_NAMESPACE);
        return toReturn;
    }

    public static LabelElement dmnNamespaceLabelMock() {
        return mock(LabelElement.class);
    }

    public static SpanElement dmnFilePathErrorLabelMock() {
        SpanElement toReturn = mock(SpanElement.class);
        when(toReturn.getStyle()).thenReturn(dmnFilePathErrorLabelStyleMock());
        return toReturn;
    }

    public static DivElement dmnFilePathPlaceholderMock() {
        DivElement toReturn = mock(DivElement.class);
        when(toReturn.getInnerText()).thenReturn("");
        return toReturn;
    }

    public static LabelElement dmnModelLabelMock() {
        return mock(LabelElement.class);
    }

    public static LabelElement dmnNameLabelMock() {
        return mock(LabelElement.class);
    }

    public static DivElement dmnSettingsMock() {
        DivElement toReturn = mock(DivElement.class);
        when(toReturn.getStyle()).thenReturn(dmnSettingsStyleMock());
        return toReturn;
    }

    public static InputElement ruleFlowGroupMock() {
        InputElement toReturn = mock(InputElement.class);
        when(toReturn.getValue()).thenReturn(RULE_FLOW_GROUP);
        return toReturn;
    }

    public static SpanElement scenarioTypeMock() {
        SpanElement toReturn = mock(SpanElement.class);
        when(toReturn.getInnerText()).thenReturn(SCENARIO_TYPE);
        return toReturn;
    }

    public static InputElement fileNameMock() {
        InputElement toReturn = mock(InputElement.class);
        when(toReturn.getValue()).thenReturn(FILE_NAME);
        return toReturn;
    }

    public static Style ruleSettingsStyleMock() {
        return mock(Style.class);
    }

    public static DivElement ruleSettingsMock() {
        DivElement toReturn = mock(DivElement.class);
        when(toReturn.getStyle()).thenReturn(ruleSettingsStyleMock());
        return toReturn;
    }
}
