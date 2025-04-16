/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import {
  ProcessInstance,
  ProcessInstanceFilter,
  ProcessListSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ApolloClient } from "apollo-client";
import { OperationType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import {
  handleProcessAbort,
  handleProcessMultipleAction,
  handleProcessSkip,
  handleProcessRetry,
  getProcessInstances,
  getChildProcessInstances,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export interface ProcessListState {
  filters: ProcessInstanceFilter;
  sortBy: ProcessListSortBy;
}

export interface OnOpenProcessListener {
  onOpen: (process: ProcessInstance) => void;
}

export interface OnUpdateProcessListStateListener {
  onUpdate: (processListState: ProcessListState) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}
export class ProcessListRuntimeApiClient {
  private readonly onOpenProcessListeners: OnOpenProcessListener[] = [];
  private readonly onUpdateProcessListStateListeners: OnUpdateProcessListStateListener[] = [];
  private _ProcessListState: ProcessListState;

  constructor(
    private readonly client: ApolloClient<any>,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined }
  ) {
    this._ProcessListState = {
      filters: {
        status: [],
        businessKey: [],
      },
      sortBy: {},
    };
  }

  get processListState(): ProcessListState {
    return this._ProcessListState;
  }

  async getProcessInstances(
    offset: number,
    limit: number,
    filters: ProcessInstanceFilter,
    sortBy: ProcessListSortBy
  ): Promise<ProcessInstance[]> {
    return getProcessInstances(offset, limit, filters, sortBy, this.client).then((processInstances) => {
      return processInstances.map((process) => ({
        ...process,
        endpoint: this.options?.transformEndpointBaseUrl?.(process.endpoint) ?? process.endpoint,
        serviceUrl: this.options?.transformEndpointBaseUrl?.(process.serviceUrl) ?? process.serviceUrl,
      }));
    });
  }

  async openProcess(process: ProcessInstance) {
    this.onOpenProcessListeners.forEach((listener) => listener.onOpen(process));
    return Promise.resolve();
  }

  async initialLoad(filter: ProcessInstanceFilter, sortBy: ProcessListSortBy) {
    this._ProcessListState.filters = filter;
    this._ProcessListState.sortBy = sortBy;
    return Promise.resolve();
  }

  async applyFilter(filter: ProcessInstanceFilter) {
    this.processListState.filters = filter;
    this.onUpdateProcessListStateListeners.forEach((listener) => listener.onUpdate(this.processListState));
    return Promise.resolve();
  }

  async applySorting(sortBy: ProcessListSortBy) {
    this._ProcessListState.sortBy = sortBy;
    this.onUpdateProcessListStateListeners.forEach((listener) => listener.onUpdate(this.processListState));
    return Promise.resolve();
  }

  async handleProcessSkip(processInstance: ProcessInstance) {
    return handleProcessSkip(processInstance, this.client);
  }

  async handleProcessRetry(processInstance: ProcessInstance) {
    return handleProcessRetry(processInstance, this.client);
  }

  async handleProcessAbort(processInstance: ProcessInstance) {
    return handleProcessAbort(processInstance, this.client);
  }

  async handleProcessMultipleAction(processInstances: ProcessInstance[], operationType: OperationType) {
    return handleProcessMultipleAction(processInstances, operationType, this.client);
  }

  async query(offset: number, limit: number) {
    return getProcessInstances(
      offset,
      limit,
      this._ProcessListState.filters,
      this._ProcessListState.sortBy,
      this.client
    ).then((processInstances) => {
      return processInstances.map((process) => ({
        ...process,
        endpoint: this.options?.transformEndpointBaseUrl?.(process.endpoint) ?? process.endpoint,
        serviceUrl: this.options?.transformEndpointBaseUrl?.(process.serviceUrl) ?? process.serviceUrl,
      }));
    });
  }

  async getChildProcessesQuery(rootProcessInstanceId: string) {
    return getChildProcessInstances(rootProcessInstanceId, this.client);
  }

  onOpenProcessListen(listener: OnOpenProcessListener): UnSubscribeHandler {
    this.onOpenProcessListeners.push(listener);

    return {
      unSubscribe: () => {
        const index = this.onOpenProcessListeners.indexOf(listener);
        if (index > -1) {
          this.onOpenProcessListeners.splice(index, 1);
        }
      },
    };
  }

  onUpdateProcessListState(listener: OnUpdateProcessListStateListener): UnSubscribeHandler {
    this.onUpdateProcessListStateListeners.push(listener);

    return {
      unSubscribe: () => {
        const index = this.onUpdateProcessListStateListeners.indexOf(listener);
        if (index > -1) {
          this.onUpdateProcessListStateListeners.splice(index, 1);
        }
      },
    };
  }
}
