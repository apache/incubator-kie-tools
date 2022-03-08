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

import { SwfFunction, SwfService } from "@kie-tools/serverless-workflow-service-catalog/src/api";

interface SwfServiceCatalogApi {
  getServices(): SwfService[];
  getFunctions(serviceId?: string): SwfFunction[];
  getFunctionByOperation(operationId: string): SwfFunction | undefined;
}

class SwfServiceCatalogApiImpl implements SwfServiceCatalogApi {
  constructor(private readonly services: SwfService[] = []) {}

  public getFunctionByOperation(operationId: string): SwfFunction | undefined {
    for (const service of this.services) {
      for (const func of service.functions) {
        if (func.operation === operationId) {
          return func;
        }
      }
    }
    return undefined;
  }

  public getFunctions(serviceId?: string): SwfFunction[] {
    const result: SwfFunction[] = [];

    this.services.forEach((service) => {
      if (!serviceId || (serviceId && service.id === serviceId)) {
        result.push(...service.functions);
      }
    });
    return result;
  }

  public getServices(): SwfService[] {
    return this.services;
  }
}

export class SwfServiceCatalogSingleton {
  private static instance: SwfServiceCatalogApi = new SwfServiceCatalogApiImpl();

  public static get(): SwfServiceCatalogApi {
    return SwfServiceCatalogSingleton.instance;
  }

  public static init(services: SwfService[] = []) {
    SwfServiceCatalogSingleton.instance = new SwfServiceCatalogApiImpl(services);
  }
}
