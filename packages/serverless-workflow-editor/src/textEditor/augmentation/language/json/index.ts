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
import {
  SW_SPEC_AUTH_SCHEMA,
  SW_SPEC_COMMON_SCHEMA,
  SW_SPEC_ERRORS_SCHEMA,
  SW_SPEC_EVENTS_SCHEMA,
  SW_SPEC_FUNCTIONS_SCHEMA,
  SW_SPEC_RETRIES_SCHEMA,
  SW_SPEC_SECRETS_SCHEMA,
  SW_SPEC_TIMEOUTS_SCHEMA,
  SW_SPEC_WORKFLOW_SCHEMA,
} from "../schemas";

export function initJsonSchemaDiagnostics() {
  // Uncommenting this will disable syntax highlighting for JSON as well and stop using the JSON Schema for SWF.
  //
  // monaco.languages.json.jsonDefaults.setModeConfiguration({
  //   completionItems: false,
  // });

  monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
    validate: true,
    allowComments: false,
    schemas: [
      {
        uri: "https://serverlessworkflow.io/schemas/0.8/auth.json",
        fileMatch: ["*"],
        schema: SW_SPEC_AUTH_SCHEMA,
      },
      {
        uri: "https://serverlessworkflow.io/schemas/0.8/common.json",
        fileMatch: ["*"],
        schema: SW_SPEC_COMMON_SCHEMA,
      },
      {
        uri: "https://serverlessworkflow.io/schemas/0.8/errors.json",
        fileMatch: ["*"],
        schema: SW_SPEC_ERRORS_SCHEMA,
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
        uri: "https://serverlessworkflow.io/schemas/0.8/secrets.json",
        fileMatch: ["*"],
        schema: SW_SPEC_SECRETS_SCHEMA,
      },
      {
        uri: "https://serverlessworkflow.io/schemas/0.8/timeouts.json",
        fileMatch: ["*"],
        schema: SW_SPEC_TIMEOUTS_SCHEMA,
      },
      {
        uri: "https://serverlessworkflow.io/schemas/0.8/workflow.json",
        fileMatch: ["*"],
        schema: SW_SPEC_WORKFLOW_SCHEMA,
      },
    ],
  });
}
