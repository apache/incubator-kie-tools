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
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ProcessDefinitionsListChannelApi, ProcessDefinitionsListDriver } from "../api";
import { ProcessDefinition, ProcessDefinitionsFilter } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

export default class ProcessDefinitionsListEnvelopeViewDriver implements ProcessDefinitionsListDriver {
  constructor(private readonly channelApi: MessageBusClientApi<ProcessDefinitionsListChannelApi>) {}
  initialLoad(filter: ProcessDefinitionsFilter): Promise<void> {
    return this.channelApi.requests.processDefinitionsList__initialLoad(filter);
  }
  openProcessDefinitionForm(processDefinition: ProcessDefinition): Promise<void> {
    return this.channelApi.requests.processDefinitionsList__openProcessDefinitionForm(processDefinition);
  }
  applyFilter(filter: ProcessDefinitionsFilter): Promise<void> {
    return this.channelApi.requests.processDefinitionsList__applyFilter(filter);
  }
  getProcessDefinitions(): Promise<ProcessDefinition[]> {
    return this.channelApi.requests.processDefinitionsList__getProcessDefinitions();
  }
  getProcessDefinitionByName(processName: string): Promise<ProcessDefinition> {
    return this.channelApi.requests.processDefinitionsList__getProcessDefinitionByName(processName);
  }
}
