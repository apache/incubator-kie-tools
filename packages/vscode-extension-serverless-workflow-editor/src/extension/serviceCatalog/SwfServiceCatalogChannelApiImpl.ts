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
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogUser,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import * as vscode from "vscode";
import { Uri } from "vscode";
import * as path from "path";
import { SwfServiceCatalogStore } from "./SwfServiceCatalogStore";
import { COMMAND_IDS } from "../commands";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "./rhhccServiceRegistry";
import { SwfVsCodeExtensionConfiguration } from "../configuration";

const encoder = new TextEncoder();

export class SwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor(
    private readonly args: {
      settings: SwfVsCodeExtensionConfiguration;
      baseFileAbsolutePath: string;
      defaultUser: SwfServiceCatalogUser | undefined;
      defaultServiceRegistryUrl: string | undefined;
      swfServiceCatalogStore: SwfServiceCatalogStore;
    }
  ) {}

  public kogitoSwfServiceCatalog_user(): SharedValueProvider<SwfServiceCatalogUser | undefined> {
    return { defaultValue: this.args.defaultUser };
  }

  public kogitoSwfServiceCatalog_serviceRegistryUrl(): SharedValueProvider<string | undefined> {
    return { defaultValue: this.args.defaultServiceRegistryUrl };
  }

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return { defaultValue: [] };
  }

  public kogitoSwfServiceCatalog_logInToRhhcc(): void {
    vscode.commands.executeCommand(COMMAND_IDS.loginToRhhcc);
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    vscode.window.setStatusBarMessage("Serverless Workflow Editor: Refreshing...");
    this.args.swfServiceCatalogStore.refresh().then(() => vscode.window.setStatusBarMessage(""));
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(containingService: SwfServiceCatalogService): void {
    if (containingService.source.type === SwfServiceCatalogServiceSourceType.LOCAL_FS) {
      return;
    }

    const serviceFileName = getServiceFileNameFromSwfServiceCatalogServiceId(containingService.source.id);
    const specsDirAbsolutePath = this.args.settings.getInterpolatedSpecsDirPath(this.args);

    const serviceFileAbsolutePath = path.join(specsDirAbsolutePath, serviceFileName);
    vscode.workspace.fs.writeFile(Uri.parse(serviceFileAbsolutePath), encoder.encode(containingService.rawContent));
    vscode.window.showInformationMessage(`Wrote ${serviceFileAbsolutePath}.`);
  }

  public kogitoSwfServiceCatalog_setupServiceRegistryUrl(): void {
    vscode.commands.executeCommand(COMMAND_IDS.setupServiceRegistryUrl);
  }
}
