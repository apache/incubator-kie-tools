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
import {
  EditorLanguageService,
  EditorLanguageServiceArgs,
  ELsCompletionsMap,
  ELsNode,
} from "@kie-tools/editor-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, Diagnostic, Position, Range } from "vscode-languageserver-types";
import {
  DashbuilderLanguageServiceCodeCompletion,
  DashbuilderLanguageServiceCodeCompletionFunctionsArgs,
} from "./DashbuilderLanguageServiceCodeCompletion";
import { DashbuilderLanguageServiceCodeLenses } from "./DashbuilderLanguageServiceCodeLenses";
import { CodeCompletionStrategy } from "./types";

export type DashbuilderLanguageServiceArgs = EditorLanguageServiceArgs;

export class DashbuilderLanguageService {
  private readonly els: EditorLanguageService;

  constructor(private readonly args: DashbuilderLanguageServiceArgs) {
    this.els = new EditorLanguageService(this.args);
  }

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CompletionItem[]> {
    return this.els.getCompletionItems({
      ...args,
      completions,
    });
  }

  public async getCodeLenses(args: {
    content: string;
    uri: string;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: CodeCompletionStrategy;
  }): Promise<CodeLens[]> {
    return this.els.getCodeLenses({
      ...args,
      codeLenses: DashbuilderLanguageServiceCodeLenses,
    });
  }

  public async getDiagnostics(args: {
    content: string;
    uriPath: string;
    rootNode: ELsNode | undefined;
    getSchemaDiagnostics: (textDocument: TextDocument, fileMatch: string[]) => Promise<Diagnostic[]>;
  }): Promise<Diagnostic[]> {
    return await this.els.getDiagnostics({
      ...args,
      getSchemaDiagnostics: args.getSchemaDiagnostics,
    });
  }

  public dispose() {
    // empty for now
  }
}

const completions: ELsCompletionsMap<DashbuilderLanguageServiceCodeCompletionFunctionsArgs> = new Map([
  [null, DashbuilderLanguageServiceCodeCompletion.getEmptyFileCodeCompletions],
]);
