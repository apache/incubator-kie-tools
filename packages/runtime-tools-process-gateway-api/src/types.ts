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
  processId?: string;
  processInstanceId?: string;
  rootProcessId?: string;
  rootProcessInstanceId?: string;
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

export interface BulkProcessInstanceActionResponse {
  successProcessInstances: ProcessInstance[];
  failedProcessInstances: ProcessInstance[];
}

export enum ProcessInstanceState {
  Active = "ACTIVE",
  Completed = "COMPLETED",
  Aborted = "ABORTED",
  Suspended = "SUSPENDED",
  Error = "ERROR",
}

export interface ProcessInstanceError {
  __typename?: "ProcessInstanceError";
  nodeDefinitionId: string;
  message?: string;
}
export interface ProcessInstance {
  id: string;
  processId: string;
  processName?: string;
  parentProcessInstanceId?: string;
  rootProcessInstanceId?: string;
  rootProcessId?: string;
  roles?: string[];
  state: ProcessInstanceState;
  endpoint: string;
  serviceUrl?: string;
  nodes: NodeInstance[];
  milestones?: Milestone[];
  variables?: string;
  start: Date;
  end?: Date;
  parentProcessInstance?: ProcessInstance;
  childProcessInstances?: ProcessInstance[];
  error?: ProcessInstanceError;
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

export interface ProcessInstanceFilter {
  status: ProcessInstanceState[];
  businessKey?: string[];
}

export interface ProcessListSortBy {
  processName?: OrderBy;
  state?: OrderBy;
  start?: OrderBy;
  lastUpdate?: OrderBy;
}

export interface ProcessListState {
  filters: ProcessInstanceFilter;
  sortBy: ProcessListSortBy;
}

export interface ProcessDefinition {
  processName: string;
  endpoint: string;
}

export interface ProcessResponse {
  id: string;
  processdata: JsonType;
}

export type UserTaskInstance = {
  id: string;
  description?: string;
  name?: string;
  priority?: string;
  processInstanceId: string;
  processId: string;
  rootProcessInstanceId?: string;
  rootProcessId?: string;
  state: string;
  actualOwner?: string;
  adminGroups?: string[];
  adminUsers?: string[];
  completed?: Date;
  started: Date;
  excludedUsers?: string[];
  potentialGroups?: string[];
  potentialUsers?: string[];
  inputs?: string;
  outputs?: string;
  referenceName?: string;
  lastUpdate: Date;
  endpoint?: string;
};
