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

import { BackendProxy } from "@kie-tooling-core/backend/dist/api";
import {
  KogitoEdit,
  ResourceContentRequest,
  ResourceContentService,
  ResourceListRequest,
} from "@kie-tooling-core/workspace/dist/api";
import { EditorContent, KogitoEditorChannelApi, StateControlCommand } from "@kie-tooling-core/editor/dist/api";
import { Tutorial, UserInteraction } from "@kie-tooling-core/guided-tour/dist/api";
import { WorkspaceApi } from "@kie-tooling-core/workspace/dist/api";
import * as __path from "path";
import * as vscode from "vscode";
import { KogitoEditor } from "./KogitoEditor";
import { Notification, NotificationsApi } from "@kie-tooling-core/notifications/dist/api";
import { VsCodeI18n } from "./i18n";
import { I18n } from "@kie-tooling-core/i18n/dist/core";

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

  public kogitoWorkspace_newEdit(edit: KogitoEdit) {
    this.editor.document.notifyEdit(this.editor, edit);
  }

  public kogitoWorkspace_openFile(path: string) {
    this.workspaceApi.kogitoWorkspace_openFile(
      __path.isAbsolute(path) ? path : __path.join(__path.dirname(this.editor.document.uri.fsPath), path)
    );
  }

  public async kogitoEditor_contentRequest() {
    return vscode.workspace.fs.readFile(this.initialBackup ?? this.editor.document.uri).then((contentArray) => {
      this.initialBackup = undefined;
      return { content: this.decoder.decode(contentArray), path: this.editor.document.relativePath };
    });
  }

  public kogitoEditor_setContentError(editorContent: EditorContent) {
    const i18n = this.i18n.getCurrent();

    vscode.window
      .showErrorMessage(
        i18n.errorOpeningFileText(this.editor.document.uri.fsPath.split("/").pop()!),
        i18n.openAsTextButton
      )
      .then((s1) => {
        if (s1 !== i18n.openAsTextButton) {
          return;
        }

        this.editor.close();
        vscode.commands.executeCommand("vscode.openWith", this.editor.document.uri, "default");
        vscode.window.showInformationMessage(i18n.reopenAsDiagramText, i18n.reopenAsDiagramButton).then((s2) => {
          if (s2 !== i18n.reopenAsDiagramButton) {
            return;
          }

          vscode.window
            .showTextDocument(this.editor.document.uri)
            .then((editor) => editor.document.save())
            .then(() => vscode.commands.executeCommand("workbench.action.closeActiveEditor"))
            .then(() => vscode.commands.executeCommand("vscode.openWith", this.editor.document.uri, this.viewType));
        });
      });
  }

  public kogitoEditor_ready() {
    /* empty */
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand) {
    /* VS Code has his own state control API. */
  }

  public kogitoGuidedTour_guidedTourRegisterTutorial(tutorial: Tutorial) {
    /* empty */
  }

  public kogitoGuidedTour_guidedTourUserInteraction(userInteraction: UserInteraction) {
    /* empty */
  }

  public kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest) {
    return this.resourceContentService.get(request.path, request.opts);
  }

  public kogitoWorkspace_resourceListRequest(request: ResourceListRequest) {
    return this.resourceContentService.list(request.pattern, request.opts);
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return Promise.resolve(vscode.env.language);
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.notificationsApi.kogitoNotifications_createNotification(notification);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    this.notificationsApi.kogitoNotifications_setNotifications(path, notifications);
  }

  public kogitoNotifications_removeNotifications(path: string): void {
    this.notificationsApi.kogitoNotifications_removeNotifications(path);
  }
}
