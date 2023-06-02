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
  createCompletionItem,
  EditorLanguageServiceCodeCompletionFunctions,
  EditorLanguageServiceCodeCompletionFunctionsArgs,
  EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs,
  ELsNode,
  nodeUpUntilType,
  findNodeAtLocation,
  getNodePath,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { jqBuiltInFunctions } from "@kie-tools/serverless-workflow-jq-expressions/dist/utils";
import {
  SwfCatalogSourceType,
  SwfServiceCatalogFunction,
  SwfServiceCatalogEvent,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { Specification } from "@severlessworkflow/sdk-typescript";
import { CompletionItem, CompletionItemKind, InsertTextFormat, Position, Range } from "vscode-languageserver-types";
import { SwfLanguageServiceCommandExecution } from "../api";
import {
  emptyWorkflowCompletion,
  eventCompletion,
  eventStateCompletion,
  functionCompletion,
  injectStateCompletion,
  operationStateCompletion,
  switchStateCompletion,
  workflowCompletion,
} from "../assets/code-completions";
import * as swfModelQueries from "./modelQueries";
import { SwfLanguageServiceConfig } from "./SwfLanguageService";
import { JqCompletions } from "./types";

type SwfCompletionItemServiceCatalogFunction = SwfServiceCatalogFunction & { operation: string };
type SwfCompletionItemServiceCatalogEvent = SwfServiceCatalogEvent;
export type SwfCompletionItemServiceCatalogService = Omit<SwfServiceCatalogService, "functions"> & {
  functions: SwfCompletionItemServiceCatalogFunction[];
  events: SwfCompletionItemServiceCatalogEvent[];
};

export type SwfLanguageServiceCodeCompletionFunctionsArgs = EditorLanguageServiceCodeCompletionFunctionsArgs & {
  langServiceConfig: SwfLanguageServiceConfig;
  swfCompletionItemServiceCatalogServices: SwfCompletionItemServiceCatalogService[];
  jqCompletions: JqCompletions;
};
interface JqFunctionCompletion {
  /**
   * @param wordToSearch The wordToSearch is empty to show all the completion. Otherwise it completes the word.
   */
  (args: SwfLanguageServiceCodeCompletionFunctionsArgs & { wordToSearch: string }): Promise<CompletionItem[]>;
}

function toCompletionItemLabel(namespace: string, resource: string, operation: string) {
  return `${namespace}»${resource}#${operation}`;
}

function isRemotePath(pathUri: string): boolean {
  return /^(http|https|file):\/\//.test(pathUri);
}

function toCompletionItemLabelPrefix(
  swfServiceCatalogFunction: SwfServiceCatalogFunction,
  specsDirRelativePosixPath: string
) {
  switch (swfServiceCatalogFunction.source.type) {
    case SwfCatalogSourceType.LOCAL_FS:
      const fileName =
        swfServiceCatalogFunction.source.serviceFileAbsolutePath.split("/").pop() ??
        swfServiceCatalogFunction.source.serviceFileAbsolutePath;
      return toCompletionItemLabel(specsDirRelativePosixPath, fileName, swfServiceCatalogFunction.name);
    case SwfCatalogSourceType.SERVICE_REGISTRY:
      return toCompletionItemLabel(
        swfServiceCatalogFunction.source.registry,
        swfServiceCatalogFunction.source.serviceId,
        swfServiceCatalogFunction.name
      );
    default:
      return "";
  }
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

function extractFunctionsPath(functionsNode: ELsNode[]) {
  const relativeList: string[] = [];
  const remoteList: string[] = [];
  functionsNode.forEach((func: ELsNode) => {
    const functionType = findNodeAtLocation(func, ["type"])?.value.trim() ?? "rest";
    if (functionType == "rest" || functionType == "asyncapi") {
      const path = findNodeAtLocation(func, ["operation"])?.value.split("#")[0];
      if (path) {
        if (isRemotePath(path)) {
          remoteList.push(path);
        } else {
          relativeList.push(path);
        }
      }
    }
  });
  return { relativeList, remoteList };
}
/**
 * get word to search for built-in jq functions.
 */
function getJqCompletionWordToSearch(slicedValue: string): string {
  const removeSpecialChar = slicedValue.replace(/[^a-zA-Z _()]/g, "");
  const builtInFunctionMatch = removeSpecialChar.match(/\s(\w+)?$/);
  if (builtInFunctionMatch === null && removeSpecialChar.length) {
    return removeSpecialChar.trim();
  } else if (builtInFunctionMatch && builtInFunctionMatch[1] === undefined) {
    return "";
  } else if (builtInFunctionMatch && builtInFunctionMatch[1].length) {
    return builtInFunctionMatch[1];
  }
  return "";
}

/**
 * get the input workflow variables from remote/relative paths.
 */
const getJqInputVariablesCompletions: JqFunctionCompletion = async function getJqInputVariablesCompletions(
  args: SwfLanguageServiceCodeCompletionFunctionsArgs & { wordToSearch: string }
): Promise<CompletionItem[]> {
  const showAllCompletion = !args.wordToSearch.length ? true : false;
  const { relativeList, remoteList } = extractFunctionsPath(
    findNodeAtLocation(args.rootNode, ["functions"])?.children as ELsNode[]
  );
  const dataInputSchemaPath = findNodeAtLocation(args.rootNode, ["dataInputSchema"])?.value;
  if (dataInputSchemaPath) {
    if (isRemotePath(dataInputSchemaPath)) {
      remoteList.push(dataInputSchemaPath);
    } else {
      relativeList.push(dataInputSchemaPath);
    }
  }
  if (remoteList.length > 0 || relativeList.length > 0) {
    const schemaData = await Promise.all([
      ...(await args.jqCompletions.remote.getJqAutocompleteProperties({
        textDocument: args.document,
        schemaPaths: remoteList ?? [],
      })),
      ...(await args.jqCompletions.relative.getJqAutocompleteProperties({
        textDocument: args.document,
        schemaPaths: relativeList ?? [],
      })),
    ]);
    if (schemaData.length === 0) {
      return Promise.resolve([]);
    }
    return Promise.resolve(
      schemaData
        .filter((prop: Record<string, string>) =>
          showAllCompletion ? true : Object.keys(prop)[0].startsWith(args.wordToSearch)
        )
        .map((parsedProp: Record<string, string>) => {
          return createCompletionItem({
            ...args,
            completion: Object.keys(parsedProp)[0],
            kind: CompletionItemKind.Value,
            label: Object.keys(parsedProp)[0],
            detail: Object.values(parsedProp)[0],
            filterText: showAllCompletion ? Object.keys(parsedProp)[0] : args.wordToSearch,
            extraOptions: {
              insertText: Object.keys(parsedProp)[0],
            },
            overwriteRange: Range.create(
              Position.create(
                args.cursorPosition.line,
                showAllCompletion
                  ? args.cursorPosition.character
                  : args.cursorPosition.character - args.wordToSearch.length
              ),
              Position.create(args.cursorPosition.line, args.cursorPosition.character)
            ),
          });
        })
    );
  }
  return [];
};
/**
 * get reusable functions defined in the functions array of the swf file.
 */
const getReusableFunctionCompletion: JqFunctionCompletion = async function (
  args: SwfLanguageServiceCodeCompletionFunctionsArgs & { wordToSearch: string }
): Promise<CompletionItem[]> {
  const reusalbeFunctions: ELsNode = findNodeAtLocation(args.rootNode, ["functions"])!;
  const functionNamesArray: string[] = [];
  const isWordToSearchExist = args.wordToSearch.length ? true : false;
  const overwriteRange = Range.create(
    Position.create(args.cursorPosition.line, args.cursorPosition.character - args.wordToSearch.length),
    Position.create(args.cursorPosition.line, args.cursorPosition.character)
  );
  if (reusalbeFunctions.type === "array") {
    reusalbeFunctions.children?.forEach((func) => {
      if (findNodeAtLocation(func, ["type"])?.value === "expression") {
        const functionName = findNodeAtLocation(func, ["name"])?.value;
        functionNamesArray.push(functionName);
      }
    });
    return functionNamesArray
      .filter((name: string) => (!isWordToSearchExist ? true : name.startsWith(args.wordToSearch)))
      .map((filteredName: string) => {
        return createCompletionItem({
          ...args,
          completion: filteredName,
          kind: CompletionItemKind.Function,
          label: filteredName,
          filterText: isWordToSearchExist ? args.wordToSearch : filteredName,
          detail: "Reusable functions(expressions) defined in the functions array",
          extraOptions: {
            insertText: filteredName,
          },
          overwriteRange,
        });
      });
  }
  return [];
};
/**
 * get jq built-in CodeCompletions.
 */
const getJqBuiltInFunctions: JqFunctionCompletion = async function (
  args: SwfLanguageServiceCodeCompletionFunctionsArgs & { wordToSearch: string }
): Promise<CompletionItem[]> {
  const isWordToSearchExist = args.wordToSearch.length ? true : false;
  const overwriteRange = Range.create(
    Position.create(args.cursorPosition.line, args.cursorPosition.character - args.wordToSearch.length),
    Position.create(args.cursorPosition.line, args.cursorPosition.character)
  );
  return jqBuiltInFunctions
    .filter((func: { functionName: string; description: string }) =>
      !isWordToSearchExist ? true : func.functionName.startsWith(args.wordToSearch)
    )
    .map((filteredFunc: { functionName: string; description: string }) => {
      return createCompletionItem({
        ...args,
        completion: filteredFunc.functionName,
        kind: CompletionItemKind.Function,
        label: filteredFunc.functionName,
        detail: filteredFunc.description,
        filterText: isWordToSearchExist ? args.wordToSearch : filteredFunc.functionName,
        extraOptions: {
          insertText: filteredFunc.functionName,
        },
        overwriteRange,
      });
    });
};
/**
 * get jq CodeCompletion functions.
 */
async function getJqFunctionCompletions(
  args: SwfLanguageServiceCodeCompletionFunctionsArgs
): Promise<CompletionItem[]> {
  const {
    currentNode,
    cursorOffset,
    currentNode: { offset },
  } = args;
  const isCurrentNodeValueWithQuotes = currentNode.length === currentNode.value.length ? 0 : 1;
  const currentCursor = cursorOffset - offset;
  const slicedValue = currentNode.value.slice(0, currentCursor - isCurrentNodeValueWithQuotes);
  const inputVariableMatch = slicedValue.match(/.*((\.)|(fn:))(\w+)?$/);
  if (inputVariableMatch) {
    const wordToSearch = inputVariableMatch[4] ?? "";
    return inputVariableMatch[1] === "."
      ? await getJqInputVariablesCompletions({ ...args, wordToSearch })
      : await getReusableFunctionCompletion({ ...args, wordToSearch });
  }
  const wordToSearch = getJqCompletionWordToSearch(slicedValue);
  return await getJqBuiltInFunctions({ ...args, wordToSearch });
}
/**
 * SwfLanguageService CodeCompletion functions
 */
export const SwfLanguageServiceCodeCompletion: EditorLanguageServiceCodeCompletionFunctions = {
  getEmptyFileCodeCompletions(
    args: EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs
  ): Promise<CompletionItem[]> {
    const kind = CompletionItemKind.Text;
    const emptyWorkflowLabel = "Empty Serverless Workflow";
    const exampleWorkflowLabel = "Serverless Workflow Example";

    return Promise.resolve([
      {
        kind,
        label: exampleWorkflowLabel,
        detail: "Start with a simple Serverless Workflow",
        sortText: `100_${exampleWorkflowLabel}`, //place the completion on top in the menu
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
      {
        kind,
        label: emptyWorkflowLabel,
        detail: "Start with an empty Serverless Workflow",
        sortText: `100_${emptyWorkflowLabel}`, //place the completion on top in the menu
        textEdit: {
          newText: args.codeCompletionStrategy.translate({
            ...args,
            completion: emptyWorkflowCompletion,
            completionItemKind: kind,
          }),
          range: Range.create(args.cursorPosition, args.cursorPosition),
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
    ]);
  },

  getEventsCompletions: async (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const kind = CompletionItemKind.Interface;

    const existingEventNames = swfModelQueries.getEvents(args.rootNode).map((f) => f.name);

    const specsDir = await args.langServiceConfig.getSpecsDirPosixPaths(args.document);

    const result = args.swfCompletionItemServiceCatalogServices.flatMap((swfServiceCatalogService) => {
      const dirRelativePosixPath = specsDir.specsDirRelativePosixPath;

      return swfServiceCatalogService.events
        .filter(
          (swfServiceCatalogEvent: any) =>
            swfServiceCatalogEvent.name && !existingEventNames.includes(swfServiceCatalogEvent.name)
        )
        .map((swfServiceCatalogEvent: any) => {
          const swfEvent = {
            name: `$\{1:${swfServiceCatalogEvent.name}}`,
            source: swfServiceCatalogEvent.eventSource,
            type: swfServiceCatalogEvent.eventType,
            kind: swfServiceCatalogEvent.kind,
            metadata: swfServiceCatalogEvent.metadata,
          };

          const command: SwfLanguageServiceCommandExecution<"swf.ls.commands.ImportEventFromCompletionItem"> = {
            name: "swf.ls.commands.ImportEventFromCompletionItem",
            args: {
              containingService: swfServiceCatalogService,
              documentUri: args.document.uri,
            },
          };

          const kind =
            swfServiceCatalogEvent.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
              ? CompletionItemKind.Interface
              : CompletionItemKind.Reference;

          const label = args.codeCompletionStrategy.formatLabel(
            toCompletionItemLabelPrefix(swfServiceCatalogEvent, dirRelativePosixPath),
            kind
          );

          return createCompletionItem({
            ...args,
            completion: swfEvent,
            kind,
            label,
            detail:
              swfServiceCatalogService.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
                ? swfServiceCatalogService.source.url
                : swfServiceCatalogEvent.operation,
            extraOptions: {
              command: {
                command: command.name,
                title: "Import event from completion item",
                arguments: [command.args],
              },
            },
          });
        });
    });

    const genericEventCompletion = createCompletionItem({
      ...args,
      completion: eventCompletion,
      kind,
      label: "New event",
      detail: "Add a new event",
    });

    return Promise.resolve([...result, genericEventCompletion]);
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
    const routesDir = await args.langServiceConfig.getRoutesDirPosixPaths(args.document);

    const result = args.swfCompletionItemServiceCatalogServices.flatMap((swfServiceCatalogService) => {
      let dirRelativePosixPath: string;

      if (swfServiceCatalogService.type === SwfServiceCatalogServiceType.camelroute) {
        dirRelativePosixPath = routesDir.routesDirRelativePosixPath;
      } else {
        dirRelativePosixPath = specsDir.specsDirRelativePosixPath;
      }

      return swfServiceCatalogService.functions
        .filter(
          (swfServiceCatalogFunc) =>
            swfServiceCatalogFunc.name && !existingFunctionOperations.includes(swfServiceCatalogFunc.operation)
        )
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
            swfServiceCatalogFunc.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
              ? CompletionItemKind.Interface
              : CompletionItemKind.Reference;

          const label = args.codeCompletionStrategy.formatLabel(
            toCompletionItemLabelPrefix(swfServiceCatalogFunc, dirRelativePosixPath),
            kind
          );

          return createCompletionItem({
            ...args,
            completion: swfFunction,
            kind,
            label,
            detail:
              swfServiceCatalogService.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
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
        });
    });

    const genericFunctionCompletion = createCompletionItem({
      ...args,
      completion: functionCompletion,
      kind: CompletionItemKind.Interface,
      label: "New function",
      detail: "Add a new function",
    });

    return Promise.resolve([...result, genericFunctionCompletion]);
  },
  getFunctionOperationCompletions: async (
    args: SwfLanguageServiceCodeCompletionFunctionsArgs
  ): Promise<CompletionItem[]> => {
    if (!args.currentNode.parent?.parent) {
      return Promise.resolve([]);
    }
    // As "rest" is the default, if the value is undefined, it's a rest function too.
    const isRestFunction = (findNodeAtLocation(args.currentNode.parent.parent, ["type"])?.value ?? "rest") === "rest";
    const isExpression = findNodeAtLocation(args.currentNode.parent.parent, ["type"])?.value === "expression";
    if (!isRestFunction && !isExpression) {
      return Promise.resolve([]);
    }
    if (isExpression) {
      return Promise.resolve(await getJqFunctionCompletions(args));
    }
    const existingFunctionOperations = swfModelQueries.getFunctions(args.rootNode).map((f) => f.operation);

    const result = args.swfCompletionItemServiceCatalogServices
      .flatMap((s) => s.functions)
      .filter((swfServiceCatalogFunc) => !existingFunctionOperations.includes(swfServiceCatalogFunc.operation))
      .map((swfServiceCatalogFunc) => {
        const kind =
          swfServiceCatalogFunc.source.type === SwfCatalogSourceType.SERVICE_REGISTRY
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

  getJqcompletions: async (args: SwfLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
    const jqCompletions = await getJqFunctionCompletions(args);
    if (args.currentNode && args.currentNode.type === "string") {
      return Promise.resolve(jqCompletions);
    }
    return Promise.resolve([]);
  },
};
