/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { CancellationToken, CustomTextEditorProvider, TextDocument, WebviewPanel } from "vscode";
import { VsCodeKieEditorControllerFactory } from "./VsCodeKieEditorControllerFactory";

export class VsCodeKieEditorsTextEditorProvider implements CustomTextEditorProvider {
  public constructor(
    private readonly context: vscode.ExtensionContext,
    private readonly viewType: string,
    private readonly editorFactory: VsCodeKieEditorControllerFactory
  ) {}

  public register() {
    return vscode.window.registerCustomEditorProvider(this.viewType, this, {
      webviewOptions: {
        retainContextWhenHidden: true,
      },
    });
  }

  public async resolveCustomTextEditor(document: TextDocument, webviewPanel: WebviewPanel, token: CancellationToken) {
    this.editorFactory.configureNew(webviewPanel, { type: "text", document });
  }
}
