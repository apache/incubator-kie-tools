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

import { RhhccAuthenticationStore } from "./RhhccAuthenticationStore";
import * as vscode from "vscode";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../configuration";
import { askForServiceRegistryUrl } from "../serviceCatalog/rhhccServiceRegistry";
import { RhhccServiceRegistryServiceCatalogStore } from "../serviceCatalog/rhhccServiceRegistry/RhhccServiceRegistryServiceCatalogStore";

export async function setupRhhccAuthenticationStore(args: {
  context: vscode.ExtensionContext;
  configuration: SwfVsCodeExtensionConfiguration;
  rhhccAuthenticationStore: RhhccAuthenticationStore;
  rhhccServiceRegistryServiceCatalogStore: RhhccServiceRegistryServiceCatalogStore;
}) {
  args.context.subscriptions.push(
    vscode.authentication.onDidChangeSessions(async (e) => {
      if (e.provider.id === "redhat-mas-account-auth") {
        await args.rhhccAuthenticationStore.setSession(await getSession());
      }
    })
  );

  args.context.subscriptions.push(
    args.rhhccAuthenticationStore.subscribeToSessionChange(async (session) => {
      if (!session) {
        return args.rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      const configuredServiceRegistryUrl = args.configuration.getConfiguredServiceRegistryUrl();
      if (configuredServiceRegistryUrl) {
        return args.rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      const serviceRegistryUrl = await askForServiceRegistryUrl({ currentValue: configuredServiceRegistryUrl });
      vscode.workspace.getConfiguration().update(CONFIGURATION_SECTIONS.serviceRegistryUrl, serviceRegistryUrl);
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL saved.", 3000);

      return args.rhhccServiceRegistryServiceCatalogStore.refresh();
    })
  );

  await args.rhhccAuthenticationStore.setSession(await getSession());
}

async function getSession() {
  try {
    // This method, although returning a Thenable, can throw an error when there's no Authentication Provider registered.
    return await vscode.authentication.getSession("redhat-mas-account-auth", ["openid"]);
  } catch (e) {
    return undefined;
  }
}
