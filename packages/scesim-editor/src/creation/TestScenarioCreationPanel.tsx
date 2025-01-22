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
import { useCallback, useMemo, useState } from "react";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { basename } from "path";

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { HelpIcon } from "@patternfly/react-icons/dist/esm/icons/help-icon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import AddIcon from "@patternfly/react-icons/dist/esm/icons/add-circle-o-icon";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";

import { useTestScenarioEditorStoreApi } from "../store/TestScenarioStoreContext";
import { useTestScenarioEditorI18n } from "../i18n";

import "./TestScenarioCreationPanel.css";
import { useExternalModels } from "../externalModels/TestScenarioEditorDependenciesContext";
import { ExternalDmn } from "../TestScenarioEditor";
import { useTestScenarioEditor } from "../TestScenarioEditorContext";
import { createNewDmnTypeTestScenario } from "../mutations/createNewDmnTypeTestScenario";
import { createNewRuleTypeTestScenario } from "../mutations/createNewRuleTypeTestScenario";

function TestScenarioCreationPanel() {
  const { i18n } = useTestScenarioEditorI18n();
  const { onRequestExternalModelsAvailableToInclude, onRequestExternalModelByPath } = useExternalModels();
  const { onRequestToResolvePath } = useTestScenarioEditor();
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();

  const [assetType, setAssetType] = React.useState<"" | "DMN" | "RULE">("");
  const [isAutoFillTableEnabled, setAutoFillTableEnabled] = React.useState(true);
  const [isStatelessSessionRule, setStatelessSessionRule] = React.useState(false);
  const [isTestSkipped, setTestSkipped] = React.useState(false);
  const [kieSessionRule, setKieSessionRule] = React.useState("");
  const [availableDmnModelPaths, setAvailableDmnModelPaths] = useState<string[] | undefined>(undefined);
  const [selectedDmnPathRelativeToThisScesim, setSelectedDmnPathRelativeToThisScesim] = useState<string | undefined>(
    undefined
  );
  const [ruleFlowGroup, setRuleFlowGroup] = React.useState("");
  const [selectedDmnModel, setSelectedDmnModel] = useState<ExternalDmn | undefined>(undefined);
  const [selectedModelError, setSelectedModelError] = useState<string | undefined>(undefined);

  const assetsOption = [
    { value: "", label: i18n.creationPanel.assetsOption.noChoice, disabled: true },
    { value: "DMN", label: i18n.creationPanel.assetsOption.dmn, disabled: false },
    { value: "RULE", label: i18n.creationPanel.assetsOption.rule, disabled: false },
  ];

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!selectedDmnPathRelativeToThisScesim || onRequestExternalModelByPath === undefined) {
          return;
        }

        onRequestExternalModelByPath(selectedDmnPathRelativeToThisScesim)
          .then((externalModel) => {
            if (canceled.get() || !externalModel) {
              return;
            }

            setSelectedModelError(undefined);
            setSelectedDmnModel(externalModel);
          })
          .catch((err) => {
            setSelectedModelError(
              `An error occurred when parsing the selected model '${selectedDmnPathRelativeToThisScesim}'. Please double-check it is a non-empty valid model.`
            );
            console.error(err);
            return;
          });
      },
      [onRequestExternalModelByPath, selectedDmnPathRelativeToThisScesim]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        onRequestExternalModelsAvailableToInclude?.()
          .then((paths) => {
            if (canceled.get()) {
              return;
            }
            setAvailableDmnModelPaths(paths);
          })
          .catch((err) => {
            console.error(err);
            return;
          });
      },
      [onRequestExternalModelsAvailableToInclude]
    )
  );

  const createTestScenario = useCallback(
    () =>
      testScenarioEditorStoreApi.setState((state) => {
        assetType === "DMN"
          ? createNewDmnTypeTestScenario({
              dmnModel: selectedDmnModel!,
              factMappingsModel:
                state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!,
              factMappingValuesModel: state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!,
              isAutoFillTableEnabled,
              isTestSkipped,
              settingsModel: state.scesim.model.ScenarioSimulationModel.settings,
            })
          : createNewRuleTypeTestScenario({
              factMappingsModel:
                state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!,
              factMappingValuesModel: state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!,
              isStatelessSessionRule,
              isTestSkipped,
              kieSessionRule,
              ruleFlowGroup,
              settingsModel: state.scesim.model.ScenarioSimulationModel.settings,
            });
      }),
    [
      assetType,
      isAutoFillTableEnabled,
      isStatelessSessionRule,
      isTestSkipped,
      kieSessionRule,
      ruleFlowGroup,
      selectedDmnModel,
      testScenarioEditorStoreApi,
    ]
  );

  return (
    <EmptyState>
      <EmptyStateIcon icon={CubesIcon} />
      <Title headingLevel={"h6"} size={"md"}>
        {i18n.creationPanel.title}
      </Title>
      <Form isHorizontal className="kie-scesim-editor--creation-form">
        <FormGroup label={i18n.creationPanel.assetsGroup} isRequired>
          <FormSelect
            id="asset-type-select"
            name="asset-type-select"
            onChange={(value: "" | "DMN" | "RULE") => setAssetType(value)}
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
              <FormSelect
                id="dmn-select"
                name="dmn-select"
                onChange={(path) => {
                  if (typeof path !== "string") {
                    throw new Error(`Invalid path for an included model ${JSON.stringify(path)}`);
                  }
                  setSelectedDmnPathRelativeToThisScesim(path);
                  console.trace(path);
                }}
                value={selectedDmnPathRelativeToThisScesim}
              >
                <FormSelectOption key={undefined} isDisabled label={i18n.creationPanel.dmnNoChoice} value={""} />
                {((availableDmnModelPaths?.length ?? 0) > 0 &&
                  availableDmnModelPaths?.map((path) => {
                    const normalizedPosixPathRelativeToTheWorkspaceRoot = onRequestToResolvePath?.(path) ?? path;
                    return (
                      <FormSelectOption
                        key={path}
                        value={normalizedPosixPathRelativeToTheWorkspaceRoot}
                        label={basename(normalizedPosixPathRelativeToTheWorkspaceRoot)}
                      />
                    );
                  })) || (
                  <FormSelectOption key={undefined} isDisabled label={i18n.creationPanel.dmnNoPresent} value={""} />
                )}
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
                onChange={(value: boolean) => {
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
                onChange={(value) => setKieSessionRule(value)}
                placeholder={"<" + i18n.creationPanel.optional + ">"}
                type="text"
                value={kieSessionRule}
              />
            </FormGroup>
            <FormGroup label={i18n.creationPanel.kieAgendaGroup}>
              <TextInput
                onChange={(value) => setRuleFlowGroup(value)}
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
                onChange={(value) => {
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
            onChange={(value: boolean) => {
              setTestSkipped(value);
            }}
          />
        </FormGroup>
      </Form>
      <Button
        icon={<AddIcon />}
        isDisabled={assetType === "" || (assetType === "DMN" && !selectedDmnModel)}
        onClick={createTestScenario}
        variant="primary"
      >
        {i18n.creationPanel.createButton}
      </Button>
    </EmptyState>
  );
}

export default TestScenarioCreationPanel;
