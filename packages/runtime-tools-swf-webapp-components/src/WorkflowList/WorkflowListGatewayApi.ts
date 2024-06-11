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
  BulkWorkflowInstanceActionResponse,
  WorkflowInstance,
  WorkflowInstanceFilter,
  WorkflowListSortBy,
} from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { WorkflowListQueries } from "./WorkflowListQueries";
import { OperationType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface WorkflowListGatewayApi {
  workflowListState: WorkflowListState;
  initialLoad: (filter: WorkflowInstanceFilter, sortBy: WorkflowListSortBy) => Promise<void>;
  openWorkflow: (workflow: WorkflowInstance) => Promise<void>;
  applyFilter: (filter: WorkflowInstanceFilter) => Promise<void>;
  applySorting: (SortBy: WorkflowListSortBy) => Promise<void>;
  handleWorkflowSkip: (workflowInstance: WorkflowInstance) => Promise<void>;
  handleWorkflowRetry: (workflowInstance: WorkflowInstance) => Promise<void>;
  handleWorkflowAbort: (workflowInstance: WorkflowInstance) => Promise<void>;
  handleWorkflowMultipleAction: (
    workflowInstances: WorkflowInstance[],
    operationType: OperationType
  ) => Promise<BulkWorkflowInstanceActionResponse>;
  query(offset: number, limit: number): Promise<WorkflowInstance[]>;
  getChildWorkflowsQuery(rootWorkflowInstanceId: string): Promise<WorkflowInstance[]>;
  openTriggerCloudEvent: (workflowInstance?: WorkflowInstance) => void;
  onOpenWorkflowListen: (listener: OnOpenWorkflowListener) => UnSubscribeHandler;
  onOpenTriggerCloudEventListen: (listener: OnOpenTriggerCloudEventListener) => UnSubscribeHandler;
}

export interface WorkflowListState {
  filters: WorkflowInstanceFilter;
  sortBy: WorkflowListSortBy;
}

export interface OnOpenWorkflowListener {
  onOpen: (workflow: WorkflowInstance) => void;
}

export interface OnOpenTriggerCloudEventListener {
  onOpen: (workflow?: WorkflowInstance) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}
export class WorkflowListGatewayApiImpl implements WorkflowListGatewayApi {
  private readonly onOpenWorkflowListeners: OnOpenWorkflowListener[] = [];
  private readonly onOpenTriggerCloudEventListeners: OnOpenTriggerCloudEventListener[] = [];

  private readonly queries: WorkflowListQueries;
  private _WorkflowListState: WorkflowListState;

  constructor(queries: WorkflowListQueries) {
    this.queries = queries;
    this._WorkflowListState = {
      filters: {
        status: [],
        businessKey: [],
      },
      sortBy: {},
    };
  }

  get workflowListState(): WorkflowListState {
    return this._WorkflowListState;
  }

  openWorkflow = (workflow: WorkflowInstance): Promise<void> => {
    this.onOpenWorkflowListeners.forEach((listener) => listener.onOpen(workflow));
    return Promise.resolve();
  };

  initialLoad = (filter: WorkflowInstanceFilter, sortBy: WorkflowListSortBy): Promise<void> => {
    this._WorkflowListState.filters = filter;
    this._WorkflowListState.sortBy = sortBy;
    return Promise.resolve();
  };

  applyFilter = (filter: WorkflowInstanceFilter): Promise<void> => {
    this.workflowListState.filters = filter;
    return Promise.resolve();
  };

  applySorting = (sortBy: WorkflowListSortBy) => {
    this._WorkflowListState.sortBy = sortBy;
    return Promise.resolve();
  };

  handleWorkflowSkip = async (workflowInstance: WorkflowInstance): Promise<void> => {
    return this.queries.handleWorkflowSkip(workflowInstance);
  };

  handleWorkflowRetry = async (workflowInstance: WorkflowInstance): Promise<void> => {
    return this.queries.handleWorkflowRetry(workflowInstance);
  };

  handleWorkflowAbort = async (workflowInstance: WorkflowInstance): Promise<void> => {
    return this.queries.handleWorkflowAbort(workflowInstance);
  };

  handleWorkflowMultipleAction = async (
    workflowInstances: WorkflowInstance[],
    operationType: OperationType
  ): Promise<BulkWorkflowInstanceActionResponse> => {
    return this.queries.handleWorkflowMultipleAction(workflowInstances, operationType);
  };
  query(offset: number, limit: number): Promise<WorkflowInstance[]> {
    return new Promise<WorkflowInstance[]>((resolve, reject) => {
      this.queries
        .getWorkflowInstances(offset, limit, this._WorkflowListState.filters, this._WorkflowListState.sortBy)
        .then((value) => {
          resolve(value);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }

  getChildWorkflowsQuery(rootWorkflowInstanceId: string): Promise<WorkflowInstance[]> {
    return new Promise<WorkflowInstance[]>((resolve, reject) => {
      this.queries
        .getChildWorkflowInstances(rootWorkflowInstanceId)
        .then((value) => {
          resolve(value);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }

  onOpenWorkflowListen(listener: OnOpenWorkflowListener): UnSubscribeHandler {
    this.onOpenWorkflowListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onOpenWorkflowListeners.indexOf(listener);
      if (index > -1) {
        this.onOpenWorkflowListeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }

  onOpenTriggerCloudEventListen(listener: OnOpenTriggerCloudEventListener): UnSubscribeHandler {
    this.onOpenTriggerCloudEventListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onOpenTriggerCloudEventListeners.indexOf(listener);
      if (index > -1) {
        this.onOpenTriggerCloudEventListeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }

  openTriggerCloudEvent(workflowInstance?: WorkflowInstance): void {
    this.onOpenTriggerCloudEventListeners.forEach((listener) => listener.onOpen(workflowInstance));
  }
}
