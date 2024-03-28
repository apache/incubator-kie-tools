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
  NodeInstance,
  Milestone,
  TriggerableNode,
  OrderBy,
  JsonType,
} from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export enum JobStatus {
  Error = "ERROR",
  Executed = "EXECUTED",
  Scheduled = "SCHEDULED",
  Retry = "RETRY",
  Canceled = "CANCELED",
}

export interface Job {
  id: string;
  status: JobStatus;
  expirationTime: Date;
  priority: number;
  callbackEndpoint: string;
  repeatInterval: number;
  repeatLimit: number;
  scheduledId: string;
  retries: number;
  lastUpdate: Date;
  executionCounter?: number;
  endpoint: string;
  nodeInstanceId?: string;
  workflowId?: string;
  workflowInstanceId?: string;
  rootWorkflowId?: string;
  rootWorkflowInstanceId?: string;
}

export interface BulkCancel {
  successJobs: Job[];
  failedJobs: Job[];
}

export interface JobCancel {
  modalTitle: string;
  modalContent: string;
}

export interface JobsSortBy {
  status?: OrderBy;
  expirationTime?: OrderBy;
  priority?: OrderBy;
  retries?: OrderBy;
  lastUpdate?: OrderBy;
  executionCounter?: OrderBy;
}

export interface BulkWorkflowInstanceActionResponse {
  successWorkflowInstances: WorkflowInstance[];
  failedWorkflowInstances: WorkflowInstance[];
}

export enum WorkflowInstanceState {
  Active = "ACTIVE",
  Completed = "COMPLETED",
  Aborted = "ABORTED",
  Suspended = "SUSPENDED",
  Error = "ERROR",
}

export interface WorkflowInstanceError {
  __typename?: "WorkflowInstanceError";
  nodeDefinitionId: string;
  message?: string;
}
export interface WorkflowInstance {
  id: string;
  processId: string;
  processName?: string;
  parentWorkflowInstanceId?: string;
  rootWorkflowInstanceId?: string;
  rootWorkflowId?: string;
  roles?: string[];
  state: WorkflowInstanceState;
  endpoint: string;
  serviceUrl?: string;
  nodes: NodeInstance[];
  milestones?: Milestone[];
  variables?: string | unknown;
  start: Date;
  end?: Date;
  parentWorkflowInstance?: WorkflowInstance;
  childWorkflowInstances?: WorkflowInstance[];
  error?: WorkflowInstanceError;
  addons?: string[];
  lastUpdate: Date;
  businessKey?: string;
  isSelected?: boolean;
  errorMessage?: string;
  isOpen?: boolean;
  diagram?: string;
  nodeDefinitions?: TriggerableNode[];
  source?: string;
}

export interface WorkflowInstanceFilter {
  status: WorkflowInstanceState[];
  businessKey?: string[];
}

export interface WorkflowListSortBy {
  workflowName?: OrderBy;
  state?: OrderBy;
  start?: OrderBy;
  lastUpdate?: OrderBy;
}

export interface WorkflowListState {
  filters: WorkflowInstanceFilter;
  sortBy: WorkflowListSortBy;
}

export interface WorkflowDefinition {
  workflowName: string;
  endpoint: string;
}

export interface WorkflowResponse {
  id: string;
  workflowdata: JsonType;
}

export enum CloudEventMethod {
  POST = "POST",
  PUT = "PUT",
}

export interface CloudEventRequest {
  endpoint: string;
  method: CloudEventMethod;

  headers: CloudEventHeaders;
  data: string;
}

export interface CloudEventHeaders {
  type: string; // Type of the cloud event
  source: string; // Source of the cloud event

  extensions: Record<string, string>;
}
