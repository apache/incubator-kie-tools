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
import { getProcessDefinitionList } from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export interface ProcessDefinitionListGatewayApi {
  getProcessDefinitionFilter: () => Promise<string[]>;
  setProcessDefinitionFilter: (filter: string[]) => Promise<void>;
  getProcessDefinitionsQuery: () => Promise<ProcessDefinition[]>;
  openProcessForm: (processDefinition: ProcessDefinition) => Promise<void>;
  openTriggerCloudEvent: () => void;
  onOpenProcessFormListen: (listener: OnOpenProcessFormListener) => UnSubscribeHandler;
  onOpenTriggerCloudEventListen: (listener: OnOpenTriggerCloudEventListener) => UnSubscribeHandler;
}

export interface OnOpenProcessFormListener {
  onOpen: (processDefinition: ProcessDefinition) => void;
}

export interface OnOpenTriggerCloudEventListener {
  onOpen: () => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class ProcessDefinitionListGatewayApiImpl implements ProcessDefinitionListGatewayApi {
  private readonly onOpenProcessListeners: OnOpenProcessFormListener[] = [];
  private readonly onOpenTriggerCloudEventListeners: OnOpenTriggerCloudEventListener[] = [];

  private readonly kogitoAppUrl: string;
  private readonly openApiPath: string;
  private processDefinitionFilter: string[] = [];

  constructor(url: string, path: string) {
    this.kogitoAppUrl = url;
    this.openApiPath = path;
  }

  getProcessDefinitionFilter(): Promise<string[]> {
    return Promise.resolve(this.processDefinitionFilter);
  }

  setProcessDefinitionFilter(filter: string[]): Promise<void> {
    this.processDefinitionFilter = filter;
    return Promise.resolve();
  }

  openProcessForm(processDefinition: ProcessDefinition): Promise<void> {
    this.onOpenProcessListeners.forEach((listener) => listener.onOpen(processDefinition));
    return Promise.resolve();
  }

  onOpenProcessFormListen(listener: OnOpenProcessFormListener): UnSubscribeHandler {
    this.onOpenProcessListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onOpenProcessListeners.indexOf(listener);
      if (index > -1) {
        this.onOpenProcessListeners.splice(index, 1);
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

  getProcessDefinitionsQuery(): Promise<ProcessDefinition[]> {
    return getProcessDefinitionList(this.kogitoAppUrl, this.openApiPath);
  }

  openTriggerCloudEvent(): void {
    this.onOpenTriggerCloudEventListeners.forEach((listener) => listener.onOpen());
  }
}
