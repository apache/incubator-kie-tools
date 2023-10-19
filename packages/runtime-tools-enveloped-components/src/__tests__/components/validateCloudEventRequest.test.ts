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

import { CloudEventMethod } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { validateCloudEventRequest } from "../../cloudEventForm/envelope/components/CloudEventForm/validateCloudEventRequest";

describe("validateCloudEventRequest tests", () => {
  it("Valid result", () => {
    const validation = validateCloudEventRequest({
      method: CloudEventMethod.POST,
      endpoint: "/",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "any",
        source: "any",
        extensions: {},
      },
    });

    expect(validation.isValid()).toBeTruthy();
  });

  it("Invalid result - endpoint", () => {
    const validation = validateCloudEventRequest({
      method: CloudEventMethod.POST,
      endpoint: "",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "any",
        source: "any",
        extensions: {},
      },
    });

    expect(validation.isValid()).toBeFalsy();
    expect(validation.getFieldValidation("endpoint")).not.toBeUndefined();
    expect(validation.getFieldValidation("eventType")).toBeUndefined();
    expect(validation.getFieldValidation("eventData")).toBeUndefined();
  });

  it("Invalid result - event type", () => {
    const validation = validateCloudEventRequest({
      method: CloudEventMethod.POST,
      endpoint: "/",
      data: '{"name": "Jon Snow"}',
      headers: {
        type: "",
        source: "any",
        extensions: {},
      },
    });

    expect(validation.isValid()).toBeFalsy();
    expect(validation.getFieldValidation("endpoint")).toBeUndefined();
    expect(validation.getFieldValidation("eventType")).not.toBeUndefined();
    expect(validation.getFieldValidation("eventData")).toBeUndefined();
  });

  it("Invalid result - event data", () => {
    const eventRequest = {
      method: CloudEventMethod.POST,
      endpoint: "/",
      data: "this should break because is not a json string",
      headers: {
        type: "any",
        source: "any",
        extensions: {},
      },
    };

    let validation = validateCloudEventRequest(eventRequest);

    expect(validation.isValid()).toBeFalsy();
    expect(validation.getFieldValidation("endpoint")).toBeUndefined();
    expect(validation.getFieldValidation("eventType")).toBeUndefined();
    expect(validation.getFieldValidation("eventData")).not.toBeUndefined();

    eventRequest.data = '"a string is a valid json value but not a valid payload"';

    validation = validateCloudEventRequest(eventRequest);

    expect(validation.isValid()).toBeFalsy();
    expect(validation.getFieldValidation("endpoint")).toBeUndefined();
    expect(validation.getFieldValidation("eventType")).toBeUndefined();
    expect(validation.getFieldValidation("eventData")).not.toBeUndefined();
  });
});
