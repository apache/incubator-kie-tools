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

import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";

export interface DashbuilderLanguageServiceChannelApi {
  kogitoDashbuilderLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]>;

  kogitoDashbuilderLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]>;
}

export type DashbuilderLanguageServiceCommandTypes = "dashbuilder.ls.commands.OpenCompletionItems";

export type DashbuilderLanguageServiceCommandArgs = {
  "dashbuilder.ls.commands.OpenCompletionItems": { newCursorPosition: Position };
};

export type DashbuilderLanguageServiceCommandIds = Record<DashbuilderLanguageServiceCommandTypes, string>;

export type DashbuilderLanguageServiceCommandHandlers = {
  [K in DashbuilderLanguageServiceCommandTypes]: (args: DashbuilderLanguageServiceCommandArgs[K]) => any;
};

export interface DashbuilderLanguageServiceCommandExecution<T extends DashbuilderLanguageServiceCommandTypes> {
  name: T;
  args: DashbuilderLanguageServiceCommandArgs[T];
}
