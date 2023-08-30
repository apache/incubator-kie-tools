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
  EditorYamlCodeCompletionStrategy,
  EditorYamlLanguageService,
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

export class SwfYamlLanguageService implements IEditorLanguageService {
  private readonly ls: SwfLanguageService;
  private readonly yamlELs: EditorYamlLanguageService;
  private readonly codeCompletionStrategy: SwfYamlCodeCompletionStrategy;

  constructor(args: Omit<SwfLanguageServiceArgs, "lang">) {
    this.ls = new SwfLanguageService({
      ...args,
      lang: {
        fileLanguage: FileLanguage.YAML,
        fileMatch: ["*.sw.yaml", "*.sw.yml"],
      },
    });

    this.codeCompletionStrategy = new SwfYamlCodeCompletionStrategy();
    this.yamlELs = new EditorYamlLanguageService({
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
    return await this.yamlELs.getCompletionItems(args);
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    return await this.yamlELs.getCodeLenses(args);
  }

  public async getDiagnostics(args: { content: string; uriPath: string }): Promise<Diagnostic[]> {
    return await this.yamlELs.getDiagnostics({
      ...args,
      getSchemaDiagnostics: (args: { textDocument: TextDocument; fileMatch: string[] }) =>
        this.getSchemaDiagnostics(args),
    });
  }

  public async getSchemaDiagnostics(args: { textDocument: TextDocument; fileMatch: string[] }): Promise<Diagnostic[]> {
    return await this.yamlELs.getSchemaDiagnostics({
      ...args,
      jsonSchema: SW_SPEC_WORKFLOW_SCHEMA,
    });
  }

  public dispose() {
    return this.yamlELs.dispose();
  }
}

export class SwfYamlCodeCompletionStrategy implements CodeCompletionStrategy {
  private eLsCodeCompletionStrategy: EditorYamlCodeCompletionStrategy;

  constructor() {
    this.eLsCodeCompletionStrategy = new EditorYamlCodeCompletionStrategy();
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
