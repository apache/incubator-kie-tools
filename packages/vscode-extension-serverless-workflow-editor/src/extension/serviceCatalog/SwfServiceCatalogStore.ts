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

import { FsWatchingServiceCatalogStore } from "./fs";
import { RhhccServiceRegistryServiceCatalogStore } from "./rhhccServiceRegistry/RhhccServiceRegistryServiceCatalogStore";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { RhhccAuthenticationStore } from "../rhhcc/RhhccAuthenticationStore";
import * as vscode from "vscode";
import { AuthenticationSession } from "vscode";
import { askForServiceRegistryUrl } from "./rhhccServiceRegistry";

export class SwfServiceCatalogStore {
  private fsSwfServiceCatalogServices: SwfServiceCatalogService[] = [];
  private rhhccServiceRegistriesSwfServiceCatalogServices: SwfServiceCatalogService[] = [];
  private rhhccStoreSubscription: (session: AuthenticationSession | undefined) => void;

  constructor(
    private readonly args: {
      fsWatchingServiceCatalogStore: FsWatchingServiceCatalogStore;
      rhhccServiceRegistryServiceCatalogStore: RhhccServiceRegistryServiceCatalogStore;
      rhhccAuthenticationStore: RhhccAuthenticationStore;
    }
  ) {}

  public async init(callback: (swfServiceCatalogServices: SwfServiceCatalogService[]) => Promise<any>) {
    await this.args.fsWatchingServiceCatalogStore.init({
      onNewServices: (s) => {
        this.fsSwfServiceCatalogServices = s;
        return callback(this.getCombinedSwfServiceCatalogServices());
      },
    });

    await this.args.rhhccServiceRegistryServiceCatalogStore.init({
      onNewServices: (s) => {
        this.rhhccServiceRegistriesSwfServiceCatalogServices = s;
        return callback(this.getCombinedSwfServiceCatalogServices());
      },
    });

    this.rhhccStoreSubscription = this.args.rhhccAuthenticationStore.subscribeToSessionChange(async (session) => {
      if (!session) {
        return this.args.rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      if (this.args.rhhccServiceRegistryServiceCatalogStore.serviceRegistryUrl) {
        return this.args.rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      const serviceRegistryUrl = await askForServiceRegistryUrl({
        currentValue: this.args.rhhccServiceRegistryServiceCatalogStore.serviceRegistryUrl,
      });

      this.args.rhhccServiceRegistryServiceCatalogStore.setServiceRegistryUrl(serviceRegistryUrl);
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL saved.", 3000);
      return this.args.rhhccServiceRegistryServiceCatalogStore.refresh();
    });
  }

  private getCombinedSwfServiceCatalogServices() {
    return [...this.rhhccServiceRegistriesSwfServiceCatalogServices, ...this.fsSwfServiceCatalogServices];
  }

  public async refresh() {
    // Don't need to refresh this.fs because it keeps itself updated with FS Watchers
    return this.args.rhhccServiceRegistryServiceCatalogStore.refresh();
  }

  public dispose() {
    this.args.fsWatchingServiceCatalogStore.dispose();
    this.args.rhhccAuthenticationStore.unsubscribeToSessionChange(this.rhhccStoreSubscription);
  }
}
