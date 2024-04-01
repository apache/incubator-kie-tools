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

import { triggerStartCloudEvent } from "@kie-tools/runtime-tools-swf-gateway-api/dist/gatewayApi";
import { CloudEventFormGatewayApiImpl } from "../../impl/CloudEventFormGatewayApiImpl";

jest.mock("@kie-tools/runtime-tools-swf-gateway-api/dist/gatewayApi", () => ({
  triggerStartCloudEvent: jest.fn(),
}));

let gatewayApi: CloudEventFormGatewayApiImpl;

describe("CloudEventFormGatewayApiImpl tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new CloudEventFormGatewayApiImpl("http://localhost:8080");
  });

  it("should trigger start cloud event", async () => {
    const event = {} as any;

    await gatewayApi.triggerStartCloudEvent(event);

    expect(triggerStartCloudEvent).toHaveBeenCalledWith(event, "http://localhost:8080");
  });
});
