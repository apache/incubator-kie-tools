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
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import TableIcon from "@patternfly/react-icons/dist/esm/icons/table-icon";

import "./TestScenarioEditor.css";
import { TestToolsPanel } from "../panels/TestToolsPanel";

export enum TestScenarioEditorTab {
  EDITOR,
  BACKGROUND,
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

  return (
    <>
      <Tabs isFilled={true} activeKey={tab} onSelect={onTabChanged} role="region" className={"kie-scesim-editor--tabs"}>
        <Tab
          eventKey={TestScenarioEditorTab.EDITOR}
          title={
            <>
              <TabTitleIcon>
                <TableIcon />
              </TabTitleIcon>
              <TabTitleText>Scenarios</TabTitleText>
            </>
          }
        >
          {tab === TestScenarioEditorTab.EDITOR && (
            <Drawer isExpanded={true} isInline={true} position={"right"}>
              <DrawerContent panelContent={<TestToolsPanel />}>
                <DrawerContentBody>
                  <div className={"kie-scesim-editor--diagram-container"}>Scenario Grid</div>
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
    </>
  );
});
