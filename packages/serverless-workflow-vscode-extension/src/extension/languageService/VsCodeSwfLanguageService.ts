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
import { getFileLanguageOrThrow } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { KogitoEditorDocument } from "@kie-tools-core/vscode-extension/dist/VsCodeKieEditorController";
import { JqExpressionsReadSchemaFromFs } from "../jqExpressionCompletion/fs/JqExpressionsReadSchemaFromFs";
import { removeDuplicatedKeyValuePairs } from "@kie-tools/serverless-workflow-jq-expressions/dist/utils";
export const SWF_YAML_LANGUAGE_ID = "serverless-workflow-yaml";
export const SWF_JSON_LANGUAGE_ID = "serverless-workflow-json";

export class VsCodeSwfLanguageService {
  private readonly jsonLs: SwfJsonLanguageService;
  private readonly yamlLs: SwfYamlLanguageService;
  private readonly swfJsonLs: SwfJsonLanguageService;
  private readonly swfYamlLs: SwfYamlLanguageService;
  private readonly fsWatchingSwfServiceCatalogStore: Map<string, FsWatchingServiceCatalogRelativeStore> = new Map();

  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      swfServiceCatalogGlobalStore: SwfServiceCatalogStore;
    }
  ) {
    const defaultLsArgs = this.getDefaultLsArgs({ shouldIncludeJsonSchemaDiagnostics: async () => false });
    this.jsonLs = new SwfJsonLanguageService(defaultLsArgs);
    this.yamlLs = new SwfYamlLanguageService(defaultLsArgs);
    const swfLanguageLsArgs = this.getDefaultLsArgs({});
    this.swfJsonLs = new SwfJsonLanguageService(swfLanguageLsArgs);
    this.swfYamlLs = new SwfYamlLanguageService(swfLanguageLsArgs);
  }

  public getLs(document: vscode.TextDocument): SwfJsonLanguageService | SwfYamlLanguageService {
    const fileLanguage = getFileLanguageOrThrow(document.fileName);
    if (fileLanguage === FileLanguage.YAML) {
      return document.languageId === SWF_YAML_LANGUAGE_ID ? this.swfYamlLs : this.yamlLs;
    } else if (fileLanguage === FileLanguage.JSON) {
      return document.languageId === SWF_JSON_LANGUAGE_ID ? this.swfJsonLs : this.jsonLs;
    } else {
      throw new Error(`Could not determine LS for ${document.fileName}`);
    }
  }

  public getLsForDiagramEditor(
    document: KogitoEditorDocument["document"]
  ): SwfJsonLanguageService | SwfYamlLanguageService {
    const fileLanguage = getFileLanguageOrThrow(document.uri.path);
    if (fileLanguage === FileLanguage.YAML) {
      return this.yamlLs;
    } else if (fileLanguage === FileLanguage.JSON) {
      return this.jsonLs;
    } else {
      throw new Error(`Could not determine LS for Diagram Editor for ${document.uri.path}`);
    }
  }

  private getDefaultLsArgs(
    configOverrides: Partial<SwfLanguageServiceArgs["config"]>
  ): Omit<SwfLanguageServiceArgs, "lang"> {
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
      jqCompletions: {
        remote: {
          getJqAutocompleteProperties: async (args: {
            textDocument: TextDocument;
            schemaPaths: string[];
          }): Promise<Record<string, string>[]> => {
            const jqExpressionReadSchema = new JqExpressionsReadSchemaFromFs();
            const contentArray = await jqExpressionReadSchema.getContentFromRemoteUrl(args.schemaPaths);
            return removeDuplicatedKeyValuePairs(jqExpressionReadSchema.parseSchemaProperties(contentArray));
          },
        },
        relative: {
          getJqAutocompleteProperties: async (args: {
            textDocument: TextDocument;
            schemaPaths: string[];
          }): Promise<Record<string, string>[]> => {
            const schemaAbsoluteFilePath = args.schemaPaths.map((schemaPath: string) => {
              return this.getSchemaFilePosixPath({ doc: args.textDocument, schemaPath });
            });
            const jqExpressionReadSchema = new JqExpressionsReadSchemaFromFs();
            const contentArray = await jqExpressionReadSchema.getContentFromFs(schemaAbsoluteFilePath);
            return removeDuplicatedKeyValuePairs(jqExpressionReadSchema.parseSchemaProperties(contentArray));
          },
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
        getRoutesDirPosixPaths: async (textDocument) => {
          return this.getRoutesDirPosixPaths(textDocument);
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
        shouldIncludeJsonSchemaDiagnostics: async () => {
          return true;
        },
        ...configOverrides,
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

  private getSchemaFilePosixPath(args: { doc: TextDocument; schemaPath: string }) {
    const baseFileAbsolutePosixPath = vscode.Uri.parse(args.doc.uri).path;
    return baseFileAbsolutePosixPath.replace(/(\/.*)\/.+/, "$1").concat("/", args.schemaPath);
  }

  private getRoutesDirPosixPaths(document: TextDocument) {
    const baseFileAbsolutePosixPath = vscode.Uri.parse(document.uri).path;

    const routesDirAbsolutePosixPath = definitelyPosixPath(
      this.args.configuration.getInterpolatedRoutesDirAbsolutePosixPath({
        baseFileAbsolutePosixPath,
      })
    );

    const routesDirRelativePosixPath = definitelyPosixPath(
      path.relative(path.dirname(baseFileAbsolutePosixPath), routesDirAbsolutePosixPath)
    );

    return { routesDirRelativePosixPath, routesDirAbsolutePosixPath };
  }

  public dispose() {
    this.jsonLs.dispose();
    this.yamlLs.dispose();
    return Array.from(this.fsWatchingSwfServiceCatalogStore.values()).forEach((f) => f.dispose());
  }
}
