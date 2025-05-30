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
import { Job, JobOperationResult, ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  SvgSuccessResponse,
  SvgErrorResponse,
  TriggerableNode,
  NodeInstance,
} from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import {
  OnOpenProcessInstanceDetailsListener,
  ProcessDetailsChannelApi,
  ProcessDetailsState,
  ProcessDetailsUnSubscribeHandler,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";
import {
  getJobs,
  getProcessDetails,
  getProcessDetailsSVG,
  getTriggerableNodes,
  handleJobReschedule,
  handleNodeInstanceCancel,
  handleNodeInstanceRetrigger,
  handleNodeTrigger,
  handleProcessAbort,
  handleProcessRetry,
  handleProcessSkip,
  handleProcessVariableUpdate,
  jobCancel,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";
import ApolloClient from "apollo-client";

export class ProcessDetailsChannelApiImpl implements ProcessDetailsChannelApi {
  private _processDetailsState: ProcessDetailsState;
  private readonly listeners: OnOpenProcessInstanceDetailsListener[] = [];

  constructor(
    private readonly client: ApolloClient<any>,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined }
  ) {
    this._processDetailsState = { id: "" };
  }

  get processDetailsState(): ProcessDetailsState {
    return this._processDetailsState;
  }

  processDetails__getProcessDetails(id: string): Promise<ProcessInstance> {
    return getProcessDetails(id, this.client).then((details) => {
      return {
        ...details,
        endpoint: this.options?.transformEndpointBaseUrl?.(details.endpoint) ?? details.endpoint,
        serviceUrl: this.options?.transformEndpointBaseUrl?.(details.serviceUrl) ?? details.serviceUrl,
      };
    });
  }

  processDetails__getProcessDiagram(processInstance: ProcessInstance): Promise<SvgSuccessResponse | SvgErrorResponse> {
    return getProcessDetailsSVG(processInstance, this.client);
  }

  processDetails__getJobs(id: string): Promise<Job[]> {
    return getJobs(id, this.client);
  }

  processDetails__getTriggerableNodes(processInstance: ProcessInstance): Promise<TriggerableNode[]> {
    return getTriggerableNodes(processInstance, this.client);
  }

  processDetails__cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobOperationResult> {
    return jobCancel(job, this.client);
  }

  processDetails__rescheduleJob(
    job: any,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return handleJobReschedule(job, repeatInterval, repeatLimit, scheduleDate, this.client);
  }

  processDetails__openProcessDetails(id: string): void {
    this._processDetailsState = { id: id };
    this.listeners.forEach((listener) => listener.onOpen(id));
  }

  processDetails__handleProcessAbort(processInstance: ProcessInstance): Promise<void> {
    return handleProcessAbort(processInstance, this.client);
  }

  processDetails__handleNodeTrigger(processInstance: ProcessInstance, node: TriggerableNode): Promise<void> {
    return handleNodeTrigger(processInstance, node, this.client);
  }

  processDetails__handleProcessVariableUpdate(processInstance: ProcessInstance, updatedJson: Record<string, unknown>) {
    return handleProcessVariableUpdate(processInstance, updatedJson, this.client);
  }

  processDetails__handleProcessRetry(processInstance: ProcessInstance): Promise<void> {
    return handleProcessRetry(processInstance, this.client);
  }

  processDetails__handleNodeInstanceCancel(processInstance: ProcessInstance, node: NodeInstance): Promise<void> {
    return handleNodeInstanceCancel(processInstance, node, this.client);
  }

  processDetails__handleProcessSkip(processInstance: ProcessInstance): Promise<void> {
    return handleProcessSkip(processInstance, this.client);
  }

  processDetails__handleNodeInstanceRetrigger(
    processInstance: ProcessInstance,
    node: Pick<NodeInstance, "id">
  ): Promise<void> {
    return handleNodeInstanceRetrigger(processInstance, node, this.client);
  }

  processDetails__onOpenProcessInstanceDetailsListener(
    listener: OnOpenProcessInstanceDetailsListener
  ): Promise<ProcessDetailsUnSubscribeHandler> {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return Promise.resolve({
      unSubscribe,
    });
  }
}
