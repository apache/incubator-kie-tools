/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { Position, TextDocument } from "vscode-languageserver-textdocument";
import { CompletionItemKind, Range } from "vscode-languageserver-types";
import { EditorLanguageServiceCommandTypes } from "../api";

// types ElsJsonPath, ELsNode, ELsNodeType need to be compatible with jsonc types
export declare type ElsJsonPath = (string | number)[];
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
  path: ElsJsonPath;
  root: ELsNode | undefined;
}

export interface ShouldCreateCodelensArgs {
  content: string;
  node: ELsNode;
  commandName: EditorLanguageServiceCommandTypes;
}

export interface TranslateArgs {
  completion: object | string;
  completionItemKind: CompletionItemKind;
  currentNodeRange?: Range;
  cursorOffset: number;
  document: TextDocument;
  overwriteRange?: Range;
}

export interface CodeCompletionStrategy {
  translate(args: TranslateArgs): string;
  formatLabel(label: string, completionItemKind: CompletionItemKind): string;
  shouldComplete(args: ShouldCompleteArgs): boolean;
  getStartNodeValuePosition(document: TextDocument, node: ELsNode): Position | undefined;
  shouldCreateCodelens(args: ShouldCreateCodelensArgs): boolean;
}
