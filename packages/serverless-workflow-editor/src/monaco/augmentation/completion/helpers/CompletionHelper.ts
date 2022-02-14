/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { CancellationToken, editor, languages, Position } from "monaco-editor";
import { ASTDocument, ASTNode } from "../../language/parser";
import { TextDocument } from "vscode-languageserver-types";
import { MonacoLanguage } from "../../language";

export type MonacoCompletionContext = {
  model: editor.ITextModel;
  position: Position;
  context: languages.CompletionContext;
  token: CancellationToken;
};

export type CompletionContext = {
  node: ASTNode;
  astDocument: ASTDocument;
  document: TextDocument;
  language: MonacoLanguage;
  monacoContext: MonacoCompletionContext;
};

export interface CompletionHelper {
  fillSuggestions: (consumer: (suggestions: languages.CompletionItem[]) => void, context: CompletionContext) => void;
}

export abstract class AbstractCompletionHelper implements CompletionHelper {
  abstract matches: (node: ASTNode) => boolean;
  abstract buildSuggestions: (context: CompletionContext) => languages.CompletionItem[] | undefined;

  fillSuggestions(consumer: (suggestions: languages.CompletionItem[]) => void, context: CompletionContext): void {
    if (context.node && this.matches(context.node)) {
      const suggestions = this.buildSuggestions(context);
      if (suggestions) {
        consumer(suggestions);
      }
    }
  }
}
