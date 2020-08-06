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

import {
  KogitoEdit,
  KogitoEditorChannelApi,
  ResourceContentRequest,
  ResourceContentService,
  ResourceListRequest,
  StateControlCommand,
  Tutorial,
  UserInteraction
} from "@kogito-tooling/microeditor-envelope-protocol";
import * as vscode from "vscode";
import * as fs from "fs";
import * as __path from "path";
import { KogitoEditor } from "./KogitoEditor";
import { KogitoEditableDocument } from "./KogitoEditableDocument";

export class KogitoEditorChannelApiImpl implements KogitoEditorChannelApi {
  private readonly decoder = new TextDecoder("utf-8");

  constructor(
    private readonly document: KogitoEditableDocument,
    private readonly editor: KogitoEditor,
    private readonly resourceContentService: ResourceContentService,
    private initialBackup = document.initialBackup
  ) {}

  public receive_newEdit(edit: KogitoEdit) {
    this.document.notifyEdit(this.editor, edit);
  }

  public receive_openFile(path: string) {
    const resolvedPath = __path.isAbsolute(path) ? path : __path.join(__path.dirname(this.document.uri.fsPath), path);
    if (!fs.existsSync(resolvedPath)) {
      throw new Error(`Cannot open file at: ${resolvedPath}.`);
    }
    vscode.commands.executeCommand("vscode.open", vscode.Uri.parse(resolvedPath));
  }

  public async receive_contentRequest() {
    return vscode.workspace.fs.readFile(this.initialBackup ?? this.document.uri).then(contentArray => {
      this.initialBackup = undefined;
      return { content: this.decoder.decode(contentArray), path: this.document.relativePath };
    });
  }

  public receive_setContentError(errorMessage: string) {
    vscode.window.showErrorMessage(errorMessage);
  }

  public receive_ready() {
    /* empty */
  }

  public receive_stateControlCommandUpdate(command: StateControlCommand) {
    /* VS Code has his own state control API. */
  }

  public receive_guidedTourRegisterTutorial(tutorial: Tutorial) {
    /* empty */
  }

  public receive_guidedTourUserInteraction(userInteraction: UserInteraction) {
    /* empty */
  }

  public receive_resourceContentRequest(request: ResourceContentRequest) {
    return this.resourceContentService.get(request.path, request.opts);
  }

  public receive_resourceListRequest(request: ResourceListRequest) {
    return this.resourceContentService.list(request.pattern, request.opts);
  }
}
