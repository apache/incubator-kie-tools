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

import { findPositionByStateName, FileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/editor";

const mockJson = `{
  "id":"helloworld",
  "version":"1.0",
  "specVersion":"0.8",
  "name":"Hello World Workflow",
  "start":"Hello State",
  "states": [
    {
      "name": "Hello State",
      "type": "inject",
      "data": {
        "result": "Hello World!"
      },
      "end": true
    },
    {
      "name": "State with \"quotes\" inside",
      "type": "inject",
      "data": {
        "result": "Hello World!"
      },
      "end": true
    }
  ]
}`;

const mockYaml = `
  ---
  id: helloworld
  version: '1.0'
  specVersion: '0.8'
  name: Hello World Workflow
  start: Hello State
  states:
  - name: Hello State
    type: inject
    data:
      result: Hello World!
    end: true
  - name: State with "quotes" inside
    type: inject
    data:
      result: Hello World!
    end: true
`;

describe("findPositionByStateName", () => {
  it("should return null with wrong inputs", () => {
    // @ts-ignore
    expect(findPositionByStateName(null, null)).toBeNull();
    // @ts-ignore
    expect(findPositionByStateName()).toBeNull();
    expect(findPositionByStateName("", "")).toBeNull();
    expect(findPositionByStateName("", "test")).toBeNull();
    expect(findPositionByStateName("{ fakeJson: true }", "")).toBeNull();
  });

  it.each([
    ["Hello State", { line: 9, character: 7 }],
    ['State with "quotes" inside', { line: 17, character: 7 }],
    ["Not present", null],
  ])('using the mock JSON, searching: "%s"', (stateName, position) => {
    expect(findPositionByStateName(mockJson, stateName, FileLanguage.JSON)).toStrictEqual(position);
  });

  it.each([
    ["Hello State", { line: 9, character: 4 }],
    ['State with "quotes" inside', { line: 14, character: 4 }],
    ["Not present", null],
  ])('using the mock YAML, searching: "%s"', (stateName, position) => {
    expect(findPositionByStateName(mockYaml, stateName, FileLanguage.YAML)).toStrictEqual(position);
  });
});
