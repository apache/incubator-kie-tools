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
  | "swf.ls.commands.ImportFunctionFromCompletionItem"
  | "swf.ls.commands.OpenFunctionsWidget"
  | "swf.ls.commands.OpenStatesWidget"
  | "swf.ls.commands.OpenCompletionItems"
  | "swf.ls.commands.OpenServiceRegistriesConfig"
  | "swf.ls.commands.LogInServiceRegistries"
  | "swf.ls.commands.RefreshServiceRegistries";

export type SwfLanguageServiceCommandArgs = {
  "swf.ls.commands.ImportFunctionFromCompletionItem": {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  };
  "swf.ls.commands.OpenFunctionsWidget": { position: Position };
  "swf.ls.commands.OpenStatesWidget": { position: Position };
  "swf.ls.commands.OpenCompletionItems": { newCursorPosition: Position };
  "swf.ls.commands.OpenServiceRegistriesConfig": {};
  "swf.ls.commands.LogInServiceRegistries": {};
  "swf.ls.commands.RefreshServiceRegistries": {};
};

export type SwfLanguageServiceCommandIds = Record<SwfLanguageServiceCommandTypes, string>;

export type SwfLanguageServiceCommandHandlers = {
  [K in SwfLanguageServiceCommandTypes]: (args: SwfLanguageServiceCommandArgs[K]) => any;
};

export interface SwfLanguageServiceCommandExecution<T extends SwfLanguageServiceCommandTypes> {
  name: T;
  args: SwfLanguageServiceCommandArgs[T];
}
