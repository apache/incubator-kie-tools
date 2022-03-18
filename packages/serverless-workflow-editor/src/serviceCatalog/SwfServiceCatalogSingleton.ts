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

import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogService,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SwfServiceCatalogUser } from "@kie-tools/serverless-workflow-service-catalog/src/api";

interface SwfServiceCatalogApi {
  getServices(): SwfServiceCatalogService[];
  getUser(): SwfServiceCatalogUser | undefined;
  getFunctions(serviceId?: string): SwfServiceCatalogFunction[];
  getFunctionByOperation(operationId: string): SwfServiceCatalogFunction | undefined;
}

class SwfServiceCatalogApiImpl implements SwfServiceCatalogApi {
  constructor(
    private readonly services: SwfServiceCatalogService[] = [],
    private readonly user: SwfServiceCatalogUser | undefined = undefined
  ) {}

  public getFunctionByOperation(operation: string): SwfServiceCatalogFunction | undefined {
    for (const swfServiceCatalogService of this.services) {
      for (const swfServiceCatalogFunction of swfServiceCatalogService.functions) {
        if (swfServiceCatalogFunction.operation === operation) {
          return swfServiceCatalogFunction;
        }
      }
    }

    return undefined;
  }

  public getFunctions(): SwfServiceCatalogFunction[] {
    return this.services.flatMap((service) => service.functions);
  }

  public getServices() {
    return this.services;
  }

  public getUser() {
    return this.user;
  }
}

export class SwfServiceCatalogSingleton {
  private static instance = new SwfServiceCatalogApiImpl();

  public static get(): SwfServiceCatalogApi {
    return SwfServiceCatalogSingleton.instance;
  }

  public static init(services: SwfServiceCatalogService[] = [], user: SwfServiceCatalogUser | undefined) {
    SwfServiceCatalogSingleton.instance = new SwfServiceCatalogApiImpl(services, user);
  }
}
