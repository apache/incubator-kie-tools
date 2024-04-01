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
import { WorkflowListChannelApi, WorkflowListDriver } from "../api";
import {
  BulkWorkflowInstanceActionResponse,
  WorkflowInstance,
  WorkflowInstanceFilter,
  WorkflowListSortBy,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

export class WorkflowListChannelApiImpl implements WorkflowListChannelApi {
  constructor(private readonly driver: WorkflowListDriver) {}

  workflowList__initialLoad(filter: WorkflowInstanceFilter, sortBy: WorkflowListSortBy): Promise<void> {
    return this.driver.initialLoad(filter, sortBy);
  }

  workflowList__openWorkflow(workflow: WorkflowInstance): Promise<void> {
    return this.driver.openWorkflow(workflow);
  }

  workflowList__applyFilter(filter: WorkflowInstanceFilter): Promise<void> {
    return this.driver.applyFilter(filter);
  }

  workflowList__applySorting(sortBy: WorkflowListSortBy): Promise<void> {
    return this.driver.applySorting(sortBy);
  }

  workflowList__handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void> {
    return this.driver.handleWorkflowSkip(workflowInstance);
  }
  workflowList__handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void> {
    return this.driver.handleWorkflowRetry(workflowInstance);
  }
  workflowList__handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void> {
    return this.driver.handleWorkflowAbort(workflowInstance);
  }
  workflowList__handleWorkflowMultipleAction(
    workflowInstances: WorkflowInstance[],
    operationType: OperationType
  ): Promise<BulkWorkflowInstanceActionResponse> {
    return this.driver.handleWorkflowMultipleAction(workflowInstances, operationType);
  }
  workflowList__query(offset: number, limit: number): Promise<WorkflowInstance[]> {
    return this.driver.query(offset, limit);
  }

  workflowList__getChildWorkflowsQuery(rootWorkflowInstanceId: string): Promise<WorkflowInstance[]> {
    return this.driver.getChildWorkflowsQuery(rootWorkflowInstanceId);
  }

  workflowList__openTriggerCloudEvent(workflowInstance?: WorkflowInstance): void {
    this.driver.openTriggerCloudEvent(workflowInstance);
  }
}
