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

import { TextDocument } from "vscode-languageserver-textdocument";
import path, { posix as posixPath } from "path";
import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { getServiceFileNameFromSwfServiceCatalogServiceId, SwfServiceCatalogStore } from "./SwfServiceCatalogStore";
import { SettingsContextType } from "../../settings/SettingsContext";

export class EditorSwfLanguageService {
  public readonly ls: SwfJsonLanguageService;
  constructor(settings: SettingsContextType) {
    this.ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: {
        global: {
          getServices: async () => {
            return SwfServiceCatalogStore.storedServices;
          },
        },
        relative: {
          getServices: async (textDocument) => {
            // const specsDirAbsolutePosixPath = this.getSpecsDirPosixPaths(textDocument).specsDirAbsolutePosixPath;
            // let swfServiceCatalogRelativeStore = this.fsWatchingSwfServiceCatalogStore.get(specsDirAbsolutePosixPath);
            // if (swfServiceCatalogRelativeStore) {
            //   return swfServiceCatalogRelativeStore.getServices();
            // }

            // swfServiceCatalogRelativeStore = new FsWatchingServiceCatalogRelativeStore({
            //   baseFileAbsolutePosixPath: vscode.Uri.parse(textDocument.uri).path,
            //   configuration: this.args.configuration,
            // });

            // await swfServiceCatalogRelativeStore.init();
            // this.fsWatchingSwfServiceCatalogStore.set(specsDirAbsolutePosixPath, swfServiceCatalogRelativeStore);
            // return swfServiceCatalogRelativeStore.getServices();
            return [];
            // return SwfServiceCatalogStore.storedServices;
          },
        },
        getServiceFileNameFromSwfServiceCatalogServiceId: async (swfServiceCatalogServiceId) => {
          return getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId);
        },
      },
      config: {
        shouldDisplayRhhccIntegration: async () => {
          return Promise.resolve(false);
        },
        shouldReferenceServiceRegistryFunctionsWithUrls: async () => {
          return Promise.resolve(true);
        },
        getServiceRegistryUrl: () => {
          return settings.serviceRegistry.config.coreRegistryApi;
        },
        getServiceRegistryAuthInfo: () => {
          return {
            username: settings.serviceAccount.config.clientId,
            token: settings.serviceAccount.config.clientSecret,
          };
        },
        getSpecsDirPosixPaths: async (textDocument) => {
          return {
            specsDirRelativePosixPath: "",
            specsDirAbsolutePosixPath: "",
          };
        },
      },
    });
  }

  private getSpecsDirPosixPaths(document: TextDocument) {
    const baseFileAbsolutePosixPath = path.parse(document.uri).dir;

    const specsDirAbsolutePosixPath = baseFileAbsolutePosixPath;

    const specsDirRelativePosixPath = posixPath.relative(
      posixPath.dirname(baseFileAbsolutePosixPath),
      specsDirAbsolutePosixPath
    );

    return { specsDirRelativePosixPath, specsDirAbsolutePosixPath };
  }

  public dispose() {
    this.ls.dispose();
    // return Array.from(this.fsWatchingSwfServiceCatalogStore.values()).forEach((f) => f.dispose());
  }
}
