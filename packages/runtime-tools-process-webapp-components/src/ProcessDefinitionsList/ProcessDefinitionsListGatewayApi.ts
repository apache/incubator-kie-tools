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
  ProcessDefinition,
  ProcessDefinitionsFilter,
  ProcessDefinitionsListState,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ProcessDefinitionsListQueries } from "./ProcessDefinitionsListQueries";

export interface ProcessDefinitionsListGatewayApi {
  processDefinitionsListState: ProcessDefinitionsListState;
  initialLoad: (filter: ProcessDefinitionsFilter) => Promise<void>;
  openProcessDefinitionForm: (processDefinition: ProcessDefinition) => Promise<void>;
  applyFilter: (filter: ProcessDefinitionsFilter) => Promise<void>;
  query(): Promise<ProcessDefinition[]>;
  onOpenProcessDefinitionListen: (listener: OnOpenProcessDefinitionListener) => UnSubscribeHandler;
  onUpdateProcessDefinitionsListState: (listener: OnUpdateProcessDefinitionsListStateListener) => UnSubscribeHandler;
}

export interface OnOpenProcessDefinitionListener {
  onOpen: (processDefinition: ProcessDefinition) => void;
}

export interface OnUpdateProcessDefinitionsListStateListener {
  onUpdate: (processDefinitionsListState: ProcessDefinitionsListState) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}
export class ProcessDefinitionsListGatewayApiImpl implements ProcessDefinitionsListGatewayApi {
  private readonly onOpenProcessDefinitionListeners: OnOpenProcessDefinitionListener[] = [];
  private readonly onUpdateProcessDefinitionsListStateListeners: OnUpdateProcessDefinitionsListStateListener[] = [];
  private readonly queries: ProcessDefinitionsListQueries;
  private _ProcessDefinitionsListState: ProcessDefinitionsListState;

  constructor(queries: ProcessDefinitionsListQueries) {
    this.queries = queries;
    this._ProcessDefinitionsListState = {
      filters: {
        processNames: [],
      },
    };
  }

  get processDefinitionsListState(): ProcessDefinitionsListState {
    return this._ProcessDefinitionsListState;
  }

  openProcessDefinitionForm = (processDefinition: ProcessDefinition): Promise<void> => {
    this.onOpenProcessDefinitionListeners.forEach((listener) => listener.onOpen(processDefinition));
    return Promise.resolve();
  };

  initialLoad = (filter: ProcessDefinitionsFilter): Promise<void> => {
    this.processDefinitionsListState.filters = filter;
    return Promise.resolve();
  };

  applyFilter = (filter: ProcessDefinitionsFilter): Promise<void> => {
    this.processDefinitionsListState.filters = filter;
    this.onUpdateProcessDefinitionsListStateListeners.forEach((listener) =>
      listener.onUpdate(this.processDefinitionsListState)
    );
    return Promise.resolve();
  };

  query(): Promise<ProcessDefinition[]> {
    return new Promise<ProcessDefinition[]>((resolve, reject) => {
      this.queries
        .getProcessDefinitions(this._ProcessDefinitionsListState.filters)
        .then((value) => {
          resolve(value);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }

  onOpenProcessDefinitionListen(listener: OnOpenProcessDefinitionListener): UnSubscribeHandler {
    this.onOpenProcessDefinitionListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onOpenProcessDefinitionListeners.indexOf(listener);
      if (index > -1) {
        this.onOpenProcessDefinitionListeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }

  onUpdateProcessDefinitionsListState(listener: OnUpdateProcessDefinitionsListStateListener): UnSubscribeHandler {
    this.onUpdateProcessDefinitionsListStateListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onUpdateProcessDefinitionsListStateListeners.indexOf(listener);
      if (index > -1) {
        this.onUpdateProcessDefinitionsListStateListeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }
}
