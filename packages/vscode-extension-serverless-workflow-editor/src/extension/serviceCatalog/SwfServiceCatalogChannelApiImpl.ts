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
  SwfServiceCatalogFunction,
  SwfServiceCatalogService,
  SwfServiceCatalogUser,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { SwfServiceCatalogStore } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import * as vscode from "vscode";

export class SwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor(
    private readonly args: {
      defaultUser: SwfServiceCatalogUser | undefined;
      swfServiceCatalogStore: SwfServiceCatalogStore;
    }
  ) {}

  public kogitoSwfServiceCatalog_user(): SharedValueProvider<SwfServiceCatalogUser | undefined> {
    return { defaultValue: this.args.defaultUser };
  }

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return { defaultValue: [] };
  }

  public kogitoSwfServiceCatalog_logInToRhhcc(): void {
    vscode.commands.executeCommand("extension.kogito.swf.logInToRhhcc");
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    vscode.window.setStatusBarMessage(
      "Serverless Workflow Editor: Refreshing Service Catalog using Service Registries from Red Hat Hybrid Cloud Console...",
      3000
    );
    return this.args.swfServiceCatalogStore.refresh();
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(
    service: SwfServiceCatalogService,
    importedFunction: SwfServiceCatalogFunction
  ): void {
    vscode.window.showInformationMessage(JSON.stringify(service) + JSON.stringify(importedFunction));
  }
}
