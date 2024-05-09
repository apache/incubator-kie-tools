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
import * as kiefile from "./KieFile";
import * as kiefilesfetcher from "./KieFilesFetcher";

export class KieFilesWatcher {
  private kieFilesOpenedHandler: ((openedKieFiles: kiefile.KieFile[]) => void) | null = null;
  private kieFileChangedHandler: ((changedKieFile: kiefile.KieFile) => void) | null = null;
  private kieFilesClosedHandler: ((closedKieFiles: kiefile.KieFile[]) => void) | null = null;

  private tabChangeListener: vscode.Disposable;
  private watchedKieFiles: kiefile.KieFile[];

  public static async create(): Promise<KieFilesWatcher> {
    const watcher = new KieFilesWatcher();
    await watcher.updateWatchedKieFiles();
    return watcher;
  }

  private constructor() {
    this.watchedKieFiles = [];
    this.tabChangeListener = vscode.window.tabGroups.onDidChangeTabs(this.updateWatchedKieFiles, this);
  }

  private async updateWatchedKieFiles() {
    const activeKieFiles: kiefile.KieFile[] = await kiefilesfetcher.findActiveKieFiles([
      kiefilesfetcher.bpmnDocumentFilter,
      kiefilesfetcher.dmnDocumentFilter,
    ]);

    const kieFilesToStartWatching: kiefile.KieFile[] = activeKieFiles.filter(
      (activeFile) => !this.watchedKieFiles.some((watchedFile) => watchedFile.uri.fsPath === activeFile.uri.fsPath)
    );

    const kieFilesToStopWatching: kiefile.KieFile[] = this.watchedKieFiles.filter(
      (watchedFile) => !activeKieFiles.some((activeFile) => activeFile.uri.fsPath === watchedFile.uri.fsPath)
    );

    this.stopWatching(kieFilesToStopWatching);
    this.startWatching(kieFilesToStartWatching);
  }

  private stopWatching(kieFilesToStopWatching: kiefile.KieFile[]) {
    kieFilesToStopWatching.forEach((kieFile) => {
      const index = this.watchedKieFiles.indexOf(kieFile);
      this.watchedKieFiles.splice(index, 1);
      kieFile.dispose();
    });

    if (kieFilesToStopWatching.length > 0) {
      this.fireKieFilesClosedEvent(kieFilesToStopWatching);
    }
  }

  private startWatching(kieFilesToStartWatching: kiefile.KieFile[]) {
    kieFilesToStartWatching.forEach((kieFile) => {
      this.watchedKieFiles.push(kieFile);
      kieFile.subscribeKieFileChanged(() => this.fireKieFileChangedEvent(kieFile));
    });

    if (kieFilesToStartWatching.length > 0) {
      this.fireKieFilesOpenedEvent(kieFilesToStartWatching);
    }
  }

  private fireKieFilesOpenedEvent(openedKieFiles: kiefile.KieFile[]) {
    this.kieFilesOpenedHandler?.(openedKieFiles);
  }

  private fireKieFileChangedEvent(changedKieFile: kiefile.KieFile) {
    this.kieFileChangedHandler?.(changedKieFile);
  }

  private fireKieFilesClosedEvent(closedKieFiles: kiefile.KieFile[]) {
    this.kieFilesClosedHandler?.(closedKieFiles);
  }

  public subscribeKieFilesOpened(handler: (opendKieFiles: kiefile.KieFile[]) => void) {
    this.kieFilesOpenedHandler = handler;
  }

  public subscribeKieFileChanged(handler: (changedKieFile: kiefile.KieFile) => void) {
    this.kieFileChangedHandler = handler;
  }

  public subscribeKieFilesClosed(handler: (closedKieFiles: kiefile.KieFile[]) => void) {
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
