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
import * as path from "path";
import {
  SwfJsonLanguageService,
  SwfLanguageServiceArgs,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { FileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { FsWatchingServiceCatalogRelativeStore } from "../serviceCatalog/fs";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "../serviceCatalog/serviceRegistry";
import { definitelyPosixPath } from "@kie-tools-core/vscode-extension/dist/ConfigurationInterpolation";

export class VsCodeSwfLanguageService {
  private readonly jsonLs: SwfJsonLanguageService;
  private readonly yamlLs: SwfYamlLanguageService;
  private readonly fsWatchingSwfServiceCatalogStore: Map<string, FsWatchingServiceCatalogRelativeStore> = new Map();

  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
    }
  ) {
    const lsArgs = this.getLsArgs();
    this.jsonLs = new SwfJsonLanguageService(lsArgs);
    this.yamlLs = new SwfYamlLanguageService(lsArgs);
  }

  public getLs(fileLanguage: FileLanguage): SwfJsonLanguageService | SwfYamlLanguageService {
    return fileLanguage === FileLanguage.YAML ? this.yamlLs : this.jsonLs;
  }

  private getLsArgs(): Omit<SwfLanguageServiceArgs, "lang"> {
    return {
      fs: {},
      serviceCatalog: {
        global: {
          getServices: async () => {
            return this.args.swfServiceCatalogGlobalStore.storedServices;
          },
        },
        relative: {
          getServices: async (textDocument) => {
            const { specsDirAbsolutePosixPath } = this.getSpecsDirPosixPaths(textDocument);
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
  }

  private getSpecsDirPosixPaths(document: TextDocument) {
    const baseFileAbsolutePosixPath = vscode.Uri.parse(document.uri).path;

    const specsDirAbsolutePosixPath = definitelyPosixPath(
      this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath({
        baseFileAbsolutePosixPath,
      })
    );

    const specsDirRelativePosixPath = definitelyPosixPath(
      path.relative(path.dirname(baseFileAbsolutePosixPath), specsDirAbsolutePosixPath)
    );

    return { specsDirRelativePosixPath, specsDirAbsolutePosixPath };
  }

  public dispose() {
    this.jsonLs.dispose();
    this.yamlLs.dispose();
    return Array.from(this.fsWatchingSwfServiceCatalogStore.values()).forEach((f) => f.dispose());
  }
}
