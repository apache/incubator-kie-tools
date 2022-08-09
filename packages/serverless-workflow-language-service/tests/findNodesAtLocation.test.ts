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

describe("findNodesAtLocation", () => {
  describe("JSON", () => {
    test("undefined JSON", () => {
      const nodesAtLocation = findNodesAtLocation(undefined, ["functions", "*"]);

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(0);
    });

    test("selecting a node not in JSON", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "transition": "end"
    }
  ]
}`.trim();
      const root = ls.parseContent(content);
      const nodesAtLocation = findNodesAtLocation(root, ["functions", "*"]);

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(0);
    });

    test("selecting a state name", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "transition": "end"
    }
  ]
}`.trim();
      const root = ls.parseContent(content);
      const nodesAtLocation = findNodesAtLocation(root, ["states", "*", "name"]);

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[0]).not.toBeUndefined();
      expect(nodesAtLocation[0].value).toBe("testState1");
    });

    test("selecting the 2nd state name", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "transition": "end"
    },
    {
      "name": "testState2",
      "type": "operation",
      "transition": "end"
    }
  ]
}`.trim();
      const root = ls.parseContent(content);
      const nodesAtLocation = findNodesAtLocation(root, ["states", 1, "name"]);

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[0]).not.toBeUndefined();
      expect(nodesAtLocation[0].value).toBe("testState2");
    });

    test("selecting all the state names", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "transition": "end"
    },
    {
      "name": "testState2",
      "type": "operation",
      "transition": "end"
    }
  ]
}`.trim();
      const root = ls.parseContent(content);
      const nodesAtLocation = findNodesAtLocation(root, ["states", "*", "name"]);

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[0]).not.toBeUndefined();
      expect(nodesAtLocation[0].value).toBe("testState1");
      expect(nodesAtLocation[1].value).toBe("testState2");
    });

    test("selecting all the functionRef using 2 *", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "actions": [
        {
          "name": "testState1_action1",
          "functionRef": {
            "refName": "testState1_functionRef1"
            "arguments": {}
          }
        },
        {
          "name": "testState1_action2",
          "functionRef": {
            "refName": "testState1_functionRef2"
            "arguments": {}
          }
        },
      ],
    },
    {
      "name": "testState2",
      "type": "operation",
      "actions": [
        {
          "name": "testState2_action1",
          "functionRef": {
            "refName": "testState2_functionRef1"
            "arguments": {}
          }
        }
    }
  ]
}`.trim();
      const root = ls.parseContent(content);
      const nodesAtLocation = findNodesAtLocation(root, ["states", "*", "actions", "*", "functionRef", "refName"]);

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(3);
      expect(nodesAtLocation[0].value).toBe("testState1_functionRef1");
      expect(nodesAtLocation[1].value).toBe("testState1_functionRef2");
      expect(nodesAtLocation[2].value).toBe("testState2_functionRef1");
    });

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

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[0]).not.toBeUndefined();
      expect(nodesAtLocation[0].offset).toBe(nodeAtOffset?.offset);
      expect(nodesAtLocation[0].length).toBe(nodeAtOffset?.length);
    });
  });
});
