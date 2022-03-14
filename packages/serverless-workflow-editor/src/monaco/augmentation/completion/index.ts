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
import { SwfFunction } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SwfFunctionDefinition, SwfFunctionRef, SwfServiceCatalogSingleton } from "../../../catalog";
import { getWorkflowSwfFunctionDefinitions } from "./utils";

const completions = new Map<
  jsonc.JSONPath,
  (args: {
    model: editor.ITextModel;
    workflow?: Specification.Workflow;
    cursorPosition: Position;
    nodePositions: { start: Position; end: Position };
    commandIds: SwfMonacoEditorInstance["commands"];
    actualNode: jsonc.Node;
    rootNode: jsonc.Node;
  }) => languages.CompletionItem[]
>([
  [
    ["functions"], // This JSONPath is a pre-filtering of the completion item.
    ({ model, cursorPosition, actualNode, workflow, rootNode }) => {
      if (actualNode.type != "array") {
        console.debug("Cannot autocomplete: Functions should be an array.");
        return [];
      }

      const existingOperations: string[] = getWorkflowSwfFunctionDefinitions(rootNode, workflow).map(
        (swfFunctionDef) => swfFunctionDef.operation
      );

      const wordPosition = model.getWordAtPosition(cursorPosition);

      const range = new Range(
        cursorPosition.lineNumber,
        wordPosition?.startColumn ?? cursorPosition.column,
        cursorPosition.lineNumber,
        wordPosition?.endColumn ?? cursorPosition.column
      );

      return SwfServiceCatalogSingleton.get()
        .getFunctions()
        .filter((swfFunction: SwfFunction) => !existingOperations.includes(swfFunction.operation))
        .map((swfFunction: SwfFunction) => {
          const swfFunctionDef: SwfFunctionDefinition = {
            name: `$\{1:${swfFunction.name}}`,
            operation: swfFunction.operation,
            type: swfFunction.type,
          };
          return {
            label: swfFunction.name,
            detail: swfFunctionDef.operation,
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: JSON.stringify(swfFunctionDef, null, 2),
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range,
          };
        });
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef"],
    ({ model, cursorPosition, actualNode, workflow, rootNode }) => {
      if (actualNode.type !== "property") {
        console.debug("Cannot autocomplete: functionRef should be a property.");
        return [];
      }

      const swfFunctionDefinitions = getWorkflowSwfFunctionDefinitions(rootNode, workflow);

      const wordPosition = model.getWordAtPosition(cursorPosition);

      const range = new Range(
        cursorPosition.lineNumber,
        wordPosition?.startColumn ?? cursorPosition.column,
        cursorPosition.lineNumber,
        wordPosition?.endColumn ?? cursorPosition.column
      );

      const completionItems: languages.CompletionItem[] = [];

      swfFunctionDefinitions.forEach((swfFunctionDef) => {
        const swfFunction = SwfServiceCatalogSingleton.get().getFunctionByOperation(swfFunctionDef.operation);
        if (!swfFunction) {
          return;
        }

        const refArgs: Record<string, string> = {};
        let index = 1;
        Object.keys(swfFunction.arguments).forEach((argName) => {
          refArgs[argName] = `$\{${index++}:}`;
        });
        const swfFunctionRef: SwfFunctionRef = {
          refName: swfFunctionDef.name,
          arguments: refArgs,
        };
        const swFunctionRefContent = JSON.stringify(swfFunctionRef, null, 2);
        completionItems.push({
          label: `${swfFunctionRef.refName}`,
          sortText: swfFunctionRef.refName,
          filterText: swfFunctionRef.refName,
          detail: `${swfFunction.operation}`,
          kind: monaco.languages.CompletionItemKind.Snippet,
          insertText: swFunctionRefContent,
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          range,
        });
      });

      return completionItems;
    },
  ],
]);

export function initJsonCompletion(commandIds: SwfMonacoEditorInstance["commands"]): void {
  monaco.languages.registerCompletionItemProvider("json", {
    provideCompletionItems: (
      model: editor.ITextModel,
      position: Position,
      context: languages.CompletionContext,
      token: CancellationToken
    ): languages.ProviderResult<languages.CompletionList> => {
      if (token.isCancellationRequested) {
        return;
      }

      const rootNode = jsonc.parseTree(model.getValue());
      if (!rootNode) {
        return;
      }

      const cursorOffset = model.getOffsetAt(position);

      const actualNode = jsonc.findNodeAtOffset(rootNode, cursorOffset);
      if (!actualNode) {
        return;
      }

      const nodePositions = {
        start: model.getPositionAt(actualNode.offset),
        end: model.getPositionAt(actualNode.offset + actualNode.length),
      };

      let workflow: Specification.Workflow | undefined;
      try {
        workflow = Specification.Workflow.fromSource(model.getValue());
      } catch (e) {
        console.error("Could not create Workflow from model", e);
        workflow = undefined;
      }

      const location = jsonc.getLocation(model.getValue(), cursorOffset);

      return {
        suggestions: Array.from(completions.entries())
          .filter(([jsonPath, _]) => location.matches(jsonPath))
          .flatMap(([_, completionItemsDelegate]) =>
            completionItemsDelegate({
              model,
              cursorPosition: position,
              commandIds,
              actualNode,
              nodePositions,
              workflow,
              rootNode,
            })
          ),
      };
    },
  });
}
