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

import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";
import { ServerlessWorkflowCombinedEditorChannelApi } from "../api";

export class SwfLanguageServiceChannelApiImpl implements SwfLanguageServiceChannelApi {
  constructor(private readonly channelApi: MessageBusClientApi<ServerlessWorkflowCombinedEditorChannelApi>) {}

  public async kogitoSwfLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.channelApi.requests.kogitoSwfLanguageService__getCompletionItems(args);
  }

  public async kogitoSwfLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]> {
    return this.channelApi.requests.kogitoSwfLanguageService__getCodeLenses(args);
  }
}
