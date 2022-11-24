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
import { CompletionItem, CompletionItemKind, InsertTextFormat, Position, Range } from "vscode-languageserver-types";
import { getNodePath } from "./SwfLanguageService";
import { SwfLanguageServiceCommandExecution } from "../api";
import {
  eventCompletion,
  eventStateCompletion,
  functionCompletion,
  injectStateCompletion,
  operationStateCompletion,
  switchStateCompletion,
  workflowCompletion,
} from "../assets/code-completions";
import * as swfModelQueries from "./modelQueries";
import { nodeUpUntilType } from "./nodeUpUntilType";
import { findNodeAtLocation, SwfLanguageServiceConfig } from "./SwfLanguageService";
import { CodeCompletionStrategy, SwfLsNode } from "./types";
import { TextDocument } from "vscode-languageserver-textdocument";

type SwfCompletionItemServiceCatalogFunction = SwfServiceCatalogFunction & { operation: string };
export type SwfCompletionItemServiceCatalogService = Omit<SwfServiceCatalogService, "functions"> & {
  functions: SwfCompletionItemServiceCatalogFunction[];
};

export type SwfLanguageServiceCodeCompletionFunctionsArgs = {
  codeCompletionStrategy: CodeCompletionStrategy;
  currentNode: SwfLsNode;
  currentNodeRange: Range;
  cursorOffset: number;
  document: TextDocument;
  langServiceConfig: SwfLanguageServiceConfig;
  overwriteRange: Range;
  rootNode: SwfLsNode;
  swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
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

function createCompletionItem(args: {
  codeCompletionStrategy: CodeCompletionStrategy;
  completion: object | string;
  currentNodeRange: Range;
  cursorOffset: number;
  document: TextDocument;
  detail: string;
  extraOptions?: Partial<CompletionItem>;
  kind: CompletionItemKind;
  label: string;
  overwriteRange: Range;
}): CompletionItem {
  return {
    kind: args.kind,
    label: args.label,
    sortText: `100_${args.label}`, //place the completion on top in the menu
    filterText: args.label,
    detail: args.detail,
    textEdit: {
      newText: args.codeCompletionStrategy.translate({
        ...args,
        completionItemKind: args.kind,
      }),
      range: args.overwriteRange,
    },
    insertTextFormat: InsertTextFormat.Snippet,
    ...args.extraOptions,
  };
}

function getStateNameCompletion(
  args: SwfLanguageServiceCodeCompletionFunctionsArgs & { states: Specification.States }
): CompletionItem[] {
  return args.states.flatMap((state: any) => {
    const kind = CompletionItemKind.Value;
    const label = args.codeCompletionStrategy.formatLabel(state.name!, kind);

    return [
      createCompletionItem({
        ...args,
        completion: `${state.name}`,
        kind,
        label,
        detail: `"${state.name}"`,
      }),
    ];
  });
}

/**
 * SwfLanguageService CodeCompletion functions
 */
export const SwfLanguageServiceCodeCompletion = {
  getEmptyFileCodeCompletions(args: {
    cursorPosition: Position;
    codeCompletionStrategy: CodeCompletionStrategy;
    cursorOffset: number;
    document: TextDocument;
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
          newText: args.codeCompletionStrategy.translate({
            ...args,
            completion: workflowCompletion,
            completionItemKind: kind,
          }),
          range: Range.create(args.cursorPosition, args.cursorPosition),
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
    ];
  },

  getEventsCompletions: async (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const kind = CompletionItemKind.Interface;

    return Promise.resolve([
      createCompletionItem({
        ...args,
        completion: eventCompletion,
        kind,
        label: "New event",
        detail: "Add a new event",
      }),
    ]);
  },

  getStatesCompletions: async (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const kind = CompletionItemKind.Interface;

    return Promise.resolve([
      createCompletionItem({
        ...args,
        completion: operationStateCompletion,
        kind,
        label: "New operation state",
        detail: "Add a new operation state",
      }),
      createCompletionItem({
        ...args,
        completion: eventStateCompletion,
        kind,
        label: "New event state",
        detail: "Add a new event state",
      }),
      createCompletionItem({
        ...args,
        completion: switchStateCompletion,
        kind,
        label: "New switch state",
        detail: "Add a new switch state",
      }),
      createCompletionItem({
        ...args,
        completion: injectStateCompletion,
        kind,
        label: "New inject state",
        detail: "Add a new inject state",
      }),
    ]);
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

          return createCompletionItem({
            ...args,
            completion: swfFunction,
            kind,
            label,
            detail:
              swfServiceCatalogService.source.type === SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY
                ? swfServiceCatalogService.source.url
                : swfServiceCatalogFunc.operation,
            extraOptions: {
              command: {
                command: command.name,
                title: "Import function from completion item",
                arguments: [command.args],
              },
            },
          });
        })
    );

    const genericFunctionCompletion = createCompletionItem({
      ...args,
      completion: functionCompletion,
      kind: CompletionItemKind.Interface,
      label: "New function",
      detail: "Add a new function",
    });

    return Promise.resolve([...result, genericFunctionCompletion]);
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

        return createCompletionItem({
          ...args,
          completion: `${swfServiceCatalogFunc.operation}`,
          kind,
          label,
          detail: label,
        });
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
        createCompletionItem({
          ...args,
          completion: swfFunctionRef,
          kind,
          label,
          detail: `${swfServiceCatalogFunc.operation}`,
        }),
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
        createCompletionItem({
          ...args,
          completion: `${swfFunction.name}`,
          kind,
          label,
          detail: `"${swfFunction.name}"`,
        }),
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
      createCompletionItem({
        ...args,
        completion: swfFunctionRefArgs,
        kind,
        label,
        detail: swfFunction.operation,
      }),
    ]);
  },

  getEventRefsCompletions: (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const result = swfModelQueries.getEvents(args.rootNode).flatMap((event) => {
      const kind = CompletionItemKind.Value;
      const label = args.codeCompletionStrategy.formatLabel(event.name!, kind);

      return [
        createCompletionItem({
          ...args,
          completion: `${event.name}`,
          kind,
          label,
          detail: `"${event.name}"`,
        }),
      ];
    });
    return Promise.resolve(result);
  },

  getTransitionCompletions: (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const statePath = getNodePath(args.currentNode).slice(0, 2);
    const currentStateName = findNodeAtLocation(args.rootNode, [...statePath, "name"])?.value || "";
    const states = swfModelQueries
      .getStates(args.rootNode)
      .filter((s) => s.name !== currentStateName) as Specification.States;
    const result = getStateNameCompletion({ ...args, states });

    return Promise.resolve(result);
  },

  getStartCompletions: (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const states = swfModelQueries.getStates(args.rootNode);
    const result = getStateNameCompletion({ ...args, states });

    return Promise.resolve(result);
  },
};
