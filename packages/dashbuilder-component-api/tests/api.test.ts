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
  ColumnType,
  DataSet,
  FilterRequest,
  FunctionCallRequest,
  FunctionResponse,
  FunctionResultType,
  ComponentMessage,
  MessageType,
  MessageProperty,
  ComponentApi,
  ComponentBus,
} from "../src/index";

import { DashbuilderComponentController } from "../src/controller/DashbuilderComponentController";

const controller = new ComponentApi().getComponentController() as DashbuilderComponentController;

const sampleDataSet: DataSet = {
  columns: [
    {
      name: "Name",
      type: ColumnType.LABEL,
      settings: {
        columnId: "name",
        columnName: "Name",
        valueExpression: "value",
        emptyTemplate: "---",
      },
    },
    {
      name: "Age",
      type: ColumnType.NUMBER,
      settings: {
        columnId: "age",
        columnName: "age",
        valueExpression: "value",
        emptyTemplate: "---",
        valuePattern: "#,##0.00",
      },
    },
  ],
  data: [["John", "32"]],
};

describe("[Controller API] Callbacks", () => {
  it("INIT Callback without params", async () => {
    const handleInit = jest.fn();
    controller.setOnInit(handleInit);
    await postInitMessage(new Map());
    expect(handleInit).toHaveBeenCalledTimes(1);
  });

  it("INIT Callback with params", async () => {
    const handleInit = jest.fn();
    const params = new Map<string, any>();
    params.set("hello", "world");
    controller.setOnInit(handleInit);
    await postInitMessage(params);
    expect(handleInit).toHaveBeenCalledWith(params);
  });

  it("DataSet Callback", async () => {
    const handleDataSet = jest.fn();
    controller.setOnDataSet(handleDataSet);
    await postDataSetMessage();
    expect(handleDataSet).toHaveBeenCalledWith(sampleDataSet, expect.any(Map));
  });
});

describe("[Controller API] Sending Requests", () => {
  const bus = mockBus();
  const componentId = "42";
  beforeAll(() => {
    const params = new Map<string, any>();
    params.set(MessageProperty.COMPONENT_ID, componentId);
    controller.init(params);
    controller.setComponentBus(bus);
  });

  it("Configuration Issues", async () => {
    const configIssue = "some configuration issue.";
    const params = new Map<string, any>();
    const expected: ComponentMessage = {
      type: MessageType.FIX_CONFIGURATION,
      properties: params,
    };
    params.set(MessageProperty.CONFIGURATION_ISSUE, configIssue);

    controller.requireConfigurationFix(configIssue);
    await delay(0);

    expect(bus.send).toBeCalledWith(componentId, expected);
  });

  it("Configuration Fixed", async () => {
    const message: ComponentMessage = {
      type: MessageType.CONFIGURATION_OK,
      properties: new Map(),
    };
    controller.configurationOk();
    await delay(0);

    expect(bus.send).toBeCalledWith(componentId, message);
  });

  it("Filter", async () => {
    const filterRequest: FilterRequest = {
      column: 1,
      reset: false,
      row: 1,
    };
    const props = new Map<string, any>();
    props.set(MessageProperty.FILTER, filterRequest);
    const message: ComponentMessage = {
      type: MessageType.FILTER,
      properties: props,
    };
    controller.filter(filterRequest);
    expect(bus.send).toBeCalledWith(componentId, message);
  });
});

describe("[Controller API] Function Calls", () => {
  it("Function Success", async () => {
    const functionCall = buildFunctionCallRequest();

    const callPromise = controller.callFunction(functionCall);
    await delay(0);

    const result = "SUCCESS RESULT";
    const response = buildFunctionResponse(functionCall, result, FunctionResultType.SUCCESS);

    window.postMessage(response, window.location.origin);
    return expect(callPromise).resolves.toBe(result);
  });

  it("Function Success", async () => {
    const functionCall = buildFunctionCallRequest();

    const callPromise = controller.callFunction(functionCall);
    await delay(0);

    const result = "SUCCESS RESULT";
    const response = buildFunctionResponse(functionCall, result, FunctionResultType.SUCCESS);

    window.postMessage(response, window.location.origin);
    return expect(callPromise).resolves.toBe(result);
  });

  it("Function Not Found", async () => {
    const functionCall = buildFunctionCallRequest();

    const callPromise = controller.callFunction(functionCall);
    await delay(0);

    const message = "NOT FOUND RESULT";
    const response = buildFunctionResponse(functionCall, "", FunctionResultType.NOT_FOUND, message);

    window.postMessage(response, window.location.origin);
    return expect(callPromise).rejects.toBe(message);
  });

  it("Function Execution Error", async () => {
    const functionCall = buildFunctionCallRequest();
    const callPromise = controller.callFunction(functionCall);
    await delay(0);

    const message = "ERROR RESULT";
    const response = buildFunctionResponse(functionCall, "", FunctionResultType.ERROR, message);

    window.postMessage(response, window.location.origin);
    return expect(callPromise).rejects.toBe(message);
  });
});

function buildFunctionCallRequest(): FunctionCallRequest {
  const functionParams = new Map();
  functionParams.set("test", "test");
  return {
    functionName: "test function name",
    parameters: functionParams,
  };
}

const delay = (ms: number) => {
  return new Promise((res) => setTimeout(res, ms));
};

async function postDataSetMessage() {
  const params = new Map<string, any>();
  params.set("dataSet", sampleDataSet);
  const datasetMsg: ComponentMessage = {
    type: MessageType.DATASET,
    properties: params,
  };
  await postMessage(datasetMsg);
}

async function postInitMessage(params: Map<string, any>) {
  const init: ComponentMessage = {
    type: MessageType.INIT,
    properties: params,
  };
  await postMessage(init);
}

async function postMessage(message: ComponentMessage) {
  window.postMessage(message, window.location.origin);
  await delay(0);
}

function mockBus(): ComponentBus {
  return {
    destroy: jest.fn(),
    start: jest.fn(),
    send: jest.fn(),
    setListener: jest.fn(),
  };
}

function buildFunctionResponse(
  _request: FunctionCallRequest,
  _result: string,
  _type: FunctionResultType,
  _message?: string
) {
  // sends the response here
  const functionResponse: FunctionResponse = {
    message: _message || "success",
    resultType: _type,
    result: _result,
    request: _request,
  };
  const params = new Map<string, any>();
  const functionResponseMessage: ComponentMessage = {
    type: MessageType.FUNCTION_RESPONSE,
    properties: params,
  };
  params.set(MessageProperty.FUNCTION_RESPONSE, functionResponse);

  return functionResponseMessage;
}
