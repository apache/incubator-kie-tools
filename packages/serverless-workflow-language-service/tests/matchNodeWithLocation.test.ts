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
  nodeUpUntilType,
  SwfJsonLanguageService,
  SwfYamlLanguageService,
  SwfLsNode,
  matchNodeWithLocation,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { defaultConfig, defaultServiceCatalogConfig } from "./SwfLanguageServiceConfigs";
import { treat } from "./testUtils";

describe("matchNodeWithLocation", () => {
  describe("JSON", () => {
    test("matching root node with empty content", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`🎯{}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    test("matching empty functions array", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`
{
  "functions": [🎯]
}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    describe("matching functions array", () => {
      test("with cursorOffset at the functions array", () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`
{
  "functions": [🎯
  {
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  }]
}`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      });

      test("with cursorOffset at the first function", () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`
{
  "functions": [{🎯
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  }]
}`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });

      test("with cursorOffset at the second function", () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`
{
  "functions": [
  {
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  },
  {🎯
        "name": "function2",
        "operation": "openapi.yml#getGreeting"
  }]
}`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });
    });

    test("matching refName", () => {
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
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction",
          "functionRef": {
            "refName":"myFunc"
          }
        }
      ]
    },
    {
      "name": "testState2",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction1",
          "functionRef": {
            "refName":"myFunc"
          }
        },
        {
          "name": "testStateAction2",
          "functionRef": {
            "refName":"🎯"
          }
        }
      ]
    }
  ]
}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "refName"])
      ).toBeTruthy();
    });

    test("matching arguments", () => {
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
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction",
          "functionRef": {
            "refName":"myFunc"
          }
        }
      ]
    },
    {
      "name": "testState2",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction1",
          "functionRef": {
            "refName":"myFunc"
          }
        },
        {
          "name": "testStateAction2",
          "functionRef": {
            "refName":"myfunc",
            "arguments": {
              🎯
            }
          }
        }
      ]
    }
  ]
}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "arguments"])
      ).toBeTruthy();
    });
  });

  describe("YAML", () => {
    test("matching root node with empty content", () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`---🎯 `);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    test("matching empty function array", () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`

---
functions: [🎯]`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    describe("matching functions array", () => {
      test("with cursorOffset at the first function", () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`
---
functions:
- name: function1🎯
  operation: openapi.yml#getGreeting`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });

      test("with cursorOffset at the second function", () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`---
functions:
- name: function1
  operation: openapi.yml#getGreeting
- name: function2🎯
  operation: openapi.yml#getGreeting
`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });
    });

    test("matching refName", () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`
---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: "🎯"
`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "refName"])
      ).toBeTruthy();
    });

    test("matching arguments", () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState1
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: myFunc
- name: testState2
  type: operation
  transition: end
  actions:
  - name: testStateAction1
    functionRef:
      refName: myFunc
  - name: testStateAction2
    functionRef:
      refName: myfunc
      arguments: 
        🎯arg
  end: true
`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);
      const ff = findNodeAtOffset;

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "arguments"])
      ).toBeTruthy();
    });
  });
});
