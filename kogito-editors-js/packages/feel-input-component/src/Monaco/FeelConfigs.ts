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

import * as Monaco from "monaco-editor";

export const MONACO_FEEL_LANGUAGE = "feel-language";

export const MONACO_FEEL_THEME = "feel-theme";

export const feelTheme = (): Monaco.editor.IStandaloneThemeData => {
  return {
    base: "vs",
    inherit: true,
    rules: [
      { token: "feel-keyword", foreground: "26268C", fontStyle: "bold" },
      { token: "feel-numeric", foreground: "3232E7" },
      { token: "feel-boolean", foreground: "26268D", fontStyle: "bold" },
      { token: "feel-string", foreground: "2A9343" },
      { token: "feel-function", foreground: "3232E8" },
    ],
    colors: {
      "editorLineNumber.foreground": "#000000",
    },
  };
};

export const feelTokensConfig = (): Monaco.languages.IMonarchLanguage => {
  return {
    tokenizer: {
      root: [
        [/(?:true|false)/, "feel-boolean"],
        [/[0-9]+/, "feel-numeric"],
        [/(?:"(?:.*?)")/, "feel-string"],
        [/(?:(?:[a-z ]+\()|(?:\()|(?:\)))/, "feel-function"],
        [/(?:if|then|else)/, "feel-keyword"],
        [
          /(?:for|in|return|if|then|else|some|every|satisfies|instance|of|function|external|or|and|between|not|null)/,
          "feel-keyword",
        ],
      ],
    },
  };
};

export const feelDefaultConfig = (
  options?: Monaco.editor.IStandaloneEditorConstructionOptions
): Monaco.editor.IStandaloneEditorConstructionOptions => {
  return {
    language: MONACO_FEEL_LANGUAGE,
    theme: MONACO_FEEL_THEME,
    fontSize: 15,
    contextmenu: false,
    useTabStops: false,
    folding: false,
    automaticLayout: true,
    lineNumbersMinChars: 0,
    overviewRulerBorder: false,
    scrollBeyondLastLine: false,
    hideCursorInOverviewRuler: true,
    scrollbar: {
      useShadows: false,
    },
    minimap: {
      enabled: false,
    },

    ...options,
  };
};

export const feelDefaultSuggestions = (): Monaco.languages.CompletionItem[] => {
  const suggestions: Monaco.languages.CompletionItem[] = [];

  const suggestionTypes = {
    Snippet: [
      ["if", "if $1 then\n\t$0\nelse\n\t"],
      ["for", "for element in $1 return\n\t$0"],
    ],
    Function: [
      // String
      ["substring(string, start position, length?)", "substring($1, $2, $3)"],
      ["string length(string)", "string length($1)"],
      ["upper case(string)", "upper case($1)"],
      ["lower case(string)", "lower case($1)"],
      ["substring before(string, match)", "substring before($1, $2)"],
      ["substring after(string, match)", "substring after($1, $2)"],
      ["replace(input, pattern, replacement, flags?)", "replace($1, $2, $3, $4)"],
      ["contains(string, match)", "contains($1, $2)"],
      ["starts with(string, match)", "starts with($1, $2)"],
      ["ends with(string, match)", "ends with($1, $2)"],
      ["matches(input, pattern, flags?)", "matches($1, $2, $3)"],
      ["split(string, delimiter)", "split($1, $2)"],

      // List
      ["list contains(list, element)", "list contains($1, $2)"],
      ["count(list)", "count($1)"],
      ["min(list)", "min($1)"],
      ["max(list)", "max($1)"],
      ["sum(list)", "sum($1)"],
      ["mean(list)", "mean($1)"],
      ["and(list)", "and($1)"],
      ["or(list)", "or($1)"],
      ["sublist(list, start position, length?)", "sublist($1, $2, $3)"],
      ["append(list, item...)", "append($1, $2)"],
      ["concatenate(list...)", "concatenate($1)"],
      ["insert before(list, position, newItem)", "insert before($1, $2, $3)"],
      ["remove(list, position)", "remove($1, $2)"],
      ["reverse(list)", "remove($1)"],
      ["index of(list, match)", "index of($1, $2)"],
      ["union(list...)", "union($1)"],
      ["distinct values(list)", "distinct values($1)"],
      ["flatten(list)", "flatten($1)"],
      ["product(list)", "product($1)"],
      ["median(list)", "median($1)"],
      ["stddev(list)", "stddev($1)"],
      ["mode(list)", "mode($1)"],

      // Number
      ["decimal(n, scale)", "decimal($1, $2)"],
      ["floor(n)", "floor($1)"],
      ["ceiling(n)", "ceiling($1)"],
      ["abs(n)", "abs($1)"],
      ["modulo(dividend, divisor)", "modulo($1, $2)"],
      ["sqrt(number)", "sqrt($1)"],
      ["log(number)", "log($1)"],
      ["exp(number)", "exp($1)"],
      ["odd(number)", "odd($1)"],
      ["even(number)", "even($1)"],

      // Boolean
      ["not(negand)", "not($1)"],
    ],
  };

  for (const suggestion of suggestionTypes.Snippet) {
    suggestions.push({
      kind: Monaco.languages.CompletionItemKind.Keyword,
      insertTextRules: Monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      label: suggestion[0],
      insertText: suggestion[1],
    } as Monaco.languages.CompletionItem);
  }

  for (const suggestion of suggestionTypes.Function) {
    suggestions.push({
      kind: Monaco.languages.CompletionItemKind.Function,
      insertTextRules: Monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      label: suggestion[0],
      insertText: suggestion[1],
    } as Monaco.languages.CompletionItem);
  }

  return suggestions;
};
