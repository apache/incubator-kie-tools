/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import * as fs from "fs";
import { parse } from "path";
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorJobRegistry, JobType } from "./KogitoEditorJobRegistry";
import {
  EditorContent,
  KogitoEdit,
  ResourceContentRequest,
  ResourceContentService,
  Router,
  ResourceListRequest, StateControlEvent
} from "@kogito-tooling/core-api";
import { Uri } from "vscode";

export class KogitoEditor {
  private static readonly DIRTY_INDICATOR = " *";

  private readonly uri: vscode.Uri;
  private readonly relativePath: string;
  private readonly webviewLocation: string;
  private readonly context: vscode.ExtensionContext;
  private readonly router: Router;
  private readonly panel: vscode.WebviewPanel;
  private readonly editorStore: KogitoEditorStore;
  private readonly jobRegistry: KogitoEditorJobRegistry;
  private readonly envelopeBusOuterMessageHandler: EnvelopeBusOuterMessageHandler;
  private readonly resourceContentService: ResourceContentService;
  private readonly signalEdit: (edit: KogitoEdit) => void;

  private readonly encoder = new TextEncoder();
  private readonly decoder = new TextDecoder("utf-8");

  get busId(): string {
    return this.envelopeBusOuterMessageHandler.busId;
  }

  public constructor(
    relativePath: string,
    uri: vscode.Uri,
    initialBackup: vscode.Uri | undefined,
    panel: vscode.WebviewPanel,
    context: vscode.ExtensionContext,
    router: Router,
    webviewLocation: string,
    editorStore: KogitoEditorStore,
    jobRegistry: KogitoEditorJobRegistry,
    resourceContentService: ResourceContentService,
    signalEdit: (edit: KogitoEdit) => void
  ) {
    this.relativePath = relativePath;
    this.uri = uri;
    this.panel = panel;
    this.context = context;
    this.router = router;
    this.webviewLocation = webviewLocation;
    this.editorStore = editorStore;
    this.jobRegistry = jobRegistry;
    this.resourceContentService = resourceContentService;
    this.signalEdit = signalEdit;
    this.envelopeBusOuterMessageHandler = new EnvelopeBusOuterMessageHandler(
      {
        postMessage: msg => {
          this.panel.webview.postMessage(msg);
        }
      },
      (self: EnvelopeBusOuterMessageHandler) => ({
        pollInit: () => {
          self.request_initResponse("vscode");
        },
        receive_languageRequest: () => {
          const pathFileExtension = this.uri.fsPath.split(".").pop()!;
          self.respond_languageRequest(this.router.getLanguageData(pathFileExtension));
        },
        receive_contentResponse: (content: EditorContent) => {
          const fileJob = this.jobRegistry.resolve(self.busId);
          if (!fileJob || fileJob.cancellation.isCancellationRequested) {
            return;
          }
          vscode.workspace.fs.writeFile(fileJob.target, this.encoder.encode(content.content)).then(() => {
            if (fileJob.type === JobType.SAVE) {
              vscode.window.setStatusBarMessage("Saved successfully!", 3000);
            }
            this.jobRegistry.execute(self.busId);
          });
        },
        receive_contentRequest: () => {
          vscode.workspace.fs.readFile(initialBackup ?? this.uri).then(contentArray => {
            initialBackup = undefined;
            self.respond_contentRequest({
              content: this.decoder.decode(contentArray),
              path: this.relativePath
            });
          });
        },
        receive_setContentError: (errorMessage: string) => {
          vscode.window.showErrorMessage(errorMessage);
        },
        receive_dirtyIndicatorChange: (isDirty: boolean) => {
          this.updateDirtyIndicator(isDirty);
        },
        receive_resourceContentRequest: (request: ResourceContentRequest) => {
          this.resourceContentService
            .get(request.path, request.opts)
            .then(content => self.respond_resourceContent(content!));
        },
        receive_resourceListRequest: (request: ResourceListRequest) => {
          this.resourceContentService.list(request.pattern, request.opts).then(list => self.respond_resourceList(list));
        },
        receive_ready(): void {
          /**/
        },
        notify_editorUndo: () => {
          this.notify_editorUndo();
        },
        notify_editorRedo: () => {
          this.notify_editorRedo();
        },
        receive_newEdit: (edit: KogitoEdit) => {
          this.notify_newEdit(edit);
        },
        receive_previewRequest: preview => {
          if (preview) {
            const parsedPath = parse(this.uri.fsPath);
            fs.writeFileSync(`${parsedPath.dir}/${parsedPath.name}-svg.svg`, preview);
          }
        },
        notify_stateControl(event: StateControlEvent) {
          /**/
        }
      })
    );
  }

  public asWebviewUri(absolutePath: Uri) {
    return this.panel.webview.asWebviewUri(absolutePath);
  }

  private updateDirtyIndicator(isDirty: boolean) {
    const titleWithoutDirtyIndicator = this.panel.title.endsWith(KogitoEditor.DIRTY_INDICATOR)
      ? this.panel.title.slice(0, -KogitoEditor.DIRTY_INDICATOR.length)
      : this.panel.title;

    this.panel.title = isDirty
      ? `${titleWithoutDirtyIndicator}${KogitoEditor.DIRTY_INDICATOR}`
      : titleWithoutDirtyIndicator;
  }

  public async requestSave(destination: vscode.Uri, cancellation: vscode.CancellationToken): Promise<void> {
    return this.requestSaveOnPath(destination, JobType.SAVE, cancellation);
  }

  public async requestBackup(destination: vscode.Uri, cancellation: vscode.CancellationToken): Promise<void> {
    return this.requestSaveOnPath(destination, JobType.BACKUP, cancellation);
  }

  public async deleteBackup(destination: vscode.Uri): Promise<void> {
    await vscode.workspace.fs.delete(destination);
  }

  private async requestSaveOnPath(
    destination: vscode.Uri,
    type: JobType,
    cancellation: vscode.CancellationToken
  ): Promise<void> {
    this.envelopeBusOuterMessageHandler.request_contentResponse();

    return new Promise<void>(resolve =>
      this.jobRegistry.register(this.busId, {
        type: type,
        target: destination,
        consumer: resolve,
        cancellation: cancellation
      })
    );
  }

  public async notify_editorRevert(): Promise<void> {
    const content = this.decoder.decode(await vscode.workspace.fs.readFile(this.uri));
    this.envelopeBusOuterMessageHandler.respond_contentRequest({
      content: content,
      path: this.relativePath
    });
  }

  public async notify_editorUndo(): Promise<void> {
    this.envelopeBusOuterMessageHandler.notify_editorUndo();
  }

  public async notify_editorRedo(): Promise<void> {
    this.envelopeBusOuterMessageHandler.notify_editorRedo();
  }

  public notify_newEdit(edit: KogitoEdit) {
    this.signalEdit(edit);
  }

  public requestPreview() {
    this.envelopeBusOuterMessageHandler.request_previewResponse();
  }

  public setupEnvelopeBus() {
    this.context.subscriptions.push(
      this.panel.webview.onDidReceiveMessage(
        msg => this.envelopeBusOuterMessageHandler.receive(msg),
        this,
        this.context.subscriptions
      )
    );

    this.envelopeBusOuterMessageHandler.startInitPolling();
  }

  public setupPanelActiveStatusChange() {
    this.panel.onDidChangeViewState(
      () => {
        if (this.panel.active) {
          this.editorStore.setActive(this);
        }

        if (!this.panel.active && this.editorStore.isActive(this)) {
          this.editorStore.setNoneActive();
        }
      },
      this,
      this.context.subscriptions
    );
  }

  public setupPanelOnDidDispose() {
    this.panel.onDidDispose(
      () => {
        this.envelopeBusOuterMessageHandler.stopInitPolling();
        this.editorStore.close(this);
      },
      this,
      this.context.subscriptions
    );
  }

  private getWebviewIndexJsPath() {
    return this.router.getRelativePathTo(this.webviewLocation);
  }

  public hasUri(uri: vscode.Uri) {
    return this.uri === uri;
  }

  public isActive() {
    return this.panel.active;
  }

  public viewColumn() {
    return this.panel.viewColumn;
  }

  public focus() {
    this.panel.reveal(this.viewColumn(), true);
  }

  public setupWebviewContent() {
    this.panel.webview.html = `
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <style>
                html, body, div#envelope-app {
                    margin: 0;
                    border: 0;
                    padding: 0;
                    overflow: hidden;
                    height: 100%;
                }
                .panel-heading.uf-listbar-panel-header span {
                    color: white !important;
                }
            </style>
        
            <title></title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        </head>
        <body>
        <div id="loading-screen" style="z-index:100;position:relative"></div>
        <div id="envelope-app"></div>
        <script src="${this.getWebviewIndexJsPath()}"></script>
        </body>
        </html>
    `;
  }
}
