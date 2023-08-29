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

import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../../configuration";
import { ServiceRegistryInstanceClient } from "./ServiceRegistryInstanceClient";
import { AuthProvider, AuthProviderFactory } from "./auth";
import * as vscode from "vscode";
import { RedHatAuthExtensionStateStore } from "../../RedHatAuthExtensionStateStore";

export class ServiceRegistriesStore {
  private readonly authProviderFactory: AuthProviderFactory;
  private readonly registryClientStore: Map<ServiceRegistryInstanceClient, SwfServiceCatalogService[]> = new Map();
  private readonly subscriptions: Set<(services: SwfServiceCatalogService[]) => Promise<any>> = new Set();

  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      redhatAuthExtensionStateStore: RedHatAuthExtensionStateStore;
      context: vscode.ExtensionContext;
    }
  ) {
    this.authProviderFactory = new AuthProviderFactory({
      redhatAuthExtensionStateStore: this.args.redhatAuthExtensionStateStore,
    });

    args.context.subscriptions.push(
      vscode.workspace.onDidChangeConfiguration((e) => {
        if (e.affectsConfiguration(CONFIGURATION_SECTIONS.serviceRegistriesSettings)) {
          this.init();
        }
      })
    );
    args.context.subscriptions.push(
      this.args.redhatAuthExtensionStateStore.subscribeToRedHatAuthExtensionStateChange(() => this.init())
    );

    args.context.subscriptions.push(new vscode.Disposable(() => this.dispose()));
  }

  public get storedServices() {
    return [...this.registryClientStore.values()].flat();
  }

  public get isConfigured() {
    return this.registryClientStore.size > 0;
  }

  public get authProviders() {
    const result: AuthProvider[] = [];

    Array.from(this.registryClientStore.keys()).forEach((registryClient) => {
      if (registryClient.autProvider) {
        result.push(registryClient.autProvider);
      }
    });

    return result;
  }

  public get shouldLoginServices() {
    return this.authProviders.find((authProvider) => authProvider.shouldLogin()) !== undefined;
  }

  public get canRefreshServices() {
    return (
      Array.from(this.registryClientStore.keys()).find(
        (registryClient) => !registryClient.autProvider || !registryClient.autProvider.shouldLogin()
      ) !== undefined
    );
  }

  public async init(): Promise<void> {
    this.registryClientStore.clear();

    const registrySettings = this.args.configuration.getServiceRegistrySettings();

    const showMissingExtensionWarn = registrySettings?.registries?.reduce((result, clientSettings) => {
      try {
        const authProvider = this.authProviderFactory.lookupAuthProvider({
          authProvider: clientSettings.authProvider,
          context: this.args.context,
        });

        if (!authProvider) {
          return true;
        }

        const client = new ServiceRegistryInstanceClient({
          name: clientSettings.name,
          url: clientSettings.url,
          authProvider,
        });

        this.registryClientStore.set(client, []);

        authProvider.subscribeToSessionChange(() => this.refreshClientStore(client));

        return result;
      } catch (err) {
        console.log("Couldn't create Service Registry client for: ", clientSettings, err);
      }
    }, false);

    if (showMissingExtensionWarn) {
      this.showMissingExtensionWarn();
    }

    return await this.refresh();
  }

  public subscribeToNewServices(subs: (services: SwfServiceCatalogService[]) => Promise<any>) {
    this.subscriptions.add(subs);
    return new vscode.Disposable(() => {
      this.unsubscribeToNewServices(subs);
    });
  }

  public unsubscribeToNewServices(subs: (services: SwfServiceCatalogService[]) => Promise<any>) {
    this.subscriptions.delete(subs);
  }

  public async refresh() {
    const registryInstanceClients = Array.from(this.registryClientStore.keys());
    const promises = registryInstanceClients.map((registry) => registry.getSwfServiceCatalogServices());

    await Promise.all(promises).then((result) => {
      result.forEach((swfServices, index) => {
        this.registryClientStore.set(registryInstanceClients[index], swfServices);
      });
      this.notifyRefresh();
    });

    return Promise.resolve();
  }

  public dispose() {
    this.registryClientStore.clear();
  }

  private async refreshClientStore(registryClient: ServiceRegistryInstanceClient) {
    const services = await registryClient.getSwfServiceCatalogServices();
    this.registryClientStore.set(registryClient, services);
    this.notifyRefresh();
  }

  private notifyRefresh() {
    this.subscriptions.forEach((subscription) => subscription(this.storedServices));
  }

  private showMissingExtensionWarn() {
    if (this.args.redhatAuthExtensionStateStore.isRedHatAuthExtensionEnabled) {
      return;
    }
    vscode.window.showWarningMessage("Serverless Workflow Editor: Red Hat Authentication extension not available", {
      modal: true,
      detail:
        "Looks like at least one of the configured Service Registries requires Red Hat authentication but the `Red Hat Authentication` extension isn't available. Please check that the extension is installed and enabled.",
    });
  }
}
