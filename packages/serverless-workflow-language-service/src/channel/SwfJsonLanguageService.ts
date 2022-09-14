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

import * as jsonc from "jsonc-parser";
import { CodeLens, CompletionItem, CompletionItemKind, Position, Range } from "vscode-languageserver-types";
import { SwfLanguageService, SwfLanguageServiceArgs } from "./SwfLanguageService";
import { SwfLsNode, CodeCompletionStrategy, ShouldCompleteArgs } from "./types";
import { FileLanguage } from "../api";

export class SwfJsonLanguageService {
  private readonly ls: SwfLanguageService;
  private readonly codeCompletionStrategy: JsonCodeCompletionStrategy;

  constructor(args: Omit<SwfLanguageServiceArgs, "lang">) {
    this.ls = new SwfLanguageService({
      ...args,
      lang: {
        fileLanguage: FileLanguage.JSON,
        fileMatch: ["*.sw.json"],
      },
    });

    this.codeCompletionStrategy = new JsonCodeCompletionStrategy();
  }

  parseContent(content: string): SwfLsNode | undefined {
    return jsonc.parseTree(content);
  }

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.ls.getCompletionItems({
      ...args,
      rootNode: this.parseContent(args.content),
      codeCompletionStrategy: this.codeCompletionStrategy,
    });
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    return this.ls.getCodeLenses({ ...args, rootNode: this.parseContent(args.content) });
  }

  public async getDiagnostics(args: { content: string; uriPath: string }) {
    return this.ls.getDiagnostics({ ...args, rootNode: this.parseContent(args.content) });
  }

  public dispose() {
    return this.ls.dispose();
  }
}

class JsonCodeCompletionStrategy implements CodeCompletionStrategy {
  public translate(completion: object | string): string {
    return JSON.stringify(completion, null, 2);
  }

  public formatLabel(label: string, completionItemKind: CompletionItemKind): string {
    return (
      [CompletionItemKind.Value, CompletionItemKind.Function, CompletionItemKind.Folder] as CompletionItemKind[]
    ).includes(completionItemKind)
      ? `"${label}"`
      : label;
  }

  public shouldComplete(args: ShouldCompleteArgs): boolean {
    const cursorJsonLocation = jsonc.getLocation(args.content, args.cursorOffset);
    return cursorJsonLocation.matches(args.path) && cursorJsonLocation.path.length === args.path.length;
  }
}
