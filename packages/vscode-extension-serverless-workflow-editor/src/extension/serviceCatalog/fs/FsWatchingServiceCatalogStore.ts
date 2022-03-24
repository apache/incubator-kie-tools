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
import { parseOpenApi } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { SwfServiceCatalogService } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../../configuration";

const OPENAPI_EXTENSIONS_REGEX = new RegExp("^.*\\.(yaml|yml|json)$");

export class FsWatchingServiceCatalogStore {
  private onChangeCallback: undefined | ((services: SwfServiceCatalogService[]) => Promise<any>);
  private configurationChangedCallback: Disposable | undefined;
  private fsWatcher: Disposable | undefined;

  constructor(
    private readonly args: { baseFileAbsolutePosixPath: string; configuration: SwfVsCodeExtensionConfiguration }
  ) {}

  public init(args: { onNewServices: (newSwfServiceCatalogServices: SwfServiceCatalogService[]) => Promise<any> }) {
    this.onChangeCallback = args.onNewServices;

    const initialSpecsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath(
      this.args
    );

    this.fsWatcher = this.setupFsWatcher(initialSpecsDirAbsolutePosixPath);
    this.configurationChangedCallback = this.getConfigurationChangedCallback();

    return this.refresh(initialSpecsDirAbsolutePosixPath);
  }

  private getConfigurationChangedCallback() {
    return vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (!e.affectsConfiguration(CONFIGURATION_SECTIONS.specsStoragePath)) {
        return;
      }

      const newSpecsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath(this.args);
      this.fsWatcher?.dispose();
      this.fsWatcher = this.setupFsWatcher(newSpecsDirAbsolutePosixPath);

      return this.refresh(newSpecsDirAbsolutePosixPath);
    });
  }

  private setupFsWatcher(absolutePosixPath: string): Disposable {
    const fsWatcher = vscode.workspace.createFileSystemWatcher(
      `${absolutePosixPath}/*.{json,yaml,yml}`,
      false,
      false,
      false
    );

    const onDidCreate: Disposable = fsWatcher.onDidCreate(() => this.refresh(absolutePosixPath));
    const onDidChange: Disposable = fsWatcher.onDidChange(() => this.refresh(absolutePosixPath));
    const onDidDelete: Disposable = fsWatcher.onDidDelete(() => this.refresh(absolutePosixPath));

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

  private async refresh(specsDirAbsolutePosixPath: string) {
    try {
      const services = await this.readFileSystemServices(specsDirAbsolutePosixPath);
      return this.onChangeCallback?.(services);
    } catch (e) {
      console.error("Could not refresh SWF Service Catalog services", e);
      return this.onChangeCallback?.([]);
    }
  }

  private readFileSystemServices(specsDirAbsolutePosixPath: string): Promise<SwfServiceCatalogService[]> {
    return new Promise<SwfServiceCatalogService[]>((resolve, reject) => {
      try {
        const specsDirAbsolutePosixPathUri = vscode.Uri.parse(specsDirAbsolutePosixPath);

        vscode.workspace.fs.stat(specsDirAbsolutePosixPathUri).then((stats) => {
          if (!stats || stats.type !== FileType.Directory) {
            reject(`Invalid specs dir path: ${specsDirAbsolutePosixPath}`);
            return;
          }

          vscode.workspace.fs.readDirectory(specsDirAbsolutePosixPathUri).then((files) => {
            if (!files || files.length <= 0) {
              resolve([]);
              return;
            }

            const promises: Thenable<SwfServiceCatalogService[]>[] = [];

            files.forEach(([fileName, type]) => {
              if (!(type === FileType.File && OPENAPI_EXTENSIONS_REGEX.test(fileName.toLowerCase()))) {
                return;
              }

              const fileUri = specsDirAbsolutePosixPathUri.with({ path: specsDirAbsolutePosixPath + "/" + fileName });
              promises.push(this.readServiceFile(fileUri, fileName, specsDirAbsolutePosixPath));
            });

            if (promises.length > 0) {
              Promise.all(promises).then((services) => resolve(services.flatMap((s) => s)));
            } else {
              resolve([]);
            }
          });
        });
      } catch (e) {
        console.error(e);
        reject(`Could not load services for SWF Service Catalog. ${e}`);
      }
    });
  }

  private async readServiceFile(fileUrl: vscode.Uri, fileName: string, specsDirAbsolutePosixPath: string) {
    const rawData = await vscode.workspace.fs.readFile(fileUrl);
    try {
      return [
        parseOpenApi({
          baseFileAbsolutePosixPath: this.args.baseFileAbsolutePosixPath,
          specsDirAbsolutePosixPath: specsDirAbsolutePosixPath,
          serviceFileName: fileName,
          serviceFileContent: new TextDecoder("utf-8").decode(rawData),
        }),
      ];
    } catch (e) {
      console.error(e);
      return [];
    }
  }
}
