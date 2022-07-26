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
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";
import { SwfLanguageService, SwfLanguageServiceArgs } from "./SwfLanguageService";
import { SwfLsNode } from "./types";
import { FileLanguage } from "../api";

export class SwfJsonLanguageService {
  private readonly ls: SwfLanguageService;

  constructor(args: Omit<SwfLanguageServiceArgs, "lang">) {
    this.ls = new SwfLanguageService({
      ...args,
      lang: {
        fileLanguage: FileLanguage.JSON,
        fileMatch: ["*.sw.json"],
      },
    });
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
    return this.ls.getCompletionItems({ ...args, rootNode: this.parseContent(args.content) });
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
