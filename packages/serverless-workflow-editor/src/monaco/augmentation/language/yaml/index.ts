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

import { MonacoLanguage } from "../MonacoLanguage";
import { setDiagnosticsOptions } from "monaco-yaml";
import * as YAML from "yaml";

import {
  SW_SPEC_COMMON_SCHEMA,
  SW_SPEC_EVENTS_SCHEMA,
  SW_SPEC_FUNCTIONS_SCHEMA,
  SW_SPEC_RETRIES_SCHEMA,
  SW_SPEC_SCHEMA,
} from "../schemas";
import { JSONSchema7 } from "json-schema";

setDiagnosticsOptions({
  enableSchemaRequest: true,
  hover: true,
  completion: true,
  validate: true,
  format: true,
  schemas: [
    {
      uri: "common.json",
      fileMatch: ["*"],
      schema: SW_SPEC_COMMON_SCHEMA,
    },
    {
      uri: "events.json",
      fileMatch: ["*"],
      schema: SW_SPEC_EVENTS_SCHEMA as JSONSchema7,
    },
    {
      uri: "functions.json",
      fileMatch: ["*"],
      schema: SW_SPEC_FUNCTIONS_SCHEMA as JSONSchema7,
    },
    {
      uri: "retries.json",
      fileMatch: ["*"],
      schema: SW_SPEC_RETRIES_SCHEMA as JSONSchema7,
    },
    {
      uri: "workflow.json",
      fileMatch: ["*"],
      schema: SW_SPEC_SCHEMA as JSONSchema7,
    },
  ],
});

export function initYAMLLanguage(): MonacoLanguage {
  return {
    languageId: "yaml",

    getDefaultContent: (content) => {
      if (!content) {
        return "";
      }
      return content;
    },

    getStringValue: (object) => YAML.stringify(object),
  };
}
