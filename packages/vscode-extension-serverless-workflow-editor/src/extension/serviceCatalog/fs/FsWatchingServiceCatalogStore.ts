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

  constructor(private readonly args: { baseFileAbsolutePath: string; settings: SwfVsCodeExtensionConfiguration }) {}

  public init(args: { onNewServices: (newSwfServiceCatalogServices: SwfServiceCatalogService[]) => Promise<any> }) {
    this.onChangeCallback = args.onNewServices;

    const initialSpecsDirAbsolutePath = this.args.settings.getInterpolatedSpecsDirPath(this.args);

    this.fsWatcher = this.setupFsWatcher(initialSpecsDirAbsolutePath);
    this.configurationChangedCallback = this.getConfigurationChangedCallback();

    return this.refresh(initialSpecsDirAbsolutePath);
  }

  private getConfigurationChangedCallback() {
    return vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (!e.affectsConfiguration(CONFIGURATION_SECTIONS.specsStoragePath)) {
        return;
      }

      const newSpecsDirAbsolutePath = this.args.settings.getInterpolatedSpecsDirPath(this.args);
      this.fsWatcher?.dispose();
      this.fsWatcher = this.setupFsWatcher(newSpecsDirAbsolutePath);

      return this.refresh(newSpecsDirAbsolutePath);
    });
  }

  private setupFsWatcher(absolutePath: string): Disposable {
    const fsWatcher = vscode.workspace.createFileSystemWatcher(
      `${absolutePath}/*.{json,yaml,yml}`,
      false,
      false,
      false
    );

    const onDidCreate: Disposable = fsWatcher.onDidCreate(() => this.refresh(absolutePath));
    const onDidChange: Disposable = fsWatcher.onDidChange(() => this.refresh(absolutePath));
    const onDidDelete: Disposable = fsWatcher.onDidDelete(() => this.refresh(absolutePath));

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

  private async refresh(specsDirAbsolutePath: string) {
    try {
      const services = await this.readFileSystemServices(specsDirAbsolutePath);
      return this.onChangeCallback?.(services);
    } catch (e) {
      console.error("Could not refresh SWF Service Catalog services", e);
      return this.onChangeCallback?.([]);
    }
  }

  private readFileSystemServices(specsDirAbsolutePath: string): Promise<SwfServiceCatalogService[]> {
    return new Promise<SwfServiceCatalogService[]>((resolve, reject) => {
      try {
        const specsDirAbsolutePathUri = vscode.Uri.parse(specsDirAbsolutePath);

        vscode.workspace.fs.stat(specsDirAbsolutePathUri).then((stats) => {
          if (!stats || stats.type !== FileType.Directory) {
            reject(`Invalid specs dir path: ${specsDirAbsolutePath}`);
            return;
          }

          vscode.workspace.fs.readDirectory(specsDirAbsolutePathUri).then((files) => {
            if (!files || files.length <= 0) {
              resolve([]);
              return;
            }

            const promises: Thenable<SwfServiceCatalogService[]>[] = [];

            files.forEach(([fileName, type]) => {
              if (!(type === FileType.File && OPENAPI_EXTENSIONS_REGEX.test(fileName.toLowerCase()))) {
                return;
              }

              const fileUrl = specsDirAbsolutePathUri.with({ path: specsDirAbsolutePath + "/" + fileName }); // FIXME: windows?
              promises.push(this.readServiceFile(fileUrl, fileName, specsDirAbsolutePath));
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

  private async readServiceFile(fileUrl: vscode.Uri, fileName: string, specsDirAbsolutePath: string) {
    const rawData = await vscode.workspace.fs.readFile(fileUrl);
    try {
      return [
        parseOpenApi({
          baseFileAbsolutePath: this.args.baseFileAbsolutePath,
          specsDirAbsolutePath: specsDirAbsolutePath,
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
