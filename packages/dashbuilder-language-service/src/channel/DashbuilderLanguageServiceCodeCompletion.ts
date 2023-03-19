/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { CompletionItem, CompletionItemKind, InsertTextFormat, Position, Range } from "vscode-languageserver-types";
import { dashbuilderCompletion } from "../assets/code-completions/";
import { DashbuilderLsNode, TranslateArgs } from "./types";
import { TextDocument } from "vscode-languageserver-textdocument";
import { indentText } from "./indentText";
import { positions_equals } from "./DashbuilderLanguageService";
import { dump } from "yaml-language-server-parser";

export type DashbuilderLanguageServiceCodeCompletionFunctionsArgs = {
  currentNode: DashbuilderLsNode;
  currentNodeRange: Range;
  cursorOffset: number;
  document: TextDocument;
  overwriteRange: Range;
  rootNode: DashbuilderLsNode;
};

const translateYamlDoc = (args: TranslateArgs) => {
  const completionDump = dump(args.completion, {}).slice(2, -1).trim();
  if (["{}", "[]"].includes(completionDump) || args.completionItemKind === CompletionItemKind.Text) {
    return completionDump;
  }
  const skipFirstLineIndent = args.completionItemKind !== CompletionItemKind.Module;
  const completionItemNewLine = args.completionItemKind === CompletionItemKind.Module ? "\n" : "";
  const completionText = completionItemNewLine + indentText(completionDump, 2, " ", skipFirstLineIndent);
  return ([CompletionItemKind.Interface, CompletionItemKind.Reference] as CompletionItemKind[]).includes(
    args.completionItemKind
  ) && positions_equals(args.overwriteRange?.start ?? null, args.currentNodeRange?.start ?? null)
    ? `- ${completionText}\n`
    : completionText;
};
/**
 * DashbuilderLanguageService CodeCompletion functions
 */

export const DashbuilderLanguageServiceCodeCompletion = {
  getEmptyFileCodeCompletions(args: {
    cursorPosition: Position;
    cursorOffset: number;
    document: TextDocument;
  }): CompletionItem[] {
    const kind = CompletionItemKind.Text;
    const label = "Create your first dashboard";
    return [
      {
        kind,
        label,
        detail: "Start with a simple dashboard",
        sortText: `100_${label}`, //place the completion on top in the menu
        textEdit: {
          newText: translateYamlDoc({
            ...args,
            completion: dashbuilderCompletion,
            completionItemKind: kind,
          }),
          range: Range.create(args.cursorPosition, args.cursorPosition),
        },
        insertTextFormat: InsertTextFormat.Snippet,
      },
    ];
  },
};
