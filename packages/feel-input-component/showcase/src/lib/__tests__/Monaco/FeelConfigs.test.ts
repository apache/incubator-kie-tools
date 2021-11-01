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
  feelTheme,
  feelTokensConfig,
  feelDefaultConfig,
  feelDefaultSuggestions,
  MONACO_FEEL_LANGUAGE,
  MONACO_FEEL_THEME,
} from "../../";

describe("FeelConfigs", () => {
  test("feelTheme", () => {
    const theme = feelTheme();

    expect(theme.base).toBe("vs");
    expect(theme.inherit).toBeTruthy();
    expect(theme.rules).toHaveLength(5);
    expect(theme.colors["editorLineNumber.foreground"]).toBe("#000000");
  });

  test("feelTokensConfig", () => {
    const tokens = feelTokensConfig().tokenizer.root;

    expect(tokens).toHaveLength(6);
    expect(tokens.map((t: any) => t[1])).toEqual([
      "feel-boolean",
      "feel-numeric",
      "feel-string",
      "feel-function",
      "feel-keyword",
      "feel-keyword",
    ]);
  });

  test("feelDefaultConfig", () => {
    const value = "test";
    const config = feelDefaultConfig({ value });

    expect(config.language).toBe(MONACO_FEEL_LANGUAGE);
    expect(config.theme).toBe(MONACO_FEEL_THEME);
    expect(config.fontSize).toBe(15);
    expect(config.contextmenu).toBe(false);
    expect(config.useTabStops).toBe(false);
    expect(config.folding).toBe(false);
    expect(config.automaticLayout).toBe(true);
    expect(config.lineNumbersMinChars).toBe(0);
    expect(config.overviewRulerBorder).toBe(false);
    expect(config.scrollBeyondLastLine).toBe(false);
    expect(config.hideCursorInOverviewRuler).toBe(true);
    expect(config.scrollbar?.useShadows).toBe(false);
    expect(config.minimap?.enabled).toBe(false);
    expect(config.value).toBe(value);
  });

  test("feelDefaultSuggestions", () => {
    const suggestions = feelDefaultSuggestions();

    expect(suggestions).toHaveLength(47);

    // Keyword suggestions
    expect(suggestions[0]).toEqual({
      kind: "Keyword",
      insertTextRules: "InsertAsSnippet",
      label: "if",
      insertText: "if $1 then\n\t$0\nelse\n\t",
    });
    expect(suggestions[1]).toEqual({
      kind: "Keyword",
      insertTextRules: "InsertAsSnippet",
      label: "for",
      insertText: "for element in $1 return\n\t$0",
    });

    // Function suggestions
    expect(suggestions[2]).toEqual({
      kind: "Function",
      insertTextRules: "InsertAsSnippet",
      label: "substring(string, start position, length?)",
      insertText: "substring($1, $2, $3)",
    });
    expect(suggestions[3]).toEqual({
      kind: "Function",
      insertTextRules: "InsertAsSnippet",
      label: "string length(string)",
      insertText: "string length($1)",
    });
  });
});
