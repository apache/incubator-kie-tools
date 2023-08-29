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
import { JSONSchema } from "vscode-json-languageservice";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, Diagnostic, Position, Range } from "vscode-languageserver-types";
import { FileLanguage } from "../api";
import {
  EditorLanguageServiceCodeCompletionFunctionsArgs,
  ELsCompletionsMap,
} from "./EditorLanguageServiceCodeCompletion";
import {
  EditorLanguageServiceCodeLenses,
  EditorLanguageServiceCodeLensesFunctionsArgs,
} from "./EditorLanguageServiceCodeLenses";
import { findNodesAtLocation } from "./findNodesAtLocation";
import { doRefValidation, RefValidationMap } from "./refValidation";
import { ELsCodeCompletionStrategy, ELsJsonPath, ELsNode, IEditorLanguageService } from "./types";

export type EditorLanguageServiceArgs = {
  fs: {};
  lang: {
    fileLanguage: FileLanguage;
    fileMatch: string[];
  };
};

export class EditorLanguageService implements IEditorLanguageService {
  constructor(private readonly args: EditorLanguageServiceArgs) {}

  public async getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: ELsCodeCompletionStrategy;
    completions: ELsCompletionsMap<EditorLanguageServiceCodeCompletionFunctionsArgs>;
    extraCompletionFunctionsArgs?: {};
  }): Promise<CompletionItem[]> {
    const doc = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);
    const cursorOffset = doc.offsetAt(args.cursorPosition);

    if (!args.rootNode) {
      const getEmptyFileCodeCompletions = args.completions.get(null);
      return args.content.trim().length || !getEmptyFileCodeCompletions
        ? []
        : getEmptyFileCodeCompletions({ ...args, cursorOffset, document: doc });
    }

    const currentNode = findNodeAtOffset(args.rootNode, cursorOffset, true);
    if (!currentNode) {
      return [];
    }

    const currentNodeRange: Range = {
      start: doc.positionAt(currentNode.offset),
      end: doc.positionAt(currentNode.offset + currentNode.length),
    };
    const overwriteRange = ["string", "number", "boolean", "null"].includes(currentNode?.type)
      ? currentNodeRange
      : args.cursorWordRange;

    const matchedCompletions = Array.from(args.completions.entries()).filter(
      ([path, _]) =>
        path &&
        args.codeCompletionStrategy.shouldComplete({
          content: args.content,
          cursorOffset: cursorOffset,
          cursorPosition: args.cursorPosition,
          node: currentNode,
          path,
          root: args.rootNode,
        })
    );
    const result = await Promise.all(
      matchedCompletions.map(([_, completionItemsDelegate]) => {
        return completionItemsDelegate({
          codeCompletionStrategy: args.codeCompletionStrategy,
          currentNode,
          currentNodeRange,
          cursorOffset,
          cursorPosition: args.cursorPosition,
          document: doc,
          overwriteRange,
          rootNode: args.rootNode!,
          ...(args.extraCompletionFunctionsArgs || {}),
        });
      })
    );
    return Promise.resolve(result.flat());
  }

  public async getDiagnostics(args: {
    content: string;
    uriPath: string;
    rootNode: ELsNode | undefined;
    getSchemaDiagnostics?: (args: { textDocument: TextDocument; fileMatch: string[] }) => Promise<Diagnostic[]>;
    validationMap?: RefValidationMap;
  }): Promise<Diagnostic[]> {
    if (!args.rootNode) {
      return [];
    }

    // this ensure the document is validated again
    const docVersion = Math.floor(Math.random() * 1000);

    const textDocument = TextDocument.create(args.uriPath, this.args.lang.fileLanguage, docVersion, args.content);
    const refValidationResults = args.validationMap
      ? doRefValidation({ textDocument, rootNode: args.rootNode, validationMap: args.validationMap })
      : [];
    const schemaValidationResults =
      (await args.getSchemaDiagnostics?.({ textDocument, fileMatch: this.args.lang.fileMatch })) ?? [];

    return [...schemaValidationResults, ...refValidationResults];
  }

  public async getCodeLenses(args: {
    content: string;
    uri: string;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: ELsCodeCompletionStrategy;
    codeLenses: EditorLanguageServiceCodeLenses;
    extraCodeLensesFunctionsArgs?: {};
  }): Promise<CodeLens[]> {
    if (!args.content.trim().length) {
      return args.codeLenses.createNewFile();
    }

    if (!args.rootNode) {
      return [];
    }

    const document = TextDocument.create(args.uri, this.args.lang.fileLanguage, 0, args.content);

    const codeLensesFunctionsArgs: EditorLanguageServiceCodeLensesFunctionsArgs = {
      document,
      content: args.content,
      rootNode: args.rootNode,
      codeCompletionStrategy: args.codeCompletionStrategy,
      ...args.extraCodeLensesFunctionsArgs,
    };

    const result: CodeLens[] = [];
    Object.keys(args.codeLenses).forEach((key) => {
      if (key !== "createNewFile") {
        result.push(...args.codeLenses[key](codeLensesFunctionsArgs));
      }
    });

    return result;
  }

  getSchemaDiagnostics(args: {
    textDocument: TextDocument;
    fileMatch: string[];
    jsonSchema: JSONSchema;
  }): Promise<Diagnostic[]> {
    return Promise.resolve([]);
  }

  public dispose() {
    // empty for now
  }
}

export function findNodeAtLocation(root: ELsNode, path: ELsJsonPath): ELsNode | undefined {
  return findNodesAtLocation({ root, path })[0];
}

export function findNodeAtOffset(root: ELsNode, offset: number, includeRightBound?: boolean): ELsNode | undefined {
  return jsonc.findNodeAtOffset(root as jsonc.Node, offset, includeRightBound) as ELsNode;
}

export function getNodePath(node: ELsNode): ELsJsonPath {
  return jsonc.getNodePath(node as jsonc.Node);
}

/**
 * Test if position `a` equals position `b`.
 * This function is compatible with https://microsoft.github.io/monaco-editor/api/classes/monaco.Position.html#equals-1
 *
 * @param a -
 * @param b -
 * @returns true if the positions are equal, false otherwise
 */
export const positions_equals = (a: Position | null, b: Position | null): boolean =>
  a?.line === b?.line && a?.character == b?.character;
