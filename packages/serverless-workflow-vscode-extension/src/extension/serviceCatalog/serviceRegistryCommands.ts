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

import * as vscode from "vscode";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../configuration";
import { COMMAND_IDS } from "../commandIds";
import { ServiceRegistriesStore } from "./serviceRegistry";

export function setupServiceRegistryIntegrationCommands(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  serviceRegistryStore: ServiceRegistriesStore;
}) {
  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.serviceRegistriesLogin, () => {
      args.serviceRegistryStore.authProviders
        .filter((authProvider) => authProvider.shouldLogin())
        .forEach(async (authProvider) => authProvider.login());
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.serviceRegistriesRefresh, () => {
      vscode.window.setStatusBarMessage("Serverless Workflow Editor: Refreshing...");
      args.serviceRegistryStore.refresh().then(() => vscode.window.setStatusBarMessage(""));
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.serviceRegistriesConfig, () => {
      vscode.commands.executeCommand(
        "workbench.action.openSettings",
        `@id:${CONFIGURATION_SECTIONS.serviceRegistriesSettings}`
      );
    })
  );
}
