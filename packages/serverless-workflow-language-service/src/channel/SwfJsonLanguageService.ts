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
import { getLanguageService } from "vscode-json-languageservice";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, CompletionItemKind, Diagnostic, Position, Range } from "vscode-languageserver-types";
import { FileLanguage } from "../api";
import { SW_SPEC_WORKFLOW_SCHEMA } from "../schemas";
import { SwfLanguageService, SwfLanguageServiceArgs } from "./SwfLanguageService";
import {
  CodeCompletionStrategy,
  ShouldCompleteArgs,
  ShouldCreateCodelensArgs,
  SwfLsNode,
  TranslateArgs,
} from "./types";

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
    return this.ls.getCodeLenses({
      ...args,
      rootNode: this.parseContent(args.content),
      codeCompletionStrategy: this.codeCompletionStrategy,
    });
  }

  public async getDiagnostics(args: { content: string; uriPath: string }) {
    return this.ls.getDiagnostics({
      ...args,
      rootNode: this.parseContent(args.content),
      getSchemaDiagnostics: this.getSchemaDiagnostics,
    });
  }

  private async getSchemaDiagnostics(textDocument: TextDocument, fileMatch: string[]): Promise<Diagnostic[]> {
    const jsonLs = getLanguageService({
      schemaRequestService: async (uri) => {
        if (uri === SW_SPEC_WORKFLOW_SCHEMA.$id) {
          return JSON.stringify(SW_SPEC_WORKFLOW_SCHEMA);
        } else {
          throw new Error(`Unable to load schema from '${uri}'`);
        }
      },
    });

    jsonLs.configure({
      allowComments: false,
      schemas: [{ fileMatch: fileMatch, uri: SW_SPEC_WORKFLOW_SCHEMA.$id }],
    });

    const jsonDocument = jsonLs.parseJSONDocument(textDocument);
    return jsonLs.doValidation(textDocument, jsonDocument);
  }

  public dispose() {
    return this.ls.dispose();
  }
}

export class JsonCodeCompletionStrategy implements CodeCompletionStrategy {
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

  public getStartNodeValuePosition(document: TextDocument, node: SwfLsNode): Position | undefined {
    const position = document.positionAt(node.offset);
    const nextPosition = document.positionAt(node.offset + 1);
    return node.type === "boolean" ? position : nextPosition;
  }

  public shouldComplete(args: ShouldCompleteArgs): boolean {
    const cursorJsonLocation = jsonc.getLocation(args.content, args.cursorOffset);
    return cursorJsonLocation.matches(args.path) && cursorJsonLocation.path.length === args.path.length;
  }

  public shouldCreateCodelens(_args: ShouldCreateCodelensArgs): boolean {
    return true;
  }
}
