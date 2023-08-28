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

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import CogIcon from "@patternfly/react-icons/dist/esm/icons/cog-icon";
import EditIcon from "@patternfly/react-icons/dist/esm/icons/edit-alt-icon";
import ErrorIcon from "@patternfly/react-icons/dist/esm/icons/error-circle-o-icon";
import InfoIcon from "@patternfly/react-icons/dist/esm/icons/info-icon";
import TableIcon from "@patternfly/react-icons/dist/esm/icons/table-icon";

import TestScenarioCreationPanel from "./panels/TestScenarioCreationPanel";
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

enum TestScenarioFileStatus {
  EMPTY,
  ERROR,
  NEW,
  UNSUPPORTED,
  VALID,
}

export type TestScenarioEditorRef = {
  /** TODO Convert these to Promises */
  getContent(): string;
  setContent(path: string, content: string): void;
};

function TestScenarioDocksPanel({
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

function TestScenarioParserErrorPanel({
  scesimFileStatus,
  scesimFileVersion,
}: {
  scesimFileStatus: TestScenarioFileStatus;
  scesimFileVersion: string;
}) {
  return (
    <EmptyState>
      <EmptyStateIcon icon={ErrorIcon} />

      <Title headingLevel="h4" size="lg">
        {scesimFileStatus === TestScenarioFileStatus.UNSUPPORTED
          ? "This file holds a Test Scenario asset version (" + scesimFileVersion + ") not supported"
          : "Generic parser error"}
      </Title>
      <EmptyStateBody>
        {scesimFileStatus === TestScenarioFileStatus.UNSUPPORTED
          ? "Most likely, this file has been generated with a very old Business Central version."
          : "Impossibile to correctly parse the provided scesim file."}
      </EmptyStateBody>
    </EmptyState>
  );
}

export const TestScenarioEditor = React.forwardRef((props: {}, ref: React.Ref<TestScenarioEditorRef>) => {
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
      /* NOT ACCESSIBLE
      if (scesim?.ScenarioSimulationModel["parsererror"]) {
        return TestScenarioFileStatus.ERROR;
      } */
      if (scesimModel.ScenarioSimulationModel["@_version"] != "1.8") {
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
    ref,
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
      {(() => {
        switch (scesimFileStatus) {
          case TestScenarioFileStatus.EMPTY:
            return (
              <Bullseye>
                <Spinner aria-label="SCESIM Data loading .." />
              </Bullseye>
            );
          case TestScenarioFileStatus.NEW:
            return <TestScenarioCreationPanel onCreateScesimButtonClicked={setInitialSettings} />;
          case TestScenarioFileStatus.UNSUPPORTED:
            return (
              <TestScenarioParserErrorPanel
                scesimFileStatus={scesimFileStatus}
                scesimFileVersion={scesimModel.ScenarioSimulationModel["@_version"]!}
              />
            );
          case TestScenarioFileStatus.VALID:
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
            );
        }
      })()}
    </>
  );
});
