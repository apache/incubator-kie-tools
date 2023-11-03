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

import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import TestScenarioDrawerDataObjectsPanel from "./TestScenarioDrawerDataObjectsPanel";
import TestScenarioDrawerCheatSheetPanel from "./TestScenarioDrawerCheatSheetPanel";
import TestScenarioDrawerSettingsPanel from "../drawer/TestScenarioDrawerSettingsPanel";
import {
  TestScenarioDataObject,
  TestScenarioEditorDock,
  TestScenarioSettings,
  TestScenarioType,
} from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

function TestScenarioDrawerPanel({
  dataObjects,
  fileName,
  onDrawerClose,
  onUpdateSettingField,
  selectedDock,
  testScenarioSettings,
}: {
  dataObjects: TestScenarioDataObject[];
  fileName: string;
  onDrawerClose: () => void;
  onUpdateSettingField: (field: string, value: boolean | string) => void;
  selectedDock: TestScenarioEditorDock;
  testScenarioSettings: TestScenarioSettings;
}) {
  const { i18n } = useTestScenarioEditorI18n();

  return (
    <DrawerPanelContent isResizable={true} minSize={"400px"} defaultSize={"500px"}>
      <DrawerHead>
        <DrawerActions>
          <DrawerCloseButton onClose={onDrawerClose} />
        </DrawerActions>
        <TextContent>
          <Text component={TextVariants.h2}>
            {(() => {
              switch (selectedDock) {
                case TestScenarioEditorDock.CHEATSHEET:
                  return i18n.drawer.cheatSheet.title;
                case TestScenarioEditorDock.DATA_OBJECT:
                  return testScenarioSettings.assetType === TestScenarioType[TestScenarioType.DMN]
                    ? i18n.drawer.dataObjects.titleDMN
                    : i18n.drawer.dataObjects.titleRule;
                case TestScenarioEditorDock.SETTINGS:
                  return i18n.drawer.settings.title;
                default:
                  throw new Error("Wrong state, an invalid dock has been selected " + selectedDock);
              }
            })()}
          </Text>
        </TextContent>
        <Divider />
      </DrawerHead>
      <DrawerPanelBody>
        {(() => {
          switch (selectedDock) {
            case TestScenarioEditorDock.CHEATSHEET:
              return <TestScenarioDrawerCheatSheetPanel assetType={testScenarioSettings.assetType} />;
            case TestScenarioEditorDock.DATA_OBJECT:
              return (
                // <TestScenarioDrawerDataObjectsPanel
                //   assetType={testScenarioSettings.assetType}
                //   dataObjects={dataObjects}
                // />
                <div></div>
              );
            case TestScenarioEditorDock.SETTINGS:
              return (
                <TestScenarioDrawerSettingsPanel
                  fileName={fileName}
                  onUpdateSettingField={onUpdateSettingField}
                  testScenarioSettings={testScenarioSettings}
                />
              );
            default:
              throw new Error("Wrong state, an invalid dock has been selected " + selectedDock);
          }
        })()}
      </DrawerPanelBody>
    </DrawerPanelContent>
  );
}

export default TestScenarioDrawerPanel;
