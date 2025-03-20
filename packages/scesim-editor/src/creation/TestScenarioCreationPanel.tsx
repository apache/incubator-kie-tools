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
import { useCallback, useState } from "react";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { basename } from "path";

import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateFooter,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { HelpIcon } from "@patternfly/react-icons/dist/esm/icons/help-icon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";

import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import AddIcon from "@patternfly/react-icons/dist/esm/icons/add-circle-o-icon";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";

import { useTestScenarioEditorStoreApi } from "../store/TestScenarioStoreContext";
import { useTestScenarioEditorI18n } from "../i18n";

import { useExternalModels } from "../externalModels/TestScenarioEditorDependenciesContext";
import { ExternalDmn } from "../TestScenarioEditor";
import { createNewDmnTypeTestScenario } from "../mutations/createNewDmnTypeTestScenario";
import { createNewRuleTypeTestScenario } from "../mutations/createNewRuleTypeTestScenario";

import "./TestScenarioCreationPanel.css";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/deprecated";

function TestScenarioCreationPanel() {
  const { i18n } = useTestScenarioEditorI18n();
  const { onRequestExternalModelsAvailableToInclude, onRequestExternalModelByPath } = useExternalModels();
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();

  const [allDmnModelNormalizedPosixRelativePaths, setAllDmnModelNormalizedPosixRelativePaths] = useState<
    string[] | undefined
  >(undefined);
  const [assetType, setAssetType] = React.useState<"" | "DMN" | "RULE">("");
  const [isAutoFillTableEnabled, setAutoFillTableEnabled] = React.useState(true);
  const [isStatelessSessionRule, setStatelessSessionRule] = React.useState(false);
  const [isTestSkipped, setTestSkipped] = React.useState(false);
  const [kieSessionRule, setKieSessionRule] = React.useState("");
  const [ruleFlowGroup, setRuleFlowGroup] = React.useState("");
  const [selectedDmnModel, setSelectedDmnModel] = useState<ExternalDmn | undefined>(undefined);
  const [selectedDmnModelPathRelativeToThisScesim, setSelectedDmnModelPathRelativeToThisScesim] = useState<
    string | undefined
  >(undefined);

  const assetsOption = [
    { value: "", label: i18n.creationPanel.assetsOption.noChoice, disabled: true },
    { value: "DMN", label: i18n.creationPanel.assetsOption.dmn, disabled: false },
    { value: "RULE", label: i18n.creationPanel.assetsOption.rule, disabled: false },
  ];

  /** It retrieves all the avaiable DMN files available in the user's project */
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        onRequestExternalModelsAvailableToInclude?.()
          .then((dmnModelNormalizedPosixPathRelativePaths) => {
            console.debug(
              "[TestScenarioCreationPanel] The below external DMN models have been found ",
              dmnModelNormalizedPosixPathRelativePaths
            );

            if (canceled.get()) {
              setAllDmnModelNormalizedPosixRelativePaths(undefined);
              return;
            }
            setAllDmnModelNormalizedPosixRelativePaths(
              dmnModelNormalizedPosixPathRelativePaths.sort((modelA, modelB) =>
                basename(modelA).localeCompare(basename(modelB))
              )
            );
          })
          .catch((err) => {
            console.error(
              `[TestScenarioCreationPanel] The below error when trying to retrieve all the External DMN files from the project.`
            );
            console.error(err);
            throw err;
          });
      },
      [onRequestExternalModelsAvailableToInclude]
    )
  );

  /** It returns the unmarshalled representation of a DMN model given its path */
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!selectedDmnModelPathRelativeToThisScesim || onRequestExternalModelByPath === undefined) {
          return;
        }

        onRequestExternalModelByPath(selectedDmnModelPathRelativeToThisScesim)
          .then((externalDMNModel) => {
            console.debug(
              "[TestScenarioCreationPanel] The below external DMN model have been loaded ",
              externalDMNModel
            );

            if (canceled.get() || !externalDMNModel) {
              setSelectedDmnModel(undefined);
              return;
            }

            setSelectedDmnModel(externalDMNModel);
          })
          .catch((err) => {
            console.error(
              `[TestScenarioCreationPanel] An error occurred when parsing the selected model '${selectedDmnModelPathRelativeToThisScesim}'. Please double-check it is a non-empty valid model.`
            );
            console.error(err);
            throw err;
          });
      },
      [onRequestExternalModelByPath, selectedDmnModelPathRelativeToThisScesim]
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

  const [isModelSelectOpen, setModelSelectOpen] = useState(false);

  return (
    <EmptyState>
      <EmptyStateHeader
        titleText={<>{i18n.creationPanel.title}</>}
        icon={<EmptyStateIcon icon={CubesIcon} />}
        headingLevel={"h6"}
      />
      <EmptyStateFooter>
        <Form className="kie-scesim-editor--creation-form" isHorizontal>
          <FormGroup isRequired label={i18n.creationPanel.assetsGroup}>
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
              <FormGroup isRequired label={i18n.creationPanel.dmnGroup}>
                <Select
                  variant={SelectVariant.single}
                  id="dmn-select"
                  name="dmn-select"
                  onToggle={(_event, val) => setModelSelectOpen(val)}
                  isOpen={isModelSelectOpen}
                  typeAheadAriaLabel={"Select a model..."}
                  placeholderText={"Select a model..."}
                  maxHeight={"350px"}
                  onSelect={(e, dmnModelPathRelativeToThisScesim) => {
                    if (typeof dmnModelPathRelativeToThisScesim !== "string") {
                      throw new Error(
                        `Invalid path for an included model ${JSON.stringify(dmnModelPathRelativeToThisScesim)}`
                      );
                    }

                    console.debug(`[TestScenarioCreationPanel] Selected path ${dmnModelPathRelativeToThisScesim}`);
                    setSelectedDmnModelPathRelativeToThisScesim(dmnModelPathRelativeToThisScesim);
                    setModelSelectOpen(false);
                  }}
                  selections={selectedDmnModelPathRelativeToThisScesim}
                >
                  {((allDmnModelNormalizedPosixRelativePaths?.length ?? 0) > 0 &&
                    allDmnModelNormalizedPosixRelativePaths?.map((normalizedPosixPathRelativeToTheOpenFile) => (
                      <SelectOption
                        key={normalizedPosixPathRelativeToTheOpenFile}
                        value={normalizedPosixPathRelativeToTheOpenFile}
                        description={normalizedPosixPathRelativeToTheOpenFile}
                      >
                        {basename(normalizedPosixPathRelativeToTheOpenFile)}
                      </SelectOption>
                    ))) || [<SelectOption key={undefined} isDisabled label={i18n.creationPanel.dmnNoPresent} />]}
                </Select>
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
                    <Icon className="kie-scesim-editor-creation-panel--info-icon" size="sm" status="info">
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
        {assetType === "RULE" && (
          <Alert
            className="kie-scesim-editor-creation-panel--rule-scesim-alert"
            variant="danger"
            title="Rule based Test Scenario is not supported yet."
          />
        )}
        <Button
          icon={<AddIcon />}
          isDisabled={assetType === "" || assetType === "RULE" || (assetType === "DMN" && !selectedDmnModel)}
          onClick={createTestScenario}
          variant="primary"
        >
          {i18n.creationPanel.createButton}
        </Button>
      </EmptyStateFooter>
    </EmptyState>
  );
}

export default TestScenarioCreationPanel;
