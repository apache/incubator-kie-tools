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

import { BackendProxy } from "@kie-tools-core/backend/dist/api";
import {
  WorkspaceEdit,
  ResourceContentRequest,
  ResourceContentService,
  ResourceListRequest,
  WorkspaceChannelApi,
} from "@kie-tools-core/workspace/dist/api";
import { EditorContent, KogitoEditorChannelApi, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { Tutorial, UserInteraction } from "@kie-tools-core/guided-tour/dist/api";
import * as __path from "path";
import * as vscode from "vscode";
import { VsCodeKieEditorController } from "./VsCodeKieEditorController";
import { Notification, NotificationsChannelApi } from "@kie-tools-core/notifications/dist/api";
import { VsCodeI18n } from "./i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import {
  JavaCodeCompletionAccessor,
  JavaCodeCompletionApi,
  JavaCodeCompletionChannelApi,
  JavaCodeCompletionClass,
} from "@kie-tools-core/vscode-java-code-completion/dist/api";

export class DefaultVsCodeKieEditorChannelApiImpl implements KogitoEditorChannelApi, JavaCodeCompletionChannelApi {
  constructor(
    private readonly editor: VsCodeKieEditorController,
    private readonly resourceContentService: ResourceContentService,
    private readonly workspaceApi: WorkspaceChannelApi,
    private readonly backendProxy: BackendProxy,
    private readonly notificationsApi: NotificationsChannelApi,
    private readonly javaCodeCompletionApi: JavaCodeCompletionApi,
    private readonly viewType: string,
    private readonly i18n: I18n<VsCodeI18n>
  ) {}

  public async kogitoWorkspace_newEdit(workspaceEdit: WorkspaceEdit) {
    if (this.editor.document.type === "custom") {
      this.editor.document.document.notifyEdit(this.editor, workspaceEdit);
      return;
    }

    if (this.editor.document.type === "text") {
      this.editor.stopListeningToDocumentChanges();

      const changeDocumentSubscription = vscode.workspace.onDidChangeTextDocument(async (e) => {
        if (e.contentChanges.length <= 0) {
          return;
        }

        this.editor.startListeningToDocumentChanges();
        changeDocumentSubscription.dispose();
      });

      const { content } = await this.editor.envelopeServer.envelopeApi.requests.kogitoEditor_contentRequest();

      const edit = new vscode.WorkspaceEdit();

      // TODO: This shouldn't be a replace all the time. More conscious changes lead to better undo/redo stack.
      // See https://issues.redhat.com/browse/KOGITO-7106
      edit.replace(
        this.editor.document.document.uri,
        new vscode.Range(0, 0, this.editor.document.document.lineCount, 0),
        content
      );

      vscode.workspace.applyEdit(edit);
      return;
    }

    throw new Error("Document type not supported");
  }

  public kogitoWorkspace_openFile(path: string) {
    this.workspaceApi.kogitoWorkspace_openFile(
      __path.isAbsolute(path) ? path : __path.join(__path.dirname(this.editor.document.document.uri.path), path)
    );
  }

  public async kogitoEditor_contentRequest() {
    let content: string;
    try {
      content = await this.editor.getDocumentContent();
    } catch (e) {
      // If file doesn't exist, we create an empty one.
      // This is important for the use-case where users type `code new-file.dmn` on a terminal.
      await vscode.workspace.fs.writeFile(this.editor.document.document.uri, new Uint8Array());
      return { content: "", path: this.editor.document.document.uri.path };
    }

    return { content, path: this.editor.document.document.uri.path };
  }

  public kogitoEditor_setContentError(editorContent: EditorContent) {
    const i18n = this.i18n.getCurrent();

    vscode.window
      .showErrorMessage(
        i18n.errorOpeningFileText(this.editor.document.document.uri.fsPath.split("/").pop()!),
        i18n.openAsTextButton
      )
      .then((s1) => {
        if (s1 !== i18n.openAsTextButton) {
          return;
        }

        this.editor.close();
        vscode.commands.executeCommand("vscode.openWith", this.editor.document.document.uri, "default");
        vscode.window.showInformationMessage(i18n.reopenAsDiagramText, i18n.reopenAsDiagramButton).then((s2) => {
          if (s2 !== i18n.reopenAsDiagramButton) {
            return;
          }

          vscode.window
            .showTextDocument(this.editor.document.document.uri)
            .then((editor) => editor.document.save())
            .then(() => vscode.commands.executeCommand("workbench.action.closeActiveEditor"))
            .then(() =>
              vscode.commands.executeCommand("vscode.openWith", this.editor.document.document.uri, this.viewType)
            );
        });
      });
  }

  public kogitoEditor_ready() {
    /* empty */
  }

  public kogitoEditor_theme() {
    return { defaultValue: this.editor.getCurrentTheme() };
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

  public kogitoJavaCodeCompletion__getAccessors(fqcn: string, query: string): Promise<JavaCodeCompletionAccessor[]> {
    return this.javaCodeCompletionApi.getAccessors(fqcn, query);
  }

  public kogitoJavaCodeCompletion__getClasses(query: string): Promise<JavaCodeCompletionClass[]> {
    return this.javaCodeCompletionApi.getClasses(query);
  }

  public kogitoJavaCodeCompletion__isLanguageServerAvailable(): Promise<boolean> {
    return this.javaCodeCompletionApi.isLanguageServerAvailable();
  }
}
