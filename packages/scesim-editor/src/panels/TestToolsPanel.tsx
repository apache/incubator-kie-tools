/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";

import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import { TestScenarioEditorDock } from "../TestScenarioEditor";

export function TestToolsPanel({
  selectedDock,
  onClose,
}: {
  selectedDock: TestScenarioEditorDock;
  onClose: () => void;
}) {
  return (
    <DrawerPanelContent isResizable={true} minSize={"400px"} defaultSize={"500px"}>
      <DrawerHead>
        <DrawerActions>
          <DrawerCloseButton onClose={onClose} />
        </DrawerActions>
        <TextContent>
          <Text component={TextVariants.h4}>
            {(() => {
              switch (selectedDock) {
                case TestScenarioEditorDock.CHEATSHEET:
                  return <>Scenario Cheatsheet</>;
                case TestScenarioEditorDock.DATA_OBJECT:
                  return <>Data Objects tool</>;
                case TestScenarioEditorDock.SETTINGS:
                  return <>Settings</>;
                default:
                  throw new Error("");
              }
            })()}
          </Text>
        </TextContent>
        <Divider />
      </DrawerHead>
      <DrawerPanelBody>
        {/* The following is a temporary text content. Specific panel for all Docks will be managed */}
        <TextContent>
          <Text component={TextVariants.small}>
            {(() => {
              switch (selectedDock) {
                case TestScenarioEditorDock.CHEATSHEET:
                  return <>Scenario Cheatsheet</>;
                case TestScenarioEditorDock.DATA_OBJECT:
                  return (
                    <>
                      {
                        'To create a test scenario, define the "Given" and "Expect" columns by using the expression editor below.'
                      }
                    </>
                  );
                case TestScenarioEditorDock.SETTINGS:
                  return <>Settings</>;
                default:
                  throw new Error("");
              }
            })()}
          </Text>
        </TextContent>
      </DrawerPanelBody>
    </DrawerPanelContent>
  );
}
