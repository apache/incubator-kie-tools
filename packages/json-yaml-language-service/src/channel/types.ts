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

import { Position, TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, CompletionItemKind, Diagnostic, Range } from "vscode-languageserver-types";
import { JSONSchema } from "vscode-json-languageservice";

// types ElsJsonPath, ELsNode, ELsNodeType need to be compatible with jsonc types
export declare type ELsJsonPath = (string | number)[];
export declare type ELsNodeType = "object" | "array" | "property" | "string" | "number" | "boolean" | "null";

type OmitDistributive<T, K extends PropertyKey> = T extends any
  ? T extends object
    ? Id<OmitRecursively<T, K>>
    : T
  : never;
type Id<T> = {} & { [P in keyof T]: T[P] };
export type OmitRecursively<T extends any, K extends PropertyKey> = Omit<
  { [P in keyof T]: OmitDistributive<T[P], K> },
  K
>;

/**
 * The AST node used in the LanguageServices
 */
export type ELsNode = {
  type: ELsNodeType;
  value?: any;
  offset: number;
  length: number;
  colonOffset?: number;
  parent?: ELsNode;
  children?: ELsNode[];
};

export interface ShouldCompleteArgs {
  content: string;
  cursorOffset: number;
  cursorPosition: Position;
  node: ELsNode | undefined;
  path: ELsJsonPath;
  root: ELsNode | undefined;
}

export interface ELsShouldCreateCodelensArgs<CommandTypes = never> {
  content: string;
  node: ELsNode;
  commandName: CommandTypes;
}

export interface TranslateArgs {
  completion: object | string;
  completionItemKind: CompletionItemKind;
  currentNodeRange?: Range;
  cursorOffset: number;
  document: TextDocument;
  overwriteRange?: Range;
}

export interface ELsCodeCompletionStrategy<CommandTypes = never> {
  translate(args: TranslateArgs): string;
  formatLabel(label: string, completionItemKind: CompletionItemKind): string;
  shouldComplete(args: ShouldCompleteArgs): boolean;
  getStartNodeValuePosition(document: TextDocument, node: ELsNode): Position | undefined;
  shouldCreateCodelens(args: ELsShouldCreateCodelensArgs<CommandTypes>): boolean;
}

export interface IEditorLanguageService {
  getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: ELsCodeCompletionStrategy;
  }): Promise<CompletionItem[]>;

  getCodeLenses(args: {
    content: string;
    uri: string;
    rootNode: ELsNode | undefined;
    codeCompletionStrategy: ELsCodeCompletionStrategy;
  }): Promise<CodeLens[]>;

  getDiagnostics(args: {
    content: string;
    uriPath: string;
    rootNode: ELsNode | undefined;
    getSchemaDiagnostics: (args: {
      textDocument: TextDocument;
      fileMatch: string[];
      jsonSchema: JSONSchema;
    }) => Promise<Diagnostic[]>;
  }): Promise<Diagnostic[]>;

  getSchemaDiagnostics(args: {
    textDocument: TextDocument;
    fileMatch: string[];
    jsonSchema: JSONSchema;
  }): Promise<Diagnostic[]>;

  dispose(): void;
}
