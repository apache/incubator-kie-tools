/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import { ApolloClient } from "apollo-client";
import {
  TriggerableNode,
  NodeInstance,
  SvgSuccessResponse,
  SvgErrorResponse,
} from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import {
  handleProcessAbort,
  handleProcessSkip,
  handleProcessRetry,
  jobCancel,
  handleJobReschedule,
  handleNodeTrigger,
  handleProcessVariableUpdate,
  handleNodeInstanceCancel,
  handleNodeInstanceRetrigger,
  getProcessDetails,
  getJobs,
  getSVG,
  getTriggerableNodes,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";
import { Job, JobOperationResult, ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

export interface ProcessDetailsQueries {
  getProcessDetails(id: string): Promise<ProcessInstance>;
  getJobs(id: string): Promise<Job[]>;
  handleProcessSkip(processInstance: ProcessInstance): Promise<void>;
  handleProcessAbort(processInstance: ProcessInstance): Promise<void>;
  handleProcessRetry(processInstance: ProcessInstance): Promise<void>;
  getSVG(processInstance: ProcessInstance): Promise<any>;
  jobCancel(job: Job): Promise<JobOperationResult>;
  rescheduleJob: (
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
  getTriggerableNodes(processInstance: ProcessInstance): Promise<TriggerableNode[]>;
  handleNodeTrigger(processInstance: ProcessInstance, node: any): Promise<void>;
  handleProcessVariableUpdate: (
    processInstance: ProcessInstance,
    updateJson: Record<string, unknown>
  ) => Promise<Record<string, unknown>>;
  handleNodeInstanceCancel: (processInstance: ProcessInstance, node: NodeInstance) => Promise<void>;
  handleNodeInstanceRetrigger(processInstance: ProcessInstance, node: NodeInstance): Promise<void>;
}

export class GraphQLProcessDetailsQueries implements ProcessDetailsQueries {
  constructor(
    private readonly client: ApolloClient<any>,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined }
  ) {}

  async getProcessDetails(id: string): Promise<ProcessInstance> {
    return getProcessDetails(id, this.client).then((details) => {
      return {
        ...details,
        endpoint: this.options?.transformEndpointBaseUrl?.(details.endpoint) ?? details.endpoint,
        serviceUrl: this.options?.transformEndpointBaseUrl?.(details.serviceUrl) ?? details.serviceUrl,
      };
    });
  }

  async getJobs(id: string): Promise<Job[]> {
    return Promise.resolve(getJobs(id, this.client));
  }

  async handleProcessSkip(processInstance: ProcessInstance): Promise<void> {
    return handleProcessSkip(processInstance, this.client);
  }

  async handleProcessAbort(processInstance: ProcessInstance): Promise<void> {
    return handleProcessAbort(processInstance, this.client);
  }

  async handleProcessRetry(processInstance: ProcessInstance): Promise<void> {
    return handleProcessRetry(processInstance, this.client);
  }

  async getSVG(processInstance: ProcessInstance): Promise<SvgSuccessResponse | SvgErrorResponse> {
    return Promise.resolve(getSVG(processInstance, this.client));
  }

  async jobCancel(job: Job): Promise<JobOperationResult> {
    return jobCancel(job, this.client);
  }

  async rescheduleJob(
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return handleJobReschedule(job, repeatInterval, repeatLimit, scheduleDate, this.client);
  }

  async getTriggerableNodes(processInstance: ProcessInstance): Promise<TriggerableNode[]> {
    return Promise.resolve(getTriggerableNodes(processInstance, this.client));
  }

  async handleNodeTrigger(processInstance: ProcessInstance, node: any): Promise<void> {
    return handleNodeTrigger(processInstance, node, this.client);
  }

  async handleNodeInstanceCancel(processInstance: ProcessInstance, node: NodeInstance): Promise<void> {
    return handleNodeInstanceCancel(processInstance, node, this.client);
  }

  async handleProcessVariableUpdate(
    processInstance: ProcessInstance,
    updateJson: Record<string, unknown>
  ): Promise<Record<string, unknown>> {
    return handleProcessVariableUpdate(processInstance, updateJson, this.client);
  }

  async handleNodeInstanceRetrigger(processInstance: ProcessInstance, node: NodeInstance): Promise<void> {
    return handleNodeInstanceRetrigger(processInstance, node, this.client);
  }
}
