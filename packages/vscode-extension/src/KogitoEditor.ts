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
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { Router, ResourceContentService, ResourceContent } from "@kogito-tooling/core-api";

export class KogitoEditor {
  private static readonly DIRTY_INDICATOR = " *";

  private readonly path: string;
  private readonly webviewLocation: string;
  private readonly context: vscode.ExtensionContext;
  private readonly router: Router;
  private readonly panel: vscode.WebviewPanel;
  private readonly editorStore: KogitoEditorStore;
  private readonly envelopeBusOuterMessageHandler: EnvelopeBusOuterMessageHandler;
  private readonly resourceContentService: ResourceContentService;

  public constructor(
    path: string,
    panel: vscode.WebviewPanel,
    context: vscode.ExtensionContext,
    router: Router,
    webviewLocation: string,
    editorStore: KogitoEditorStore,
    resourceContentService: ResourceContentService
  ) {
    this.path = path;
    this.panel = panel;
    this.context = context;
    this.router = router;
    this.webviewLocation = webviewLocation;
    this.editorStore = editorStore;
    this.resourceContentService = resourceContentService;
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
          const pathFileExtension = this.path.split(".").pop()!;
          self.respond_languageRequest(this.router.getLanguageData(pathFileExtension));
        },
        receive_contentResponse: (content: string) => {
          fs.writeFileSync(this.path, content);
          vscode.window.setStatusBarMessage("Saved successfully!", 3000);
        },
        receive_contentRequest: () => {
          self.respond_contentRequest(fs.readFileSync(this.path).toString());
        },
        receive_setContentError: (errorMessage: string) => {
          vscode.window.showErrorMessage(errorMessage);
        },
        receive_dirtyIndicatorChange: (isDirty: boolean) => {
          this.updateDirtyIndicator(isDirty);
        },
        receive_resourceContentRequest: (uri: string) => {
          this.resourceContentService.read(uri).then((v: ResourceContent) => self.respond_resourceContent(v));
        },
        receive_resourceListRequest: (pattern: string) => {
          this.resourceContentService.list(pattern).then(list => self.respond_resourceList(list));
        },
        receive_ready(): void {
          /**/
        }
      })
    );
  }

  private updateDirtyIndicator(isDirty: boolean) {
    const titleWithoutDirtyIndicator = this.panel.title.endsWith(KogitoEditor.DIRTY_INDICATOR)
      ? this.panel.title.slice(0, -KogitoEditor.DIRTY_INDICATOR.length)
      : this.panel.title;

    this.panel.title = isDirty
      ? `${titleWithoutDirtyIndicator}${KogitoEditor.DIRTY_INDICATOR}`
      : titleWithoutDirtyIndicator;
  }

  public requestSave() {
    this.envelopeBusOuterMessageHandler.request_contentResponse();
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

  public hasPath(path: string) {
    return this.path === path;
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
