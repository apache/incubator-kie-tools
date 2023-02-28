/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { defaultConfig, defaultServiceCatalogConfig } from "./SwfLanguageServiceConfigs";
import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { SwfYamlLanguageService } from "../dist/channel";
export function getJqBuiltInFunctionTests(): Array<Array<string>> {
  return [
    ["operation with empty object", `{🎯}`],
    ["operation with no value", `"🎯"`],
    ["cursor after some random word", `"some_func 🎯"`],
    ["cursor before empty space", `"🎯 "`],
    ["cursor after a value - auto-complete the word map", `"map🎯"`],
  ];
}

export function getJqReusableFunctionTests(): Array<Array<string>> {
  return [
    ["operation with empty object", `{🎯}`],
    ["operation with fn:", `"fn:🎯"`],
    ["space before fn:", `" fn:🎯"`],
    ["fn: starting with r", `"fn:r🎯"`],
    ["fn: starting with a", `"fn:a🎯"`],
    ["fn: after some value", `"some_val fn:🎯"`],
  ];
}

export function getJqVariableTests(): Array<Array<string>> {
  return [
    ["operation with empty object", `{🎯}`],
    ["cursor before the dot(variable):", `"🎯."`],
    ["cursor after the dot(variable):", `".🎯"`],
    ["cursor with a space after the  dot(variable):", `". 🎯"`],
    ["cursor with a value following the  dot(variable):", `".a🎯"`],
  ];
}
export function getJsonLsForJqExpressionTests(): SwfJsonLanguageService {
  return new SwfJsonLanguageService({
    fs: {},
    serviceCatalog: defaultServiceCatalogConfig,
    config: defaultConfig,
    jqCompletions: {
      remote: {
        getJqAutocompleteProperties: async (_args) => [{ name: "string" }, { age: "1" }] as Record<string, string>[],
      },
      relative: {
        getJqAutocompleteProperties: async (_args) =>
          [{ avgLoad: "string" }, { numberOfPods: "1" }] as Record<string, string>[],
      },
    },
  });
}

export function getYamlLsForJqExpressionTests(): SwfYamlLanguageService {
  return new SwfYamlLanguageService({
    fs: {},
    serviceCatalog: defaultServiceCatalogConfig,
    config: defaultConfig,
    jqCompletions: {
      remote: {
        getJqAutocompleteProperties: async (_args) => [{ name: "string" }, { age: "1" }] as Record<string, string>[],
      },
      relative: {
        getJqAutocompleteProperties: async (_args) =>
          [{ avgLoad: "string" }, { numberOfPods: "1" }] as Record<string, string>[],
      },
    },
  });
}
