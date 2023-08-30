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

import { Checkbox } from "@patternfly/react-core/dist/esm/components/Checkbox";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/esm/components/FormSelect";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

import { TestScenarioEditorDock } from "../editor/TestScenarioEditor";

export function SettingPanel() {
  return (
    <>
      <Title headingLevel={"h6"}>Name</Title>
      <TextInput value={"test.scesim"} type="text" isDisabled />
      <Title headingLevel={"h6"}>Type</Title>
      <TextInput value={"DMN"} type="text" isDisabled />
      <Title headingLevel={"h6"}>DMN Model</Title>
      <FormSelect value={"1"} aria-label="FormSelect Input" ouiaId="BasicFormSelect">
        <FormSelectOption isDisabled={true} key={0} value={"1"} label={"MockedDMN.dmn"} />
        <FormSelectOption isDisabled={true} key={1} value={"2"} label={"MockedDMN2.dmn"} />
      </FormSelect>
      <Title headingLevel={"h6"}>DMN Namespace</Title>
      <TextInput value={"Not available"} type="text" isDisabled />
      <Title headingLevel={"h6"}>DMN Name</Title>
      <TextInput value={"MockedDMN"} type="text" isDisabled />
      <Checkbox id="skip-test" label="Skip this test scenario during the test" />
    </>
  );
}

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
          <Text component={TextVariants.h2}>
            {(() => {
              switch (selectedDock) {
                case TestScenarioEditorDock.CHEATSHEET:
                  return "Scenario Cheatsheet";
                case TestScenarioEditorDock.DATA_OBJECT:
                  return "Data Objects tool";
                case TestScenarioEditorDock.SETTINGS:
                  return "Settings";
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
          <Text>
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
                  return <SettingPanel />;
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
