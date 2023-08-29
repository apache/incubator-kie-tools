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

import { defaultConfig, defaultServiceCatalogConfig } from "./SwfLanguageServiceConfigs";
import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { SwfYamlLanguageService } from "../dist/channel";

const languageServiceArgs = {
  fs: {},
  serviceCatalog: defaultServiceCatalogConfig,
  config: defaultConfig,
  jqCompletions: {
    remote: {
      getJqAutocompleteProperties: async (_args: any) => [{ name: "string" }, { age: "1" }] as Record<string, string>[],
    },
    relative: {
      getJqAutocompleteProperties: async (_args: any) =>
        [{ avgLoad: "string" }, { numberOfPods: "1" }] as Record<string, string>[],
    },
  },
};

export function getJqBuiltInFunctionTests(): Array<Array<string>> {
  return [
    ["empty object", `{🎯}`],
    ["no value/double quotes", `"🎯"`],
    ["cursor after some random word", `"some_func 🎯"`],
    ["cursor before empty space", `"🎯 "`],
    ["cursor after a value - auto-complete the word map", `"map🎯"`],
  ];
}

export function getJqReusableFunctionTests(): Array<Array<string>> {
  return [
    ["empty object", `{🎯}`],
    ["value fn:/double quotes", `"fn:🎯"`],
    ["space before fn:", `" fn:🎯"`],
    ["fn: starting with r", `"fn:r🎯"`],
    ["fn: starting with a", `"fn:a🎯"`],
    ["fn: after some value", `"some_val fn:🎯"`],
  ];
}

export function getJqVariableTests(): Array<Array<string>> {
  return [
    ["empty object", `{🎯}`],
    ["cursor before the dot(variable)", `"🎯."`],
    ["cursor after the dot(variable):/double quotes", `".🎯"`],
    ["cursor with a space after the  dot(variable)", `". 🎯"`],
    ["cursor with a value following the  dot(variable)", `".a🎯"`],
  ];
}

export function getSingleQuoteTestForYaml(): Array<Array<string>> {
  return [
    ["no value/single quotes", `'🎯'`],
    ["value fn:/single quotes", `'fn:🎯'`],
    ["cursor after the dot(variable):/single quotes", `'.🎯'`],
  ];
}

export function getJsonLsForJqExpressionTests(): SwfJsonLanguageService {
  return new SwfJsonLanguageService(languageServiceArgs);
}

export function getYamlLsForJqExpressionTests(): SwfYamlLanguageService {
  return new SwfYamlLanguageService(languageServiceArgs);
}
