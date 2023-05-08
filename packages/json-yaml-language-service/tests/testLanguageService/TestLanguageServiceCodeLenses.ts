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

import {
  createNewFileCodeLens,
  createOpenCompletionItemsCodeLenses,
  EditorLanguageServiceCodeLenses,
  EditorLanguageServiceCodeLensesFunctionsArgs,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { CodeLens } from "vscode-languageserver-types";
import { EditorLanguageServiceCommandTypes } from "../../dist/api";

export type TestLanguageServiceCodeLensesFunctionsArgs =
  EditorLanguageServiceCodeLensesFunctionsArgs<EditorLanguageServiceCommandTypes>;

/**
 * Functions to create CodeLenses
 */
export const testLanguageServiceCodeLenses: EditorLanguageServiceCodeLenses = {
  createNewFile: (): CodeLens[] => createNewFileCodeLens("Create a Serverless Workflow"),

  addFunction: (args: TestLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createOpenCompletionItemsCodeLenses({
      ...args,
      jsonPath: ["functions"],
      title: "+ Add function...",
      nodeType: "array",
    }),
};
