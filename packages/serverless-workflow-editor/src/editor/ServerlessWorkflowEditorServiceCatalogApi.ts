/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ServerlessWorkflowChannelApi } from "./ServerlessWorkflowChannelApi";
import { FunctionDefinition, ServiceDefinition } from "@kie-tools/service-catalog/dist/api";
import { KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { ServiceCatalogApi } from "../api/ServiceCatalogApi";

export class ServerlessWorkflowEditorServiceCatalogApi implements ServiceCatalogApi {
  constructor(private readonly envelopeContext: KogitoEditorEnvelopeContextType<ServerlessWorkflowChannelApi>) {}

  public getServiceDefinitions(): Promise<ServiceDefinition[]> {
    return this.envelopeContext.channelApi.requests.kogitoServiceCatalog_getServiceDefinitions();
  }

  public getFunctionDefinitions(serviceId?: string): Promise<FunctionDefinition[]> {
    return this.envelopeContext.channelApi.requests.kogitoServiceCatalog_getFunctionDefinitions();
  }

  public getFunctionDefinitionByOperation(operationId: string): Promise<FunctionDefinition | undefined> {
    return this.envelopeContext.channelApi.requests.kogitoServiceCatalog_getFunctionDefinitionByOperation(operationId);
  }
}
