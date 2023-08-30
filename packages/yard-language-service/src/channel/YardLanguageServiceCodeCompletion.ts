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
import { decisionCompletion, emptyDecisionCompletion } from "../assets/code-completions/";

export type YardLanguageServiceCodeCompletionFunctionsArgs = EditorLanguageServiceCodeCompletionFunctionsArgs & {};

export const YardLanguageServiceCodeCompletion: EditorLanguageServiceCodeCompletionFunctions = {
  getEmptyFileCodeCompletions(
    args: EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs
  ): Promise<CompletionItem[]> {
    const kind = CompletionItemKind.Text;
    const emptyDecisionLabel = "Empty Serverless Decision";
    const exampleDecisionLabel = "Serverless Decision Example";

    return Promise.resolve([
      {
        kind,
        label: exampleDecisionLabel,
        detail: "Start with a simple Serverless Decision",
        sortText: `100_${exampleDecisionLabel}`, //place the completion on top in the menu
        textEdit: {
          newText: args.codeCompletionStrategy.translate({
            ...args,
            completion: decisionCompletion,
            completionItemKind: kind,
          }),
          range: Range.create(args.cursorPosition, args.cursorPosition),
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
      {
        kind,
        label: emptyDecisionLabel,
        detail: "Start with an empty Serverless Decision",
        sortText: `100_${emptyDecisionLabel}`, //place the completion on top in the menu
        textEdit: {
          newText: args.codeCompletionStrategy.translate({
            ...args,
            completion: emptyDecisionCompletion,
            completionItemKind: kind,
          }),
          range: Range.create(args.cursorPosition, args.cursorPosition),
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
    ]);
  },
};
