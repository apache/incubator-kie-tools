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

import { FunctionDefinition, ServiceCatalogChannelApi, ServiceDefinition } from "../api";
import { ServiceCatalogRegistry } from "./ServiceCatalogRegistry";

export class ServiceCatalogChannelApiImpl implements ServiceCatalogChannelApi {
  constructor(private readonly registry?: ServiceCatalogRegistry) {}

  public kogitoServiceCatalog_getServiceDefinitions(): Promise<ServiceDefinition[]> {
    if (this.registry) {
      return this.registry.getServiceDefinitions();
    }
    return Promise.resolve([]);
  }

  public kogitoServiceCatalog_getFunctionDefinitions(serviceId?: string): Promise<FunctionDefinition[]> {
    if (this.registry) {
      return this.registry.getFunctionDefinitions(serviceId);
    }
    return Promise.resolve([]);
  }

  public kogitoServiceCatalog_getFunctionDefinitionByOperation(
    operationId: string
  ): Promise<FunctionDefinition | undefined> {
    if (this.registry && operationId) {
      return this.registry.getFunctionDefinition(operationId);
    }
    return Promise.resolve(undefined);
  }
}
