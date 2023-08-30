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
  createCompletionItem,
  EditorLanguageService,
  EditorLanguageServiceArgs,
  EditorLanguageServiceCodeCompletionFunctionsArgs,
  EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs,
  ELsCodeCompletionStrategy,
  ELsCompletionsMap,
  ELsNode,
  IEditorLanguageService,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { JSONSchema } from "vscode-json-languageservice";
import { TextDocument } from "vscode-languageserver-textdocument";
import {
  CodeLens,
  CompletionItem,
  CompletionItemKind,
  Diagnostic,
  InsertTextFormat,
  Position,
  Range,
} from "vscode-languageserver-types";
import { testRefValidationMap } from "./testRefValidationMap";
import { testLanguageServiceCodeLenses } from "./TestLanguageServiceCodeLenses";

export class TestLanguageService implements IEditorLanguageService {
  private readonly els: EditorLanguageService;

  constructor(private readonly args: EditorLanguageServiceArgs) {
    this.els = new EditorLanguageService(this.args);
  }

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: ELsCodeCompletionStrategy;
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
    codeCompletionStrategy: ELsCodeCompletionStrategy;
  }): Promise<CodeLens[]> {
    return this.els.getCodeLenses({
      ...args,
      codeLenses: testLanguageServiceCodeLenses,
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
      validationMap: testRefValidationMap,
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

const completions: ELsCompletionsMap<EditorLanguageServiceCodeCompletionFunctionsArgs> = new Map([
  [
    null,
    async (args: EditorLanguageServiceEmptyFileCodeCompletionFunctionArgs): Promise<CompletionItem[]> => {
      const kind = CompletionItemKind.Text;
      const emptyWorkflowLabel = "Empty Serverless Workflow";
      const exampleWorkflowLabel = "Serverless Workflow Example";

      return Promise.resolve([
        {
          kind,
          label: emptyWorkflowLabel,
          detail: "Start with an empty Serverless Workflow",
          sortText: `100_${emptyWorkflowLabel}`, //place the completion on top in the menu
          textEdit: {
            newText: args.codeCompletionStrategy.translate({
              ...args,
              completion: { id: "Empty code completion test" },
              completionItemKind: kind,
            }),
            range: Range.create(args.cursorPosition, args.cursorPosition),
          },
          insertTextFormat: InsertTextFormat.Snippet,
        },
      ]);
    },
  ],
  [
    ["start"],
    async (args: EditorLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
      const kind = CompletionItemKind.Interface;

      return Promise.resolve([
        createCompletionItem({
          ...args,
          completion: "Start Code completed!",
          kind,
          label: "New start completion",
          detail: "Add a new start completion",
        }),
      ]);
    },
  ],
  [
    ["functions"],
    async (args: EditorLanguageServiceCodeCompletionFunctionsArgs): Promise<CompletionItem[]> => {
      const kind = CompletionItemKind.Interface;

      return Promise.resolve([
        createCompletionItem({
          ...args,
          completion: "Function Code completed!",
          kind,
          label: "New function completion",
          detail: "Add a new function",
        }),
      ]);
    },
  ],
]);
