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

import { CodeLens, CompletionItem, Position, Range, TextDocumentIdentifier } from "vscode-languageserver-types";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";

export interface SwfLanguageServiceChannelApi {
  kogitoSwfLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]>;

  kogitoSwfLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]>;
}

export type SwfLanguageServiceCommandTypes =
  | "LogInToRhhcc"
  | "SetupServiceRegistryUrl"
  | "RefreshServiceCatalogFromRhhcc"
  | "ImportFunctionFromCompletionItem"
  | "OpenFunctionsWidget"
  | "OpenStatesWidget"
  | "OpenFunctionsCompletionItems";

export type SwfLanguageServiceCommandArgs = {
  LogInToRhhcc: {};
  SetupServiceRegistryUrl: {};
  RefreshServiceCatalogFromRhhcc: {};
  ImportFunctionFromCompletionItem: { containingService: SwfServiceCatalogService };
  OpenFunctionsWidget: { position: Position };
  OpenStatesWidget: { position: Position };
  OpenFunctionsCompletionItems: { newCursorPosition: Position };
};

export type SwfLanguageServiceCommandIds = Record<SwfLanguageServiceCommandTypes, string>;

export interface SwfLanguageServiceCommandExecution<T extends SwfLanguageServiceCommandTypes> {
  name: T;
  args: SwfLanguageServiceCommandArgs[T];
}
