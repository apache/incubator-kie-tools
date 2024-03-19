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
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";

import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";

import { testScenarioEditorDictionaries, TestScenarioEditorI18nContext, testScenarioEditorI18nDefaults } from "./i18n";

import { getMarshaller, SceSimModel } from "@kie-tools/scesim-marshaller";
import {
  SceSim__FactMappingType,
  SceSim__ScenarioSimulationModelType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import AddIcon from "@patternfly/react-icons/dist/esm/icons/add-circle-o-icon";
import CubesIcon from "@patternfly/react-icons/dist/esm/icons/cubes-icon";
import ErrorIcon from "@patternfly/react-icons/dist/esm/icons/error-circle-o-icon";
import TableIcon from "@patternfly/react-icons/dist/esm/icons/table-icon";
import HelpIcon from "@patternfly/react-icons/dist/esm/icons/help-icon";

import ErrorBoundary from "./reactExt/ErrorBoundary";
import TestScenarioDrawerPanel from "./drawer/TestScenarioDrawerPanel";
import TestScenarioSideBarMenu from "./sidebar/TestScenarioSideBarMenu";
import TestScenarioTable from "./table/TestScenarioTable";
import { useTestScenarioEditorI18n } from "./i18n";

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

export type TestScenarioAlert = {
  enabled: boolean;
  message?: string;
  variant: "success" | "danger" | "warning" | "info" | "default";
};

export type TestScenarioDataObject = {
  id: string;
  name: string;
  customBadgeContent?: string;
  children?: TestScenarioDataObject[];
};

export type OnSceSimModelChange = (model: SceSimModel) => void;

export type TestScenarioEditorProps = {
  /**
   * The SceSim itself.
   */
  model?: SceSimModel;
  /**
   * Called when a change occurs on `model`, so the controlled flow of the component can be done.
   */
  onModelChange?: OnSceSimModelChange;
};

export type TestScenarioEditorRef = {
  /* TODO Convert these to Promises */
  getContent(): string;
  setContent(pathRelativeToTheWorkspaceRoot: string, content: string): void;
};

export type TestScenarioSettings = {
  assetType: string;
  dmnFilePath?: string;
  dmnName?: string;
  dmnNamespace?: string;
  isStatelessSessionRule?: boolean;
  isTestSkipped: boolean;
  kieSessionRule?: string;
  ruleFlowGroup?: string;
};

/* Sub-Components */

function TestScenarioCreationPanel({
  onCreateScesimButtonClicked,
}: {
  onCreateScesimButtonClicked: (
    assetType: string,
    isStatelessSessionRule: boolean,
    isTestSkipped: boolean,
    kieSessionRule: string,
    ruleFlowGroup: string
  ) => void;
}) {
  const assetsOption = [
    { value: "", label: "Select a type", disabled: true },
    { value: TestScenarioType[TestScenarioType.DMN], label: "Decision (DMN)", disabled: false },
    { value: TestScenarioType[TestScenarioType.RULE], label: "Rule (DRL)", disabled: false },
  ];

  const [assetType, setAssetType] = React.useState("");
  const [kieSessionRule, setKieSessionRule] = React.useState("");
  const [ruleFlowGroup, setRuleFlowGroup] = React.useState("");
  const [isTestSkipped, setTestSkipped] = React.useState(false);
  const [isStatelessSessionRule, setStatelessSessionRule] = React.useState(false);

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
            onChange={(value: string) => setAssetType(value)}
          >
            {assetsOption.map((option, index) => (
              <FormSelectOption isDisabled={option.disabled} key={index} value={option.value} label={option.label} />
            ))}
          </FormSelect>
        </FormGroup>
        {assetType === TestScenarioType[TestScenarioType.DMN] && (
          <FormGroup label="Select DMN" isRequired>
            <FormSelect id="dmn-select" name="dmn-select" value={"select one"} isDisabled>
              <FormSelectOption isDisabled={true} key={0} value={"select one"} label={"Select a DMN file"} />
            </FormSelect>
          </FormGroup>
        )}
        {assetType === TestScenarioType[TestScenarioType.RULE] && (
          <>
            <FormGroup label={"KIE Session"}>
              <TextInput
                placeholder={"<Optional>"}
                onChange={(value) => setKieSessionRule(value)}
                type="text"
                value={kieSessionRule}
              />
            </FormGroup>
            <FormGroup label={"Group"}>
              <TextInput
                placeholder={"<Optional>"}
                onChange={(value) => setRuleFlowGroup(value)}
                type="text"
                value={ruleFlowGroup}
              />
            </FormGroup>
            <FormGroup>
              <Checkbox
                id="stateless-session-checkbox"
                isChecked={isStatelessSessionRule}
                label="Stateless Session"
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
            label="Skip this file during the test"
            onChange={(value: boolean) => {
              setTestSkipped(value);
            }}
          />
        </FormGroup>
      </Form>
      <Button
        variant="primary"
        icon={<AddIcon />}
        isDisabled={assetType == ""}
        onClick={() =>
          onCreateScesimButtonClicked(assetType, isStatelessSessionRule, isTestSkipped, kieSessionRule, ruleFlowGroup)
        }
      >
        Create
      </Button>
    </EmptyState>
  );
}

function TestScenarioMainPanel({
  fileName,
  scesimModel,
  updateSettingField,
  updateTestScenarioModel,
}: {
  fileName: string;
  scesimModel: { ScenarioSimulationModel: SceSim__ScenarioSimulationModelType };
  updateSettingField: (field: string, value: string) => void;
  updateTestScenarioModel: React.Dispatch<React.SetStateAction<SceSimModel>>;
}) {
  const { i18n } = useTestScenarioEditorI18n();

  const [alert, setAlert] = useState<TestScenarioAlert>({ enabled: false, variant: "info" });
  const [tab, setTab] = useState(TestScenarioEditorTab.EDITOR);

  const scenarioTableScrollableElementRef = useRef<HTMLDivElement | null>(null);
  const backgroundTableScrollableElementRef = useRef<HTMLDivElement | null>(null);

  const onTabChanged = useCallback((_event, tab) => {
    setTab(tab);
  }, []);

  const [dockPanel, setDockPanel] = useState({ isOpen: true, selected: TestScenarioEditorDock.DATA_OBJECT });

  const closeDockPanel = useCallback(() => {
    setDockPanel((prev) => {
      return { ...prev, isOpen: false };
    });
  }, []);

  const openDockPanel = useCallback((selected: TestScenarioEditorDock) => {
    setDockPanel({ isOpen: true, selected: selected });
  }, []);

  /** This is TEMPORARY */
  const dataObjectsFromScesim = useMemo(() => {
    /* To create the Data Object arrays we need an external source, in details: */
    /* DMN Data: Retrieving DMN type from linked DMN file */
    /* Java classes: Retrieving Java classes info from the user projects */
    /* At this time, none of the above are supported */
    /* Therefore, it tries to retrieve these info from the SCESIM file, if are present */

    /* Retriving Data Object from the scesim file.       
       That makes sense for previously created scesim files */

    const factsMappings: SceSim__FactMappingType[] =
      scesimModel!.ScenarioSimulationModel["simulation"]!["scesimModelDescriptor"]!["factMappings"]!["FactMapping"] ??
      [];

    const dataObjects: TestScenarioDataObject[] = [];

    /* The first two FactMapping are related to the "Number" and "Description" columns. 
       If those columns only are present, no Data Objects can be detected in the scesim file */
    for (let i = 2; i < factsMappings.length; i++) {
      if (factsMappings[i].className!.__$$text === "java.lang.Void") {
        continue;
      }
      const dataObject = dataObjects.find((value) => value.id === factsMappings[i]["factAlias"].__$$text);
      if (dataObject) {
        if (!dataObject.children?.some((value) => value.id === factsMappings[i]["expressionAlias"]?.__$$text)) {
          dataObject.children!.push({
            id: factsMappings[i]["expressionAlias"]!.__$$text,
            name: factsMappings[i]["expressionAlias"]!.__$$text,
            customBadgeContent: factsMappings[i]["className"].__$$text,
          });
        }
      } else {
        dataObjects.push({
          id: factsMappings[i]["factAlias"].__$$text,
          name: factsMappings[i]["factAlias"].__$$text,
          customBadgeContent: factsMappings[i]["factIdentifier"]!["className"]!.__$$text,
          children: [
            {
              id: factsMappings[i]["expressionAlias"]!.__$$text,
              name: factsMappings[i]["expressionAlias"]!.__$$text,
              customBadgeContent: factsMappings[i]["className"].__$$text,
            },
          ],
        });
      }
    }

    return dataObjects;
  }, [scesimModel]);

  /** It determines the Alert State */
  useEffect(() => {
    const assetType = scesimModel.ScenarioSimulationModel["settings"]!["type"]!.__$$text;

    let alertEnabled = false;
    let alertMessage = "";
    let alertVariant: "default" | "danger" | "warning" | "info" | "success" = "danger";

    if (dataObjectsFromScesim.length > 0) {
      alertMessage =
        assetType === TestScenarioType[TestScenarioType.DMN]
          ? i18n.alerts.dmnDataRetrievedFromScesim
          : i18n.alerts.ruleDataRetrievedFromScesim;
      alertEnabled = true;
    } else {
      alertMessage =
        assetType === TestScenarioType[TestScenarioType.DMN]
          ? i18n.alerts.dmnDataNotAvailable
          : i18n.alerts.ruleDataNotAvailable;
      alertVariant = assetType === TestScenarioType[TestScenarioType.DMN] ? "warning" : "danger";
      alertEnabled = true;
    }

    setAlert({ enabled: alertEnabled, message: alertMessage, variant: alertVariant });
  }, [dataObjectsFromScesim, i18n, scesimModel.ScenarioSimulationModel]);

  return (
    <>
      <div className="kie-scesim-editor--content">
        <Drawer isExpanded={dockPanel.isOpen} isInline={true} position={"right"}>
          <DrawerContent
            panelContent={
              <TestScenarioDrawerPanel
                dataObjects={dataObjectsFromScesim}
                fileName={fileName}
                onDrawerClose={closeDockPanel}
                onUpdateSettingField={updateSettingField}
                selectedDock={dockPanel.selected}
                testScenarioSettings={{
                  assetType: scesimModel.ScenarioSimulationModel.settings!.type!.__$$text,
                  dmnName: scesimModel.ScenarioSimulationModel.settings!.dmnName?.__$$text,
                  dmnNamespace: scesimModel.ScenarioSimulationModel.settings!.dmnNamespace?.__$$text,
                  isStatelessSessionRule: scesimModel.ScenarioSimulationModel.settings!.stateless?.__$$text ?? false,
                  isTestSkipped: scesimModel.ScenarioSimulationModel.settings!.skipFromBuild?.__$$text ?? false,
                  kieSessionRule: scesimModel.ScenarioSimulationModel.settings!.dmoSession?.__$$text,
                  ruleFlowGroup: scesimModel.ScenarioSimulationModel.settings!.ruleFlowGroup?.__$$text,
                }}
              />
            }
          >
            <DrawerContentBody>
              {alert.enabled && (
                <div className="kie-scesim-editor--content-alert">
                  <Alert variant={alert.variant} title={alert.message} />
                </div>
              )}
              <div className="kie-scesim-editor--content-tabs">
                <Tabs isFilled={true} activeKey={tab} onSelect={onTabChanged} role="region">
                  <Tab
                    eventKey={TestScenarioEditorTab.EDITOR}
                    title={
                      <>
                        <TabTitleIcon>
                          <TableIcon />
                        </TabTitleIcon>
                        <TabTitleText>{i18n.tab.scenarioTabTitle}</TabTitleText>
                        <Tooltip content={i18n.tab.scenarioTabInfo}>
                          <Icon size="sm" status="info">
                            <HelpIcon />
                          </Icon>
                        </Tooltip>
                      </>
                    }
                  >
                    <div
                      className="kie-scesim-editor--scenario-table-container"
                      ref={scenarioTableScrollableElementRef}
                    >
                      <TestScenarioTable
                        assetType={scesimModel.ScenarioSimulationModel.settings.type!.__$$text}
                        tableData={scesimModel.ScenarioSimulationModel.simulation}
                        scrollableParentRef={scenarioTableScrollableElementRef}
                        updateTestScenarioModel={updateTestScenarioModel}
                      />
                    </div>
                  </Tab>
                  <Tab
                    eventKey={TestScenarioEditorTab.BACKGROUND}
                    title={
                      <>
                        <TabTitleIcon>
                          <TableIcon />
                        </TabTitleIcon>
                        <TabTitleText>{i18n.tab.backgroundTabTitle}</TabTitleText>
                        <Tooltip content={i18n.tab.backgroundTabInfo}>
                          <Icon size="sm" status="info">
                            <HelpIcon />
                          </Icon>
                        </Tooltip>
                      </>
                    }
                  >
                    <div
                      className="kie-scesim-editor--background-table-container"
                      ref={backgroundTableScrollableElementRef}
                    >
                      <TestScenarioTable
                        assetType={scesimModel.ScenarioSimulationModel.settings.type!.__$$text}
                        tableData={scesimModel.ScenarioSimulationModel.background}
                        scrollableParentRef={backgroundTableScrollableElementRef}
                        updateTestScenarioModel={updateTestScenarioModel}
                      />
                    </div>
                  </Tab>
                </Tabs>
              </div>
            </DrawerContentBody>
          </DrawerContent>
        </Drawer>
      </div>
      <TestScenarioSideBarMenu selectedSideBarMenuItem={dockPanel} onSideBarButtonClicked={openDockPanel} />
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
      setContent: (normalizedPosixPathRelativeToTheWorkspaceRoot, content) => {
        console.debug("SCESIM setContent called");
        console.debug("=== FILE CONTENT ===");
        console.debug(content ? content : "EMPTY FILE");
        console.debug("=== END FILE CONTENT ===");

        setScesimFile({ content: content || EMPTY_ONE_EIGHT, path: normalizedPosixPathRelativeToTheWorkspaceRoot });
      },
    }),
    [marshaller.builder, scesimModel]
  );

  /** scesim model update functions */

  const setInitialSettings = useCallback(
    (
      assetType: string,
      isStatelessSessionRule: boolean,
      isTestSkipped: boolean,
      kieSessionRule: string,
      ruleFlowGroup: string
    ) =>
      setScesimModel((prevState) => ({
        ScenarioSimulationModel: {
          ...prevState.ScenarioSimulationModel,
          settings: {
            ...prevState.ScenarioSimulationModel.settings,
            dmnFilePath:
              assetType === TestScenarioType[TestScenarioType.DMN] ? { __$$text: "./MockedDMNName.dmn" } : undefined,
            dmoSession:
              assetType === TestScenarioType[TestScenarioType.RULE] && kieSessionRule
                ? { __$$text: kieSessionRule }
                : undefined,
            ruleFlowGroup:
              assetType === TestScenarioType[TestScenarioType.RULE] && ruleFlowGroup
                ? { __$$text: ruleFlowGroup }
                : undefined,
            skipFromBuild: { __$$text: isTestSkipped },
            stateless:
              assetType === TestScenarioType[TestScenarioType.RULE] ? { __$$text: isStatelessSessionRule } : undefined,
            type: { __$$text: assetType },
          },
        },
      })),
    [setScesimModel]
  );

  const updateSettingsField = useCallback(
    (fieldName: string, value: string) =>
      setScesimModel((prevState) => ({
        ScenarioSimulationModel: {
          ...prevState.ScenarioSimulationModel,
          ["settings"]: {
            ...prevState.ScenarioSimulationModel["settings"],
            [fieldName]: { __$$text: value },
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
            return (
              <TestScenarioMainPanel
                fileName={scesimFile.path}
                scesimModel={scesimModel}
                updateTestScenarioModel={setScesimModel}
                updateSettingField={updateSettingsField}
              />
            );
        }
      })()}
    </>
  );
};

export const TestScenarioEditor = React.forwardRef(
  (props: TestScenarioEditorProps, ref: React.Ref<TestScenarioEditorRef>) => {
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
  }
);
