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
import { KIEFile } from "./kie-file";

export class KIEFileWatcher {
  private kieFilesOpenedEventHandler: KIEFilesOpenedHandler | null;
  private kieFilesClosedEventHandler: KIEFilesClosedHandler | null;
  private kieFilesChangedEventHandler: KIEFilesChangedHandler | null;

  private tabChangeListener: vscode.Disposable | undefined;
  private watchedKIEFiles: KIEFile[];

  constructor() {
    this.watchedKIEFiles = [];
    this.tabChangeListener = vscode.window.tabGroups.onDidChangeTabs(this.updateOpenFiles, this);
    this.updateOpenFiles();
  }

  private async updateOpenFiles() {
    var watchedFilesUpdated = false;
    var lastWatchedFilesCount = this.watchedKIEFiles.length;

    const openKIEFiles: vscode.TextDocument[] = await KIEFileWatcher.findOpenKIEFiles();

    const kieFileToStopWatching: KIEFile[] = this.watchedKIEFiles.filter(
      (watchedFile) => !openKIEFiles.some((openFile) => openFile.uri.fsPath === watchedFile.fsPath)
    );

    kieFileToStopWatching.forEach((kieFile) => {
      const index = this.watchedKIEFiles.indexOf(kieFile);
      this.watchedKIEFiles.splice(index, 1);
      watchedFilesUpdated = true;
      kieFile.dispose();
    });

    const kieFilesToStartWatching: vscode.TextDocument[] = openKIEFiles.filter(
      (openFile) => !this.watchedKIEFiles.some((watchedFile) => watchedFile.fsPath === openFile.uri.fsPath)
    );

    kieFilesToStartWatching.forEach((openFile) => {
      const kieFile = new KIEFile(openFile);
      kieFile.subscribeKIEFileChanged(() => {
        this.fireKIEFilesChangedEvent();
      });
      this.watchedKIEFiles.push(kieFile);
      watchedFilesUpdated = true;
    });

    if (watchedFilesUpdated) {
      if (lastWatchedFilesCount == 0 && this.watchedKIEFiles.length > 0) {
        this.fireKIEFilesOpenedEvent();
      }
      if (lastWatchedFilesCount > 0 && this.watchedKIEFiles.length == 0) {
        this.fireKIEFilesClosedEvent();
      }
      this.fireKIEFilesChangedEvent();
    }
  }

  private fireKIEFilesOpenedEvent() {
    if (this.kieFilesOpenedEventHandler) {
      this.kieFilesOpenedEventHandler();
    }
  }

  private fireKIEFilesClosedEvent() {
    if (this.kieFilesClosedEventHandler) {
      this.kieFilesClosedEventHandler();
    }
  }

  private fireKIEFilesChangedEvent() {
    if (this.kieFilesChangedEventHandler) {
      this.kieFilesChangedEventHandler();
    }
  }

  public subscribeKIEFilesOpened(handler: KIEFilesOpenedHandler) {
    this.kieFilesOpenedEventHandler = handler;
  }

  public subscribeKIEFilesClosed(handler: KIEFilesClosedHandler) {
    this.kieFilesClosedEventHandler = handler;
  }

  public subscribeKIEFilesChanged(handler: KIEFilesChangedHandler) {
    this.kieFilesChangedEventHandler = handler;
  }

  public unsubscribeKIEFilesOpened() {
    this.kieFilesClosedEventHandler = null;
  }

  public unsubscribeKIEFilesClosed() {
    this.kieFilesClosedEventHandler = null;
  }

  public unsubscribeKIEFilesChanged() {
    this.kieFilesChangedEventHandler = null;
  }

  public dispose(): void {
    this.tabChangeListener?.dispose();
    this.unsubscribeKIEFilesOpened();
    this.unsubscribeKIEFilesClosed();
    this.unsubscribeKIEFilesChanged();
  }

  private static async findOpenFiles(documentSelectors: vscode.DocumentSelector): Promise<vscode.TextDocument[]> {
    const tabGroups = vscode.window.tabGroups.all;

    const foundDocuments: vscode.TextDocument[] = [];
    for (const tabGroup of tabGroups) {
      for (const tab of tabGroup.tabs) {
        if (tab.input instanceof vscode.TabInputCustom || tab.input instanceof vscode.TabInputText) {
          const textDocument: vscode.TextDocument = await vscode.workspace.openTextDocument(tab.input.uri);
          if (vscode.languages.match(documentSelectors, textDocument) > 0) {
            foundDocuments.push(textDocument);
          }
        }
      }
    }

    return foundDocuments;
  }

  public static async findOpenBPMNFiles(): Promise<vscode.TextDocument[]> {
    const bpmnDocumentFilter: vscode.DocumentSelector = {
      language: "bpmn",
      scheme: "file",
    };

    return KIEFileWatcher.findOpenFiles(bpmnDocumentFilter);
  }

  public static async findOpenDMNFiles(): Promise<vscode.TextDocument[]> {
    const dmnDocumentFilter: vscode.DocumentSelector = {
      language: "dmn",
      scheme: "file",
    };

    return KIEFileWatcher.findOpenFiles(dmnDocumentFilter);
  }

  public static async findOpenKIEFiles(): Promise<vscode.TextDocument[]> {
    const bpmnFiles = await KIEFileWatcher.findOpenBPMNFiles();
    const dmnFiles = await KIEFileWatcher.findOpenDMNFiles();

    const openKieFiles: vscode.TextDocument[] = [...bpmnFiles, ...dmnFiles];

    return openKieFiles;
  }
}

interface KIEFilesOpenedHandler {
  (): void;
}

interface KIEFilesClosedHandler {
  (): void;
}

interface KIEFilesChangedHandler {
  (): void;
}
