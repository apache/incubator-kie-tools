/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
  initializeFeelLanguage,
  initializeMonacoTheme,
  initializeFeelTokensProvider,
  feelTheme,
  feelTokensConfig,
  initializeFeelCompletionItemProvider,
} from "../../";
import * as Monaco from "monaco-editor";
import { SuggestionProvider } from "../../../showcase/src/lib/Monaco/FeelInitializer";

describe("FeelInitializer", () => {
  test("initializeFeelLanguage", () => {
    initializeFeelLanguage();

    const languages = Monaco.languages.getLanguages();
    const lastRegistered = languages[languages.length - 1];

    expect(lastRegistered).toEqual({
      aliases: ["feel-language", "feel", "feel-dmn"],
      id: "feel-language",
      mimetypes: ["text/feel"],
    });
  });

  test("initializeMonacoTheme", () => {
    const spyDefineTheme = jest.spyOn(Monaco.editor, "defineTheme");
    initializeMonacoTheme();
    expect(spyDefineTheme).toBeCalledWith("feel-theme", feelTheme());
  });

  test("initializeFeelTokensProvider", () => {
    const spySetMonarchTokensProvider = jest.spyOn(Monaco.languages, "setMonarchTokensProvider");
    initializeFeelTokensProvider();
    expect(spySetMonarchTokensProvider).toBeCalledWith("feel-language", feelTokensConfig());
  });

  test("initializeFeelCompletionItemProvider when provider is not passed returns default suggestions", () => {
    const spyRegisterCompletionItemProvider = jest.spyOn(Monaco.languages, "registerCompletionItemProvider");
    const model = {} as Monaco.editor.ITextModel;
    const position = {} as Monaco.Position;

    const suggestions = initializeFeelCompletionItemProvider()(model, position).suggestions;

    expect(spyRegisterCompletionItemProvider).toBeCalledWith("feel-language", expect.anything());
    expect(suggestions).toHaveLength(47);
  });

  test("initializeFeelCompletionItemProvider when provider is passed returns provider suggestions", () => {
    const spyRegisterCompletionItemProvider = jest.spyOn(Monaco.languages, "registerCompletionItemProvider");
    const provider = (_feelExpression: string, _row: number, _col: number) => {
      return [
        {
          label: "label",
          insertText: "insertText",
        } as Monaco.languages.CompletionItem,
      ];
    };
    const model = {
      getValue: () => "value",
    } as Monaco.editor.ITextModel;
    const position = {} as Monaco.Position;

    const suggestions = initializeFeelCompletionItemProvider(provider)(model, position).suggestions;

    expect(spyRegisterCompletionItemProvider).toBeCalledWith("feel-language", expect.anything());
    expect(suggestions).toHaveLength(1);
  });
});
