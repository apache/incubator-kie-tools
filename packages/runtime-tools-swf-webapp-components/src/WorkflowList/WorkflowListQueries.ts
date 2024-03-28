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
  getChildWorkflowInstances,
  getWorkflowInstances,
  handleWorkflowAbort,
  handleWorkflowMultipleAction,
  handleWorkflowRetry,
  handleWorkflowSkip,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/gatewayApi";
import {
  BulkWorkflowInstanceActionResponse,
  WorkflowInstance,
  WorkflowInstanceFilter,
  WorkflowListSortBy,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { ApolloClient } from "apollo-client";

export interface WorkflowListQueries {
  getWorkflowInstances(
    start: number,
    end: number,
    filters: WorkflowInstanceFilter,
    sortBy: WorkflowListSortBy
  ): Promise<WorkflowInstance[]>;
  getChildWorkflowInstances(rootWorkflowInstanceId: string): Promise<WorkflowInstance[]>;
  handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void>;
  handleWorkflowAbort(workflowInstance: WorkflowInstance): Promise<void>;
  handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void>;

  handleWorkflowMultipleAction(
    workflowInstances: WorkflowInstance[],
    operationType: OperationType
  ): Promise<BulkWorkflowInstanceActionResponse>;
}

export class GraphQLWorkflowListQueries implements WorkflowListQueries {
  private readonly client: ApolloClient<any>;

  constructor(client: ApolloClient<any>) {
    this.client = client;
  }

  getWorkflowInstances(
    offset: number,
    limit: number,
    filters: WorkflowInstanceFilter,
    sortBy: WorkflowListSortBy
  ): Promise<WorkflowInstance[]> {
    return getWorkflowInstances(offset, limit, filters, sortBy, this.client);
  }

  getChildWorkflowInstances(rootWorkflowInstanceId: string): Promise<WorkflowInstance[]> {
    return getChildWorkflowInstances(rootWorkflowInstanceId, this.client);
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

  async handleWorkflowMultipleAction(workflowInstances: WorkflowInstance[], operationType: OperationType) {
    return handleWorkflowMultipleAction(workflowInstances, operationType, this.client);
  }
}
