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

import { KogitoEditorChannelApi, StateControlCommand } from "@kogito-tooling/editor/dist/api";
import {
  KogitoEdit,
  ResourceContentRequest,
  ResourceContentService,
  ResourceListRequest,
  WorkspaceApi
} from "@kogito-tooling/channel-common-api";
import * as vscode from "vscode";
import * as __path from "path";
import { KogitoEditor } from "./KogitoEditor";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour/dist/api";

export class KogitoEditorChannelApiImpl implements KogitoEditorChannelApi {
  private readonly decoder = new TextDecoder("utf-8");

  constructor(
    private readonly editor: KogitoEditor,
    private readonly resourceContentService: ResourceContentService,
    private readonly workspaceApi: WorkspaceApi,
    private initialBackup = editor.document.initialBackup
  ) {}

  public receive_newEdit(edit: KogitoEdit) {
    this.editor.document.notifyEdit(this.editor, edit);
  }

  public receive_openFile(path: string) {
    this.workspaceApi.receive_openFile(
      __path.isAbsolute(path) ? path : __path.join(__path.dirname(this.editor.document.uri.fsPath), path)
    );
  }

  public async receive_contentRequest() {
    return vscode.workspace.fs.readFile(this.initialBackup ?? this.editor.document.uri).then(contentArray => {
      this.initialBackup = undefined;
      return { content: this.decoder.decode(contentArray), path: this.editor.document.relativePath };
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
