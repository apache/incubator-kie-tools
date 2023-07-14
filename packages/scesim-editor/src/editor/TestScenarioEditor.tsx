/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "@patternfly/react-core/dist/styles/base.css";

import * as React from "react";
import { useCallback, useImperativeHandle, useState } from "react";

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import CogIcon from "@patternfly/react-icons/dist/esm/icons/cog-icon";
import EditIcon from "@patternfly/react-icons/dist/esm/icons/edit-alt-icon";
import InfoIcon from "@patternfly/react-icons/dist/esm/icons/info-icon";
import TableIcon from "@patternfly/react-icons/dist/esm/icons/table-icon";

import { TestToolsPanel } from "../panels/TestToolsPanel";

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
};

export const TestScenarioEditor = React.forwardRef((props: { xml: string }, ref: React.Ref<TestScenarioEditorRef>) => {
  useImperativeHandle(
    ref,
    () => ({
      getContent: () => "",
    }),
    []
  );

  const [tab, setTab] = useState(TestScenarioEditorTab.EDITOR);
  const onTabChanged = useCallback((e, tab) => {
    setTab(tab);
  }, []);

  const [dockPanel, setDockPanel] = useState({ isOpen: true, selected: TestScenarioEditorDock.DATA_OBJECT });

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
      <div className="kie-scesim-editor--right-sidebar">
        <Tooltip content={<div>Data Objects tool: It provides a tool to add your Data Objects in Test Scenarios</div>}>
          <Button
            variant="plain"
            onClick={() => setDockPanel({ isOpen: true, selected: TestScenarioEditorDock.DATA_OBJECT })}
            icon={<EditIcon />}
          />
        </Tooltip>
        <Tooltip content={<div>Settings</div>}>
          <Button
            variant="plain"
            onClick={() => setDockPanel({ isOpen: true, selected: TestScenarioEditorDock.SETTINGS })}
            icon={<CogIcon />}
          />
        </Tooltip>
        <Tooltip
          content={<div>CheatSheet: In this panel you can found useful information for Test Scenario Usage</div>}
        >
          <Button
            variant="plain"
            onClick={() => setDockPanel({ isOpen: true, selected: TestScenarioEditorDock.CHEATSHEET })}
            icon={<InfoIcon />}
          />
        </Tooltip>
      </div>
    </>
  );
});
