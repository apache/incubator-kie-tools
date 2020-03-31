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
import { CustomDocument, WebviewPanel } from "vscode";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { KogitoEditingCapability, KogitoEditingCapabilityFactory } from "./KogitoEditingCapabilityFactory";
import { KogitoEdit } from "@kogito-tooling/core-api";

export class KogitoWebviewProvider implements vscode.CustomEditorProvider {
  public static readonly viewType = "kieKogitoWebviewEditors";

  private readonly capabilites: Map<CustomDocument, KogitoEditingCapability> = new Map<CustomDocument, KogitoEditingCapability>();

  private readonly editorFactory: KogitoEditorFactory;
  public readonly editingCapabilityFactory: KogitoEditingCapabilityFactory;

  public constructor(editorFactory: KogitoEditorFactory, editingCapabilityFactory: KogitoEditingCapabilityFactory) {
    this.editorFactory = editorFactory;
    this.editingCapabilityFactory = editingCapabilityFactory;
  }

  public register() {
    return vscode.window.registerCustomEditorProvider(KogitoWebviewProvider.viewType, this, {
      retainContextWhenHidden: true
    });
  }

  public async resolveCustomDocument(document: CustomDocument<unknown>) {
    const capability = this.editingCapabilityFactory.createNew(document);
    this.capabilites.set(document, capability);

    document.onDidDispose(e => {
      this.capabilites.delete(document);
    });

    return { editing: capability };
  }

  public async resolveCustomEditor(document: CustomDocument<unknown>, webview: WebviewPanel) {
    this.editorFactory.configureNew(document.uri, webview, edit => this.signalEdit(document, edit));
  }

  private signalEdit(document: CustomDocument, edit: KogitoEdit) {
    const capability = this.capabilites.get(document);

    if(capability) {
      capability.notifyEdit(edit);
    }
  }
}
