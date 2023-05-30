/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { MessageBusClientApi, SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceRegistriesSettings,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";

export class NoOpSwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor() {}

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return {
      defaultValue: [],
    };
  }

  public kogitoSwfServiceCatalog_serviceRegistriesSettings(): SharedValueProvider<SwfServiceRegistriesSettings> {
    return {
      defaultValue: {
        registries: [],
      },
    };
  }

  public kogitoSwfServiceCatalog_refresh(): void {}

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {}

  public kogitoSwfServiceCatalog_importEventFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {}

  public kogitoSwfServiceCatalog_logInServiceRegistries(): void {}

  public kogitoSwfServiceCatalog_setupServiceRegistriesSettings(): void {}
}
