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

import * as fs from "fs";
import * as vscode from "vscode";
import { Service, CapabilityResponse } from "@kogito-tooling/backend-api";
import {
  ServiceId,
  FileOperationType,
  FileOperationEvent,
  WorkspaceWatcherCapability,
} from "@kogito-tooling/backend-channel-api";

export class VsCodeWorkspaceWatcherService implements Service, WorkspaceWatcherCapability {
  private watcher: vscode.FileSystemWatcher | undefined;

  private readonly eventEmitter = new vscode.EventEmitter<FileOperationEvent>();

  public identify(): string {
    return ServiceId.WORKSPACE_WATCHER;
  }

  public async start(): Promise<void> {
    this.registerWorkspace(vscode.workspace.workspaceFolders![0].uri.fsPath);
  }

  public stop(): void {
    this.eventEmitter.dispose();
    this.unregisterWorkspace();
  }

  public async satisfyRequirements(): Promise<boolean> {
    return !!vscode.workspace.workspaceFolders && vscode.workspace.workspaceFolders.length > 0;
  }

  public registerWorkspace(basePath: string): CapabilityResponse<void> {
    if (this.watcher) {
      return CapabilityResponse.ok();
    }

    if (!fs.existsSync(basePath)) {
      throw new Error(`${basePath} does not exist.`);
    }

    this.watcher = vscode.workspace.createFileSystemWatcher(
      new vscode.RelativePattern(vscode.workspace.getWorkspaceFolder(vscode.Uri.parse(basePath))!, "**/*"),
      false,
      false,
      false
    );

    this.watcher.onDidCreate((uri) => this.fireEvent({ type: FileOperationType.ADD, path: uri.fsPath }));
    this.watcher.onDidChange((uri) => this.fireEvent({ type: FileOperationType.CHANGE, path: uri.fsPath }));
    this.watcher.onDidDelete((uri) => this.fireEvent({ type: FileOperationType.DELETE, path: uri.fsPath }));

    return CapabilityResponse.ok();
  }

  public unregisterWorkspace(): CapabilityResponse<void> {
    if (!this.watcher) {
      return CapabilityResponse.ok();
    }

    this.watcher.dispose();
    this.watcher = undefined;
    return CapabilityResponse.ok();
  }

  public fireEvent(event: FileOperationEvent): CapabilityResponse<void> {
    console.debug(`WorkspaceWatcher: ${event.type} ${event.path}`);
    this.eventEmitter.fire(event);
    return CapabilityResponse.ok();
  }
}
