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

import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { WorkflowDetailsChannelApi, WorkflowDetailsDriver } from "../api";
import { TriggerableNode, NodeInstance } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { Job, JobCancel, WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

export default class WorkflowDetailsEnvelopeViewDriver implements WorkflowDetailsDriver {
  constructor(private readonly channelApi: MessageBusClientApi<WorkflowDetailsChannelApi>) {}

  handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void> {
    return this.channelApi.requests.workflowDetails__handleWorkflowAbort(workflowInstance);
  }

  cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobCancel> {
    return this.channelApi.requests.workflowDetails__cancelJob(job);
  }

  rescheduleJob(
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return this.channelApi.requests.workflowDetails__rescheduleJob(job, repeatInterval, repeatLimit, scheduleDate);
  }

  getTriggerableNodes(workflowInstance: WorkflowInstance): Promise<TriggerableNode[]> {
    return this.channelApi.requests.workflowDetails__getTriggerableNodes(workflowInstance);
  }

  handleNodeTrigger(workflowInstance: WorkflowInstance, node: TriggerableNode): Promise<void> {
    return this.channelApi.requests.workflowDetails__handleNodeTrigger(workflowInstance, node);
  }

  handleWorkflowVariableUpdate(workflowInstance: WorkflowInstance, updatedJson: Record<string, unknown>) {
    return this.channelApi.requests.workflowDetails__handleWorkflowVariableUpdate(workflowInstance, updatedJson);
  }

  workflowDetailsQuery(id: string): Promise<WorkflowInstance> {
    return this.channelApi.requests.workflowDetails__workflowDetailsQuery(id);
  }

  jobsQuery(id: string): Promise<Job[]> {
    return this.channelApi.requests.workflowDetails__jobsQuery(id);
  }
  openWorkflowInstanceDetails(id: string): void {
    return this.channelApi.notifications.workflowDetails__openWorkflowDetails.send(id);
  }

  handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void> {
    return this.channelApi.requests.workflowDetails__handleWorkflowRetry(workflowInstance);
  }

  handleNodeInstanceCancel(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void> {
    return this.channelApi.requests.workflowDetails__handleNodeInstanceCancel(workflowInstance, node);
  }

  handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void> {
    return this.channelApi.requests.workflowDetails__handleWorkflowSkip(workflowInstance);
  }

  handleNodeInstanceRetrigger(workflowInstance: WorkflowInstance, node: Pick<NodeInstance, "id">): Promise<void> {
    return this.channelApi.requests.workflowDetails__handleNodeInstanceRetrigger(workflowInstance, node);
  }
}
