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

import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip/Tooltip";
import { TreeView } from "@patternfly/react-core/dist/js/components/TreeView/TreeView";

import { Icon } from "@patternfly/react-core/dist/esm/components/Icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/esm/icons/info-circle-icon";

import { TestScenarioDataObject, TestScenarioType } from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

import "./TestScenarioDrawerDataObjectsPanel.css";

function TestScenarioDataObjectsPanel({
  assetType,
  dataObjects,
}: {
  assetType: string;
  dataObjects: TestScenarioDataObject[];
}) {
  const { i18n } = useTestScenarioEditorI18n();

  return (
    <>
      <Text>
        {i18n.drawer.dataObjects.description}
        <Tooltip
          content={
            assetType === TestScenarioType[TestScenarioType.DMN]
              ? i18n.drawer.dataObjects.dataObjectsDescriptionDMN
              : i18n.drawer.dataObjects.dataObjectsDescriptionRule
          }
        >
          <Icon className={"kie-scesim-editor-drawer-data-objects--info-icon"} size="sm" status="info">
            <InfoCircleIcon />
          </Icon>
        </Tooltip>
      </Text>
      <Title className={"kie-scesim-editor-drawer-data-objects--selector-title"} headingLevel={"h6"}>
        {i18n.drawer.dataObjects.selectorTitle}
      </Title>
      {dataObjects.length > 0 ? (
        <TreeView className={"kie-scesim-editor-drawer-data-objects--selector"} data={dataObjects} hasBadges />
      ) : (
        <Text>OOOpps</Text>
      )}
    </>
  );
}

export default TestScenarioDataObjectsPanel;
