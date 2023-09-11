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

import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";

import { testScenarioEditorDictionaries, TestScenarioEditorI18nContext, testScenarioEditorI18nDefaults } from "./i18n";

import { getMarshaller } from "@kie-tools/scesim-marshaller";
import { SceSim__ScenarioSimulationModelType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import AddIcon from "@patternfly/react-icons/dist/esm/icons/add-circle-o-icon";
import CogIcon from "@patternfly/react-icons/dist/esm/icons/cog-icon";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";
import EditIcon from "@patternfly/react-icons/dist/esm/icons/edit-alt-icon";
import ErrorIcon from "@patternfly/react-icons/dist/esm/icons/error-circle-o-icon";
import InfoIcon from "@patternfly/react-icons/dist/esm/icons/info-icon";
import TableIcon from "@patternfly/react-icons/dist/esm/icons/table-icon";

import { ErrorBoundary } from "./reactExt/ErrorBoundary";
import { TestToolsPanel } from "./panels/TestToolsPanel";

import { EMPTY_ONE_EIGHT } from "./resources/EmptyScesimFile";

import "./TestScenarioEditor.css";

/* Constants */

const CURRENT_SUPPORTED_VERSION = "1.8";

/* Enums */

export enum TestScenarioEditorDock {
  CHEATSHEET,
  DATA_OBJECT,
  SETTINGS,
}

enum TestScenarioEditorTab {
  EDITOR,
  BACKGROUND,
}

enum TestScenarioFileStatus {
  EMPTY,
  ERROR,
  NEW,
  UNSUPPORTED,
  VALID,
}

export enum TestScenarioType {
  DMN,
  RULE,
}

/* Types */

export type TestScenarioEditorRef = {
  /* TODO Convert these to Promises */
  getContent(): string;
  setContent(path: string, content: string): void;
};

/* Sub-Components */

function TestScenarioCreationPanel({
  onCreateScesimButtonClicked,
}: {
  onCreateScesimButtonClicked: (assetType: string, skipFile: boolean) => void;
}) {
  const assetsOption = [
    { value: "", label: "Select a type", disabled: true },
    { value: TestScenarioType[TestScenarioType.DMN], label: "Decision (DMN)", disabled: false },
    { value: TestScenarioType[TestScenarioType.RULE], label: "Rule (DRL)", disabled: true },
  ];

  const [assetType, setAssetType] = React.useState("");
  const [skipFile, setSkipFile] = React.useState(false);

  return (
    <EmptyState>
      <EmptyStateIcon icon={CubesIcon} />
      <Title headingLevel={"h6"} size={"md"}>
        Create a new Test Scenario
      </Title>
      <Form isHorizontal className="kie-scesim-editor--creation-form">
        <FormGroup label="Asset type" isRequired>
          <FormSelect
            value={assetType}
            id="asset-type-select"
            name="asset-type-select"
            onChange={(value: string) => {
              console.log(assetType);
              setAssetType(value);
            }}
          >
            {assetsOption.map((option, index) => (
              <FormSelectOption isDisabled={option.disabled} key={index} value={option.value} label={option.label} />
            ))}
          </FormSelect>
        </FormGroup>
        {assetType == TestScenarioType[TestScenarioType.DMN] && (
          <FormGroup label="Select DMN" isRequired>
            <FormSelect id="dmn-select" name="dmn-select" value={"select one"} isDisabled>
              <FormSelectOption isDisabled={true} key={0} value={"select one"} label={"Select a DMN file"} />
            </FormSelect>
          </FormGroup>
        )}
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
      <Button
        variant="primary"
        icon={<AddIcon />}
        isDisabled={assetType == ""}
        onClick={() => onCreateScesimButtonClicked(assetType, skipFile)}
      >
        Create
      </Button>
    </EmptyState>
  );
}

function TestScenarioDocksPanel({
  onDockButtonClicked,
}: {
  onDockButtonClicked: (selected: TestScenarioEditorDock) => void;
}) {
  return (
    <div className="kie-scesim-editor--right-sidebar">
      <Tooltip content={<div>Data Objects tool: It provides a tool to add your Data Objects in Test Scenarios</div>}>
        <Button
          variant="plain"
          onClick={() => onDockButtonClicked(TestScenarioEditorDock.DATA_OBJECT)}
          icon={<EditIcon />}
        />
      </Tooltip>
      <Tooltip content={<div>Settings</div>}>
        <Button
          variant="plain"
          onClick={() => onDockButtonClicked(TestScenarioEditorDock.SETTINGS)}
          icon={<CogIcon />}
        />
      </Tooltip>
      <Tooltip content={<div>CheatSheet: In this panel you can found useful information for Test Scenario Usage</div>}>
        <Button
          variant="plain"
          onClick={() => onDockButtonClicked(TestScenarioEditorDock.CHEATSHEET)}
          icon={<InfoIcon />}
        />
      </Tooltip>
    </div>
  );
}

function TestScenarioMainPanel() {
  const [tab, setTab] = useState<TestScenarioEditorTab>(TestScenarioEditorTab.EDITOR);

  const onTabChanged = useCallback((_event, tab) => {
    setTab(tab);
  }, []);

  const [dockPanel, setDockPanel] = useState({ isOpen: true, selected: TestScenarioEditorDock.DATA_OBJECT });

  const closeDockPanel = useCallback(() => {
    setDockPanel((prev) => {
      return { ...prev, isOpen: false };
    });
  }, []);

  const openDockPanel = useCallback((selected: TestScenarioEditorDock.DATA_OBJECT) => {
    setDockPanel({ isOpen: true, selected: selected });
  }, []);

  return (
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
                  panelContent={<TestToolsPanel selectedDock={dockPanel.selected} onClose={closeDockPanel} />}
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
      <TestScenarioDocksPanel onDockButtonClicked={openDockPanel} />
    </>
  );
}

function TestScenarioParserErrorPanel({
  parserErrorTitle,
  parserErrorMessage,
}: {
  parserErrorTitle: string;
  parserErrorMessage: string;
}) {
  return (
    <EmptyState>
      <EmptyStateIcon icon={ErrorIcon} />
      <Title headingLevel="h4" size="lg">
        {parserErrorTitle}
      </Title>
      <EmptyStateBody>{parserErrorMessage}</EmptyStateBody>
    </EmptyState>
  );
}

const TestScenarioEditorInternal = ({ forwardRef }: { forwardRef?: React.Ref<TestScenarioEditorRef> }) => {
  /** Test Scenario File, Model and Marshaller Management  */

  const [scesimFile, setScesimFile] = useState({ content: "", path: "" });

  const marshaller = useMemo(() => getMarshaller(scesimFile.content.trim()), [scesimFile]);

  const scesimLoaded: { ScenarioSimulationModel: SceSim__ScenarioSimulationModelType } = useMemo(
    () => marshaller.parser.parse(),
    [marshaller.parser]
  );

  const [scesimModel, setScesimModel] = useState(scesimLoaded);

  const scesimFileStatus = useMemo(() => {
    if (scesimModel.ScenarioSimulationModel) {
      const parserErrorField = "parsererror" as keyof typeof scesimModel.ScenarioSimulationModel;
      if (scesimModel.ScenarioSimulationModel[parserErrorField]) {
        return TestScenarioFileStatus.ERROR;
      }
      if (scesimModel.ScenarioSimulationModel["@_version"] != CURRENT_SUPPORTED_VERSION) {
        return TestScenarioFileStatus.UNSUPPORTED;
      } else if (scesimModel.ScenarioSimulationModel["settings"]?.["type"]) {
        return TestScenarioFileStatus.VALID;
      } else {
        return TestScenarioFileStatus.NEW;
      }
    } else {
      return TestScenarioFileStatus.EMPTY;
    }
  }, [scesimModel]);

  useEffect(() => {
    console.debug("SCESIM Model updated");
    console.debug(scesimLoaded);
    setScesimModel(scesimLoaded);
  }, [scesimLoaded]);

  /** Implementing Editor APIs */

  useImperativeHandle(
    forwardRef,
    () => ({
      getContent: () => marshaller.builder.build(scesimModel),
      setContent: (path, content) => {
        console.debug("SCESIM setContent called");
        console.debug("=== FILE CONTENT ===");
        console.debug(content ? content : "EMPTY FILE");
        console.debug("=== END FILE CONTENT ===");

        setScesimFile({ content: content || EMPTY_ONE_EIGHT, path: path });
      },
    }),
    [marshaller.builder, scesimModel]
  );

  /** scesim model update functions */

  const setInitialSettings = useCallback(
    (assetType: string, skipFile: boolean) =>
      setScesimModel((prevState) => ({
        ScenarioSimulationModel: {
          ...prevState.ScenarioSimulationModel,
          ["settings"]: {
            ...prevState.ScenarioSimulationModel["settings"],
            ["type"]: assetType,
            ["skipFromBuild"]: skipFile,
          },
        },
      })),
    [setScesimModel]
  );

  return (
    <>
      {(() => {
        switch (scesimFileStatus) {
          case TestScenarioFileStatus.EMPTY:
            return (
              <Bullseye>
                <Spinner aria-label="SCESIM Data loading .." />
              </Bullseye>
            );
          case TestScenarioFileStatus.ERROR:
            return (
              <TestScenarioParserErrorPanel
                parserErrorTitle={"File parsing error"}
                parserErrorMessage={
                  "Impossibile to correctly parse the provided scesim file. Most likely, the XML structure of the file " +
                  "is invalid."
                }
              />
            );
          case TestScenarioFileStatus.NEW:
            return <TestScenarioCreationPanel onCreateScesimButtonClicked={setInitialSettings} />;
          case TestScenarioFileStatus.UNSUPPORTED:
            return (
              <TestScenarioParserErrorPanel
                parserErrorTitle={
                  "This file holds a Test Scenario asset version (" +
                  scesimModel.ScenarioSimulationModel["@_version"] +
                  ") not supported"
                }
                parserErrorMessage={
                  "Most likely, this file has been generated with a very old Business Central version (< 7.30.0.Final). " +
                  "Please update your Business Central instance and download again this scesim file, it will be automatically updated to the supported version (" +
                  CURRENT_SUPPORTED_VERSION +
                  ")."
                }
              />
            );
          case TestScenarioFileStatus.VALID:
            return <TestScenarioMainPanel />;
        }
      })()}
    </>
  );
};

export const TestScenarioEditor = React.forwardRef((props: {}, ref: React.Ref<TestScenarioEditorRef>) => {
  const [scesimFileParsingError, setScesimFileParsingError] = useState<Error | null>(null);

  return (
    <I18nDictionariesProvider
      defaults={testScenarioEditorI18nDefaults}
      dictionaries={testScenarioEditorDictionaries}
      initialLocale={navigator.language}
      ctx={TestScenarioEditorI18nContext}
    >
      <ErrorBoundary
        error={
          <TestScenarioParserErrorPanel
            parserErrorTitle={"File parsing error"}
            parserErrorMessage={
              "Impossibile to correctly parse the provided scesim file. Cause: " + scesimFileParsingError?.message
            }
          />
        }
        setError={setScesimFileParsingError}
      >
        <TestScenarioEditorInternal forwardRef={ref} {...props} />
      </ErrorBoundary>
    </I18nDictionariesProvider>
  );
});
