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

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import org.drools.workbench.screens.scenariosimulation.client.MockProducer;

abstract class AbstractSettingsTest {

    protected DivElement kieSettingsContentMock;

    protected LabelElement nameLabelMock;

    protected InputElement fileNameMock;

    protected LabelElement typeLabelMock;

    protected SpanElement scenarioTypeMock;

    protected DivElement ruleSettingsMock;

    protected Style ruleSettingsStyleMock;

    protected InputElement dmoSessionMock;

    protected InputElement ruleFlowGroupMock;

    protected DivElement dmnSettingsMock;

    protected Style dmnSettingsStyleMock;

    protected LabelElement dmnModelLabelMock;

    protected DivElement dmnFilePathPlaceholderMock;

    protected SpanElement dmnFilePathErrorLabelMock;

    protected Style dmnFilePathErrorLabelStyleMock;

    protected LabelElement dmnNamespaceLabelMock;

    protected InputElement dmnNamespaceMock;

    protected LabelElement dmnNameLabelMock;

    protected InputElement dmnNameMock;

    protected InputElement skipFromBuildMock;

    protected InputElement statelessMock;

    protected SettingsView settingsViewMock;

    protected void setup() {
        settingsViewMock = MockProducer.getSettingsViewMock();
        kieSettingsContentMock = MockProducer.kieSettingsContentMock();
        nameLabelMock = settingsViewMock.getNameLabel();
        fileNameMock = settingsViewMock.getFileName();
        typeLabelMock = settingsViewMock.getTypeLabel();
        scenarioTypeMock = settingsViewMock.getScenarioType();
        ruleSettingsMock = settingsViewMock.getRuleSettings();
        ruleSettingsStyleMock = ruleSettingsMock.getStyle();
        dmoSessionMock = settingsViewMock.getDmoSession();
        ruleFlowGroupMock = settingsViewMock.getRuleFlowGroup();
        dmnSettingsMock = settingsViewMock.getDmnSettings();
        dmnSettingsStyleMock = dmnSettingsMock.getStyle();
        dmnModelLabelMock = settingsViewMock.getDmnNameLabel();
        dmnFilePathPlaceholderMock = settingsViewMock.getDmnFilePathPlaceholder();
        dmnFilePathErrorLabelMock = settingsViewMock.getDmnFilePathErrorLabel();
        dmnFilePathErrorLabelStyleMock = dmnFilePathErrorLabelMock.getStyle();
        dmnNamespaceLabelMock = settingsViewMock.getDmnNamespaceLabel();
        dmnNamespaceMock = settingsViewMock.getDmnNamespace();
        dmnNameLabelMock = settingsViewMock.getDmnNameLabel();
        dmnNameMock = settingsViewMock.getDmnName();
        skipFromBuildMock = settingsViewMock.getSkipFromBuild();
        statelessMock = settingsViewMock.getStateless();
    }
}