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

import * as Monaco from "@kie-tools-core/monaco-editor";

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
    keywords: [
      "for",
      "in",
      "return",
      "if",
      "then",
      "else",
      "some",
      "every",
      "satisfies",
      "instance",
      "of",
      "function",
      "external",
      "or",
      "and",
      "between",
      "not",
      "null",
    ],
    functions: [
      "abs",
      "after",
      "all",
      "any",
      "append",
      "before",
      "ceiling",
      "code",
      "coincides",
      "concatenate",
      "contains",
      "count",
      "date",
      "date and time",
      "day of week",
      "day of year",
      "decimal",
      "decision table",
      "distinct values",
      "duration",
      "during",
      "ends",
      "even",
      "exp",
      "finished by",
      "finishes",
      "flatten",
      "floor",
      "get entries",
      "get value",
      "includes",
      "index of",
      "insert before",
      "invoke",
      "list contains",
      "log",
      "lower case",
      "matches",
      "max",
      "mean",
      "median",
      "meets",
      "met by",
      "min",
      "mode",
      "modulo",
      "month of year",
      "nn all",
      "nn any",
      "nn count",
      "nn max",
      "nn mean",
      "nn median",
      "nn min",
      "nn mode",
      "nn stddev",
      "nn sum",
      "not",
      "now",
      "number",
      "odd",
      "overlapped after by",
      "overlapped before by",
      "overlapped by",
      "overlaps after",
      "overlaps before",
      "overlaps",
      "product",
      "remove",
      "replace",
      "reverse",
      "sort",
      "split",
      "sqrt",
      "started by",
      "starts with",
      "starts",
      "stddev",
      "string length",
      "string",
      "sublist",
      "substring after",
      "substring before",
      "substring",
      "sum",
      "time",
      "today",
      "union",
      "upper case",
      "week of years",
      "years and months duration",
    ],
    tokenizer: {
      root: [
        [/(?:true|false)/, "feel-boolean"],
        [/[0-9]+/, "feel-numeric"],
        [/(?:"(?:.*?)")/, "feel-string"],
        [/[\w$]*[a-z_$][\w$]*/, { cases: { "@keywords": "feel-keyword", "@functions": "feel-function" } }],
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
      ["abs(n)", "abs($1)"],
      ["after(range, point)", "after($1, $2)"],
      ["after(range1, range2)", "after($1, $2)"],
      ["after(point, range)", "after($1, $2)"],
      ["after(point1, point2)", "after($1, $2)"],
      ["all(list)", "all($1)"],
      ["any(list)", "any($1)"],
      ["append(list, item)", "append($1, $2)"],
      ["before(range, point)", "before($1, $2)"],
      ["before(range1, range2)", "before($1, $2)"],
      ["before(point, range)", "before($1, $2)"],
      ["before(point1, point2)", "before($1, $2)"],
      ["ceiling(n)", "ceiling($1)"],
      ["code(value)", "code($1)"],
      ["coincides(range1, range2)", "coincides($1, $2)"],
      ["coincides(point1, point2)", "coincides($1, $2)"],
      ["concatenate(list)", "concatenate($1)"],
      ["contains(string, match)", "contains($1, $2)"],
      ["count(list)", "count($1)"],
      ["date and time(date, time)", "date and time($1, $2)"],
      ["date and time(from)", "date and time($1)"],
      ["date and time(year, month, day, hour, minute, second)", "date and time($1, $2, $3, $4, $5, $6)"],
      [
        "date and time(year, month, day, hour, minute, second, hour offset)",
        "date and time($1, $2, $3, $4, $5, $6, $7)",
      ],
      ["date and time(year, month, day, hour, minute, second, timezone)", "date and time($1, $2, $3, $4, $5, $6, $7)"],
      ["date(from)", "date($1)"],
      ["date(year, month, day)", "date($1, $2, $3)"],
      ["day of week(date)", "day of week($1)"],
      ["day of year(date)", "day of year($1)"],
      ["decimal(n, scale)", "decimal($1, $2)"],
      [
        "decision table(ctx, outputs, input expression list, input values list, output values, rule list, hit policy, default output value)",
        "decision table($1, $2, $3, $4, $5, $6, $7, $8)",
      ],
      ["distinct values(list)", "distinct values($1)"],
      ["duration(from)", "duration($1)"],
      ["during(range1, range2)", "during($1, $2)"],
      ["during(point, range)", "during($1, $2)"],
      ["ends with(string, match)", "ends with($1, $2)"],
      ["even(number)", "even($1)"],
      ["exp(number)", "exp($1)"],
      ["finished by(range, point)", "finished by($1, $2)"],
      ["finished by(range1, range2)", "finished by($1, $2)"],
      ["finishes(range1, range2)", "finishes($1, $2)"],
      ["finishes(point, range)", "finishes($1, $2)"],
      ["flatten(list)", "flatten($1)"],
      ["floor(n)", "floor($1)"],
      ["get entries(m)", "get entries($1)"],
      ["get value(m, key)", "get value($1, $2)"],
      ["includes(range, index)", "includes($1, $2)"],
      ["includes(range1, range2)", "includes($1, $2)"],
      ["index of(list, match)", "index of($1, $2)"],
      ["insert before(list, position, newItem)", "insert before($1, $2, $3)"],
      ["invoke(ctx, namespace, model name, decision name, parameters)", "invoke($1, $2, $3, $4, $5)"],
      ["list contains(list, element)", "list contains($1, $2)"],
      ["log(number)", "log($1)"],
      ["lower case(string)", "lower case($1)"],
      ["matches(input, pattern)", "matches($1, $2)"],
      ["matches(input, pattern, flags)", "matches($1, $2, $3)"],
      ["max(list)", "max($1)"],
      ["mean(list)", "mean($1)"],
      ["median(list)", "median($1)"],
      ["meets(range1, range2)", "meets($1, $2)"],
      ["met by(range1, range2)", "met by($1, $2)"],
      ["min(list)", "min($1)"],
      ["mode(list)", "mode($1)"],
      ["modulo(dividend, divisor)", "modulo($1, $2)"],
      ["month of year(date)", "month of year($1)"],
      ["nn all(list)", "nn all($1)"],
      ["nn any(list)", "nn any($1)"],
      ["nn count(list)", "nn count($1)"],
      ["nn max(list)", "nn max($1)"],
      ["nn mean(list)", "nn mean($1)"],
      ["nn median(list)", "nn median($1)"],
      ["nn min(list)", "nn min($1)"],
      ["nn mode(list)", "nn mode($1)"],
      ["nn stddev(list)", "nn stddev($1)"],
      ["nn sum(list)", "nn sum($1)"],
      ["not(negand)", "not($1)"],
      ["now()", "now()"],
      ["number(from, grouping separator, decimal separator)", "number($1, $2, $3)"],
      ["odd(number)", "odd($1)"],
      ["overlapped after by(range1, range2)", "overlapped after by($1, $2)"],
      ["overlapped before by(range1, range2)", "overlapped before by($1, $2)"],
      ["overlapped by(range1, range2)", "overlapped by($1, $2)"],
      ["overlaps after(range1, range2)", "overlaps after($1, $2)"],
      ["overlaps before(range1, range2)", "overlaps before($1, $2)"],
      ["overlaps(range1, range2)", "overlaps($1, $2)"],
      ["product(list)", "product($1)"],
      ["remove(list, position)", "remove($1, $2)"],
      ["replace(input, pattern, replacement)", "replace($1, $2, $3)"],
      ["replace(input, pattern, replacement, flags)", "replace($1, $2, $3, $4)"],
      ["reverse(list)", "reverse($1)"],
      ["sort()", "sort()"],
      ["sort(ctx, list, precedes)", "sort($1, $2, $3)"],
      ["sort(list)", "sort($1)"],
      ["split(string, delimiter)", "split($1, $2)"],
      ["split(string, delimiter, flags)", "split($1, $2, $3)"],
      ["sqrt(number)", "sqrt($1)"],
      ["started by(range, point)", "started by($1, $2)"],
      ["started by(range1, range2)", "started by($1, $2)"],
      ["starts with(string, match)", "starts with($1, $2)"],
      ["starts(range1, range2)", "starts($1, $2)"],
      ["starts(point, range)", "starts($1, $2)"],
      ["stddev(list)", "stddev($1)"],
      ["string length(string)", "string length($1)"],
      ["string(from)", "string($1)"],
      ["string(mask, p)", "string($1, $2)"],
      ["sublist(list, start position)", "sublist($1, $2)"],
      ["sublist(list, start position, length)", "sublist($1, $2, $3)"],
      ["substring after(string, match)", "substring after($1, $2)"],
      ["substring before(string, match)", "substring before($1, $2)"],
      ["substring(string, start position)", "substring($1, $2)"],
      ["substring(string, start position, length)", "substring($1, $2, $3)"],
      ["sum(list)", "sum($1)"],
      ["time(from)", "time($1)"],
      ["time(hour, minute, second)", "time($1, $2, $3)"],
      ["time(hour, minute, second, offset)", "time($1, $2, $3, $4)"],
      ["today()", "today()"],
      ["union(list)", "union($1)"],
      ["upper case(string)", "upper case($1)"],
      ["week of year(date)", "week of year($1)"],
      ["years and months duration(from, to)", "years and months duration($1, $2)"],
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
