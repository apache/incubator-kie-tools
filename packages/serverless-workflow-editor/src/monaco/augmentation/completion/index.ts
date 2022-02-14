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
import { SwfMonacoEditorCommands, SwfMonacoEditorInstance } from "../../SwfMonacoEditorApi";
import CompletionItemKind = languages.CompletionItemKind;

export type CompletionArgs = {
  model: editor.ITextModel;
  position: Position;
  context: languages.CompletionContext;
  token: CancellationToken;
};

const completions = new Map<
  jsonc.JSONPath,
  (args: { position: Position; commands: SwfMonacoEditorInstance["commands"] }) => languages.CompletionItem
>([
  [
    ["functions"],
    ({ position, commands }) => ({
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
        id: commands["FunctionsCompletion"],
        title: "My-Completion-Command",
        arguments: [{ position }],
      },
    }),
  ],
]);

export function initJsonCompletion(commands: SwfMonacoEditorCommands): void {
  monaco.languages.registerCompletionItemProvider("json", {
    provideCompletionItems: function (
      model: editor.ITextModel,
      position: Position,
      context: languages.CompletionContext,
      token: CancellationToken
    ): languages.ProviderResult<languages.CompletionList> {
      const rootNode = jsonc.parseTree(model.getValue());
      if (!rootNode) {
        return { suggestions: [] };
      }

      const location = jsonc.getLocation(model.getValue(), model.getOffsetAt(position));

      return {
        suggestions: Array.from(completions.entries())
          .filter(([k, v]) => location.matches(k))
          .map(([k, v]) => v({ position, commands })),
      };
    },
  });
}
