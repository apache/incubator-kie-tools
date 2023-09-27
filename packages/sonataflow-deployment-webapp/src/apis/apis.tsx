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
import { v4 as uuid } from "uuid";
import { OpenAPI } from "openapi-types";
import { CloudEventRequest, SONATAFLOW_BUSINESS_KEY } from "./CloudEvent";
import { WorkflowResponse } from "./WorkflowResponse";

export const getCustomWorkflowSchema = async (
  api: OpenAPI.Document,
  workflowName: string
): Promise<Record<string, any>> => {
  let schema = {};

  try {
    const schemaFromRequestBody = api.paths["/" + workflowName].post.requestBody.content["application/json"].schema;

    if (schemaFromRequestBody.type) {
      schema = {
        type: schemaFromRequestBody.type,
        properties: schemaFromRequestBody.properties,
      };
    } else {
      schema = (api as any).components.schemas[workflowName + "_input"];
    }
  } catch (e) {
    console.log(e);
    schema = (api as any).components.schemas[workflowName + "_input"];
  }

  return schema ?? null;
};

export const startWorkflowRest = (
  data: Record<string, any>,
  endpoint: string,
  businessKey: string
): Promise<WorkflowResponse> => {
  const requestURL = `${endpoint}${businessKey.length > 0 ? `?businessKey=${businessKey}` : ""}`;
  return new Promise((resolve, reject) => {
    axios
      .post(requestURL, { workflowdata: data })
      .then((response: any) => {
        resolve(response.data);
      })
      .catch((err) => reject(err));
  });
};

const doTriggerCloudEvent = (event: CloudEventRequest, devUIUrl: string): Promise<any> => {
  const cloudEvent = {
    ...event.headers.extensions,
    specversion: "1.0",
    id: uuid(),
    source: event.headers.source ?? "",
    type: event.headers.type,
    data: event.data ? JSON.parse(event.data) : {},
  };

  if (devUIUrl.endsWith("/")) {
    devUIUrl = devUIUrl.slice(0, devUIUrl.length - 1);
  }

  const url = `${devUIUrl}${event.endpoint.startsWith("/") ? "" : "/"}${event.endpoint}`;

  return axios.request({
    url,
    method: event.method,
    data: cloudEvent,
  });
};

export const triggerStartCloudEvent = (event: CloudEventRequest, devUIUrl: string): Promise<string> => {
  if (!event.headers.extensions[SONATAFLOW_BUSINESS_KEY]) {
    event.headers.extensions[SONATAFLOW_BUSINESS_KEY] = String(Math.floor(Math.random() * 100000));
  }

  return new Promise((resolve, reject) => {
    doTriggerCloudEvent(event, devUIUrl)
      .then(() => resolve(event.headers.extensions[SONATAFLOW_BUSINESS_KEY]))
      .catch((error) => reject(error));
  });
};

export const triggerCloudEvent = (event: CloudEventRequest, devUIUrl: string): Promise<any> => {
  return doTriggerCloudEvent(event, devUIUrl);
};
