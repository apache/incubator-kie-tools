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

import {
  matchNodeWithLocation,
  findNodeAtOffset,
  parseYamlContent,
  parseJsonContent,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { treat } from "./testUtils";

describe("matchNodeWithLocation", () => {
  describe("JSON", () => {
    test("matching root node with empty content", () => {
      const { content, cursorOffset } = treat(`🎯{}`);
      const root = parseJsonContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    test("matching empty functions array", () => {
      const { content, cursorOffset } = treat(`
{
  "functions": [🎯]
}`);
      const root = parseJsonContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    test("matching functionRef", () => {
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
          "functionRef": 🎯
        }
      ]
    }
  ]
}`);
      const root = parseJsonContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef"])).toBeTruthy();
    });

    test("matching refName", () => {
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
      const root = parseJsonContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "refName"])
      ).toBeTruthy();
    });

    test("matching arguments", () => {
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
      const root = parseJsonContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "arguments"])
      ).toBeTruthy();
    });

    describe("matching functions array", () => {
      const { content, cursorOffset } = treat(`
{
  "functions": [🎯 {
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  }]
}`);
      const root = parseJsonContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      test("should not match functions with states", () => {
        expect(matchNodeWithLocation(root!, node!, ["states", "*"])).toBeFalsy();
        expect(matchNodeWithLocation(root!, node!, ["states"])).toBeFalsy();
      });

      test("with cursorOffset at the functions array", () => {
        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      });

      test("with cursorOffset at the first function", () => {
        const node = findNodeAtOffset(root!, cursorOffset + 1, true);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });
    });
  });

  describe("YAML", () => {
    test("matching root node with empty content", () => {
      const { content, cursorOffset } = treat(`---🎯 `);
      const root = parseYamlContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    test("matching empty function array", () => {
      const { content, cursorOffset } = treat(`

---
functions: [🎯]`);
      const root = parseYamlContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    test("matching refName", () => {
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
      const root = parseYamlContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "refName"])
      ).toBeTruthy();
    });

    test("matching arguments", () => {
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
        a🎯
  end: true
`);
      const root = parseYamlContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "arguments"])
      ).toBeTruthy();
    });

    test("matching empty operation property without same level content after", () => {
      const { content, cursorOffset } = treat(`---
functions:
- name: testRelativeFunction1
  operation:🎯`);
      const root = parseYamlContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeFalsy();

      expect(matchNodeWithLocation(root!, node!, ["functions", "*", "operation"])).toBeTruthy();
    });

    test("matching empty operation property with same level content after", () => {
      const { content, cursorOffset } = treat(`---
functions:
- name: testRelativeFunction1
  operation:🎯
  type: rest`);
      const root = parseYamlContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeFalsy();

      expect(matchNodeWithLocation(root!, node!, ["functions", "*", "operation"])).toBeTruthy();
    });

    describe("matching functions array", () => {
      const { content, cursorOffset } = treat(`---
functions:
- 🎯`);
      const root = parseYamlContent(content);
      const node = findNodeAtOffset(root!, cursorOffset, true);

      test("should not match functions with states", () => {
        expect(matchNodeWithLocation(root!, node!, ["states", "*"])).toBeFalsy();
        expect(matchNodeWithLocation(root!, node!, ["states"])).toBeFalsy();
      });

      test("with cursorOffset at the first function property against 'functions *' selector", () => {
        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
      });

      test.skip("with cursorOffset at the first function property against 'functions' selector", () => {
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });
    });
  });
});
