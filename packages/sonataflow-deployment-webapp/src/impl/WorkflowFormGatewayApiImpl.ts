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
import { OpenAPI } from "openapi-types";
import { WorkflowResponse } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import {
  startWorkflowRest,
  getCustomWorkflowSchemaFromApi,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/gatewayApi/apis";

export interface WorkflowFormGatewayApi {
  setBusinessKey(bk: string): void;
  getBusinessKey(): string;
  getCustomWorkflowSchema(workflowName: string): Promise<Record<string, any>>;
  startWorkflow(endpoint: string, data: Record<string, any>): Promise<WorkflowResponse>;
}

export class WorkflowFormGatewayApiImpl implements WorkflowFormGatewayApi {
  private businessKey: string;

  constructor(private api: OpenAPI.Document) {
    this.businessKey = "";
  }

  setBusinessKey(bk: string) {
    this.businessKey = bk;
  }

  getBusinessKey(): string {
    return this.businessKey;
  }

  getCustomWorkflowSchema(workflowName: string) {
    return getCustomWorkflowSchemaFromApi(this.api, workflowName);
  }

  startWorkflow(endpoint: string, data: Record<string, any>) {
    return startWorkflowRest(data, endpoint, "");
  }
}
