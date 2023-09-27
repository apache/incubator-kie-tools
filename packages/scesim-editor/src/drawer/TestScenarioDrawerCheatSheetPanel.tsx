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

import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import { TestScenarioType } from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

function TestScenarioDrawerCheatSheetPanel({ assetType }: { assetType: string }) {
  const { i18n } = useTestScenarioEditorI18n();

  return (
    <TextContent>
      <Text component={TextVariants.p}>{i18n.drawer.cheatSheet.paragraph1}</Text>
      <Text>
        {i18n.drawer.cheatSheet.paragraph2a}
        <strong>{i18n.drawer.dataObject.title}</strong>
        {i18n.drawer.cheatSheet.paragraph2b}
      </Text>
      <Text>
        {i18n.drawer.cheatSheet.paragraph3a}
        <strong>{i18n.tab.backgroundTabTitle}</strong>
        {i18n.drawer.cheatSheet.paragraph3b}
        <strong>{i18n.tab.scenarioTabTitle}</strong>
        {i18n.drawer.cheatSheet.paragraph3c}
      </Text>
      <Text>
        If the same GIVEN data is shared with the multiple test scenarios, you can use the Background tab to define them
        only once. The way to create a column in the Background tab is the same as of the Model tab.
      </Text>
      <Text>Now, define the test scenarios. Each cell of the table must contain a valid expression.</Text>
      <Text>
        Values in the GIVEN part of the table are evaluated as literal expressions, whereas values in the EXPECT section
        are evaluated as unary expressions. The correctness of these unary tests is verified using the results from a
        DMN evaluation.
      </Text>
      <Text>DMN-based scenarios use the FEEL expression language. Supported data types are:</Text>
    </TextContent>
  );
}

export default TestScenarioDrawerCheatSheetPanel;
