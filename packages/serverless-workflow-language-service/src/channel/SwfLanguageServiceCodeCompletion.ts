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
import { Specification } from "@severlessworkflow/sdk-typescript";
import { TextDocument } from "vscode-json-languageservice";
import { CompletionItem, CompletionItemKind, InsertTextFormat, Position, Range } from "vscode-languageserver-types";
import { SwfLanguageServiceCommandExecution } from "../api";
import * as simpleTemplate from "../assets/code-completion/simple-template.sw.json";
import * as swfModelQueries from "./modelQueries";
import { nodeUpUntilType } from "./nodeUpUntilType";
import { findNodeAtLocation, SwfLanguageServiceConfig } from "./SwfLanguageService";
import { CodeCompletionStrategy, SwfLsNode } from "./types";

type SwfCompletionItemServiceCatalogFunction = SwfServiceCatalogFunction & { operation: string };
export type SwfCompletionItemServiceCatalogService = Omit<SwfServiceCatalogService, "functions"> & {
  functions: SwfCompletionItemServiceCatalogFunction[];
};

export type SwfLanguageServiceCodeCompletionFunctionsArgs = {
  document: TextDocument;
  overwriteRange: Range;
  currentNode: SwfLsNode;
  currentNodeRange: Range;
  rootNode: SwfLsNode;
  codeCompletionStrategy: CodeCompletionStrategy;
  swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
  langServiceConfig: SwfLanguageServiceConfig;
};

function toCompletionItemLabel(namespace: string, resource: string, operation: string) {
  return `${namespace}»${resource}#${operation}`;
}

function toCompletionItemLabelPrefix(
  swfServiceCatalogFunction: SwfServiceCatalogFunction,
  specsDirRelativePosixPath: string
) {
  switch (swfServiceCatalogFunction.source.type) {
    case SwfServiceCatalogFunctionSourceType.LOCAL_FS:
      const fileName =
        swfServiceCatalogFunction.source.serviceFileAbsolutePath.split("/").pop() ??
        swfServiceCatalogFunction.source.serviceFileAbsolutePath;
      return toCompletionItemLabel(specsDirRelativePosixPath, fileName, swfServiceCatalogFunction.name);
    case SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY:
      return toCompletionItemLabel(
        swfServiceCatalogFunction.source.registry,
        swfServiceCatalogFunction.source.serviceId,
        swfServiceCatalogFunction.name
      );
    default:
      return "";
  }
}

/**
 * SwfLanguageService CodeCompletion functions
 */
export const SwfLanguageServiceCodeCompletion = {
  getEmptyFileCodeCompletions(args: {
    cursorPosition: Position;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): CompletionItem[] {
    const kind = CompletionItemKind.Text;
    const label = "Create your first Serverless Workflow";

    return [
      {
        kind,
        label,
        detail: "Start with a simple Serverless Workflow",
        sortText: `100_${label}`, //place the completion on top in the menu
        textEdit: {
          newText: args.codeCompletionStrategy.translate({ completion: simpleTemplate, completionItemKind: kind }),
          range: Range.create(args.cursorPosition, args.cursorPosition),
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
    ];
  },

  getFunctionCompletions: async (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const existingFunctionOperations = swfModelQueries.getFunctions(args.rootNode).map((f) => f.operation);

    const specsDir = await args.langServiceConfig.getSpecsDirPosixPaths(args.document);

    const result = args.swfCompletionItemServiceCatalogServices.flatMap((swfServiceCatalogService) =>
      swfServiceCatalogService.functions
        .filter((swfServiceCatalogFunc) => !existingFunctionOperations.includes(swfServiceCatalogFunc.operation))
        .map((swfServiceCatalogFunc) => {
          const swfFunction: Omit<Specification.Function, "normalize"> = {
            name: `$\{1:${swfServiceCatalogFunc.name}}`,
            operation: swfServiceCatalogFunc.operation,
            type: swfServiceCatalogFunc.type,
          };

          const command: SwfLanguageServiceCommandExecution<"swf.ls.commands.ImportFunctionFromCompletionItem"> = {
            name: "swf.ls.commands.ImportFunctionFromCompletionItem",
            args: {
              containingService: swfServiceCatalogService,
              documentUri: args.document.uri,
            },
          };

          const kind =
            swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
              ? CompletionItemKind.Interface
              : CompletionItemKind.Reference;

          const label = args.codeCompletionStrategy.formatLabel(
            toCompletionItemLabelPrefix(swfServiceCatalogFunc, specsDir.specsDirRelativePosixPath),
            kind
          );

          return {
            kind,
            label,
            detail:
              swfServiceCatalogService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY
                ? swfServiceCatalogService.source.url
                : swfServiceCatalogFunc.operation,
            textEdit: {
              newText:
                args.codeCompletionStrategy.translate({
                  completion: swfFunction,
                  completionItemKind: kind,
                  overwriteRange: args.overwriteRange,
                  currentNodeRange: args.currentNodeRange,
                }) + (args.currentNode.type === "object" ? "," : ""),
              range: args.overwriteRange,
            },
            snippet: true,
            insertTextFormat: InsertTextFormat.Snippet,
            command: {
              command: command.name,
              title: "Import function from completion item",
              arguments: [command.args],
            },
          };
        })
    );
    return Promise.resolve(result);
  },

  getFunctionOperationCompletions: (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    if (!args.currentNode.parent?.parent) {
      return Promise.resolve([]);
    }

    // As "rest" is the default, if the value is undefined, it's a rest function too.
    const isRestFunction = (findNodeAtLocation(args.currentNode.parent.parent, ["type"])?.value ?? "rest") === "rest";
    if (!isRestFunction) {
      return Promise.resolve([]);
    }

    const existingFunctionOperations = swfModelQueries.getFunctions(args.rootNode).map((f) => f.operation);

    const result = args.swfCompletionItemServiceCatalogServices
      .flatMap((s) => s.functions)
      .filter((swfServiceCatalogFunc) => !existingFunctionOperations.includes(swfServiceCatalogFunc.operation))
      .map((swfServiceCatalogFunc) => {
        const kind =
          swfServiceCatalogFunc.source.type === SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY
            ? CompletionItemKind.Function
            : CompletionItemKind.Folder;

        const label = args.codeCompletionStrategy.formatLabel(swfServiceCatalogFunc.operation, kind);

        return {
          kind,
          label,
          detail: label,
          filterText: label,
          textEdit: {
            newText: args.codeCompletionStrategy.translate({
              completion: `${swfServiceCatalogFunc.operation}`,
              completionItemKind: kind,
            }),
            range: args.overwriteRange,
          },
          insertTextFormat: InsertTextFormat.Snippet,
        };
      });
    return Promise.resolve(result);
  },

  getFunctionRefCompletions: (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    if (args.currentNode.type !== "property") {
      console.debug("Cannot autocomplete: functionRef should be a property.");
      return Promise.resolve([]);
    }

    const result = swfModelQueries.getFunctions(args.rootNode).flatMap((swfFunction) => {
      const swfServiceCatalogFunc = args.swfCompletionItemServiceCatalogServices
        .flatMap((f) => f.functions)
        .filter((f) => f.operation === swfFunction.operation)
        .pop()!;
      if (!swfServiceCatalogFunc) {
        return [];
      }

      let argIndex = 1;
      const swfFunctionRefArgs: Record<string, string> = {};
      Object.keys(swfServiceCatalogFunc.arguments).forEach((argName) => {
        swfFunctionRefArgs[argName] = `$\{${argIndex++}:}`;
      });

      const swfFunctionRef: Omit<Specification.Functionref, "normalize"> = {
        refName: swfFunction.name,
        arguments: swfFunctionRefArgs,
      };

      const kind = CompletionItemKind.Module;
      const label = args.codeCompletionStrategy.formatLabel(swfFunctionRef.refName, kind);

      return [
        {
          kind,
          label,
          sortText: label,
          detail: `${swfServiceCatalogFunc.operation}`,
          textEdit: {
            newText: args.codeCompletionStrategy.translate({ completion: swfFunctionRef, completionItemKind: kind }),
            range: args.overwriteRange,
          },
          insertTextFormat: InsertTextFormat.Snippet,
        },
      ];
    });

    return Promise.resolve(result);
  },

  getFunctionRefRefnameCompletions: (
    args: SwfLanguageServiceCodeCompletionFunctionsArgs
  ): Promise<CompletionItem[]> => {
    const result = swfModelQueries.getFunctions(args.rootNode).flatMap((swfFunction) => {
      const kind = CompletionItemKind.Value;
      const label = args.codeCompletionStrategy.formatLabel(swfFunction.name, kind);

      return [
        {
          kind,
          label,
          sortText: label,
          detail: `"${swfFunction.name}"`,
          filterText: label,
          textEdit: {
            newText: args.codeCompletionStrategy.translate({
              completion: `${swfFunction.name}`,
              completionItemKind: kind,
            }),
            range: args.overwriteRange,
          },
          insertTextFormat: InsertTextFormat.Snippet,
        },
      ];
    });
    return Promise.resolve(result);
  },

  getFunctionRefArgumentsCompletions: (
    args: SwfLanguageServiceCodeCompletionFunctionsArgs
  ): Promise<CompletionItem[]> => {
    if (args.currentNode.type !== "property" && args.currentNode.type !== "string") {
      console.debug("Cannot autocomplete: arguments should be a property.");
      return Promise.resolve([]);
    }

    const startNode = nodeUpUntilType(args.currentNode, "object");

    if (!startNode) {
      return Promise.resolve([]);
    }

    const swfFunctionRefName: string = findNodeAtLocation(startNode, ["refName"])?.value;
    if (!swfFunctionRefName) {
      return Promise.resolve([]);
    }

    const swfFunction = swfModelQueries
      .getFunctions(args.rootNode)
      ?.filter((f) => f.name === swfFunctionRefName)
      .pop();
    if (!swfFunction) {
      return Promise.resolve([]);
    }

    const swfServiceCatalogFunc = args.swfCompletionItemServiceCatalogServices
      .flatMap((f) => f.functions)
      .filter((f) => f.operation === swfFunction.operation)
      .pop()!;
    if (!swfServiceCatalogFunc) {
      return Promise.resolve([]);
    }

    let argIndex = 1;
    const swfFunctionRefArgs: Record<string, string> = {};
    Object.keys(swfServiceCatalogFunc.arguments).forEach((argName) => {
      swfFunctionRefArgs[argName] = `$\{${argIndex++}:}`;
    });

    const kind = CompletionItemKind.Module;
    const label = `'${swfFunctionRefName}' arguments`;

    return Promise.resolve([
      {
        kind,
        label,
        sortText: label,
        detail: swfFunction.operation,
        textEdit: {
          newText: args.codeCompletionStrategy.translate({ completion: swfFunctionRefArgs, completionItemKind: kind }),
          range: args.overwriteRange,
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
    ]);
  },
};
