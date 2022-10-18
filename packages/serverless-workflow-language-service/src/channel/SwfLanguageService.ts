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

import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import * as jsonc from "jsonc-parser";
import { posix as posixPath } from "path";
import { getLanguageService, TextDocument } from "vscode-json-languageservice";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";
import { FileLanguage } from "../api";
import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { findNodesAtLocation } from "./findNodesAtLocation";
import { doRefValidation } from "./refValidation";
import {
  SwfCompletionItemServiceCatalogService,
  SwfLanguageServiceCodeCompletion,
} from "./SwfLanguageServiceCodeCompletion";
import {
  SwfLanguageServiceCodeLenses,
  SwfLanguageServiceCodeLensesFunctionsArgs,
} from "./SwfLanguageServiceCodeLenses";
import { CodeCompletionStrategy, SwfJsonPath, SwfLsNode } from "./types";

export type SwfLanguageServiceConfig = {
  shouldConfigureServiceRegistries: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  shouldServiceRegistriesLogIn: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  canRefreshServices: () => boolean; //TODO: See https://issues.redhat.com/browse/KOGITO-7107
  getSpecsDirPosixPaths: (
    textDocument: TextDocument
  ) => Promise<{ specsDirRelativePosixPath: string; specsDirAbsolutePosixPath: string }>;
  shouldDisplayServiceRegistriesIntegration: () => Promise<boolean>;
  shouldReferenceServiceRegistryFunctionsWithUrls: () => Promise<boolean>;
  shouldIncludeJsonSchemaDiagnostics: () => Promise<boolean>;
};

export type SwfLanguageServiceArgs = {
  fs: {};
  lang: {
    fileLanguage: FileLanguage;
    fileMatch: string[];
  };
  serviceCatalog: {
    global: {
      getServices: () => Promise<SwfServiceCatalogService[]>;
    };
    relative: {
      getServices: (textDocument: TextDocument) => Promise<SwfServiceCatalogService[]>;
    };
    getServiceFileNameFromSwfServiceCatalogServiceId: (
      registryName: string,
      swfServiceCatalogServiceId: string
    ) => Promise<string>;
  };
  config: SwfLanguageServiceConfig;
};

export class SwfLanguageService {
  constructor(private readonly args: SwfLanguageServiceArgs) {}

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: SwfLsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CompletionItem[]> {
    if (!args.rootNode) {
      return args.content.trim().length ? [] : SwfLanguageServiceCodeCompletion.getEmptyFileCodeCompletions(args);
    }

    const doc = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);
    const cursorOffset = doc.offsetAt(args.cursorPosition);

    const currentNode = findNodeAtOffset(args.rootNode, cursorOffset, true);
    if (!currentNode) {
      return [];
    }

    const currentNodeRange: Range = {
      start: doc.positionAt(currentNode.offset),
      end: doc.positionAt(currentNode.offset + currentNode.length),
    };

    const overwriteRange = ["string", "number", "boolean", "null"].includes(currentNode?.type)
      ? currentNodeRange
      : args.cursorWordRange;

    const swfCompletionItemServiceCatalogServices = await Promise.all(
      [
        ...(await this.args.serviceCatalog.global.getServices()),
        ...(await this.args.serviceCatalog.relative.getServices(doc)),
      ].map(async (service) => ({
        ...service,
        functions: await Promise.all(
          service.functions.map(async (func) => ({
            ...func,
            operation: await this.getSwfCompletionItemServiceCatalogFunctionOperation(service, func, doc),
          }))
        ),
      }))
    );

    const matchedCompletions = Array.from(completions.entries()).filter(([path, _]) =>
      args.codeCompletionStrategy.shouldComplete({
        root: args.rootNode,
        node: currentNode,
        path: path,
        content: args.content,
        cursorOffset: cursorOffset,
      })
    );

    const result = await Promise.all(
      matchedCompletions.map(([_, completionItemsDelegate]) => {
        return completionItemsDelegate({
          document: doc,
          cursorPosition: args.cursorPosition,
          currentNode,
          currentNodeRange,
          rootNode: args.rootNode!,
          overwriteRange,
          swfCompletionItemServiceCatalogServices,
          langServiceConfig: this.args.config,
          codeCompletionStrategy: args.codeCompletionStrategy,
        });
      })
    );

    return Promise.resolve(result.flat());
  }

  public async getDiagnostics(args: { content: string; uriPath: string; rootNode: SwfLsNode | undefined }) {
    if (!args.rootNode) {
      return [];
    }

    const textDocument = TextDocument.create(
      args.uriPath,
      `serverless-workflow-${this.args.lang.fileLanguage}`,
      1,
      args.content
    );

    const refValidationResults = doRefValidation({ textDocument, rootNode: args.rootNode });

    if (this.args.lang.fileLanguage === FileLanguage.YAML) {
      //TODO: Include JSON Schema validation for YAML as well. Probably use what the YAML extension uses?
      return refValidationResults;
    }

    const schemaValidationResults = (await this.args.config.shouldIncludeJsonSchemaDiagnostics())
      ? await this.getJsonSchemaDiagnostics(textDocument)
      : [];

    return [...schemaValidationResults, ...refValidationResults];
  }

  private async getJsonSchemaDiagnostics(textDocument: TextDocument) {
    const jsonLs = getLanguageService({
      schemaRequestService: async (uri) => {
        if (uri === SW_SPEC_WORKFLOW_SCHEMA.$id) {
          return JSON.stringify(SW_SPEC_WORKFLOW_SCHEMA);
        } else {
          throw new Error(`Unable to load schema from '${uri}'`);
        }
      },
    });

    jsonLs.configure({
      allowComments: false,
      schemas: [{ fileMatch: this.args.lang.fileMatch, uri: SW_SPEC_WORKFLOW_SCHEMA.$id }],
    });

    const jsonDocument = jsonLs.parseJSONDocument(textDocument);
    return jsonLs.doValidation(textDocument, jsonDocument);
  }

  public async getCodeLenses(args: {
    content: string;
    uri: string;
    rootNode: SwfLsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CodeLens[]> {
    if (!args.content.trim().length) {
      return SwfLanguageServiceCodeLenses.createNewSWF();
    }

    if (!args.rootNode) {
      return [];
    }

    const document = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);

    const displayRhhccIntegration = await this.args.config.shouldDisplayServiceRegistriesIntegration();
    const codeLensesFunctionsArgs: SwfLanguageServiceCodeLensesFunctionsArgs = {
      config: this.args.config,
      document,
      content: args.content,
      rootNode: args.rootNode,
      codeCompletionStrategy: args.codeCompletionStrategy,
    };

    return [
      ...(displayRhhccIntegration ? SwfLanguageServiceCodeLenses.setupServiceRegistries(codeLensesFunctionsArgs) : []),
      ...(displayRhhccIntegration ? SwfLanguageServiceCodeLenses.logInServiceRegistries(codeLensesFunctionsArgs) : []),
      ...(displayRhhccIntegration
        ? SwfLanguageServiceCodeLenses.refreshServiceRegistries(codeLensesFunctionsArgs)
        : []),
      ...SwfLanguageServiceCodeLenses.addFunction(codeLensesFunctionsArgs),
    ];
  }

  public dispose() {
    // empty for now
  }

  private async getSwfCompletionItemServiceCatalogFunctionOperation(
    containingService: SwfServiceCatalogService,
    func: SwfServiceCatalogFunction,
    document: TextDocument
  ): Promise<string> {
    const { specsDirRelativePosixPath } = await this.args.config.getSpecsDirPosixPaths(document);

    if (func.source.type === SwfServiceCatalogFunctionSourceType.LOCAL_FS) {
      const serviceFileName = posixPath.basename(func.source.serviceFileAbsolutePath);
      const serviceFileRelativePosixPath = posixPath.join(specsDirRelativePosixPath, serviceFileName);
      return `${serviceFileRelativePosixPath}#${func.name}`;
    } else if (
      (await this.args.config.shouldReferenceServiceRegistryFunctionsWithUrls()) &&
      containingService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY &&
      func.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
    ) {
      return `${containingService.source.url}#${func.name}`;
    } else if (
      containingService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY &&
      func.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
    ) {
      const serviceFileName = await this.args.serviceCatalog.getServiceFileNameFromSwfServiceCatalogServiceId(
        containingService.source.registry,
        containingService.source.id
      );
      const serviceFileRelativePosixPath = posixPath.join(specsDirRelativePosixPath, serviceFileName);
      return `${serviceFileRelativePosixPath}#${func.name}`;
    } else {
      throw new Error("Unknown Service Catalog function source type");
    }
  }
}

const completions = new Map<
  SwfJsonPath,
  (args: {
    swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
    document: TextDocument;
    cursorPosition: Position;
    currentNode: SwfLsNode;
    overwriteRange: Range;
    currentNodeRange: Range;
    rootNode: SwfLsNode;
    langServiceConfig: SwfLanguageServiceConfig;
    codeCompletionStrategy: CodeCompletionStrategy;
  }) => Promise<CompletionItem[]>
>([
  [["functions", "*"], SwfLanguageServiceCodeCompletion.getFunctionCompletions],
  [["functions", "*", "operation"], SwfLanguageServiceCodeCompletion.getFunctionOperationCompletions],
  [["states", "*", "actions", "*", "functionRef"], SwfLanguageServiceCodeCompletion.getFunctionRefCompletions],
  [
    ["states", "*", "actions", "*", "functionRef", "refName"],
    SwfLanguageServiceCodeCompletion.getFunctionRefRefnameCompletions,
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "arguments"],
    SwfLanguageServiceCodeCompletion.getFunctionRefArgumentsCompletions,
  ],
]);

export function findNodeAtLocation(root: SwfLsNode, path: SwfJsonPath): SwfLsNode | undefined {
  return findNodesAtLocation({ root, path })[0];
}

export function findNodeAtOffset(root: SwfLsNode, offset: number, includeRightBound?: boolean): SwfLsNode | undefined {
  return jsonc.findNodeAtOffset(root as jsonc.Node, offset, includeRightBound) as SwfLsNode;
}
