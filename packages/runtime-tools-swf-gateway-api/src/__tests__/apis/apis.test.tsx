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
import axios from "axios";
import { CloudEventMethod } from "../../types";
import {
  triggerCloudEvent,
  triggerStartCloudEvent,
  getCustomWorkflowSchemaFromApi,
  startWorkflowRest,
} from "../../gatewayApi";
import {
  KOGITO_BUSINESS_KEY,
  KOGITO_PROCESS_REFERENCE_ID,
} from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe("swf custom form tests", () => {
  it("get custom custom workflow schema from post schema - success ", async () => {
    const schemaPost = {
      schema: {
        title: "Expression",
        description: "Schema for expression test",
        required: ["numbers"],
        type: "object",
        properties: {
          numbers: {
            description: "The array of numbers to be operated with",
            type: "array",
            items: {
              type: "object",
              properties: {
                x: {
                  type: "number",
                },
                y: {
                  type: "number",
                },
              },
            },
          },
        },
      },
    };

    const api: any = {
      paths: {
        ["/expression"]: {
          post: {
            requestBody: {
              content: {
                ["application/json"]: schemaPost,
              },
            },
          },
        },
      },
    };
    const result = await getCustomWorkflowSchemaFromApi(api, "expression");
    expect(result.type).toEqual("object");
    expect(result.properties.numbers.type).toEqual("array");
  });
  it("get custom custom workflow schema - success - no workflowdata", async () => {
    const api: any = {
      components: {
        schemas: {
          // no data
        },
      },
    };
    const result = await getCustomWorkflowSchemaFromApi(api, "expression");
    expect(result).toEqual(null);
  });

  it("get custom workflow schema - success - with workflowdata", async () => {
    const schema = {
      components: {
        schemas: {},
      },
      type: "object",
      properties: {
        name: {
          type: "string",
        },
      },
    };
    const workflowName = "expression";
    const api: any = {
      components: {
        schemas: {
          [workflowName + "_input"]: { ...schema },
        },
      },
    };
    const result = await getCustomWorkflowSchemaFromApi(api, workflowName);
    expect(result).toEqual(schema);
  });

  it("start workflow test - success", async () => {
    mockedAxios.post.mockResolvedValue({
      data: {
        id: "1234",
        workflowdata: {},
      },
    });
    const result = await startWorkflowRest({ name: "John" }, "http://localhost:8080/test", "1234");
    expect(result).toEqual({
      id: "1234",
      workflowdata: {},
    });
  });

  it("start workflow test - failure", async () => {
    mockedAxios.post.mockRejectedValue({
      errorMessage: "Failed to start workflow instance",
    });
    startWorkflowRest({ name: "John" }, "http://localhost:8080/test", "1234").catch((error) => {
      expect(error).toEqual({
        errorMessage: "Failed to start workflow instance",
      });
    });
  });
});

describe("triiger cloud events serction", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it("trigger cloud event start - with businesskey", async () => {
    mockedAxios.request.mockResolvedValue("success");
    const event = {
      method: CloudEventMethod.POST,
      endpoint: "/endpoint",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "eventType",
        source: "eventSource",
        extensions: {
          kogitobusinesskey: "1234",
        },
      },
    };
    const response = await triggerStartCloudEvent(event, "http://localhost:8080/");

    expect(mockedAxios.request).toHaveBeenCalled();
    expect(response).toBe("1234");

    const request = mockedAxios.request.mock.calls[0][0];

    expect(request.url).toBe("http://localhost:8080/endpoint");
    expect(request.method).toBe("POST");
    expect(request.data).toHaveProperty("specversion", "1.0");
    expect(request.data).toHaveProperty("type", "eventType");
    expect(request.data).toHaveProperty("source", "eventSource");
    expect(request.data).toHaveProperty(KOGITO_BUSINESS_KEY, "1234");
    expect(request.data).toHaveProperty("data", JSON.parse(event.data));
  });

  it("trigger cloud event start - without businesskey", async () => {
    mockedAxios.request.mockResolvedValue("success");
    const event = {
      method: CloudEventMethod.POST,
      endpoint: "/endpoint",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "eventType",
        source: "eventSource",
        extensions: {},
      },
    };
    const response = await triggerStartCloudEvent(event, "http://localhost:8080/");

    expect(mockedAxios.request).toHaveBeenCalled();
    expect(response).not.toBeUndefined();

    const request = mockedAxios.request.mock.calls[0][0];

    expect(request.url).toBe("http://localhost:8080/endpoint");
    expect(request.method).toBe("POST");
    expect(request.data).toHaveProperty(KOGITO_BUSINESS_KEY, response);
  });

  it("trigger cloud event - with instanceId", async () => {
    mockedAxios.request.mockResolvedValue("success");
    const event = {
      method: CloudEventMethod.POST,
      endpoint: "/endpoint",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "eventType",
        source: "eventSource",
        extensions: {
          kogitoprocrefid: "1234",
        },
      },
    };
    const response = await triggerCloudEvent(event, "http://localhost:8080/");

    expect(mockedAxios.request).toHaveBeenCalled();
    expect(response).not.toBeUndefined();

    const request = mockedAxios.request.mock.calls[0][0];

    expect(request.url).toBe("http://localhost:8080/endpoint");
    expect(request.method).toBe("POST");
    expect(request.data).toHaveProperty(KOGITO_PROCESS_REFERENCE_ID, "1234");
    expect(request.data).not.toHaveProperty(KOGITO_BUSINESS_KEY);
  });

  it("trigger cloud event - without instanceId", async () => {
    mockedAxios.request.mockResolvedValue("success");
    const event = {
      method: CloudEventMethod.POST,
      endpoint: "/endpoint",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "eventType",
        source: "eventSource",
        extensions: {},
      },
    };
    const response = await triggerCloudEvent(event, "http://localhost:8080/");

    expect(mockedAxios.request).toHaveBeenCalled();
    expect(response).not.toBeUndefined();

    const request = mockedAxios.request.mock.calls[0][0];

    expect(request.url).toBe("http://localhost:8080/endpoint");
    expect(request.method).toBe("POST");
    expect(request.data).not.toHaveProperty(KOGITO_PROCESS_REFERENCE_ID);
    expect(request.data).not.toHaveProperty(KOGITO_BUSINESS_KEY);
  });

  it("trigger cloud event - using PUT", async () => {
    mockedAxios.request.mockResolvedValue("success");
    const event = {
      method: CloudEventMethod.PUT,
      endpoint: "/endpoint",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "eventType",
        source: "eventSource",
        extensions: {
          kogitoprocrefid: "1234",
        },
      },
    };
    const response = await triggerCloudEvent(event, "http://localhost:8080/");

    expect(mockedAxios.request).toHaveBeenCalled();
    expect(response).not.toBeUndefined();

    const request = mockedAxios.request.mock.calls[0][0];

    expect(request.url).toBe("http://localhost:8080/endpoint");
    expect(request.method).toBe("PUT");
  });
});
