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
  doRefValidation,
  parseJsonContent,
  parseYamlContent,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { testRefValidationMap } from "./testLanguageService/testRefValidationMap";
import { trim } from "./testUtils";

function textDoc(content: string) {
  return TextDocument.create("", "serverless-workflow-json", 0, content);
}

describe("test JSON refValidation method against source and target paths", () => {
  test("check functionRef against functions array", () => {
    const { content } = trim(`
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
      "name": "testState",
      "type": "operation",
      "actions": [
        {
          "name": "testStateAction",
          "functionRef": {
            "refName":"testFunction"
          }
        }
      ]
    },
  ]
}
            `);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: testRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'testFunction' in 'functions'",
        range: { end: { character: 36, line: 16 }, start: { character: 22, line: 16 } },
      },
    ]);
  });

  test("check eventRef against events array", () => {
    const { content } = trim(`
{
  "events": [
    {
      "name": "HighBodyTemperature",
      "type": "org.application.info",
      "source": "applicationssource"
      }
  ],
  "onEvents": [{
     "eventRefs": ["HighBloodPressure"]
  }],
}
          `);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: testRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'HighBloodPressure' in 'events'",
        range: { end: { character: 38, line: 9 }, start: { character: 19, line: 9 } },
      },
    ]);
  });
});

describe("test YAML refValidation method against source and target paths", () => {
  test("check functionRef against functions array", () => {
    const { content } = trim(`
---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  actions:
    - name: testStateAction
      functionRef:
        refName: "testFunc"
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: testRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'testFunc' in 'functions'",
        range: { end: { character: 27, line: 11 }, start: { character: 17, line: 11 } },
      },
    ]);
  });

  test("check eventRef against events array", () => {
    const { content } = trim(`
---
events:
- name: HighBodyTemperature
  type: org.application.info
  source: applicationssource
onEvents:
- eventRefs:
  - HighBloodPressure
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: testRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'HighBloodPressure' in 'events'",
        range: { end: { character: 21, line: 7 }, start: { character: 4, line: 7 } },
      },
    ]);
  });
});
