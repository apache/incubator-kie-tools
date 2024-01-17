/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import * as vscode from "vscode";
import {
  CancellationToken,
  CustomDocument,
  CustomDocumentBackup,
  CustomDocumentEditEvent,
  EventEmitter,
  Uri,
} from "vscode";
import { VsCodeKieEditorController } from "./VsCodeKieEditorController";
import { VsCodeKieEditorStore } from "./VsCodeKieEditorStore";
import { VsCodeOutputLogger } from "./VsCodeOutputLogger";
import { VsCodeI18n } from "./i18n";
import { VsCodeNotificationsChannelApiImpl } from "./notifications/VsCodeNotificationsChannelApiImpl";
import { executeOnSaveHook } from "./onSaveHook";
import { getNormalizedPosixPathRelativeToWorkspaceRoot, getWorkspaceRoot } from "./workspace/workspaceRoot";
import * as __path from "path";

export class VsCodeKieEditorCustomDocument implements CustomDocument {
  private readonly encoder = new TextEncoder();
  private readonly decoder = new TextDecoder("utf-8");

  private readonly vsCodeLogger = new VsCodeOutputLogger(VsCodeKieEditorCustomDocument.name);

  private readonly _onDidDispose = new EventEmitter<void>();
  public readonly onDidDispose = this._onDidDispose.event;

  private readonly _onDidChange = new EventEmitter<CustomDocumentEditEvent<VsCodeKieEditorCustomDocument>>();
  public readonly onDidChange = this._onDidChange.event;

  public constructor(
    public readonly uri: Uri,
    public readonly initialBackup: Uri | undefined,
    public readonly editorStore: VsCodeKieEditorStore,
    private readonly vsCodeI18n: I18n<VsCodeI18n>,
    private readonly vscodeNotifications: VsCodeNotificationsChannelApiImpl,
    private readonly editorEnvelopeLocator: EditorEnvelopeLocator
  ) {}

  public dispose() {
    this._onDidDispose.fire();
    this._onDidDispose.dispose();
    this._onDidChange.dispose();
  }

  get normalizedPosixPathRelativeToTheWorkspaceRoot() {
    return getNormalizedPosixPathRelativeToWorkspaceRoot(this);
  }

  get fileExtension() {
    const lastSlashIndex = this.uri.path.lastIndexOf("/");
    const fileName = this.uri.path.substring(lastSlashIndex + 1);

    const firstDotIndex = fileName.indexOf(".");
    const fileExtension = fileName.substring(firstDotIndex + 1);

    return fileExtension;
  }

  get fileType() {
    return this.editorEnvelopeLocator.getEnvelopeMapping(this.uri.fsPath)?.type;
  }

  public async save(destination: Uri, cancellation: CancellationToken): Promise<void> {
    const i18n = this.vsCodeI18n.getCurrent();
    try {
      const editor = this.editorStore.get(this.uri);
      if (!editor) {
        this.vsCodeLogger.error(`Cannot save because there's no open Editor for ${this.uri.fsPath}`);
        return;
      }

      try {
        const notifications = await editor.validate();
        this.vscodeNotifications.setNotifications(
          this,
          __path.posix.normalize(
            __path.relative(getWorkspaceRoot(this).workspaceRootAbsoluteFsPath, destination.fsPath)
          ),
          notifications
        );
      } catch (e) {
        this.vsCodeLogger.warn(`File was not validated: ${e}`);
      }

      const content = await editor.getContent();
      if (cancellation.isCancellationRequested) {
        this.vsCodeLogger.info(cancellation);
        return;
      }

      await vscode.workspace.fs.writeFile(destination, this.encoder.encode(content));
      executeOnSaveHook(this.fileType);
      vscode.window.setStatusBarMessage(i18n.savedSuccessfully, 3000);
    } catch (e) {
      this.vsCodeLogger.error(`Error saving. ${e}`);
    }
  }

  public async backup(destination: Uri, cancellation: CancellationToken): Promise<CustomDocumentBackup> {
    const editor = this.editorStore.get(this.uri);
    if (!editor) {
      throw new Error(`Cannot proceed with backup. Editor is null for path ${this.uri.fsPath}.`);
    }

    const customDocumentBackup = {
      id: destination.fsPath,
      delete: () => vscode.workspace.fs.delete(destination),
    };

    if (cancellation.isCancellationRequested) {
      return customDocumentBackup;
    }

    const content = await editor.getContent();
    await vscode.workspace.fs.writeFile(destination, this.encoder.encode(content));
    console.info("Backup saved.");

    return customDocumentBackup;
  }

  public async revert(cancellation: CancellationToken): Promise<void> {
    const input = await vscode.workspace.fs.readFile(this.uri);
    const editor = this.editorStore.get(this.uri);
    if (editor) {
      return editor.setContent(this.normalizedPosixPathRelativeToTheWorkspaceRoot, this.decoder.decode(input));
    }
  }

  public notifyEdit(editor: VsCodeKieEditorController, edit: WorkspaceEdit) {
    this._onDidChange.fire({
      label: "edit",
      document: this,
      undo: async () => editor.undo(),
      redo: async () => editor.redo(),
    });
  }
}
