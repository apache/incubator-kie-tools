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

import * as monaco from "monaco-editor";
import * as jsonService from "vscode-json-languageservice";
import { TextDocument } from "vscode-languageserver-types";
import { MonacoLanguage } from "../MonacoLanguage";
import {
  SW_SPEC_COMMON_SCHEMA,
  SW_SPEC_EVENTS_SCHEMA,
  SW_SPEC_FUNCTIONS_SCHEMA,
  SW_SPEC_RETRIES_SCHEMA,
  SW_SPEC_SCHEMA,
} from "../schemas";
import { ASTDocument } from "../parser";

monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
  validate: true,
  allowComments: false,
  schemas: [
    {
      uri: "https://serverlessworkflow.io/schemas/0.8/common.json",
      fileMatch: ["*"],
      schema: SW_SPEC_COMMON_SCHEMA,
    },
    {
      uri: "https://serverlessworkflow.io/schemas/0.8/events.json",
      fileMatch: ["*"],
      schema: SW_SPEC_EVENTS_SCHEMA,
    },
    {
      uri: "https://serverlessworkflow.io/schemas/0.8/functions.json",
      fileMatch: ["*"],
      schema: SW_SPEC_FUNCTIONS_SCHEMA,
    },
    {
      uri: "https://serverlessworkflow.io/schemas/0.8/retries.json",
      fileMatch: ["*"],
      schema: SW_SPEC_RETRIES_SCHEMA,
    },
    {
      uri: "https://serverlessworkflow.io/schemas/0.8/workflow.json",
      fileMatch: ["*"],
      schema: SW_SPEC_SCHEMA,
    },
  ],
  enableSchemaRequest: true,
});

const jsonLangService = jsonService.getLanguageService({});

export function lookupJSONLanguage(): MonacoLanguage {
  return {
    languageId: "json",

    parser: {
      parseContent(content: TextDocument): ASTDocument {
        return jsonLangService.parseJSONDocument(content) as ASTDocument;
      },
    },

    getDefaultContent: (content) => {
      if (!content || content.trim() === "") {
        return "{}";
      }
      return content;
    },

    getStringValue: (object) => JSON.stringify(object),
  };
}
