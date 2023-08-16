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
  EditorLanguageService,
  EditorLanguageServiceArgs,
  ELsCompletionsMap,
  ELsNode,
  IEditorLanguageService,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { JSONSchema } from "vscode-json-languageservice";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, Diagnostic, Position, Range } from "vscode-languageserver-types";
import {
  DashbuilderLanguageServiceCodeCompletion,
  DashbuilderLanguageServiceCodeCompletionFunctionsArgs,
} from "./DashbuilderLanguageServiceCodeCompletion";
import { DashbuilderLanguageServiceCodeLenses } from "./DashbuilderLanguageServiceCodeLenses";
import { CodeCompletionStrategy } from "./types";

export type DashbuilderLanguageServiceArgs = EditorLanguageServiceArgs;

export class DashbuilderLanguageService implements IEditorLanguageService {
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
    getSchemaDiagnostics: (args: { textDocument: TextDocument; fileMatch: string[] }) => Promise<Diagnostic[]>;
  }): Promise<Diagnostic[]> {
    return await this.els.getDiagnostics({
      ...args,
      getSchemaDiagnostics: args.getSchemaDiagnostics,
    });
  }

  public async getSchemaDiagnostics(args: {
    textDocument: TextDocument;
    fileMatch: string[];
    jsonSchema: JSONSchema;
  }): Promise<Diagnostic[]> {
    return this.els.getSchemaDiagnostics(args);
  }

  public dispose() {
    this.els.dispose();
  }
}

const completions: ELsCompletionsMap<DashbuilderLanguageServiceCodeCompletionFunctionsArgs> = new Map([
  [null, DashbuilderLanguageServiceCodeCompletion.getEmptyFileCodeCompletions],
]);
