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

export enum JobStatus {
  Error = "ERROR",
  Executed = "EXECUTED",
  Scheduled = "SCHEDULED",
  Retry = "RETRY",
  Canceled = "CANCELED",
}

export interface Job {
  id: string;
  workflowId: string;
  workflowInstanceId: string;
  rootWorkflowInstanceId?: string;
  rootWorkflowId?: string;
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
}

export interface BulkCancel {
  successJobs: Job[];
  failedJobs: Job[];
}

export interface BulkWorkflowInstanceActionResponse {
  successWorkflowInstances: WorkflowInstance[];
  failedWorkflowInstances: WorkflowInstance[];
}

export interface JobCancel {
  modalTitle: string;
  modalContent: string;
}

export enum WorkflowInstanceState {
  Active = "ACTIVE",
  Completed = "COMPLETED",
  Aborted = "ABORTED",
  Suspended = "SUSPENDED",
  Error = "ERROR",
}

export enum TitleType {
  SUCCESS = "success",
  FAILURE = "failure",
}

export enum MilestoneStatus {
  Available = "AVAILABLE",
  Active = "ACTIVE",
  Completed = "COMPLETED",
}

export interface NodeInstance {
  __typename?: "NodeInstance";
  id: string;
  name: string;
  type: string;
  enter: Date;
  exit?: Date;
  definitionId: string;
  nodeId: string;
}

export interface TriggerableNode {
  id: number;
  name: string;
  type: string;
  uniqueId: string;
  nodeDefinitionId: string;
}

export interface Milestone {
  __typename?: "Milestone";
  id: string;
  name: string;
  status: MilestoneStatus;
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

export enum OrderBy {
  ASC = "ASC",
  DESC = "DESC",
}

export interface WorkflowListSortBy {
  workflowName?: OrderBy;
  state?: OrderBy;
  start?: OrderBy;
  lastUpdate?: OrderBy;
}

export interface JobsSortBy {
  status?: OrderBy;
  expirationTime?: OrderBy;
  priority?: OrderBy;
  retries?: OrderBy;
  lastUpdate?: OrderBy;
  executionCounter?: OrderBy;
}

export interface WorkflowListState {
  filters: WorkflowInstanceFilter;
  sortBy: WorkflowListSortBy;
}

export interface SvgSuccessResponse {
  svg: string;
  error?: never;
}

export interface SvgErrorResponse {
  error: string;
  svg?: never;
}

export interface WorkflowDefinition {
  workflowName: string;
  endpoint: string;
}

export type JsonType = { [key: string]: string | number | boolean };

export interface WorkflowResponse {
  id: string;
  workflowdata: JsonType;
}

export enum SCHEMA_VERSION {
  DRAFT_7 = "http://json-schema.org/draft-07/schema#",
  DRAFT_2019_09 = "https://json-schema.org/draft/2019-09/schema",
}
export interface FormRendererApi {
  doReset: () => void;
}

export enum OperationType {
  ABORT = "ABORT",
  SKIP = "SKIP",
  RETRY = "RETRY",
  CANCEL = "CANCEL",
}
export const KOGITO_PROCESS_REFERENCE_ID = "kogitoprocrefid";
export const KOGITO_BUSINESS_KEY = "kogitobusinesskey";

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

export interface CustomDashboardInfo {
  name: string;
  path: string;
  lastModified: Date;
}

export interface FormResources {
  scripts: {
    [key: string]: string;
  };
  styles: {
    [key: string]: string;
  };
}

export enum FormType {
  HTML = "HTML",
  TSX = "TSX",
}

interface FormConfiguration {
  schema: string;
  resources: FormResources;
}

export interface Form {
  formInfo: FormInfo;
  source: string;
  configuration: FormConfiguration;
}

export interface FormContent {
  source: string;
  configuration: FormConfiguration;
}

export interface FormInfo {
  name: string;
  type: FormType;
  lastModified: Date;
}
