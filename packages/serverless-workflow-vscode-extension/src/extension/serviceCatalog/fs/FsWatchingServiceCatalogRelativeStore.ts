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
import { parseApiContent } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { SwfServiceCatalogServiceSource } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import {
  SwfServiceCatalogService,
  SwfCatalogSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../../configuration";
import path = require("path");

const EXTENSIONS_REGEX = new RegExp("^.*\\.(yaml|yml|json)$");

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

    const initialRoutesDirAbsolutePosixPath = this.args.configuration.getInterpolatedRoutesDirAbsolutePosixPath(
      this.args
    );

    this.fsWatcher = this.setupFsWatcher({
      specsDirAbsolutePosixPath: initialSpecsDirAbsolutePosixPath,
      routesDirAbsolutePosixPath: initialRoutesDirAbsolutePosixPath,
    });
    this.configurationChangedCallback = this.getConfigurationChangedCallback();

    return this.refresh({
      specsDirAbsolutePosixPath: initialSpecsDirAbsolutePosixPath,
      routesDirAbsolutePosixPath: initialRoutesDirAbsolutePosixPath,
    });
  }

  public get storedServices() {
    return this.services;
  }

  private getConfigurationChangedCallback() {
    return vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (
        !e.affectsConfiguration(CONFIGURATION_SECTIONS.specsStoragePath) &&
        !e.affectsConfiguration(CONFIGURATION_SECTIONS.routesStoragePath)
      ) {
        return;
      }

      const newSpecsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath(this.args);
      const newRoutesDirAbsolutePosixPath = this.args.configuration.getInterpolatedRoutesDirAbsolutePosixPath(
        this.args
      );
      this.fsWatcher?.dispose();
      this.fsWatcher = this.setupFsWatcher({
        specsDirAbsolutePosixPath: newSpecsDirAbsolutePosixPath,
        routesDirAbsolutePosixPath: newRoutesDirAbsolutePosixPath,
      });

      return this.refresh({
        specsDirAbsolutePosixPath: newSpecsDirAbsolutePosixPath,
        routesDirAbsolutePosixPath: newRoutesDirAbsolutePosixPath,
      });
    });
  }

  private setupFsWatcher(args: {
    specsDirAbsolutePosixPath: string;
    routesDirAbsolutePosixPath: string;
  }): vscode.Disposable {
    const fsWatchers = [
      vscode.workspace.createFileSystemWatcher(
        new vscode.RelativePattern(vscode.Uri.parse(args.specsDirAbsolutePosixPath), "*.{json,yaml,yml}"),
        false,
        false,
        false
      ),
      vscode.workspace.createFileSystemWatcher(
        new vscode.RelativePattern(vscode.Uri.parse(args.routesDirAbsolutePosixPath), "*.{json,yaml,yml}"),
        false,
        false,
        false
      ),
    ];

    const onDidCreateWatchers: vscode.Disposable[] = fsWatchers.map((fsWatcher) =>
      fsWatcher.onDidCreate(() => this.refresh(args))
    );
    const onDidChangeWatchers: vscode.Disposable[] = fsWatchers.map((fsWatcher) =>
      fsWatcher.onDidChange(() => this.refresh(args))
    );
    const onDidDeleteWatchers: vscode.Disposable[] = fsWatchers.map((fsWatcher) =>
      fsWatcher.onDidDelete(() => this.refresh(args))
    );

    return {
      dispose: () => {
        onDidCreateWatchers.forEach((onDidCreate) => onDidCreate.dispose());
        onDidChangeWatchers.forEach((onDidChange) => onDidChange.dispose());
        onDidDeleteWatchers.forEach((onDidDelete) => onDidDelete.dispose());
        fsWatchers.forEach((fsWatcher) => fsWatcher.dispose());
      },
    };
  }

  public dispose(): void {
    this.fsWatcher?.dispose();
    this.configurationChangedCallback?.dispose();
  }

  public async refresh(args: { specsDirAbsolutePosixPath: string; routesDirAbsolutePosixPath: string }) {
    try {
      this.services = [
        ...(await this.readFileSystemServices(args.specsDirAbsolutePosixPath)),
        ...(await this.readFileSystemServices(args.routesDirAbsolutePosixPath)),
      ];
    } catch (e) {
      console.error("Could not refresh SWF Service Catalog relative store.", e);
    }
  }

  private readFileSystemServices(dirAbsolutePosixPath: string): Promise<SwfServiceCatalogService[]> {
    return new Promise<SwfServiceCatalogService[]>((resolve, reject) => {
      try {
        const dirAbsolutePosixPathUri = vscode.Uri.parse(dirAbsolutePosixPath);

        const promises: Thenable<SwfServiceCatalogService[]>[] = [];

        vscode.workspace.fs.stat(dirAbsolutePosixPathUri).then(
          (stats) => {
            if (!stats || stats.type !== vscode.FileType.Directory) {
              reject(`Invalid specs dir path: ${dirAbsolutePosixPath}`);
              return;
            }

            vscode.workspace.fs.readDirectory(dirAbsolutePosixPathUri).then((files) => {
              if (!files || files.length <= 0) {
                resolve([]);
                return;
              }

              files.forEach(([fileName, type]) => {
                if (!(type === vscode.FileType.File && EXTENSIONS_REGEX.test(fileName.toLowerCase()))) {
                  return;
                }

                const fileUri = dirAbsolutePosixPathUri.with({
                  path: dirAbsolutePosixPathUri.path + "/" + fileName,
                });

                promises.push(this.readServiceFile(fileUri, fileName, dirAbsolutePosixPath));
              });

              if (promises.length > 0) {
                Promise.all(promises).then((services) => resolve(services.flatMap((s) => s)));
              } else {
                resolve([]);
              }
            });
          },
          (reason) => {
            console.log(`could not load folder in ${dirAbsolutePosixPathUri}.`, reason);
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
      const serviceSource = path.join(specsDirAbsolutePosixPath, fileName);
      const source: SwfServiceCatalogServiceSource = {
        type: SwfCatalogSourceType?.LOCAL_FS,
        absoluteFilePath: serviceSource,
      };

      return [
        parseApiContent({
          serviceFileName: fileName,
          serviceFileContent: new TextDecoder("utf-8").decode(rawData),
          source,
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
