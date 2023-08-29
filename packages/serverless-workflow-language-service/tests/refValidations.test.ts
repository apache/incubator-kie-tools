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
import { swfRefValidationMap } from "../dist/channel/swfRefValidationMap";
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
        validationMap: swfRefValidationMap,
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
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
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
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
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
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
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
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
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
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
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
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
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
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
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

  test("check invalid errors declaration against states array", () => {
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
    [
      {
        "name": "SomeErrorOne",
        "code": "404",
        "description": "Server has not found anything matching the provided service endpoint information"
      }
    ]
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
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'SomeErrorOne' in 'errors'",
        range: { end: { character: 40, line: 33 }, start: { character: 26, line: 33 } },
      },
      {
        message: "Missing 'SomeErrorTwo' in 'errors'",
        range: { end: { character: 56, line: 33 }, start: { character: 42, line: 33 } },
      },
    ]);
  });

  test("check invalid retryableErrors against errors array", () => {
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
  "retries": [
    [
      "name": "FirstRetryStrategy",
      "delay": "PT1M",
      "maxAttempts": 5
    ]
  ],
  "states": [{
    "actions": [{
      "functionRef": "MyFirstFunction",
      "retryRef": "FirstRetryStrategy"
    }]
  }],
}
        `);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'FirstRetryStrategy' in 'retries'",
        range: { end: { character: 38, line: 23 }, start: { character: 18, line: 23 } },
      },
    ]);
  });

  test("check results passing array of values to functions array", () => {
    const { content } = trim(`
{
  "functions": [
    {
      "name": ["myFunc"],
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
            "refName":"myFunc"
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
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'myFunc' in 'functions'",
        range: { end: { character: 30, line: 16 }, start: { character: 22, line: 16 } },
      },
    ]);
  });

  test("check results passing array of values to errors array", () => {
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
      "name": ["SomeErrorOne"],
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
    }]
  }],
}
        `);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseJsonContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'SomeErrorOne' in 'errors'",
        range: { end: { character: 40, line: 31 }, start: { character: 26, line: 31 } },
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
        validationMap: swfRefValidationMap,
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
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'HighBloodPressure' in 'events'",
        range: { end: { character: 21, line: 7 }, start: { character: 4, line: 7 } },
      },
    ]);
  });

  test("check resultEventRef against events array", () => {
    const { content } = trim(`
---
events:
- name: MakeVetAppointment
  type: org.application.info
  source: applicationssource
onEvents:
- actions:
  - eventRef:
      triggerEventRef: MakeVetAppointment
      data: test
      resultEventRef: VetAppointmentInfo
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'VetAppointmentInfo' in 'events'",
        range: { end: { character: 40, line: 10 }, start: { character: 22, line: 10 } },
      },
    ]);
  });

  test("check triggerEventRef against events array", () => {
    const { content } = trim(`
---
events:
- name: VetAppointmentInfo
  type: org.application.info
  source: applicationssource
onEvents:
- actions:
  - eventRef:
      triggerEventRef: MakeVetAppointment
      data: test
      resultEventRef: VetAppointmentInfo        
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'MakeVetAppointment' in 'events'",
        range: { end: { character: 41, line: 8 }, start: { character: 23, line: 8 } },
      },
    ]);
  });

  test("check retryRef against retries array", () => {
    const { content } = trim(`
---
functions:
- name: MyFirstFunction
  operation: "./specs/myService#MyFirstFunction"
  type: rest
- name: MySecondFunction
  operation: "./specs/myService#MySecondFunction"
  type: rest
errors:
- name: SomeErrorOne
  code: '404'
  description: Server has not found anything matching the provided service endpoint
    information
retries:
- name: FirstRetryStrategy
  delay: PT1M
  maxAttempts: 5
states:
- actions:
  - functionRef: MyFirstFunction
    retryRef: FirstRetryStrategy
    retryableErrors:
    - SomeErrorOne
  - functionRef: MySecondFunction
    retryRef: SecondRetryStrategy
    retryableErrors:
    - SomeErrorOne
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'SecondRetryStrategy' in 'retries'",
        range: { end: { character: 33, line: 24 }, start: { character: 14, line: 24 } },
      },
    ]);
  });

  test("check retryableErrors against errors array", () => {
    const { content } = trim(`
---
functions:
- name: MyFirstFunction
  operation: "./specs/myService#MyFirstFunction"
  type: rest
- name: MySecondFunction
  operation: "./specs/myService#MySecondFunction"
  type: rest
errors:
- name: SomeErrorOne
  code: '404'
  description: Server has not found anything matching the provided service endpoint
    information
retries:
- name: FirstRetryStrategy
  delay: PT1M
  maxAttempts: 5
states:
- actions:
  - functionRef: MyFirstFunction
    retryRef: FirstRetryStrategy
    retryableErrors:
    - SomeErrorOne
    - SomeErrorTwo
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'SomeErrorTwo' in 'errors'",
        range: { end: { character: 18, line: 23 }, start: { character: 6, line: 23 } },
      },
    ]);
  });

  test("check transition against states array", () => {
    const { content } = trim(`
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
        refName: "myFunc"
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'end' in 'states'",
        range: { end: { character: 17, line: 8 }, start: { character: 14, line: 8 } },
      },
    ]);
  });

  test("check invalid eventRef declaration against eventRefs array", () => {
    const { content } = trim(`
---
events:
- name: 
  - ref: HighBloodPressure
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
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'HighBloodPressure' in 'events'",
        range: { end: { character: 21, line: 8 }, start: { character: 4, line: 8 } },
      },
    ]);
  });

  test("check invalid functions declaration against refName", () => {
    const { content } = trim(`
---
functions:
- name: 
  - ref: testFunc
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
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'testFunc' in 'functions'",
        range: { end: { character: 27, line: 12 }, start: { character: 17, line: 12 } },
      },
    ]);
  });

  test("check invalid errors declaration against states array", () => {
    const { content } = trim(`
---
functions:
- name: MyFirstFunction
  operation: "./specs/myService#MyFirstFunction"
  type: rest
- name: MySecondFunction
  operation: "./specs/myService#MySecondFunction"
  type: rest
errors:
- refName: SomeErrorOne
  code: '404'
  description: Server has not found anything matching the provided service endpoint
    information
retries:
- name: FirstRetryStrategy
  delay: PT1M
  maxAttempts: 5
states:
- actions:
  - functionRef: MyFirstFunction
    retryRef: FirstRetryStrategy
    retryableErrors:
    - SomeErrorOne
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'SomeErrorOne' in 'errors'",
        range: { end: { character: 18, line: 22 }, start: { character: 6, line: 22 } },
      },
    ]);
  });

  test("check invalid retries declaration against retriesRef", () => {
    const { content } = trim(`
---
functions:
- name: MyFirstFunction
  operation: "./specs/myService#MyFirstFunction"
  type: rest
- name: MySecondFunction
  operation: "./specs/myService#MySecondFunction"
  type: rest
errors:
- name: SomeErrorOne
  code: '404'
  description: Server has not found anything matching the provided service endpoint
    information
retries:
- name: 
  - ref: FirstRetryStrategy
  delay: PT1M
  maxAttempts: 5
states:
- actions:
  - functionRef: MyFirstFunction
    retryRef: FirstRetryStrategy
    retryableErrors:
    - SomeErrorOne
`);
    expect(
      doRefValidation({
        textDocument: textDoc(content),
        rootNode: parseYamlContent(content)!,
        validationMap: swfRefValidationMap,
      })
    ).toEqual([
      {
        message: "Missing 'FirstRetryStrategy' in 'retries'",
        range: { end: { character: 32, line: 21 }, start: { character: 14, line: 21 } },
      },
    ]);
  });
});
