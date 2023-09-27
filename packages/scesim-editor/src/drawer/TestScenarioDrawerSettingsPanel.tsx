/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";

import { Checkbox } from "@patternfly/react-core/dist/esm/components/Checkbox";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/esm/components/FormSelect";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

import { TestScenarioSettings, TestScenarioType } from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

import "./TestScenarioDrawerSettingsPanel.css";
import { Icon } from "@patternfly/react-core/dist/esm/components/Icon/Icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/esm/icons/info-circle-icon";
import { Tooltip } from "@patternfly/react-core/dist/esm/components/Tooltip";

function TestScenarioDrawerSettingsPanel({
  fileName,
  onUpdateSettingField,
  testScenarioSettings,
}: {
  fileName: string;
  onUpdateSettingField: (field: string, value: boolean | string) => void;
  testScenarioSettings: TestScenarioSettings;
}) {
  const { i18n } = useTestScenarioEditorI18n();

  return (
    <>
      <Title headingLevel={"h6"}>{i18n.drawer.settings.fileName}</Title>
      <TextInput value={fileName} type="text" isDisabled />
      <Title headingLevel={"h6"}>{i18n.drawer.settings.assetType}</Title>
      <TextInput value={testScenarioSettings.assetType} type="text" isDisabled />
      {testScenarioSettings.assetType === TestScenarioType[TestScenarioType.DMN] ? (
        <>
          <Title headingLevel={"h6"}>{i18n.drawer.settings.dmnModel}</Title>
          {/* Temporary Mocked */}
          <FormSelect value={"1"} aria-label="FormSelect Input" ouiaId="BasicFormSelect">
            <FormSelectOption isDisabled={true} key={0} value={"1"} label={"MockedDMN.dmn"} />
            <FormSelectOption isDisabled={true} key={1} value={"2"} label={"MockedDMN2.dmn"} />
          </FormSelect>
          <Title headingLevel={"h6"}>{i18n.drawer.settings.dmnName}</Title>
          <TextInput value={testScenarioSettings.dmnName} type="text" isDisabled />
          <Title headingLevel={"h6"}>{i18n.drawer.settings.dmnNameSpace}</Title>
          <TextInput value={testScenarioSettings.dmnNamespace} type="text" isDisabled />
        </>
      ) : (
        <>
          <Title headingLevel={"h6"}>
            {i18n.drawer.settings.kieSessionRule}
            <Tooltip content={i18n.drawer.settings.kieSessionRuleTooltip}>
              <Icon className={"kie-scesim-editor-drawer-settings--info-icon"} size="sm" status="info">
                <InfoCircleIcon />
              </Icon>
            </Tooltip>
          </Title>
          <TextInput
            onChange={(value) => onUpdateSettingField("dmoSession", value)}
            placeholder={i18n.drawer.settings.kieSessionRulePlaceholder}
            type="text"
            value={testScenarioSettings.kieSessionRule}
          />
          <Title headingLevel={"h6"}>
            {i18n.drawer.settings.ruleFlowGroup}
            <Tooltip content={i18n.drawer.settings.ruleFlowGroupTooltip}>
              <Icon className={"kie-scesim-editor-drawer-settings--info-icon"} size="sm" status="info">
                <InfoCircleIcon />
              </Icon>
            </Tooltip>
          </Title>
          <TextInput
            onChange={(value) => onUpdateSettingField("ruleFlowGroup", value)}
            placeholder={i18n.drawer.settings.ruleFlowGroupPlaceholder}
            type="text"
            value={testScenarioSettings.ruleFlowGroup}
          />
          <div className={"kie-scesim-editor-drawer-settings--checkbox-group"}>
            <div className={"kie-scesim-editor-drawer-settings--checkbox"}>
              <Checkbox
                id="stateless-session"
                isChecked={testScenarioSettings.isStatelessSessionRule}
                label={i18n.drawer.settings.statelessSessionRule}
                onChange={(value) => onUpdateSettingField("stateless", value)}
              />
            </div>
            <Tooltip content={i18n.drawer.settings.statelessSessionRuleTooltip}>
              <Icon className={"kie-scesim-editor-drawer-settings--info-icon"} size="sm" status="info">
                <InfoCircleIcon />
              </Icon>
            </Tooltip>
          </div>
        </>
      )}
      <div className={"kie-scesim-editor-drawer-settings--checkbox-group"}>
        <div className={"kie-scesim-editor-drawer-settings--checkbox"}>
          <Checkbox
            id="skip-test"
            isChecked={testScenarioSettings.isTestSkipped}
            label={i18n.drawer.settings.testSkipped}
            onChange={(value) => onUpdateSettingField("skipFromBuild", value)}
          />
        </div>
        <Tooltip content={i18n.drawer.settings.testSkippedTooltip}>
          <Icon className={"kie-scesim-editor-drawer-settings--info-icon"} size="sm" status="info">
            <InfoCircleIcon />
          </Icon>
        </Tooltip>
      </div>
    </>
  );
}

export default TestScenarioDrawerSettingsPanel;
