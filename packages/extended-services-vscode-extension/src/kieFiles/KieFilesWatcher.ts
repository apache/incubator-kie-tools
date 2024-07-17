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

import * as vscode from "vscode";
import { KieFile } from "./KieFile";
import { bpmnDocumentFilter, dmnDocumentFilter, findActiveKieFiles } from "./KieFilesFetcher";

export class KieFilesWatcher {
  private kieFilesOpenedHandler: ((openedKieFiles: KieFile[]) => void) | null = null;
  private kieFileChangedHandler: ((changedKieFile: KieFile) => void) | null = null;
  private kieFilesClosedHandler: ((closedKieFiles: KieFile[]) => void) | null = null;

  private tabChangeListener: vscode.Disposable;

  public watchedKieFiles: readonly KieFile[];

  public constructor() {
    this.watchedKieFiles = [];
    this.tabChangeListener = vscode.window.tabGroups.onDidChangeTabs(this.updateWatchedKieFiles, this);
  }

  public async updateWatchedKieFiles() {
    const activeKieFiles: KieFile[] = await findActiveKieFiles([bpmnDocumentFilter, dmnDocumentFilter]);

    const kieFilesToStartWatching: KieFile[] = activeKieFiles.filter(
      (activeFile) => !this.watchedKieFiles.some((watchedFile) => watchedFile.uri.fsPath === activeFile.uri.fsPath)
    );
    kieFilesToStartWatching.forEach((kieFile) =>
      kieFile.subscribeKieFileChanged(() => this.fireKieFileChangedEvent(kieFile))
    );

    const kieFilesToStopWatching: KieFile[] = this.watchedKieFiles.filter(
      (watchedFile) => !activeKieFiles.some((activeFile) => activeFile.uri.fsPath === watchedFile.uri.fsPath)
    );
    kieFilesToStopWatching.forEach((kieFile) => kieFile.dispose());

    const kieFilesToKeepWatching: KieFile[] = this.watchedKieFiles.filter((watchedFile) =>
      activeKieFiles.some((activeFile) => activeFile.uri.fsPath === watchedFile.uri.fsPath)
    );

    this.watchedKieFiles = [...kieFilesToStartWatching, ...kieFilesToKeepWatching];

    if (kieFilesToStartWatching.length > 0) {
      this.fireKieFilesOpenedEvent(kieFilesToStartWatching);
    }

    if (kieFilesToStopWatching.length > 0) {
      this.fireKieFilesClosedEvent(kieFilesToStopWatching);
    }
  }

  private fireKieFilesOpenedEvent(openedKieFiles: KieFile[]) {
    this.kieFilesOpenedHandler?.(openedKieFiles);
  }

  private fireKieFileChangedEvent(changedKieFile: KieFile) {
    this.kieFileChangedHandler?.(changedKieFile);
  }

  private fireKieFilesClosedEvent(closedKieFiles: KieFile[]) {
    this.kieFilesClosedHandler?.(closedKieFiles);
  }

  public subscribeKieFilesOpened(handler: (opendKieFiles: KieFile[]) => void) {
    this.kieFilesOpenedHandler = handler;
  }

  public subscribeKieFileChanged(handler: (changedKieFile: KieFile) => void) {
    this.kieFileChangedHandler = handler;
  }

  public subscribeKieFilesClosed(handler: (closedKieFiles: KieFile[]) => void) {
    this.kieFilesClosedHandler = handler;
  }

  public unsubscribeKieFilesOpened() {
    this.kieFilesOpenedHandler = null;
  }

  public unsubscribeKieFileChanged() {
    this.kieFileChangedHandler = null;
  }

  public unsubscribeKieFilesClosed() {
    this.kieFilesClosedHandler = null;
  }

  public dispose(): void {
    this.watchedKieFiles.forEach((kieFile) => kieFile.dispose());
    this.watchedKieFiles = [];

    this.tabChangeListener?.dispose();
    this.unsubscribeKieFilesOpened();
    this.unsubscribeKieFilesClosed();
    this.unsubscribeKieFileChanged();
  }
}
