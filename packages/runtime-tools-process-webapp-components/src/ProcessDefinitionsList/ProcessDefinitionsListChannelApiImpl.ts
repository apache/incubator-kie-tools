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
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  OnOpenProcessDefinitionListener,
  OnUpdateProcessDefinitionsListStateListener,
  ProcessDefinitionsFilter,
  ProcessDefinitionsListChannelApi,
  ProcessDefinitionsListState,
  UnSubscribeHandler,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDefinitionsList";
import ApolloClient from "apollo-client";
import {
  getProcessDefinitionByName,
  getProcessDefinitions,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export class ProcessDefinitionsListChannelApiImpl implements ProcessDefinitionsListChannelApi {
  private readonly onOpenProcessDefinitionListeners: OnOpenProcessDefinitionListener[] = [];
  private readonly onUpdateProcessDefinitionsListStateListeners: OnUpdateProcessDefinitionsListStateListener[] = [];
  private _processDefinitionsListState: ProcessDefinitionsListState;

  constructor(
    private readonly client: ApolloClient<any>,
    private readonly options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined }
  ) {
    this._processDefinitionsListState = {
      filters: {
        processNames: [],
      },
    };
  }

  get processDefinitionsListState(): ProcessDefinitionsListState {
    return this._processDefinitionsListState;
  }

  processDefinitionsList__initialLoad(filter: ProcessDefinitionsFilter): Promise<void> {
    this.processDefinitionsListState.filters = filter;
    return Promise.resolve();
  }

  processDefinitionsList__openProcessDefinitionForm(processDefinition: ProcessDefinition): void {
    this.onOpenProcessDefinitionListeners.forEach((listener) => listener.onOpen(processDefinition));
  }

  processDefinitionsList__applyFilter(filter: ProcessDefinitionsFilter): Promise<void> {
    this.processDefinitionsListState.filters = filter;
    this.onUpdateProcessDefinitionsListStateListeners.forEach((listener) =>
      listener.onUpdate(this.processDefinitionsListState)
    );
    return Promise.resolve();
  }

  processDefinitionsList__getProcessDefinitions(): Promise<ProcessDefinition[]> {
    return getProcessDefinitions(this.client).then((processDefinitions) => {
      return processDefinitions
        .map((definition) => ({
          ...definition,
          endpoint: this.options?.transformEndpointBaseUrl?.(definition.endpoint) ?? definition.endpoint,
        }))
        .filter(
          (definition) =>
            this._processDefinitionsListState.filters?.processNames?.length === 0 ||
            this._processDefinitionsListState.filters.processNames.includes(definition.processName)
        );
    });
  }

  processDefinitionsList__getProcessDefinitionByName(processName: string): Promise<ProcessDefinition> {
    return getProcessDefinitionByName(processName, this.client).then((definition) => {
      return {
        ...definition,
        endpoint: this.options?.transformEndpointBaseUrl?.(definition.endpoint) ?? definition.endpoint,
      };
    });
  }

  processDefinitionsList__onOpenProcessDefinitionListen(
    listener: OnOpenProcessDefinitionListener
  ): Promise<UnSubscribeHandler> {
    this.onOpenProcessDefinitionListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onOpenProcessDefinitionListeners.indexOf(listener);
      if (index > -1) {
        this.onOpenProcessDefinitionListeners.splice(index, 1);
      }
    };

    return Promise.resolve({
      unSubscribe,
    });
  }

  processDefinitionsList__onUpdateProcessDefinitionsListState(
    listener: OnUpdateProcessDefinitionsListStateListener
  ): Promise<UnSubscribeHandler> {
    this.onUpdateProcessDefinitionsListStateListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onUpdateProcessDefinitionsListStateListeners.indexOf(listener);
      if (index > -1) {
        this.onUpdateProcessDefinitionsListStateListeners.splice(index, 1);
      }
    };

    return Promise.resolve({
      unSubscribe,
    });
  }
}
