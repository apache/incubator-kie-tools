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

import { CloudEventRequest } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { triggerStartCloudEvent, triggerCloudEvent } from "@kie-tools/runtime-tools-gateway-api/dist/gatewayApi";

export interface CloudEventFormGatewayApi {
  triggerStartCloudEvent(event: CloudEventRequest): Promise<string>;
  triggerCloudEvent(event: CloudEventRequest): Promise<any>;
}

export class CloudEventFormGatewayApiImpl implements CloudEventFormGatewayApi {
  constructor(private readonly baseUrl: string) {}

  async triggerStartCloudEvent(event: CloudEventRequest): Promise<string> {
    const response = await triggerStartCloudEvent(event, this.baseUrl);
    return response;
  }

  triggerCloudEvent(event: CloudEventRequest): Promise<any> {
    return triggerCloudEvent(event, this.baseUrl);
  }
}
