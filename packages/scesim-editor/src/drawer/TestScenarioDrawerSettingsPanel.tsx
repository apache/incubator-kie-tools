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
  return (
    <>
      <Title headingLevel={"h6"}>Name</Title>
      <TextInput value={fileName} type="text" isDisabled />
      <Title headingLevel={"h6"}>Type</Title>
      <TextInput value={testScenarioSettings.assetType} type="text" isDisabled />
      {testScenarioSettings.assetType === TestScenarioType[TestScenarioType.DMN] ? (
        <>
          <Title headingLevel={"h6"}>DMN Model</Title>
          <FormSelect value={"1"} aria-label="FormSelect Input" ouiaId="BasicFormSelect">
            <FormSelectOption isDisabled={true} key={0} value={"1"} label={"MockedDMN.dmn"} />
            <FormSelectOption isDisabled={true} key={1} value={"2"} label={"MockedDMN2.dmn"} />
          </FormSelect>
          <Title headingLevel={"h6"}>DMN Name</Title>
          <TextInput value={testScenarioSettings.dmnName} type="text" isDisabled />
          <Title headingLevel={"h6"}>DMN Namespace</Title>
          <TextInput value={testScenarioSettings.dmnNamespace} type="text" isDisabled />
        </>
      ) : (
        <>
          <Title headingLevel={"h6"}>
            KIE Session
            <Tooltip content="Optional value. If not specified, the default session will be used.">
              <Icon className={"kie-scesim-editor-drawer-settings--info-icon"} size="sm" status="info">
                <InfoCircleIcon />
              </Icon>
            </Tooltip>
          </Title>
          <TextInput
            onChange={(value) => onUpdateSettingField("dmoSession", value)}
            placeholder={"(Optional) KieSession"}
            type="text"
            value={testScenarioSettings.kieSessionRule}
          />
          <Title headingLevel={"h6"}>
            AgendaGroup/RuleFlowGroup
            <Tooltip content="Optional value. The defined Scenario will be executed against the provided Agenda Group or RuleFlowGroup.">
              <Icon className={"kie-scesim-editor-drawer-settings--info-icon"} size="sm" status="info">
                <InfoCircleIcon />
              </Icon>
            </Tooltip>
          </Title>
          <TextInput
            onChange={(value) => onUpdateSettingField("ruleFlowGroup", value)}
            placeholder={"(Optional) AgendaGroup or RuleFlowGroup."}
            type="text"
            value={testScenarioSettings.ruleFlowGroup}
          />
          <div className={"kie-scesim-editor-drawer-settings--checkbox-group"}>
            <div className={"kie-scesim-editor-drawer-settings--checkbox"}>
              <Checkbox
                id="stateless-session"
                isChecked={testScenarioSettings.isStatelessSessionRule}
                label="Stateless Session"
                onChange={(value) => onUpdateSettingField("stateless", value)}
              />
            </div>
            <Tooltip content="Select or clear this check box to specify if the KIE Session is stateless or not. If the current KieSession is stateless and the check box is not selected, the tests will fail.">
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
            label="Skip this test scenario during the test"
            onChange={(value) => onUpdateSettingField("skipFromBuild", value)}
          />
        </div>
        <Tooltip content="If enabled, all defined Scenarios in this file will be skipped during test execution.">
          <Icon className={"kie-scesim-editor-drawer-settings--info-icon"} size="sm" status="info">
            <InfoCircleIcon />
          </Icon>
        </Tooltip>
      </div>
    </>
  );
}

export default TestScenarioDrawerSettingsPanel;
