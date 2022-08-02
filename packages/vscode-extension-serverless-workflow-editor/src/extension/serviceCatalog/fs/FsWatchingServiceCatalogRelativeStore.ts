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
import { parseOpenApi } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../../configuration";

const OPENAPI_EXTENSIONS_REGEX = new RegExp("^.*\\.(yaml|yml|json)$");

export class FsWatchingServiceCatalogRelativeStore {
  private configurationChangedCallback: vscode.Disposable | undefined;
  private fsWatcher: vscode.Disposable | undefined;
  private services: SwfServiceCatalogService[] = [];

  constructor(
    private readonly args: { baseFileAbsolutePosixPath: string; configuration: SwfVsCodeExtensionConfiguration }
  ) {}

  public init() {
    const initialSpecsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath(
      this.args
    );

    this.fsWatcher = this.setupFsWatcher({ specsDirAbsolutePosixPath: initialSpecsDirAbsolutePosixPath });
    this.configurationChangedCallback = this.getConfigurationChangedCallback();

    return this.refresh({ specsDirAbsolutePosixPath: initialSpecsDirAbsolutePosixPath });
  }

  public get storedServices() {
    return this.services;
  }

  private getConfigurationChangedCallback() {
    return vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (!e.affectsConfiguration(CONFIGURATION_SECTIONS.specsStoragePath)) {
        return;
      }

      const newSpecsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath(this.args);
      this.fsWatcher?.dispose();
      this.fsWatcher = this.setupFsWatcher({ specsDirAbsolutePosixPath: newSpecsDirAbsolutePosixPath });

      return this.refresh({ specsDirAbsolutePosixPath: newSpecsDirAbsolutePosixPath });
    });
  }

  private setupFsWatcher(args: { specsDirAbsolutePosixPath: string }): vscode.Disposable {
    const fsWatcher = vscode.workspace.createFileSystemWatcher(
      new vscode.RelativePattern(vscode.Uri.parse(args.specsDirAbsolutePosixPath), "*.{json,yaml,yml}"),
      false,
      false,
      false
    );

    const onDidCreate: vscode.Disposable = fsWatcher.onDidCreate(() => this.refresh(args));
    const onDidChange: vscode.Disposable = fsWatcher.onDidChange(() => this.refresh(args));
    const onDidDelete: vscode.Disposable = fsWatcher.onDidDelete(() => this.refresh(args));

    return {
      dispose: () => {
        onDidCreate.dispose();
        onDidChange.dispose();
        onDidDelete.dispose();
        fsWatcher.dispose();
      },
    };
  }

  public dispose(): void {
    this.fsWatcher?.dispose();
    this.configurationChangedCallback?.dispose();
  }

  public async refresh(args: { specsDirAbsolutePosixPath: string }) {
    try {
      this.services = await this.readFileSystemServices(args);
    } catch (e) {
      console.error("Could not refresh SWF Service Catalog relative store.", e);
    }
  }

  private readFileSystemServices(args: { specsDirAbsolutePosixPath: string }): Promise<SwfServiceCatalogService[]> {
    return new Promise<SwfServiceCatalogService[]>((resolve, reject) => {
      try {
        const specsDirAbsolutePosixPathUri = vscode.Uri.parse(args.specsDirAbsolutePosixPath);

        vscode.workspace.fs.stat(specsDirAbsolutePosixPathUri).then(
          (stats) => {
            if (!stats || stats.type !== vscode.FileType.Directory) {
              reject(`Invalid specs dir path: ${args.specsDirAbsolutePosixPath}`);
              return;
            }

            vscode.workspace.fs.readDirectory(specsDirAbsolutePosixPathUri).then((files) => {
              if (!files || files.length <= 0) {
                resolve([]);
                return;
              }

              const promises: Thenable<SwfServiceCatalogService[]>[] = [];

              files.forEach(([fileName, type]) => {
                if (!(type === vscode.FileType.File && OPENAPI_EXTENSIONS_REGEX.test(fileName.toLowerCase()))) {
                  return;
                }

                const fileUri = specsDirAbsolutePosixPathUri.with({
                  path: specsDirAbsolutePosixPathUri.path + "/" + fileName,
                });
                promises.push(this.readServiceFile(fileUri, fileName, args.specsDirAbsolutePosixPath));
              });

              if (promises.length > 0) {
                Promise.all(promises).then((services) => resolve(services.flatMap((s) => s)));
              } else {
                resolve([]);
              }
            });
          },
          (reason) => {
            console.log(`could not load specs folder in ${specsDirAbsolutePosixPathUri}.`, reason);
            return resolve([]);
          }
        );
      } catch (e) {
        console.error(e);
        reject(`Could not load services for SWF Service Catalog. ${e}`);
      }
    });
  }

  private async readServiceFile(fileUri: vscode.Uri, fileName: string, specsDirAbsolutePosixPath: string) {
    const rawData = await vscode.workspace.fs.readFile(fileUri);
    try {
      return [
        parseOpenApi({
          specsDirAbsolutePosixPath,
          serviceFileName: fileName,
          serviceFileContent: new TextDecoder("utf-8").decode(rawData),
        }),
      ];
    } catch (e) {
      console.error(e);
      return [];
    }
  }

  getServices() {
    return this.services;
  }
}
