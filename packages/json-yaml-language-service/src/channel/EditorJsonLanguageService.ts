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

import * as jsonc from "jsonc-parser";
import { getLanguageService, JSONSchema } from "vscode-json-languageservice";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, CompletionItemKind, Diagnostic, Position, Range } from "vscode-languageserver-types";
import {
  ELsCodeCompletionStrategy,
  ELsNode,
  ELsShouldCreateCodelensArgs,
  IEditorLanguageService,
  ShouldCompleteArgs,
  TranslateArgs,
} from "./types";

export class EditorJsonLanguageService implements IEditorLanguageService {
  protected readonly ls: IEditorLanguageService;
  protected readonly codeCompletionStrategy: ELsCodeCompletionStrategy;

  constructor(args: { ls: IEditorLanguageService; codeCompletionStrategy: ELsCodeCompletionStrategy }) {
    this.ls = args.ls;
    this.codeCompletionStrategy = args.codeCompletionStrategy;
  }

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.ls.getCompletionItems({
      ...args,
      rootNode: parseJsonContent(args.content),
      codeCompletionStrategy: this.codeCompletionStrategy,
    });
  }

  public async getCodeLenses(args: { content: string; uri: string }): Promise<CodeLens[]> {
    return this.ls.getCodeLenses({
      ...args,
      rootNode: parseJsonContent(args.content),
      codeCompletionStrategy: this.codeCompletionStrategy,
    });
  }

  public async getDiagnostics(args: {
    content: string;
    uriPath: string;
    getSchemaDiagnostics(args: {
      textDocument: TextDocument;
      fileMatch: string[];
      jsonSchema: JSONSchema;
    }): Promise<Diagnostic[]>;
  }): Promise<Diagnostic[]> {
    return this.ls.getDiagnostics({
      ...args,
      rootNode: parseJsonContent(args.content),
      getSchemaDiagnostics: args.getSchemaDiagnostics,
    });
  }

  public async getSchemaDiagnostics(args: {
    textDocument: TextDocument;
    fileMatch: string[];
    jsonSchema: JSONSchema;
  }): Promise<Diagnostic[]> {
    const jsonLs = getLanguageService({
      schemaRequestService: async (uri) => {
        if (uri === args.jsonSchema.$id) {
          return JSON.stringify(args.jsonSchema);
        } else {
          throw new Error(`Unable to load schema from '${uri}'`);
        }
      },
    });

    jsonLs.configure({
      allowComments: false,
      schemas: [{ fileMatch: args.fileMatch, uri: args.jsonSchema.$id! }],
    });

    const jsonDocument = jsonLs.parseJSONDocument(args.textDocument);
    return jsonLs.doValidation(args.textDocument, jsonDocument);
  }

  public dispose() {
    return this.ls.dispose();
  }
}

export class EditorJsonCodeCompletionStrategy implements ELsCodeCompletionStrategy {
  public translate(args: TranslateArgs): string {
    const content = args.document.getText();
    const isContentEmpty = !content.trim();
    const isLastChild = isOffsetAtLastChild(content, args.cursorOffset);
    const hasNodeCommaAlready = !isContentEmpty ? hasNodeComma(content, args.cursorOffset) : false;

    return (
      JSON.stringify(args.completion, null, 2) + (!isContentEmpty && !isLastChild && !hasNodeCommaAlready ? "," : "")
    );
  }

  public formatLabel(label: string, completionItemKind: CompletionItemKind): string {
    return (
      [CompletionItemKind.Value, CompletionItemKind.Function, CompletionItemKind.Folder] as CompletionItemKind[]
    ).includes(completionItemKind)
      ? `"${label}"`
      : label;
  }

  public getStartNodeValuePosition(document: TextDocument, node: ELsNode): Position | undefined {
    const position = document.positionAt(node.offset);
    const nextPosition = document.positionAt(node.offset + 1);
    return node.type === "boolean" ? position : nextPosition;
  }

  public shouldComplete(args: ShouldCompleteArgs): boolean {
    const cursorJsonLocation = jsonc.getLocation(args.content, args.cursorOffset);
    return cursorJsonLocation.matches(args.path) && cursorJsonLocation.path.length === args.path.length;
  }

  public shouldCreateCodelens(_args: ELsShouldCreateCodelensArgs): boolean {
    return true;
  }
}

export function parseJsonContent(content: string): ELsNode | undefined {
  return jsonc.parseTree(content);
}

/**
 * Check if a node has a comma after the end
 *
 * @param content -
 * @param cursorOffset -
 * @returns true if found, false otherwise
 */
export function hasNodeComma(content: string, cursorOffset: number): boolean {
  return /^"?[\s\n]*,/.test(content.slice(cursorOffset));
}

/**
 * Check if an offset is at the last child.
 *
 * @param content -
 * @param cursorOffset -
 * @returns true if yes, false otherwise. If the content is empty returns true.
 */
export function isOffsetAtLastChild(content: string, cursorOffset: number): boolean {
  if (!content.trim()) {
    return true;
  }
  return /^"?[\s\n]*[\]}]/.test(content.slice(cursorOffset));
}
