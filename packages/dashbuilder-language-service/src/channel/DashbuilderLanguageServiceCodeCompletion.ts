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

import {
  EditorLanguageServiceCodeCompletionFunctions,
  EditorLanguageServiceCodeCompletionFunctionsArgs,
  EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { CompletionItem, CompletionItemKind, InsertTextFormat, Range } from "vscode-languageserver-types";
import { dashbuilderCompletion } from "../assets/code-completions/";

export type DashbuilderLanguageServiceCodeCompletionFunctionsArgs =
  EditorLanguageServiceCodeCompletionFunctionsArgs & {};

/**
 * DashbuilderLanguageService CodeCompletion functions
 */
export const DashbuilderLanguageServiceCodeCompletion: EditorLanguageServiceCodeCompletionFunctions = {
  getEmptyFileCodeCompletions(
    args: EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs
  ): Promise<CompletionItem[]> {
    const kind = CompletionItemKind.Text;
    const label = "Create your first dashboard";
    return Promise.resolve([
      {
        kind,
        label,
        detail: "Start with a simple dashboard",
        sortText: `100_${label}`, //place the completion on top in the menu
        textEdit: {
          newText: args.codeCompletionStrategy.translate({
            ...args,
            completion: dashbuilderCompletion,
            completionItemKind: kind,
          }),
          range: Range.create(args.cursorPosition, args.cursorPosition),
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
    ]);
  },
};
