/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as Monaco from "@kie-tools-core/monaco-editor";
import { ReservedWords } from "@kie-tools/dmn-feel-antlr4-parser";
import { Element } from "./themes/Element";

export const MONACO_FEEL_LANGUAGE = "feel-language";

export const MONACO_FEEL_THEME = "feel-theme";

export const feelTheme = (): Monaco.editor.IStandaloneThemeData => {
  return {
    base: "vs",
    inherit: true,
    rules: [
      { token: Element[Element.FeelKeyword], foreground: "#26268C", fontStyle: "bold" },
      { token: Element[Element.FeelNumeric], foreground: "#1750EB" },
      { token: Element[Element.FeelBoolean], foreground: "#26268D", fontStyle: "bold" },
      { token: Element[Element.FeelString], foreground: "#067D17" },
      { token: Element[Element.FeelFunction], foreground: "#00627A" },
      { token: Element[Element.Variable], foreground: "#917632", fontStyle: "underline" },
      { token: Element[Element.FunctionCall], foreground: "#917632", fontStyle: "underline italic" },
      { token: Element[Element.UnknownVariable], foreground: "#ff0000", fontStyle: "underline bold" },
      { token: Element[Element.FunctionParameterVariable], foreground: "#036e9b", fontStyle: "italic" },
      { token: Element[Element.DynamicVariable], foreground: "#8b97a2", fontStyle: "underline" },
    ],
    colors: {
      "editorLineNumber.foreground": "#000000",
    },
  };
};

export const feelTokensConfig = (): Monaco.languages.IMonarchLanguage => {
  return {
    keywords: Array.from(ReservedWords.FeelKeywords),
    functions: Array.from(ReservedWords.FeelFunctions),
    tokenizer: {
      root: [
        [/(?:true|false)/, Element[Element.FeelBoolean]],
        [/[0-9]+/, Element[Element.FeelNumeric]],
        [/(?:"(?:.*?)")/, Element[Element.FeelString]],
        [/([a-z{1}][a-z_\s]*[a-z$])(?=\()/, { cases: { "@functions": Element[Element.FeelFunction] } }],
        [/[\w$]*[a-z_$][\w$]*/, { cases: { "@keywords": Element[Element.FeelKeyword] } }],
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
    wordBasedSuggestions: false,
    "semanticHighlighting.enabled": true,

    ...options,
  };
};

export const feelDefaultSuggestions = (): Monaco.languages.CompletionItem[] => {
  const suggestions: Monaco.languages.CompletionItem[] = [];

  const suggestionTypes = {
    keywords: [
      "and",
      "between",
      "else",
      "every",
      "external",
      "false",
      "for",
      "function",
      "if",
      "in",
      "instance of",
      "not",
      "null",
      "or",
      "return",
      "then",
      "satisfies",
      "some",
      "true",
    ],
    snippet: [
      ["if", "if $1 then\n\t$0\nelse\n\t"],
      ["instance of", "instance of $0"],
      ["for", "for element in $1 return\n\t$0"],
    ],
    function: [
      {
        label: "abs(n)",
        insertText: "abs($1)",
        description: "Returns the absolute value of `n`",
        parameters: [["n", `\`number\`, \`days and time duration\`, \`years and months duration\``]],
        examples: ["abs( 10 ) = 10", "abs( -10 ) = 10", 'abs( @"PT5H" ) = @"PT5H"', 'abs( @"-PT5H" ) = @"PT5H"'],
      },
      {
        label: "after(point1, point2)",
        insertText: "after($1, $2)",
        description: "Returns true when `point1` is after `point2`",
        parameters: [
          ["point1", `\`number\``],
          ["point2", `\`number\``],
        ],
        examples: ["after( 10, 5 ) = true", "after( 5, 10 ) = false"],
      },
      {
        label: "after(point, range)",
        insertText: "after($1, $2)",
        description: "Returns true when `point` is after `range`",
        parameters: [
          ["point", `\`number\``],
          ["range", `\`range\` (\`interval\`)`],
        ],
        examples: ["after( 12, [1..10] ) = true", "after( 10, [1..10) ) = true", "after( 10, [1..10] ) = false"],
      },
      {
        label: "after(range, point)",
        insertText: "after($1, $2)",
        description: "Returns true when `range` is after `point`",
        parameters: [
          ["range", `\`range\` (\`interval\`)`],
          ["point", `\`number\``],
        ],
        examples: [
          "after( [11..20], 12 ) = false",
          "after( [11 ..20], 10 ) = true",
          "after( (11..20], 11 ) = true",
          "after( [11 ..20], 11 ) = false",
        ],
      },
      {
        label: "after(range1, range2)",
        insertText: "after($1, $2)",
        description: "Returns true when `range1` is after `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "after( [11..20], [1..10] ) = true",
          "after( [1 ..10], [11 ..20] ) = false",
          "after( [11 ..20], [1.. 11) ) = true",
          "after( (11..20], [1..11] ) = true",
        ],
      },
      {
        label: "all(list)",
        insertText: "all($1)",
        description: "Returns true if all elements in the `list` are true.",
        parameters: [["list", `\`list\` of \`boolean\` elements`]],
        examples: [
          "all( [false,null,true] ) = false",
          "all( [true,null,true] ) = false",
          "all( true ) = true",
          "all( [] ) = true",
          "all( 0 ) = null",
        ],
      },
      {
        label: "any(list)",
        insertText: "any($1)",
        description:
          "Returns true if any `list` item is true, else false if empty or all `list` items are false, else null",
        parameters: [["list", `\`list\` of \`boolean\` elements`]],
        examples: [
          "any( [false,null,true] ) = true",
          "any( false ) = false",
          "any( [] ) = false",
          "any( null ) = null",
          "any( 0 ) = null",
        ],
      },
      {
        label: "append(list, item)",
        insertText: "append($1, $2)",
        description: "Returns new list with items appended",
        parameters: [
          ["list", `\`list\``],
          ["item", "Any type (even more items)"],
        ],
        examples: ["append( [1], 2, 3 ) = [1,2,3]", "append( [1], 2 ) = [1,2]", "append( [], 1, 2 ) = [1,2]"],
      },
      {
        label: "before(point1, point2)",
        insertText: "before($1, $2)",
        description: "Returns true when `point1` is before `point2`",
        parameters: [
          ["point1", `\`number\``],
          ["point2", `\`number\``],
        ],
        examples: ["before( 1, 10 ) = true", "before( 10, 1 ) = false"],
      },
      {
        label: "before(point, range)",
        insertText: "before($1, $2)",
        description: "Returns true when `point` is before `range`",
        parameters: [
          ["point", `\`number\``],
          ["range", `\`range\` (\`interval\`)`],
        ],
        examples: ["before( 1, [1.. 10] ) = false", "before( 1, (1..10] ) = true", "before( 1, [5.. 10] )= true"],
      },
      {
        label: "before(range, point)",
        insertText: "before($1, $2)",
        description: "Returns true when a `range` is before `point`",
        parameters: [
          ["range", `\`range\` (\`interval\`)`],
          ["point", `\`number\``],
        ],
        examples: ["before( [1..10], 10 ) = false", "before( [1..10), 10 ) = true", "before( [1..10], 15 ) = true"],
      },
      {
        label: "before(range1, range1)",
        insertText: "before($1, $2)",
        description: "Returns true when `range1` is before `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "before( [1..10], [15..20] ) = true",
          "before( [1..10], [10..20] ) = false",
          "before( [1..10), [10..20] ) = true",
          "before( [1..10], (10..20] ) = true",
        ],
      },
      {
        label: "ceiling(n)",
        insertText: "ceiling($1)",
        description: "Returns `n` with rounding mode ceiling. If `n` is null the result is null.",
        parameters: [["n", `\`number\``]],
        examples: ["ceiling( 1.5 ) = 2", "ceiling( -1.5 ) = -1"],
      },
      {
        label: "ceiling(n, scale)",
        insertText: "ceiling($1, $2)",
        description:
          "Returns `n` with given scale and rounding mode ceiling. If at least one of `n` or `scale` is null, the result is null. The `scale` must be in the range [−6111..6176].",
        parameters: [
          ["n", `\`number\``],
          ["scale", `\`number\``],
        ],
        examples: ["ceiling( -1.56, 1 ) = -1.5"],
      },
      {
        label: "code(value)",
        insertText: "code($1)",
        description: "",
        parameters: [],
        examples: [],
      },
      {
        label: "coincides(point1, point2)",
        insertText: "coincides($1, $2)",
        description: "Returns true when `point1` coincides with `point2`",
        parameters: [
          ["point1", `\`number\``],
          ["point2", `\`number\``],
        ],
        examples: ["coincides( 5, 5 ) = true", "coincides( 3, 4 ) = false"],
      },
      {
        label: "coincides(range1, range2)",
        insertText: "coincides($1, $2)",
        description: "Returns true when `range1` coincides with `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "coincides( [1..5], [1..5] ) = true",
          "coincides( (1..5), [1..5] ) = false",
          "coincides( [1..5], [2..6] ) = false",
        ],
      },
      {
        label: "concatenate(list...)",
        insertText: "concatenate($1)",
        description: "Returns a new list that is a concatenation of the arguments",
        parameters: [["list", `Multiple \`list\``]],
        examples: ["concatenate( [1,2], [3] ) = [1,2,3]"],
      },
      {
        label: "contains(string, match)",
        insertText: "contains($1, $2)",
        description: "Does the `string` contain the `match`?",
        parameters: [
          ["string", `string`],
          ["match", `string`],
        ],
        examples: ['contains( "foobar", "of" ) = false', 'contains( "foobar", "fo" ) = true'],
      },
      {
        label: "context(entries)",
        insertText: "context($1)",
        description:
          "Returns a new `context` that includes all specified entries. If a `context` item contains additional entries beyond the required `key` and `value` entries, the additional entries are ignored. If a `context` item is missing the required `key` and `value` entries, the final result is null.",
        parameters: [["entries", `\`list\` of \`context\``]],
        examples: [
          'context( [{key:"a", value:1}, {key:"b", value:2}] ) = { a:1, b:2 }',
          'context([ {key:"a", value:1}, {key:"b", value:2, something: "else"}] ) = {a:1, b:2}',
          'context( [{key:"a", value:1}, {key:"b"}] ) = null',
        ],
      },
      {
        label: "context merge(contexts)",
        insertText: "context merge($1)",
        description:
          "Returns a new `context` that includes all entries from the given `contexts`; if some of the keys are equal, the entries are overridden. The entries are overridden in the same order as specified by the supplied parameter, with new entries added as the last entry in the new context.",
        parameters: [["contexts", `\`list\` of \`context\``]],
        examples: ["context merge( [{x:1}, {y:2}] ) = {x:1, y:2}", "context merge( [{x:1, y:0}, {y:2}] ) = {x:1, y:2}"],
      },
      {
        label: "context put(context, key, value)",
        insertText: "context put($1, $2, $3)",
        description:
          "Returns a new `context` that includes the new entry, or overrides the existing value if an entry for the same key already exists in the supplied `context` parameter. A new entry is added as the last entry of the new context. If overriding an existing entry, the order of the keys maintains the same order as in the original context.",
        parameters: [
          ["context", `\`context\``],
          ["key", `\`string\``],
          ["value", `\`Any\` type`],
        ],
        examples: [
          'context put( {x:1}, "y", 2 ) = {x:1, y:2}',
          'context put( {x:1, y:0}, "y", 2 ) = {x:1, y:2}',
          'context put( {x:1, y:0, z:0} , "y", 2) = {x:1, y:2, z:0}',
        ],
      },
      {
        label: "context put(context, keys, value)",
        insertText: "context put($1, $2, $3)",
        description:
          "Returns the composite of nested invocations to `context put()` for each item in keys hierarchy in `context`.",
        parameters: [
          ["context", `\`context\``],
          ["keys", `\`list\` of \`string\``],
          ["value", `\`Any\` type`],
        ],
        examples: [
          'context put( {x:1}, ["y"], 2 ) = context put( {x:1}, "y", 2) ',
          'context put( {x:1}, ["y"], 2 ) = {x:1, y:2}',
          'context put( {x:1, y: {a: 0} }, ["y", "a"], 2 ) = {x:1, y: {a: 2} }',
          `context put( {x:1, y: {a: 0} }, [], 2 ) = null`,
        ],
      },
      {
        label: "count(list)",
        insertText: "count($1)",
        description: "Returns size of `list`, or zero if `list` is empty",
        parameters: [["list", `\`list\``]],
        examples: ["count( [1,2,3] ) = 3", "count( [] ) = 0", "count( [1, [2,3]] ) = 2"],
      },
      {
        label: "date(from)",
        insertText: "date($1)",
        description: "convert `from` to a date",
        parameters: [["from", `\`string\` or \`date and time\``]],
        examples: [
          'date( "2012-12-25" ) – date( "2012-12-24" ) = duration( "P1D" )',
          'date( date and time( "2012-12-25T11:00:00Z" ) ) = date( "2012-12-25")',
        ],
      },
      {
        label: "date(year, month, day)",
        insertText: "date($1, $2, $3)",
        description: "Creates a date from `year`, `month`, `day` component values",
        parameters: [
          ["year", `\`number\``],
          ["month", `\`number\``],
          ["day", `\`number\``],
        ],
        examples: ['date( 2012, 12, 25 ) = date( "2012-12-25" )'],
      },
      {
        label: "date and time(from)",
        insertText: "date and time($1)",
        description: "convert `from` to a date and time",
        parameters: [["from", `string`]],
        examples: [
          'date and time( "2012-12-24T23:59:00" ) + duration( "PT1M" ) = date and time( "2012-12-25T00:00:00" )',
        ],
      },
      {
        label: "date and time(date, time)",
        insertText: "date and time($1, $2)",
        description: "Creates a date time from the given `date` (ignoring any time component) and the given `time`",
        parameters: [
          ["date", `\`date\` or \`date and time\``],
          ["time", `\`time\``],
        ],
        examples: [
          'date and time( "2012-12-24T23:59:00" ) = date and time( date( "2012-12-24" ), time ( “23:59:00" ) )',
        ],
      },
      {
        label: "date and time(date, time, timezone)",
        insertText: "date and time($1, $2, $3)",
        description: "Creates a date time from the given `date`, `time` and timezone",
        parameters: [
          ["date", `\`date\` or \`date and time\``],
          ["time", `\`time\``],
          ["timezone", `\`string\``],
        ],
        examples: [
          'date and time( date("2024-12-24"), time("23:59:00"), "Z" ) = date and time( "2024-12-24T23:59:00Z" )',
          'date and time( date("2024-12-24"), time("23:59:00"), "America/Costa_Rica" ) = date and time( "2024-12-24T23:59:00@America/Costa_Rica" )',
        ],
      },
      {
        label: "date and time(year, month, day, hour, minute, second)",
        insertText: "date and time($1, $2, $3, $4, $5, $6)",
        description: "Creates a date time from the given `year`, `month`, `day`, `hour`, `minute`, and `second`.",
        parameters: [
          ["year", `\`number\``],
          ["month", `\`number\``],
          ["day", `\`number\``],
          ["hour", `\`number\``],
          ["minute", `\`number\``],
          ["second", `\`number\``],
        ],
        examples: ['date and time( 2012, 12, 24, 23, 59, 59 ) = date and time( "2012-12-24T23:59:59" )'],
      },
      {
        label: "date and time(year, month, day, hour, minute, second, offset)",
        insertText: "date and time($1, $2, $3, $4, $5, $6, $7)",
        description:
          "Creates a date time from the given `year`, `month`, `day`, `hour`, `minute`, `second` and `offset`",
        parameters: [
          ["year", `\`number\``],
          ["month", `\`number\``],
          ["day", `\`number\``],
          ["hour", `\`number\``],
          ["minute", `\`number\``],
          ["second", `\`number\``],
          ["offset", `\`number\``],
        ],
        examples: ['date and time( 2012, 12, 24, 23, 59, 59, -2 ) = date and time( "2012-12-24T23:59:59-02:00" )'],
      },
      {
        label: "date and time(year, month, day, hour, minute, second, timezone)",
        insertText: "date and time($1, $2, $3, $4, $5, $6, $7)",
        description:
          "Creates a date time from the given `year`, `month`, `day`, `hour`, `minute`, `second` and `timezone`",
        parameters: [
          ["year", `\`number\``],
          ["month", `\`number\``],
          ["day", `\`number\``],
          ["hour", `\`number\``],
          ["minute", `\`number\``],
          ["second", `\`number\``],
          ["timezone", `\`string\``],
        ],
        examples: ['date and time( 2012, 12, 24, 23, 59, 59, "Z" ) = date and time( "2012-12-24T23:59:59Z" )'],
      },
      {
        label: "day of week(date)",
        insertText: "day of week($1)",
        description:
          "Returns the day of the week according to the Gregorian calendar enumeration: “Monday”, “Tuesday”, “Wednesday”, “Thursday”, “Friday”, “Saturday”, “Sunday”",
        parameters: [["date", `\`date\` or \`date and time\``]],
        examples: ['day of week( date(2019, 9, 17) ) = "Tuesday"'],
      },
      {
        label: "day of year(date)",
        insertText: "day of year($1)",
        description: "Returns the Gregorian number of the day within the year",
        parameters: [["date", `\`date\` or \`date and time\``]],
        examples: ["day of year( date(2019, 9, 17) ) = 260"],
      },
      {
        label: "decimal(n, scale)",
        insertText: "decimal($1, $2)",
        description: "Returns `n` with given `scale. The `scale` must be in the range [−6111..6176].`",
        parameters: [
          ["n", `\`number\``],
          ["scale", `\`number\``],
        ],
        examples: ["decimal( 1/3, 2 ) = .33", "decimal( 1.5, 0 ) = 2", "decimal( 2.5, 0 ) = 2"],
      },
      {
        label:
          "decision table(ctx, outputs, input expression list, input values list, output values, rule list, hit policy, default output value)",
        insertText: "decision table($1, $2, $3, $4, $5, $6, $7, $8)",
        description: "",
        parameters: [],
        examples: [],
      },
      {
        label: "distinct values(list)",
        insertText: "distinct values($1)",
        description: "Returns `list` without duplicates",
        parameters: [["list", `\`list\``]],
        examples: ["distinct values( [1,2,3,2,1] ) = [1,2,3]"],
      },
      {
        label: "duration(from)",
        insertText: "duration($1)",
        description: "Converts `from` to a days and time or years and months duration",
        parameters: [["from", `string`]],
        examples: [
          'date and time( "2012-12-24T23:59:00" ) - date and time( "2012-12-22T03:45:00" ) = duration( "P2DT20H14M" )',
          'duration( "P2Y2M" ) = duration( "P26M" )',
        ],
      },
      {
        label: "during(point, range)",
        insertText: "during($1, $2)",
        description: "Returns true when `point` is during `range`",
        parameters: [
          ["point", `\`number\``],
          ["range", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "during( 5, [1..10] ) = true",
          "during( 12, [1..10] ) = false",
          "during( 1, [1..10] ) = true",
          "during( 10, [1..10] ) = true",
          "during( 1, (1..10] ) = false",
          "during( 10, [1..10) ) = false",
        ],
      },
      {
        label: "during(range1, range2)",
        insertText: "during($1, $2)",
        description: "Returns true when a `range1` is during `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "during( [4..6], [1..10] ) = true",
          "during( [1..5], [1..10] ) = true",
          "during( (1..5], (1..10] ) = true",
          "during( (1..10), [1..10] ) = true",
          "during( [5..10), [1..10) ) = true",
          "during( [1..10), [1..10] ) = true",
          "during( (1..10], [1..10] ) = true",
          "during( [1..10], [1..10] ) = true",
        ],
      },
      {
        label: "ends with(string, match)",
        insertText: "ends with($1, $2)",
        description: "Does the `string` end with the `match`?",
        parameters: [
          ["string", `string`],
          ["match", `string`],
        ],
        examples: ['ends with("foobar", "r") = true'],
      },
      {
        label: "even(number)",
        insertText: "even($1)",
        description: "Returns true if `number` is even, false if it is odd",
        parameters: [
          ["string", `string`],
          ["match", `string`],
        ],
        examples: ["even( 5 ) = false", "even( 2 ) = true"],
      },
      {
        label: "exp(number)",
        insertText: "exp($1)",
        description: "Returns the Euler’s number e raised to the power of `number`.",
        parameters: [["number", `\`number\``]],
        examples: ["exp( 5 ) = 148.413159102577"],
      },
      {
        label: "finished by(range, point)",
        insertText: "finished by($1, $2)",
        description: "Returns true when `range` is finished by `point`",
        parameters: [
          ["range", `\`range\` (\`interval\`)`],
          ["point", `\`number\``],
        ],
        examples: ["finished by( [1..10], 10 ) = true", "finished by( [1..10), 10 ) = false"],
      },
      {
        label: "finished by(range1, range2)",
        insertText: "finished by($1, $2)",
        description: "Returns true when `range1` is finished by `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "finished by( [1..10], [5..10] ) = true",
          "finished by( [1..10], [5..10) ) = false",
          "finished by( [1..10), [5..10) ) = true",
          "finished by( [1..10], [1..10] ) = true",
          "finished by( [1..10], (1..10] ) = true",
        ],
      },
      {
        label: "finishes(point, range)",
        insertText: "finishes($1, $2)",
        description: "Returns true when `point` finishes `range`",
        parameters: [
          ["point", `\`number\``],
          ["range", `\`range\` (\`interval\`)`],
        ],
        examples: ["finishes( 10, [1..10] ) = true", "finishes( 10, [1..10) ) = false"],
      },
      {
        label: "finishes(range1, range2)",
        insertText: "finishes($1, $2)",
        description: "Returns true when `range1` finishes `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "finishes( 10, [1..10] ) = true",
          "finishes( 10, [1..10) ) = false",
          "finishes( [5..10], [1..10] ) = true",
          "finishes( [5..10), [1..10] ) = false",
          "finishes( [5..10), [1..10) ) = true",
          "finishes( [1..10], [1..10] ) = true",
          "finishes( (1..10], [1..10] ) = true",
        ],
      },
      {
        label: "flatten(list)",
        insertText: "flatten($1)",
        description: "Flatten nested lists",
        parameters: [["list", `\`list\``]],
        examples: ["flatten( [[1 ,2],[[3]], 4] ) = [1,2,3,4]"],
      },
      {
        label: "floor(n)",
        insertText: "floor($1)",
        description: "Returns `n` with rounding mode flooring. If `n` is null the result is null.",
        parameters: [["n", `\`number\``]],
        examples: ["floor(1.5) = 1"],
      },
      {
        label: "floor(n, scale)",
        insertText: "floor($1, $2)",
        description:
          "Returns `n` with given scale and rounding mode flooring. If at least one of `n` or scale is null, the result is null. The `scale` must be in the range [−6111..6176].",
        parameters: [
          ["n", `\`number\``],
          ["scale", `\`number\``],
        ],
        examples: ["floor( -1.56, 1 ) = -1.6"],
      },
      {
        label: "get entries(m)",
        insertText: "get entries($1)",
        description: "Produces a list of key,value pairs from a context `m`",
        parameters: [["m", `\`context\``]],
        examples: [
          'get entries( {key1 : "value1", key2 : "value2"} ) = [ { key : "key1", value : "value1" }, {key : "key2", value : "value2"} ]',
        ],
      },
      {
        label: "get value(m, key)",
        insertText: "get value($1, $2)",
        description: "Select the value of the entry named `key` from context `m`",
        parameters: [
          ["m", `\`context\``],
          ["key", `\`string\``],
        ],
        examples: [
          'get value( {key1 : "value1"}, "key1" ) = "value1"',
          'get value( {key1 : "value1"}, "unexistent-key" ) = null',
        ],
      },
      {
        label: "includes(range, point)",
        insertText: "includes($1, $2)",
        description: "Returns true when `range` includes `point`",
        parameters: [
          ["range", `\`range\` (\`interval\`)`],
          ["point", `\`number\``],
        ],
        examples: [
          "includes( [1..10], 5 ) = true",
          "includes( [1..10], 12 ) = false",
          "includes( [1..10], 1 ) = true",
          "includes( [1..10], 10 ) = true",
          "includes( (1..10], 1 ) = false",
          "includes( [1..10), 10 ) = false]",
        ],
      },
      {
        label: "includes(range1, range2)",
        insertText: "includes($1, $2)",
        description: "Returns true when `range1` includes `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "includes( [1..10], [4..6] ) = true",
          "includes( [1..10], [1..5] ) = true",
          "includes( (1..10], (1..5] ) = true",
          "includes( [1..10], (1..10) ) = true",
          "includes( [1..10), [5..10) ) = true",
          "includes( [1..10], [1..10) ) = true",
          "includes( [1..10], (1..10] ) = true",
          "includes( [1..10], [1..10] ) = true",
        ],
      },
      {
        label: "index of(list, match)",
        insertText: "index of($1, $2)",
        description: "Returns ascending list of `list` positions containing `match`",
        parameters: [
          ["list", `\`list\``],
          ["match", `\`string\``],
        ],
        examples: ["index of( [1,2,3,2], 2 ) = [2,4]", "index of( [1,2,3,2], 1 ) = [1]"],
      },
      {
        label: "insert before(list, position, newItem)",
        insertText: "insert before($1, $2, $3)",
        description: "Return new list with `newItem` inserted at `position`",
        parameters: [
          ["list", `\`list\``],
          ["position", `\`number\``],
          ["newItem", `Any type`],
        ],
        examples: ["insert before( [1 ,3], 1, 2 ) = [2,1,3]"],
      },
      {
        label: "is(value1, value2)",
        insertText: "is($1, $2)",
        description: "Returns true if both values are the same element in the FEEL semantic domain",
        parameters: [
          ["value1", `Any type`],
          ["value2", `Any type`],
        ],
        examples: [
          'is( date( "2012-12-25" ), time( "23:00:50" ) ) = false',
          'is( date( "2012-12-25" ), date( "2012-12-25" ) ) = true',
          'is( time( "23:00:50z" ), time( "23:00:50" ) ) = false',
          'is( time( "23:00:50z" ), time( "23:00:50+00:00" ) ) = true',
        ],
      },
      {
        label: "invoke(ctx, namespace, model name, decision name, parameters)",
        insertText: "invoke($1, $2, $3, $4, $5)",
        description: "",
        parameters: [],
        examples: [""],
      },
      {
        label: "list contains(list, element)",
        insertText: "list contains($1, $2)",
        description: "Does the `list` contain the `element`?",
        parameters: [
          ["list", `\`list\``],
          ["element", `Any type`],
        ],
        examples: ["list contains( [1,2,3], 2 ) = true"],
      },
      {
        label: "list replace(list, position, newItem)",
        insertText: "list replace($1, $2, $3)",
        description: "Returns new list with `newItem` replaced at `position`.",
        parameters: [
          ["list", `\`list\``],
          ["position", `\`number\``],
          ["newItem", `Any type`],
        ],
        examples: ["list replace( [2, 4, 7, 8], 3, 6) = [2, 4, 6, 8]"],
      },
      {
        label: "list replace(list, match, newItem)",
        insertText: "list replace($1, $2, $3)",
        description:
          "Returns new list with `newItem` replaced at all positions where the `match` function returned `true`",
        parameters: [
          ["list", `\`list\``],
          ["match", `boolean function(item, newItem)`],
          ["newItem", `Any type`],
        ],
        examples: ["list replace( [2, 4, 7, 8], function(item, newItem) item < newItem, 5) = [5, 5, 7, 8]"],
      },
      {
        label: "log(number)",
        insertText: "log($1)",
        description: "Returns the natural logarithm (base e) of the `number` parameter",
        parameters: [["number", `\`number\``]],
        examples: ["log( 10 ) = 2.30258509299"],
      },
      {
        label: "lower case(string)",
        insertText: "lower case($1)",
        description: "Returns lowercased `string`",
        parameters: [["string", `\`string\``]],
        examples: ['lower case( "aBc4" ) = "abc4"'],
      },
      {
        label: "matches(input, pattern)",
        insertText: "matches($1, $2)",
        description: "Does the `input` match the regexp `pattern`?",
        parameters: [
          ["input", `\`string\``],
          ["pattern", `\`string\``],
        ],
        examples: ['matches( "foobar", "^fo*b" ) = true'],
      },
      {
        label: "matches(input, pattern, flags)",
        insertText: "matches($1, $2, $3)",
        description: "Does the `input` match the regexp `pattern`?",
        parameters: [
          ["input", `\`string\``],
          ["pattern", `\`string\``],
          [
            "flags",
            `\`string\` \`i\` makes the regex match case insensitive, \`s\` enables "single-line mode" or "dot-all" mode. \`m\` enables "multi-line mode"`,
          ],
        ],
        examples: ['matches( "foobar", "^Fo*bar", "i" ) = true'],
      },
      {
        label: "max(list)",
        insertText: "max($1)",
        description: "Returns maximum item, or null if `list` is empty",
        parameters: [["list", `\`list\``]],
        examples: ["min( [1,2,3] ) = 1", "max( 1,2,3 ) = 3", "min( 1 ) = min( [1] ) = 1", "max( [] ) = null"],
      },
      {
        label: "mean(list)",
        insertText: "mean($1)",
        description: "Returns arithmetic mean (average) of `list` of numbers",
        parameters: [["list", `\`list\``]],
        examples: ["mean( [1,2,3] ) = 2", "mean( 1,2,3 ) = 2", "mean( 1 ) = 1", "mean( [] ) = null"],
      },
      {
        label: "median(list)",
        insertText: "median($1)",
        description:
          "Returns the median element of the `list` of numbers. I.e., after sorting the `list`, if the `list` has an odd number of elements, it returns the middle element. If the `list` has an even number of elements, returns the average of the two middle elements. If the `list` is empty, returns null",
        parameters: [["list", `\`list\``]],
        examples: ["median( 8, 2, 5, 3, 4 ) = 4", "median( [6, 1, 2, 3] ) = 2.5", "median( [ ] ) = null"],
      },
      {
        label: "meets(range1, range2)",
        insertText: "meets($1, $2)",
        description: "Returns true when `range1` meets `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "meets( [1..5], [5..10] ) = true",
          "meets( [1..5), [5..10] ) = false",
          "meets( [1..5], (5..10] ) = false",
          "meets( [1..5], [6..10] ) = false",
        ],
      },
      {
        label: "met by(range1, range2)",
        insertText: "met by($1, $2)",
        description: "Returns true when `range1` is met `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "met by( [5..10], [1..5] ) = true",
          "met by( [5..10], [1..5) ) = false",
          "met by( (5..10], [1..5] ) = false",
          "met by( [6..10], [1..5] ) = false",
        ],
      },
      {
        label: "min(list)",
        insertText: "min($1)",
        description: "Returns minimum item, or null if `list` is empty",
        parameters: [["list", `\`list\``]],
        examples: ["min( [1,2,3] ) = 1", "min( 1 ) = 1", "min( [1] ) = 1"],
      },
      {
        label: "mode(list)",
        insertText: "mode($1)",
        description:
          "Returns the mode of the numbers in the `list`. If multiple elements are returned, the numbers are sorted in ascending order.",
        parameters: [["list", `\`list\``]],
        examples: ["mode( 6, 3, 9, 6, 6 ) = [6]", "mode( [6, 1, 9, 6, 1] ) = [1, 6]", "mode( [ ] ) = [ ]"],
      },
      {
        label: "modulo(dividend, divisor)",
        insertText: "modulo($1, $2)",
        description: "Returns the remainder of the division of `dividend` by `divisor`",
        parameters: [
          ["dividend", `\`number\``],
          ["divisor", `\`number\``],
        ],
        examples: [
          "modulo( 12, 5 ) = 2",
          "modulo( -12,5 )= 3",
          "modulo( 12,-5 )= -3",
          "modulo( -12,-5 )= -2",
          "modulo( 10.1, 4.5 )= 1.1",
          "modulo( -10.1, 4.5 )= 3.4",
          "modulo( 10.1, -4.5 )= -3.4",
          "modulo( -10.1, -4.5 )= -1.1",
        ],
      },
      {
        label: "month of year(date)",
        insertText: "month of year($1)",
        description: "Returns the month of the year",
        parameters: [["date", `\`date\` or \`date and time\``]],
        examples: ['month of year( date(2017, 2, 18) ) = "February"'],
      },
      {
        label: "nn all(list)",
        insertText: "nn all($1)",
        description: "Returns true if all elements in the `list` are true. null values are ignored",
        parameters: [["list", `\`list\` of \`boolean\` elements`]],
        examples: [
          "nn all( [false,null,true] ) = false",
          "nn all( [true,null,true] ) = true",
          "nn all( true ) = true",
          "nn all( [] ) = true",
          "nn all( 0 ) = null",
        ],
      },
      {
        label: "nn any(list)",
        insertText: "nn any($1)",
        description: "Returns true if any element in the `list` is true. null values are ignored",
        parameters: [["list", `\`list\` of \`boolean\` elements`]],
        examples: [
          "nn any( [false,null,true] ) = true",
          "nn any( false ) = false",
          "nn any( [] ) = false",
          "nn any( null ) = false",
          "nn any( 0 ) = null",
        ],
      },
      {
        label: "nn count(list)",
        insertText: "nn count($1)",
        description: "Returns size of `list`, or zero if `list` is empty. null values are not counted",
        parameters: [["list", `\`list\``]],
        examples: [
          "nn count( [1,2,3] ) = 3",
          "nn count( [1,2,null,3] ) = 3",
          "nn count( [] ) = 0",
          "nn count( [1, [2,3]] ) = 2",
        ],
      },
      {
        label: "nn max(list)",
        insertText: "nn max($1)",
        description: "Returns maximum item, or null if `list` is empty. null values are ignored",
        parameters: [["list", `\`list\``]],
        examples: [
          "nn min( [1,2,3] ) = 1",
          "nn max( 1,2,3 ) = 3",
          "nn max( 1,2,3,null ) = 3",
          "nn min( 1 ) = min( [1] ) = 1",
          "nn max( [] ) = null",
        ],
      },
      {
        label: "nn mean(list)",
        insertText: "nn mean($1)",
        description: "Returns arithmetic mean (average) of numbers. null values are ignored",
        parameters: [["list", `\`list\``]],
        examples: [
          "nn mean( [1,2,3] ) = 2",
          "nn mean( 1,2,3 ) = 2",
          "nn mean( 1,2,3,null ) = 2",
          "nn mean( 1 ) = 1",
          "nn mean( [] ) = null",
        ],
      },
      {
        label: "nn median(list)",
        insertText: "nn median($1)",
        description:
          "Returns the median element of the `list` of numbers. null values are ignored. I.e., after sorting the `list`, if the `list` has an odd number of elements, it returns the middle element. If the `list` has an even number of elements, returns the average of the two middle elements. If the `list` is empty, returns null",
        parameters: [["list", `\`list\``]],
        examples: [
          "nn median( 8, 2, 5, 3, 4 ) = 4",
          "nn median( 20, 30, null, 40, null, 10 ) =  25",
          "nn median( [6, 1, 2, 3] ) = 2.5",
          "nn median( [ ] ) = null",
        ],
      },
      {
        label: "nn min(list)",
        insertText: "nn min($1)",
        description: "Returns minimum item, or null if `list` is empty. null values are ignored",
        parameters: [["list", `\`list\``]],
        examples: ["nn min( [1,2,3] ) = 1", "nn min( [1,2,null,3] ) = 1", "nn min( 1 ) = 1", "nn min( [1] ) = 1"],
      },
      {
        label: "nn mode(list)",
        insertText: "nn mode($1)",
        description:
          "Returns the mode of the numbers in the `list`. null values are ignored. If multiple elements are returned, the numbers are sorted in ascending order",
        parameters: [["list", `\`list\``]],
        examples: [
          "nn mode( 6, 3, 9, 6, 6 ) = [6]",
          "nn mode( 20, 30, null, 20, null, 10) = [20]",
          "nn mode( [6, 1, 9, 6, 1] ) = [1, 6]",
          "nn mode( [ ] ) = [ ]",
        ],
      },
      {
        label: "nn stddev(list)",
        insertText: "nn stddev($1)",
        description: "Returns the standard deviation of the numbers in the `list`. null values are ignored.",
        parameters: [["list", `\`list\``]],
        examples: [
          "nn stddev( 2, 4, 7, 5 ) = 2.081665999466132735282297706979931",
          "nn stddev( 20, 30, null, 40, null, 10) = 12.90994448735805628393088466594133",
          "nn stddev( [47] ) = null",
          "nn stddev( 47 ) = null",
          "nn stddev( [ ] ) = null",
        ],
      },
      {
        label: "nn sum(list)",
        insertText: "nn sum($1)",
        description: "Returns the sum of the numbers in the `list`. null values are ignored.",
        parameters: [["list", `\`list\``]],
        examples: [
          "nn sum( [1,2,3] ) = 6",
          "nn sum( 4, -1, 12.1, null, 5, null, 10 ) = 30.1",
          "nn sum( 1,2,3 ) = 6",
          "nn sum( 1 ) = 1",
          "nn sum( [] ) = null",
        ],
      },
      {
        label: "not(negand)",
        insertText: "not($1)",
        description: "Performs the logical negation of the `negand` operand",
        parameters: [["negand", `\`boolean\``]],
        examples: ["not( true ) = false", "not( null ) = null"],
      },
      {
        label: "now()",
        insertText: "now()",
        description: "Returns the current date and time.",
        parameters: [],
        examples: ["now()"],
      },
      {
        label: "number(from)",
        insertText: "number($1)",
        description: "Converts `from` to a number.",
        parameters: [["from", "`string` or `number` representing a valid number"]],
        examples: ['number( "1.1" ) = number( "1.1", "null", "null" ) = 1.1', "number( 5 ) = 5"],
      },
      {
        label: "number(from, grouping separator, decimal separator)",
        insertText: "number($1, $2, $3)",
        description: "Converts `from` to a number using the specified separators.",
        parameters: [
          ["from", "`string` representing a valid number"],
          ["grouping separator", "Space (` `), comma (`,`), period (`.`), or null"],
          ["decimal separator", "Same types as `grouping separator`, but the values cannot match"],
        ],
        examples: ['number( "1 000,0", " ", "," ) = number( "1,000.0", ",", "." )'],
      },
      {
        label: "odd(number)",
        insertText: "odd($1)",
        description: "Returns true if the specified `number` is odd.",
        parameters: [["number", `\`number\``]],
        examples: ["odd( 5 ) = true", "odd( 2 ) = false"],
      },
      {
        label: "overlaps after(range1, range2)",
        insertText: "overlaps after($1, $2)",
        description: "Returns true when `range1` overlaps after `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "overlaps after( [3..8], [1..5] ) = true",
          "overlaps after( [6..8], [1..5] ) = false",
          "overlaps after( [5..8], [1..5] ) = true",
          "overlaps after( (5..8], [1..5] ) = false",
          "overlaps after( [5..8], [1..5) ) = false",
          "overlaps after( (1..5], [1..5) ) = true",
          "overlaps after( (1..5], [1..5] ) = true",
          "overlaps after( [1..5], [1..5) ) = false",
          "overlaps after( [1..5], [1..5] ) = false",
          "overlaps after( (1..5), [1..5] ) = false",
          "overlaps after( (1..5], [1..6] ) = false",
          "overlaps after( (1..5], (1..5] ) = false",
          "overlaps after( (1..5], [2..5] ) = false",
        ],
      },
      {
        label: "overlaps before(range1, range2)",
        insertText: "overlaps before($1, $2)",
        description: "Returns true when `range1` overlaps before `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "overlaps before( [1..5], [3..8] ) = true",
          "overlaps before( [1..5], [6..8] ) = false",
          "overlaps before( [1..5], [5..8] ) = true",
          "overlaps before( [1..5], (5..8] ) = false",
          "overlaps before( [1..5), [5..8] ) = false",
          "overlaps before( [1..5), (1..5] ) = true",
          "overlaps before( [1..5], (1..5] ) = true",
          "overlaps before( [1..5), [1..5] ) = false",
          "overlaps before( [1..5], [1..5] ) = false",
        ],
      },
      {
        label: "overlaps(range1, range2)",
        insertText: "overlaps($1, $2)",
        description: "Returns true when `range1` overlaps `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "overlaps( [1..5], [3..8] ) = true",
          "overlaps( [3..8], [1..5] ) = true",
          "overlaps( [1..8], [3..5] ) = true",
          "overlaps( [3..5], [1..8] ) = true",
          "overlaps( [1..5], [6..8] ) = false",
          "overlaps( [6..8], [1..5] ) = false",
          "overlaps( [1..5], [5..8] ) = true",
          "overlaps( [1..5], (5..8] ) = false",
          "overlaps( [1..5), [5..8] ) = false",
          "overlaps( [1..5), (5..8] ) = false",
          "overlaps( [5..8], [1..5] ) = true",
          "overlaps( (5..8], [1..5] ) = false",
          "overlaps( [5..8], [1..5) ) = false",
          "overlaps( (5..8], [1..5) ) = false",
        ],
      },
      {
        label: "product(list)",
        insertText: "product($1)",
        description: "Returns the product of the numbers in the `list`",
        parameters: [["list", `\`list\` of \`number\` elements`]],
        examples: ["product( [2, 3, 4] ) = 24", "product( [] ) = null", "product( 2, 3, 4 ) = 24"],
      },
      {
        label: "range(from)",
        insertText: "range($1)",
        description: "Convert from a range `string` to a `range`.",
        parameters: [["from", `range \`string\``]],
        examples: [
          'range( "[18..21)" ) is [18..21)',
          'range( "[2..)" ) is >=2',
          'range( "(..2)" ) is <2',
          'range( "" ) is null',
          'range( "[..]" ) is null',
        ],
      },
      {
        label: "remove(list, position)",
        insertText: "remove($1, $2)",
        description: "Creates a list with the removed element excluded from the specified `position`.",
        parameters: [
          ["list", `\`list\``],
          ["position", `\`number\``],
        ],
        examples: ["remove( [1,2,3], 2 ) = [1,3]"],
      },
      {
        label: "replace(input, pattern, replacement)",
        insertText: "replace($1, $2, $3)",
        description: "Calculates the regular expression replacement",
        parameters: [
          ["input", `\`string\``],
          ["pattern", `\`string\``],
          ["replacement", `\`string\``],
        ],
        examples: [
          'replace( "banana", "a", "o" ) = "bonono"',
          'replace( "abcd", "(ab)|(a)", "[1=$1][2=$2]" ) = "[1=ab][2=]cd"',
        ],
      },
      {
        label: "replace(input, pattern, replacement, flags)",
        insertText: "replace($1, $2, $3, $4)",
        description: "Calculates the regular expression replacement",
        parameters: [
          ["input", `\`string\``],
          ["pattern", `\`string\``],
          ["replacement", `\`string\``],
          [
            "flags",
            `\`string\` \`i\` makes the regex match case insensitive, \`s\` enables "single-line mode" or "dot-all" mode. \`m\` enables "multi-line mode"`,
          ],
        ],
        examples: ['replace( "foobar", "^fOO", "ttt", "i") = "tttbar"'],
      },
      {
        label: "reverse(list)",
        insertText: "reverse($1)",
        description: "Returns a reversed `list`",
        parameters: [["list", `\`list\``]],
        examples: ["reverse( [1,2,3] ) = [3,2,1]"],
      },
      {
        label: "round down(n, scale)",
        insertText: "round down($1, $2)",
        description:
          "Returns `n` with given `scale` and rounding mode round down. If at least one of `n` or `scale` is null, the result is null. The `scale` must be in the range [−6111..6176].",
        parameters: [
          ["n", `\`number\``],
          ["scale", `\`number\``],
        ],
        examples: [
          "round down( 5.5, 0 ) = 5",
          "round down( -5.5, 0 ) = -5",
          "round down( 1.121, 2 ) = 1.12",
          "round down( -1.126, 2 ) = -1.12",
        ],
      },
      {
        label: "round down(n)",
        insertText: "round down($1)",
        description: "Returns `n` with rounding mode round down. If `n` is null, the result is null.",
        parameters: [["n", `\`number\``]],
        examples: ["round down( 5.5) = 5", "round down( -5.5) = -5"],
      },
      {
        label: "round half down(n, scale)",
        insertText: "round half down($1, $2)",
        description:
          "Returns `n` with given `scale` and rounding mode round half down. If at least one of `n` or `scale` is null, the result is null. The `scale` must be in the range [−6111..6176].",
        parameters: [
          ["n", `\`number\``],
          ["scale", `\`number\``],
        ],
        examples: [
          "round half down( 5.5, 0 ) = 5",
          "round half down( -5.5, 0 ) = -5",
          "round half down( 1.121, 2 ) = 1.12",
          "round half down( -1.126, 2 ) = -1.13",
        ],
      },
      {
        label: "round half down(n)",
        insertText: "round half down($1)",
        description: "Returns `n` with rounding mode round half down. If `n` is null, the result is null.",
        parameters: [["n", `\`number\``]],
        examples: ["round half down( 5.5) = 5", "round half down( -5.5) = -5"],
      },
      {
        label: "round half up(n, scale)",
        insertText: "round half up($1, $2)",
        description:
          "Returns `n` with given `scale` and rounding mode round half up. If at least one of `n` or `scale` is null, the result is null. The `scale` must be in the range [−6111..6176].",
        parameters: [
          ["n", `\`number\``],
          ["scale", `\`number\``],
        ],
        examples: [
          "round half up( 5.5, 0 ) = 6",
          "round half up( -5.5, 0 ) = -6",
          "round half up( 1.121, 2 ) = 1.12",
          "round half up( -1.126, 2 ) = -1.13",
        ],
      },
      {
        label: "round half up(n)",
        insertText: "round half up($1)",
        description: "Returns `n` with rounding mode round half up. If  `n` is null, the result is null.",
        parameters: [["n", `\`number\``]],
        examples: ["round half up(5.5) = 6 ", "round half up( -5.5) = -6"],
      },
      {
        label: "round up(n, scale)",
        insertText: "round up($1, $2)",
        description:
          "Returns `n` with given `scale` and rounding mode round up. If at least one of `n` or `scale` is null, the result is null. The `scale` must be in the range [−6111..6176].",
        parameters: [
          ["n", `\`number\``],
          ["scale", `\`number\``],
        ],
        examples: [
          "round up( 5.5, 0 ) = 6",
          "round up( -5.5, 0 ) = -6",
          "round up( 1.121, 2 ) = 1.13",
          "round up( -1.126, 2 ) = -1.13",
        ],
      },
      {
        label: "round up(n)",
        insertText: "round up($1)",
        description: "Returns `n` with rounding mode round up. If `n` is null, the result is null.",
        parameters: [["n", `\`number\``]],
        examples: ["round up(5.5) = 6", "round up(-5.5) = -6 "],
      },
      {
        label: "sort(list)",
        insertText: "sort($1)",
        description:
          "Returns a list of the same elements but ordered according a default sorting, if the elements are comparable (eg. `number` or `string`)",
        parameters: [["list", `\`list\``]],
        examples: ["sort( [3,1,4,5,2] ) = [1,2,3,4,5]"],
      },
      {
        label: "sort(list, precedes)",
        insertText: "sort($1, $2)",
        description: "Returns a list of the same elements but ordered according to the sorting function",
        parameters: [
          ["list", `\`list\``],
          ["precedes", `\`function\``],
        ],
        examples: ["sort( list: [3,1,4,5,2], precedes: function(x,y) x < y ) = [1,2,3,4,5]"],
      },
      {
        label: "split(string, delimiter)",
        insertText: "split($1, $2)",
        description:
          "Returns a list of the original `string` and splits it at the `delimiter` regular expression pattern",
        parameters: [
          ["string", `\`string\``],
          ["delimiter", `\`string\` for a regular expression pattern`],
        ],
        examples: ['split( "John Doe", "\\s" ) = ["John", "Doe"]', 'split( "a;b;c;;", ";" ) = ["a","b","c","",""]'],
      },
      {
        label: "sqrt(number)",
        insertText: "sqrt($1)",
        description: "Returns the square root of the specified `number`.",
        parameters: [["number", `\`number\``]],
        examples: ["sqrt( 16 ) = 4"],
      },
      {
        label: "started by(range, point)",
        insertText: "started by($1, $2)",
        description: "Returns true when a `range` is started by a `point`",
        parameters: [
          ["range", `\`range\` (\`interval\`)`],
          ["point", `\`number\``],
        ],
        examples: [
          "started by( [1..10], 1 ) = true",
          "started by( (1..10], 1 ) = false",
          "started by( [1..10], 2 ) = false",
        ],
      },
      {
        label: "started by(range1, range2)",
        insertText: "started by($1, $2)",
        description: "Returns true when `range1` is started by `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "started by( [1..10], [1..5] ) = true",
          "started by( (1..10], (1..5] ) = true",
          "started by( [1..10], (1..5] ) = false",
          "started by( (1..10], [1..5] ) = false",
          "started by( [1..10], [1..10] ) = true",
          "started by( [1..10], [1..10) ) = true",
          "started by( (1..10), (1..10) ) = true",
        ],
      },
      {
        label: "starts with(string, match)",
        insertText: "starts with($1, $2)",
        description: "Does the `string` start with the `match`?",
        parameters: [
          ["string", `string`],
          ["match", `string`],
        ],
        examples: ['starts with( "testing", "te" ) = true'],
      },
      {
        label: "starts(point, range)",
        insertText: "starts($1, $2)",
        description: "Returns true when `point` starts a `range`",
        parameters: [
          ["point", `\`number\``],
          ["range", `\`range\` (\`interval\`)`],
        ],
        examples: ["starts( 1, [1..10] ) = true", "starts( 1, (1..10] ) = false", "starts( 2, [1..10] ) = false"],
      },
      {
        label: "starts(range1, range2)",
        insertText: "starts($1, $2)",
        description: "Returns true when a `range1` starts a `range2`",
        parameters: [
          ["range1", `\`range\` (\`interval\`)`],
          ["range2", `\`range\` (\`interval\`)`],
        ],
        examples: [
          "starts( [1..5], [1..10] ) = true",
          "starts( (1..5], (1..10] ) = true",
          "starts( (1..5], [1..10] ) = false",
          "starts( [1..5], (1..10] ) = false",
          "starts( [1..10], [1..10] ) = true",
          "starts( [1..10), [1..10] ) = true",
          "starts( (1..10), (1..10) ) = true",
        ],
      },
      {
        label: "stddev(list)",
        insertText: "stddev($1)",
        description: "Returns the standard deviation of the numbers in the `list`",
        parameters: [["list", `\`list\``]],
        examples: [
          "stddev( 2, 4, 7, 5 ) = 2.081665999466132735282297706979931",
          "stddev( [47] ) = null",
          "stddev( 47 ) = null",
          "stddev( [ ] ) = null",
        ],
      },
      {
        label: "string length(string)",
        insertText: "string length($1)",
        description: "Calculates the length of the specified `string`.",
        parameters: [["string", `\`string\``]],
        examples: ['string length( "tes" ) = 3', 'string length( "U01F40Eab" ) = 3'],
      },
      {
        label: "string(from)",
        insertText: "string($1)",
        description: "Provides a string representation of the specified parameter",
        parameters: [["from", `Not null value`]],
        examples: ['string( 1.1 ) = "1.1"', "string( null ) = null"],
      },
      {
        label: "string join(list)",
        insertText: "string join($1)",
        description:
          "Returns a string which is composed by joining all the string elements from the `list` parameter. Null elements in the `list` parameter are ignored. If `list` is empty, the result is the empty string.",
        parameters: [["list", `\`list\` of \`string\``]],
        examples: [
          'string join( ["a","b","c"] ) = "abc"',
          'string join( ["a",null,"c"] ) = "ac"',
          'string join([]) = ""',
        ],
      },
      {
        label: "string join(list, delimiter)",
        insertText: "string join($1, $2)",
        description:
          "Returns a string which is composed by joining all the string elements from the `list` parameter, separated by the `delimiter`. The `delimiter` can be an empty string. Null elements in the `list` parameter are ignored. If `list` is empty, the result is the empty string. If `delimiter` is null, the string elements are joined without a separator.",
        parameters: [
          ["list", `\`list\` of \`string\``],
          ["delimiter", `\`string\``],
        ],
        examples: [
          'string join(["a","b","c"], "_and_") = "a_and_b_and_c"',
          'string join(["a","b","c"], "") = "abc"',
          'string join(["a","b","c"], null) = "abc"',
          'string join(["a"], "X") = "a"',
          'string join(["a",null,"c"], "X") = "aXc"',
          'string join([], "X") = ""',
        ],
      },
      {
        label: "sublist(list, start position)",
        insertText: "sublist($1, $2)",
        description: "Returns the sublist from the `start position`",
        parameters: [
          ["list", `\`list\``],
          ["start position", `\`number\``],
        ],
        examples: ["sublist( [4,5,6], 2 ) = [5,6]"],
      },
      {
        label: "sublist(list, start position, length)",
        insertText: "substring($1, $2, $3)",
        description: "Returns the sublist from the `start position for the specified `length`",
        parameters: [
          ["list", `\`list\``],
          ["start position", `\`number\``],
          ["length", `\`number\``],
        ],
        examples: ["sublist( [4,5,6], 1, 2 ) = [4,5]"],
      },
      {
        label: "substring after(string, match)",
        insertText: "substring after($1, $2)",
        description: "Calculates the substring after the `match`",
        parameters: [
          ["string", `string`],
          ["match", `string`],
        ],
        examples: ['substring after( "testing", "te" ) = "sting"', 'substring after( "testing", "tin" ) = "g"'],
      },
      {
        label: "substring before(string, match)",
        insertText: "substring before($1, $2)",
        description: "Calculates the substring before the `match`",
        parameters: [
          ["string", `string`],
          ["match", `string`],
        ],
        examples: ['substring before( "testing", "te" ) = ""', 'substring before( "testing", "tin" ) = "tes"'],
      },
      {
        label: "substring(string, start position)",
        insertText: "substring($1, $2)",
        description: "Returns the substring from the `start position`. The first character is at position value 1",
        parameters: [
          ["string", `\`string\``],
          ["start position", `\`number\``],
        ],
        examples: ['substring( "testing", 3 ) = "sting"', 'substring( "U01F40Eab", 2 ) = "ab"'],
      },
      {
        label: "substring(string, start position, length)",
        insertText: "substring($1, $2, $3)",
        description:
          "Returns the substring from the `start position` for the specified `length`. The first character is at position value 1",
        parameters: [
          ["string", `\`string\``],
          ["start position", `\`number\``],
          ["length", `\`number\``],
        ],
        examples: ['substring( "testing", 3, 3 ) = "sti"', 'substring( "testing", -2, 1 ) = "n"'],
      },
      {
        label: "sum(list)",
        insertText: "sum($1)",
        description: "Returns the sum of the numbers in the `list`",
        parameters: [["list", `\`list\``]],
        examples: ["sum( [1,2,3] ) = 6", "sum( 1,2,3 ) = 6", "sum( 1 ) = 1", "sum( [] ) = null"],
      },
      {
        label: "time(from)",
        insertText: "time($1)",
        description: "Produces a time from the specified parameter",
        parameters: [["from", `\`string\` or \`date and time\``]],
        examples: [
          'time( "23:59:00z" ) + duration( "PT2M" ) = time( "00:01:00@Etc/UTC" )',
          'time(date and time( "2012-12-25T11:00:00Z" )) = time( "11:00:00Z" )',
        ],
      },
      {
        label: "time(hour, minute, second)",
        insertText: "date and time($1, $2, $3)",
        description: "Creates a time from the given `hour`, `minute`, and `second`.",
        parameters: [
          ["hour", `\`number\``],
          ["minute", `\`number\``],
          ["second", `\`number\``],
        ],
        examples: ['time( 23, 59, 59 ) = time( "23:59:59" )'],
      },
      {
        label: "time(hour, minute, second, offset)",
        insertText: "time($1, $2, $3)",
        description: "Creates a time from the given `hour`, `minute`, `second` and `offset`",
        parameters: [
          ["hour", `\`number\``],
          ["minute", `\`number\``],
          ["second", `\`number\``],
          ["offset", `\`days and time duration or null\``],
        ],
        examples: ['time( "23:59:00z" ) = time(23, 59, 0, duration( "PT0H" ))'],
      },
      {
        label: "today()",
        insertText: "today()",
        description: "Returns the current date",
        parameters: [],
        examples: ["today()"],
      },
      {
        label: "union(list)",
        insertText: "union($1)",
        description: "Returns a list of all the elements from multiple lists and excludes duplicates",
        parameters: [["list", `\`list\``]],
        examples: ["union( [1,2],[2,3] ) = [1,2,3]"],
      },
      {
        label: "upper case(string)",
        insertText: "string($1)",
        description: "Produces an uppercase version of the specified `string`.",
        parameters: [["string", `\`string\``]],
        examples: ['upper case( "aBc4" ) = "ABC4"'],
      },
      {
        label: "week of year(date)",
        insertText: "week of year($1)",
        description: "Returns the Gregorian week of the year as defined by ISO 8601",
        parameters: [["date", `\`date\` or \`date and time\``]],
        examples: [
          "week of year( date(2019, 9, 17) ) = 38",
          "week of year( date(2003, 12, 29) ) = 1",
          "week of year( date(2004, 1, 4) ) = 1",
          "week of year( date(2005, 1, 1) ) = 53",
          "week of year( date(2005, 1, 3) ) = 1",
          "week of year( date(2005, 1, 9) ) = 1",
        ],
      },
      {
        label: "years and months duration(from, to)",
        insertText: "years and months duration($1, $2)",
        description: "Calculates the years and months duration between the two specified parameters.",
        parameters: [
          ["from", `\`date\` or \`date and time\``],
          ["to", `\`date\` or \`date and time\``],
        ],
        examples: ['years and months duration( date( "2011-12-22" ), date( "2013-08-24" ) ) = duration( "P1Y8M" )'],
      },
    ],
  };

  for (const suggestion of suggestionTypes.keywords) {
    suggestions.push({
      kind: Monaco.languages.CompletionItemKind.Keyword,
      label: suggestion,
      insertText: suggestion,
    } as Monaco.languages.CompletionItem);
  }

  for (const suggestion of suggestionTypes.snippet) {
    suggestions.push({
      kind: Monaco.languages.CompletionItemKind.Snippet,
      insertTextRules: Monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      label: suggestion[0],
      insertText: suggestion[1],
    } as Monaco.languages.CompletionItem);
  }

  for (const suggestion of suggestionTypes.function) {
    suggestions.push({
      kind: Monaco.languages.CompletionItemKind.Function,
      insertTextRules: Monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      label: suggestion.label,
      insertText: suggestion.insertText,
      documentation:
        suggestion.description !== ""
          ? {
              isTrusted: true,
              value: generateDocumentationMarkDown(
                generateMarkdownFEELCode([suggestion.label]),
                suggestion.description,
                generateMarkdownParametersTable(suggestion.parameters),
                generateMarkdownFEELCode(suggestion.examples)
              ),
            }
          : null,
    } as Monaco.languages.CompletionItem);
  }

  return suggestions;
};

/**
 * It generates a Markdown FEEL code block given an array of code statement. E.g:
 *  \`\`\`FEEL
 *  string length( "tes" ) = 3
 *  string length( "\U01F40Eab" ) = 3
 *  \`\`\`
 */
const generateMarkdownFEELCode = (codeStatements: string[]): string => {
  return `\`\`\`FEEL\n${codeStatements.join(`\n`)}\n\`\`\``;
};

/**
 * It generates a Markdown Table to show all the parameters requested by a function. E.g:
 *  | Parameter | Type |
 *  |-|-|
 *  | \`name\`| type |
 *  | \`name2\`| type2 |
 */
const generateMarkdownParametersTable = (parameters: string[][]): string => {
  if (parameters.length === 0) {
    return "";
  }
  const rows = parameters.map((item) => `|\`${item[0]}\`|${item[1]}|`);
  return `| Parameter | Type |\n|-|-|\n${rows.join(`\n`)}`;
};

const generateDocumentationMarkDown = (
  feelFunctionTitle: string,
  description: string,
  parametersTable: string,
  feelFunctionExamples: string
): string =>
  `${feelFunctionTitle}\n\n---\n\n_${description}_\n\n${parametersTable}\n\nExample(s):\n${feelFunctionExamples}`;
