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

import * as vscode from "vscode";
import { initSwfOffsetsApi } from "./languageService/initSwfOffsetsApi";

export interface ServerlessWorkflowTextEditorEnvelopeApi {
  kogitoSwfTextEditor__moveCursorToNode(args: { nodeName: string; documentUri?: string }): void;
}

export class ServerlessWorkflowTextEditorEnvelopeApiImpl implements ServerlessWorkflowTextEditorEnvelopeApi {
  public async kogitoSwfTextEditor__moveCursorToNode(args: { nodeName: string; documentUri: string }): Promise<void> {
    const textEditor = vscode.window.visibleTextEditors.filter(
      (textEditor: vscode.TextEditor) => textEditor.document.uri.path === args.documentUri
    )[0];

    if (!textEditor) {
      console.debug("TextEditor not found");
      return;
    }

    const resourceUri = textEditor.document.uri;

    const swfOffsetsApi = initSwfOffsetsApi(textEditor.document);

    const targetOffset = swfOffsetsApi.getStateNameOffset(args.nodeName);
    if (!targetOffset) {
      return;
    }

    const targetPosition = textEditor.document.positionAt(targetOffset);
    if (targetPosition === null) {
      return;
    }

    await vscode.commands.executeCommand("vscode.open", resourceUri, {
      viewColumn: textEditor.viewColumn,
      preserveFocus: false,
    } as vscode.TextDocumentShowOptions);

    const targetRange = new vscode.Range(targetPosition, targetPosition);

    textEditor.revealRange(targetRange, vscode.TextEditorRevealType.InCenter);
    textEditor.selections = [new vscode.Selection(targetPosition, targetPosition)];
  }
}
