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

import { doRefValidation } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { trim } from "./testUtils";
import * as jsonc from "jsonc-parser";

function textDoc(content: string) {
  return TextDocument.create("", "serverless-workflow-json", 0, content);
}

describe("test custom validation method against source and target paths", () => {
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
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
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
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
      {
        message: "Missing 'HighBloodPressure' in 'events'",
        range: { end: { character: 38, line: 9 }, start: { character: 19, line: 9 } },
      },
    ]);
  });

  test("check resultEventRef against events array", () => {
    const { content } = trim(`
{
  "events": [
    {
      "name": "MakeVetAppointment",
      "type": "org.application.info",
      "source": "applicationssource"
      }
  ],
  "onEvents": [{
    "actions": [{
      "eventRef": {
        "triggerEventRef": "MakeVetAppointment",
        "data": "test",
        "resultEventRef": "VetAppointmentInfo"
      }
    }]
  }],
}
        `);
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
      {
        message: "Missing 'VetAppointmentInfo' in 'events'",
        range: { end: { character: 46, line: 13 }, start: { character: 26, line: 13 } },
      },
    ]);
  });

  test("check triggerEventRef against events array", () => {
    const { content } = trim(`
{
  "events": [
    {
      "name": "VetAppointmentInfo",
      "type": "org.application.info",
      "source": "applicationssource"
      }
  ],
  "onEvents": [{
    "actions": [{
      "eventRef": {
        "triggerEventRef": "MakeVetAppointment",
        "data": "test",
        "resultEventRef": "VetAppointmentInfo"
      }
    }]
  }],
}
        `);
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
      {
        message: "Missing 'MakeVetAppointment' in 'events'",
        range: { end: { character: 47, line: 11 }, start: { character: 27, line: 11 } },
      },
    ]);
  });

  test("check retryRef against retries array", () => {
    const { content } = trim(`
{
  "functions": [
    {
      "name": "MyFirstFunction",
      "operation": "./specs/myService#MyFirstFunction",
      "type": "rest"
    },
    {
      "name": "MySecondFunction",
      "operation": "./specs/myService#MySecondFunction",
      "type": "rest"
    }
  ],
  "errors": [
    {
      "name": "SomeErrorOne",
      "code": "404",
      "description": "Server has not found anything matching the provided service endpoint information"
    }
  ],
  "retries": [
    {
      "name": "FirstRetryStrategy",
      "delay": "PT1M",
      "maxAttempts": 5
    }
  ],
  "states": [{
    "actions": [{
      "functionRef": "MyFirstFunction",
      "retryRef": "FirstRetryStrategy",
      "retryableErrors": ["SomeErrorOne"]
    },
    {
      "functionRef": "MySecondFunction",
      "retryRef": "SecondRetryStrategy",
      "retryableErrors": ["SomeErrorOne"]
    }]
  }],
}
        `);
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
      {
        message: "Missing 'SecondRetryStrategy' in 'retries'",
        range: { end: { character: 39, line: 35 }, start: { character: 18, line: 35 } },
      },
    ]);
  });

  test("check retryableErrors against errors array", () => {
    const { content } = trim(`
{
  "functions": [
    {
      "name": "MyFirstFunction",
      "operation": "./specs/myService#MyFirstFunction",
      "type": "rest"
    },
    {
      "name": "MySecondFunction",
      "operation": "./specs/myService#MySecondFunction",
      "type": "rest"
    }
  ],
  "errors": [
    {
      "name": "SomeErrorOne",
      "code": "404",
      "description": "Server has not found anything matching the provided service endpoint information"
    }
  ],
  "retries": [
    {
      "name": "FirstRetryStrategy",
      "delay": "PT1M",
      "maxAttempts": 5
    }
  ],
  "states": [{
    "actions": [{
      "functionRef": "MyFirstFunction",
      "retryRef": "FirstRetryStrategy",
      "retryableErrors": ["SomeErrorOne", "SomeErrorTwo"]
    }]
  }],
}
        `);
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
      {
        message: "Missing 'SomeErrorTwo' in 'errors'",
        range: { end: { character: 56, line: 31 }, start: { character: 42, line: 31 } },
      },
    ]);
  });

  test("check transition against states array", () => {
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
  ]
}
      `);
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
      {
        message: "Missing 'end' in 'states'",
        range: { end: { character: 25, line: 12 }, start: { character: 20, line: 12 } },
      },
    ]);
  });

  test("check invalid functions declaration against states array", () => {
    const { content } = trim(`
{
  "functions": [
    [
      { "foo": "bar" }
    ]
  ],
  "states": [
    {
      "name": "testState",
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
  ]
}
      `);
    expect(doRefValidation({ textDocument: textDoc(content), rootNode: jsonc.parseTree(content)! })).toEqual([
      {
        message: "Missing 'myFunc' in 'functions'",
        range: { end: { character: 30, line: 15 }, start: { character: 22, line: 15 } },
      },
      {
        message: "Missing 'end' in 'states'",
        range: { end: { character: 25, line: 10 }, start: { character: 20, line: 10 } },
      },
    ]);
  });

  // TODO: Add tests for YAML as well.
});
