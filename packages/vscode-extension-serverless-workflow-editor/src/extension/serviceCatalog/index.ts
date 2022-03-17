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

import { SwfServiceCatalogStore } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { FsWatchingServiceCatalogStore } from "./fs";
import {
  getInterpolateSettingsValue,
  settingsTokenKeys,
} from "@kie-tools-core/vscode-extension/dist/SettingsInterpolation";
import { RhhccServiceRegistryServiceCatalogStore } from "./rhhccServiceRegistry";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { RhhccAuthenticationStore } from "../rhhcc/RhhccAuthenticationStore";
import { AuthenticationSession } from "vscode";

export function getSwfServiceCatalogStore(args: {
  filePath: string;
  configuredSpecsDirPath: string;
  rhhccAuthenticationStore: RhhccAuthenticationStore;
}): SwfServiceCatalogStore {
  const interpolatedSpecsDirPath = getInterpolateSettingsValue({
    filePath: args.filePath,
    value: args.configuredSpecsDirPath,
  });

  const specsDirParentPath = args.configuredSpecsDirPath.includes(settingsTokenKeys["${fileDirname}"])
    ? interpolatedSpecsDirPath.substring(interpolatedSpecsDirPath.lastIndexOf("/") + 1)
    : interpolatedSpecsDirPath;

  return new CompositeServiceCatalogStore({
    fs: new FsWatchingServiceCatalogStore(specsDirParentPath, interpolatedSpecsDirPath),
    rhhccServiceRegistry: new RhhccServiceRegistryServiceCatalogStore(args.rhhccAuthenticationStore),
    rhhccAuthenticationStore: args.rhhccAuthenticationStore,
  });
}

export class CompositeServiceCatalogStore implements SwfServiceCatalogStore {
  private fsSwfServiceCatalogServices: SwfServiceCatalogService[] = [];
  private rhhccServiceRegistriesSwfServiceCatalogServices: SwfServiceCatalogService[] = [];
  private rhhccStoreSubscription: (session: AuthenticationSession | undefined) => void;

  constructor(
    private readonly args: {
      fs: FsWatchingServiceCatalogStore;
      rhhccServiceRegistry: RhhccServiceRegistryServiceCatalogStore;
      rhhccAuthenticationStore: RhhccAuthenticationStore;
    }
  ) {}

  public async init(callback: (swfServiceCatalogServices: SwfServiceCatalogService[]) => Promise<any>) {
    await this.args.fs.init((s) => {
      this.fsSwfServiceCatalogServices = s;
      return callback(this.getCombinedSwfServiceCatalogServices());
    });

    await this.args.rhhccServiceRegistry.init((s) => {
      this.rhhccServiceRegistriesSwfServiceCatalogServices = s;
      return callback(this.getCombinedSwfServiceCatalogServices());
    });

    this.rhhccStoreSubscription = this.args.rhhccAuthenticationStore.subscribe(() => {
      return this.args.rhhccServiceRegistry.refresh();
    });
  }

  private getCombinedSwfServiceCatalogServices() {
    return [...this.rhhccServiceRegistriesSwfServiceCatalogServices, ...this.fsSwfServiceCatalogServices];
  }

  public async refresh() {
    // Don't need to refresh this.fs because it keeps itself updated with FS Watchers
    return this.args.rhhccServiceRegistry.refresh();
  }

  public dispose() {
    this.args.fs.dispose();
    this.args.rhhccAuthenticationStore.unsubscribe(this.rhhccStoreSubscription);
  }
}
