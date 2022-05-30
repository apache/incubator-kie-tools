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

import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { ServiceRegistryInfo } from "./ServiceRegistryInfo";
import { getServiceFileNameFromSwfServiceCatalogServiceId, SwfServiceCatalogStore } from "./SwfServiceCatalogStore";

export class EditorSwfLanguageService {
  public readonly ls: SwfJsonLanguageService;

  constructor(args: { serviceRegistryInfo: ServiceRegistryInfo }) {
    this.ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: {
        global: {
          getServices: async () => SwfServiceCatalogStore.storedServices,
        },
        relative: {
          getServices: async (_textDocument) => [],
        },
        getServiceFileNameFromSwfServiceCatalogServiceId: async (swfServiceCatalogServiceId) =>
          getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId),
      },
      config: {
        shouldDisplayRhhccIntegration: async () => false,
        shouldReferenceServiceRegistryFunctionsWithUrls: async () => true,
        getServiceRegistryUrl: () => args.serviceRegistryInfo.url,
        getServiceRegistryAuthInfo: () => args.serviceRegistryInfo.authInfo,
        getSpecsDirPosixPaths: async (_textDocument) => ({
          specsDirRelativePosixPath: "",
          specsDirAbsolutePosixPath: "",
        }),
      },
    });
  }

  public dispose() {
    this.ls.dispose();
  }
}
