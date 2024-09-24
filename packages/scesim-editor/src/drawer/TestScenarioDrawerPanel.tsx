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

import { SceSimModel } from "@kie-tools/scesim-marshaller";

import TestScenarioDrawerDataSelectorPanel from "./TestScenarioDrawerDataSelectorPanel";
import TestScenarioDrawerCheatSheetPanel from "./TestScenarioDrawerCheatSheetPanel";
import TestScenarioDrawerSettingsPanel from "../drawer/TestScenarioDrawerSettingsPanel";
import {
  TestScenarioDataObject,
  TestScenarioEditorDock,
  TestScenarioSelectedColumnMetaData,
  TestScenarioType,
} from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";
import { useTestScenarioEditorStore } from "../store/TestScenarioStoreContext";

function TestScenarioDrawerPanel({
  dataObjects,
  fileName,
  onDrawerClose,
  scesimModel,
  selectedColumnMetaData,
  selectedDock,
  updateSelectedColumnMetaData,
  updateTestScenarioModel,
}: {
  dataObjects: TestScenarioDataObject[];
  fileName: string;
  onDrawerClose: () => void;
  scesimModel: SceSimModel;
  selectedColumnMetaData: TestScenarioSelectedColumnMetaData | null;
  selectedDock: TestScenarioEditorDock;
  updateSelectedColumnMetaData: React.Dispatch<React.SetStateAction<TestScenarioSelectedColumnMetaData | null>>;
  updateTestScenarioModel?: React.Dispatch<React.SetStateAction<SceSimModel>>;
}) {
  const { i18n } = useTestScenarioEditorI18n();
  const scesim = useTestScenarioEditorStore((state) => state.scesim);
  const assetType = scesim.model.ScenarioSimulationModel.settings.type!.__$$text;

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
                  return assetType === TestScenarioType[TestScenarioType.DMN]
                    ? i18n.drawer.dataSelector.titleDMN
                    : i18n.drawer.dataSelector.titleRule;
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
              return <TestScenarioDrawerCheatSheetPanel assetType={assetType} />;
            case TestScenarioEditorDock.DATA_OBJECT:
              return (
                <TestScenarioDrawerDataSelectorPanel
                  assetType={assetType}
                  dataObjects={dataObjects}
                  scesimModel={scesimModel}
                  selectedColumnMetadata={selectedColumnMetaData}
                  updateSelectedColumnMetaData={updateSelectedColumnMetaData}
                  updateTestScenarioModel={updateTestScenarioModel}
                />
              );
            case TestScenarioEditorDock.SETTINGS:
              return <TestScenarioDrawerSettingsPanel fileName={fileName} />;
            default:
              throw new Error("Wrong state, an invalid dock has been selected " + selectedDock);
          }
        })()}
      </DrawerPanelBody>
    </DrawerPanelContent>
  );
}

export default TestScenarioDrawerPanel;
