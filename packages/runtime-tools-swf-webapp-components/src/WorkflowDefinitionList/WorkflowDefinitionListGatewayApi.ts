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

import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { getWorkflowDefinitionList } from "@kie-tools/runtime-tools-swf-gateway-api/dist/gatewayApi";

export interface WorkflowDefinitionListGatewayApi {
  getWorkflowDefinitionFilter: () => Promise<string[]>;
  setWorkflowDefinitionFilter: (filter: string[]) => Promise<void>;
  getWorkflowDefinitionsQuery: () => Promise<WorkflowDefinition[]>;
  openWorkflowForm: (workflowDefinition: WorkflowDefinition) => Promise<void>;
  openTriggerCloudEvent: () => void;
  onOpenWorkflowFormListen: (listener: OnOpenWorkflowFormListener) => UnSubscribeHandler;
  onOpenTriggerCloudEventListen: (listener: OnOpenTriggerCloudEventListener) => UnSubscribeHandler;
}

export interface OnOpenWorkflowFormListener {
  onOpen: (workflowDefinition: WorkflowDefinition) => void;
}

export interface OnOpenTriggerCloudEventListener {
  onOpen: () => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class WorkflowDefinitionListGatewayApiImpl implements WorkflowDefinitionListGatewayApi {
  private readonly onOpenWorkflowListeners: OnOpenWorkflowFormListener[] = [];
  private readonly onOpenTriggerCloudEventListeners: OnOpenTriggerCloudEventListener[] = [];

  private readonly baseUrl: string;
  private readonly openApiPath: string;
  private workflowDefinitionFilter: string[] = [];

  constructor(url: string, path: string) {
    this.baseUrl = url;
    this.openApiPath = path;
  }

  getWorkflowDefinitionFilter(): Promise<string[]> {
    return Promise.resolve(this.workflowDefinitionFilter);
  }

  setWorkflowDefinitionFilter(filter: string[]): Promise<void> {
    this.workflowDefinitionFilter = filter;
    return Promise.resolve();
  }

  openWorkflowForm(workflowDefinition: WorkflowDefinition): Promise<void> {
    this.onOpenWorkflowListeners.forEach((listener) => listener.onOpen(workflowDefinition));
    return Promise.resolve();
  }

  onOpenWorkflowFormListen(listener: OnOpenWorkflowFormListener): UnSubscribeHandler {
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

  getWorkflowDefinitionsQuery(): Promise<WorkflowDefinition[]> {
    return getWorkflowDefinitionList(this.baseUrl, this.openApiPath);
  }

  openTriggerCloudEvent(): void {
    this.onOpenTriggerCloudEventListeners.forEach((listener) => listener.onOpen());
  }
}
