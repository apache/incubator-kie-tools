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
  EditorLanguageServiceCommandArgs,
  EditorLanguageServiceCommandExecution,
  EditorLanguageServiceCommandHandlers,
  EditorLanguageServiceCommandIds,
  EditorLanguageServiceCommandTypes,
} from "@kie-tools/json-yaml-language-service/dist/api";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";

export interface YardLanguageServiceChannelApi {
  kogitoYardLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]>;

  kogitoYardLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]>;
}

export type YardLanguageServiceCommandTypes = EditorLanguageServiceCommandTypes;

export type YardLanguageServiceCommandArgs = EditorLanguageServiceCommandArgs & {};

export type YardLanguageServiceCommandIds = EditorLanguageServiceCommandIds<YardLanguageServiceCommandTypes>;

export type YardLanguageServiceCommandHandlers = EditorLanguageServiceCommandHandlers<
  YardLanguageServiceCommandTypes,
  YardLanguageServiceCommandArgs
>;

export interface YardLanguageServiceCommandExecution<T extends YardLanguageServiceCommandTypes>
  extends EditorLanguageServiceCommandExecution<T, YardLanguageServiceCommandArgs> {}
