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
  SwfServiceRegistriesSettings,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import * as vscode from "vscode";
import { COMMAND_IDS } from "../commandIds";
import { SwfVsCodeExtensionConfiguration } from "../configuration";
import { SwfServiceCatalogSupportActions } from "./SwfServiceCatalogSupportActions";

export class SwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      baseFileAbsolutePosixPath: string;
      swfServiceCatalogSupportActions: SwfServiceCatalogSupportActions;
    }
  ) {}

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return { defaultValue: [] };
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    vscode.commands.executeCommand(COMMAND_IDS.serviceRegistriesRefresh);
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.args.swfServiceCatalogSupportActions.importFunctionFromCompletionItem(args);
  }

  public kogitoSwfServiceCatalog_importEventFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.args.swfServiceCatalogSupportActions.importEventFromCompletionItem(args);
  }

  public kogitoSwfServiceCatalog_logInServiceRegistries(): void {
    vscode.commands.executeCommand(COMMAND_IDS.serviceRegistriesLogin);
  }

  public kogitoSwfServiceCatalog_serviceRegistriesSettings(): SharedValueProvider<SwfServiceRegistriesSettings> {
    return { defaultValue: this.args.configuration.getServiceRegistrySettings() };
  }

  public kogitoSwfServiceCatalog_setupServiceRegistriesSettings(): void {
    vscode.commands.executeCommand(COMMAND_IDS.serviceRegistriesConfig);
  }
}
