/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import * as jsonc from "jsonc-parser";
import { posix as posixPath } from "path";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, Diagnostic, DiagnosticSeverity, Position, Range } from "vscode-languageserver-types";
import { FileLanguage } from "../api";
import {
  EditorLanguageServiceCodeCompletionFunctionsArgs,
  ELsCompletionsMap,
} from "./EditorLanguageServiceCodeCompletion";
import { findNodesAtLocation } from "./findNodesAtLocation";
import { ELsCodeCompletionStrategy, ELsJsonPath, ELsNode } from "./types";

export type EditorLanguageServiceArgs = {
  fs: {};
  lang: {
    fileLanguage: FileLanguage;
    fileMatch: string[];
  };
};

export class EditorLanguageService {
  constructor(private readonly args: EditorLanguageServiceArgs) {}

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: ELsCodeCompletionStrategy;
    completions: ELsCompletionsMap<EditorLanguageServiceCodeCompletionFunctionsArgs>;
    extraCompletionFunctionsArgs: {};
  }): Promise<CompletionItem[]> {
    const doc = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);
    const cursorOffset = doc.offsetAt(args.cursorPosition);
    if (!args.rootNode) {
      return [];
    }

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

    const matchedCompletions = Array.from(args.completions.entries()).filter(([path, _]) =>
      args.codeCompletionStrategy.shouldComplete({
        content: args.content,
        cursorOffset: cursorOffset,
        cursorPosition: args.cursorPosition,
        node: currentNode,
        path,
        root: args.rootNode,
      })
    );
    const result = await Promise.all(
      matchedCompletions.map(([_, completionItemsDelegate]) => {
        return completionItemsDelegate({
          codeCompletionStrategy: args.codeCompletionStrategy,
          currentNode,
          currentNodeRange,
          cursorOffset,
          cursorPosition: args.cursorPosition,
          document: doc,
          overwriteRange,
          rootNode: args.rootNode!,
          ...args.extraCompletionFunctionsArgs,
        });
      })
    );
    return Promise.resolve(result.flat());
  }

  // private getFunctionDiagnostics(services: SwfServiceCatalogService[]): Diagnostic[] {
  //   return services.flatMap((value) => this.generateDiagnostic(value.functions));
  // }
  //
  // private generateDiagnostic(serviceCatalogFunctions: SwfServiceCatalogFunction[]): Diagnostic[] {
  //   const functionsWithoutName = serviceCatalogFunctions.filter((fs) => !fs.name;
  //
  //   return functionsWithoutName.length >= 1
  //     ? [
  //         Diagnostic.create(
  //           Range.create(Position.create(0, 0), Position.create(0, 0)),
  //           this.getWarningMessage(serviceCatalogFunctions[0].source),
  //           DiagnosticSeverity.Warning
  //         ),
  //       ]
  //     : [];
  // }
  //
  // private getWarningMessage(swfServiceCatalogFunctionSource: SwfServiceCatalogFunctionSource): string {
  //   if (swfServiceCatalogFunctionSource.type == "SERVICE_REGISTRY") {
  //     return `The ${swfServiceCatalogFunctionSource.serviceId} service in the  ${swfServiceCatalogFunctionSource.registry} registry is missing the "operationId" property in at least one operation`;
  //   }
  //   if (swfServiceCatalogFunctionSource.type === "LOCAL_FS") {
  //     return `The ${swfServiceCatalogFunctionSource.serviceFileAbsolutePath} service is missing the "operationId" property in at least one operation`;
  //   }
  //   return "";
  // }
  //
  // public async getDiagnostics(args: {
  //   content: string;
  //   uriPath: string;
  //   rootNode: ELsNode | undefined;
  //   getSchemaDiagnostics: (textDocument: TextDocument, fileMatch: string[]) => Promise<Diagnostic[]>;
  // }): Promise<Diagnostic[]> {
  //   if (!args.rootNode) {
  //     return [];
  //   }
  //
  //   // this ensure the document is validated again
  //   const docVersion = Math.floor(Math.random() * 1000);
  //
  //   const textDocument = TextDocument.create(
  //     args.uriPath,
  //     `serverless-workflow-${this.args.lang.fileLanguage}`,
  //     docVersion,
  //     args.content
  //   );
  //   const refValidationResults = doRefValidation({ textDocument, rootNode: args.rootNode, validationMap: swfRefValidationMap });
  //   const schemaValidationResults = (await this.args.config.shouldIncludeJsonSchemaDiagnostics())
  //     ? await args.getSchemaDiagnostics(textDocument, this.args.lang.fileMatch)
  //     : [];
  //
  //   const doc = TextDocument.create(args.uriPath, this.args.lang.fileLanguage, 0, args.content);
  //   const globalServices = await this.args.serviceCatalog.global.getServices();
  //   const relativeServices = await this.args.serviceCatalog.relative.getServices(doc);
  //   return [
  //     ...schemaValidationResults,
  //     ...refValidationResults,
  //     ...this.getFunctionDiagnostics([...globalServices, ...relativeServices]),
  //   ];
  // }
  //
  // public async getCodeLenses(args: {
  //   content: string;
  //   uri: string;
  //   rootNode: ELsNode | undefined;
  //   codeCompletionStrategy: ELsCodeCompletionStrategy;
  // }): Promise<CodeLens[]> {
  //   if (!args.content.trim().length) {
  //     return SwfLanguageServiceCodeLenses.createNewSWF();
  //   }
  //
  //   if (!args.rootNode) {
  //     return [];
  //   }
  //
  //   const document = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);
  //
  //   const displayRhhccIntegration = await this.args.config.shouldDisplayServiceRegistriesIntegration();
  //   const codeLensesFunctionsArgs: SwfLanguageServiceCodeLensesFunctionsArgs = {
  //     config: this.args.config,
  //     document,
  //     content: args.content,
  //     rootNode: args.rootNode,
  //     codeCompletionStrategy: args.codeCompletionStrategy,
  //   };
  //
  //   return [
  //     ...(displayRhhccIntegration ? SwfLanguageServiceCodeLenses.setupServiceRegistries(codeLensesFunctionsArgs) : []),
  //     ...(displayRhhccIntegration ? SwfLanguageServiceCodeLenses.logInServiceRegistries(codeLensesFunctionsArgs) : []),
  //     ...(displayRhhccIntegration
  //       ? SwfLanguageServiceCodeLenses.refreshServiceRegistries(codeLensesFunctionsArgs)
  //       : []),
  //     ...SwfLanguageServiceCodeLenses.addFunction(codeLensesFunctionsArgs),
  //     ...SwfLanguageServiceCodeLenses.addEvent(codeLensesFunctionsArgs),
  //     ...SwfLanguageServiceCodeLenses.addState(codeLensesFunctionsArgs),
  //   ];
  // }
  //
  // public dispose() {
  //   // empty for now
  // }
  //
  // private async getSwfCompletionItemServiceCatalogFunctionOperation(
  //   containingService: SwfServiceCatalogService,
  //   func: SwfServiceCatalogFunction,
  //   document: TextDocument
  // ): Promise<string> {
  //   const { specsDirRelativePosixPath } = await this.args.config.getSpecsDirPosixPaths(document);
  //   const { routesDirRelativePosixPath } = await this.args.config.getRoutesDirPosixPaths(document);
  //
  //   let dirRelativePosixPath;
  //
  //   if (containingService.type === SwfServiceCatalogServiceType.camelroute) {
  //     dirRelativePosixPath = routesDirRelativePosixPath;
  //   } else {
  //     dirRelativePosixPath = specsDirRelativePosixPath;
  //   }
  //
  //   if (func.source.type === SwfCatalogSourceType.LOCAL_FS) {
  //     const serviceFileName = posixPath.basename(func.source.serviceFileAbsolutePath);
  //     const serviceFileRelativePosixPath = posixPath.join(dirRelativePosixPath, serviceFileName);
  //     return `${serviceFileRelativePosixPath}#${func.name}`;
  //   } else if (
  //     (await this.args.config.shouldReferenceServiceRegistryFunctionsWithUrls()) &&
  //     containingService.source.type === SwfCatalogSourceType.SERVICE_REGISTRY &&
  //     func.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
  //   ) {
  //     return `${containingService.source.url}#${func.name}`;
  //   } else if (
  //     containingService.source.type === SwfCatalogSourceType.SERVICE_REGISTRY &&
  //     func.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
  //   ) {
  //     const serviceFileName = await this.args.serviceCatalog.getServiceFileNameFromSwfServiceCatalogServiceId(
  //       containingService.source.registry,
  //       containingService.source.id
  //     );
  //     const serviceFileRelativePosixPath = posixPath.join(dirRelativePosixPath, serviceFileName);
  //     return `${serviceFileRelativePosixPath}#${func.name}`;
  //   } else {
  //     throw new Error("Unknown Service Catalog function source type");
  //   }
  // }
}

// const completions = new Map<
//   ELsJsonPath,
//   (args: {
//     codeCompletionStrategy: ELsCodeCompletionStrategy;
//     currentNode: ELsNode;
//     currentNodeRange: Range;
//     cursorOffset: number;
//     cursorPosition: Position;
//     document: TextDocument;
//     langServiceConfig: SwfLanguageServiceConfig;
//     overwriteRange: Range;
//     rootNode: ELsNode;
//     swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
//     jqCompletions: JqCompletions;
//   }) => Promise<CompletionItem[]>
// >([
//   [["start"], SwfLanguageServiceCodeCompletion.getStartCompletions],
//   [["functions", "*"], SwfLanguageServiceCodeCompletion.getFunctionCompletions],
//   [["functions", "*", "operation"], SwfLanguageServiceCodeCompletion.getFunctionOperationCompletions],
//   [["events", "*"], SwfLanguageServiceCodeCompletion.getEventsCompletions],
//   [["states", "*"], SwfLanguageServiceCodeCompletion.getStatesCompletions],
//   [["states", "*", "actions", "*", "functionRef"], SwfLanguageServiceCodeCompletion.getFunctionRefCompletions],
//   [
//     ["states", "*", "actions", "*", "functionRef", "refName"],
//     SwfLanguageServiceCodeCompletion.getFunctionRefRefnameCompletions,
//   ],
//   [
//     ["states", "*", "actions", "*", "functionRef", "arguments"],
//     SwfLanguageServiceCodeCompletion.getFunctionRefArgumentsCompletions,
//   ],
//   [["states", "*", "actions", "*", "functionRef", "arguments", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
//   [["states", "*", "actions", "*", "actionDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
//   [["states", "*", "stateDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
//   [["states", "*", "onEvents", "*", "eventDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
//   [["states", "*", "eventDataFilter", "*"], SwfLanguageServiceCodeCompletion.getJqcompletions],
//   [["states", "*", "dataConditions", "*", "condition"], SwfLanguageServiceCodeCompletion.getJqcompletions],
//   [["states", "*", "onEvents", "*", "eventRefs", "*"], SwfLanguageServiceCodeCompletion.getEventRefsCompletions],
//   [["states", "*", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
//   [["states", "*", "dataConditions", "*", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
//   [["states", "*", "defaultCondition", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
//   [["states", "*", "eventConditions", "*", "transition"], SwfLanguageServiceCodeCompletion.getTransitionCompletions],
// ]);

export function findNodeAtLocation(root: ELsNode, path: ELsJsonPath): ELsNode | undefined {
  return findNodesAtLocation({ root, path })[0];
}

export function findNodeAtOffset(root: ELsNode, offset: number, includeRightBound?: boolean): ELsNode | undefined {
  return jsonc.findNodeAtOffset(root as jsonc.Node, offset, includeRightBound) as ELsNode;
}

export function getNodePath(node: ELsNode): ELsJsonPath {
  return jsonc.getNodePath(node as jsonc.Node);
}

/**
 * Test if position `a` equals position `b`.
 * This function is compatible with https://microsoft.github.io/monaco-editor/api/classes/monaco.Position.html#equals-1
 *
 * @param a -
 * @param b -
 * @returns true if the positions are equal, false otherwise
 */
export const positions_equals = (a: Position | null, b: Position | null): boolean =>
  a?.line === b?.line && a?.character == b?.character;
