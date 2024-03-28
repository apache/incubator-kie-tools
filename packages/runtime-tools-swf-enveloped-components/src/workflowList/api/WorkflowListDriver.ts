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

import { OperationType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import {
  BulkWorkflowInstanceActionResponse,
  WorkflowInstance,
  WorkflowInstanceFilter,
  WorkflowListSortBy,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

export interface WorkflowListDriver {
  initialLoad(filter: WorkflowInstanceFilter, sortBy: WorkflowListSortBy): Promise<void>;
  openWorkflow(workflow: WorkflowInstance): Promise<void>;
  applyFilter(filter: WorkflowInstanceFilter): Promise<void>;
  applySorting(sortBy: WorkflowListSortBy): Promise<void>;
  handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void>;
  handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void>;
  handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void>;
  handleWorkflowMultipleAction(
    workflowInstances: WorkflowInstance[],
    operationType: OperationType
  ): Promise<BulkWorkflowInstanceActionResponse>;
  query(offset: number, limit: number): Promise<WorkflowInstance[]>;
  getChildWorkflowsQuery(rootWorkflowInstanceId: string): Promise<WorkflowInstance[]>;
  openTriggerCloudEvent(workflowInstance?: WorkflowInstance): void;
}
