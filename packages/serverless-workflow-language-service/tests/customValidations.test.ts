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

import { doCustomValidation } from "../dist/channel/customValidations";
import { TextDocument } from "vscode-languageserver-textdocument";
import { trim } from "./testUtils";

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
    const doc = TextDocument.create("", "serverless-workflow-json", 0, content);
    const validationResults = [
      {
        message: "Missing testFunction in functions",
        range: { end: { character: 36, line: 16 }, start: { character: 22, line: 16 } },
      },
    ];
    expect(doCustomValidation(content, doc)).toEqual(validationResults);
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
    const doc = TextDocument.create("", "serverless-workflow-json", 0, content);
    const validationResults = [
      {
        message: "Missing HighBloodPressure in events",
        range: { end: { character: 38, line: 9 }, start: { character: 19, line: 9 } },
      },
    ];
    expect(doCustomValidation(content, doc)).toEqual(validationResults);
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
    const doc = TextDocument.create("", "serverless-workflow-json", 0, content);
    const validationResults = [
      {
        message: "Missing VetAppointmentInfo in events",
        range: { end: { character: 46, line: 13 }, start: { character: 26, line: 13 } },
      },
    ];
    expect(doCustomValidation(content, doc)).toEqual(validationResults);
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
    const doc = TextDocument.create("", "serverless-workflow-json", 0, content);
    const validationResults = [
      {
        message: "Missing MakeVetAppointment in events",
        range: { end: { character: 47, line: 11 }, start: { character: 27, line: 11 } },
      },
    ];
    expect(doCustomValidation(content, doc)).toEqual(validationResults);
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
    const doc = TextDocument.create("", "serverless-workflow-json", 0, content);
    const validationResults = [
      {
        message: "Missing SecondRetryStrategy in retries",
        range: { end: { character: 39, line: 35 }, start: { character: 18, line: 35 } },
      },
    ];
    expect(doCustomValidation(content, doc)).toEqual(validationResults);
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
    const doc = TextDocument.create("", "serverless-workflow-json", 0, content);
    const validationResults = [
      {
        message: "Missing SomeErrorTwo in errors",
        range: { end: { character: 56, line: 31 }, start: { character: 42, line: 31 } },
      },
    ];
    expect(doCustomValidation(content, doc)).toEqual(validationResults);
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
    const doc = TextDocument.create("", "serverless-workflow-json", 0, content);
    const validationResults = [
      {
        message: "Missing end in states",
        range: { end: { character: 25, line: 12 }, start: { character: 20, line: 12 } },
      },
    ];
    expect(doCustomValidation(content, doc)).toEqual(validationResults);
  });
});
