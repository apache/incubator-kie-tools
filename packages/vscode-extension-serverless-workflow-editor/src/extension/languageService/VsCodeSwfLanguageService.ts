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
import * as vscode from "vscode";
import { RhhccAuthenticationStore } from "../rhhcc/RhhccAuthenticationStore";
import { SwfVsCodeExtensionConfiguration } from "../configuration";
import { SwfServiceCatalogStore } from "../serviceCatalog/SwfServiceCatalogStore";
import { posix as posixPath } from "path";
import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { FsWatchingServiceCatalogRelativeStore } from "../serviceCatalog/fs";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "../serviceCatalog/rhhccServiceRegistry";

export class VsCodeSwfLanguageService {
  public readonly ls: SwfJsonLanguageService;
  private readonly fsWatchingSwfServiceCatalogStore: Map<string, FsWatchingServiceCatalogRelativeStore> = new Map();
  constructor(
    private readonly args: {
      rhhccAuthenticationStore: RhhccAuthenticationStore;
      configuration: SwfVsCodeExtensionConfiguration;
      swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
    }
  ) {
    this.ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: {
        global: {
          getServices: async () => {
            return args.swfServiceCatalogGlobalStore.storedServices;
          },
        },
        relative: {
          getServices: async (textDocument) => {
            const specsDirAbsolutePosixPath = this.getSpecsDirPosixPaths(textDocument).specsDirAbsolutePosixPath;
            let swfServiceCatalogRelativeStore = this.fsWatchingSwfServiceCatalogStore.get(specsDirAbsolutePosixPath);
            if (swfServiceCatalogRelativeStore) {
              return swfServiceCatalogRelativeStore.getServices();
            }

            swfServiceCatalogRelativeStore = new FsWatchingServiceCatalogRelativeStore({
              baseFileAbsolutePosixPath: vscode.Uri.parse(textDocument.uri).path,
              configuration: this.args.configuration,
            });

            await swfServiceCatalogRelativeStore.init();
            this.fsWatchingSwfServiceCatalogStore.set(specsDirAbsolutePosixPath, swfServiceCatalogRelativeStore);
            return swfServiceCatalogRelativeStore.getServices();
          },
        },
        getServiceFileNameFromSwfServiceCatalogServiceId: async (swfServiceCatalogServiceId) => {
          return getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId);
        },
      },
      config: {
        shouldDisplayRhhccIntegration: async () => {
          // FIXME: This should take the OS into account as well. RHHCC integration only works on macOS.
          // https://issues.redhat.com/browse/KOGITO-7105
          return vscode.env.uiKind === vscode.UIKind.Desktop;
        },
        shouldReferenceServiceRegistryFunctionsWithUrls: async () => {
          return args.configuration.getConfiguredFlagShouldReferenceServiceRegistryFunctionsWithUrls();
        },
        getServiceRegistryUrl: () => {
          return args.configuration.getConfiguredServiceRegistryUrl();
        },
        getServiceRegistryAuthInfo: () => {
          const session = args.rhhccAuthenticationStore.session;
          return !session ? undefined : { username: session.account.label, token: session.accessToken };
        },
        getSpecsDirPosixPaths: async (textDocument) => {
          return this.getSpecsDirPosixPaths(textDocument);
        },
      },
    });
  }

  private getSpecsDirPosixPaths(document: TextDocument) {
    const baseFileAbsolutePosixPath = vscode.Uri.parse(document.uri).path;

    const specsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath({
      baseFileAbsolutePosixPath,
    });

    const specsDirRelativePosixPath = posixPath.relative(
      posixPath.dirname(baseFileAbsolutePosixPath),
      specsDirAbsolutePosixPath
    );

    return { specsDirRelativePosixPath, specsDirAbsolutePosixPath };
  }

  public dispose() {
    this.ls.dispose();
    return Array.from(this.fsWatchingSwfServiceCatalogStore.values()).forEach((f) => f.dispose());
  }
}
