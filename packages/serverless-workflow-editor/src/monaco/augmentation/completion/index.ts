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
import CompletionItemKind = languages.CompletionItemKind;

const completions = new Map<
  jsonc.JSONPath,
  (args: {
    workflow: Specification.Workflow;
    cursorPosition: Position;
    nodePositions: { start: Position; end: Position };
    commandIds: SwfMonacoEditorInstance["commands"];
    actualNode: jsonc.Node;
  }) => languages.CompletionItem[]
>([
  [
    ["functions"], // This JSONPath is a pre-filtering of the completion item.
    ({ cursorPosition, commandIds, actualNode }) => {
      /*
        Here we can use any logic that we want, based on the `actualNode` that at the cursor position.
      */

      return [
        {
          kind: CompletionItemKind.Snippet,
          label: "Completion item that triggers a command",
          sortText: "a", // Keep it at the top
          insertText: "",
          range: {
            startColumn: cursorPosition.column,
            endColumn: cursorPosition.column,
            startLineNumber: cursorPosition.lineNumber,
            endLineNumber: cursorPosition.lineNumber,
          },
          command: {
            id: commandIds["RunFunctionsCompletion"],
            title: "",
            arguments: [{ cursorPosition, actualNode }], // The actualNode is passed to the command, so it can make decisions based on it too.
          },
        },
        {
          kind: CompletionItemKind.Snippet,
          label: "Completion item that inserts text",
          sortText: "a", // Keep it at the top
          insertText: "Awesome text made from a completion item",
          range: {
            startColumn: cursorPosition.column,
            endColumn: cursorPosition.column,
            startLineNumber: cursorPosition.lineNumber,
            endLineNumber: cursorPosition.lineNumber,
          },
        },
      ];
    },
  ],
  [
    ["states", "*", "actions", "*", "functionRef", "refName"],
    ({ cursorPosition, actualNode, workflow, nodePositions }) => {
      // `refName` is going to be a property node with two string nodes as children. The first one is `refName`, and the second one is the actual value.
      if (actualNode.parent?.children?.[1] !== actualNode) {
        console.info("oops, wrong node.");
        return [];
      }

      if (typeof workflow?.functions === "string") {
        console.info("string. ignoring.");
        return [];
      }

      const range = new Range(
        nodePositions.start.lineNumber,
        nodePositions.start.column,
        nodePositions.end.lineNumber,
        nodePositions.end.column
      );

      console.info(range);
      console.info(cursorPosition);

      return Array.from(workflow?.functions ?? []).map((f) => ({
        kind: CompletionItemKind.Snippet,
        detail: "Function",
        label: f.name,
        sortText: f.name,
        filterText: f.name,
        insertText: `"${f.name}"`,
        range,
      }));
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

      const workflow = Specification.Workflow.fromSource(model.getValue());

      const location = jsonc.getLocation(model.getValue(), cursorOffset);

      return {
        incomplete: true,
        suggestions: Array.from(completions.entries())
          .filter(([jsonPath, _]) => location.matches(jsonPath))
          .flatMap(([_, completionItemsDelegate]) =>
            completionItemsDelegate({
              cursorPosition: position,
              commandIds,
              actualNode,
              nodePositions,
              workflow,
            })
          ),
      };
    },
  });
}
