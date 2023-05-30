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

import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceRegistriesSettings,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SwfServiceCatalogStore } from "./SwfServiceCatalogStore";

export class SwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor(private readonly catalogStore: SwfServiceCatalogStore) {
    this.kogitoSwfServiceCatalog_refresh();
  }

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return { defaultValue: this.catalogStore.services };
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    this.catalogStore.refresh();
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(_args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    // No-op
  }

  public kogitoSwfServiceCatalog_importEventFromCompletionItem(_args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    // No-op
  }

  public kogitoSwfServiceCatalog_serviceRegistriesSettings(): SharedValueProvider<SwfServiceRegistriesSettings> {
    return { defaultValue: { registries: [] } };
  }

  public kogitoSwfServiceCatalog_logInServiceRegistries(): void {
    // No-op
  }

  public kogitoSwfServiceCatalog_setupServiceRegistriesSettings(): void {
    // No-op
  }
}
