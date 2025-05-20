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
  NodeInstance,
  SvgErrorResponse,
  SvgSuccessResponse,
  TriggerableNode,
} from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface OnOpenProcessInstanceDetailsListener {
  onOpen(id: string): void;
}

export interface ProcessDetailsUnSubscribeHandler {
  unSubscribe: () => void;
}

export interface ProcessDetailsChannelApi {
  processDetails__openProcessDetails(id: string): void;
  processDetails__getProcessDetails(id: string): Promise<ProcessInstance>;
  processDetails__getProcessDiagram(processInstance: ProcessInstance): Promise<SvgSuccessResponse | SvgErrorResponse>;
  processDetails__getJobs(id: string): Promise<Job[]>;
  processDetails__getTriggerableNodes(processInstance: ProcessInstance): Promise<TriggerableNode[]>;
  processDetails__cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobOperationResult>;
  processDetails__rescheduleJob(
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }>;
  processDetails__handleProcessAbort(processInstance: ProcessInstance): Promise<void>;
  processDetails__handleNodeTrigger(processInstance: ProcessInstance, node: TriggerableNode): Promise<void>;
  processDetails__handleProcessVariableUpdate(
    processInstance: ProcessInstance,
    updatedJson: Record<string, unknown>
  ): Promise<Record<string, unknown>>;
  processDetails__handleProcessRetry(processInstance: ProcessInstance): Promise<void>;
  processDetails__handleNodeInstanceCancel(processInstance: ProcessInstance, node: NodeInstance): Promise<void>;
  processDetails__handleProcessSkip(processInstance: ProcessInstance): Promise<void>;
  processDetails__handleNodeInstanceRetrigger(
    processInstance: ProcessInstance,
    node: Pick<NodeInstance, "id">
  ): Promise<void>;
  processDetails__onOpenProcessInstanceDetailsListener(
    listener: OnOpenProcessInstanceDetailsListener
  ): Promise<ProcessDetailsUnSubscribeHandler>;
}
