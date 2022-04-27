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

import * as vscode from "vscode";
import { askForServiceRegistryUrl } from "./rhhccServiceRegistry";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../configuration";
import { COMMAND_IDS } from "../commandIds";

export function setupServiceRegistryIntegrationCommands(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
}) {
  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.loginToRhhcc, () => {
      vscode.authentication.getSession("redhat-mas-account-auth", ["openid"], { createIfNone: true });
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.setupServiceRegistryUrl, async () => {
      const serviceRegistryUrl = await askForServiceRegistryUrl({
        currentValue: args.configuration.getConfiguredServiceRegistryUrl(),
      });

      if (!serviceRegistryUrl) {
        return;
      }

      vscode.workspace.getConfiguration().update(CONFIGURATION_SECTIONS.serviceRegistryUrl, serviceRegistryUrl);
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL saved.", 3000);
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.removeServiceRegistryUrl, () => {
      vscode.workspace.getConfiguration().update(CONFIGURATION_SECTIONS.serviceRegistryUrl, "");
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL removed.", 3000);
    })
  );
}
