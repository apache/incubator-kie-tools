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

import "@patternfly/react-core/dist/styles/base.css";

import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";

import { getMarshaller } from "@kie-tools/scesim-marshaller";
import { SceSim__ScenarioSimulationModelType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import AddIcon from "@patternfly/react-icons/dist/esm/icons/add-circle-o-icon";
import CogIcon from "@patternfly/react-icons/dist/esm/icons/cog-icon";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";
import EditIcon from "@patternfly/react-icons/dist/esm/icons/edit-alt-icon";
import InfoIcon from "@patternfly/react-icons/dist/esm/icons/info-icon";
import TableIcon from "@patternfly/react-icons/dist/esm/icons/table-icon";

import { TestToolsPanel } from "./panels/TestToolsPanel";

import { EMPTY_ONE_EIGHT } from "./resources/EmptyScesimFile";

import "./TestScenarioEditor.css";

export enum TestScenarioEditorTab {
  EDITOR,
  BACKGROUND,
}

export enum TestScenarioEditorDock {
  CHEATSHEET,
  DATA_OBJECT,
  SETTINGS,
}

export type TestScenarioEditorRef = {
  getContent(): string;
  setContent(path: string, content: string): void;
};

export function TestScenarioDocksPanel({
  setDockPanel,
}: {
  setDockPanel: (isOpen: boolean, selected: TestScenarioEditorDock) => void;
}) {
  return (
    <div className="kie-scesim-editor--right-sidebar">
      <Tooltip content={<div>Data Objects tool: It provides a tool to add your Data Objects in Test Scenarios</div>}>
        <Button
          variant="plain"
          onClick={() => setDockPanel(true, TestScenarioEditorDock.DATA_OBJECT)}
          icon={<EditIcon />}
        />
      </Tooltip>
      <Tooltip content={<div>Settings</div>}>
        <Button
          variant="plain"
          onClick={() => setDockPanel(true, TestScenarioEditorDock.SETTINGS)}
          icon={<CogIcon />}
        />
      </Tooltip>
      <Tooltip content={<div>CheatSheet: In this panel you can found useful information for Test Scenario Usage</div>}>
        <Button
          variant="plain"
          onClick={() => setDockPanel(true, TestScenarioEditorDock.CHEATSHEET)}
          icon={<InfoIcon />}
        />
      </Tooltip>
    </div>
  );
}

/* Test Scenario Creation Panel  */

export function TestScenarioCreationPanel({
  onCreateScesimButtonClicked,
}: {
  onCreateScesimButtonClicked: (assetType: string, skipFile: boolean) => void;
}) {
  const assetsOption = [
    { value: "select one", label: "Select a type", disabled: true },
    { value: "DMN", label: "Decision (DMN)", disabled: false },
    { value: "RULE", label: "Rule (DRL)", disabled: true },
  ];

  const [assetType, setAssetType] = React.useState("select one");
  const [skipFile, setSkipFile] = React.useState(false);

  return (
    <EmptyState>
      <EmptyStateIcon icon={CubesIcon} />
      <Title headingLevel={"h6"} size={"md"}>
        Create a new Test Scenario
      </Title>
      <Form isHorizontal className="kie-scesim-editor--creation-form">
        <FormGroup label="Asset type" isRequired>
          <FormSelect value={assetType} id="asset-type-select" name="asset-type-select">
            onChange=
            {(value: string) => {
              setAssetType(value);
            }}
            {assetsOption.map((option, index) => (
              <FormSelectOption isDisabled={option.disabled} key={index} value={option.value} label={option.label} />
            ))}
          </FormSelect>
        </FormGroup>
        <FormGroup label="Select DMN" isRequired>
          <FormSelect id="dmn-select" name="dmn-select" value={"select one"} isDisabled>
            <FormSelectOption isDisabled={true} key={0} value={"select one"} label={"Select a DMN file"} />
          </FormSelect>
        </FormGroup>
        <FormGroup>
          <Checkbox
            id="skip-scesim-checkbox"
            isChecked={skipFile}
            label="Skip this file during the test"
            name="skip-scesim-checkbox"
            onChange={(value: boolean) => {
              setSkipFile(value);
            }}
          />
        </FormGroup>
      </Form>
      <Button variant="primary" icon={<AddIcon />} onClick={() => onCreateScesimButtonClicked(assetType, skipFile)}>
        Create
      </Button>
    </EmptyState>
  );
}

export const TestScenarioEditor = React.forwardRef((props: {}, ref: React.Ref<TestScenarioEditorRef>) => {
  /** Test Scenario File, Model and Marshaller Management  */

  const [scesimFile, setScesimFile] = useState({ content: EMPTY_ONE_EIGHT, path: "" });

  const marshaller = useMemo(() => getMarshaller(scesimFile.content.trim()), [scesimFile]);

  const scesimInitial: { ScenarioSimulationModel: SceSim__ScenarioSimulationModelType } = useMemo(
    () => marshaller.parser.parse(),
    [marshaller.parser]
  );

  const [scesim, setScesim] = useState(scesimInitial);
  useEffect(() => {
    setScesim(scesimInitial);
  }, [scesimInitial]);

  /** Implementing Editor APIs */

  useImperativeHandle(
    ref,
    () => ({
      getContent: () => marshaller.builder.build(scesim),
      setContent: (path, content) => setScesimFile({ content: content || EMPTY_ONE_EIGHT, path: path }),
    }),
    [marshaller.builder, scesim]
  );

  /** scesim model update functions */

  const setInitialSettings = useCallback(
    (assetType: string, skipFile: boolean) =>
      setScesim((prevState) => ({
        ScenarioSimulationModel: {
          ...prevState.ScenarioSimulationModel,
          ["settings"]: {
            ...prevState.ScenarioSimulationModel["settings"],
            ["type"]: assetType,
            ["skipFromBuild"]: skipFile,
          },
        },
      })),
    [setScesim]
  );

  /** Test Scenario Tab Panel  */

  const [tab, setTab] = useState<TestScenarioEditorTab>(TestScenarioEditorTab.EDITOR);

  const onTabChanged = useCallback((event, tab) => {
    setTab(tab);
  }, []);

  /** Test Scenario Right Dock Panel  */

  const [dockPanel, setDockPanel] = useState({ isOpen: true, selected: TestScenarioEditorDock.DATA_OBJECT });

  const onDockPanelChange = useCallback((isOpen: boolean, selected: TestScenarioEditorDock.DATA_OBJECT) => {
    setDockPanel({ isOpen: isOpen, selected: selected });
  }, []);

  return (
    <>
      {scesim?.ScenarioSimulationModel["settings"]?.["type"] ? (
        <>
          <div className="kie-scesim-editor--content">
            <Tabs
              isFilled={true}
              activeKey={tab}
              onSelect={onTabChanged}
              role="region"
              className={"kie-scesim-editor--tabs"}
            >
              <Tab
                eventKey={TestScenarioEditorTab.EDITOR}
                title={
                  <>
                    <TabTitleIcon>
                      <TableIcon />
                    </TabTitleIcon>
                    <TabTitleText>Test Scenarios</TabTitleText>
                  </>
                }
              >
                {tab === TestScenarioEditorTab.EDITOR && (
                  <Drawer isExpanded={dockPanel.isOpen} isInline={true} position={"right"}>
                    <DrawerContent
                      panelContent={
                        <TestToolsPanel
                          selectedDock={dockPanel.selected}
                          onClose={() =>
                            setDockPanel((prev) => {
                              return { ...prev, isOpen: false };
                            })
                          }
                        />
                      }
                    >
                      <DrawerContentBody>
                        <div className={"kie-scesim-editor--grid-container"}>Scenario Grid</div>
                      </DrawerContentBody>
                    </DrawerContent>
                  </Drawer>
                )}
              </Tab>
              <Tab
                eventKey={TestScenarioEditorTab.BACKGROUND}
                isDisabled
                title={
                  <>
                    <TabTitleIcon>
                      <TableIcon />
                    </TabTitleIcon>
                    <TabTitleText>Background</TabTitleText>
                  </>
                }
              >
                Backgroud
              </Tab>
            </Tabs>
          </div>
          <TestScenarioDocksPanel setDockPanel={onDockPanelChange} />
        </>
      ) : (
        <TestScenarioCreationPanel onCreateScesimButtonClicked={setInitialSettings} />
      )}
    </>
  );
});
