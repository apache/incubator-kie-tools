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
import { FileLanguage } from "../editor";
import { SwfLanguageService, SwfLanguageServiceArgs, SwfLSNode } from "./SwfLanguageService";

export class SwfJsonLanguageService {
  fileLanguage = FileLanguage.JSON;
  protected fileMatch = ["*.sw.json"];
  private ls: SwfLanguageService;

  constructor(args: SwfLanguageServiceArgs) {
    this.ls = new SwfLanguageService(args);
  }

  parseContent(content: string): SwfLSNode | undefined {
    return jsonc.parseTree(content);
  }

  public getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    const rootNode = this.parseContent(args.content);

    if (!rootNode) {
      return Promise.resolve([]);
    }
    return this.ls.getCompletionItems({
      ...args,
      rootNode,
    });
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    const rootNode = this.parseContent(args.content);

    if (!rootNode) {
      return Promise.resolve([]);
    }
    return this.ls.getCodeLenses({
      ...args,
      rootNode,
    });
  }

  public async getDiagnostics(args: { content: string; uriPath: string }) {
    return this.ls.getDiagnostics(args);
  }

  public dispose() {
    return this.ls.dispose();
  }
}
