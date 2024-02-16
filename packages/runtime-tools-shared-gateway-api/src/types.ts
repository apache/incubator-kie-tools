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

export enum OrderBy {
  ASC = "ASC",
  DESC = "DESC",
}

export interface SvgSuccessResponse {
  svg: string;
  error?: never;
}

export interface SvgErrorResponse {
  error: string;
  svg?: never;
}

export type JsonType = { [key: string]: string | number | boolean };

export enum SCHEMA_VERSION {
  DRAFT_7 = "http://json-schema.org/draft-07/schema#",
  DRAFT_2019_09 = "https://json-schema.org/draft/2019-09/schema",
}

export enum OperationType {
  ABORT = "ABORT",
  SKIP = "SKIP",
  RETRY = "RETRY",
  CANCEL = "CANCEL",
}
export const KOGITO_PROCESS_REFERENCE_ID = "kogitoprocrefid";
export const KOGITO_BUSINESS_KEY = "kogitobusinesskey";

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
