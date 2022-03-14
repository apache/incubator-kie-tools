/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as vscode from "vscode";
import { Disposable, FileType } from "vscode";
import { parseOpenApi, SwfServiceCatalogStore } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { SwfService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";

const OPENAPI_EXTENSIONS_REGEX = new RegExp("^.*\\.(yaml|yml|json)$");

export class FsWatchingServiceCatalogStore implements SwfServiceCatalogStore {
  private onChangeCallback: (services: SwfService[]) => Promise<any>;

  private readonly onDispose: () => void;

  constructor(private readonly specsDirParentPath: string, private readonly specsDirPath: string) {
    const fsWatcher = vscode.workspace.createFileSystemWatcher(
      `${specsDirPath}/*.{json,yaml,yml}`,
      false,
      false,
      false
    );

    const onDidCreate: Disposable = fsWatcher.onDidCreate(() => this.refresh());
    const onDidChange: Disposable = fsWatcher.onDidChange(() => this.refresh());
    const onDidDelete: Disposable = fsWatcher.onDidDelete(() => this.refresh());

    this.onDispose = () => {
      onDidCreate.dispose();
      onDidChange.dispose();
      onDidDelete.dispose();
      fsWatcher.dispose();
    };
  }

  public init(callback: (swfServices: SwfService[]) => Promise<any>) {
    this.onChangeCallback = callback;
    return this.refresh();
  }

  public dispose(): void {
    this.onDispose();
  }

  private async refresh() {
    try {
      const services = await this.readFileSystemServices();
      return this.onChangeCallback?.(services);
    } catch (err) {
      console.error("Cannot load services", err);
      return this.onChangeCallback?.([]);
    }
  }

  private readFileSystemServices(): Promise<SwfService[]> {
    return new Promise<SwfService[]>((resolve, reject) => {
      try {
        const specsDirAbsolutePath = vscode.Uri.parse(this.specsDirPath);

        vscode.workspace.fs.stat(specsDirAbsolutePath).then((stats) => {
          if (!stats || stats.type !== FileType.Directory) {
            reject(`Invalid path: ${this.specsDirPath}`);
            return;
          }

          vscode.workspace.fs.readDirectory(specsDirAbsolutePath).then((files) => {
            if (!files || files.length <= 0) {
              resolve([]);
              return;
            }

            const promises: Thenable<SwfService[]>[] = [];

            files.forEach(([fileName, type]) => {
              if (!(type === FileType.File && OPENAPI_EXTENSIONS_REGEX.test(fileName.toLowerCase()))) {
                return;
              }

              const fileUrl = specsDirAbsolutePath.with({ path: this.specsDirPath + "/" + fileName });
              promises.push(this.readServiceFile(fileUrl, fileName));
            });

            if (promises.length > 0) {
              Promise.all(promises).then((services) => resolve(services.flatMap((s) => s)));
            } else {
              resolve([]);
            }
          });
        });
      } catch (error) {
        reject(`Error loading catalog: ${error}`);
      }
    });
  }

  private async readServiceFile(fileUrl: vscode.Uri, fileName: string) {
    const rawData = await vscode.workspace.fs.readFile(fileUrl);
    try {
      const swfService = parseOpenApi({
        fileName,
        storagePath: this.specsDirParentPath,
        content: Buffer.from(rawData).toString("utf-8"),
      });
      return [swfService];
    } catch (err) {
      return [];
    }
  }
}
