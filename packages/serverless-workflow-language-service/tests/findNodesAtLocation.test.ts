/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import {
  findNodeAtOffset,
  findNodesAtLocation,
  SwfJsonLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { defaultConfig, defaultServiceCatalogConfig } from "./SwfLanguageServiceConfigs";
import { treat } from "./testUtils";

describe.skip("findNodesAtLocation", () => {
  test("selecting empty value", () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });
    const { content, cursorOffset } = treat(`
{
  "functions": [
    {
      "name": "myFunc",
      "operation": "./specs/myService#myFunc",
      "type": "rest"
    }
  ],
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "transition": "end"
    },
    {
      "name": "testState2",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction2",
          "functionRef": 🎯
        }
      ]
    }
  ]
}`);
    const root = ls.parseContent(content);
    const nodeAtOffset = findNodeAtOffset(root!, cursorOffset);
    const nodesAtLocation = findNodesAtLocation(root, ["states", "*", "actions", "*", "functionRef"]);
    debugger;

    expect(nodesAtLocation[0]).not.toBeUndefined();
    expect(nodesAtLocation[0].offset).toBe(nodeAtOffset?.offset);
    expect(nodesAtLocation).toBe(nodeAtOffset);
  });
});
