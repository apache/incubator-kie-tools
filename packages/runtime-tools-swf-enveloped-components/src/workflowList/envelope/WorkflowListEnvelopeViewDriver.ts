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
import {
  BulkWorkflowInstanceActionResponse,
  WorkflowInstance,
  WorkflowInstanceFilter,
  WorkflowListSortBy,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { WorkflowListChannelApi, WorkflowListDriver } from "../api";
import { OperationType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export default class WorkflowListEnvelopeViewDriver implements WorkflowListDriver {
  constructor(private readonly channelApi: MessageBusClientApi<WorkflowListChannelApi>) {}
  initialLoad(filter: WorkflowInstanceFilter, sortBy: WorkflowListSortBy): Promise<void> {
    return this.channelApi.requests.workflowList__initialLoad(filter, sortBy);
  }
  openWorkflow(workflow: WorkflowInstance): Promise<void> {
    return this.channelApi.requests.workflowList__openWorkflow(workflow);
  }
  applyFilter(filter: WorkflowInstanceFilter): Promise<void> {
    return this.channelApi.requests.workflowList__applyFilter(filter);
  }
  applySorting(sortBy: WorkflowListSortBy): Promise<void> {
    return this.channelApi.requests.workflowList__applySorting(sortBy);
  }
  handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void> {
    return this.channelApi.requests.workflowList__handleWorkflowSkip(workflowInstance);
  }
  handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void> {
    return this.channelApi.requests.workflowList__handleWorkflowRetry(workflowInstance);
  }
  handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void> {
    return this.channelApi.requests.workflowList__handleWorkflowAbort(workflowInstance);
  }
  handleWorkflowMultipleAction(
    workflowInstances: WorkflowInstance[],
    operationType: OperationType
  ): Promise<BulkWorkflowInstanceActionResponse> {
    return this.channelApi.requests.workflowList__handleWorkflowMultipleAction(workflowInstances, operationType);
  }
  query(offset: number, limit: number): Promise<WorkflowInstance[]> {
    return this.channelApi.requests.workflowList__query(offset, limit);
  }
  getChildWorkflowsQuery(rootWorkflowInstanceId: string): Promise<WorkflowInstance[]> {
    return this.channelApi.requests.workflowList__getChildWorkflowsQuery(rootWorkflowInstanceId);
  }

  openTriggerCloudEvent(workflowInstance?: WorkflowInstance): void {
    this.channelApi.notifications.workflowList__openTriggerCloudEvent.send(workflowInstance);
  }
}
