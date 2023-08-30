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
  findNodesAtLocation,
  parseJsonContent,
  parseYamlContent,
} from "@kie-tools/json-yaml-language-service/dist/channel";

describe("findNodesAtLocation", () => {
  describe("JSON", () => {
    test("undefined JSON", () => {
      const nodesAtLocation = findNodesAtLocation({ root: undefined, path: ["functions", "*"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(0);
    });

    test("selecting a node not in JSON", () => {
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation"
    }
  ]
}`.trim();
      const root = parseJsonContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["functions", "*"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(0);
    });

    test("selecting a state name", () => {
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation"
    }
  ]
}`.trim();
      const root = parseJsonContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["states", "*", "name"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[0]).not.toBeUndefined();
      expect(nodesAtLocation[0].value).toBe("testState1");
    });

    test("selecting all the state names", () => {
      const content = `
{
  "states": [
    {
      "name": "testState1",
      "type": "operation"
    },
    {
      "name": "testState2",
      "type": "operation"
    }
  ]
}`.trim();
      const root = parseJsonContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["states", "*", "name"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[0]).not.toBeUndefined();
      expect(nodesAtLocation[0].value).toBe("testState1");
      expect(nodesAtLocation[1].value).toBe("testState2");
    });

    test("selecting all the functionRef using 2 *", () => {
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
            "refName": "testState1_functionRef1",
            "arguments": {}
          }
        },
        {
          "name": "testState1_action2",
          "functionRef": {
            "refName": "testState1_functionRef2",
            "arguments": {}
          }
        }
      ]
    },
    {
      "name": "testState2",
      "type": "operation",
      "actions": [
        {
          "name": "testState2_action1",
          "functionRef": {
            "refName": "testState2_functionRef1",
            "arguments": {}
          }
        }
      ]
    }
  ]
}`.trim();
      const root = parseJsonContent(content);
      const nodesAtLocation = findNodesAtLocation({
        root,
        path: ["states", "*", "actions", "*", "functionRef", "refName"],
      });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(3);
      expect(nodesAtLocation[0].value).toBe("testState1_functionRef1");
      expect(nodesAtLocation[1].value).toBe("testState1_functionRef2");
      expect(nodesAtLocation[2].value).toBe("testState2_functionRef1");
    });

    test("selecting empty value for functionRef", () => {
      const content = `
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
      "actions": [
        {
          "name": "testStateAction1",
          "functionRef": {
            "refName": "testState1_functionRef1"
            "arguments": {}
          }
        },
        {
          "name": "testStateAction2",
          "functionRef": 
        }
      ]
    }
  ]
}`.trim();
      const root = parseJsonContent(content);
      const nodesAtLocation = findNodesAtLocation({
        root,
        path: ["states", "*", "actions", "*", "functionRef"],
        includeUncompleteProps: true,
      });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[1]).not.toBeUndefined();
      expect(content.slice(nodesAtLocation[0].offset, nodesAtLocation[0].offset + nodesAtLocation[0].length)).toBe(`{
            "refName": "testState1_functionRef1"
            "arguments": {}
          }`);
      expect(content.slice(nodesAtLocation[1].offset, nodesAtLocation[1].offset + nodesAtLocation[1].length))
        .toBe(`"functionRef": 
        }`);
    });

    test("selecting the functions array", () => {
      const content = `{
  "functions": [{
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  },{
        "name": "function2",
        "operation": "openapi.yml#getGreeting"
  }]
}`.trim();
      const root = parseJsonContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["functions"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(1);
      expect(nodesAtLocation[0].type).toBe("array");
      expect(content.slice(nodesAtLocation[0].offset, nodesAtLocation[0].offset + nodesAtLocation[0].length)).toBe(`[{
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  },{
        "name": "function2",
        "operation": "openapi.yml#getGreeting"
  }]`);
    });

    test("selecting all the functions", () => {
      const content = `{
  "functions": [{
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  },{
        "name": "function2",
        "operation": "openapi.yml#getGreeting"
  }]
}`.trim();
      const root = parseJsonContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["functions", "*"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(2);
      expect(nodesAtLocation[0].type).toBe("object");
      expect(content.slice(nodesAtLocation[0].offset, nodesAtLocation[0].offset + nodesAtLocation[0].length)).toBe(`{
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  }`);
    });
  });

  describe("YAML", () => {
    test("undefined YAML", () => {
      const nodesAtLocation = findNodesAtLocation({ root: undefined, path: ["functions", "*"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(0);
    });

    test("selecting a node not in YAML", () => {
      const content = `---
states:
- name: testState1
  type: operation
`.trim();
      const root = parseYamlContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["functions", "*"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(0);
    });

    test("selecting empty value for functionRef", () => {
      const content = `---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState1
  type: operation
  actions:
  - name: testStateAction1
    functionRef: 
      a
  - name: testStateAction2
    functionRef:
      refName: testState1_functionRef1
      arguments:
        firstArg: test
`;
      const root = parseYamlContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["states", "*", "actions", "*", "functionRef"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation[0]).not.toBeUndefined();
      expect(content.slice(nodesAtLocation[0].offset, nodesAtLocation[0].offset + nodesAtLocation[0].length)).toBe("a");
      expect(content.slice(nodesAtLocation[1].offset, nodesAtLocation[1].offset + nodesAtLocation[1].length))
        .toBe(`refName: testState1_functionRef1
      arguments:
        firstArg: test`);
    });

    test("selecting the functions array", () => {
      const content = `---
      functions:
      - name: function1
        operation: openapi.yml#getGreeting
      - name: function2
        operation: openapi.yml#getGreeting
      `.trim();
      const root = parseYamlContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["functions"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(1);
      expect(nodesAtLocation[0].type).toBe("array");
      expect(content.slice(nodesAtLocation[0].offset, nodesAtLocation[0].offset + nodesAtLocation[0].length))
        .toBe(`- name: function1
        operation: openapi.yml#getGreeting
      - name: function2
        operation: openapi.yml#getGreeting`);
    });

    test("selecting all the functions", () => {
      const content = `---
      functions:
      - name: function1
        operation: openapi.yml#getGreeting
      - name: function2
        operation: openapi.yml#getGreeting
      `.trim();
      const root = parseYamlContent(content);
      const nodesAtLocation = findNodesAtLocation({ root, path: ["functions", "*"] });

      expect(nodesAtLocation).not.toBeUndefined();
      expect(nodesAtLocation.length).toBe(2);
      expect(nodesAtLocation[0].type).toBe("object");
      expect(content.slice(nodesAtLocation[0].offset, nodesAtLocation[0].offset + nodesAtLocation[0].length))
        .toBe(`name: function1
        operation: openapi.yml#getGreeting`);
    });
  });
});
