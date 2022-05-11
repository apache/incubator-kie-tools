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
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceCatalogUser,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { SettingsContextType } from "../../settings/SettingsContext";
import { SwfServiceCatalogStore } from "./SwfServiceCatalogStore";

export class SwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor(
    private readonly args: {
      settings: SettingsContextType;
    }
  ) {}

  public kogitoSwfServiceCatalog_user(): SharedValueProvider<SwfServiceCatalogUser | undefined> {
    return { defaultValue: { username: this.args.settings.serviceAccount.config.clientId } };
  }

  public kogitoSwfServiceCatalog_serviceRegistryUrl(): SharedValueProvider<string | undefined> {
    return { defaultValue: this.args.settings.serviceRegistry.config.coreRegistryApi };
  }

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return { defaultValue: SwfServiceCatalogStore.storedServices };
  }

  public kogitoSwfServiceCatalog_logInToRhhcc(): void {}

  public kogitoSwfServiceCatalog_refresh(): void {
    SwfServiceCatalogStore.refresh(
      this.args.settings.kieSandboxExtendedServices.config.buildUrl(),
      this.args.settings.serviceRegistry.config,
      this.args.settings.serviceAccount.config
    );
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    // this.args.swfServiceCatalogSupportActions.importFunctionFromCompletionItem(args);
  }

  public kogitoSwfServiceCatalog_setupServiceRegistryUrl(): void {
    // vscode.commands.executeCommand(COMMAND_IDS.setupServiceRegistryUrl);
  }
}
