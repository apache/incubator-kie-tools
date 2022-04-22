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

import { RhhccServiceRegistryServiceCatalogStore } from "./rhhccServiceRegistry/RhhccServiceRegistryServiceCatalogStore";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import * as vscode from "vscode";

export class SwfServiceCatalogStore {
  private subscriptions: Set<(services: SwfServiceCatalogService[]) => Promise<any>> = new Set();
  private rhhccServiceRegistriesSwfServiceCatalogServices: SwfServiceCatalogService[] = [];

  constructor(
    private readonly args: {
      rhhccServiceRegistryServiceCatalogStore: RhhccServiceRegistryServiceCatalogStore;
    }
  ) {}

  public async init() {
    await this.args.rhhccServiceRegistryServiceCatalogStore.init();
    this.args.rhhccServiceRegistryServiceCatalogStore.subscribeToNewServices((s) => {
      this.rhhccServiceRegistriesSwfServiceCatalogServices = s;
      return Promise.all(Array.from(this.subscriptions).map((subscription) => subscription(this.storedServices)));
    });
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

  public get storedServices() {
    return this.rhhccServiceRegistriesSwfServiceCatalogServices;
  }

  public async refresh() {
    console.info("SWF Service Catalog global store :: updating...");
    const ret = await this.args.rhhccServiceRegistryServiceCatalogStore.refresh();
    console.info("SWF Service Catalog global store :: updated.");
    return ret;
  }

  public dispose() {
    this.args.rhhccServiceRegistryServiceCatalogStore.dispose();
  }
}
