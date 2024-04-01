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

import { TriggerableNode, NodeInstance } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { Job, JobCancel, WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

export interface WorkflowDetailsChannelApi {
  workflowDetails__handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void>;
  workflowDetails__cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobCancel>;
  workflowDetails__rescheduleJob(
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }>;
  workflowDetails__getTriggerableNodes(workflowInstance: WorkflowInstance): Promise<TriggerableNode[]>;
  workflowDetails__handleNodeTrigger(workflowInstance: WorkflowInstance, node: TriggerableNode): Promise<void>;
  workflowDetails__handleWorkflowVariableUpdate(
    workflowInstance: WorkflowInstance,
    updatedJson: Record<string, unknown>
  ): Promise<Record<string, unknown>>;
  workflowDetails__workflowDetailsQuery(id: string): Promise<WorkflowInstance>;
  workflowDetails__jobsQuery(id: string): Promise<Job[]>;
  workflowDetails__openWorkflowDetails(id: string): void;
  workflowDetails__handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void>;
  workflowDetails__handleNodeInstanceCancel(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void>;
  workflowDetails__handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void>;
  workflowDetails__handleNodeInstanceRetrigger(
    workflowInstance: WorkflowInstance,
    node: Pick<NodeInstance, "id">
  ): Promise<void>;
}
