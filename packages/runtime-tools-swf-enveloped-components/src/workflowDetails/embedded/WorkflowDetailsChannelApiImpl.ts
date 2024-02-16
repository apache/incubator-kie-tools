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

import { Job, JobCancel, WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { WorkflowDetailsChannelApi, WorkflowDetailsDriver } from "../api";
import { TriggerableNode, NodeInstance } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export class WorkflowDetailsChannelApiImpl implements WorkflowDetailsChannelApi {
  constructor(private readonly driver: WorkflowDetailsDriver) {}

  workflowDetails__handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void> {
    return this.driver.handleWorkflowAbort(workflowInstance);
  }

  workflowDetails__cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobCancel> {
    return this.driver.cancelJob(job);
  }

  workflowDetails__rescheduleJob(
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return this.driver.rescheduleJob(job, repeatInterval, repeatLimit, scheduleDate);
  }

  workflowDetails__getTriggerableNodes(workflowInstance: WorkflowInstance): Promise<TriggerableNode[]> {
    return this.driver.getTriggerableNodes(workflowInstance);
  }

  workflowDetails__handleNodeTrigger(workflowInstance: WorkflowInstance, node: TriggerableNode): Promise<void> {
    return this.driver.handleNodeTrigger(workflowInstance, node);
  }

  workflowDetails__handleWorkflowVariableUpdate(
    workflowInstance: WorkflowInstance,
    updatedJson: Record<string, unknown>
  ) {
    return this.driver.handleWorkflowVariableUpdate(workflowInstance, updatedJson);
  }

  workflowDetails__workflowDetailsQuery(id: string): Promise<WorkflowInstance> {
    return this.driver.workflowDetailsQuery(id);
  }

  workflowDetails__jobsQuery(id: string): Promise<Job[]> {
    return this.driver.jobsQuery(id);
  }
  workflowDetails__openWorkflowDetails(id: string): void {
    this.driver.openWorkflowInstanceDetails(id);
  }

  workflowDetails__handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void> {
    return this.driver.handleWorkflowRetry(workflowInstance);
  }

  workflowDetails__handleNodeInstanceCancel(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void> {
    return this.driver.handleNodeInstanceCancel(workflowInstance, node);
  }

  workflowDetails__handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void> {
    return this.driver.handleWorkflowSkip(workflowInstance);
  }

  workflowDetails__handleNodeInstanceRetrigger(
    workflowInstance: WorkflowInstance,
    node: Pick<NodeInstance, "id">
  ): Promise<void> {
    return this.driver.handleNodeInstanceRetrigger(workflowInstance, node);
  }
}
