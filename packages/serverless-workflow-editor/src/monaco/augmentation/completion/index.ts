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

import * as jsonService from "vscode-json-languageservice";
import { CancellationToken, editor, languages, Position } from "monaco-editor";
import { MonacoCompletionHelper } from "./helpers";
import * as monaco from "monaco-editor";

export type CompletionArgs = {
  model: editor.ITextModel;
  position: Position;
  context: languages.CompletionContext;
  token: CancellationToken;
};

const jsonLangService = jsonService.getLanguageService({});

export function initCompletion() {
  monaco.languages.registerCompletionItemProvider("json", {
    provideCompletionItems(
      model: editor.ITextModel,
      position: Position,
      context: languages.CompletionContext,
      token: CancellationToken
    ): languages.ProviderResult<languages.CompletionList> {
      return getSuggestions({
        model,
        position,
        context,
        token,
      });
    },
  });
}

const getSuggestions = ({
  model,
  position,
  context,
  token,
}: CompletionArgs): languages.ProviderResult<languages.CompletionList> => {
  const document = jsonService.TextDocument.create("", "json", model.getVersionId(), model.getValue());
  const offset = document.offsetAt({
    line: position.lineNumber - 1,
    character: position.column,
  });

  const json = jsonLangService.parseJSONDocument(document);
  const node = json.getNodeFromOffset(offset, true);

  const suggestions: languages.CompletionItem[] = [];

  const consumer = (helperSuggestions: languages.CompletionItem[]): void => {
    if (helperSuggestions) {
      suggestions.push(...helperSuggestions);
    }
  };

  if (node) {
    MonacoCompletionHelper.fillSuggestions(consumer, {
      json,
      node,
      document,
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
};
