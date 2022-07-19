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

  /* TODO: SwfLanguageServiceChannelApiImpl: Last thing I noticed is that on the SWF Combined Editor, the moveCursorToNode is forwarding the call to its Channel, where in fact it should handle it and communicate the SWF Text Editor that this occurred. Same thing for the highlightNode method, which should not forward, but instead communicate the Diagram that the selection changed on the SWF Text Editor. */
  public kogitoSwfLanguageService__moveCursorToNode(args: { nodeName: string; documentUri?: string }): void {
    this.channelApi.notifications.kogitoSwfLanguageService__moveCursorToNode.send(args);
  }

  public kogitoSwfLanguageService__highlightNode(args: { nodeName: string; documentUri?: string }): void {
    this.channelApi.notifications.kogitoSwfLanguageService__highlightNode.send(args);
  }
}
