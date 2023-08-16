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
  EditorJsonCodeCompletionStrategy,
  EditorJsonLanguageService,
  ELsNode,
  ELsShouldCreateCodelensArgs,
  IEditorLanguageService,
  ShouldCompleteArgs,
  TranslateArgs,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, CompletionItemKind, Diagnostic, Position, Range } from "vscode-languageserver-types";
import { FileLanguage } from "../api";
import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { SwfLanguageService, SwfLanguageServiceArgs } from "./SwfLanguageService";
import { CodeCompletionStrategy, ShouldCreateCodelensArgs } from "./types";

export class SwfJsonLanguageService implements IEditorLanguageService {
  private readonly ls: SwfLanguageService;
  private readonly jsonELs: EditorJsonLanguageService;
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
    this.jsonELs = new EditorJsonLanguageService({
      ls: this.ls,
      codeCompletionStrategy: this.codeCompletionStrategy,
    });
  }

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return await this.jsonELs.getCompletionItems(args);
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    return await this.jsonELs.getCodeLenses(args);
  }

  public async getDiagnostics(args: { content: string; uriPath: string }): Promise<Diagnostic[]> {
    return await this.jsonELs.getDiagnostics({
      ...args,
      getSchemaDiagnostics: (args: { textDocument: TextDocument; fileMatch: string[] }) =>
        this.getSchemaDiagnostics(args),
    });
  }

  public async getSchemaDiagnostics(args: { textDocument: TextDocument; fileMatch: string[] }): Promise<Diagnostic[]> {
    return await this.jsonELs.getSchemaDiagnostics({
      ...args,
      jsonSchema: SW_SPEC_WORKFLOW_SCHEMA,
    });
  }

  public dispose() {
    return this.jsonELs.dispose();
  }
}

export class JsonCodeCompletionStrategy implements CodeCompletionStrategy {
  private eLsCodeCompletionStrategy: EditorJsonCodeCompletionStrategy;

  constructor() {
    this.eLsCodeCompletionStrategy = new EditorJsonCodeCompletionStrategy();
  }

  public translate(args: TranslateArgs): string {
    return this.eLsCodeCompletionStrategy.translate(args);
  }

  public formatLabel(label: string, completionItemKind: CompletionItemKind): string {
    return this.eLsCodeCompletionStrategy.formatLabel(label, completionItemKind);
  }

  public getStartNodeValuePosition(document: TextDocument, node: ELsNode): Position | undefined {
    return this.eLsCodeCompletionStrategy.getStartNodeValuePosition(document, node);
  }

  public shouldComplete(args: ShouldCompleteArgs): boolean {
    return this.eLsCodeCompletionStrategy.shouldComplete(args);
  }

  public shouldCreateCodelens(args: ShouldCreateCodelensArgs): boolean {
    return this.eLsCodeCompletionStrategy.shouldCreateCodelens(args as ELsShouldCreateCodelensArgs);
  }
}
