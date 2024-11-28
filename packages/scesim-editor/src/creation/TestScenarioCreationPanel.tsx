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
import { useCallback } from "react";

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { HelpIcon } from "@patternfly/react-icons/dist/esm/icons/help-icon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import {} from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import AddIcon from "@patternfly/react-icons/dist/esm/icons/add-circle-o-icon";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";

import { useTestScenarioEditorStoreApi } from "../store/TestScenarioStoreContext";
import { useTestScenarioEditorI18n } from "../i18n";

import "./TestScenarioCreationPanel.css";

function TestScenarioCreationPanel() {
  const { i18n } = useTestScenarioEditorI18n();

  const assetsOption = [
    { value: "", label: i18n.creationPanel.assetsOption.noChoice, disabled: true },
    { value: "DMN", label: i18n.creationPanel.assetsOption.dmn, disabled: false },
    { value: "RULE", label: i18n.creationPanel.assetsOption.rule, disabled: false },
  ];

  const [assetType, setAssetType] = React.useState<"" | "DMN" | "RULE">("");
  const [isAutoFillTableEnabled, setAutoFillTableEnabled] = React.useState(true);
  const [isStatelessSessionRule, setStatelessSessionRule] = React.useState(false);
  const [isTestSkipped, setTestSkipped] = React.useState(false);
  const [kieSessionRule, setKieSessionRule] = React.useState("");
  const [ruleFlowGroup, setRuleFlowGroup] = React.useState("");
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();

  const createTestScenario = useCallback(
    (
      assetType: string,
      isStatelessSessionRule: boolean,
      isTestSkipped: boolean,
      kieSessionRule: string,
      ruleFlowGroup: string
    ) =>
      testScenarioEditorStoreApi.setState((state) => {
        const settings = state.scesim.model.ScenarioSimulationModel.settings;
        settings.dmnFilePath = assetType === "DMN" ? { __$$text: "./MockedDMNName.dmn" } : undefined;
        settings.dmnName = assetType === "DMN" ? { __$$text: "MockedDMNName.dmn" } : undefined;
        settings.dmnNamespace = assetType === "DMN" ? { __$$text: "https:\\kiegroup" } : undefined;
        settings.dmoSession = assetType === "RULE" && kieSessionRule ? { __$$text: kieSessionRule } : undefined;
        settings.ruleFlowGroup = assetType === "RULE" && ruleFlowGroup ? { __$$text: ruleFlowGroup } : undefined;
        settings.skipFromBuild = { __$$text: isTestSkipped };
        settings.stateless = assetType === "RULE" ? { __$$text: isStatelessSessionRule } : undefined;
        settings.type = { __$$text: assetType };
      }),
    [testScenarioEditorStoreApi]
  );

  return (
    <EmptyState>
      <EmptyStateHeader
        titleText={<>{i18n.creationPanel.title}</>}
        icon={<EmptyStateIcon icon={CubesIcon} />}
        headingLevel={"h6"}
      />
      <EmptyStateFooter>
        <Form isHorizontal className="kie-scesim-editor--creation-form">
          <FormGroup label={i18n.creationPanel.assetsGroup} isRequired>
            <FormSelect
              id="asset-type-select"
              name="asset-type-select"
              onChange={(_event, value: "" | "DMN" | "RULE") => setAssetType(value)}
              value={assetType}
            >
              {assetsOption.map((option, index) => (
                <FormSelectOption isDisabled={option.disabled} key={index} label={option.label} value={option.value} />
              ))}
            </FormSelect>
          </FormGroup>
          {assetType === "DMN" && (
            <>
              <FormGroup label={i18n.creationPanel.dmnGroup} isRequired>
                <FormSelect id="dmn-select" name="dmn-select" value={""} isDisabled>
                  <FormSelectOption isDisabled={true} key={0} value={""} label={i18n.creationPanel.dmnNoChoice} />
                </FormSelect>
              </FormGroup>
              <FormGroup>
                <Checkbox
                  id="auto-fill-table-checkbox"
                  isChecked={isAutoFillTableEnabled}
                  label={
                    <>
                      <span>{i18n.creationPanel.autoFillTable}</span>
                      <Tooltip content={i18n.creationPanel.autoFillTableTooltip}>
                        <Icon className={"kie-scesim-editor-creation-panel--info-icon"} size="sm" status="info">
                          <HelpIcon />
                        </Icon>
                      </Tooltip>
                    </>
                  }
                  onChange={(_event, value: boolean) => {
                    setAutoFillTableEnabled(value);
                  }}
                />
              </FormGroup>
            </>
          )}
          {assetType === "RULE" && (
            <>
              <FormGroup label={i18n.creationPanel.kieSessionGroup}>
                <TextInput
                  onChange={(_event, value) => setKieSessionRule(value)}
                  placeholder={"<" + i18n.creationPanel.optional + ">"}
                  type="text"
                  value={kieSessionRule}
                />
              </FormGroup>
              <FormGroup label={i18n.creationPanel.kieAgendaGroup}>
                <TextInput
                  onChange={(_event, value) => setRuleFlowGroup(value)}
                  placeholder={"<" + i18n.creationPanel.optional + ">"}
                  type="text"
                  value={ruleFlowGroup}
                />
              </FormGroup>
              <FormGroup>
                <Checkbox
                  id="stateless-session-checkbox"
                  isChecked={isStatelessSessionRule}
                  label={
                    <>
                      <span>{i18n.creationPanel.statelessSession}</span>
                      <Tooltip content={i18n.drawer.settings.statelessSessionRuleTooltip}>
                        <Icon className={"kie-scesim-editor-creation-panel--info-icon"} size="sm" status="info">
                          <HelpIcon />
                        </Icon>
                      </Tooltip>
                    </>
                  }
                  onChange={(_event, value) => {
                    setStatelessSessionRule(value);
                  }}
                />
              </FormGroup>
            </>
          )}
          <FormGroup>
            <Checkbox
              id="test-skipped-checkbox"
              isChecked={isTestSkipped}
              label={
                <>
                  <span>{i18n.creationPanel.testSkip}</span>
                  <Tooltip content={i18n.drawer.settings.testSkippedTooltip}>
                    <Icon className={"kie-scesim-editor-creation-panel--info-icon"} size="sm" status="info">
                      <HelpIcon />
                    </Icon>
                  </Tooltip>
                </>
              }
              onChange={(_event, value: boolean) => {
                setTestSkipped(value);
              }}
            />
          </FormGroup>
        </Form>
        <Button
          icon={<AddIcon />}
          isDisabled={assetType == ""}
          onClick={() =>
            createTestScenario(assetType, isStatelessSessionRule, isTestSkipped, kieSessionRule, ruleFlowGroup)
          }
          variant="primary"
        >
          {i18n.creationPanel.createButton}
        </Button>
      </EmptyStateFooter>
    </EmptyState>
  );
}

export default TestScenarioCreationPanel;
