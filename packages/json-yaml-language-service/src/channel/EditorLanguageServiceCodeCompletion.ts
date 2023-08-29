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

import { TextDocument } from "vscode-languageserver-textdocument";
import { ELsCodeCompletionStrategy, ELsJsonPath, ELsNode } from "./types";
import { CompletionItem, CompletionItemKind, InsertTextFormat, Position, Range } from "vscode-languageserver-types";

export type EditorLanguageServiceCodeCompletionFunctionsArgs = {
  codeCompletionStrategy: ELsCodeCompletionStrategy;
  currentNode: ELsNode;
  currentNodeRange: Range;
  cursorOffset: number;
  cursorPosition: Position;
  document: TextDocument;
  overwriteRange: Range;
  rootNode: ELsNode;
};

export type EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs = {
  cursorPosition: Position;
  codeCompletionStrategy: ELsCodeCompletionStrategy;
  cursorOffset: number;
  document: TextDocument;
};

export type ELsCompletionsMap<ARGS extends EditorLanguageServiceCodeCompletionFunctionsArgs> = Map<
  ELsJsonPath | null,
  (args: ARGS | EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs) => Promise<CompletionItem[]>
>;

export function createCompletionItem(args: {
  codeCompletionStrategy: ELsCodeCompletionStrategy;
  completion: object | string;
  currentNodeRange: Range;
  cursorOffset: number;
  document: TextDocument;
  detail: string;
  extraOptions?: Partial<CompletionItem>;
  kind: CompletionItemKind;
  label: string;
  overwriteRange: Range;
  filterText?: string;
}): CompletionItem {
  return {
    kind: args.kind,
    label: args.label,
    sortText: `100_${args.label}`, //place the completion on top in the menu
    filterText: args.filterText ?? args.label,
    detail: args.detail,
    textEdit: {
      newText: args.codeCompletionStrategy.translate({
        ...args,
        completionItemKind: args.kind,
      }),
      range: args.overwriteRange,
    },
    insertTextFormat: InsertTextFormat.Snippet,
    ...args.extraOptions,
  };
}

/**
 * CodeCompletion functions type
 */
export type EditorLanguageServiceCodeCompletionFunctions = {
  [name: string]: (args: EditorLanguageServiceCodeCompletionFunctionsArgs) => Promise<CompletionItem[]>;

  getEmptyFileCodeCompletions: (
    args: EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs
  ) => Promise<CompletionItem[]>;
};
