/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as monaco from "monaco-editor";
import { CancellationToken, editor, languages, Position, Range } from "monaco-editor";
import * as jsonc from "jsonc-parser";
import { SwfMonacoEditorInstance } from "../../SwfMonacoEditorApi";
import { Specification } from "@severlessworkflow/sdk-typescript";
import { SwfServiceCatalogSingleton } from "../../../serviceCatalog";
import * as swfModelQueries from "./modelQueries";

const completions = new Map<
  jsonc.JSONPath,
  (args: {
    model: editor.ITextModel;
    cursorPosition: Position;
    context: languages.CompletionContext;
    currentNode: jsonc.Node;
    overwriteRange: Range;
    currentNodePosition: { start: Position; end: Position };
    commandIds: SwfMonacoEditorInstance["commands"];
    rootNode: jsonc.Node;
  }) => languages.CompletionItem[]
>([
  [
    ["functions", "*"],
    ({ model, currentNode, rootNode, overwriteRange, cursorPosition, commandIds }) => {
      const separator = currentNode.type === "object" ? "," : "";
      const existingOperations = swfModelQueries.getFunctions(rootNode).map((f) => f.operation);

      return SwfServiceCatalogSingleton.get()
        .getFunctions()
        .filter((swfServiceCatalogFunc) => !existingOperations.includes(swfServiceCatalogFunc.operation))
        .map((swfServiceCatalogFunc) => {
          const swfFunction: Omit<Specification.Function, "normalize"> = {
            name: `$\{1:${swfServiceCatalogFunc.name}}`,
            operation: swfServiceCatalogFunc.operation,
            type: swfServiceCatalogFunc.type,
          };
          return {
            kind: monaco.languages.CompletionItemKind.Module,
            label: swfServiceCatalogFunc.name,
            detail: swfFunction.operation,
            insertText: JSON.stringify(swfFunction, null, 2) + separator,
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: overwriteRange,
            command: {
              id: commandIds["ImportFunctionFromCompletionItem"],
              title: "Import function from completion item",
              //FIXME: tiago Pass the service here
              arguments: [{ service: undefined, importedFunction: swfServiceCatalogFunc }],
            },
          };
        });
    },
  ],
  [
    ["functions", "*", "operation"],
    ({ currentNode, rootNode, overwriteRange }) => {
      if (!currentNode.parent?.parent) {
        return [];
      }

      // As "rest" is the default, if the value is undefined, it's a rest function too.
      const isRestFunction =
        (jsonc.findNodeAtLocation(currentNode.parent.parent, ["type"])?.value ?? "rest") === "rest";
      if (!isRestFunction) {
        return [];
      }

      const existingOperations = swfModelQueries.getFunctions(rootNode).map((f) => f.operation);
      return SwfServiceCatalogSingleton.get()
        .getFunctions()
        .filter((swfServiceCatalogFunc) => !existingOperations.includes(swfServiceCatalogFunc.operation))
        .map((swfServiceCatalogFunc) => {
          return {
            kind: monaco.languages.CompletionItemKind.Value,
            label: swfServiceCatalogFunc.operation,
            detail: swfServiceCatalogFunc.operation,
            filterText: `"${swfServiceCatalogFunc.operation}"`,
            insertText: `"${swfServiceCatalogFunc.operation}"`,
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: overwriteRange,
          };
        });
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef"],
    ({ overwriteRange, currentNode, rootNode }) => {
      if (currentNode.type !== "property") {
        console.debug("Cannot autocomplete: functionRef should be a property.");
        return [];
      }

      return swfModelQueries.getFunctions(rootNode).flatMap((swfFunction) => {
        const swfServiceCatalogFunc = SwfServiceCatalogSingleton.get().getFunctionByOperation(swfFunction.operation);
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

        return [
          {
            kind: monaco.languages.CompletionItemKind.Module,
            label: `${swfFunctionRef.refName}`,
            sortText: swfFunctionRef.refName,
            detail: `${swfServiceCatalogFunc.operation}`,
            insertText: JSON.stringify(swfFunctionRef, null, 2),
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: overwriteRange,
          },
        ];
      });
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "refName"],
    ({ overwriteRange, rootNode }) => {
      return swfModelQueries.getFunctions(rootNode).flatMap((swfFunction) => {
        return [
          {
            kind: monaco.languages.CompletionItemKind.Value,
            label: swfFunction.name,
            sortText: swfFunction.name,
            detail: swfFunction.name,
            filterText: `"${swfFunction.name}"`,
            insertText: `"${swfFunction.name}"`,
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: overwriteRange,
          },
        ];
      });
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "arguments"],
    ({ overwriteRange, currentNode, rootNode }) => {
      if (currentNode.type !== "property") {
        console.debug("Cannot autocomplete: functionRef should be a property.");
        return [];
      }

      if (!currentNode.parent) {
        return [];
      }

      const swfFunctionRefName: string = jsonc.findNodeAtLocation(currentNode.parent, ["refName"])?.value;
      if (!swfFunctionRefName) {
        return [];
      }

      const swfFunction = swfModelQueries
        .getFunctions(rootNode)
        ?.filter((f) => f.name === swfFunctionRefName)
        .pop();
      if (!swfFunction) {
        return [];
      }

      const swfServiceCatalogFunc = SwfServiceCatalogSingleton.get().getFunctionByOperation(swfFunction.operation);
      if (!swfServiceCatalogFunc) {
        return [];
      }

      let argIndex = 1;
      const swfFunctionRefArgs: Record<string, string> = {};
      Object.keys(swfServiceCatalogFunc.arguments).forEach((argName) => {
        swfFunctionRefArgs[argName] = `$\{${argIndex++}:}`;
      });

      return [
        {
          kind: monaco.languages.CompletionItemKind.Module,
          label: `'${swfFunctionRefName}' arguments`,
          sortText: `${swfFunctionRefName} arguments`,
          detail: swfFunction.operation,
          insertText: JSON.stringify(swfFunctionRefArgs, null, 2),
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          range: overwriteRange,
        },
      ];
    },
  ],
]);

export function initJsonCompletion(commandIds: SwfMonacoEditorInstance["commands"]): void {
  monaco.languages.registerCompletionItemProvider("json", {
    triggerCharacters: [" ", ":"],
    provideCompletionItems: (
      model: editor.ITextModel,
      cursorPosition: Position,
      context: languages.CompletionContext,
      cancellationToken: CancellationToken
    ): languages.ProviderResult<languages.CompletionList> => {
      if (cancellationToken.isCancellationRequested) {
        return;
      }

      const rootNode = jsonc.parseTree(model.getValue());
      if (!rootNode) {
        return;
      }

      const cursorOffset = model.getOffsetAt(cursorPosition);

      const currentNode = jsonc.findNodeAtOffset(rootNode, cursorOffset);
      if (!currentNode) {
        return;
      }

      const currentNodePosition = {
        start: model.getPositionAt(currentNode.offset),
        end: model.getPositionAt(currentNode.offset + currentNode.length),
      };

      const currentWordPosition = model.getWordAtPosition(cursorPosition);

      const overwriteRange = ["string", "number", "boolean", "null"].includes(currentNode?.type)
        ? Range.fromPositions(currentNodePosition.start, currentNodePosition.end)
        : new Range(
            cursorPosition.lineNumber,
            currentWordPosition?.startColumn ?? cursorPosition.column,
            cursorPosition.lineNumber,
            currentWordPosition?.endColumn ?? cursorPosition.column
          );

      const cursorJsonLocation = jsonc.getLocation(model.getValue(), cursorOffset);

      return {
        suggestions: Array.from(completions.entries())
          .filter(([path, _]) => cursorJsonLocation.matches(path) && cursorJsonLocation.path.length === path.length)
          .flatMap(([_, completionItemsDelegate]) =>
            completionItemsDelegate({
              model,
              cursorPosition,
              context,
              commandIds,
              currentNode,
              currentNodePosition,
              rootNode,
              overwriteRange,
            })
          ),
      };
    },
  });
}
