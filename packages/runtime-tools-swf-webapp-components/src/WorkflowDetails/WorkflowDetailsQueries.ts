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

import {
  getJobs,
  getTriggerableNodes,
  getWorkflowDetails,
  handleJobReschedule,
  handleNodeInstanceCancel,
  handleNodeInstanceRetrigger,
  handleNodeTrigger,
  handleWorkflowAbort,
  handleWorkflowRetry,
  handleWorkflowSkip,
  handleWorkflowVariableUpdate,
  jobCancel,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/gatewayApi";
import { NodeInstance, TriggerableNode } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { Job, JobCancel, WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { ApolloClient } from "apollo-client";

export interface WorkflowDetailsQueries {
  getWorkflowDetails(id: string): Promise<WorkflowInstance>;
  getJobs(id: string): Promise<Job[]>;
  handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void>;
  handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void>;
  handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void>;
  jobCancel(job: Job): Promise<JobCancel>;
  rescheduleJob: (
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
  getTriggerableNodes(workflowInstance: WorkflowInstance): Promise<TriggerableNode[]>;
  handleNodeTrigger(workflowInstance: WorkflowInstance, node: any): Promise<void>;
  handleWorkflowVariableUpdate: (
    workflowInstance: WorkflowInstance,
    updateJson: Record<string, unknown>
  ) => Promise<Record<string, unknown>>;
  handleNodeInstanceCancel: (workflowInstance: WorkflowInstance, node: NodeInstance) => Promise<void>;
  handleNodeInstanceRetrigger(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void>;
}

export class GraphQLWorkflowDetailsQueries implements WorkflowDetailsQueries {
  private readonly client: ApolloClient<any>;

  constructor(client: ApolloClient<any>) {
    this.client = client;
  }

  async getWorkflowDetails(id: string): Promise<WorkflowInstance> {
    return getWorkflowDetails(id, this.client);
  }

  async getJobs(id: string): Promise<Job[]> {
    return Promise.resolve(getJobs(id, this.client));
  }

  async handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void> {
    return handleWorkflowSkip(workflowInstance, this.client);
  }

  async handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void> {
    return handleWorkflowAbort(workflowInstance, this.client);
  }

  async handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void> {
    return handleWorkflowRetry(workflowInstance, this.client);
  }

  async jobCancel(job: Job): Promise<JobCancel> {
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

  async getTriggerableNodes(workflowInstance: WorkflowInstance): Promise<TriggerableNode[]> {
    return Promise.resolve(getTriggerableNodes(workflowInstance, this.client));
  }

  async handleNodeTrigger(workflowInstance: WorkflowInstance, node: any): Promise<void> {
    return handleNodeTrigger(workflowInstance, node, this.client);
  }

  async handleNodeInstanceCancel(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void> {
    return handleNodeInstanceCancel(workflowInstance, node, this.client);
  }

  async handleWorkflowVariableUpdate(
    workflowInstance: WorkflowInstance,
    updateJson: Record<string, unknown>
  ): Promise<Record<string, unknown>> {
    return handleWorkflowVariableUpdate(workflowInstance, updateJson, this.client);
  }

  async handleNodeInstanceRetrigger(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void> {
    return handleNodeInstanceRetrigger(workflowInstance, node, this.client);
  }
}
