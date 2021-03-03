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

import { BackendProxy } from "@kogito-tooling/backend/dist/api";
import {
  KogitoEdit,
  ResourceContentRequest,
  ResourceContentService,
  ResourceListRequest
} from "@kogito-tooling/channel-common-api";
import { EditorContent, KogitoEditorChannelApi, StateControlCommand } from "@kogito-tooling/editor/dist/api";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour/dist/api";
import { WorkspaceApi } from "@kogito-tooling/workspace/dist/api";
import * as __path from "path";
import * as vscode from "vscode";
import { KogitoEditor } from "./KogitoEditor";
import { Notification, NotificationsApi } from "@kogito-tooling/notifications/dist/api";
import { VsCodeI18n } from "./i18n";
import { I18n } from "@kogito-tooling/i18n/dist/core";

export class KogitoEditorChannelApiImpl implements KogitoEditorChannelApi {
  private readonly decoder = new TextDecoder("utf-8");

  constructor(
    private readonly editor: KogitoEditor,
    private readonly resourceContentService: ResourceContentService,
    private readonly workspaceApi: WorkspaceApi,
    private readonly backendProxy: BackendProxy,
    private readonly notificationsApi: NotificationsApi,
    private readonly viewType: string,
    private readonly i18n: I18n<VsCodeI18n>,
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

  public receive_setContentError(editorContent: EditorContent) {
    const i18n = this.i18n.getCurrent();

    vscode.window
      .showErrorMessage(
        i18n.errorOpeningFileText(this.editor.document.uri.fsPath.split("/").pop()!),
        i18n.openAsTextButton
      )
      .then(s1 => {
        if (s1 !== i18n.openAsTextButton) {
          return;
        }

        vscode.commands.executeCommand("vscode.openWith", this.editor.document.uri, "default");
        vscode.window.showInformationMessage(i18n.reopenAsDiagramText, i18n.reopenAsDiagramButton).then(s2 => {
          if (s2 !== i18n.reopenAsDiagramButton) {
            return;
          }

          const reopenAsDiagram = () =>
            vscode.commands.executeCommand("vscode.openWith", this.editor.document.uri, this.viewType);

          if (vscode.window.activeTextEditor?.document.uri.fsPath !== this.editor.document.uri.fsPath) {
            reopenAsDiagram();
            return;
          }

          vscode.window.activeTextEditor?.document.save().then(() => reopenAsDiagram());
        });
      });
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

  public receive_getLocale(): Promise<string> {
    return Promise.resolve(vscode.env.language);
  }

  public createNotification(notification: Notification): void {
    this.notificationsApi.createNotification(notification);
  }

  public setNotifications(path: string, notifications: Notification[]): void {
    this.notificationsApi.setNotifications(path, notifications);
  }

  public removeNotifications(path: string): void {
    this.notificationsApi.removeNotifications(path);
  }
}
