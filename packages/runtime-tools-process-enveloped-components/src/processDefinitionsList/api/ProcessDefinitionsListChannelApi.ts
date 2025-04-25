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
import { ProcessDefinitionsFilter, ProcessDefinitionsListState } from "./ProcessDefinitionsListApi";

export interface OnOpenProcessDefinitionListener {
  onOpen: (processDefinition: ProcessDefinition) => void;
}

export interface OnUpdateProcessDefinitionsListStateListener {
  onUpdate: (processDefinitionsListState: ProcessDefinitionsListState) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export interface ProcessDefinitionsListChannelApi {
  processDefinitionsList__initialLoad(filter: ProcessDefinitionsFilter): Promise<void>;
  processDefinitionsList__openProcessDefinitionForm(processDefinition: ProcessDefinition): void;
  processDefinitionsList__applyFilter(filter: ProcessDefinitionsFilter): Promise<void>;
  processDefinitionsList__getProcessDefinitions(): Promise<ProcessDefinition[]>;
  processDefinitionsList__getProcessDefinitionByName(processName: string): Promise<ProcessDefinition>;
  processDefinitionsList__onOpenProcessDefinitionListen(
    listener: OnOpenProcessDefinitionListener
  ): Promise<UnSubscribeHandler>;
  processDefinitionsList__onUpdateProcessDefinitionsListState(
    listener: OnUpdateProcessDefinitionsListStateListener
  ): Promise<UnSubscribeHandler>;
}
