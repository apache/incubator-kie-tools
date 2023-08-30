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

import { MessageBusClientApi, SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceRegistriesSettings,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";

export class SwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor(
    private readonly args: {
      channelApi: MessageBusClientApi<SwfServiceCatalogChannelApi>;
      services: SwfServiceCatalogService[];
      serviceRegistriesSettings: SwfServiceRegistriesSettings;
    }
  ) {}

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return {
      defaultValue: this.args.services,
    };
  }

  public kogitoSwfServiceCatalog_serviceRegistriesSettings(): SharedValueProvider<SwfServiceRegistriesSettings> {
    return {
      defaultValue: this.args.serviceRegistriesSettings,
    };
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_refresh.send();
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_importFunctionFromCompletionItem.send(args);
  }

  public kogitoSwfServiceCatalog_importEventFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_importEventFromCompletionItem.send(args);
  }

  public kogitoSwfServiceCatalog_logInServiceRegistries(): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_logInServiceRegistries.send();
  }

  public kogitoSwfServiceCatalog_setupServiceRegistriesSettings(): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_setupServiceRegistriesSettings.send();
  }
}
