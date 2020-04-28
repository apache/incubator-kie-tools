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
import { CancellationToken, CustomDocument, Uri, WebviewPanel } from "vscode";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { KogitoEditingDelegate } from "./KogitoEditingDelegate";
import { KogitoEdit } from "@kogito-tooling/core-api";

export class KogitoWebviewProvider implements vscode.CustomEditorProvider<KogitoEdit> {

  private readonly viewType: string;
  private readonly editorFactory: KogitoEditorFactory;
  public readonly editingDelegate: KogitoEditingDelegate;

  public constructor(viewType: string, editorFactory: KogitoEditorFactory, editingDelegate: KogitoEditingDelegate) {
    this.viewType = viewType;
    this.editorFactory = editorFactory;
    this.editingDelegate = editingDelegate;
  }

  public register() {
    return vscode.window.registerCustomEditorProvider2(this.viewType, this, {
      webviewOptions: {
        retainContextWhenHidden: true
      }
    });
  }

  public resolveCustomEditor(
    document: CustomDocument<KogitoEdit>,
    webviewPanel: WebviewPanel,
    token: CancellationToken
  ) {
    this.editorFactory.configureNew(document.uri, webviewPanel, edit => {
      this.editingDelegate.notifyEdit(document, edit);
    });
  }

  public openCustomDocument(uri: Uri, token: CancellationToken) {
    return new KogitoCustomDocument(uri);
  }
}

class KogitoCustomDocument implements CustomDocument<KogitoEdit> {
  public readonly savedEdits: ReadonlyArray<KogitoEdit>;
  public readonly appliedEdits: ReadonlyArray<KogitoEdit>;
  public readonly isClosed: boolean;
  public readonly isDirty: boolean;
  public readonly isUntitled: boolean;
  public readonly onDidDispose: vscode.Event<void>;
  public readonly version: number;
  public readonly uri: Uri;

  constructor(uri: Uri) {
    this.uri = uri;
  }
}
