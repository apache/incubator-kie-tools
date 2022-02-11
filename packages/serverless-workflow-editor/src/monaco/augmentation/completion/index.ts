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
import { TextDocument } from "vscode-languageserver-types";
import { CancellationToken, editor, languages, Position } from "monaco-editor";
import { MonacoCompletionHelper } from "./helpers";
import { MonacoAugmentation } from "../MonacoAugmentation";
import { MonacoLanguage } from "../language";

export type CompletionArgs = {
  model: editor.ITextModel;
  position: Position;
  context: languages.CompletionContext;
  token: CancellationToken;
};

export function initCompletion(augmentation: MonacoAugmentation): void {
  monaco.languages.registerCompletionItemProvider(augmentation.language.languageId, {
    provideCompletionItems(
      model: editor.ITextModel,
      position: Position,
      context: languages.CompletionContext,
      token: CancellationToken
    ): languages.ProviderResult<languages.CompletionList> {
      if (!augmentation) {
        return null;
      }

      const language: MonacoLanguage = augmentation.language;

      if (!language.parser) {
        return null;
      }

      const document = TextDocument.create("", language.languageId, model.getVersionId(), model.getValue());

      const astDocument = language.parser.parseContent(document);

      const offset = document.offsetAt({
        line: position.lineNumber - 1,
        character: position.column,
      });

      const node = astDocument.getNodeFromOffset(offset);

      const suggestions: languages.CompletionItem[] = [];

      const consumer = (helperSuggestions: languages.CompletionItem[]): void => {
        if (helperSuggestions) {
          suggestions.push(...helperSuggestions);
        }
      };

      if (node) {
        MonacoCompletionHelper.fillSuggestions(consumer, {
          astDocument,
          node,
          document,
          language: augmentation.language,
          monacoContext: {
            model,
            context,
            token,
            position,
          },
        });
      }

      return {
        suggestions,
      };
    },
  });
}
