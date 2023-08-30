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
import * as vscode from "vscode";
import { ServiceRegistriesStore } from "./serviceRegistry";

export class SwfServiceCatalogStore {
  private subscriptions: Set<(services: SwfServiceCatalogService[]) => Promise<any>> = new Set();

  constructor(
    private readonly args: {
      serviceRegistriesStore: ServiceRegistriesStore;
    }
  ) {}

  public get storedServices() {
    return this.args.serviceRegistriesStore.storedServices;
  }

  public get isServiceRegistryConfigured() {
    return this.args.serviceRegistriesStore.isConfigured;
  }

  public get shouldLoginServices() {
    return this.args.serviceRegistriesStore.shouldLoginServices;
  }

  public get canRefreshServices() {
    return this.args.serviceRegistriesStore.canRefreshServices;
  }

  public async init() {
    this.args.serviceRegistriesStore.subscribeToNewServices((swfServices) => {
      return Promise.all(Array.from(this.subscriptions).map((subscription) => subscription(this.storedServices)));
    });
    await this.args.serviceRegistriesStore.init();
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
    return this.args.serviceRegistriesStore.refresh();
  }

  public dispose() {
    this.args.serviceRegistriesStore.dispose();
  }
}
