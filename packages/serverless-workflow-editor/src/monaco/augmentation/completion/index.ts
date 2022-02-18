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
import { CancellationToken, editor, languages, Position } from "monaco-editor";
import * as jsonc from "jsonc-parser";
import { SwfMonacoEditorInstance } from "../../SwfMonacoEditorApi";
import CompletionItemKind = languages.CompletionItemKind;

export type CompletionArgs = {
  model: editor.ITextModel;
  position: Position;
  context: languages.CompletionContext;
  token: CancellationToken;
};

const completions = new Map<
  jsonc.JSONPath,
  (args: { position: Position; commandIds: SwfMonacoEditorInstance["commands"] }) => languages.CompletionItem
>([
  [
    ["functions"],
    ({ position, commandIds }) => ({
      kind: CompletionItemKind.Snippet,
      insertText: "",
      range: {
        startColumn: position.column,
        endColumn: position.column,
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
      },
      label: "My completion item",
      command: {
        id: commandIds["RunFunctionsCompletion"],
        title: "My-Completion-Command",
        arguments: [{ position }],
      },
    }),
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

      const location = jsonc.getLocation(model.getValue(), model.getOffsetAt(position));

      return {
        suggestions: Array.from(completions.entries())
          .filter(([jsonPath, _]) => location.matches(jsonPath))
          .map(([_, completionItemDelegate]) => completionItemDelegate({ position, commandIds })),
      };
    },
  });
}
