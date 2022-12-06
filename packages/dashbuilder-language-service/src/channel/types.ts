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

import { TextDocument } from "vscode-languageserver-textdocument";
import { CompletionItemKind, Range } from "vscode-languageserver-types";

export declare type DashbuilderLsNodeType = "object" | "array" | "property" | "string" | "number" | "boolean" | "null";
export declare type DashbuilderJsonPath = (string | number)[];

/**
 * The AST node used in the LanguageServices
 */
export type DashbuilderLsNode = {
  type: DashbuilderLsNodeType;
  value?: any;
  offset: number;
  length: number;
  colonOffset?: number;
  parent?: DashbuilderLsNode;
  children?: DashbuilderLsNode[];
};

export interface TranslateArgs {
  completion: object | string;
  completionItemKind: CompletionItemKind;
  currentNodeRange?: Range;
  cursorOffset: number;
  document: TextDocument;
  overwriteRange?: Range;
}
