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
import { SwfVsCodeExtensionConfiguration } from "../configuration";
import { SwfServiceCatalogStore } from "../serviceCatalog/SwfServiceCatalogStore";
import { posix as posixPath } from "path";
import {
  SwfJsonLanguageService,
  SwfYamlLanguageService,
  SwfLanguageService,
  SwfLanguageServiceArgs,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { FileLanguage, getFileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/editor";
import { FsWatchingServiceCatalogRelativeStore } from "../serviceCatalog/fs";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "../serviceCatalog/serviceRegistry";

export class VsCodeSwfLanguageService {
  private _ls: SwfLanguageService;
  private readonly fsWatchingSwfServiceCatalogStore: Map<string, FsWatchingServiceCatalogRelativeStore> = new Map();
  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
    }
  ) {
    vscode.workspace.onDidOpenTextDocument((document) => {
      this.reinitLs(document);
    });

    this.reinitLs(vscode.window.activeTextEditor?.document);
  }

  public get ls(): SwfLanguageService {
    return this._ls;
  }

  private reinitLs(document: vscode.TextDocument | undefined) {
    if (!document) {
      return;
    }
    const filePath = document.uri.path;
    const fileLanguage = getFileLanguage(filePath);
    const lsArgs: SwfLanguageServiceArgs = {
      fs: {},
      serviceCatalog: {
        global: {
          getServices: async () => {
            return this.args.swfServiceCatalogGlobalStore.storedServices;
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
        getServiceFileNameFromSwfServiceCatalogServiceId: async (registryName, swfServiceCatalogServiceId) => {
          return getServiceFileNameFromSwfServiceCatalogServiceId(registryName, swfServiceCatalogServiceId);
        },
      },
      config: {
        shouldDisplayServiceRegistriesIntegration: async () => {
          // FIXME: This should take the OS into account as well. RHHCC integration only works on macOS.
          // https://issues.redhat.com/browse/KOGITO-7105
          return vscode.env.uiKind === vscode.UIKind.Desktop;
        },
        shouldReferenceServiceRegistryFunctionsWithUrls: async () => {
          return this.args.configuration.getConfiguredFlagShouldReferenceServiceRegistryFunctionsWithUrls();
        },
        getSpecsDirPosixPaths: async (textDocument) => {
          return this.getSpecsDirPosixPaths(textDocument);
        },
        shouldConfigureServiceRegistries: () => {
          return !this.args.swfServiceCatalogGlobalStore.isServiceRegistryConfigured;
        },
        shouldServiceRegistriesLogIn: () => {
          return this.args.swfServiceCatalogGlobalStore.shouldLoginServices;
        },
        canRefreshServices: () => {
          return this.args.swfServiceCatalogGlobalStore.canRefreshServices;
        },
      },
    };

    if (fileLanguage === FileLanguage.JSON) {
      this._ls = new SwfJsonLanguageService(lsArgs);
    } else if (fileLanguage === FileLanguage.YAML) {
      this._ls = new SwfYamlLanguageService(lsArgs);
    }
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
