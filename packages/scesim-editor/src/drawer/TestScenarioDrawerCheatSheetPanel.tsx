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

import { Text, TextContent, TextList, TextListItem } from "@patternfly/react-core/dist/js/components/Text";

import { TestScenarioType } from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

function TestScenarioDrawerCheatSheetPanel({ assetType }: { assetType: string }) {
  const { i18n } = useTestScenarioEditorI18n();

  return (
    <TextContent>
      <Text>{i18n.drawer.cheatSheet.paragraph1}</Text>
      <Text>
        {i18n.drawer.cheatSheet.paragraph2(
          assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.dataSelector.titleDMN
            : i18n.drawer.dataSelector.titleRule
        )}
      </Text>
      <Text>{i18n.drawer.cheatSheet.paragraph3(i18n.tab.backgroundTabTitle, i18n.tab.scenarioTabTitle)}</Text>
      <Text>{i18n.drawer.cheatSheet.paragraph4}</Text>
      {assetType === TestScenarioType[TestScenarioType.DMN] && <Text>{i18n.drawer.cheatSheet.paragraph5DMN}</Text>}
      <Text>
        {assetType === TestScenarioType[TestScenarioType.DMN]
          ? i18n.drawer.cheatSheet.paragraph6DMN
          : i18n.drawer.cheatSheet.paragraph6Rule}
      </Text>
      <TextList>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression1DMN
            : i18n.drawer.cheatSheet.expression1Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression2DMN
            : i18n.drawer.cheatSheet.expression2Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression3DMN
            : i18n.drawer.cheatSheet.expression3Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression4DMN
            : i18n.drawer.cheatSheet.expression4Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression5DMN
            : i18n.drawer.cheatSheet.expression5Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression6DMN
            : i18n.drawer.cheatSheet.expression6Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression7DMN
            : i18n.drawer.cheatSheet.expression7Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression8DMN
            : i18n.drawer.cheatSheet.expression8Rule}
        </TextListItem>
        <TextListItem>
          {assetType === TestScenarioType[TestScenarioType.DMN]
            ? i18n.drawer.cheatSheet.expression9DMN
            : i18n.drawer.cheatSheet.expression9Rule}
        </TextListItem>
        {assetType === TestScenarioType[TestScenarioType.DMN] && (
          <TextListItem>{i18n.drawer.cheatSheet.expression10DMN}</TextListItem>
        )}
      </TextList>
    </TextContent>
  );
}

export default TestScenarioDrawerCheatSheetPanel;
