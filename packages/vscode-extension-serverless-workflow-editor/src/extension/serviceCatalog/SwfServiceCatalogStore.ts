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

export class SwfServiceCatalogStore {
  private fsSwfServiceCatalogServices: SwfServiceCatalogService[] = [];
  private rhhccServiceRegistriesSwfServiceCatalogServices: SwfServiceCatalogService[] = [];

  constructor(
    private readonly args: {
      fsWatchingServiceCatalogStore: FsWatchingServiceCatalogStore;
      rhhccServiceRegistryServiceCatalogStore: RhhccServiceRegistryServiceCatalogStore;
    }
  ) {}

  public async init(args: {
    onNewServices: (newSwfServiceCatalogServices: SwfServiceCatalogService[]) => Promise<any>;
  }) {
    await this.args.fsWatchingServiceCatalogStore.init({
      onNewServices: (s) => {
        this.fsSwfServiceCatalogServices = s;
        return args.onNewServices(this.getCombinedSwfServiceCatalogServices());
      },
    });

    await this.args.rhhccServiceRegistryServiceCatalogStore.init({
      onNewServices: (s) => {
        this.rhhccServiceRegistriesSwfServiceCatalogServices = s;
        return args.onNewServices(this.getCombinedSwfServiceCatalogServices());
      },
    });
  }

  private getCombinedSwfServiceCatalogServices() {
    return [...this.rhhccServiceRegistriesSwfServiceCatalogServices, ...this.fsSwfServiceCatalogServices];
  }

  public async refresh() {
    return this.args.rhhccServiceRegistryServiceCatalogStore.refresh();
  }

  public dispose() {
    this.args.fsWatchingServiceCatalogStore.dispose();
    this.args.rhhccServiceRegistryServiceCatalogStore.dispose();
  }
}
