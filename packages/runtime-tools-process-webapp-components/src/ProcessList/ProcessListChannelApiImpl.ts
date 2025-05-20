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
import {
  ProcessListChannelApi,
  OnOpenProcessListener,
  OnUpdateProcessListStateListener,
  ProcessListState,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/processList";

export class ProcessListChannelApiImpl implements ProcessListChannelApi {
  private readonly onOpenProcessListeners: OnOpenProcessListener[] = [];
  private readonly onUpdateProcessListStateListeners: OnUpdateProcessListStateListener[] = [];
  private _processListState: ProcessListState;

  constructor(
    private readonly client: ApolloClient<any>,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined }
  ) {
    this._processListState = {
      filters: {
        status: [],
        businessKey: [],
      },
      sortBy: {},
    };
  }

  async processList__getProcessInstances(
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

  async processList__openProcess(process: ProcessInstance) {
    this.onOpenProcessListeners.forEach((listener) => listener.onOpen(process, this._processListState));
  }

  async processList__initialLoad(filter: ProcessInstanceFilter, sortBy: ProcessListSortBy) {
    this._processListState.filters = filter;
    this._processListState.sortBy = sortBy;
    return Promise.resolve();
  }

  async processList__applyFilter(filter: ProcessInstanceFilter) {
    this._processListState.filters = filter;
    this.onUpdateProcessListStateListeners.forEach((listener) => listener.onUpdate(this._processListState));
    return Promise.resolve();
  }

  async processList__applySorting(sortBy: ProcessListSortBy) {
    this._processListState.sortBy = sortBy;
    this.onUpdateProcessListStateListeners.forEach((listener) => listener.onUpdate(this._processListState));
    return Promise.resolve();
  }

  async processList__handleProcessSkip(processInstance: ProcessInstance) {
    return handleProcessSkip(processInstance, this.client);
  }

  async processList__handleProcessRetry(processInstance: ProcessInstance) {
    return handleProcessRetry(processInstance, this.client);
  }

  async processList__handleProcessAbort(processInstance: ProcessInstance) {
    return handleProcessAbort(processInstance, this.client);
  }

  async processList__handleProcessMultipleAction(processInstances: ProcessInstance[], operationType: OperationType) {
    return handleProcessMultipleAction(processInstances, operationType, this.client);
  }

  async processList__query(offset: number, limit: number) {
    return getProcessInstances(
      offset,
      limit,
      this._processListState.filters,
      this._processListState.sortBy,
      this.client
    ).then((processInstances) => {
      return processInstances.map((process) => ({
        ...process,
        endpoint: this.options?.transformEndpointBaseUrl?.(process.endpoint) ?? process.endpoint,
        serviceUrl: this.options?.transformEndpointBaseUrl?.(process.serviceUrl) ?? process.serviceUrl,
      }));
    });
  }

  async processList__getChildProcessesQuery(rootProcessInstanceId: string) {
    return getChildProcessInstances(rootProcessInstanceId, this.client);
  }

  async processList__onOpenProcessListen(listener: OnOpenProcessListener) {
    this.onOpenProcessListeners.push(listener);

    return Promise.resolve({
      unSubscribe: () => {
        const index = this.onOpenProcessListeners.indexOf(listener);
        if (index > -1) {
          this.onOpenProcessListeners.splice(index, 1);
        }
      },
    });
  }

  async processList__onUpdateProcessListState(listener: OnUpdateProcessListStateListener) {
    this.onUpdateProcessListStateListeners.push(listener);

    return Promise.resolve({
      unSubscribe: () => {
        const index = this.onUpdateProcessListStateListeners.indexOf(listener);
        if (index > -1) {
          this.onUpdateProcessListStateListeners.splice(index, 1);
        }
      },
    });
  }
}
